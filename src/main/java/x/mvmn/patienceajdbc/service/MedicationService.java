package x.mvmn.patienceajdbc.service;

import java.util.List;

import x.mvmn.patienceajdbc.model.Medication;

public interface MedicationService {

	public Medication create(long illnessId, String medicationName);

	public Medication get(long medicationId);

	public long countAll();

	public long countByIllness(long illnessId);

	public List<Medication> list(long illnessId);

	public Medication findByName(long illnessId, String medicationName);

	public boolean delete(Medication medication);

	public boolean delete(long medicationId);

	public boolean deleteByName(long illnessId, String medicationName);

	public int deleteAllByIllness(long illnessId);

	public String getNameById(long medicationId);

	public long getIdByName(long illnessId, String medicationName);

	public boolean updateName(long medicationId, String newName);

}