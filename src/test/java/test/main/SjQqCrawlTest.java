package test.main;

import crawler.CrawlerExecutorService;
import crawler.ICrawler;
import crawler.entity.Message;
import crawler.site.sj.qq.AppNameObj;
import crawler.site.sj.qq.ContentICrawler;
import org.apache.log4j.Logger;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 腾讯应用宝示例
 */
public class SjQqCrawlTest {
    private static final Logger LOG = Logger.getLogger(SjQqCrawlTest.class);

    public static void main(String[] args) throws InterruptedException {
        LOG.info("当前线程id： " + Thread.currentThread().getId());
        final AtomicInteger i = new AtomicInteger(0);
        long start = System.currentTimeMillis();

        final String[] arr = new String[]{
                "103", "101", "122", "102", "112", "106", "104", "110", "115", "119", "111", "107", "118", "108", "100", "114", "117", "109", "105", "113", "116"
        };


        CrawlerExecutorService executor = new CrawlerExecutorService(10);
        executor.doFetcher(new LinkedList<Message>() {{
            add(new Message());
        }}, new ICrawler() {
            @Override
            public List<Message> crawl(Message input) {
                LinkedList<Message> ret = new LinkedList<>();
                //爬取1页 30个
                int pageSize = 30;
                int startRow = 0;
                for (String cateId : arr) {
                    ret.add(new Message() {
                        {
                            set(Message._URL, String.format("http://sj.qq.com/myapp/cate/appList.htm?orgame=1&categoryId=%s&pageSize=%s&pageContext=%s"
                                    , cateId, pageSize, startRow));
                        }
                    });
                }

                return ret;
            }
        })// seeds是 CategoryUrlGenICrawler爬虫要处理（并知道如何处理）的
//                .setParallel(false)// 默认是并发，下一步不要并发运行（不使用线程池），因为要使用写文件，会有多线程不安全的问题
                .doFetcher(new ContentICrawler())
                .setParallel(false)// 默认是并发，下一步不要并发运行（不使用线程池），因为要使用写文件，会有多线程不安全的问题
                .doFetcher(new ICrawler<AppNameObj>() {
                    @Override
                    public List<AppNameObj> crawl(AppNameObj input) {
                        int v = i.addAndGet(1);
                        LOG.debug("Thread ID:" + Thread.currentThread().getId() + "\t  Line" + v + ": " + input);
                        try {
                            LOG.info(input.toCsvString());
                        } catch (Exception e) {
                            LOG.error(e.getMessage(), e);
                        }
                        return null;
                    }
                });

        long end = System.currentTimeMillis();
        LOG.info("take:" + (end - start) / 1000 + " sec");
        LOG.info("total lines:" + i.get() + " line");

        executor.close();
    }
}


