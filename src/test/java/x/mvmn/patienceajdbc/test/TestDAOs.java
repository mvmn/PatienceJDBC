package x.mvmn.patienceajdbc.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
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

public class TestDAOs extends TestCase {

	private ClassPathXmlApplicationContext daoContext;

	@Before
	public void setUp() throws Exception {
		GenericApplicationContext parentContext = new GenericApplicationContext();
		final Properties props = new Properties();
		props.load(TestDAOs.class.getResourceAsStream("/db.properties"));
		parentContext.getBeanFactory().registerSingleton("dbConnectionDialog", new Object() {
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
		});
		parentContext.refresh();
		parentContext.getBean("dbConnectionDialog");

		ClassPathXmlApplicationContext dbContext = new ClassPathXmlApplicationContext(new String[] { "springioc/db.xml" }, parentContext);
		DataSource dataSource = (DataSource) dbContext.getBean("dbDataSource");
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			Statement statement = connection.createStatement();
			BufferedReader buffReader = new BufferedReader(new InputStreamReader(TestDAOs.class.getResourceAsStream("/createschema.sql"), "UTF-8"));
			StringBuilder createSchemaSqlFileContentBuilder = new StringBuilder();
			String line;
			do {
				line = buffReader.readLine();
				if (line != null) {
					createSchemaSqlFileContentBuilder.append(line).append("\n");
				}
			} while (line != null);
			for (String statemetText : createSchemaSqlFileContentBuilder.toString().split(";")) {
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
		daoContext = new ClassPathXmlApplicationContext(new String[] { "springioc/dao.xml" }, dbContext);
	}

	@Test
	public void testAll() throws Exception {

		IllnessDao illnessDao = daoContext.getBean("illnessDao", IllnessDao.class);
		MedicationDao medicationDao = daoContext.getBean("medicationDao", MedicationDao.class);
		PatientDao patientDao = daoContext.getBean("patientDao", PatientDao.class);
		TagDao tagDao = daoContext.getBean("tagDao", TagDao.class);

		assert (illnessDao.countAll() == 0);
		assert (medicationDao.countAll() == 0);
		assert (patientDao.countAll() == 0);
		assert (tagDao.countAll() == 0);
		for (int i = 0; i < 2; i++) {
			for (Illness illness : illnessDao.listAll()) {
				illnessDao.delete(illness);
			}

			Illness flu = illnessDao.create("Flu");
			Illness plague = illnessDao.create("Plague");

			assert (illnessDao.countAll() == 2);
			Illness parkinson = illnessDao.create("Боткина");
			assert (illnessDao.countAll() == 3);
			assert (illnessDao.getNameById(parkinson.getId()).equals("Боткина"));
			illnessDao.delete(parkinson);
			assert (illnessDao.countAll() == 2);

			for (Medication med : medicationDao.list(flu.getId())) {
				medicationDao.delete(med);
			}

			assert (medicationDao.list(flu.getId()).size() == 0);
			Medication fluAspirin = medicationDao.create(flu.getId(), "Aspirin");
			Medication fluAflubin = medicationDao.create(flu.getId(), "Aflubin");
			Medication plagueAflubin = medicationDao.create(plague.getId(), "Aflubin");
			assert (medicationDao.countByIllness(flu.getId()) == 2);
			assert (medicationDao.list(plague.getId()).size() == 1);
			illnessDao.deleteByName("Plague");
			assert (medicationDao.countByIllness(plague.getId()) == 0);
			assert (medicationDao.findByName(flu.getId(), "Aflubin") != null);
			assert (medicationDao.findByName(plagueAflubin.getIllnessId(), plagueAflubin.getName()) == null);
			assert (medicationDao.list(flu.getId()).contains(fluAspirin));
			assert (medicationDao.list(flu.getId()).contains(fluAflubin));

			for (Tag tag : tagDao.listAll()) {
				tagDao.delete(tag);
			}
			assert (tagDao.countAll() == 0);
			assert (tagDao.listAll().size() == 0);
			Tag tagOne = tagDao.create("tag one");
			Tag tagTwo = tagDao.create("tag two");
			assert (tagOne.getName().equals("tag one"));
			assert (tagDao.listAll().get(1).getName().equals("tag two"));
			assert (tagDao.findByName(tagTwo.getName()).getId() == tagTwo.getId());
			assert (tagDao.getIdByName(tagOne.getName()) == tagOne.getId());
			assert (tagDao.getNameById(tagTwo.getId()).equals("tag two"));

			for (PatientData patient : patientDao.list(null, -1, -1, false)) {
				patientDao.delete(patient);
			}
			assert (patientDao.countAll() == 0);
			assert (patientDao.list(null, -1, -1, false).size() == 0);

			patientDao.create("Doe", "John", null, null, 1965, 01, 01, null, null, null, 2010, 01, 01, true, "Was ill.");
			patientDao.create("Doe", "Jane", null, null, 1970, 01, 01, null, null, null, 2012, 01, 01, true, "Was sick.");
			assert (patientDao.countAll() == 2);
			assert (patientDao.findCount("Doe", null, null, null, null, null, null, null, null, null, null, null, null, null, null) == 2);
			assert (patientDao.find(null, null, null, null, null, null, null, null, null, null, null, null, null, null, "%sick%", false).size() == 1);
			assert (patientDao.find(null, null, null, null, null, null, null, null, null, null, null, null, null, null, "%sick%", false).get(0).getFirstName()
					.equals("Jane"));
			assert (patientDao.find(null, null, "Petrovych", null, null, null, null, null, null, null, null, null, null, null, null, false).size() == 0);
			assert (patientDao.find(null, null, null, null, null, null, null, null, null, null, null, null, null, null, "%ill%", false).size() == 1);
			PatientDataImpl john = patientDao.find(null, null, null, null, null, null, null, null, null, null, null, null, null, null, "%ill%", false).get(0);
			assert (john.getFirstName().equals("John"));
			john.setPatronymicName("Petrovych");
			patientDao.update(john, false);
			assert (patientDao.find(null, null, "Petrovych", null, null, null, null, null, null, null, null, null, null, null, null, false).size() == 1);
			List<Medication> fluMeds = medicationDao.list(flu.getId());
			assert (fluMeds.size() == 2);
			john.setPreviousTreatments(fluMeds);
			patientDao.update(john, true);
			assert (patientDao.get(john.getId(), true).getPreviousTreatments().size() == 2);
			medicationDao.deleteAllByIllness(flu.getId());
			assert (patientDao.get(john.getId(), true).getPreviousTreatments().size() == 0);
			patientDao.create("Тест", "Василь", "Батькович", "Село", null, null, null, null, null, null, null, null, null, false, "Занедужав.");
		}
	}
}
