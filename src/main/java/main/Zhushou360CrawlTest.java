package main;

import fetcher.Fetcher;
import fetcher.FetcherExecutor;
import fetcher.site.zhushou360.CategoryUrlGenFetcher;
import fetcher.site.zhushou360.ContentFetcher;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author sugan
 * @since 2015-05-26.
 */
public class Zhushou360CrawlTest {

    public static void main(String[] args) throws IOException, InterruptedException {

        System.out.println("THread id " + Thread.currentThread().getId());

        final AtomicInteger i = new AtomicInteger(0);
        long start = System.currentTimeMillis();
        final String outputFileName = "/tmp/zhushou.360.txt";
        System.out.println(outputFileName);
        final FileWriter fw = new FileWriter(outputFileName);

        String[] arr = new String[]{
                "11","12","14","15","16","18","17","102228","102230","102231","102232","102139","102233"
        };

        FetcherExecutor executor = new FetcherExecutor(10);
        executor.doFetcher(Arrays.asList(arr), new CategoryUrlGenFetcher())
                .doFetcher(new ContentFetcher())
                .setParallel(false)
                .doFetcher(new Fetcher() {
                    private Set<String> uniqSet = new HashSet<String>();

                    @Override
                    public List<String> fetch(String input) {
                        List<String> ret = new LinkedList<String>();
                        if (!uniqSet.contains(input)) {
                            uniqSet.add(input);
                            ret.add(input);
                        }
                        return ret;
                    }
                })
                .doFetcher(new Fetcher() {
                    @Override
                    public List<String> fetch(String input) {
                        int v = i.addAndGet(1);
                        System.out.println("Thread ID:" + Thread.currentThread().getId() + "\t  Line" + v + ": " + input);
                        try {
                            fw.write(input + "\n");
                        } catch (IOException e) {
                            e.printStackTrace();
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
