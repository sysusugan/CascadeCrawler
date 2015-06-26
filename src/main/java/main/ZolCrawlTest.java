package main;

import com.yeezhao.commons.util.AdvFile;
import com.yeezhao.commons.util.ILineParser;
import fetcher.Fetcher;
import fetcher.FetcherExecutor;
import fetcher.site.detail.zol.ContentFetcher;
import fetcher.site.detail.zol.ItemUrlFetcher;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author sugan
 * @since 2015-05-26.
 */
public class ZolCrawlTest {

    public static void main(String[] args) throws IOException {
        System.out.println("==== using parallel mode:" + false);
        System.out.println("THread id " + Thread.currentThread().getId());
        final Set<String> set = new HashSet<String>();
        InputStream in = FetcherExecutor.class.getResourceAsStream("/mobile_brand_url.txt");
        AdvFile.loadFileInDelimitLine(in, new ILineParser() {
            @Override
            public void parseLine(String s) {
                set.add(s);
            }
        }, "UTF-8");

        final AtomicInteger i = new AtomicInteger(0);
        long start = System.currentTimeMillis();
        final String fileName = "/tmp/zol.txt";
        System.out.println(fileName);
        final FileWriter fw = new FileWriter(fileName);

        FetcherExecutor executor = new FetcherExecutor(10);
        executor.doFetcher(set, new ItemUrlFetcher())
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
        System.exit(0);
    }
}
