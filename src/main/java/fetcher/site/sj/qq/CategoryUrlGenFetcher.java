package fetcher.site.sj.qq;

import fetcher.Fetcher;

import java.util.LinkedList;
import java.util.List;

/**
 * generate catrgory
 *
 * @author sugan
 * @since 2015-06-26.
 */
public class CategoryUrlGenFetcher implements Fetcher {

    private int pageSize = 50;
    private int startRow = 0;

    @Override
    public List<String> fetch(String cateId) {
        LinkedList<String> ret = new LinkedList<String>();
        ret.add(String.format("http://sj.qq.com/myapp/cate/appList.htm?orgame=1&categoryId=%s&pageSize=%s&pageContext=%s"
                , cateId, pageSize, startRow));

        return ret;
    }
}
