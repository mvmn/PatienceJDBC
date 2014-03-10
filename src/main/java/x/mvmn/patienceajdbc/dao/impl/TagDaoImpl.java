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

import x.mvmn.patienceajdbc.dao.TagDao;
import x.mvmn.patienceajdbc.model.Tag;
import x.mvmn.patienceajdbc.model.impl.TagImpl;

public class TagDaoImpl implements TagDao {

	private static class TagRowMapper implements RowMapper<TagImpl> {

		public TagImpl mapRow(ResultSet rs, int rowNum) throws SQLException {
			long dbId = rs.getLong(1);
			String name = rs.getString(2);
			return new TagImpl(dbId, name);
		}
	}

	private final JdbcTemplate jdbcTemplate;
	private static final TagRowMapper TAG_ROW_MAPPER = new TagRowMapper();

	public TagDaoImpl(final JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see x.mvmn.patienceajdbc.dao.impl.TagDao#create(java.lang.String)
	 */
	public TagImpl create(final String tagName) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {

			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement prepStatement = con.prepareStatement("insert into Tag(name) values (?)", Statement.RETURN_GENERATED_KEYS);
				prepStatement.setString(1, tagName);
				return prepStatement;
			}
		}, keyHolder);
		final Number key = keyHolder.getKey();
		final long dbId = key.longValue();

		return new TagImpl(dbId, tagName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see x.mvmn.patienceajdbc.dao.impl.TagDao#countAll()
	 */
	public long countAll() {
		return jdbcTemplate.queryForLong("select count(id) from Tag");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see x.mvmn.patienceajdbc.dao.impl.TagDao#listAll()
	 */
	public List<TagImpl> listAll() {
		List<TagImpl> result = jdbcTemplate.query("select id, name from tag ORDER BY name", TAG_ROW_MAPPER);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see x.mvmn.patienceajdbc.dao.impl.TagDao#get(long)
	 */
	public TagImpl get(final long tagId) {
		TagImpl result = null;
		List<TagImpl> results = jdbcTemplate.query("select id, name from tag where id = ?", new Object[] { tagId }, TAG_ROW_MAPPER);
		if (results != null && results.size() > 0) {
			result = results.get(0);
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see x.mvmn.patienceajdbc.dao.impl.TagDao#findByName(java.lang.String)
	 */
	public TagImpl findByName(final String tagName) {
		TagImpl result = null;
		List<TagImpl> results = jdbcTemplate.query("select id, name from tag where name = ?", new Object[] { tagName }, TAG_ROW_MAPPER);
		if (results != null && results.size() > 0) {
			result = results.get(0);
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see x.mvmn.patienceajdbc.dao.impl.TagDao#delete(x.mvmn.patienceajdbc.model .Tag)
	 */
	public boolean delete(final Tag tag) {
		return delete(tag.getId());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see x.mvmn.patienceajdbc.dao.impl.TagDao#delete(long)
	 */
	@Transactional
	public boolean delete(final long tagId) {
		// jdbcTemplate.execute("START TRANSACTION");
		// try {
		jdbcTemplate.update("delete from ExaminationResultsToTag where tagId = ?", tagId);
		boolean result = jdbcTemplate.update("delete from Tag where id = ?", tagId) > 0;
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
	 * @see x.mvmn.patienceajdbc.dao.impl.TagDao#deleteByName(java.lang.String)
	 */
	@Transactional
	public boolean deleteByName(final String tagName) {
		// jdbcTemplate.execute("START TRANSACTION");
		// try {
		jdbcTemplate.update("delete from ExaminationResultsToTag where tagId = (select id from Tag where name = ?)", tagName);
		boolean result = jdbcTemplate.update("delete from Tag where name = ?", tagName) > 0;
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
	 * @see x.mvmn.patienceajdbc.dao.impl.TagDao#getNameById(long)
	 */
	public String getNameById(final long tagId) {
		return jdbcTemplate.queryForObject("select name from Tag where id = ?", new Object[] { tagId }, String.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see x.mvmn.patienceajdbc.dao.impl.TagDao#getIdByName(java.lang.String)
	 */
	public long getIdByName(final String tagName) {
		return jdbcTemplate.queryForLong("select id from Tag where name = ?", tagName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see x.mvmn.patienceajdbc.dao.impl.TagDao#updateName(long, java.lang.String)
	 */
	public boolean updateName(final long tagId, final String newName) {
		return jdbcTemplate.update("update tag set name = ? where id = ?", newName, tagId) > 0;
	}

}
