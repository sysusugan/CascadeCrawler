package crawler.site.sj.qq;

import com.google.gson.Gson;
import crawler.ICrawler;
import crawler.entity.Message;
import org.apache.log4j.Logger;
import util.HttpUtil;

import java.util.LinkedList;
import java.util.List;

/**
 *
 */

public class ContentICrawler implements ICrawler<Message> {
    private static final Logger LOG = Logger.getLogger(ContentICrawler.class);
    private Gson GSON = new Gson();


    public List<Message> crawl(Message msg) {
        LOG.info("Fetching url:" + msg);

        List<Message> ret = new LinkedList<>();
        try {
            String docStr = HttpUtil.getDocumentString(msg.getString(Message._URL));
            JsonRstObj rst = GSON.fromJson(docStr, JsonRstObj.class);
            if (rst.obj != null && rst.obj.size() > 0) {
                for (AppNameObj obj : rst.obj) {
                    ret.add(obj);
                }
            }
        } catch (Throwable e) {
            LOG.error(e.getMessage(), e);
        }
        return ret;
    }

}

//分析网页结构
class JsonRstObj {
    public List<AppNameObj> obj;
}

