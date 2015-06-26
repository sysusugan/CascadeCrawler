package fetcher.site.zhushou360;

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

    @Override
    public List<String> fetch(String cateId) {
        LinkedList<String> ret = new LinkedList<String>();
        int startPage = 0;
        while (startPage++ < 2) {
            ret.add(String.format("http://zhushou.360.cn/list/index/cid/%s/?page=%s", cateId, startPage));
        }

        return ret;
    }
}
