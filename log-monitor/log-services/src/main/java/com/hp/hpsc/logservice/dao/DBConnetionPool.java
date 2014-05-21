package com.hp.hpsc.logservice.dao;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import com.hp.hpsc.logservice.utils.LmConsts;
import com.hp.it.spf.xa.properties.PropertyResourceBundleManager;

public class DBConnetionPool {
    private static LinkedList<Connection> m_notUsedConnection = new LinkedList<Connection>();
    private static HashSet<Connection> m_usedUsedConnection = new HashSet<Connection>();
    private static String m_user = PropertyResourceBundleManager.getString(LmConsts.CONFIG_FILE_NAME, LmConsts.DB_USER);
    private static String m_password = PropertyResourceBundleManager.getString(LmConsts.CONFIG_FILE_NAME, LmConsts.DB_PASSWORD);
    private static String url = PropertyResourceBundleManager.getString(LmConsts.CONFIG_FILE_NAME, LmConsts.DB_URL);
    private static String m_url = "jdbc:oracle:thin:" + m_user + "/" + m_password + "@"+ url;
    private static int m_maxConnect = Integer.valueOf(PropertyResourceBundleManager.getString(LmConsts.CONFIG_FILE_NAME, LmConsts.DB_MAX_CONNECTION));
    static final boolean DEBUG = false;
    static private long m_lastClearClosedConnection = System.currentTimeMillis();
    public static long CHECK_CLOSED_CONNECTION_TIME = Long.valueOf(PropertyResourceBundleManager.getString(LmConsts.CONFIG_FILE_NAME, LmConsts.DB_CHECK_CLO_CON_TIME));

    static {
        try {
            initDriver();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public DBConnetionPool(String url, String user, String password) {
        m_url = url;
        m_user = user;
        m_password = password;
    }

    private static void initDriver() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        Driver driver = null;

        driver = (Driver)Class.forName("oracle.jdbc.OracleDriver").newInstance();
        installDriver(driver);

    }

    public static void installDriver(Driver driver) {
        try {
            DriverManager.registerDriver(driver);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static synchronized Connection getConnection() {
        clearClosedConnection();

        while (m_notUsedConnection.size() > 0) {
            try {
                Connection con = (Connection)m_notUsedConnection.removeFirst();

                if (con.isClosed()) {
                    continue;
                }

                m_usedUsedConnection.add(con);
                return con;
            } catch (SQLException e) {
            }
        }
        int newCount = getIncreasingConnectionCount();
        LinkedList<Connection> list = new LinkedList<Connection>();
        Connection con = null;

        for (int i = 0; i < newCount; i++) {
            con = getNewConnection();
            if (con != null) {
                list.add(con);
            }
        }

        if (list.size() == 0)
            return null;

        con = (Connection)list.removeFirst();
        m_usedUsedConnection.add(con);
        m_notUsedConnection.addAll(list);
        list.clear();

        return con;
    }

    public static Connection getNewConnection() {
        try {
            Connection con = DriverManager.getConnection(m_url, m_user, m_password);
            return con;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static synchronized void returnConnectionToPool(Connection con) {
        boolean exist = m_usedUsedConnection.remove(con);
        if (exist) {
            m_notUsedConnection.addLast(con);
        }
    }

    public static int close() {
        int count = 0;

        Iterator<Connection> iterator = m_notUsedConnection.iterator();
        while (iterator.hasNext()) {
            try {
                ((Connection)iterator.next()).close();
                count++;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        m_notUsedConnection.clear();

        iterator = m_usedUsedConnection.iterator();
        while (iterator.hasNext()) {
            try {
                ((Connection)iterator.next()).close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        m_usedUsedConnection.clear();

        return count;
    }

    private static void clearClosedConnection() {
        long time = System.currentTimeMillis();

        if (time < m_lastClearClosedConnection) {
            time = m_lastClearClosedConnection;
            return;
        }

        if (time - m_lastClearClosedConnection < CHECK_CLOSED_CONNECTION_TIME) {
            return;
        }

        m_lastClearClosedConnection = time;

        Iterator<Connection> iterator = m_notUsedConnection.iterator();
        while (iterator.hasNext()) {
            Connection con = (Connection)iterator.next();

            try {
                if (con.isClosed()) {
                    iterator.remove();
                }
            } catch (SQLException e) {
                iterator.remove();

            }
        }

        int decrease = getDecreasingConnectionCount();

        while (decrease > 0 && m_notUsedConnection.size() > 0) {
            Connection con = (Connection)m_notUsedConnection.removeFirst();

            try {
                con.close();
            } catch (SQLException e) {

            }
        }
    }

    public static int getIncreasingConnectionCount() {
        int count = 1;
        count = getConnectionCount() / 4;

        if (count < 1)
            count = 1;

        return count;
    }

    public static int getDecreasingConnectionCount() {
        int count = 0;

        if (getConnectionCount() > m_maxConnect) {
            count = getConnectionCount() - m_maxConnect;
        }

        return count;
    }

    public static synchronized int getNotUsedConnectionCount() {
        return m_notUsedConnection.size();
    }

    public static synchronized int getUsedConnectionCount() {
        return m_usedUsedConnection.size();
    }

    public static synchronized int getConnectionCount() {
        return m_notUsedConnection.size() + m_usedUsedConnection.size();
    }

}
