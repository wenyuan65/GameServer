package com.panda.game.tools.proto;

import com.google.protobuf.Parser;
import com.googlecode.protobuf.format.JsonFormat;
import com.panda.game.common.utils.ScanUtil;
import com.panda.game.proto.CmdPb.*;
import com.panda.game.proto.PacketPb;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 解析使用wireshark软件抓包的.pcap文件, 根据ProtoBuf协议文件还原出原始数据
 */
public class PcapFileDecoder {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private static final String Hex = "0123456789abcdef";
    public static final Map<String, Class<?>> protocolMap = new HashMap<>();

    public static ByteBuf tempBuff = null;

    public static void loadProtocol() {
        Set<Class<?>> allClasses = ScanUtil.scan(PacketPb.class.getPackage().getName());
        for (Class<?> allClass : allClasses) {
            if (allClass.getName().endsWith("Push") || allClass.getName().endsWith("Rs") || allClass.getName().endsWith("Rq")) {
                protocolMap.put(allClass.getSimpleName(), allClass);
            }
        }
    }

    public static List<ByteBuf> parsePackets(ByteBuf byteBuf) {
        List<ByteBuf> buffList = new ArrayList<>();

        if (tempBuff != null) {
            ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(tempBuff.readableBytes() + byteBuf.readableBytes());
            buffer.writeBytes(tempBuff);
            buffer.writeBytes(byteBuf);
            byteBuf = buffer;
        }

        while (byteBuf.readableBytes() > 4) {
            int len = byteBuf.getInt(byteBuf.readerIndex());

            if (byteBuf.readableBytes() < len) {
                tempBuff = byteBuf;
                return buffList;
            }

            byteBuf.skipBytes(4);
            ByteBuf buf = byteBuf.readBytes(len);

            buffList.add(buf);

            tempBuff = null;
        }

        return buffList;
    }

