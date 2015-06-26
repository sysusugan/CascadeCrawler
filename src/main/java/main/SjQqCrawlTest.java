package main;

import fetcher.Fetcher;
import fetcher.FetcherExecutor;
import fetcher.site.sj.qq.CategoryUrlGenFetcher;
import fetcher.site.sj.qq.ContentFetcher;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author sugan
 * @since 2015-05-26.
 */
public class SjQqCrawlTest {

    public static void main(String[] args) throws IOException, InterruptedException {

        System.out.println("THread id " + Thread.currentThread().getId());

        final AtomicInteger i = new AtomicInteger(0);
        long start = System.currentTimeMillis();
        final String fileName = "/tmp/sj.qq.txt";
        System.out.println(fileName);
        final FileWriter fw = new FileWriter(fileName);

        String[] arr = new String[]{
                "103", "101", "122", "102", "112", "106", "104", "110", "115", "119", "111", "107", "118", "108", "100", "114", "117", "109", "105", "113", "116"
        };

        FetcherExecutor executor = new FetcherExecutor(10);
        executor.doFetcher(Arrays.asList(arr), new CategoryUrlGenFetcher())
                .doFetcher(new ContentFetcher())
                .setParallel(false)// 默认是并发，下一步不要并发运行（不使用线程池），因为要使用写文件，会有多线程不安全的问题
                .doFetcher(new Fetcher() {
                    @Override
                    public List<String> fetch(String input) {
                        int v = i.addAndGet(1);
                        System.out.println("Thread ID:" + Thread.currentThread().getId() + "\t  Line" + v + ": " + input);
                        synchronized (fw) {
                            try {
                                fw.write(input + "\n");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        return null;
                    }
                });


        fw.close();
        System.out.println("All done!");
        long end = System.currentTimeMillis();
        System.out.println("take:" + (end - start) / 1000 + " sec");
        System.out.println("total lines:" + i.get() + " line");

        executor.close();
    }
}
