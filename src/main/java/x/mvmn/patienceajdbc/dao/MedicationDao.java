package x.mvmn.patienceajdbc.dao;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import x.mvmn.patienceajdbc.model.Medication;

public interface MedicationDao {

	public Medication create(long illnessId, String medicationName);

	public Medication get(long medicationId);

	public long countAll();

	public long countByIllness(long illnessId);

	public List<Medication> list(long illnessId);

	public Medication findByName(long illnessId, String medicationName);

	public boolean delete(Medication medication);

	@Transactional
	public boolean delete(long medicationId);

	@Transactional
	public boolean deleteByName(long illnessId, String medicationName);

	@Transactional
	public int deleteAllByIllness(long illnessId);

	public String getNameById(long medicationId);

	public long getIdByName(long illnessId, String medicationName);

	public boolean updateName(long medicationId, String newName);

}