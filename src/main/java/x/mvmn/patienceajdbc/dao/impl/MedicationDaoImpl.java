package x.mvmn.patienceajdbc.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Transactional;

import x.mvmn.patienceajdbc.dao.MedicationDao;
import x.mvmn.patienceajdbc.model.Medication;
import x.mvmn.patienceajdbc.model.impl.MedicationImpl;

public class MedicationDaoImpl implements MedicationDao {

	private static class MedicationRowMapper implements RowMapper<Medication> {

		public MedicationImpl mapRow(ResultSet rs, int rowNum) throws SQLException {
			long dbId = rs.getLong(1);
			long illnessId = rs.getLong(2);
			String name = rs.getString(3);
			return new MedicationImpl(dbId, illnessId, name);
		}
	}

	private final JdbcTemplate jdbcTemplate;
	private static final MedicationRowMapper MEDICATION_ROW_MAPPER = new MedicationRowMapper();

	public MedicationDaoImpl(final JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see x.mvmn.patienceajdbc.dao.impl.MedicationDao#create(long, java.lang.String)
	 */
	public MedicationImpl create(final long illnessId, final String medicationName) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {

			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement prepStatement = con
						.prepareStatement("insert into Medication(illnessId, name) values (?, ?)", Statement.RETURN_GENERATED_KEYS);
				prepStatement.setLong(1, illnessId);
				prepStatement.setString(2, medicationName);
				return prepStatement;
			}
		}, keyHolder);
		final Number key = keyHolder.getKey();
		final long dbId = key.longValue();

		return new MedicationImpl(dbId, illnessId, medicationName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see x.mvmn.patienceajdbc.dao.impl.MedicationDao#get(long)
	 */
	public Medication get(final long medicationId) {
		Medication result = null;
		List<Medication> results = jdbcTemplate.query("select id, illnessId, name from medication where id = ?", new Object[] { medicationId },
				MEDICATION_ROW_MAPPER);
		if (results != null && results.size() > 0) {
			result = results.get(0);
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see x.mvmn.patienceajdbc.dao.impl.MedicationDao#countAll()
	 */
	public long countAll() {
		return jdbcTemplate.queryForLong("select count(id) from medication");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see x.mvmn.patienceajdbc.dao.impl.MedicationDao#countByIllness(long)
	 */
	public long countByIllness(final long illnessId) {
		return jdbcTemplate.queryForLong("select count(id) from medication where illnessId = ? ", illnessId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see x.mvmn.patienceajdbc.dao.impl.MedicationDao#list(long)
	 */
	public List<Medication> list(final long illnessId) {
		List<Medication> result = jdbcTemplate.query("select id, illnessId, name from medication where illnessId = ?", new Object[] { illnessId },
				MEDICATION_ROW_MAPPER);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see x.mvmn.patienceajdbc.dao.impl.MedicationDao#findByName(long, java.lang.String)
	 */
	public Medication findByName(final long illnessId, final String medicationName) {
		Medication result = null;
		List<Medication> results = jdbcTemplate.query("select id, illnessId, name from medication where illnessId = ? AND name = ?", new Object[] { illnessId,
				medicationName }, MEDICATION_ROW_MAPPER);
		if (results != null && results.size() > 0) {
			result = results.get(0);
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see x.mvmn.patienceajdbc.dao.impl.MedicationDao#delete(x.mvmn.patienceajdbc .model.Medication)
	 */
	public boolean delete(final Medication medication) {
		return delete(medication.getId());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see x.mvmn.patienceajdbc.dao.impl.MedicationDao#delete(long)
	 */
	@Transactional
	public boolean delete(final long medicationId) {
		// jdbcTemplate.execute("START TRANSACTION");
		// try {
		jdbcTemplate.update("delete from MedicationToPatient where medicationId = ?", medicationId);
		boolean result = jdbcTemplate.update("delete from Medication WHERE id = ?", medicationId) > 0;
		// jdbcTemplate.execute("COMMIT");
		return result;
		// } catch (Exception e) {
		// try {
		// jdbcTemplate.execute("ROLLBACK");
		// } catch (Exception ex) {
		// ex.printStackTrace();
		// }
		// throw new RuntimeException(e);
		// }
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see x.mvmn.patienceajdbc.dao.impl.MedicationDao#deleteByName(long, java.lang.String)
	 */
	@Transactional
	public boolean deleteByName(final long illnessId, final String medicationName) {
		// jdbcTemplate.execute("START TRANSACTION");
		// try {
		jdbcTemplate.update("delete from MedicationToPatient where medicationId = (select id from medication where illnessId = ? AND name = ?)", illnessId,
				medicationName);
		boolean result = jdbcTemplate.update("delete from Medication WHERE illnessId = ? AND name = ?", illnessId, medicationName) > 0;
		// jdbcTemplate.execute("COMMIT");
		return result;
		// } catch (Exception e) {
		// try {
		// jdbcTemplate.execute("ROLLBACK");
		// } catch (Exception ex) {
		// ex.printStackTrace();
		// }
		// throw new RuntimeException(e);
		// }
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see x.mvmn.patienceajdbc.dao.impl.MedicationDao#deleteAllByIllness(long)
	 */
	@Transactional
	public int deleteAllByIllness(final long illnessId) {
		// jdbcTemplate.execute("START TRANSACTION");
		// try {
		jdbcTemplate.update("delete from MedicationToPatient where medicationId in (select id from medication where illnessId = ?)", illnessId);
		int result = jdbcTemplate.update("delete from Medication WHERE illnessId = ?", illnessId);
		// jdbcTemplate.execute("COMMIT");
		return result;
		// } catch (Exception e) {
		// try {
		// jdbcTemplate.execute("ROLLBACK");
		// } catch (Exception ex) {
		// ex.printStackTrace();
		// }
		// throw new RuntimeException(e);
		// }
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see x.mvmn.patienceajdbc.dao.impl.MedicationDao#getNameById(long)
	 */
	public String getNameById(final long medicationId) {
		return jdbcTemplate.queryForObject("select name from Medication WHERE id = ?", new Object[] { medicationId }, String.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see x.mvmn.patienceajdbc.dao.impl.MedicationDao#getIdByName(long, java.lang.String)
	 */
	public long getIdByName(final long illnessId, final String medicationName) {
		return jdbcTemplate.queryForLong("select id from Medication WHERE illnessId = ? AND name = ?", illnessId, medicationName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see x.mvmn.patienceajdbc.dao.impl.MedicationDao#updateName(long, java.lang.String)
	 */
	public boolean updateName(final long medicationId, final String newName) {
		return jdbcTemplate.update("update medication set name = ? WHERE id = ?", newName, medicationId) > 0;
	}

}
