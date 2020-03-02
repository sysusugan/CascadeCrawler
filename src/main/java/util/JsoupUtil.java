package util;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JsoupUtil {
    public static List<String> toStringList(Elements elements){
        return elements.stream().map(Element::text).collect(Collectors.toList());
    }

    public static Map<String, String> pick(Element element, Map<String,String> translation) {
        Map<String, String> ret = new HashMap<>();
        for (Map.Entry<String, String> entry : translation.entrySet()) {
            ret.put(entry.getKey(), element.select(entry.getValue()).text().trim());
        }
        return ret;
    }
}
