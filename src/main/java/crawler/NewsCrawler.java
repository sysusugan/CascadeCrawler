package crawler;

import crawler.entity.Message;
import crawler.entity.NewsMessage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.LoggerFactory;
import util.DateUtil;

import java.net.URL;
import java.text.ParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通用selector抽取爬虫，每个网页只能抽取一条数据（Key-value）
 * （一次抽取并返回多条数据场景，需自行编码实现）
 */
public class NewsCrawler implements ICrawler<NewsMessage> {
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(NewsCrawler.class);
    public static final String TITLE = "title";
    public static final String AUTHOR = "author";
    public static final String CONTENT = "content";
    public static final String DATE = "date";


    private HashMap<String, String> selectors = new HashMap<>();

    public NewsCrawler selector(String fieldName, String selector) {
        selectors.put(fieldName, selector);
        return this;
    }

    @Override
    public List<NewsMessage> crawl(NewsMessage msg) {
        try {
            String url = msg.getString(Message._URL);
            Document doc = null;
            int i = 3;
            Exception e_out = null;
            while (i-- > 0) {
                try {
                    doc = Jsoup.parse(new URL(url), 15000);
                    break;
                } catch (Exception e) {
                    e_out = e;
                }
            }
            if (doc == null) {
                LOG.warn("抓取失败url:{}", url);
                LOG.error(e_out.getMessage(), e_out);
            }
            NewsMessage newsObj = new NewsMessage();

            if (!selectors.isEmpty() && doc != null) {
                for (Map.Entry<String, String> entry : selectors.entrySet()) {
                    String key = entry.getKey();
                    String selector = entry.getValue();
                    String htmlVal = doc.select(selector).text();

                    switch (key) {
                        case TITLE:
                            newsObj.setTitle(htmlVal);
                            break;
                        case AUTHOR:
                            newsObj.setAuthor(htmlVal);
                            break;
                        case CONTENT:
                            newsObj.setContent(htmlVal);
                            break;
                        case DATE:
                            String fomattedDateStr = null;
                            try {
                                fomattedDateStr = DateUtil.toRhinoStandardDate(htmlVal);
                                newsObj.setDate(fomattedDateStr);
                            } catch (ParseException e) {
                                LOG.warn("url:{} ", url);
                                LOG.warn(e.getMessage(), e);
                            }
                            break;
                        default:
                            newsObj.set(key, htmlVal);
                            break;
                    }

                }
            }

            return Collections.singletonList(newsObj);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }

}
