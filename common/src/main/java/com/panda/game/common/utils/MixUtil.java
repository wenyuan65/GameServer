package com.panda.game.common.utils;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * 常用工具类
 * @author wenyuan
 */
public class MixUtil {

	private static final long multiplier = 0x5DEECE66DL;
	private static final long addend = 0xBL;
	private static final long mask = (1L << 48) - 1;

	/**
	 * 计算离散的值
	 * @param value
	 * @param bits
	 * @return
	 */
	public static int calcMod(long value, int bits) {
		long oldseed = value;
		long nextseed = (oldseed * multiplier + addend) & mask;

		return (int) (nextseed >>> (48 - bits));
	}

	/**
	 * 查找一个包路径的上一层路径
	 * @param pkg
	 * @return
	 */
	public static String getParent(Package pkg) {
		return getParent(pkg.getName());
	}

	/**
	 * 查找一个包路径的上一层路径
	 * @param pkg
	 * @return
	 */
	public static String getParent(String pkg) {
		int index = pkg.lastIndexOf('.');
		return index > 0 ? pkg.substring(0, index) : "";
	}

	/**
	 * 获取本地所有的ip地址
	 * @return
	 * @throws SocketException
	 */
	public static List<InetAddress> getAllLocalAddresses() throws SocketException {
		List<InetAddress> list = new ArrayList<>();
		Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
		while (networkInterfaces.hasMoreElements()) {
			NetworkInterface networkInterface = networkInterfaces.nextElement();
			Enumeration<InetAddress> addrs = networkInterface.getInetAddresses();
			while (addrs.hasMoreElements()) {
				list.add(addrs.nextElement());
			}
		}
		
		return list;
	}

	/**
	 * 检查端口号是否可用
	 * @param port
	 * @throws IOException
	 */
	public static void checkPortAvailible(int port) throws IOException {
		Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
		while (networkInterfaces.hasMoreElements()) {
			NetworkInterface networkInterface = networkInterfaces.nextElement();
			Enumeration<InetAddress> addrs = networkInterface.getInetAddresses();
			while (addrs.hasMoreElements()) {
				InetAddress addr = addrs.nextElement();
				Socket s = null;
				try {
					s = new Socket();
					s.bind(new InetSocketAddress(addr.getHostAddress(), port));
				} catch (IOException e) {
					throw e;
				} finally {
					if (s != null) {
						s.close();
					}
				}
			}
		}
	}
	
	/**
	 * 获取一个大于或等于num的二进制数2^n, 最大值取0x40000000, 最小值为2
	 * @param num
	 * @return
	 */
	public static int getBinaryNumGreatOrEquipThan(int num) {
		int result = 1;
		for (int i = 0; i < 30; i++) {
			result <<= 1;
			
			if (result >= num) {
				break;
			}
		}
		
		return result;
	}

	/**
	 * 比较两个版本号的先后顺序，
	 * @param version1
	 * @param version2
	 * @return
	 */
	public static int compareVersion(String version1, String version2) {
		int[] subVersions1 = StringUtils.str2IntArray(version1, ".");
		int[] subVersions2 = StringUtils.str2IntArray(version2, ".");

		int len = Math.min(subVersions1.length, subVersions2.length);
		for (int i = 0; i < len; i++) {
			if (subVersions1[i] != subVersions2[i]) {
				return subVersions1[i] < subVersions2[i] ? -1 : 1;
			}
		}

		return Integer.compare(subVersions1.length, subVersions2.length);
	}

	public static void main(String[] args) {
		for (int i = 0; i < 31; i++) {
			int num = (1 << i) + 1;
			int result = getBinaryNumGreatOrEquipThan(num);
			System.out.printf("num: %d, result:%d, %s ==> %s%n", num, result, Integer.toHexString(num), Integer.toHexString(result));
		}

		System.out.println(compareVersion("1.0.0.0", "1.0.0.1"));
		System.out.println(compareVersion("1.0.0.0", "1.0.0.0"));
		System.out.println(compareVersion("1.0.0.1", "1.0.0.0"));
		System.out.println(compareVersion("0.0.1.0", "1.0.0.0"));
		System.out.println(compareVersion("0.0.1.0", "0.0.0.1"));

		String[] strings1 = StringUtils.split("1.0.0.", ".");
		String[] strings2 = StringUtils.split(".1.0.0", ".");
		String[] strings3 = StringUtils.split(".1.0.0.", ".");
		System.out.println(strings3.length);
	}
	
}
