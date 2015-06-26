import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import util.HttpUtil;

import java.io.IOException;

/**
 * @author sugan
 * @since 2015-05-25.
 */
public class Test {

    @org.junit.Test
    public void test() throws IOException {
        Document ret = HttpUtil.getDocument("http://detail.zol.com.cn/cell_phone_index/subcate57_613_list_2.html");

        String l = ret.location();
        System.out.println(l);
        Elements arr = ret.select(".pro-intro");

    }
}
