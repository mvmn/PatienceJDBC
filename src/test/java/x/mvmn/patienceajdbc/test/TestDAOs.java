package x.mvmn.patienceajdbc.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import x.mvmn.patienceajdbc.dao.IllnessDao;
import x.mvmn.patienceajdbc.dao.MedicationDao;
import x.mvmn.patienceajdbc.dao.PatientDao;
import x.mvmn.patienceajdbc.dao.TagDao;
import x.mvmn.patienceajdbc.model.Illness;
import x.mvmn.patienceajdbc.model.Medication;
import x.mvmn.patienceajdbc.model.PatientData;
import x.mvmn.patienceajdbc.model.Tag;
import x.mvmn.patienceajdbc.model.impl.PatientDataImpl;

public class TestDAOs {

	private ClassPathXmlApplicationContext daoContext;

	private static GenericApplicationContext PARENT_MOCK_CONTEXT;
	private static final Object MOCK_DB_CONNECTION_INPUT = new Object() {

		final Properties props;
		{
			props = new Properties();
			try {
				props.load(TestDAOs.class.getResourceAsStream("/db.properties"));
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

	private static String SQL_SCRIPT_RECREATE_DB;

	@BeforeClass
	public static void beforeClass() throws Exception {
		{
			GenericApplicationContext parentContext = new GenericApplicationContext();
			parentContext.getBeanFactory().registerSingleton("dbConnectionDialog", MOCK_DB_CONNECTION_INPUT);
			parentContext.refresh();
			PARENT_MOCK_CONTEXT = parentContext;
		}
		{
			BufferedReader buffReader = new BufferedReader(new InputStreamReader(TestDAOs.class.getResourceAsStream("/createschema.sql"), "UTF-8"));
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

	@Test
	public void testIllnessCrud() {
		IllnessDao illnessDao = daoContext.getBean("illnessDao", IllnessDao.class);
		Assert.assertTrue(illnessDao.countAll() == 0);
		for (int i = 0; i < 2; i++) {
			for (Illness illness : illnessDao.listAll()) {
				illnessDao.delete(illness);
			}

			illnessDao.create("Flu");
			illnessDao.create("Plague");

			Assert.assertTrue(illnessDao.countAll() == 2);
			Illness cyryllicNamedIllness = illnessDao.create("Боткина");
			Assert.assertTrue(illnessDao.countAll() == 3);
			Assert.assertTrue(illnessDao.getNameById(cyryllicNamedIllness.getId()).equals("Боткина"));
			Assert.assertTrue(illnessDao.findByName("Боткина").getId() == cyryllicNamedIllness.getId());
			illnessDao.delete(cyryllicNamedIllness);
			Assert.assertTrue(illnessDao.countAll() == 2);

			Exception duplicationError = null;
			try {
				illnessDao.create("Flu");
			} catch (Exception e) {
				duplicationError = e;
			}
			Assert.assertNotNull(duplicationError);
		}
	}

	@Test
	public void testAll() throws Exception {
		IllnessDao illnessDao = daoContext.getBean("illnessDao", IllnessDao.class);
		MedicationDao medicationDao = daoContext.getBean("medicationDao", MedicationDao.class);
		PatientDao patientDao = daoContext.getBean("patientDao", PatientDao.class);
		TagDao tagDao = daoContext.getBean("tagDao", TagDao.class);

		Assert.assertTrue(illnessDao.countAll() == 0);
		Assert.assertTrue(medicationDao.countAll() == 0);
		Assert.assertTrue(patientDao.countAll() == 0);
		Assert.assertTrue(tagDao.countAll() == 0);
		for (int i = 0; i < 2; i++) {
			for (Illness illness : illnessDao.listAll()) {
				illnessDao.delete(illness);
			}

			Illness flu = illnessDao.create("Flu");
			Illness plague = illnessDao.create("Plague");

			Assert.assertTrue(illnessDao.countAll() == 2);

			for (Medication med : medicationDao.list(flu.getId())) {
				medicationDao.delete(med);
			}

			Assert.assertTrue(medicationDao.list(flu.getId()).size() == 0);
			Medication fluAspirin = medicationDao.create(flu.getId(), "Aspirin");
			Medication fluAflubin = medicationDao.create(flu.getId(), "Aflubin");
			Medication plagueAflubin = medicationDao.create(plague.getId(), "Aflubin");
			Assert.assertTrue(medicationDao.countByIllness(flu.getId()) == 2);
			Assert.assertTrue(medicationDao.list(plague.getId()).size() == 1);
			illnessDao.deleteByName("Plague");
			Assert.assertTrue(medicationDao.countByIllness(plague.getId()) == 0);
			Assert.assertTrue(medicationDao.findByName(flu.getId(), "Aflubin") != null);
			Assert.assertTrue(medicationDao.findByName(plagueAflubin.getIllnessId(), plagueAflubin.getName()) == null);
			Assert.assertTrue(medicationDao.list(flu.getId()).contains(fluAspirin));
			Assert.assertTrue(medicationDao.list(flu.getId()).contains(fluAflubin));

			for (Tag tag : tagDao.listAll()) {
				tagDao.delete(tag);
			}
			Assert.assertTrue(tagDao.countAll() == 0);
			Assert.assertTrue(tagDao.listAll().size() == 0);
			Tag tagOne = tagDao.create("tag one");
			Tag tagTwo = tagDao.create("tag two");
			Assert.assertTrue(tagOne.getName().equals("tag one"));
			Assert.assertTrue(tagDao.listAll().get(1).getName().equals("tag two"));
			Assert.assertTrue(tagDao.findByName(tagTwo.getName()).getId() == tagTwo.getId());
			Assert.assertTrue(tagDao.getIdByName(tagOne.getName()) == tagOne.getId());
			Assert.assertTrue(tagDao.getNameById(tagTwo.getId()).equals("tag two"));

			for (PatientData patient : patientDao.list(null, -1, -1, false)) {
				patientDao.delete(patient);
			}
			Assert.assertTrue(patientDao.countAll() == 0);
			Assert.assertTrue(patientDao.list(null, -1, -1, false).size() == 0);

			patientDao.create("Doe", "John", null, null, 1965, 01, 01, null, null, null, 2010, 01, 01, true, "Was ill.");
			patientDao.create("Doe", "Jane", null, null, 1970, 01, 01, null, null, null, 2012, 01, 01, true, "Was sick.");
			Assert.assertTrue(patientDao.countAll() == 2);
			Assert.assertTrue(patientDao.findCount("Doe", null, null, null, null, null, null, null, null, null, null, null, null, null, null) == 2);
			Assert.assertTrue(patientDao.find(null, null, null, null, null, null, null, null, null, null, null, null, null, null, "%sick%", false).size() == 1);
			Assert.assertTrue(patientDao.find(null, null, null, null, null, null, null, null, null, null, null, null, null, null, "%sick%", false).get(0)
					.getFirstName().equals("Jane"));
			Assert.assertTrue(patientDao.find(null, null, "Petrovych", null, null, null, null, null, null, null, null, null, null, null, null, false).size() == 0);
			Assert.assertTrue(patientDao.find(null, null, null, null, null, null, null, null, null, null, null, null, null, null, "%ill%", false).size() == 1);
			PatientDataImpl john = patientDao.find(null, null, null, null, null, null, null, null, null, null, null, null, null, null, "%ill%", false).get(0);
			Assert.assertTrue(john.getFirstName().equals("John"));
			john.setPatronymicName("Petrovych");
			patientDao.update(john, false);
			Assert.assertTrue(patientDao.find(null, null, "Petrovych", null, null, null, null, null, null, null, null, null, null, null, null, false).size() == 1);
			List<Medication> fluMeds = medicationDao.list(flu.getId());
			Assert.assertTrue(fluMeds.size() == 2);
			john.setPreviousTreatments(fluMeds);
			patientDao.update(john, true);
			Assert.assertTrue(patientDao.get(john.getId(), true).getPreviousTreatments().size() == 2);
			medicationDao.deleteAllByIllness(flu.getId());
			Assert.assertTrue(patientDao.get(john.getId(), true).getPreviousTreatments().size() == 0);
			patientDao.create("Тест", "Василь", "Батькович", "Село", null, null, null, null, null, null, null, null, null, false, "Занедужав.");
		}
	}
}
