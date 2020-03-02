package util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author sugan
 * @since 2015-12-21.
 */
public class JdbcUtil {

    public static Map<String, String> fetchMap(ResultSet rs) throws SQLException {
        return fetchMap(rs, true);
    }

    public static Map<String, String> fetchMap(ResultSet rs, boolean ignoreNullable) throws SQLException {
        int count = rs.getMetaData().getColumnCount();
        int i = 0;
        HashMap<String, String> ret = new HashMap<>();
        while (i++ < count) {
            String columnLabel = rs.getMetaData().getColumnName(i);
            String val = rs.getString(columnLabel);
            if (ignoreNullable) {
                if (null != val) ret.put(columnLabel, val);
            } else {
                ret.put(columnLabel, val);
            }
        }
        return ret;
    }
}
