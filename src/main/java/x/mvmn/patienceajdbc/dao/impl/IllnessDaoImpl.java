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

import x.mvmn.patienceajdbc.dao.IllnessDao;
import x.mvmn.patienceajdbc.model.Illness;
import x.mvmn.patienceajdbc.model.impl.IllnessImpl;

public class IllnessDaoImpl implements IllnessDao {

	private static class IllnessRowMapper implements RowMapper<IllnessImpl> {

		public IllnessImpl mapRow(ResultSet rs, int rowNum) throws SQLException {
			long dbId = rs.getLong(1);
			String name = rs.getString(2);
			return new IllnessImpl(dbId, name);
		}
	}

	private final JdbcTemplate jdbcTemplate;
	private static final IllnessRowMapper ILLNESS_ROW_MAPPER = new IllnessRowMapper();

	public IllnessDaoImpl(final JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see x.mvmn.patienceajdbc.dao.impl.IllnessDao#create(java.lang.String)
	 */
	public IllnessImpl create(final String illnessName) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {

			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement prepStatement = con.prepareStatement("insert into Illness(name) values (?)", Statement.RETURN_GENERATED_KEYS);
				prepStatement.setString(1, illnessName);

				return prepStatement;
			}
		}, keyHolder);
		final Number key = keyHolder.getKey();
		final long dbId = key.longValue();

		return new IllnessImpl(dbId, illnessName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see x.mvmn.patienceajdbc.dao.impl.IllnessDao#countAll()
	 */
	public long countAll() {
		return jdbcTemplate.queryForLong("select count(id) from illness");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see x.mvmn.patienceajdbc.dao.impl.IllnessDao#listAll()
	 */
	public List<IllnessImpl> listAll() {
		List<IllnessImpl> result = jdbcTemplate.query("select id, name from illness", ILLNESS_ROW_MAPPER);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see x.mvmn.patienceajdbc.dao.impl.IllnessDao#get(long)
	 */
	public IllnessImpl get(final long illnessId) {
		IllnessImpl result = null;
		List<IllnessImpl> results = jdbcTemplate.query("select id, name from illness where id = ?", new Object[] { illnessId }, ILLNESS_ROW_MAPPER);
		if (results != null && results.size() > 0) {
			result = results.get(0);
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see x.mvmn.patienceajdbc.dao.impl.IllnessDao#findByName(java.lang.String)
	 */
	public IllnessImpl findByName(final String illnessName) {
		IllnessImpl result = null;
		List<IllnessImpl> results = jdbcTemplate.query("select id, name from illness where name = ?", new Object[] { illnessName }, ILLNESS_ROW_MAPPER);
		if (results != null && results.size() > 0) {
			result = results.get(0);
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see x.mvmn.patienceajdbc.dao.impl.IllnessDao#delete(x.mvmn.patienceajdbc. model.Illness)
	 */
	public boolean delete(final Illness illness) {
		return delete(illness.getId());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see x.mvmn.patienceajdbc.dao.impl.IllnessDao#delete(long)
	 */
	@Transactional
	public boolean delete(final long illnessId) {
		// jdbcTemplate.execute("START TRANSACTION");
		// try {
		jdbcTemplate.update("delete from MedicationToPatient where medicationId in (select id from Medication where illnessId = ?)", illnessId);
		jdbcTemplate.update("delete from Medication where illnessId = ?", illnessId);
		boolean result = jdbcTemplate.update("delete from Illness where id = ?", illnessId) > 0;
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
	 * @see x.mvmn.patienceajdbc.dao.impl.IllnessDao#deleteByName(java.lang.String)
	 */
	@Transactional
	public boolean deleteByName(final String illnessName) {
		// jdbcTemplate.execute("START TRANSACTION");
		// try {
		jdbcTemplate.update(
				"delete from MedicationToPatient where medicationId in (select id from Medication where illnessId = (select id from illness where name = ?))",
				illnessName);
		jdbcTemplate.update("delete from Medication where illnessId = (select id from illness where name = ?)", illnessName);
		boolean result = jdbcTemplate.update("delete from Illness where name = ?", illnessName) > 0;
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
	 * @see x.mvmn.patienceajdbc.dao.impl.IllnessDao#getNameById(long)
	 */
	public String getNameById(final long illnessId) {
		return jdbcTemplate.queryForObject("select name from Illness where id = ?", new Object[] { illnessId }, String.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see x.mvmn.patienceajdbc.dao.impl.IllnessDao#getIdByName(java.lang.String)
	 */
	public long getIdByName(final String illnessName) {
		return jdbcTemplate.queryForLong("select id from Illness where name = ?", illnessName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see x.mvmn.patienceajdbc.dao.impl.IllnessDao#updateName(long, java.lang.String)
	 */
	public boolean updateName(final long illnessId, final String newName) {
		return jdbcTemplate.update("update illness set name = ? where id = ?", newName, illnessId) > 0;
	}

}
