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
 * 目标入口地址：http://news.163.com/rank/
 */
public class NeteaseNewsCralwTest {
    private static final Logger LOG = LoggerFactory.getLogger(NeteaseNewsCralwTest.class);

    public static void main(String[] args) throws InterruptedException {
        CrawlerExecutorService executor = new CrawlerExecutorService(10);

        //初始种子
        Message msg = new Message();
        msg.set(Message._URL, "http://news.163.com/special/0001386F/rank_news.html");
        List<Message> seeds = Collections.singletonList(msg);

        executor
                .setParallel(false)
                //$$('div.area-half.left > div > div.tabContents.active > table > tbody > tr > td.rank > a')
                .doFetcher(seeds, new AnchorLinkCrawler("div.area-half.left > div > div.tabContents.active > table > tbody > tr > td.rank > a"))// seeds是 CategoryUrlGenICrawler爬虫要处理（并知道如何处理）的
                .doFetcher((new NewsCrawler())
                        .selector(NewsCrawler.TITLE, "#epContentLeft > h1")
                        .selector(NewsCrawler.CONTENT, "#endText")
                        .selector(NewsCrawler.AUTHOR, "#ne_article_source")
                        .selector(NewsCrawler.DATE, "#epContentLeft > div.post_time_source")
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

