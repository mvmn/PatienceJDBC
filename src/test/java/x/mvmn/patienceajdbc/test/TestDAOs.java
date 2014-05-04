package x.mvmn.patienceajdbc.test;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import x.mvmn.patienceajdbc.dao.DataAccessObject;
import x.mvmn.patienceajdbc.dao.IllnessDao;
import x.mvmn.patienceajdbc.dao.MedicationDao;
import x.mvmn.patienceajdbc.dao.PatientDao;
import x.mvmn.patienceajdbc.dao.TagDao;
import x.mvmn.patienceajdbc.model.Illness;
import x.mvmn.patienceajdbc.model.Medication;
import x.mvmn.patienceajdbc.model.PatientData;
import x.mvmn.patienceajdbc.model.Tag;
import x.mvmn.patienceajdbc.model.impl.PatientDataImpl;

public class TestDAOs extends AbstractDAOTest<DataAccessObject> {

	// FIXME: Refactor into separate tests
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

	@Override
	protected Class<DataAccessObject> getDaoClass() {
		return null;
	}

	@Override
	protected String getDaoBeanName() {
		return null;
	}

}
