package x.mvmn.patienceajdbc.dao.impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Transactional;

import x.mvmn.patienceajdbc.dao.ExaminationDao;
import x.mvmn.patienceajdbc.model.ExaminationData;
import x.mvmn.patienceajdbc.model.IllnessPhase;
import x.mvmn.patienceajdbc.model.Medication;
import x.mvmn.patienceajdbc.model.impl.AbstractExaminationResultsImpl;
import x.mvmn.patienceajdbc.model.impl.CariotypeExaminationResultsImpl;
import x.mvmn.patienceajdbc.model.impl.ExaminationDataImpl;
import x.mvmn.patienceajdbc.model.impl.FishExaminationResultsImpl;
import x.mvmn.patienceajdbc.model.impl.MedicationImpl;
import x.mvmn.patienceajdbc.model.impl.TagImpl;

public class ExaminationDaoImpl implements ExaminationDao {

	private static class ExaminationDataRowMapper implements ResultSetExtractor<List<ExaminationDataImpl>> {

		public List<ExaminationDataImpl> extractData(ResultSet rs) throws SQLException {
			List<ExaminationDataImpl> result = new ArrayList<ExaminationDataImpl>();
			ExaminationDataImpl examData = null;

			while (rs.next()) {
				long dbId = rs.getLong(1);
				if (examData == null || dbId != examData.getId()) {
					examData = new ExaminationDataImpl();
					long patientId = rs.getLong(2);
					long illnessId = rs.getLong(3);
					int number = rs.getInt(4);
					String matherial = rs.getString(5);
					String blood = rs.getString(6);
					String mielogramm = rs.getString(7);
					String treatmentDescription = rs.getString(8);
					String comments = rs.getString(9);
					Integer examinationDateYear = rs.getInt(10) != 0 ? rs.getInt(10) : null;
					Integer examinationDateMonth = rs.getInt(11) != 0 ? rs.getInt(11) : null;
					Integer examinationDateDay = rs.getInt(12) != 0 ? rs.getInt(12) : null;
					IllnessPhase illnessPhase = rs.getString(13) != null ? IllnessPhase.valueOf(rs.getString(13)) : IllnessPhase.UNSET;
					// String typeName = rs.getString(14);

					long cariotypeId = rs.getLong(15);
					String cariotypeNomenclaturalDesc = rs.getString(16);
					String cariotypeComments = rs.getString(17);

					long fishId = rs.getLong(18);
					String fishNomenclaturalDesc = rs.getString(19);
					String fishComments = rs.getString(20);

					examData.setId(dbId);
					examData.setPatientId(patientId);
					examData.setIllnessId(illnessId);
					examData.setNumber(number);
					examData.setMatherial(matherial);
					examData.setBlood(blood);
					examData.setMielogramm(mielogramm);
					examData.setTreatmentDescription(treatmentDescription);
					examData.setComments(comments);
					examData.setExaminationDateYear(examinationDateYear);
					examData.setExaminationDateMonth(examinationDateMonth);
					examData.setExaminationDateDay(examinationDateDay);
					examData.setPhase(illnessPhase);
					examData.setTreatment(new ArrayList<Medication>());

					if (// CariotypeExaminationResults.getExaminationTypeName().equalsIgnoreCase(typeName) &&
					cariotypeId > 0) {
						CariotypeExaminationResultsImpl examResults = new CariotypeExaminationResultsImpl();
						examResults.setId(cariotypeId);
						examResults.setNomenclaturalDescription(cariotypeNomenclaturalDesc);
						examResults.setComments(cariotypeComments);
						examData.setCariotypeExaminationResults(examResults);
					}
					if (// FishExaminationResults.getExaminationTypeName().equalsIgnoreCase(typeName) &&
					fishId > 0) {
						FishExaminationResultsImpl examResults = new FishExaminationResultsImpl();
						examResults.setId(fishId);
						examResults.setNomenclaturalDescription(fishNomenclaturalDesc);
						examResults.setComments(fishComments);
						examData.setFishExaminationResults(examResults);
					}
					result.add(examData);
				}

				if (rs.getMetaData().getColumnCount() > 20) {
					long medicationId = rs.getLong(21);
					if (medicationId > 0) {
						String medicationName = rs.getString(22);
						long medicationIllnessId = rs.getLong(23); // Should be same as illnessId though...
						MedicationImpl medication = new MedicationImpl(medicationId, medicationIllnessId, medicationName);
						examData.getTreatment().add(medication);
					}
				}
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
			+ " ExaminationResults.treatmentDescription, ExaminationResults.comments, "
			+ " ExaminationResults.examinationDateYear, ExaminationResults.examinationDateMonth, ExaminationResults.examinationDateDay, ExaminationResults.illnessPhase, "
			+ " ExaminationResults.typeName, CariotypeExamResults.id, CariotypeExamResults.nomenclaturalDescription, CariotypeExamResults.comments, "
			+ " FishExamResults.id, FishExamResults.nomenclaturalDescription, FishExamResults.comments ";

	private static final String SELECT_MEDICATION_PART = " , Medication.id, Medication.name, Medication.illnessId ";

	private static final String FROM_STATEMENT = " FROM ExaminationResults "
			+ " LEFT JOIN CariotypeExamResults ON CariotypeExamResults.examinationResultsId = ExaminationResults.id "
			+ " LEFT JOIN FishExamResults ON FishExamResults.examinationResultsId = ExaminationResults.id ";

	private static final String JOIN_MEDICATIONS_STATEMENT = " LEFT JOIN MedicationToExaminationResults ON MedicationToExaminationResults.examinationId = ExaminationResults.id "
			+ " LEFT JOIN Medication ON Medication.id = MedicationToExaminationResults.medicationId ";

	private String getSelectStatement(boolean fetchMedications) {
		return fetchMedications ? SELECT_STATEMENT + SELECT_MEDICATION_PART + FROM_STATEMENT + JOIN_MEDICATIONS_STATEMENT : SELECT_STATEMENT + FROM_STATEMENT;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see x.mvmn.patienceajdbc.dao.IExaminationDao#get(long)
	 */
	public ExaminationDataImpl get(final long id, boolean fetchMedications) {
		ExaminationDataImpl result = null;
		List<ExaminationDataImpl> results = jdbcTemplate.query(getSelectStatement(fetchMedications) + " WHERE ExaminationResults.id = ?", new Object[] { id },
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
	public ExaminationDataImpl getByNumber(final int number, boolean fetchMedications) {
		ExaminationDataImpl result = null;
		List<ExaminationDataImpl> results = jdbcTemplate.query(getSelectStatement(fetchMedications) + " WHERE ExaminationResults.number = ?",
				new Object[] { number }, EXAMINATION_DATA_ROW_MAPPER);
		if (results != null && results.size() > 0) {
			result = results.get(0);
		}
		return result;
	}

	public List<ExaminationDataImpl> getByPatient(final long patientId, boolean fetchMedications) {
		List<ExaminationDataImpl> results = jdbcTemplate.query(getSelectStatement(fetchMedications) + " WHERE ExaminationResults.patientId = ?",
				new Object[] { patientId }, EXAMINATION_DATA_ROW_MAPPER);
		return results;
	}

	public List<ExaminationDataImpl> getByIllness(final long illnessId, boolean fetchMedications) {
		List<ExaminationDataImpl> results = jdbcTemplate.query(getSelectStatement(fetchMedications) + " WHERE ExaminationResults.illnessId = ?",
				new Object[] { illnessId }, EXAMINATION_DATA_ROW_MAPPER);
		return results;
	}

	public List<ExaminationDataImpl> getByPatientAndIllness(final long patientId, final long illnessId, boolean fetchMedications) {
		List<ExaminationDataImpl> results = jdbcTemplate.query(getSelectStatement(fetchMedications)
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
	@Transactional
	public ExaminationDataImpl create(final long patientId, final long illnessId, final int number, final String matherial, final String blood,
			final String mielogramm, final String treatmentDescription, final String comments, final Integer examinationDateYear,
			final Integer examinationDateMonth, final Integer examinationDateDay, final IllnessPhase illnessPhase) {
		final ExaminationDataImpl result;

		// final String subtypeTable;
		/*
		 * CariotypeExaminationResultsImpl cariotypeExaminationResultsImpl = null; FishExaminationResultsImpl fishExaminationResultsImpl = null; final
		 * AbstractExaminationResultsImpl examinationResultsImpl; if ("FISH".equalsIgnoreCase(typeName)) { subtypeTable = "FishExamResults";
		 * fishExaminationResultsImpl = new FishExaminationResultsImpl(); examinationResultsImpl = fishExaminationResultsImpl; } else if
		 * ("Cariotype".equalsIgnoreCase(typeName)) { subtypeTable = "CariotypeExamResults"; cariotypeExaminationResultsImpl = new
		 * CariotypeExaminationResultsImpl(); examinationResultsImpl = cariotypeExaminationResultsImpl; } else { subtypeTable = null; examinationResultsImpl =
		 * null; }
		 */
		FishExaminationResultsImpl fishExaminationResultsImpl = new FishExaminationResultsImpl();
		CariotypeExaminationResultsImpl cariotypeExaminationResultsImpl = new CariotypeExaminationResultsImpl();

		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {

			@SuppressWarnings("deprecation")
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement prepStatement = con
						.prepareStatement(
								"insert into ExaminationResults(patientId, illnessId, number, matherial, blood, mielogramm, treatmentDescription, comments, examinationDateYear, examinationDateMonth, examinationDateDay, illnessPhase, typeName, examinationDate) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
								Statement.RETURN_GENERATED_KEYS);
				prepStatement.setLong(1, patientId);
				prepStatement.setLong(2, illnessId);
				prepStatement.setInt(3, number);
				prepStatement.setString(4, matherial);
				prepStatement.setString(5, blood);
				prepStatement.setString(6, mielogramm);
				prepStatement.setString(7, treatmentDescription);
				prepStatement.setString(8, comments);
				prepStatement.setInt(9, examinationDateYear != null ? examinationDateYear.intValue() : 0);
				prepStatement.setInt(10, examinationDateMonth != null ? examinationDateMonth.intValue() : 0);
				prepStatement.setInt(11, examinationDateDay != null ? examinationDateDay.intValue() : 0);
				prepStatement.setString(12, illnessPhase.name());
				prepStatement.setString(13, "");
				Date examinationDate = null;
				if (examinationDateYear != null) {
					examinationDate = new Date(examinationDateYear.intValue() - 1900, examinationDateMonth != null ? examinationDateMonth.intValue() - 1 : 0,
							examinationDateDay != null ? examinationDateDay.intValue() : 1);
				}
				prepStatement.setDate(14, examinationDate);
				return prepStatement;
			}
		}, keyHolder);

		final Number key = keyHolder.getKey();
		final long examinationDataDbId = key.longValue();

		// if (subtypeTable != null) {
		Map<String, AbstractExaminationResultsImpl> examinationResultsMap = new HashMap<String, AbstractExaminationResultsImpl>();
		examinationResultsMap.put("CariotypeExamResults", cariotypeExaminationResultsImpl);
		examinationResultsMap.put("FishExamResults", fishExaminationResultsImpl);
		for (Map.Entry<String, AbstractExaminationResultsImpl> entry : examinationResultsMap.entrySet()) {
			final String subtypeTable = entry.getKey();
			AbstractExaminationResultsImpl examinationResultsImpl = entry.getValue();
			KeyHolder subtypeTableKeyHolder = new GeneratedKeyHolder();
			jdbcTemplate.update(new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement prepStatement = con.prepareStatement("insert into " + subtypeTable
							+ "(examinationResultsId, nomenclaturalDescription, comments) values (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
					prepStatement.setLong(1, examinationDataDbId);
					prepStatement.setString(2, "");
					prepStatement.setString(3, "");
					return prepStatement;
				}
			}, subtypeTableKeyHolder);

			final Number subtypeTableKey = subtypeTableKeyHolder.getKey();
			final long examinationResultsDbId = subtypeTableKey.longValue();

			examinationResultsImpl.setId(examinationResultsDbId);
			examinationResultsImpl.setNomenclaturalDescription("");
			examinationResultsImpl.setComments("");
		}

		result = new ExaminationDataImpl();
		result.setId(examinationDataDbId);
		result.setCariotypeExaminationResults(cariotypeExaminationResultsImpl);
		result.setFishExaminationResults(fishExaminationResultsImpl);
		result.setBlood(blood);
		result.setComments(comments);
		result.setExaminationDateYear(examinationDateYear);
		result.setExaminationDateMonth(examinationDateMonth);
		result.setExaminationDateDay(examinationDateDay);
		result.setIllnessId(illnessId);
		result.setMatherial(matherial);
		result.setMielogramm(mielogramm);
		result.setNumber(number);
		result.setPatientId(patientId);
		result.setPhase(illnessPhase);
		result.setTreatmentDescription(treatmentDescription);

		result.setTreatment(new ArrayList<Medication>());
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
		jdbcTemplate.update("delete from MedicationToExaminationResults where examinationId = ?", examinationId);
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

	@Override
	@Transactional
	public void update(final ExaminationData examData, final boolean updateMedications) {
		examData.getCariotypeExaminationResults().getId();

		jdbcTemplate.update(new PreparedStatementCreator() {
			@SuppressWarnings("deprecation")
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement prepStatement = con
						.prepareStatement("update ExaminationResults set patientId=?, illnessId=?, number=?, matherial=?, blood=?, mielogramm=?, treatmentDescription=?, comments=?, examinationDateYear=?, examinationDateMonth=?, examinationDateDay=?, illnessPhase=?, typeName=?, examinationDate=? where id = ?");
				prepStatement.setLong(1, examData.getPatientId());
				prepStatement.setLong(2, examData.getIllnessId());
				prepStatement.setInt(3, examData.getNumber());
				prepStatement.setString(4, examData.getMatherial());
				prepStatement.setString(5, examData.getBlood());
				prepStatement.setString(6, examData.getMielogramm());
				prepStatement.setString(7, examData.getTreatmentDescription());
				prepStatement.setString(8, examData.getComments());
				prepStatement.setInt(9, examData.getExaminationDateYear() != null ? examData.getExaminationDateYear().intValue() : 0);
				prepStatement.setInt(10, examData.getExaminationDateMonth() != null ? examData.getExaminationDateMonth().intValue() : 0);
				prepStatement.setInt(11, examData.getExaminationDateDay() != null ? examData.getExaminationDateDay().intValue() : 0);

				prepStatement.setString(12, examData.getPhase().name());
				prepStatement.setString(13, "");
				Date examinationDate = null;
				if (examData.getExaminationDateYear() != null) {
					examinationDate = new Date(examData.getExaminationDateYear().intValue() - 1900, examData.getExaminationDateMonth() != null ? examData
							.getExaminationDateMonth().intValue() - 1 : 0, examData.getExaminationDateDay() != null ? examData.getExaminationDateDay()
							.intValue() : 1);
				}
				prepStatement.setDate(14, examinationDate);
				prepStatement.setLong(15, examData.getId());
				return prepStatement;
			}
		});

		jdbcTemplate.update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement prepStatement = con.prepareStatement("update FishExamResults set nomenclaturalDescription=?, comments=? where id = ?");

				prepStatement.setString(1, examData.getFishExaminationResults().getNomenclaturalDescription());
				prepStatement.setString(2, examData.getFishExaminationResults().getComments());
				prepStatement.setLong(3, examData.getFishExaminationResults().getId());
				return prepStatement;
			}
		});

		jdbcTemplate.update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement prepStatement = con.prepareStatement("update CariotypeExamResults set nomenclaturalDescription=?, comments=? where id = ?");

				prepStatement.setString(1, examData.getCariotypeExaminationResults().getNomenclaturalDescription());
				prepStatement.setString(2, examData.getCariotypeExaminationResults().getComments());
				prepStatement.setLong(3, examData.getCariotypeExaminationResults().getId());
				return prepStatement;
			}
		});

		if (updateMedications) {
			jdbcTemplate.update("delete from MedicationToExaminationResults where examinationId = ?", examData.getId());
			for (Medication medication : examData.getTreatment()) {
				jdbcTemplate.update("insert into MedicationToExaminationResults (examinationId, medicationId) values (?, ?)", examData.getId(),
						medication.getId());
			}
		}
	}

	@Override
	public long getLastExaminationNumber() {
		return jdbcTemplate.queryForLong("select max(number) from ExaminationResults");
	}

}
