package util;

import org.apache.commons.codec.binary.Hex;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;

/**
 * 字符串工具类
 */
public class StringUtil {

    // String constants
    public static final String STR_DELIMIT_1ST = "\\|";
    public static final String STR_DELIMIT_2ND = "\\$";
    public static final String STR_DELIMIT_3RD = "#";
    public static final String STR_DELIMIT_4TH = "\\^";
    public static final String STR_DELIMIT_5TH = ":";
    public static final String STR_DELIMIT_6TH = "\\.";
    public static final char DELIMIT_1ST = '|'; // first-priority delimiter
    public static final char DELIMIT_2ND = '$'; // secondary delimiter
    public static final char DELIMIT_3RD = '#'; // third delimiter
    public static final char DELIMIT_4TH = '^'; // fourth delimiter
    public static final char DELIMIT_5TH = ':'; // fourth delimiter
    public static final char DELIMIT_6TH = '.'; // fourth delimiter
    public static final String VAL_UNK = "_UNK"; // Unknown value

    // special sign in sentence
    public static final String[] SPECIAL_SIGN = {"~", "@", "#", "$", "%", "^",
            "&", "*", "-"};

    // Sub Separator type
    public static final String SEPERATOR_SUB_SENTENCE = ";；，,:：";

    /**
     * 内置枚举类型
     **/
    public static enum Split_Sign {
        SPLIT_SPACE(" "), SPLIT_VERTICAL("|"), SPLIT_BEELINE("-"), SPLIT_STAR(
                "*"), SPLIT_LINE("\n"), SPLIT_DOWNLINE("_");

        /**
         * 定义枚举类型自己的属性
         **/
        private final String sign;

        private Split_Sign(String sign) {
            this.sign = sign;
        }

        public String getSign() {
            return sign;
        }
    }

    /**
     * compare the two arguments
     *
     * @param s1 the first object to be compared
     * @param s2 the second object to be compared
     * @return a negative integer, zero, or a positive integer  as the first argument is less than, equal to, or greater than the second
     */
    public static int strCompare(String s1, String s2) {
        if (s1 == null && s2 == null)
            return 0;
        else if (s1 == null)
            return -1;
        else if (s2 == null)
            return 1;
        return s1.compareTo(s2);
    }

