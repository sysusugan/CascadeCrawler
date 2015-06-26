package fetcher.site.apk.hiapk;

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
        while (startPage++ < 3) {
            ret.add(String.format("http://apk.hiapk.com/apps/%s?sort=5&pi=%s", cateId, startPage));
        }

        return ret;
    }
}
