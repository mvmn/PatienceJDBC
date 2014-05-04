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
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Transactional;

import x.mvmn.patienceajdbc.dao.PatientDao;
import x.mvmn.patienceajdbc.model.Medication;
import x.mvmn.patienceajdbc.model.PatientData;
import x.mvmn.patienceajdbc.model.PatientStatsData;
import x.mvmn.patienceajdbc.model.impl.MedicationImpl;
import x.mvmn.patienceajdbc.model.impl.PatientDataImpl;
import x.mvmn.patienceajdbc.model.impl.PatientStatsDataImpl;

public class PatientDaoImpl implements PatientDao {

	private static class PatientDataResultSetExtractor implements ResultSetExtractor<List<PatientDataImpl>> {

		public List<PatientDataImpl> extractData(ResultSet rs) throws SQLException {
			List<PatientDataImpl> result = new ArrayList<PatientDataImpl>();

			PatientDataImpl patientDataImpl = null;
			while (rs.next()) {
				long dbId = rs.getLong(1);
				if (patientDataImpl == null || dbId != patientDataImpl.getId()) {
					patientDataImpl = new PatientDataImpl();
					String lastName = rs.getString(2);
					String firstName = rs.getString(3);
					String patronymicName = rs.getString(4);
					String address = rs.getString(5);
					boolean dead = rs.getBoolean(6);
					String anamnesis = rs.getString(7);

					Integer birthDateYear = rs.getInt(8) > 0 ? rs.getInt(8) : null;
					Integer birthDateMonth = rs.getInt(9) > 0 ? rs.getInt(9) : null;
					Integer birthDateDay = rs.getInt(10) > 0 ? rs.getInt(10) : null;

					Integer diagnosisDateYear = rs.getInt(11) > 0 ? rs.getInt(11) : null;
					Integer diagnosisDateMonth = rs.getInt(12) > 0 ? rs.getInt(12) : null;
					Integer diagnosisDateDay = rs.getInt(13) > 0 ? rs.getInt(13) : null;

					Integer deathDateYear = rs.getInt(14) > 0 ? rs.getInt(14) : null;
					Integer deathDateMonth = rs.getInt(15) > 0 ? rs.getInt(15) : null;
					Integer deathDateDay = rs.getInt(16) > 0 ? rs.getInt(16) : null;

					patientDataImpl.setId(dbId);
					patientDataImpl.setAddress(address);
					patientDataImpl.setAnamnesis(anamnesis);

					patientDataImpl.setBirthDateYear(birthDateYear);
					patientDataImpl.setBirthDateMonth(birthDateMonth);
					patientDataImpl.setBirthDateDay(birthDateDay);

					patientDataImpl.setDiagnosisDateYear(diagnosisDateYear);
					patientDataImpl.setDiagnosisDateMonth(diagnosisDateMonth);
					patientDataImpl.setDiagnosisDateDay(diagnosisDateDay);

					patientDataImpl.setDeathDateYear(deathDateYear);
					patientDataImpl.setDeathDateMonth(deathDateMonth);
					patientDataImpl.setDeathDateDay(deathDateDay);

					patientDataImpl.setDead(dead);
					patientDataImpl.setFirstName(firstName);
					patientDataImpl.setLastName(lastName);
					patientDataImpl.setPatronymicName(patronymicName);
					patientDataImpl.setPreviousTreatments(new ArrayList<Medication>());
					result.add(patientDataImpl);
				}

				if (rs.getMetaData().getColumnCount() > 16) {
					long medicationId = rs.getLong(17);
					if (medicationId > 0) {
						String medicationName = rs.getString(18);
						long illnessId = rs.getLong(19);

						MedicationImpl medication = new MedicationImpl(medicationId, illnessId, medicationName);
						patientDataImpl.getPreviousTreatments().add(medication);
					}
				}
			}

			return result;
		}
	}

