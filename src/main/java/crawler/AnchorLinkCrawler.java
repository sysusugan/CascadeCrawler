package crawler;

import crawler.entity.Message;
import crawler.entity.NewsMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import util.HtmlUnitUtil;
import util.Pair;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;

/**
 * 抽取HTML A 标签链接
 */
@Slf4j
public class AnchorLinkCrawler implements ICrawler<Message> {
    private List<String> cssQueryList = new LinkedList<>();

    public AnchorLinkCrawler(String cssQuery) {
        anchorSelector(cssQuery);
    }

    @Override
    public List<Message> crawl(Message msg) {
        String url = msg.getString(Message._URL);
        return extractByAnchorSelector(url, cssQueryList);
    }

    public AnchorLinkCrawler anchorSelector(String cssQuery) {
        this.cssQueryList.add(cssQuery);
        return this;
    }

    /**
     * 抽取指定URL 的 链接 （HTML anchor类标签的 selector）
     *
     * @param url      传入URL
     * @param cssQueryList html的 <a> 标签selectors
     * @return
     */
    private List<Message> extractByAnchorSelector(String url, List<String> cssQueryList) {
        List<Message> rst = new LinkedList<>();
        Document doc = null;
        try {
            /**
             * 偷懒用HTMLUnit渲染， 否则分析Ajax请求太麻烦~
             */
            Pair<String, String> pair = HtmlUnitUtil.visit(url, 10000);
            doc = Jsoup.parse(pair.first);
//            doc = Jsoup.parse(new URL(url), 10000);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        for (String cssQuery : cssQueryList) {
            Elements eles = doc.select(cssQuery);

            for (Element ele : eles) {
                String href = ele.attr("href");
                Message obj = new NewsMessage();
                obj.set(Message._URL, href);
                if (StringUtils.isNotEmpty(href))
                    rst.add(obj);
            }
        }

        System.out.println("===size: " + rst.size());
        return rst;
    }

}
