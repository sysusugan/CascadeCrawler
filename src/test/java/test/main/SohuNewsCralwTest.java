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
 * //搜狐时政
 * 目标入口地址： "https://www.sohu.com/c/8/1460?spm=smpc.news-home.top-subnav.2.1583393551708Sn5Synn"
 */
public class SohuNewsCralwTest {
    private static final Logger LOG = LoggerFactory.getLogger(SohuNewsCralwTest.class);

    public static void main(String[] args) throws InterruptedException {
        CrawlerExecutorService executor = new CrawlerExecutorService(10);

        //初始种子
        Message msg = new Message();
        //搜狐时政
        msg.set(Message._URL, "https://www.sohu.com/c/8/1460?spm=smpc.news-home.top-subnav.2.1583393551708Sn5Synn");
        List<Message> seeds = Collections.singletonList(msg);

        executor
                .setParallel(false)
                .doFetcher(seeds, new AnchorLinkCrawler("#main-news > div > div.news-wrapper > div  h4 > a"))// seeds是 CategoryUrlGenICrawler爬虫要处理（并知道如何处理）的
                .doFetcher((new NewsCrawler())
                        .selector(NewsCrawler.TITLE, "#article-container > div.left.main > div:nth-child(1) > div.text > div.text-title > h1")
                        .selector(NewsCrawler.CONTENT, "#mp-editor")
                        .selector(NewsCrawler.AUTHOR, "#user-info > h4 > a")
                        .selector(NewsCrawler.DATE, "#news-time")
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

