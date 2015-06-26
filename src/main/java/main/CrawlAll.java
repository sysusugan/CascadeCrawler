package main;

import java.io.IOException;

/**
 * @author sugan
 * @since 2015-06-26.
 */
public class CrawlAll {
    public static void main(String[] args) throws IOException, InterruptedException {
        HiApkCrawlTest.main(null);
        SjQqCrawlTest.main(null);
        Zhushou360CrawlTest.main(null);

        System.exit(0);
    }
}
