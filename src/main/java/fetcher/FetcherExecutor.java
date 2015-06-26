package fetcher;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author sugan
 * @since 2015-05-25.
 */


public class FetcherExecutor {
    private static final long WAIT_TIME = 10;

    //最后一次fetcher的状态或结果信息的集合
    private ConcurrentHashMap<Integer, Collection<String>> currentRst = new ConcurrentHashMap<Integer, Collection<String>>();
    private AtomicInteger stageId = new AtomicInteger(0);
    private ExecutorService pool = null;
    private boolean parallel = true;

    public FetcherExecutor(int numThreads) {
        this.pool = Executors.newFixedThreadPool(numThreads);
    }

    private boolean checkStatus() {
        if (!currentRst.isEmpty())
            return true;
        throw new InvalidParameterException("Not initiated！");

    }

    public FetcherExecutor doFetcher(Collection<String> input, Fetcher executor) {
        this.currentRst.put(stageId.intValue(), input);
        checkStatus();
        return doFetcher(executor);
    }

    public FetcherExecutor setParallel(boolean parallel) {
        this.parallel = parallel;
        return this;
    }

    public FetcherExecutor doFetcher(Fetcher fetcher) {
        checkStatus();

        Collection<String> status = currentRst.get(stageId.intValue());
        int currentId = stageId.addAndGet(1);

        ConcurrentLinkedQueue<String> allRst = new ConcurrentLinkedQueue<String>();
        System.out.println("status size:" + status.size());
        ArrayList<Callable<Integer>> callers = new ArrayList<Callable<Integer>>();
        if (this.parallel) {
            for (String s : status) {
                //allRst负责收集所有子线程的结果
                FetcherRunner runner = new FetcherRunner(fetcher, s, allRst);
                callers.add(runner);
            }

            try {
                pool.invokeAll(callers);
                System.out.println("Threads execution done........");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            for (String s : status) {
                List<String> tmpRst = fetcher.fetch(s);
                if (tmpRst != null && tmpRst.size() > 0)
                    allRst.addAll(tmpRst);
            }
        }

        this.currentRst.put(currentId, allRst);

        //每次清理上一阶段的内存，让JVM自动GC
        if (this.currentRst.get(currentId - 1) != null)
            this.currentRst.remove(currentId - 1);
        return this;
    }

    public void close() throws InterruptedException {
        System.out.println("waiting for executor to shutdown in " + WAIT_TIME + " seconds...");
        this.pool.awaitTermination(WAIT_TIME, TimeUnit.SECONDS);
        System.out.println("executor closed...");
    }
}
