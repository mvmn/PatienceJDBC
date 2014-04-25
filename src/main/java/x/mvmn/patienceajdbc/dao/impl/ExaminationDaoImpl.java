package x.mvmn.patienceajdbc.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Transactional;

import x.mvmn.patienceajdbc.dao.ExaminationDao;
import x.mvmn.patienceajdbc.model.CariotypeExaminationResults;
import x.mvmn.patienceajdbc.model.FishExaminationResults;
import x.mvmn.patienceajdbc.model.IllnessPhase;
import x.mvmn.patienceajdbc.model.impl.AbstractExaminationResultsImpl;
import x.mvmn.patienceajdbc.model.impl.CariotypeExaminationResultsImpl;
import x.mvmn.patienceajdbc.model.impl.ExaminationDataImpl;
import x.mvmn.patienceajdbc.model.impl.FishExaminationResultsImpl;
import x.mvmn.patienceajdbc.model.impl.MedicationImpl;
import x.mvmn.patienceajdbc.model.impl.TagImpl;

public class ExaminationDaoImpl implements ExaminationDao {

	private static class ExaminationDataRowMapper implements RowMapper<ExaminationDataImpl> {

		public ExaminationDataImpl mapRow(ResultSet rs, int rowNum) throws SQLException {
			ExaminationDataImpl result = new ExaminationDataImpl();

			long dbId = rs.getLong(1);
			long patientId = rs.getLong(2);
			long illnessId = rs.getLong(3);
			int number = rs.getInt(4);
			String matherial = rs.getString(5);
			String blood = rs.getString(6);
			String mielogramm = rs.getString(7);
			String treatmentDescription = rs.getString(8);
			String comments = rs.getString(9);
			Date examinationDate = rs.getDate(10);
			IllnessPhase illnessPhase = rs.getString(11) != null ? IllnessPhase.valueOf(rs.getString(11)) : IllnessPhase.UNSET;
			String typeName = rs.getString(12);

			long cariotypeId = rs.getLong(13);
			String cariotypeNomenclaturalDesc = rs.getString(14);
			String cariotypeComments = rs.getString(15);

			long fishId = rs.getLong(16);
			String fishNomenclaturalDesc = rs.getString(17);
			String fishComments = rs.getString(18);

			result.setId(dbId);
			result.setPatientId(patientId);
			result.setIllnessId(illnessId);
			result.setNumber(number);
			result.setMatherial(matherial);
			result.setBlood(blood);
			result.setMielogramm(mielogramm);
			result.setTreatmentDescription(treatmentDescription);
			result.setComments(comments);
			result.setExaminationDate(examinationDate);
			result.setPhase(illnessPhase);

			if (CariotypeExaminationResults.getExaminationTypeName().equalsIgnoreCase(typeName) && cariotypeId > 0) {
				CariotypeExaminationResultsImpl examResults = new CariotypeExaminationResultsImpl();
				examResults.setId(cariotypeId);
				examResults.setNomenclaturalDescription(cariotypeNomenclaturalDesc);
				examResults.setComments(cariotypeComments);
				result.setCariotypeExaminationResults(examResults);
			} else if (FishExaminationResults.getExaminationTypeName().equalsIgnoreCase(typeName) && fishId > 0) {
				FishExaminationResultsImpl examResults = new FishExaminationResultsImpl();
				examResults.setId(fishId);
				examResults.setNomenclaturalDescription(fishNomenclaturalDesc);
				examResults.setComments(fishComments);
				result.setFishExaminationResults(examResults);
			}

			return result;
		}
	}

	private final JdbcTemplate jdbcTemplate;
	private static final ExaminationDataRowMapper EXAMINATION_DATA_ROW_MAPPER = new ExaminationDataRowMapper();

