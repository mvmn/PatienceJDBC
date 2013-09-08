package x.mvmn.patienceajdbc.service.impl;

import java.util.List;

import x.mvmn.patienceajdbc.dao.MedicationDao;
import x.mvmn.patienceajdbc.model.Medication;
import x.mvmn.patienceajdbc.service.MedicationService;

public class MedicationServiceImpl implements MedicationService {

	protected final MedicationDao medicationDao;

	public MedicationServiceImpl(final MedicationDao medicationDao) {
		this.medicationDao = medicationDao;
	}

	public Medication create(long illnessId, String medicationName) {
		return medicationDao.create(illnessId, medicationName);
	}

	public Medication get(long medicationId) {
		return medicationDao.get(medicationId);
	}

	public long countAll() {
		return medicationDao.countAll();
	}

	public long countByIllness(long illnessId) {
		return medicationDao.countByIllness(illnessId);
	}

	public List<Medication> list(long illnessId) {
		return medicationDao.list(illnessId);
	}

	public Medication findByName(long illnessId, String medicationName) {
		return medicationDao.findByName(illnessId, medicationName);
	}

	public boolean delete(Medication medication) {
		return medicationDao.delete(medication);
	}

	public boolean delete(long medicationId) {
		return medicationDao.delete(medicationId);
	}

	public boolean deleteByName(long illnessId, String medicationName) {
		return medicationDao.deleteByName(illnessId, medicationName);
	}

	public int deleteAllByIllness(long illnessId) {
		return medicationDao.deleteAllByIllness(illnessId);
	}

	public String getNameById(long medicationId) {
		return medicationDao.getNameById(medicationId);
	}

	public long getIdByName(long illnessId, String medicationName) {
		return medicationDao.getIdByName(illnessId, medicationName);
	}

	public boolean updateName(long medicationId, String newName) {
		return medicationDao.updateName(medicationId, newName);
	}
}
