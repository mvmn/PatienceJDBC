package x.mvmn.patienceajdbc.test;

import org.junit.Assert;
import org.junit.Test;

import x.mvmn.lang.container.TupleOfTwo;
import x.mvmn.patienceajdbc.dao.IllnessDao;
import x.mvmn.patienceajdbc.model.Illness;

public class TestIllnessDAO extends AbstractDAOTest<IllnessDao> {

	@Test
	public void testIllnessCrud() {
		IllnessDao illnessDao = getDAO();

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

	private static final TupleOfTwo<String, Class<IllnessDao>> DAO_BEAN_DEFINITION_TUPLE = new TupleOfTwo<String, Class<IllnessDao>>("illnessDao",
			IllnessDao.class);

	@Override
	protected TupleOfTwo<String, Class<IllnessDao>> getDaoBeanNameAndClass() {
		return DAO_BEAN_DEFINITION_TUPLE;
	}

}
