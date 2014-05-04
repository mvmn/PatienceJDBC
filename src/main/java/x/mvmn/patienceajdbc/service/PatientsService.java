package x.mvmn.patienceajdbc.service;

import java.util.Date;
import java.util.List;

import x.mvmn.patienceajdbc.model.PatientData;
import x.mvmn.patienceajdbc.model.PatientStatsData;

public interface PatientsService {

	public PatientData create(final String lastName, final String firstName, final String patronymicName, final String address, final Integer birthDateYear,
			final Integer birthDateMonth, final Integer birthDateDay, final Integer diagnosisDateYear, final Integer diagnosisDateMonth,
			final Integer diagnosisDateDay, final Integer deathDateYear, final Integer deathDateMonth, final Integer deathDateDay, final boolean dead,
			final String anamnesis);

	public long findCount(String lastName, String firstName, String patronymicName, String address, final Integer birthDateYear, final Integer birthDateMonth,
			final Integer birthDateDay, final Integer diagnosisDateYear, final Integer diagnosisDateMonth, final Integer diagnosisDateDay,
			final Integer deathDateYear, final Integer deathDateMonth, final Integer deathDateDay, Boolean dead, String anamnesis);

	public List<PatientData> find(String lastName, String firstName, String patronymicName, String address, final Integer birthDateYear,
			final Integer birthDateMonth, final Integer birthDateDay, final Integer diagnosisDateYear, final Integer diagnosisDateMonth,
			final Integer diagnosisDateDay, final Integer deathDateYear, final Integer deathDateMonth, final Integer deathDateDay, Boolean dead,
			String anamnesis, boolean fetchMedications);

	public List<PatientData> list(final String orderBy, final int start, final int count, final boolean fetchMedications);

	public PatientData get(final long patientId, final boolean fetchMedications);

	public boolean update(final PatientData patientData, final boolean updateMedications);

	public boolean delete(final PatientData patientData);

	public long countAll();

	public void delete(final long[] patientIds);

	public boolean delete(final long patientId);

	public List<PatientStatsData> listStats(final long illnessId, final Date visitDateFrom, final Date visitDateTo, int sortColumn);

}
