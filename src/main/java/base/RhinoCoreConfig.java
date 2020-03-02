package base;


import org.apache.hadoop.conf.Configuration;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * @author sugan
 * @since 2015-11-10.
 */
public class RhinoCoreConfig extends Configuration {
    protected static ThreadLocal<RhinoCoreConfig> config = new ThreadLocal<>();
    private static Logger LOG = Logger.getLogger(RhinoCoreConfig.class);
    protected Map<String, String> importantProperties;

    protected RhinoCoreConfig() {
        try {
            addResource(RhinoConst.FILE_PROXY_CONFIG);
            addResource(RhinoConst.FILE_HBASE_CONFIG);
            importantProperties = new HashMap<>();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            System.out.println("fail loadDefaultConfigs, exit program");
            System.exit(1);
        }
    }

    @Override
    public String get(String name) {
        if (importantProperties.containsKey(name)) {
            return importantProperties.get(name);
        }
        return super.get(name);
    }

    public Map<String, String> getImportantProperties() {
        return this.importantProperties;
    }

    public void setImportantProperty(String name, String value) {
        this.importantProperties.put(name, value);
    }

    public static RhinoCoreConfig getInstance() {
        if (null == config.get()) {
            config.set(new RhinoCoreConfig());
        }
        return config.get();
    }

}
