package x.mvmn.patienceajdbc.mysql;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;

import javax.sql.DataSource;

import com.mysql.jdbc.Driver;

public class MySqlHelper {

	public static boolean testConnection(String dbUrl, String user, String pass) throws Exception {
		try {
			Driver mySqlDriver = (Driver) Class.forName("com.mysql.jdbc.Driver").newInstance();
			DriverManager.registerDriver(mySqlDriver);
		} catch (Exception e) {
		}

		Connection conn = DriverManager.getConnection(dbUrl, user, pass);
		conn.createStatement().execute("show tables");

		return true;
	}

	public static String createMySqlDbUrl(String dbHost, String dbName) throws UnsupportedEncodingException {
		StringBuilder dbUrl = new StringBuilder("jdbc:mysql://");
		dbUrl.append(URLEncoder.encode(dbHost, "UTF-8").replaceAll("%3A", ":")).append("/").append(URLEncoder.encode(dbName, "UTF-8"));
		return dbUrl.toString();
	}

	public static DataSource createDataSource(String host, int port, String dbName, String user, String pass, boolean profileSql) throws Exception {
		// return
		// DataSources.pooledDataSource(DataSources.unpooledDataSource(dbUrl,
		// user, pass));
		return new MySqlPooledDataSourceFactory().createPooledDataSource(host, port, user, pass, dbName, profileSql);
	}

}