	private static final PatientDataResultSetExtractor PATIENT_DATA_RESULT_SET_EXTRACTOR = new PatientDataResultSetExtractor();

	private final JdbcTemplate jdbcTemplate;

	public PatientDaoImpl(final JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see x.mvmn.patienceajdbc.dao.impl.PatientDao#create(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.util.Date,
	 * java.util.Date, java.util.Date, boolean, java.lang.String)
	 */
	public PatientDataImpl create(final String lastName, final String firstName, final String patronymicName, final String address,
			final Integer birthDateYear, final Integer birthDateMonth, final Integer birthDateDay, final Integer diagnosisDateYear,
			final Integer diagnosisDateMonth, final Integer diagnosisDateDay, final Integer deathDateYear, final Integer deathDateMonth,
			final Integer deathDateDay, final boolean dead, final String anamnesis) {
		PatientDataImpl result;

		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {

			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				String statement = "insert into PatientData(lastName, firstName, patronymicName, address, " + " birthDateYear, birthDateMonth, birthDateDay, "
						+ " diagnosisDateYear, diagnosisDateMonth, diagnosisDateDay, " + " deathDateYear, deathDateMonth, deathDateDay, "
						+ " dead, anamnesis) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
				PreparedStatement prepStatement = con.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS);

				prepStatement.setString(1, lastName);
				prepStatement.setString(2, firstName);
				prepStatement.setString(3, patronymicName);
				prepStatement.setString(4, address);
				prepStatement.setInt(5, birthDateYear == null ? 0 : birthDateYear.intValue());
				prepStatement.setInt(6, birthDateMonth == null ? 0 : birthDateMonth.intValue());
				prepStatement.setInt(7, birthDateDay == null ? 0 : birthDateDay.intValue());
				prepStatement.setInt(8, diagnosisDateYear == null ? 0 : diagnosisDateYear.intValue());
				prepStatement.setInt(9, diagnosisDateMonth == null ? 0 : diagnosisDateMonth.intValue());
				prepStatement.setInt(10, diagnosisDateDay == null ? 0 : diagnosisDateDay.intValue());
				prepStatement.setInt(11, deathDateYear == null ? 0 : deathDateYear.intValue());
				prepStatement.setInt(12, deathDateMonth == null ? 0 : deathDateMonth.intValue());
				prepStatement.setInt(13, deathDateDay == null ? 0 : deathDateDay.intValue());
				prepStatement.setBoolean(14, dead);
				prepStatement.setString(15, anamnesis);
				return prepStatement;
			}
		}, keyHolder);
		final Number key = keyHolder.getKey();
		final long dbId = key.longValue();

		// result = new PatientDataImpl();
		// result.setId(dbId);
		// result.setLastName(lastName);
		// result.setFirstName(firstName);
		// result.setPatronymicName(patronymicName);
		// result.setAddress(address);
		// result.setAnamnesis(anamnesis);
		// result.setDateOfBirth(birthDate);
		// result.setDateOfDiagnosis(diagnosisDate);
		// result.setDateOfDeath(deathDate);
		// result.setDead(dead);
		result = this.get(dbId, false);

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see x.mvmn.patienceajdbc.dao.impl.PatientDao#findCount(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.util.Date,
	 * java.util.Date, java.util.Date, java.lang.Boolean, java.lang.String)
	 */
	public long findCount(final String lastName, final String firstName, final String patronymicName, final String address, final Integer birthDateYear,
			final Integer birthDateMonth, final Integer birthDateDay, final Integer diagnosisDateYear, final Integer diagnosisDateMonth,
			final Integer diagnosisDateDay, final Integer deathDateYear, final Integer deathDateMonth, final Integer deathDateDay, final Boolean dead,
			final String anamnesis) {
		SearchConditionWithParameters searchConditionWithParams = buildSearchCondition(lastName, firstName, patronymicName, address, birthDateYear,
				birthDateMonth, birthDateDay, diagnosisDateYear, diagnosisDateMonth, diagnosisDateDay, deathDateYear, deathDateMonth, deathDateDay, dead,
				anamnesis);
		return jdbcTemplate.queryForLong("SELECT count(id) FROM PatientData WHERE " + searchConditionWithParams.getCondition(),
				searchConditionWithParams.getParameters());
	}

	protected String getSelectQueryStringPart(final boolean fetchMedications) {
		String result = "SELECT PatientData.id, PatientData.lastName, PatientData.firstName, PatientData.patronymicName, PatientData.address, PatientData.dead, PatientData.anamnesis, "
				+ " PatientData.birthDateYear, PatientData.birthDateMonth, PatientData.birthDateDay, PatientData.diagnosisDateYear, PatientData.diagnosisDateMonth, PatientData.diagnosisDateDay, PatientData.deathDateYear, PatientData.deathDateMonth, PatientData.deathDateDay ";
		if (fetchMedications) {
			result += ", Medication.id, Medication.name, Medication.illnessId"
					+ " FROM PatientData LEFT JOIN MedicationToPatient ON MedicationToPatient.patientId = PatientData.id LEFT JOIN Medication ON Medication.id = MedicationToPatient.medicationId ";
		} else {
			result += " FROM PatientData ";
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see x.mvmn.patienceajdbc.dao.impl.PatientDao#find(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.util.Date,
	 * java.util.Date, java.util.Date, java.lang.Boolean, java.lang.String, boolean)
	 */
	public List<PatientDataImpl> find(final String lastName, final String firstName, final String patronymicName, final String address,
			final Integer birthDateYear, final Integer birthDateMonth, final Integer birthDateDay, final Integer diagnosisDateYear,
			final Integer diagnosisDateMonth, final Integer diagnosisDateDay, final Integer deathDateYear, final Integer deathDateMonth,
			final Integer deathDateDay, final Boolean dead, final String anamnesis, final boolean fetchMedications) {
		SearchConditionWithParameters searchConditionWithParams = buildSearchCondition(lastName, firstName, patronymicName, address, birthDateYear,
				birthDateMonth, birthDateDay, diagnosisDateYear, diagnosisDateMonth, diagnosisDateDay, deathDateYear, deathDateMonth, deathDateDay, dead,
				anamnesis);
		List<PatientDataImpl> results = jdbcTemplate.query(getSelectQueryStringPart(fetchMedications) + "  WHERE " + searchConditionWithParams.getCondition(),
				searchConditionWithParams.getParameters(), PATIENT_DATA_RESULT_SET_EXTRACTOR);
		return results;
	}

	protected static class SearchConditionWithParameters {

		private final String condition;
		private final Object[] parameters;

		protected SearchConditionWithParameters(final String condition, final Object[] parameters) {
			super();
			this.condition = condition;
			this.parameters = parameters;
		}

		public String getCondition() {
			return condition;
		}

		public Object[] getParameters() {
			return parameters;
		}

	}

	protected SearchConditionWithParameters buildSearchCondition(final String lastName, final String firstName, final String patronymicName,
			final String address, final Integer birthDateYear, final Integer birthDateMonth, final Integer birthDateDay, final Integer diagnosisDateYear,
			final Integer diagnosisDateMonth, final Integer diagnosisDateDay, final Integer deathDateYear, final Integer deathDateMonth,
			final Integer deathDateDay, final Boolean dead, final String anamnesis) {
		List<Object> params = new ArrayList<Object>(10);
		StringBuilder stringBuilder = new StringBuilder();

		if (lastName != null) {
			stringBuilder.append(" lastName = ? ");
			params.add(lastName);
		}
		if (firstName != null) {
			if (stringBuilder.length() > 0) {
				stringBuilder.append(" AND ");
			}
			stringBuilder.append(" firstName = ? ");
			params.add(firstName);
		}

		if (patronymicName != null) {
			if (stringBuilder.length() > 0) {
				stringBuilder.append(" AND ");
			}
			stringBuilder.append(" patronymicName = ? ");
			params.add(patronymicName);
		}

		if (address != null) {
			if (stringBuilder.length() > 0) {
				stringBuilder.append(" AND ");
			}
			stringBuilder.append(" address like ? ");
			params.add(address);
		}

		if (birthDateYear != null) {
			if (stringBuilder.length() > 0) {
				stringBuilder.append(" AND ");
			}
			stringBuilder.append(" birthDateYear = ? ");
			params.add(birthDateYear);
		}
		if (diagnosisDateYear != null) {
			if (stringBuilder.length() > 0) {
				stringBuilder.append(" AND ");
			}
			stringBuilder.append(" diagnosisDateYear = ? ");
			params.add(diagnosisDateYear);
		}
		if (deathDateYear != null) {
			if (stringBuilder.length() > 0) {
				stringBuilder.append(" AND ");
			}
			stringBuilder.append(" deathDateYear = ? ");
			params.add(deathDateYear);
		}

		if (birthDateMonth != null) {
			if (stringBuilder.length() > 0) {
				stringBuilder.append(" AND ");
			}
			stringBuilder.append(" birthDateMonth = ? ");
			params.add(birthDateMonth);
		}
		if (diagnosisDateMonth != null) {
			if (stringBuilder.length() > 0) {
				stringBuilder.append(" AND ");
			}
			stringBuilder.append(" diagnosisDateMonth = ? ");
			params.add(diagnosisDateMonth);
		}
		if (deathDateMonth != null) {
			if (stringBuilder.length() > 0) {
				stringBuilder.append(" AND ");
			}
			stringBuilder.append(" deathDateMonth = ? ");
			params.add(deathDateMonth);
		}

		if (birthDateDay != null) {
			if (stringBuilder.length() > 0) {
				stringBuilder.append(" AND ");
			}
			stringBuilder.append(" deathDateDay = ? ");
			params.add(deathDateDay);
		}
		if (diagnosisDateDay != null) {
			if (stringBuilder.length() > 0) {
				stringBuilder.append(" AND ");
			}
			stringBuilder.append(" diagnosisDateDay = ? ");
			params.add(diagnosisDateDay);
		}
		if (deathDateDay != null) {
			if (stringBuilder.length() > 0) {
				stringBuilder.append(" AND ");
			}
			stringBuilder.append(" deathDateDay = ? ");
			params.add(deathDateDay);
		}

		if (dead != null) {
			if (stringBuilder.length() > 0) {
				stringBuilder.append(" AND ");
			}
			stringBuilder.append(" dead = ? ");
			params.add(dead);
		}

		if (anamnesis != null) {
			if (stringBuilder.length() > 0) {
				stringBuilder.append(" AND ");
			}
			stringBuilder.append(" anamnesis like ? ");
			params.add(anamnesis);
		}

		return new SearchConditionWithParameters(stringBuilder.toString(), params.toArray());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see x.mvmn.patienceajdbc.dao.impl.PatientDao#list(java.lang.String, int, int, boolean)
	 */
	public List<PatientDataImpl> list(final String orderBy, final int start, final int count, final boolean fetchMedications) {

		StringBuilder oderAndLimit = new StringBuilder();
		List<Object> params = new ArrayList<Object>(3);
		if (orderBy != null && orderBy.trim().length() > 0) {
			oderAndLimit.append(" ORDER BY ? ");
			params.add(orderBy);
		}
		if (start >= 0 && count >= 0) {
			oderAndLimit.append(" LIMIT ?,? ");
			params.add(start);
			params.add(count);
		}

		List<PatientDataImpl> results = jdbcTemplate.query(getSelectQueryStringPart(fetchMedications) + oderAndLimit.toString(), params.toArray(),
				PATIENT_DATA_RESULT_SET_EXTRACTOR);
		return results;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see x.mvmn.patienceajdbc.dao.impl.PatientDao#get(long, boolean)
	 */
	public PatientDataImpl get(final long patientId, final boolean fetchMedications) {
		PatientDataImpl result = null;
		List<PatientDataImpl> results = jdbcTemplate.query(getSelectQueryStringPart(fetchMedications) + " where PatientData.id = ?",
				new Object[] { patientId }, PATIENT_DATA_RESULT_SET_EXTRACTOR);
		if (results != null && results.size() > 0) {
			result = results.get(0);
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see x.mvmn.patienceajdbc.dao.impl.PatientDao#update(x.mvmn.patienceajdbc. model.PatientData, boolean)
	 */
	@Transactional
	public boolean update(final PatientData patientData, final boolean updateMedications) {

		int updatedRowsCount = jdbcTemplate
				.update("update PatientData set lastName=?, firstName=?, patronymicName=?, address=?, dead=?, anamnesis=?, birthDateYear=?, birthDateMonth=?, birthDateDay=?, diagnosisDateYear=?, diagnosisDateMonth=?, diagnosisDateDay=?, deathDateYear=?, deathDateMonth=?, deathDateDay=? where id = ?",
						patientData.getLastName(), patientData.getFirstName(), patientData.getPatronymicName(), patientData.getAddress(), patientData.isDead(),
						patientData.getAnamnesis(), patientData.getBirthDateYear(), patientData.getBirthDateMonth(), patientData.getBirthDateDay(),
						patientData.getDiagnosisDateYear(), patientData.getDiagnosisDateMonth(), patientData.getDiagnosisDateDay(),
						patientData.getDeathDateYear(), patientData.getDeathDateMonth(), patientData.getDeathDateDay(), patientData.getId());

		if (updateMedications) {
			jdbcTemplate.update("delete from MedicationToPatient where patientId = ?", patientData.getId());
			for (Medication medication : patientData.getPreviousTreatments()) {
				jdbcTemplate.update("insert into MedicationToPatient (patientId, medicationId) values (?, ?)", patientData.getId(), medication.getId());
			}
		}

		return updatedRowsCount > 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see x.mvmn.patienceajdbc.dao.impl.PatientDao#delete(x.mvmn.patienceajdbc. model.PatientData)
	 */
	public boolean delete(final PatientData patientData) {
		return delete(patientData.getId());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see x.mvmn.patienceajdbc.dao.impl.PatientDao#countAll()
	 */
	public long countAll() {
		return jdbcTemplate.queryForLong("select count(id) from PatientData");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see x.mvmn.patienceajdbc.dao.impl.PatientDao#delete(long)
	 */
	@Transactional
	public boolean delete(final long patientId) {
		boolean result = false;

		// jdbcTemplate.execute("START TRANSACTION");
		// try {
		jdbcTemplate.update("delete from MedicationToPatient where patientId = ?", patientId);
		jdbcTemplate
				.update("delete from CariotypeExamResults where examinationResultsId in (select id from ExaminationResults where patientId = ?)", patientId);
		jdbcTemplate.update("delete from FishExamResults where examinationResultsId in (select id from ExaminationResults where patientId = ?)", patientId);
		jdbcTemplate.update("delete from ExaminationResultsToTag where examinationResultsId in (select id from ExaminationResults where patientId = ?)",
				patientId);
		jdbcTemplate.update("delete from ExaminationResults where patientId = ?", patientId);
		result = jdbcTemplate.update("delete from PatientData where id = ?", patientId) > 0;
		// jdbcTemplate.execute("COMMIT");
		// } catch (Exception e) {
		// try {
		// jdbcTemplate.execute("ROLLBACK");
		// } catch (Exception ex) {
		// ex.printStackTrace();
		// }
		// throw new RuntimeException(e);
		// }

		return result;
	}

	private static class PatientStatsDataResultSetExtractor implements ResultSetExtractor<List<PatientStatsData>> {

		public List<PatientStatsData> extractData(ResultSet rs) throws SQLException {
			List<PatientStatsData> result = new ArrayList<PatientStatsData>();
			while (rs.next()) {
				long dbId = rs.getLong(1);
				String firstName = rs.getString(2);
				String lastName = rs.getString(3);
				String patronymicName = rs.getString(4);
				StringBuilder fullNameBuilder = new StringBuilder();
				if (firstName != null) {
					fullNameBuilder.append(firstName).append(" ");
				}
				if (lastName != null) {
					fullNameBuilder.append(lastName).append(" ");
				}
				if (patronymicName != null) {
					fullNameBuilder.append(patronymicName).append(" ");
				}
				String fullName = fullNameBuilder.toString().trim();
				String address = rs.getString(5);
				String dateOfBirth = rs.getString(6);
				Date dateOfFirstExamination = rs.getDate(7);
				int visitsCount = rs.getInt(8);

				result.add(new PatientStatsDataImpl(dbId, fullName, dateOfBirth, address, dateOfFirstExamination, visitsCount));
			}
			return result;
		}
	}

	private final PatientStatsDataResultSetExtractor PATIENT_STATS_DATA_RESULT_SET_EXTRACTOR = new PatientStatsDataResultSetExtractor();

	/*
	 * (non-Javadoc)
	 * 
	 * @see x.mvmn.patienceajdbc.dao.impl.PatientDao#listStats(long, java.util.Date, java.util.Date, int)
	 */
	public List<PatientStatsData> listStats(final long illnessId, final Date visitDateFrom, final Date visitDateTo, int sortColumn) {
		String query = "select PatientData.id, PatientData.lastName, PatientData.firstName, PatientData.patronymicName, PatientData.address, CONCAT(COALESCE(LPAD(PatientData.birthDateYear, 4, '00'),'????'), '-', COALESCE(LPAD(PatientData.birthDateMonth, 2, '00'),'??'), '-', COALESCE(LPAD(PatientData.birthDateDay, 2, '00'),'??')) as birthDate, max(ExaminationResults.examinationDate) as lastVisitDate, count(ExaminationResults.id) as visitsCount from PatientData left join ExaminationResults on ExaminationResults.patientId = PatientData.id where (1=1) ";
		List<Object> paramsList = new ArrayList<Object>(3);
		if (illnessId >= 0) {
			paramsList.add(illnessId);
			query += " AND (ExaminationResults.illnessId = ?) ";
		}
		if (visitDateFrom != null && visitDateTo != null) {
			query += " AND ExaminationResults.examinationDate>=? AND ExaminationResults.examinationDate<=? ";
			paramsList.add(visitDateFrom);
			paramsList.add(visitDateTo);
		} else if (visitDateFrom != null) {
			query += " AND ExaminationResults.examinationDate>=? ";
			paramsList.add(visitDateFrom);
		} else if (visitDateTo != null) {
			query += " AND ExaminationResults.examinationDate<=? ";
			paramsList.add(visitDateTo);
		}
		Object[] params = paramsList.toArray(new Object[paramsList.size()]);
		query += " group by PatientData.id, PatientData.lastName, PatientData.firstName, PatientData.patronymicName, PatientData.address, birthDate ";
		if (sortColumn > 0) {
			query += " order by ";
			switch (sortColumn) {
				case 1:
					query += " PatientData.lastName ";
				break;
				case 2:
					query += " birthDate ";
				break;
				case 3:
					query += " PatientData.address ";
				break;
				case 4:
					query += " lastVisitDate ";
				break;
				default:
					query += " visitsCount ";
				break;
			}
		}

		return jdbcTemplate.query(query, params, PATIENT_STATS_DATA_RESULT_SET_EXTRACTOR);
	}
}
