package x.mvmn.patienceajdbc.test;

import org.junit.Assert;
import org.junit.Test;

import x.mvmn.lang.tuple.TupleOfTwo;
import x.mvmn.patienceajdbc.dao.IllnessDao;
import x.mvmn.patienceajdbc.model.Illness;

public class TestIllnessDAO extends AbstractDAOTest<IllnessDao> {

	@Test
	public void testCreationCountAndDeletion() {
		IllnessDao illnessDao = getDAO();

		Assert.assertEquals(0, illnessDao.countAll());

		for (int i = 0; i < 2; i++) {
			for (Illness illness : illnessDao.listAll()) {
				illnessDao.delete(illness);
			}
			Assert.assertEquals(0, illnessDao.countAll());
			illnessDao.create("Illness One " + i);
			Assert.assertEquals(1, illnessDao.countAll());
			illnessDao.create("Illness Two " + i);
			Assert.assertEquals(2, illnessDao.countAll());
			illnessDao.create("Illness Three " + i);
			Assert.assertEquals(3, illnessDao.countAll());

			Exception duplicationError = null;
			try {
				illnessDao.create("Illness One " + i);
			} catch (Exception e) {
				duplicationError = e;
			}
			Assert.assertNotNull(duplicationError);
			Assert.assertEquals(3, illnessDao.countAll());
			illnessDao.delete(illnessDao.getIdByName("Illness Three " + i));
			Assert.assertEquals(2, illnessDao.countAll());
			illnessDao.deleteByName("Illness Two " + i);
			Assert.assertEquals(1, illnessDao.countAll());
		}
	}

	@Test
	public void testFind() {
		IllnessDao illnessDao = getDAO();

		Assert.assertEquals(0, illnessDao.countAll());
		Illness cyryllicNamedIllness = illnessDao.create("Боткина");
		Assert.assertEquals(1, illnessDao.countAll());
		Assert.assertEquals("Боткина", illnessDao.getNameById(cyryllicNamedIllness.getId()));
		Assert.assertEquals(cyryllicNamedIllness.getId(), illnessDao.findByName("Боткина").getId());
	}

	@Test
	public void testUpdate() {
		IllnessDao illnessDao = getDAO();

		Assert.assertEquals(0, illnessDao.countAll());
		Illness cyryllicNamedIllness = illnessDao.create("Боткина");
		Assert.assertEquals(1, illnessDao.countAll());
		Assert.assertEquals("Боткина", illnessDao.getNameById(cyryllicNamedIllness.getId()));
		Assert.assertEquals(cyryllicNamedIllness.getId(), illnessDao.findByName("Боткина").getId());
		illnessDao.updateName(illnessDao.findByName("Боткина").getId(), "Паркинсона");
		Assert.assertEquals(1, illnessDao.countAll());
		Assert.assertEquals("Паркинсона", illnessDao.listAll().get(0).getName());
		Assert.assertEquals("Паркинсона", illnessDao.getNameById(cyryllicNamedIllness.getId()));
		Assert.assertEquals(cyryllicNamedIllness.getId(), illnessDao.findByName("Паркинсона").getId());
	}

	public static final TupleOfTwo<String, Class<IllnessDao>> DAO_BEAN_DEFINITION = new TupleOfTwo<String, Class<IllnessDao>>("illnessDao",
			IllnessDao.class);

	@Override
	protected TupleOfTwo<String, Class<IllnessDao>> getDaoBeanNameAndClass() {
		return DAO_BEAN_DEFINITION;
	}

}
