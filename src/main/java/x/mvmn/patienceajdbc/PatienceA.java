package x.mvmn.patienceajdbc;

import java.util.Locale;

import javax.swing.JFrame;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import x.mvmn.patienceajdbc.gui.SwingHelper;
import x.mvmn.patienceajdbc.gui.l10n.LocaleChangeNotifier;
import x.mvmn.patienceajdbc.gui.startup.ConnectionPropertiesDialog;
import x.mvmn.patienceajdbc.mysql.MySqlHelper;

public class PatienceA {

	public static void handleConnectClick(ConnectionPropertiesDialog connectionPropertiesDialog) throws Exception {
		String dbHost = connectionPropertiesDialog.getDbHost();
		String dbName = connectionPropertiesDialog.getDbName();
		String dbUser = connectionPropertiesDialog.getDbUser();
		String dbPassword = new String(connectionPropertiesDialog.getDbPassword());

		String dbUrl = MySqlHelper.createMySqlDbUrl(dbHost, dbName);
		boolean testResult = MySqlHelper.testConnection(dbUrl.toString(), dbUser, dbPassword);
		if (!testResult) {
			// Will never happen in current implementation of
			// MySqlHelper.testConnection - exceptions will be thrown there
			// instead
			throw new Exception("Database connection failed");
		}
		connectionPropertiesDialog.setVisible(false);
		// connectionPropertiesDialog.dispose();

		LocaleChangeNotifier localeChangeNotifier = PARENT_SPRING_CONTEXT.getBean("localeChangeNotifier", LocaleChangeNotifier.class);
		localeChangeNotifier.setLocale(localeChangeNotifier.getLastSetLocale() != null ? localeChangeNotifier.getLastSetLocale() : Locale.US);

		// System.out.println("Starting with lang " +
		// localeChangeNotifier.getLastSetLocale());

		// DataSource dbDataSource =
		// MySqlHelper.createDataSource(dbUrl.toString(), dbUser, dbPassword);
		// PARENT_SPRING_CONTEXT.getBeanFactory().initializeBean(dbDataSource,
		// "dbDataSource");

		MAIN_SPRING_CONTEXT = new ClassPathXmlApplicationContext(new String[] { "springioc/main.xml" }, PARENT_SPRING_CONTEXT);

		JFrame patientsList = MAIN_SPRING_CONTEXT.getBean("patientsListWindow", JFrame.class);
		patientsList.setVisible(true);
	}

	private static final ClassPathXmlApplicationContext PARENT_SPRING_CONTEXT = new ClassPathXmlApplicationContext("springioc/startup.xml");
	private static ClassPathXmlApplicationContext MAIN_SPRING_CONTEXT;

	public static void main(String args[]) {
		ConnectionPropertiesDialog connectionPropertiesDialog = PARENT_SPRING_CONTEXT.getBean("dbConnectionDialog", ConnectionPropertiesDialog.class);
		connectionPropertiesDialog.pack();
		SwingHelper.moveToScreenCenter(connectionPropertiesDialog);
		connectionPropertiesDialog.setVisible(true);
	}

	public static ApplicationContext getApplicationContext() {
		return MAIN_SPRING_CONTEXT;
	}

	public static void shutdown() {
		MAIN_SPRING_CONTEXT.destroy();
		ConnectionPropertiesDialog connectionPropertiesDialog = PARENT_SPRING_CONTEXT.getBean("dbConnectionDialog", ConnectionPropertiesDialog.class);
		connectionPropertiesDialog.dispose();
		System.exit(0);
	}

	public static void restart() {
		JFrame patientsList = MAIN_SPRING_CONTEXT.getBean("patientsListWindow", JFrame.class);
		patientsList.setVisible(false);
		patientsList.dispose();

		MAIN_SPRING_CONTEXT.destroy();
		ConnectionPropertiesDialog connectionPropertiesDialog = PARENT_SPRING_CONTEXT.getBean("dbConnectionDialog", ConnectionPropertiesDialog.class);
		connectionPropertiesDialog.setVisible(true);
	}
}
