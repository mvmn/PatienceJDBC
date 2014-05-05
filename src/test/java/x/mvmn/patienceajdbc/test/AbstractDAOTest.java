package x.mvmn.patienceajdbc.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Properties;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import x.mvmn.lang.tuple.TupleOfTwo;
import x.mvmn.patienceajdbc.dao.DataAccessObject;

public abstract class AbstractDAOTest<DT extends DataAccessObject> {

	protected ClassPathXmlApplicationContext daoContext;

	protected static GenericApplicationContext PARENT_MOCK_CONTEXT;
	protected static final Object MOCK_DB_CONNECTION_INPUT = new Object() {

		final Properties props;
		{
			props = new Properties();
			try {
				props.load(AbstractDAOTest.class.getResourceAsStream("/db.properties"));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		@SuppressWarnings("unused")
		public String getDbHost() {
			return props.getProperty("host");
		}

		@SuppressWarnings("unused")
		public String getDbName() {
			return props.getProperty("db");
		}

		@SuppressWarnings("unused")
		public String getDbUser() {
			return props.getProperty("user");
		}

		@SuppressWarnings("unused")
		public char[] getDbPassword() {
			return props.getProperty("password").toCharArray();
		}

		@SuppressWarnings("unused")
		public boolean getProfileSql() {
			return false;
		}
	};

	protected static String SQL_SCRIPT_RECREATE_DB;

	@BeforeClass
	public static void beforeClass() throws Exception {
		{
			GenericApplicationContext parentContext = new GenericApplicationContext();
			parentContext.getBeanFactory().registerSingleton("dbConnectionDialog", MOCK_DB_CONNECTION_INPUT);
			parentContext.refresh();
			PARENT_MOCK_CONTEXT = parentContext;
		}
		{
			BufferedReader buffReader = new BufferedReader(new InputStreamReader(AbstractDAOTest.class.getResourceAsStream("/createschema.sql"), "UTF-8"));
			StringBuilder createSchemaSqlFileContentBuilder = new StringBuilder();
			String line;
			do {
				line = buffReader.readLine();
				if (line != null) {
					createSchemaSqlFileContentBuilder.append(line).append("\n");
				}
			} while (line != null);

			SQL_SCRIPT_RECREATE_DB = createSchemaSqlFileContentBuilder.toString();
		}
	}

	public static void resetDb(ApplicationContext dbContext) throws Exception {
		DataSource dataSource = (DataSource) dbContext.getBean("dbDataSource");
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			Statement statement = connection.createStatement();
			for (String statemetText : SQL_SCRIPT_RECREATE_DB.toString().split(";")) {
				if (statemetText.trim().length() > 0) {
					statement.execute(statemetText + ";");
				}
			}
			statement.close();
		} finally {
			try {
				connection.close();
			} catch (Exception e) {
			}
		}
	}

	@Before
	public void before() throws Exception {
		ApplicationContext dbContext = new ClassPathXmlApplicationContext(new String[] { "springioc/db.xml" }, PARENT_MOCK_CONTEXT);
		resetDb(dbContext);
		daoContext = new ClassPathXmlApplicationContext(new String[] { "springioc/dao.xml" }, dbContext);
	}

	@After
	public void after() {
		daoContext.destroy();
		daoContext = null;
	}

	protected <T> T getBean(TupleOfTwo<String, Class<T>> beanDefTuple) {
		return daoContext.getBean(beanDefTuple.getFirst(), beanDefTuple.getSecond());
	}

	protected DT getDAO() {
		TupleOfTwo<String, Class<DT>> daoBeanDefTuple = getDaoBeanNameAndClass();
		return getBean(daoBeanDefTuple);
	}

	protected abstract TupleOfTwo<String, Class<DT>> getDaoBeanNameAndClass();
}