	public ExaminationDaoImpl(final JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	private static final String SELECT_STATEMENT = "select ExaminationResults.id, ExaminationResults.patientId, ExaminationResults.illnessId, "
			+ " ExaminationResults.number, ExaminationResults.matherial, ExaminationResults.blood, ExaminationResults.mielogramm, "
			+ "ExaminationResults.treatmentDescription, ExaminationResults.comments, ExaminationResults.examinationDate, ExaminationResults.illnessPhase, "
			+ " ExaminationResults.typeName, CariotypeExamResults.id, CariotypeExamResults.nomenclaturalDescription, CariotypeExamResults.comments, "
			+ " FishExamResults.id, FishExamResults.nomenclaturalDescription, FishExamResults.comments FROM ExaminationResults "
			+ " LEFT JOIN CariotypeExamResults ON CariotypeExamResults.examinationResultsId = ExaminationResults.id "
			+ " LEFT JOIN FishExamResults ON FishExamResults.examinationResultsId = ExaminationResults.id ";

	/*
	 * (non-Javadoc)
	 * 
	 * @see x.mvmn.patienceajdbc.dao.IExaminationDao#get(long)
	 */
	public ExaminationDataImpl get(final long id) {
		ExaminationDataImpl result = null;
		List<ExaminationDataImpl> results = jdbcTemplate.query(SELECT_STATEMENT + " WHERE ExaminationResults.id = ?", new Object[] { id },
				EXAMINATION_DATA_ROW_MAPPER);
		if (results != null && results.size() > 0) {
			result = results.get(0);
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see x.mvmn.patienceajdbc.dao.IExaminationDao#getByNumber(int)
	 */
	public ExaminationDataImpl getByNumber(final int number) {
		ExaminationDataImpl result = null;
		List<ExaminationDataImpl> results = jdbcTemplate.query(SELECT_STATEMENT + " WHERE ExaminationResults.number = ?", new Object[] { number },
				EXAMINATION_DATA_ROW_MAPPER);
		if (results != null && results.size() > 0) {
			result = results.get(0);
		}
		return result;
	}

	public List<ExaminationDataImpl> getByPatient(final long patientId) {
		List<ExaminationDataImpl> results = jdbcTemplate.query(SELECT_STATEMENT + " WHERE ExaminationResults.patientId = ?", new Object[] { patientId },
				EXAMINATION_DATA_ROW_MAPPER);
		return results;
	}

	public List<ExaminationDataImpl> getByIllness(final long illnessId) {
		List<ExaminationDataImpl> results = jdbcTemplate.query(SELECT_STATEMENT + " WHERE ExaminationResults.illnessId = ?", new Object[] { illnessId },
				EXAMINATION_DATA_ROW_MAPPER);
		return results;
	}

	public List<ExaminationDataImpl> getByPatientAndIllness(final long patientId, final long illnessId) {
		List<ExaminationDataImpl> results = jdbcTemplate.query(SELECT_STATEMENT
				+ " WHERE ExaminationResults.patientId = ? AND ExaminationResults.illnessId = ?", new Object[] { patientId, illnessId },
				EXAMINATION_DATA_ROW_MAPPER);
		return results;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see x.mvmn.patienceajdbc.dao.IExaminationDao#create(long, long, int, java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, java.util.Date, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public ExaminationDataImpl create(final long patientId, final long illnessId, final int number, final String matherial, final String blood,
			final String mielogramm, final String treatmentDescription, final String comments, final Date examinationDate, final IllnessPhase illnessPhase,
			final String typeName, final String nomenclaturalDescription, final String examinationComments) {
		final ExaminationDataImpl result;

		final String subtypeTable;
		CariotypeExaminationResultsImpl cariotypeExaminationResultsImpl = null;
		FishExaminationResultsImpl fishExaminationResultsImpl = null;
		final AbstractExaminationResultsImpl examinationResultsImpl;
		if ("FISH".equalsIgnoreCase(typeName)) {
			subtypeTable = "FishExamResults";
			fishExaminationResultsImpl = new FishExaminationResultsImpl();
			examinationResultsImpl = fishExaminationResultsImpl;
		} else if ("Cariotype".equalsIgnoreCase(typeName)) {
			subtypeTable = "CariotypeExamResults";
			cariotypeExaminationResultsImpl = new CariotypeExaminationResultsImpl();
			examinationResultsImpl = cariotypeExaminationResultsImpl;
		} else {
			subtypeTable = null;
			examinationResultsImpl = null;
		}

		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {

			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement prepStatement = con
						.prepareStatement(
								"insert into ExaminationResults(patientId, illnessId, number, matherial, blood, mielogramm, treatmentDescription, comments, examinationDate, illnessPhase, typeName) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
								Statement.RETURN_GENERATED_KEYS);
				prepStatement.setLong(1, patientId);
				prepStatement.setLong(2, illnessId);
				prepStatement.setInt(3, number);
				prepStatement.setString(4, matherial);
				prepStatement.setString(5, blood);
				prepStatement.setString(6, mielogramm);
				prepStatement.setString(7, treatmentDescription);
				prepStatement.setString(8, comments);
				prepStatement.setDate(9, examinationDate == null ? null : new java.sql.Date(examinationDate.getTime()));
				prepStatement.setString(10, illnessPhase.name());
				prepStatement.setString(11, typeName);
				return prepStatement;
			}
		}, keyHolder);

		final Number key = keyHolder.getKey();
		final long examinationDataDbId = key.longValue();

		if (subtypeTable != null) {
			KeyHolder subtypeTableKeyHolder = new GeneratedKeyHolder();
			jdbcTemplate.update(new PreparedStatementCreator() {

				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement prepStatement = con.prepareStatement("insert into " + subtypeTable
							+ "(examinationResultsId, nomenclaturalDescription, comments) values (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
					prepStatement.setLong(1, examinationDataDbId);
					prepStatement.setString(2, nomenclaturalDescription);
					prepStatement.setString(3, examinationComments);
					return prepStatement;
				}
			}, keyHolder);

			final Number subtypeTableKey = subtypeTableKeyHolder.getKey();
			final long examinationResultsDbId = subtypeTableKey.longValue();

			examinationResultsImpl.setId(examinationResultsDbId);
			examinationResultsImpl.setNomenclaturalDescription(nomenclaturalDescription);
			examinationResultsImpl.setComments(examinationComments);
		}

		result = new ExaminationDataImpl();
		result.setId(examinationDataDbId);
		result.setCariotypeExaminationResults(cariotypeExaminationResultsImpl);
		result.setFishExaminationResults(fishExaminationResultsImpl);
		result.setBlood(blood);
		result.setComments(comments);
		result.setExaminationDate(examinationDate);
		result.setIllnessId(illnessId);
		result.setMatherial(matherial);
		result.setMielogramm(mielogramm);
		result.setNumber(number);
		result.setPatientId(patientId);
		result.setPhase(illnessPhase);
		result.setTreatmentDescription(treatmentDescription);

		result.setTreatment(new ArrayList<MedicationImpl>());
		result.setKeywords(new ArrayList<TagImpl>());

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see x.mvmn.patienceajdbc.dao.IExaminationDao#countAll()
	 */
	public int countAll() {
		return jdbcTemplate.queryForInt("select count(id) from ExaminationResults");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see x.mvmn.patienceajdbc.dao.IExaminationDao#delete(long)
	 */
	@Transactional
	public boolean delete(long examinationId) {
		// jdbcTemplate.execute("START TRANSACTION");
		// try {
		jdbcTemplate.update("delete from ExaminationResultsToTag where examinationResultsId = ?", examinationId);
		jdbcTemplate.update("delete from CariotypeExamResults where examinationResultsId = ?", examinationId);
		jdbcTemplate.update("delete from FishExamResults where examinationResultsId = ?", examinationId);
		boolean result = jdbcTemplate.update("delete from ExaminationResults WHERE id = ?", examinationId) > 0;
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

}