    /**
     * 32-bit lower-case md5
     *
     * @param msg the message to encode
     * @return the md5 of the given message
     */
    public static String md5(String msg) {
        byte[] digetss = new byte[0];
        try {
            digetss = MessageDigest.getInstance("md5").digest(msg.getBytes(Charset.forName("UTF-8")));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return new String(Hex.encodeHex(digetss));
    }

    /**
     * upper case 16 bit md5
     *
     * @param string the message to encode
     * @return the md5 of the given message
     */
    public static String genMD5Val(String string) {
        String md5Val4RvCont = null;
        try {

            byte[] rvCont2bytes = string.getBytes();
            MessageDigest md5Inst = MessageDigest.getInstance("MD5");
            md5Inst.update(rvCont2bytes);
            byte[] md5Val = md5Inst.digest();
            StringBuilder sBuilder = new StringBuilder();
            for (int i = 0; i < md5Val.length; i++) {
                int val = ((int) md5Val[i]) & 0xff;
                if (val < 16)
                    sBuilder.append("0");
                sBuilder.append(Integer.toHexString(val));
            }
            md5Val4RvCont = sBuilder.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return md5Val4RvCont;
    }

    /**
     * append strings and return it
     *
     * @param sign enum ==> Split sign
     * @param args args
     * @return String after append
     */
    public static String getAppendString(Split_Sign sign, String... args) {
        if (args.length < 1)
            return null;
        String splitStr = sign.getSign();
        if (splitStr == null)
            return null;

        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < args.length; i++) {
            if (args[i] == null)
                return null;
            stringBuffer.append(args[i]);
            if (i != args.length - 1)
                stringBuffer.append(splitStr);
        }
        return stringBuffer.toString();
    }

    /**
     * 把数组中的字符串使用指定的分隔符组合成一个新的字符串
     *
     * @param sign                 enum ==> Split sign
     * @param senExpressionStrings the array
     * @return 数组组合后的字符串
     */
    public static String getArrayString(Split_Sign sign,
                                        String[] senExpressionStrings) {
        if (senExpressionStrings.length < 1)
            return null;
        String splitStr = sign.getSign();
        if (splitStr == null)
            return null;

        StringBuffer outString = new StringBuffer();
        for (int i = 0; i < senExpressionStrings.length; i++) {
            outString.append(senExpressionStrings[i] + splitStr);
        }
        return outString.toString();
    }

    /**
     * 对字符串进行原子分隔,比如:解放军第101医院----解 放 军 第 1 0 1 医 院 同上面不一眼，这里的分隔，分开数字和字母，将其当成一个
     *
     * @param str 待分割的字符串
     * @return 分割后的字符数组
     */
    public static String[] atomNumChrSplit(String str) {
        if (str == null)
            return null;
        int nLen = str.length();
        if (nLen < 1)
            return null;

        String[] result = null;
        result = new String[nLen];

        for (int i = 0; i < nLen; i++) {
            result[i] = str.substring(i, i + 1);
        }

        return result;
    }

    /**
     * 判断字符串数组中是否包含特定的字符
     *
     * @param strings the string array
     * @param string  the string
     * @return true if the string array contains the given string
     */
    public static boolean isHaveString(String[] strings, String string) {
        if (strings == null || strings.length < 1 || string == null)
            return false;
        for (String tmpString : strings) {
            if (string.equals(tmpString))
                return true;
        }
        return false;
    }

    /**
     * 判断当前的单词是不是一个好的单词 主要看其是否是数字或者英文字母，另外看是否包含一些特殊符号
     *
     * @param word the input word
     * @return true if only contains letter or digit
     */
    public static boolean isGoodWord(String word) {
        if (word == null)
            return false;

        for (int i = 0; i < word.length() - 1; i++) {
            if (isSpecialSign(word.substring(i, i + 1)))
                return false;
        }

        byte[] bs = word.getBytes();
        for (byte b : bs) {
            if (b >= 48 && b <= 57 || b >= 65 && b <= 90 || b >= 97 && b <= 122)
                return false;
        }

        return true;
    }

    /**
     * 判断当前字符串是否是特殊字符
     *
     * @param str the input string
     * @return true if the input string is special sign
     */
    public static boolean isSpecialSign(String str) {

        for (int i = 0; i < SPECIAL_SIGN.length; i++) {
            if (str.indexOf(SPECIAL_SIGN[i]) != -1)
                return true;
        }

        return false;
    }

    /**
     * 对输入的单字，判断其是否是标点符号
     *
     * @param inputWordString the input string
     * @return true if the input string is punctuation
     */
    public static boolean isPunctuation(String inputWordString) {
        if (SEPERATOR_SUB_SENTENCE.indexOf(inputWordString) != -1)
            return true;
        else
            return false;
    }

    /**
     * 判断字符串是否由字母数字组成
     *
     * @param str the input string
     * @return true if only contains letter or digit
     */
    public static boolean isAlphanumeric(String str) {
        if (str != null) {
            byte[] bs = str.getBytes();
            for (byte b : bs) {
                if (b < 48 || b > 57 && b < 65 || b > 90 && b < 97 || b > 122)
                    return false;
            }
            return true;
        }
        return false;
    }

    /**
     * 判断字符串是否由字母组成
     *
     * @param str the input string
     * @return true if only contains letter
     */
    public static boolean isAlpha(String str) {
        if (str != null) {
            byte[] bs = str.getBytes();
            for (byte b : bs) {
                if (b < 65 || b > 90 && b < 97 || b > 122)
                    return false;
            }
            return true;
        }
        return false;
    }

    /**
     * 判断字符串是否由数字组成
     *
     * @param str the input string
     * @return true if only contains digit
     */
    public static boolean isNumeric(String str) {
        if (str != null) {
            byte[] bs = str.getBytes();
            for (byte b : bs) {
                if (b < 48 || b > 57)
                    return false;
            }
            return true;
        }
        return false;
    }

    /**
     * 判断字符串是否包含数字或者字母
     *
     * @param s the input string
     * @return true if only contains letter or digit
     */
    public static boolean isAlphaAndNumeric(String s) {
        return s.matches(".*\\d.*") && s.matches(".*[a-zA-Z].*");
    }

    /**
     * Return true if it is a English character
     *
     * @param ch the input char
     * @return true if it is a English character
     */
    public static boolean isEnChar(char ch) {
        return ('a' <= ch && ch <= 'z') || ('A' <= ch && ch <= 'Z');
    }

    /**
     * Return true if the character is Chinese
     *
     * @param ch the input char
     * @return true if the character is Chinese
     */
    public static boolean isCnChar(char ch) {
        return ch >= '\u4E00' && ch <= '\u9FFF';
    }

    /**
     * Return true if it is a English character or digit
     *
     * @param ch the input char
     * @return true if it is a English character or digit
     */
    public static boolean isEnCharOrDigit(char ch) {
        return isEnChar(ch) || Character.isDigit(ch);
    }

    /**
     * 判断是否是合法的词边界。规则: 如果词的边界是英文字符，那么边界相邻的不能也是英文字符或者保留字符 （否则可能是词的部分，而不是边界）
     *
     * @param str       the string
     * @param start     left boundary
     * @param end       right boundary
     * @param rsrvChars the reserved chars
     * @return true if it is a valid word boundary
     */
    public static boolean isValidWordBoundary(String str, int start, int end,
                                              String rsrvChars) {
        if (start > 0) { // left boundary
            char lb = str.charAt(start - 1);
            if (isEnCharOrDigit(str.charAt(start)) && // 左边是英文字符, 旁边就不能还是英文或保留字符
                    (isEnCharOrDigit(lb) || rsrvChars.indexOf(lb) != -1))
                return false;
        }

        if (end < str.length()) { // right boundary
            char rb = str.charAt(end);
            if (isEnCharOrDigit(str.charAt(end - 1))
                    && (isEnCharOrDigit(rb) || rsrvChars.indexOf(rb) != -1))
                return false;
        }
        return true;
    }

    /**
     * 判断是否是合法的词边界。规则: 如果词的边界是英文字符，那么边界相邻的不能也是英文字符或者保留字符 （否则可能是词的部分，而不是边界）
     *
     * @param str   the string
     * @param start left boundary
     * @param end   right boundary
     * @return true if it is a valid word boundary
     */
    public static boolean isValidWordBoundary(String str, int start, int end) {
        return isValidWordBoundary(str, start, end, "");
    }

    /**
     * Converts from internal URL to external URL internal URL:
     * com.example.www/a/b.. external URL: http://www.example.com/a/b
     *
     * @param internalUrl the internal url
     * @return external url
     */
    public static String toExternalUrl(String internalUrl) {
        int pos = internalUrl.indexOf('/');
        String[] strAry = null;
        if (pos != -1)
            strAry = internalUrl.substring(0, pos).split("\\.");
        else
            strAry = internalUrl.split("\\.");

        String extUrl = new String("http://");
        for (int i = strAry.length - 1; i > 0; i--)
            extUrl += strAry[i] + ".";
        extUrl += strAry[0]
                + internalUrl.substring(pos == -1 ? internalUrl.length() : pos);
        return extUrl;
    }

    /**
     * Converts from external URL to internal URL external URL:
     * http://www.example.com/a/b internal URL: com.example.www/a/b .
     *
     * @param externalUrl the external url
     * @return internal url
     */
    public static String toInternalUrl(String externalUrl) {
        if (externalUrl == null || externalUrl.isEmpty())
            return externalUrl;
        if (externalUrl.startsWith("http://"))
            externalUrl = externalUrl.substring(7);
        return toExternalUrl(externalUrl).substring(7);
    }

    /**
     * Merge two delimited string with duplicates removed. Example: str1=a|b|c,
     * str2=a|d|e, return a|b|c|d|e Assumption: assume the two strings have no
     * duplicates in themselves.
     *
     * @param str1    the first delimited string
     * @param str2    the second delimited string
     * @param delimit the delimiter for str1/str2
     * @return the merged string
     */
    public static String mergeDelimitStrWithNoDup(String str1, String str2,
                                                  char delimit) {
        if (str1 == null && str2 == null)
            return null;
        if (str1 == null || str2 == null)
            return str1 != null ? str1 : str2;

        // Insert str1 into a hashset
        String[] elemList = str1.split("\\" + delimit);// 反斜线是为了特殊字符的分隔
        HashSet<String> set = new HashSet<String>(elemList.length);
        for (String e : elemList)
            set.add(e);

        // Check for duplicates and merge
        StringBuilder rst = new StringBuilder(str1);
        elemList = str2.split("\\" + delimit);
        for (String e : elemList)
            if (!e.isEmpty() && !set.contains(e)) {
                rst.append(delimit);
                rst.append(e);
            }
        return rst.toString();
    }

    /**
     * Return the first occurrence of s1 or s2 in str. -1 if none of them occurs
     *
     * @param str the source string
     * @param s1  the first substring to search for
     * @param s2  the second substring to search for
     * @return the index of the first occurrence of s1 or s2 in source string
     */
    public static int indexOf(String str, String s1, String s2) {
        int p1 = str.indexOf(s1);
        int p2 = str.indexOf(s2);
        return p1 > p2 ? p1 : p2;
    }

    /**
     * Backward maximal match algorithm: find all the words in dictionary by max
     * matching. if the string has several matched substrings, the order is from
     * begin to end
     *
     * @param s    - the string to be matched
     * @param dict - the dictionary
     * @return the list words in the pairs of start and end position. Note that
     * the words are sorted by its occurrence from left to right.
     */
    public static <T> List<Pair<Integer, Integer>> backwardMaxMatch(String s,
                                                                    Map<String, T> dict, int maxWordLen, int minWordLen) {
        LinkedList<Pair<Integer, Integer>> wordList = new LinkedList<Pair<Integer, Integer>>();
        int curL = 0, curR = s.length(); // Left and Right end of current
        // matching, excluding the right
        // end.
        while (curR >= minWordLen) {
            boolean isMatched = false;
            curL = curR - maxWordLen < 0 ? 0 : (curR - maxWordLen);
            while (curR - curL >= minWordLen) { // try all subsets backwards
                if (dict.containsKey(s.substring(curL, curR))) { // matched
                    wordList.addFirst(new Pair<Integer, Integer>(new Integer(
                            curL), new Integer(curR)));
                    curR = curL;
                    curL = curR - maxWordLen < 0 ? 0 : (curR - maxWordLen);
                    isMatched = true;
                    break;
                } else
                    // not matched, try subset by moving left rightwards
                    curL++;
            }
            if (!isMatched) // not matched, move the right end leftwards
                curR--;
        }
        return wordList;
    }

    /**
     * Generate a string with all the elements in strList separated by 'delimit'
     *
     * @param objList the input collection
     * @param delimit delimiter
     * @param <T>     the Type of Collection's element
     * @return string with all the elements in strList separated by 'delimit'
     */
    public static <T> String genDelimitedString(Collection<T> objList,
                                                char delimit) {
        if (objList == null || objList.isEmpty())
            return "";
        StringBuilder sb = new StringBuilder();
        for (T t : objList) {
            sb.append(t.toString());
            sb.append(delimit);
        }
        return sb.substring(0, sb.length() - 1);
    }

    /**
     * Generate a string with all the elements in array separated by 'delimit'
     *
     * @param objList the input collection
     * @param delimit delimiter
     * @param <T>     the Type of Array's element
     * @return string with all the elements in array separated by 'delimit'
     */
    public static <T> String genDelimitedString(T[] objList, char delimit) {
        if (objList == null || objList.length < 1)
            return "";
        StringBuilder sb = new StringBuilder();
        for (T t : objList) {
            sb.append(t.toString());
            sb.append(delimit);
        }
        return sb.substring(0, sb.length() - 1);
    }

    /**
     * Find longest common substring between s1 and s2. Algorithm complexity
     * O(mn); O(mn) storage complexity.
     *
     * @param first  the first input string
     * @param second the second input string
     * @return the longest common substring; null if no common substring
     */
    public static String longestCommonSubstr(String first, String second) {
        if (first == null || second == null || first.length() == 0
                || second.length() == 0) {
            return null;
        }

        int maxLen = 0, endLCS = 0; // endLCS - the end position of LCS found in
        // first
        int fl = first.length(), sl = second.length();
        int[][] table = new int[fl][sl];

        for (int i = 0; i < fl; i++) {
            for (int j = 0; j < sl; j++) {
                if (first.charAt(i) == second.charAt(j)) {
                    table[i][j] = (i == 0 || j == 0) ? 1
                            : table[i - 1][j - 1] + 1;
                    if (table[i][j] > maxLen) {
                        maxLen = table[i][j];
                        endLCS = i;
                    }
                }
            }
        }
        return maxLen == 0 ? null : first.substring(endLCS + 1 - maxLen,
                endLCS + 1);
    }

    /**
     * find all LCS against the reference string
     *
     * @param str    the string
     * @param refStr the reference string
     * @param minLen the minimum LCS length
     * @return the list of LCS ( in the index order)
     */
    public static Collection<String> findAllLCS(String str, String refStr,
                                                int minLen) {
        TreeMap<Integer, String> idx2LCS = new TreeMap<Integer, String>();
        String lcs = null;
        String oriFirstStr = str;
        while ((lcs = StringUtil.longestCommonSubstr(str, refStr)) != null
                && lcs.length() >= minLen) {
            int pos = oriFirstStr.indexOf(lcs);
            idx2LCS.put(pos, lcs);
            pos = str.indexOf(lcs);
            // Merge the rest with different special character (avoiding false
            // match due to merge operation).
            str = str.substring(0, pos) + 'Ê'
                    + str.substring(pos + lcs.length());
            pos = refStr.indexOf(lcs);
            refStr = refStr.substring(0, pos) + 'Ô'
                    + refStr.substring(pos + lcs.length());
        }
        return idx2LCS.values();
    }

    /**
     * Tell whether string s1 is a subsequence of s2, i.e. all the chars in s1
     * occurs in s2 in the same order. Example: ac is a subsequence of badc
     *
     * @param s1 a string
     * @param s2 a string
     * @return true if s1 is a subsequence of s2
     */
    public static boolean isSubsequence(String s1, String s2) {
        int len1 = s1.length();
        int len2 = s2.length();
        int[][] sublen = new int[len1 + 1][len2 + 1];
        int[][] subdir = new int[len1 + 1][len2 + 1];

        for (int i = 0; i <= len1; i++) {
            sublen[i][0] = 0;
        }
        for (int i = 0; i <= len2; i++) {
            sublen[0][i] = 0;
        }

        for (int i = 1; i <= len1; i++) {
            for (int j = 1; j <= len2; j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    sublen[i][j] = sublen[i - 1][j - 1] + 1;
                    subdir[i][j] = 0;
                } else if (sublen[i - 1][j] >= sublen[i][j - 1]) {
                    sublen[i][j] = sublen[i - 1][j];
                    subdir[i][j] = 1;
                } else {
                    sublen[i][j] = sublen[i][j - 1];
                    subdir[i][j] = 2;
                }
            }
        }
        if (len1 == sublen[len1][len2] || len2 == sublen[len1][len2]) {
            return true;
        } else {
            return false;
        }
    }