    public static void parse(PacketPb.Pkg packet, int order) {
        String name = "";
        Cmd cmd = Cmd.forNumber(packet.getCmd());
        if (cmd == null) {
            Cmd rq = Cmd.forNumber(packet.getCmd());
            if (rq == null) {
                return;
            }
            name = rq.name();
        } else {
            name = cmd.name();
        }

        try {
            // proto协议类
            Class<?> clazz = protocolMap.get(name);
            Method method = clazz.getDeclaredMethod("parser");
            Parser<?> parser = (Parser<?>) method.invoke(null);
            com.google.protobuf.GeneratedMessageV3 msg = (com.google.protobuf.GeneratedMessageV3)parser.parseFrom(packet.getBody());

            System.out.printf("[%4d]:#%d#%s#%s%n", order, packet.getPlayerId(), name, JsonFormat.printToString(msg));
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static byte[] readFile(String fileName) {
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);

        byte[] content = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream(8192);
        byte[] buf = new byte[1024];
        int n = -1;
        try {
            while ((n = inputStream.read(buf)) != -1) {
                baos.write(buf, 0, n);
            }
            baos.flush();

            content = baos.toByteArray();
            baos.close();
            inputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return content;
    }

    public static List<ByteBuf> readPcapFile(String fileName, String ipPort) {
        // 读取文件内容
        byte[] content = readFile(fileName);

        List<ByteBuf> result = new ArrayList<>();

        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(content.length);
        buffer.writeBytes(content);
        // global header
        ByteBuf globalHeaderBuf = buffer.readBytes(24);
        ByteBuf magicType = globalHeaderBuf.readBytes(4);
        String magic = byteArrToHex(ByteBufUtil.getBytes(magicType));
        boolean littleEndian = "d4c3b2a1".equalsIgnoreCase(magic);
        globalHeaderBuf.release();

        // 解析ip和端口号
        int index = ipPort.indexOf(":");
        byte[] ip = parseIp(ipPort.substring(0, index));
        int port = Integer.parseInt(ipPort.substring(index + 1));

        while (buffer.readableBytes() > 0) {
            // packetHeader
            int timestamp1 = littleEndian ? buffer.readIntLE() : buffer.readInt();
            int timestamp2 = littleEndian ? buffer.readIntLE() : buffer.readInt();
            int capLen = littleEndian ? buffer.readIntLE() : buffer.readInt();
            int len = littleEndian ? buffer.readIntLE() : buffer.readInt();

            // 抓包日期
            long timestamp = timestamp1 * 1000L + timestamp2 / 1000;
            String time = sdf.format(new Date(timestamp));

            // 数据包的buf
            ByteBuf packetBuf = buffer.readBytes(capLen);

            // 解析网络数据帧
            ByteBuf byteBuf1 = packetBuf.readBytes(4);
            String packetType1 = byteArrToHex(ByteBufUtil.getBytes(byteBuf1));
            if (!"02000000".equalsIgnoreCase(packetType1)) { // 判断是否是本地环回数据包，环回数据包省略了数据帧头部
//                packetBuf.skipBytes(12);
                packetBuf.skipBytes(8); // 源MAC地址和目的MAC地址总共12字节，前面读取了4字节
                ByteBuf packetTypeBuf = packetBuf.readBytes(2);
                String packetType = byteArrToHex(ByteBufUtil.getBytes(packetTypeBuf));
                packetTypeBuf.release();
                // ip数据包类型判断
                if (!"0800".equalsIgnoreCase(packetType)) {
                    packetBuf.release();
                    continue;
                }
            }

            // 解析ip协议包
            ByteBuf ipBuf = packetBuf.slice();

            byte ipVersionAndLen = ipBuf.getByte(0);
            int ipVersion = (ipVersionAndLen & 0xf0) >> 4;
            int ipHeaderLen = (ipVersionAndLen & 0x0f) << 2;

            byte totalLen1 = ipBuf.getByte(2);
            byte totalLen2 = ipBuf.getByte(3);
            int totalLen = ((totalLen1 & 0xff) << 8) | (totalLen2 & 0xff);

            byte[] srcIp = new byte[4];
            byte[] distIp = new byte[4];
            ipBuf.getBytes(12, srcIp);
            ipBuf.getBytes(16, distIp);

            byte protocol = ipBuf.getByte(9);
            if (protocol != 6) { // 不是tcp协议
                continue;
            }

            ipBuf.skipBytes(ipHeaderLen);

            // 解析tcp协议包
            ByteBuf tcpBuf = ipBuf.slice();
            int tcpHeaderLen = tcpBuf.getByte(12);
            tcpHeaderLen = ((tcpHeaderLen & 0xf0) >> 4) << 2;
            tcpBuf.skipBytes(tcpHeaderLen);

            byte srcPort1 = tcpBuf.getByte(0);
            byte srcPort2 = tcpBuf.getByte(1);
            byte dstPort1 = tcpBuf.getByte(2);
            byte dstPort2 = tcpBuf.getByte(3);
            int srcPort = ((srcPort1 & 0xff) << 8) | (srcPort2 & 0xff);
            int dstPort = ((dstPort1 & 0xff) << 8) | (dstPort2 & 0xff);
            // 过滤非指定ip和端口号的数据
            boolean isSrcIpPort = isIpEquals(srcIp, ip) && srcPort == port;
            boolean isDistIpPort = isIpEquals(distIp, ip) && dstPort == port;
            if (!isSrcIpPort && !isDistIpPort) {
                continue;
            }

            // 确定tcp协议长度
            int tcpDataLen = totalLen - ipHeaderLen - tcpHeaderLen;
            if (tcpDataLen == 0) {
                continue;
            }

            ByteBuf byteBuf = tcpBuf.readBytes(tcpDataLen);
            tcpBuf.release();
            System.out.printf("[%4d]：#%s#%d#%s%n", result.size() + 1, time, isSrcIpPort ? 1: 2, byteArrToHex(ByteBufUtil.getBytes(byteBuf)));

            result.add(byteBuf);
        }
        buffer.release();

        return result;
    }

    public static boolean isIpEquals(byte[] ip1, byte[] ip2) {
        return ip1[0] == ip2[0] && ip1[1] == ip2[1] && ip1[2] == ip2[2] && ip1[3] == ip2[3];
    }

    public static byte[] parseIp(String ip) {
        String[] segment = ip.split("\\.");
        int i1 = Integer.parseInt(segment[0]);
        int i2 = Integer.parseInt(segment[1]);
        int i3 = Integer.parseInt(segment[2]);
        int i4 = Integer.parseInt(segment[3]);

        return new byte[] {(byte)(i1 & 0xff), (byte)(i2 & 0xff), (byte)(i3 & 0xff), (byte)(i4 & 0xff)};
    }

    public static String byteArrToHex(byte[] btArr) {
        char strArr[] = new char[btArr.length * 2];
        int i = 0;
        for (byte bt : btArr) {
            strArr[i++] = Hex.charAt(bt>>>4 & 0xf);
            strArr[i++] = Hex.charAt(bt & 0xf);
        }
        return new String(strArr);
    }

    public static byte[] hexToByteArr(String hexStr) {
        char[] charArr = hexStr.toLowerCase().toCharArray();
        byte btArr[] = new byte[charArr.length / 2];
        int index = 0;
        for (int i = 0; i < charArr.length; i++) {
            int highBit = Hex.indexOf(charArr[i]);
            int lowBit = Hex.indexOf(charArr[++i]);
            btArr[index] = (byte) (highBit << 4 | lowBit);
            index++;
        }
        return btArr;
    }

    public static byte[] invert(byte[] buf) {
        byte[] result = new byte[buf.length];
        for (int i = 0; i < buf.length; i++) {
            result[result.length - i - 1] = buf[i];
        }

        return result;
    }

    /**
     * 解析单个协议
     * @param content
     * @return
     */
    public List<ByteBuf> decodeStr(String content) {
        byte[] byteArray = hexToByteArr(content);
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        buffer.writeBytes(byteArray);
        return Arrays.asList(buffer);
    }

    public static void main(String[] args) {
        loadProtocol();

//        String contents = "0000004d10c8b19f057a3d0a3b123908df0610691801200128013a0510b5a78932421a000000000000ffffffffffffffffff01ffffffffffffffffff014a030000005204080110018001d7e580d5b5a0020000001310dcb19f057a030a01698001d7e580d5b5a0020000001310c6b19f057a0308b2028001d7e580d5b5a0020000004e10c8b19f057a3e0a3c123a08e00610b2021801200128013a0510f5cef104421a000000000000ffffffffffffffffff01ffffffffffffffffff014a030000005204080110018001d7e580d5b5a0020000001410dcb19f057a040a02b2028001d7e580d5b5a0020000004e10c8b19f057a3e0a3c123a08e10610e2041801200128013a051085aced04421a000000000000ffffffffffffffffff01ffffffffffffffffff014a030000005204080110018001d7e580d5b5a0020000004d10c8b19f057a3d0a3b123908e20610661801200128013a0510f599d230421a000000000000ffffffffffffffffff01ffffffffffffffffff014a030000005204080110018001d7e580d5b5a0020000004e10c8b19f057a3e0a3c123a08e30610dc041801200128013a0510c5d5f404421a000000000000ffffffffffffffffff01ffffffffffffffffff014a030000005204080110018001d7e580d5b5a0020000001310c6b19f057a0308b0028001d7e580d5b5a0020000004e10c8b19f057a3e0a3c123a08e40610b0021801200128013a0510d5a3f504421a000000000000ffffffffffffffffff01ffffffffffffffffff014a030000005204080110018001d7e580d5b5a002";
//        List<ByteBuf> dataList = decodeStr(contents);
        List<ByteBuf> dataList = readPcapFile("login4.pcap", "192.168.0.80:9909");

        System.out.println("解析结果=========================================================================");
        for (int i = 0; i < dataList.size(); i++) {
            try {
                ByteBuf buffer = dataList.get(i);
                List<ByteBuf> packets = parsePackets(buffer);
                if (packets.size() == 0) {
                    continue;
                }

                for (ByteBuf packet : packets) {
                    PacketPb.Pkg pkg = PacketPb.Pkg.parseFrom(ByteBufUtil.getBytes(packet));
                    parse(pkg, i + 1);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.printf("数据[%3d]解析异常%n", i + 1);
            }
        }
    }

}
