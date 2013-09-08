package x.mvmn.patienceajdbc.mysql;

import java.sql.DriverManager;
import java.sql.SQLException;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mysql.jdbc.Driver;

public class MySqlPooledDataSourceFactory {

	public MySqlPooledDataSourceFactory() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		Driver mySqlDriver = (Driver) Class.forName("com.mysql.jdbc.Driver").newInstance();
		DriverManager.registerDriver(mySqlDriver);
	}

	public ComboPooledDataSource createPooledDataSource(String host, int port, String login, String password, String dbName, boolean profileSql) {
		ComboPooledDataSource dataSource = new ComboPooledDataSource();
		dataSource.setUser(login);
		dataSource.setPassword(password);
		{
			StringBuilder jdbcUrl = new StringBuilder("jdbc:mysql://");
			jdbcUrl.append(host).append(":").append(String.valueOf(port)).append("/").append(dbName).append("?useUnicode=true&characterEncoding=utf8");
			if (profileSql) {
				jdbcUrl.append("&profileSQL=true");
			}
			dataSource.setJdbcUrl(jdbcUrl.toString());
		}
		return dataSource;
	}
}
