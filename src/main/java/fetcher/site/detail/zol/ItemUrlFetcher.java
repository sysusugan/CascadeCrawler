package fetcher.site.detail.zol;

import fetcher.Fetcher;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import util.HttpResult;
import util.HttpUtil;

import java.util.LinkedList;
import java.util.List;

/**
 * @author sugan
 * @since 2015-05-25.
 */
public class ItemUrlFetcher implements Fetcher {
    private static int count = 0;

    @Override
    public List<String> fetch(String uri) {

        count++;
        LinkedList<String> retList = new LinkedList<String>();
        try {
            //page:
            int i = uri.lastIndexOf("_");
            int e = uri.lastIndexOf(".html");
            String page = uri.substring(i + 1, e);
            int p = Integer.parseInt(page);
            String template = uri.substring(0, i + 1) + "%s" + uri.substring(e);
//            System.out.println(template);
//            System.out.println(p);

            Elements noResult = null;
            do {
                System.out.println("page:" + p);
                HttpResult ret = null;
                uri = String.format(template, p);
                try {
                    ret = HttpUtil.getMethodResult(uri);
                } catch (Exception e1) {
                    System.out.println("url:" + uri + ", error in thread:" + Thread.currentThread().getId());
                    e1.printStackTrace();
                }
                String host = ret.getMethod().getURI().getScheme() + "://" + ret.getMethod().getURI().getHost();
                Elements arr = ret.getDocument().select(".pro-intro h3>a");

                noResult = ret.getDocument().select(".no-result");
                System.out.println("原始输入第" + (count) + "行:" + "\t" + uri + " 个数： " + arr.size());

                for (Element element : arr) {
//                    System.out.println(host + element.attr("href"));
                    retList.add(host + element.attr("href"));
                }
                p++;
            }
            while (noResult != null && noResult.isEmpty() && p < 50);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return retList;
    }
}
