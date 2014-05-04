package x.mvmn.patienceajdbc.dao;

import java.util.Date;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import x.mvmn.patienceajdbc.model.PatientData;
import x.mvmn.patienceajdbc.model.PatientStatsData;
import x.mvmn.patienceajdbc.model.impl.PatientDataImpl;

public interface PatientDao extends DataAccessObject {

	public PatientDataImpl create(final String lastName, final String firstName, final String patronymicName, final String address,
			final Integer birthDateYear, final Integer birthDateMonth, final Integer birthDateDay, final Integer diagnosisDateYear,
			final Integer diagnosisDateMonth, final Integer diagnosisDateDay, final Integer deathDateYear, final Integer deathDateMonth,
			final Integer deathDateDay, final boolean dead, final String anamnesis);

	public long findCount(String lastName, String firstName, String patronymicName, String address, final Integer birthDateYear, final Integer birthDateMonth,
			final Integer birthDateDay, final Integer diagnosisDateYear, final Integer diagnosisDateMonth, final Integer diagnosisDateDay,
			final Integer deathDateYear, final Integer deathDateMonth, final Integer deathDateDay, Boolean dead, String anamnesis);

	public List<PatientDataImpl> find(String lastName, String firstName, String patronymicName, String address, final Integer birthDateYear,
			final Integer birthDateMonth, final Integer birthDateDay, final Integer diagnosisDateYear, final Integer diagnosisDateMonth,
			final Integer diagnosisDateDay, final Integer deathDateYear, final Integer deathDateMonth, final Integer deathDateDay, Boolean dead,
			String anamnesis, boolean fetchMedications);

	public List<PatientDataImpl> list(String orderBy, int start, int count, boolean fetchMedications);

	public PatientDataImpl get(long patientId, boolean fetchMedications);

	@Transactional
	public boolean update(PatientData patientData, boolean updateMedications);

	@Transactional
	public boolean delete(PatientData patientData);

	public long countAll();

	@Transactional
	public boolean delete(long patientId);

	/**
	 * @param illnessId
	 *            mandatory parameter
	 * @param searchPhrase
	 *            null for no search phrase
	 * @param visitDateFrom
	 *            null for no start date limit
	 * @param visitDateTo
	 *            null for no end date limit
	 * @param sortColumn
	 *            -1 for default
	 * @return
	 */
	public List<PatientStatsData> listStats(long illnessId, Date visitDateFrom, Date visitDateTo, int sortColumn);

}