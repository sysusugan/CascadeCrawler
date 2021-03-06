package test.main;

import crawler.AnchorLinkCrawler;
import crawler.CrawlerExecutorService;
import crawler.ICrawler;
import crawler.NewsCrawler;
import crawler.entity.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
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

        executor.doFetcher(seeds, new AnchorLinkCrawler("#feedCardContent .feed-card-item h2 a"))
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

