package fetcher.sj.qq.com;

import com.google.gson.Gson;
import fetcher.Fetcher;
import util.HttpUtil;

import java.util.LinkedList;
import java.util.List;

/**
 * @author sugan
 * @since 2015-05-26.
 */

public class ContentFetcher implements Fetcher {

    private Gson GSON = new Gson();

    @Override
    public List<String> fetch(String url) {
        System.out.println("Fetching url:" + url + ",thread:" + Thread.currentThread().getId());

        List<String> ret = new LinkedList<String>();
        try {
            String docStr = HttpUtil.getDocumentString(url);
            JsonRstObj rst = GSON.fromJson(docStr, JsonRstObj.class);
            if (rst.obj != null && rst.obj.size() > 0) {
                for (AppObj obj : rst.obj) {
                    ret.add(obj.toCsvString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

}

class JsonRstObj {
    public List<AppObj> obj;
}

class AppObj {
    public String appName;
    public String categoryId;
    public String categoryName;
    public Double averageRating;
    public Long appDownCount;

    public String toCsvString() {
        return categoryName +
                "," + categoryId +
                "," + appName +
                "," + averageRating +
                "," + appDownCount;
    }
}
