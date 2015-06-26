package main;

import fetcher.Fetcher;
import fetcher.FetcherExecutor;
import fetcher.site.apk.hiapk.CategoryUrlGenFetcher;
import fetcher.site.apk.hiapk.ContentFetcher;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author sugan
 * @since 2015-05-26.
 */
public class HiApkCrawlTest {

    public static void main(String[] args) throws IOException {

        System.out.println("THread id " + Thread.currentThread().getId());

        final AtomicInteger i = new AtomicInteger(0);
        long start = System.currentTimeMillis();
        final String outputFileName = "/tmp/hiapk.txt";
        System.out.println(outputFileName);
        final FileWriter fw = new FileWriter(outputFileName);

        String[] arr = new String[]{
                "MediaAndVideo", "DailyLife", "Social", "Finance", "Tools", "TravelAndLocal",
                "Communication", "Shopping", "Reading", "Education", "NewsAndMagazines",
                "HealthAndFitness", "AntiVirus", "Browser", "Productivity", "Personalization",
                "Input", "Photography"
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
        System.exit(0);
    }
}
