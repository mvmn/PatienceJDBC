package x.mvmn.patienceajdbc.service;

import java.util.Date;
import java.util.List;

import x.mvmn.patienceajdbc.model.PatientData;
import x.mvmn.patienceajdbc.model.PatientStatsData;

public interface PatientsService {

	public PatientData create(String lastName, String firstName, String patronymicName, String address, Date birthDate, Date diagnoseDate, Date deathDate,
			boolean dead, String anamnesis);

	public long findCount(final String lastName, final String firstName, final String patronymicName, final String address, final Date birthDate,
			final Date diagnoseDate, final Date deathDate, final Boolean dead, final String anamnesis);

	public List<PatientData> find(final String lastName, final String firstName, final String patronymicName, final String address, final Date birthDate,
			final Date diagnoseDate, final Date deathDate, final Boolean dead, final String anamnesis, final boolean fetchMedications);

	public List<PatientData> list(final String orderBy, final int start, final int count, final boolean fetchMedications);

	public PatientData get(final long patientId, final boolean fetchMedications);

	public boolean update(final PatientData patientData, final boolean updateMedications);

	public boolean delete(final PatientData patientData);

	public long countAll();

	public void delete(final long[] patientIds);

	public boolean delete(final long patientId);

	public List<PatientStatsData> listStats(final long illnessId, final Date visitDateFrom, final Date visitDateTo, int sortColumn);

}
