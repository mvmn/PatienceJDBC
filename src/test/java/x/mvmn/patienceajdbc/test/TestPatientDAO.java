package x.mvmn.patienceajdbc.test;

import org.junit.Assert;
import org.junit.Test;

import x.mvmn.lang.tuple.TupleOfTwo;
import x.mvmn.misc.string.StringConstants;
import x.mvmn.patienceajdbc.dao.PatientDao;
import x.mvmn.patienceajdbc.model.PatientData;

public class TestPatientDAO extends AbstractDAOTest<PatientDao> {

	public static final TupleOfTwo<String, Class<PatientDao>> DAO_BEAN_DEFINITION = new TupleOfTwo<String, Class<PatientDao>>("patientDao", PatientDao.class);

	@Override
	protected TupleOfTwo<String, Class<PatientDao>> getDaoBeanNameAndClass() {
		return DAO_BEAN_DEFINITION;
	}

	@Test
	public void testCreationAndDeletion() {
		PatientDao patientDao = getDAO();
		Assert.assertEquals(0, patientDao.countAll());
		PatientData patientData = patientDao.create("last name", "first name", "patronymic name", "address stub", 1983, 10, null, null, 9, 18, null, null,
				null, false, StringConstants.LOREM_IPSUM_LONG);
		Assert.assertEquals("last name", patientData.getLastName());
		Assert.assertEquals("first name", patientData.getFirstName());
		Assert.assertEquals("patronymic name", patientData.getPatronymicName());
		Assert.assertEquals("address stub", patientData.getAddress());
		Assert.assertEquals(new Integer(1983), patientData.getBirthDateYear());
		Assert.assertEquals(new Integer(10), patientData.getBirthDateMonth());
		Assert.assertNull(patientData.getBirthDateDay());
		Assert.assertNull(patientData.getDiagnosisDateYear());
		Assert.assertEquals(new Integer(9), patientData.getDiagnosisDateMonth());
		Assert.assertEquals(new Integer(18), patientData.getDiagnosisDateDay());
		Assert.assertNull(patientData.getDeathDateYear());
		Assert.assertNull(patientData.getDeathDateMonth());
		Assert.assertNull(patientData.getDeathDateDay());
		Assert.assertFalse(patientData.isDead());
		Assert.assertEquals(StringConstants.LOREM_IPSUM_LONG, patientData.getAnamnesis());
		Assert.assertEquals(1, patientDao.countAll());
		patientDao.delete(patientData);
		Assert.assertEquals(0, patientDao.countAll());
	}

}
