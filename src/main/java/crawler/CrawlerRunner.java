package crawler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 定义爬虫服务的线程Callable接口
 */
public class CrawlerRunner<Message extends crawler.entity.Message> implements Callable {
    private static final Logger LOG = LoggerFactory.getLogger(CrawlerRunner.class);
    private ICrawler<Message> crawler;
    private Message input;
    private ConcurrentLinkedQueue<Message> all;

    public CrawlerRunner(ICrawler<Message> crawler, Message input, final ConcurrentLinkedQueue<Message> all) {
        this.crawler = crawler;
        this.input = input;
        this.all = all;
    }

    public void run() {

        if (crawler != null && input != null) {
            try {
                List<Message> result = crawler.crawl(input);
                if (result != null) {
                    all.addAll(result);
                }
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
        } else {
            LOG.warn("crawler or input is null");
        }
    }

    @Override
    public Object call() throws Exception {
        run();
        return 1;
    }
}