    private static final String PASSWORD_CRYPT_KEY = "yeezhao_key_3210";
    private final static String DES = "DES";

    /**
     * 加密
     * {@link \http://www.blogjava.net/afei0922/articles/126332.html}
     *
     * @param src 数据源
     * @param key 密钥，长度必须是8的倍数
     * @return 返回加密后的数据
     * @throws Exception
     */
    public static byte[] encrypt(byte[] src, byte[] key) throws Exception {
        // DES算法要求有一个可信任的随机数源
        SecureRandom sr = new SecureRandom();
        // 从原始密匙数据创建DESKeySpec对象
        DESKeySpec dks = new DESKeySpec(key);
        // 创建一个密匙工厂，然后用它把DESKeySpec转换成
        // 一个SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        SecretKey securekey = keyFactory.generateSecret(dks);
        // Cipher对象实际完成加密操作
        Cipher cipher = Cipher.getInstance(DES);
        // 用密匙初始化Cipher对象
        cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);
        // 现在，获取数据并加密
        // 正式执行加密操作
        return cipher.doFinal(src);
    }

    /**
     * 解密
     *
     * @param src 数据源
     * @param key 密钥，长度必须是8的倍数
     * @return 返回解密后的原始数据
     * @throws Exception
     */
    public static byte[] decrypt(byte[] src, byte[] key) throws Exception {
        // DES算法要求有一个可信任的随机数源
        SecureRandom sr = new SecureRandom();
        // 从原始密匙数据创建一个DESKeySpec对象
        DESKeySpec dks = new DESKeySpec(key);
        // 创建一个密匙工厂，然后用它把DESKeySpec对象转换成
        // 一个SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        SecretKey securekey = keyFactory.generateSecret(dks);
        // Cipher对象实际完成解密操作
        Cipher cipher = Cipher.getInstance(DES);
        // 用密匙初始化Cipher对象
        cipher.init(Cipher.DECRYPT_MODE, securekey, sr);
        // 现在，获取数据并解密
        // 正式执行解密操作
        return cipher.doFinal(src);
    }

    /**
     * 密码解密
     *
     * @param data 加密的数据
     * @return 解密后的数据
     * @throws Exception
     */
    public final static String decrypt(String data) {
        try {
            return new String(decrypt(hex2byte(data.getBytes()),
                    PASSWORD_CRYPT_KEY.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 密码加密
     *
     * @param password 原始数据
     * @return 加密后的数据
     * @throws Exception
     */
    public final static String encrypt(String password) {
        try {
            return byte2hex(encrypt(password.getBytes(),
                    PASSWORD_CRYPT_KEY.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 二进制转字符串
     *
     * @param b 字节数组
     * @return 转换成16进制的字符串
     */
    public static String byte2hex(byte[] b) {
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = (Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1)
                hs = hs + "0" + stmp;
            else
                hs = hs + stmp;
        }
        return hs.toUpperCase();
    }

    private static byte[] hex2byte(byte[] b) {
        if ((b.length % 2) != 0)
            throw new IllegalArgumentException("长度不是偶数");
        byte[] b2 = new byte[b.length / 2];
        for (int n = 0; n < b.length; n += 2) {
            String item = new String(b, n, 2);
            b2[n / 2] = (byte) Integer.parseInt(item, 16);
        }
        return b2;
    }

    /**
     * 将形如\u52fe\u52fe\u811a\u8dbe\u5934的string转换成正常字符串。
     *
     * @param unicodeString unicode字符串
     * @return 转换后的正常字符串
     */
    public static String decodeUnicodeString(String unicodeString) {
        char aChar;
        int len = unicodeString.length();
        StringBuffer outBuffer = new StringBuffer(len);

        for (int x = 0; x < len; ) {
            aChar = unicodeString.charAt(x++);
            if (aChar == '\\') {
                aChar = unicodeString.charAt(x++);
                if (aChar == 'u') {
                    // Read the xxxx
                    int value = 0;
                    for (int i = 0; i < 4; i++) {
                        aChar = unicodeString.charAt(x++);
                        switch (aChar) {
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                value = (value << 4) + aChar - '0';
                                break;
                            case 'a':
                            case 'b':
                            case 'c':
                            case 'd':
                            case 'e':
                            case 'f':
                                value = (value << 4) + 10 + aChar - 'a';
                                break;
                            case 'A':
                            case 'B':
                            case 'C':
                            case 'D':
                            case 'E':
                            case 'F':
                                value = (value << 4) + 10 + aChar - 'A';
                                break;
                            default:
                                throw new IllegalArgumentException(
                                        "Malformed   \\uxxxx   encoding.");
                        }
                    }
                    outBuffer.append((char) value);
                } else {
                    if (aChar == 't')
                        aChar = '\t';
                    else if (aChar == 'r')
                        aChar = '\r';
                    else if (aChar == 'n')
                        aChar = '\n';
                    else if (aChar == 'f')
                        aChar = '\f';
                    outBuffer.append(aChar);
                }
            } else
                outBuffer.append(aChar);
        }
        return outBuffer.toString();
    }

    /**
     * 判断字符串是否为空对象或者字符串的长度为0
     *
     * @param str the input string
     * @return true if str is null or it's length is 0
     */
    public static boolean isNullOrEmpty(String str) {
        if (str == null || str.isEmpty())
            return true;
        return false;
    }

    /**
     * str split by char with no regex
     *
     * @param str the input string
     * @param sep the separator
     * @return the string array after separate
     */
    public static String[] strSplit(String str, char sep) {
        List<String> list = new LinkedList<String>();
        int pe = 0, ps = 0;

        while (pe != -1) {
            pe = str.indexOf(sep, ps);
            String s = "";
            if (pe == -1)
                s = str.substring(ps);
            else if (pe > ps) {
                s = str.substring(ps, pe);
                ps += (pe - ps) + 1;
            } else
                ps += 1;
            list.add(s);
        }

        String[] ss = new String[list.size()];
        list.toArray(ss);
        return ss;
    }

    /**
     * str split by string with no regex
     *
     * @param str the input string
     * @param sep the separator
     * @return the string array after separate
     */
    public static String[] strSplit(String str, String sep) {
        List<String> list = new LinkedList<String>();
        int pe = 0, ps = 0;

        while (pe != -1) {
            pe = str.indexOf(sep, ps);
            String s = "";
            if (pe == -1)
                s = str.substring(ps);
            else if (pe > ps) {
                s = str.substring(ps, pe);
                ps += (pe - ps) + 1;
            } else
                ps += sep.length();
            list.add(s);
        }

        String[] ss = new String[list.size()];
        list.toArray(ss);
        return ss;
    }

}
