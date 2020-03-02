package crawler;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 爬虫线程池管理服务
 */

public class CrawlerExecutorService<Message extends crawler.entity.Message> {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(CrawlerExecutorService.class);
    private static final long WAIT_TIME_SECONDS = 5;

    //最后一次fetcher的状态或结果信息的集合
    private ConcurrentHashMap<Integer, Collection<Message>> currentRst = new ConcurrentHashMap<>();
    private AtomicInteger stageId = new AtomicInteger(0);
    private ExecutorService pool = null;
    private boolean parallel = true;

    /**
     * 初始化构造函数
     *
     * @param numThreads 线程池线程数(并发数)
     */
    public CrawlerExecutorService(int numThreads) {
        this.pool = Executors.newFixedThreadPool(numThreads);
    }

    private boolean checkStatus() {
        if (!currentRst.isEmpty())
            return true;
        throw new InvalidParameterException("Not initiated！");

    }

    public CrawlerExecutorService doFetcher(Collection<Message> input, ICrawler executor) {
        this.currentRst.put(stageId.intValue(), input != null ? input : new LinkedList<>());
        checkStatus();
        return doFetcher(executor);
    }

    public CrawlerExecutorService setParallel(boolean parallel) {
        this.parallel = parallel;
        return this;
    }

    public CrawlerExecutorService doFetcher(ICrawler crawler) {
        checkStatus();

        int currentStageId = stageId.intValue();
        Collection<Message> status = currentRst.get(currentStageId);
        int nextStageId = stageId.addAndGet(1);

        ConcurrentLinkedQueue<Message> allRst = new ConcurrentLinkedQueue<>();
        LOG.info("[Stage：{} ] , 待处理种子数量:{}, 爬虫:{} ", currentStageId, status.size(), crawler.getClass().getSimpleName());
        ArrayList<Callable<Integer>> callers = new ArrayList<Callable<Integer>>();
        if (this.parallel) {
            for (Message s : status) {
                //allRst负责收集所有子线程的结果
                CrawlerRunner runner = new CrawlerRunner(crawler, s, allRst);
                callers.add(runner);
            }

            try {
                pool.invokeAll(callers);
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
            LOG.info("[Stage：{}] 多线程模式执行完毕", currentStageId);
        } else {
            for (Message s : status) {
                try {
                    List<Message> tmpRst = crawler.crawl(s);
                    if (tmpRst != null && tmpRst.size() > 0) {
                        allRst.addAll(tmpRst);
                    }
                } catch (Exception e) {
                    LOG.error(e.getMessage(), e);
                }
            }
            LOG.info("[Stage：{}] 单线程模式执行完毕", currentStageId);
        }

        //每次清理上一阶段的内存，让JVM自动GC
        LOG.info("[Stage：{}]清理完毕", currentStageId);
        if (this.currentRst.get(currentStageId) != null)
            this.currentRst.remove(currentStageId);

        LOG.info("[Stage：{}]初始化完成", nextStageId);
        this.currentRst.put(nextStageId, allRst);

        return this;
    }

    public void close() throws InterruptedException {
        LOG.debug("Waiting for executor to shutdown in " + WAIT_TIME_SECONDS + " seconds...");
        this.pool.awaitTermination(WAIT_TIME_SECONDS, TimeUnit.SECONDS);
        pool.shutdownNow();
        LOG.debug("Executor closed...");
    }
}
