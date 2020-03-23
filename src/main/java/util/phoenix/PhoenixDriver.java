package util.phoenix;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.StringUtil;

import java.io.Serializable;
import java.sql.*;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * @author sugan
 * @since 2015-12-22
 */
public class PhoenixDriver implements Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(PhoenixDriver.class);
    private static String driverName = "org.apache.phoenix.jdbc.PhoenixDriver";
    private String connUri;
    private Statement stmt;
    private Connection con;
    private Properties properties = null;

    public PhoenixDriver(String zkQuorum, int zkPort, String hbaseZkPath)
            throws SQLException, ClassNotFoundException {
        this(zkQuorum, zkPort, hbaseZkPath, null);
    }

    public PhoenixDriver(String zkQuorum, int zkPort, String hbaseZkPath, Properties properties)
            throws SQLException, ClassNotFoundException {
        Class.forName(driverName);
        // path:  /hbase-unsecure
        // zk: localhost:2181
        this.connUri = String.format("jdbc:phoenix:%s:%s:%s", zkQuorum, zkPort, hbaseZkPath);
        LOG.info("connection uri:" + connUri);
        if (properties != null) {
            this.properties = properties;
            con = DriverManager.getConnection(connUri, properties);
            LOG.info("phoenix properties:" + properties);
        } else
            con = DriverManager.getConnection(connUri);
        con.setAutoCommit(true);
        stmt = con.createStatement();
    }

    public void reconnect() throws SQLException {
        if (con == null || con.isClosed()) {
            con = DriverManager.getConnection(connUri);
            stmt = con.createStatement();
        }
    }

    public boolean execute(String sql) throws SQLException {
        return stmt.execute(sql);
    }

    public ResultSet query(String sql) throws SQLException {
        return stmt.executeQuery(sql);
    }

    // 删除单例的function同步锁, by chenzhao at 2017.03.02
    public int[] batchExecute(List<String> sqls) throws SQLException {
//        LOG.info("------------batch add-------------------");
//        LOG.info("flush size:--------------------" + sqls.size());
        int[] results = null;
        try {
            for (String sql : sqls) {
                if (!StringUtil.isNullOrEmpty(sql)) {
                    stmt.addBatch(sql);
                }
            }
            results = stmt.executeBatch();
        } catch (SQLException e) {
            LOG.error("phoenix batch execute fail...", e);
            stmt.clearBatch();
        }
        return results;
    }

    public void close() throws SQLException {
        if (stmt != null && !stmt.isClosed()) {
            stmt.close();
        }
        if (con != null && !con.isClosed()) {
            con.close();
        }
    }

    public int[] batchExecute(String sql, Object[] params) throws SQLException {
        return batchExecute(sql, Collections.singleton(params));
    }


    private Connection getConnection() throws SQLException {
        Connection con;
        if (properties != null) {
            con = DriverManager.getConnection(connUri, properties);

            LOG.info("phoenix properties:" + properties);
        } else
            con = DriverManager.getConnection(connUri);
        con.setAutoCommit(false);
        return con;
    }

    public <T extends Object> int[] batchExecute(String sql, Collection<T[]> params) throws SQLException {
        PreparedStatement stmt = null;
        int[] rows = null;
        Connection con = null;
        try {
            con = getConnection();
            stmt = con.prepareStatement(sql);
            for (Object[] param : params) {
                fillStatement(stmt, param);
                stmt.addBatch();
                stmt.clearParameters();
            }
            rows = stmt.executeBatch();
            con.commit();
        } catch (SQLException e) {
            LOG.error(e.getMessage(), e);
        } finally {
            if (stmt != null)
                stmt.close();
            if (con != null)
                con.close();
        }
        return rows;
    }

    public void fillStatement(PreparedStatement stmt, Object... params) throws SQLException {
        ParameterMetaData pmd = null;
        pmd = stmt.getParameterMetaData();
        int stmtCount = pmd.getParameterCount();
        int paramsCount = params == null ? 0 : params.length;

        if (stmtCount != paramsCount) {
            throw new SQLException("Wrong number of parameters: expected "
                    + stmtCount + ", was given " + paramsCount);
        }

        if (params == null) {
            return;
        }

        for (int i = 0; i < params.length; i++) {
            if (params[i] != null) {
                stmt.setObject(i + 1, params[i]);
            } else {
                int sqlType = Types.VARCHAR;
                try {
                    sqlType = pmd.getParameterType(i + 1);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                stmt.setNull(i + 1, sqlType);
            }
        }
    }
}