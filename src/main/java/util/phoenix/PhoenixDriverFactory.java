package util.phoenix;

import base.RhinoConst;
import org.apache.hadoop.conf.Configuration;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by lijing on 2016/2/29.
 */
public class PhoenixDriverFactory implements Serializable {

    public static final Logger LOG = Logger.getLogger(PhoenixDriverFactory.class);
    // 使用ThreadLocal以实现每个线程独立的Driver实例, by chenzhao at 2017.02.20
    public static ThreadLocal<Map<String, PhoenixDriver>> phoenixDriverMapContainer =
            new ThreadLocal<Map<String, PhoenixDriver>>() {
                @Override
                protected Map<String, PhoenixDriver> initialValue() {
                    return new HashMap<String, PhoenixDriver>();
                }
            };

    private static Properties properties = new Properties();

    static {
        properties.put("phoenix.query.timeoutMs", "3000000");
        properties.put("hbase.rpc.timeout", "3000000");
        properties.put("phoenix.query.keepAliveMs", "3000000");
        properties.put("hbase.client.scanner.timeout.period", "3000000");
    }

    private PhoenixDriverFactory() {

    }

    /**
     * @param zkUriPath zk的地址和路径 eg:  localhost:2181:/hbase
     */
    public static PhoenixDriver getInstance(String zkUriPath) throws Exception {
        if (!zkUriPath.contains(":") || !zkUriPath.contains("/")) {
            throw new Exception("error zkUriPath,  eg: localhost:2181:/hbase ");
        }

        String[] segs = zkUriPath.split(":");
        if (segs.length == 3) {
            //默认参数
            return getInstance(segs[0].trim(), Integer.parseInt(segs[1].trim()), segs[2].trim(), properties);
        }

        throw new Exception("error zkUriPath,  eg: localhost:2181:/hbase ");
    }

    public static PhoenixDriver getInstance(Configuration conf) {
        return getInstance(conf.get(RhinoConst.ZOOKEEPER_LIST)
                , conf.getInt(RhinoConst.HBASE_ZOOKEEPER_PROPERTY_CLIENT_PORT, 2181), "/hbase-unsecure", null);
    }

    public static PhoenixDriver getInstaceWithProperties(Configuration conf, Properties properties) {
        return getInstance(conf.get(RhinoConst.ZOOKEEPER_LIST)
                , conf.getInt(RhinoConst.HBASE_ZOOKEEPER_PROPERTY_CLIENT_PORT, 2181), "/hbase-unsecure", properties);
    }

    public static PhoenixDriver getInstance(String zkList, int port, String zkPath, Properties properties) {
        String zkUriPath = String.format("%s:%s:%s", zkList, port, zkPath);
        if (zkList == null)
            LOG.error("error zkUriPath: " + zkUriPath);

        // 使用ThreadLocal以实现每个线程独立的Driver实例, by chenzhao at 2017.02.20
        Map<String, PhoenixDriver> phoenixDriverMap = phoenixDriverMapContainer.get();

        // 删除单例的class同步锁, by chenzhao at 2017.03.02
        if (phoenixDriverMap.get(zkUriPath) == null) {
            try {
                PhoenixDriver phoenixDriver = properties == null
                        ? new PhoenixDriver(zkList, port, zkPath) : new PhoenixDriver(zkList, port, zkPath, properties);
                phoenixDriverMap.put(zkUriPath, phoenixDriver);
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
                LOG.error("Phoenix driver init error", e);
            }
        }
        return phoenixDriverMap.get(zkUriPath);
    }


}
