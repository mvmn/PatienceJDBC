package x.mvmn.patienceajdbc.service.impl;

import java.util.Date;
import java.util.List;

import x.mvmn.patienceajdbc.dao.PatientDao;
import x.mvmn.patienceajdbc.model.PatientData;
import x.mvmn.patienceajdbc.model.PatientStatsData;
import x.mvmn.patienceajdbc.service.PatientsService;

public class PatientsServiceImpl implements PatientsService {

	final PatientDao patientDao;

	public PatientsServiceImpl(final PatientDao patientDao) {
		this.patientDao = patientDao;
	}

	public PatientData create(String lastName, String firstName, String patronymicName, String address, Date birthDate, Date diagnoseDate, Date deathDate,
			boolean dead, String anamnesis) {
		return patientDao.create(lastName, firstName, patronymicName, address, birthDate, diagnoseDate, deathDate, dead, anamnesis);
	}

	public long findCount(String lastName, String firstName, String patronymicName, String address, Date birthDate, Date diagnoseDate, Date deathDate,
			Boolean dead, String anamnesis) {
		return patientDao.findCount(lastName, firstName, patronymicName, address, birthDate, diagnoseDate, deathDate, dead, anamnesis);
	}

	@SuppressWarnings("unchecked")
	public List<PatientData> find(String lastName, String firstName, String patronymicName, String address, Date birthDate, Date diagnoseDate, Date deathDate,
			Boolean dead, String anamnesis, boolean fetchMedications) {
		return (List<PatientData>) (List<? extends PatientData>) patientDao.find(lastName, firstName, patronymicName, address, birthDate, diagnoseDate,
				deathDate, dead, anamnesis, fetchMedications);
	}

	@SuppressWarnings("unchecked")
	public List<PatientData> list(String orderBy, int start, int count, boolean fetchMedications) {
		return (List<PatientData>) (List<? extends PatientData>) patientDao.list(orderBy, start, count, fetchMedications);
	}

	public PatientData get(final long patientId, final boolean fetchMedications) {
		return patientDao.get(patientId, fetchMedications);
	}

	public boolean update(final PatientData patientData, final boolean updateMedications) {
		return patientDao.update(patientData, updateMedications);
	}

	public boolean delete(final PatientData patientData) {
		return patientDao.delete(patientData);
	}

	public long countAll() {
		return patientDao.countAll();
	}

	public void delete(final long[] patientIds) {
		for (long patientId : patientIds) {
			patientDao.delete(patientId);
		}
	}

	public boolean delete(final long patientId) {
		return patientDao.delete(patientId);
	}

	public List<PatientStatsData> listStats(final long illnessId, final Date visitDateFrom, final Date visitDateTo, int sortColumn) {
		return patientDao.listStats(illnessId, visitDateFrom, visitDateTo, sortColumn);
	}
}
