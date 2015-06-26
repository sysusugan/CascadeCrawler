package fetcher.site.zhushou360;

import fetcher.Fetcher;
import fetcher.entity.AppObj;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import util.HttpUtil;

import java.util.LinkedList;
import java.util.List;

/**
 * @author sugan
 * @since 2015-05-26.
 */

public class ContentFetcher implements Fetcher {

    @Override
    public List<String> fetch(String url) {
        System.out.println("Fetching url:" + url + ",thread:" + Thread.currentThread().getId());

        List<String> ret = new LinkedList<String>();
        try {
            Document doc = HttpUtil.getDocument(url);

            String cate = doc.select(".aurr").text();

            Elements listContent = doc.select("#iconList li");
            for (Element item : listContent) {
                try {

                    String app = item.select("h3 a").text();
                    if (app != null) {
                        AppObj a = new AppObj();
                        a.setAppName(app);
                        a.setCategoryName(cate);
                        ret.add((a.toCsvString()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

}
