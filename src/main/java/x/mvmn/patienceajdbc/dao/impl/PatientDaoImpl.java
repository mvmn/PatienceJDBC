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
					Date dateOfBirth = rs.getDate(6);
					Date dateOfDiagnosis = rs.getDate(7);
					Date dateOfDeath = rs.getDate(8);
					boolean dead = rs.getBoolean(9);
					String anamnesis = rs.getString(10);

					patientDataImpl.setId(dbId);
					patientDataImpl.setAddress(address);
					patientDataImpl.setAnamnesis(anamnesis);
					patientDataImpl.setDateOfBirth(dateOfBirth);
					patientDataImpl.setDateOfDeath(dateOfDeath);
					patientDataImpl.setDateOfDiagnosis(dateOfDiagnosis);
					patientDataImpl.setDead(dead);
					patientDataImpl.setFirstName(firstName);
					patientDataImpl.setLastName(lastName);
					patientDataImpl.setPatronymicName(patronymicName);
					patientDataImpl.setPreviousTreatments(new ArrayList<Medication>());
					result.add(patientDataImpl);
				}

				if (rs.getMetaData().getColumnCount() > 10) {
					long medicationId = rs.getLong(11);
					if (medicationId > 0) {
						String medicationName = rs.getString(12);
						long illnessId = rs.getLong(13);

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
	 * @see x.mvmn.patienceajdbc.dao.impl.PatientDao#create(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String, java.util.Date,
	 * java.util.Date, java.util.Date, boolean, java.lang.String)
	 */
	public PatientDataImpl create(final String lastName, final String firstName, final String patronymicName, final String address, final Date birthDate,
			final Date diagnoseDate, final Date deathDate, final boolean dead, final String anamnesis) {
		PatientDataImpl result;

		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {

			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				String statement = "insert into PatientData(lastName, firstName, patronymicName, address, birthDate, diagnoseDate, deathDate, dead, anamnesis) values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
				PreparedStatement prepStatement = con.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS);

				prepStatement.setString(1, lastName);
				prepStatement.setString(2, firstName);
				prepStatement.setString(3, patronymicName);
				prepStatement.setString(4, address);
				prepStatement.setDate(5, birthDate == null ? null : new java.sql.Date(birthDate.getTime()));
				prepStatement.setDate(6, diagnoseDate == null ? null : new java.sql.Date(diagnoseDate.getTime()));
				prepStatement.setDate(7, deathDate == null ? null : new java.sql.Date(deathDate.getTime()));
				prepStatement.setBoolean(8, dead);
				prepStatement.setString(9, anamnesis);
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
		// result.setDateOfDiagnosis(diagnoseDate);
		// result.setDateOfDeath(deathDate);
		// result.setDead(dead);
		result = this.get(dbId, false);

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see x.mvmn.patienceajdbc.dao.impl.PatientDao#findCount(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String, java.util.Date,
	 * java.util.Date, java.util.Date, java.lang.Boolean, java.lang.String)
	 */
	public long findCount(final String lastName, final String firstName, final String patronymicName, final String address, final Date birthDate,
			final Date diagnoseDate, final Date deathDate, final Boolean dead, final String anamnesis) {
		SearchConditionWithParameters searchConditionWithParams = buildSearchCondition(lastName, firstName, patronymicName, address, birthDate, diagnoseDate,
				deathDate, dead, anamnesis);
		return jdbcTemplate.queryForLong("SELECT count(id) FROM PatientData WHERE " + searchConditionWithParams.getCondition(),
				searchConditionWithParams.getParameters());
	}

	protected String getSelectQueryStringPart(final boolean fetchMedications) {
		String result;
		if (fetchMedications) {
			result = "SELECT PatientData.id, PatientData.lastName, PatientData.firstName, PatientData.patronymicName, PatientData.address, PatientData.birthDate, PatientData.diagnoseDate, PatientData.deathDate, PatientData.dead, PatientData.anamnesis, Medication.id, Medication.name, Medication.illnessId"
					+ " FROM PatientData LEFT JOIN MedicationToPatient ON MedicationToPatient.patientId = PatientData.id LEFT JOIN Medication ON Medication.id = MedicationToPatient.medicationId ";
		} else {
			result = "SELECT PatientData.id, PatientData.lastName, PatientData.firstName, PatientData.patronymicName, PatientData.address, PatientData.birthDate, PatientData.diagnoseDate, PatientData.deathDate, PatientData.dead, PatientData.anamnesis FROM PatientData ";
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see x.mvmn.patienceajdbc.dao.impl.PatientDao#find(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String, java.util.Date,
	 * java.util.Date, java.util.Date, java.lang.Boolean, java.lang.String,
	 * boolean)
	 */
	public List<PatientDataImpl> find(final String lastName, final String firstName, final String patronymicName, final String address, final Date birthDate,
			final Date diagnoseDate, final Date deathDate, final Boolean dead, final String anamnesis, final boolean fetchMedications) {
		SearchConditionWithParameters searchConditionWithParams = buildSearchCondition(lastName, firstName, patronymicName, address, birthDate, diagnoseDate,
				deathDate, dead, anamnesis);
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
			final String address, final Date birthDate, final Date diagnoseDate, final Date deathDate, final Boolean dead, final String anamnesis) {
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

		if (birthDate != null) {
			if (stringBuilder.length() > 0) {
				stringBuilder.append(" AND ");
			}
			stringBuilder.append(" birthDate = ? ");
			params.add(birthDate);
		}

		if (diagnoseDate != null) {
			if (stringBuilder.length() > 0) {
				stringBuilder.append(" AND ");
			}
			stringBuilder.append(" diagnoseDate = ? ");
			params.add(diagnoseDate);
		}

		if (deathDate != null) {
			if (stringBuilder.length() > 0) {
				stringBuilder.append(" AND ");
			}
			stringBuilder.append(" deathDate = ? ");
			params.add(deathDate);
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
	 * @see x.mvmn.patienceajdbc.dao.impl.PatientDao#list(java.lang.String, int,
	 * int, boolean)
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
	 * @see
	 * x.mvmn.patienceajdbc.dao.impl.PatientDao#update(x.mvmn.patienceajdbc.
	 * model.PatientData, boolean)
	 */
	@Transactional
	public boolean update(final PatientData patientData, final boolean updateMedications) {

		int updatedRowsCount = jdbcTemplate
				.update("update PatientData set lastName=?, firstName=?, patronymicName=?, address=?, birthDate=?, diagnoseDate=?, deathDate=?, dead=?, anamnesis=? where id = ?",
						patientData.getLastName(), patientData.getFirstName(), patientData.getPatronymicName(), patientData.getAddress(),
						patientData.getDateOfBirth(), patientData.getDateOfDiagnosis(), patientData.getDateOfDeath(), patientData.isDead(),
						patientData.getAnamnesis(), patientData.getId());

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
	 * @see
	 * x.mvmn.patienceajdbc.dao.impl.PatientDao#delete(x.mvmn.patienceajdbc.
	 * model.PatientData)
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
				Date dateOfBirth = rs.getDate(6);
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
	 * @see x.mvmn.patienceajdbc.dao.impl.PatientDao#listStats(long,
	 * java.util.Date, java.util.Date, int)
	 */
	public List<PatientStatsData> listStats(final long illnessId, final Date visitDateFrom, final Date visitDateTo, int sortColumn) {
		String query = "select PatientData.id, PatientData.lastName, PatientData.firstName, PatientData.patronymicName, PatientData.address, PatientData.birthDate, max(ExaminationResults.examinationDate) as lastVisitDate, count(ExaminationResults.id) as visitsCount from PatientData left join ExaminationResults on ExaminationResults.patientId = PatientData.id where (1=1) ";
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
		query += " group by PatientData.id, PatientData.lastName, PatientData.firstName, PatientData.patronymicName, PatientData.address, PatientData.birthDate ";
		if (sortColumn > 0) {
			query += " order by ";
			switch (sortColumn) {
				case 1:
					query += " PatientData.lastName ";
				break;
				case 2:
					query += " PatientData.birthDate ";
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
