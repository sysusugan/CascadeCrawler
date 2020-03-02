package test.main;

import com.yeezhao.commons.util.Pair;
import crawler.CrawlerExecutorService;
import crawler.ICrawler;
import crawler.NewsCrawler;
import crawler.entity.Message;
import crawler.entity.NewsMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HtmlUnitUtil;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 目标入口地址：https://news.sina.com.cn/china/
 */
public class SinaNewsCralwTest {
    private static final Logger LOG = LoggerFactory.getLogger(SinaNewsCralwTest.class);

    public static void main(String[] args) throws InterruptedException {
        CrawlerExecutorService executor = new CrawlerExecutorService(10);

        //初始种子
        Message msg = new Message();
        msg.set(Message._URL, "https://news.sina.com.cn/china/");
        List<Message> seeds = Collections.singletonList(msg);

        executor.doFetcher(seeds, new SinaLinkCrawler())// seeds是 CategoryUrlGenICrawler爬虫要处理（并知道如何处理）的
                .doFetcher((new NewsCrawler())
                        .selector(NewsCrawler.TITLE, ".main-title")
                        .selector(NewsCrawler.CONTENT, "#article")
                        .selector(NewsCrawler.AUTHOR, "#top_bar > div > div.date-source > a")
                        .selector(NewsCrawler.DATE, "#top_bar > div > div.date-source > span")
                )
//                .setParallel(false)// 默认是并发，下一步不要并发运行（不使用线程池），因为要使用写文件，会有多线程不安全的问题
                .doFetcher(new ICrawler() {
                    //打印操作
                    @Override
                    public List<Message> crawl(Message input) {
                        System.out.println(input);
                        return null;
                    }
                });

        executor.close();
    }
}

@Slf4j
class SinaLinkCrawler implements ICrawler<Message> {

    @Override
    public List<Message> crawl(Message msg) {
        List<Message> rst = new LinkedList<>();
        Document doc = null;
        try {
            Pair<String, String> pair = HtmlUnitUtil.visit(msg.getString(Message._URL), 10000);
            doc = Jsoup.parse(pair.first);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        Elements eles = doc.select("#feedCardContent .feed-card-item h2 a");

        for (Element ele : eles) {
            String href = ele.attr("href");
            Message obj = new NewsMessage();
            obj.set(Message._URL, href);
            if (StringUtils.isNotEmpty(href))
                rst.add(obj);
        }
        System.out.println("===size: " + rst.size());
        return rst;
    }
}