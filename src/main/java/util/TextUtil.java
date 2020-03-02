package util;

import com.yeezhao.commons.util.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.net.URI;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zebin
 * @since 2016-06-14.
 */
public class TextUtil {

    public static int hit(String longText, String shortText) {
        if (StringUtils.isEmpty(longText) || StringUtils.isEmpty(shortText)) {
            return 0;
        }
        int idx = longText.indexOf(shortText);
        int cnt = 0;
        while (idx != -1) {
            cnt++;
            idx = longText.indexOf(shortText, idx + shortText.length());
        }
        return cnt;
    }

    public static List<Long> numbers(String text) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(text);
        List<Long> list = new LinkedList<>();
        while (matcher.find()) {
            list.add(Long.parseLong(matcher.group()));
        }
        return list;
    }

    public static String extractFirstWord(String text, Pattern pattern) {
        return extractXthWord(text, pattern, 1);
    }

    public static String extractXthWord(String text, Pattern pattern, int index) {
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(index);
        }
        return null;
    }

    /**
     * 去掉以.htm/.html/.shtml结尾的url的查询参数
     *
     * @param url
     * @return
     */
    public static String removeUrlQueryParam(String url) {
        if (StringUtil.isNullOrEmpty(url)) {
            return null;
        }
        String[] filter = {".htm", ".html", ".shtml"};
        URI uri = URI.create(url);
        String path = uri.getPath();
        for (String suffix : filter) {
            if (path.endsWith(suffix)) {
                StringBuilder buffer = new StringBuilder();
                int port = uri.getPort();
                String portStr = port != -1 ? String.valueOf(port) : "";
                return buffer.append(uri.getScheme()).append("://")
                        .append(uri.getHost()).append(portStr)
                        .append(path).toString();
            }
        }
        return url;
    }

    // 去重
    public static String uniqStr(String str, String sep) {
        String[] strs = str.split(sep);
        Set<String> set = new HashSet<>();
        for (String s : strs) {
            set.add(s);
        }
        return StringUtils.join(set, sep);
    }
    // 去重
    public static String[] uniqStr(String[] strs) {
        Set<String> set = new HashSet<>();
        CollectionUtils.addAll(set, strs);
        return set.toArray(strs);
    }
}
