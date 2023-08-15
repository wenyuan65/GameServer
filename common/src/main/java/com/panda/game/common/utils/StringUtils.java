package com.panda.game.common.utils;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;

public class StringUtils {

    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
    public static final String DEFAULT_DELIMITER = ",";
    public static final String DEFAULT_ARRAY_DELIMITER = ";";

    private static final String default_key_value_spliterator = ":";
    private static final String default_entry_spliterator = ",";

    public static String toString(byte[] contents) {
        return new String(contents, DEFAULT_CHARSET);
    }

    public static byte[] toByte(String content) {
        return content.getBytes(DEFAULT_CHARSET);
    }

    public static boolean isBlank(String cs) {
        if (cs == null || cs.length() == 0) {
            return true;
        }

        int strLen = cs.length();
        for(int i = 0; i < strLen; ++i) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    public static boolean isNotBlank(String cs) {
        return !isBlank(cs);
    }

    public static boolean isNumber(String str) {
        if (str == null || str.trim().equals("")) {
            return false;
        }

        int length = str.length();
        for (int i = 0; i < length; i++) {
            char c = str.charAt(i);
            if (!Character.isDigit(c)) {
                return false;
            }
        }

        return true;
    }

    /**
     * 将字符串转换成一个二维数据
     * @param content
     * @return
     */
    public static List<int[]> str2IntArray2(String content) {
        String[] args = split(content, DEFAULT_ARRAY_DELIMITER);
        List<int[]> list = new ArrayList<>(args.length);
        for (String arg : args) {
            if (isBlank(arg)) {
                continue;
            }

            int[] intArray = str2IntArray(content, DEFAULT_DELIMITER);
            list.add(intArray);
        }

        return list;
    }

    /**
     * 将字符串解析为int数组, 例如："1,2,3" ==> [1, 2, 3]
     * @param content
     * @return
     */
    public static int[] str2IntArray(String content) {
        return str2IntArray(content, DEFAULT_DELIMITER);
    }

    /**
     * 将字符串解析为int数组, 例如："1,2,3" ==> [1, 2, 3]
     * @param content
     * @param delimiter 分隔符
     * @return
     */
    public static int[] str2IntArray(String content, String delimiter) {
        String[] args = split(content, delimiter);
        int[] result = new int[args.length];
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            result[i] = StringUtils.isNotBlank(arg) ? Integer.parseInt(arg) : 0;
        }
        return result;
    }

    /**
     * 将字符串解析为long数组, 例如："1,2,3" ==> [1L, 2L, 3L]
     * @param content
     * @return
     */
    public static long[] str2LongArray(String content) {
        return str2LongArray(content, DEFAULT_DELIMITER);
    }

    /**
     * 将字符串解析为int数组, 例如："1,2,3" ==> [1L, 2L, 3L]
     * @param content
     * @param delimiter 分隔符
     * @return
     */
    public static long[] str2LongArray(String content, String delimiter) {
        String[] args = split(content, delimiter);
        long[] result = new long[args.length];
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            result[i] = StringUtils.isNotBlank(arg) ? Long.parseLong(arg) : 0L;
        }
        return result;
    }

    /**
     * 将字符串按照字符切割成字符串数组, 例如：<br/>
     * "1,2,3" ==> {"1", "2", "3"},<br/>
     * "1,2,3," ==> {"1", "2", "3", ""}<br/>
     * ",1,2,3" ==> {"", "1", "2", "3"}<br/>
     * ",1,2,3," ==> {"", "1", "2", "3", ""}<br/>
     * @param content
     * @param spliterator
     * @return
     */
    public static String[] split(String content, String spliterator) {
        List<String> result = new ArrayList<>();
        int index = -1;
        int cursor = 0;
        while((index = content.indexOf(spliterator, cursor)) != -1) {
            result.add(content.substring(cursor, index));

            cursor = index + spliterator.length();
        }
        if (cursor <= content.length()) {
            result.add(content.substring(cursor));
        }

        return result.toArray(new String[result.size()]);
    }

    /**
     * 将字符串按照正则表达式割成字符串数组,
     * @param content
     * @param regex
     * @return
     */
    public static String[] splitRegex(String content, String regex) {
        return Pattern.compile(regex).split(content);
    }

    /**
     * 将map转换成字符串，例如{1=1，2=3，3=4} ==> "1:1,2:3,3:4"
     * @param map
     * @return
     */
    public static String map2Str(Map<Integer, Integer> map) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            if (sb.length() > 0) {
                sb.append(default_entry_spliterator);
            }
            sb.append(entry.getKey()).append(default_key_value_spliterator).append(entry.getValue());
        }

        return sb.toString();
    }

    /**
     * 将字符串转换成map，例如"1:1,2:3,3:4" ==> {1=1，2=3，3=4}
     * @param content
     * @return
     */
    public static Map<Integer, Integer> string2Map(String content) {
        return string2Map(content, HashMap::new, Integer::parseInt, Integer::parseInt, default_entry_spliterator, default_key_value_spliterator);
    }

    /**
     * 将字符串转换成map，例如"1:1,2:3,3:4" ==> {1=1，2=3，3=4}
     * @param content
     * @param entrySpliterator
     * @param keyValueSpliterator
     * @return
     */
    public static Map<Integer, Integer> string2Map(String content, String entrySpliterator, String keyValueSpliterator) {
        return string2Map(content, HashMap::new, Integer::parseInt, Integer::parseInt, entrySpliterator, keyValueSpliterator);
    }

    public static <K, V> Map<K, V> string2Map(String content, Supplier<Map<K, V>> supplier, Function<String, K> keyExtractor, Function<String, V> valueExtractor) {
        return string2Map(content, supplier, keyExtractor, valueExtractor, default_entry_spliterator, default_key_value_spliterator);
    }

    public static <K, V> Map<K, V> string2Map(String content, Supplier<Map<K, V>> supplier,
                                              Function<String, K> keyExtractor, Function<String, V> valueExtractor,
                                              String entrySpliterator, String keyValueSpliterator) {
        Map<K, V> map = supplier.get();
        String[] array = split(content, entrySpliterator);
        for (int i = 0; i < array.length; i++) {
            String keyValue = array[i];
            String[] args = keyValue.split(keyValueSpliterator);
            if (args.length != 2) {
                throw new RuntimeException("key/value解析异常:" + keyValue);
            }

            K key = keyExtractor.apply(args[0]);
            V value = valueExtractor.apply(args[1]);

            map.put(key, value);
        }

        return map;
    }

    /**
     * 解析http请求参数
     * @param params
     * @return
     */
    public static Map<String, String> parseParameterMap(String params) {
        Map<String, String> map = new HashMap<>();
        String[] paramsArr = params.split("&");
        for (String param : paramsArr) {
            if (StringUtils.isBlank(param)) {
                continue;
            }
            String[] pArr = param.split("=");
            String key = pArr[0];
            String value = pArr.length > 1 ? pArr[1] : null;
            map.put(key, value);
        }

        return map;
    }

    public static String getKey(String seperator, String key1, String key2) {
        StringBuilder sb = new StringBuilder();
        sb.append(key1).append(seperator).append(key2);
        return sb.toString();
    }

    public static String getKey(String seperator, Object... keys) {
        if (keys == null || keys.length == 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        for (int i = 0; i < keys.length; i++) {
            Object key = keys[i];
            if (!isFirst) {
                sb.append(seperator);
            }
            sb.append(key.toString());
            isFirst = false;
        }

        return sb.toString();
    }

}
