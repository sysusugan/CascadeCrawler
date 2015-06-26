package fetcher.zol.com;

import com.yeezhao.commons.util.StringUtil;
import fetcher.Fetcher;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import util.HttpUtil;

import java.util.*;

/**
 * @author sugan
 * @since 2015-05-26.
 */

public class ContentFetcher implements Fetcher {

    @Override
    public List<String> fetch(String url) {
        System.out.println("Fetching url:" + url + ",thread:" + Thread.currentThread().getId());
        String retStr = null;
        LinkedList<String> tagList = null;
        HashMap<String, String> scoreMap = null;
        String good = "", mid = "", bad = "", totalScore = "";
        String dpUrl = "";
        try {
            Document doc = HttpUtil.getDocument(url);
            String name = doc.select(".page-title h1").text();
            String price = doc.select(".product-price-info .price-type").text();

            Elements anchor = doc.select("#tagNav .nav li:eq(4) a");
            String href = anchor.attr("href");

            if (!StringUtil.isNullOrEmpty(href) && StringUtil.isNumeric(price)) {
                dpUrl = "http://detail.zol.com.cn" + href;

                Document dpDoc = HttpUtil.getDocument(dpUrl);
                totalScore = dpDoc.select(".total-score strong").text();

                Elements commentLevel = dpDoc.select(".comments-level li em");
                try {
                    good = commentLevel.get(0).text();
                    mid = commentLevel.get(1).text();
                    bad = commentLevel.get(2).text();
                } catch (Exception e) {
                    System.out.println("[FAIL] url:" + url + " fetch failed, no comment level...skiped");
                }

                scoreMap = new HashMap<String, String>();
                Elements scoreItem = dpDoc.select(".score-item li");
                for (Element ele : scoreItem) {
                    String title = ele.select("strong").text();
                    String val = ele.select("span.text").text();
                    if (!StringUtil.isNullOrEmpty(title) && !StringUtil.isNullOrEmpty(val)) {
                        scoreMap.put(title, val);
                    }
                }

                tagList = new LinkedList<String>();
                Elements goodWords = dpDoc.select(".good-words li a ");
                Elements badWords = dpDoc.select(".bad-words li a ");
                for (Element word : goodWords) {
                    if (!StringUtil.isNullOrEmpty(word.text()))
                        tagList.add(word.text());
                }
                for (Element word : badWords) {
                    if (!StringUtil.isNullOrEmpty(word.text()))
                        tagList.add(word.text());
                }
            }


            retStr = String.format("%s\t%s\t%s\t%s\t%s\t%s",
                    name,
                    price,
                    totalScore,
                    good,
                    mid,
                    bad);

            String scoreStr = "";
            if (scoreMap != null && scoreMap.keySet().size() == 6) {
                LinkedList<String> tmpList = new LinkedList<String>();
                tmpList.addAll(scoreMap.keySet());
                Collections.sort(tmpList);
                System.out.println(tmpList);
                for (String s : tmpList) {
                    scoreStr = scoreStr + String.format("\t%s", scoreMap.get(s));
                }

            }

            retStr = retStr + scoreStr + "\t" + (tagList == null ? "" : tagList.toString()) + "\t" + dpUrl;

        } catch (Exception e) {
            System.out.println(Thread.currentThread().getId() + "\t dian ping url:" + dpUrl);
            e.printStackTrace();
        }
        if (retStr != null) {
            ArrayList<String> retList = new ArrayList<String>();
            retList.add(retStr);
            return retList;
        }
        return null;
    }

}
