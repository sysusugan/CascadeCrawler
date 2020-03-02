package crawler.entity;

import com.google.gson.Gson;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 抽象装载爬取中间过程, 数据的存储对象
 */

public class Message implements Serializable {
    protected static final Gson gson = new Gson();
    protected static final Logger LOG = Logger.getLogger(Message.class);
    public static final String _URL = "_url";

    private Map<String, Object> info = new HashMap<>();        // 主体内容, 如文本/URL等, 也可附带额外信息

    public Object get(String key) {
        return info.get(key);
    }

    public String getString(String key) {
        return (String) info.get(key);
    }

    public void set(String key, Object val) {
        info.put(key, val);
    }

    public String toCsvString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : info.entrySet()) {
            sb.append(entry.getValue()).append(",");
        }
        return sb.substring(0, sb.length() - 1);
    }

    public String toJsonString() {
        return gson.toJson(info);
    }

    @Override
    public String toString() {
        return this.toJsonString();
    }
}
