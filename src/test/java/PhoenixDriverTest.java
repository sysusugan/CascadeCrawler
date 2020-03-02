import base.RhinoCoreConfig;
import org.apache.hadoop.hbase.HConstants;
import org.junit.Test;
import util.JdbcUtil;
import util.phoenix.PhoenixDriver;
import util.phoenix.PhoenixDriverFactory;

import java.io.IOException;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * @author sugan
 * @since 2015-12-22
 */
public class PhoenixDriverTest implements Serializable {


    /**
     * 测试Phoenix  JDBCDriver
     *
     * @throws SQLException
     * @throws IOException
     */
    @Test
    public void test() throws SQLException, IOException {
        RhinoCoreConfig conf = RhinoCoreConfig.getInstance();
        conf.set("phoenix.query.timeoutMs", "3600000");
        conf.set(HConstants.HBASE_RPC_TIMEOUT_KEY, "3600000");
        conf.set(HConstants.HBASE_CLIENT_SCANNER_TIMEOUT_PERIOD, "3600000");
        System.out.println("test0");

        //直接连接zk地址（host:port:path）的方式
        PhoenixDriver driver1 = PhoenixDriverFactory.getInstance("dev4", 2181, "/hbase-unsecure", null);

        //传入conf文件的方式， conf对象里面必须加载了hdfs-site.xml 和hbase-site.xml
//        PhoenixDriver driver1 = PhoenixDriverFactory.newInstance(conf);//需要有hdfs-site hbase-site

//        int[] ret = driver1.batchExecute("upsert into   TEST_UPSERT (\"pk\",\"keyword\") values (?, ?)", new Object[]{"testKey",  "x"});
//        int[] ret = driver1.execute("upsert into   t1 (\"key\",t,a) values (?, ?,?)", new Object[]{"testKey", "xx", "x"});
//        System.out.println(Arrays.asList(ret));
        String sql = "select * from DS_BANYAN_CAR_COMMENT_V3 limit 11 ";
        System.out.println("test sql: \t" + sql);
        ResultSet rst = driver1.query(sql);

        System.out.println("test2");
        while (rst.next()) {
            Map<String, String> map = JdbcUtil.fetchMap(rst);
            System.out.println(map);
        }
    }

}