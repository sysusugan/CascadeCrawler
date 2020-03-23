package util;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.apache.log4j.Logger;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author zebin
 * @since 2016-07-22.
 */
public class HtmlUnitUtil {

    public static final Logger LOG = Logger.getLogger(HtmlUnitUtil.class);

    /**
     * @param url     请求URL
     * @param timeout 超时时间,单位毫秒
     * @return <html,url>
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws TimeoutException
     */
    public static Pair<String, String> visit(final String url, final long timeout) throws InterruptedException {
        final Pair<String, String> pair = new Pair<>(null, null);
        try (final WebClient client = new WebClient(BrowserVersion.CHROME)) {
//        client.setTimeout((int) timeout);
            client.setJavaScriptTimeout(timeout);
//        client.setCssEnabled(false);
//        client.setThrowExceptionOnFailingStatusCode(false);
            HtmlUnitTask htmlUnitTask = new HtmlUnitTask(client, url, pair);
            Thread t = new Thread(htmlUnitTask);
            t.run();

            int maxWaitSec = 300 * 1000;//300 sec
            long st = System.currentTimeMillis();
            while (pair.first == null && pair.second == null && t.isAlive()) {
                LOG.info("sleep 10 sec for htmlunit thread...");
                TimeUnit.SECONDS.sleep(10);

                if (System.currentTimeMillis() - st > maxWaitSec) {
                    LOG.info("htmlunit thread reach timeout: 300 sec, exit...");
                    client.close();
                    t.interrupt();
                    break;
                }
            }
            return pair;
        } catch (Throwable e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }
}

class HtmlUnitTask implements Runnable {
    public static final Logger LOG = Logger.getLogger(HtmlUnitTask.class);

    WebClient client;
    private String url = null;
    Pair<String, String> pair = new Pair<>(null, null);

    public HtmlUnitTask(WebClient client, String url, Pair<String, String> pair) {
        this.client = client;
        this.url = url;
        this.pair = pair;
    }

    @Override
    public void run() {
        HtmlPage page = null;
        try {
            page = client.getPage(url);
            String html = page.asXml();
            String pageUrl = page.getUrl().toString();
            client.close();
            pair.first = html;
            pair.second = pageUrl;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }
}
