package x.mvmn.patienceajdbc.service;

import java.util.List;

import x.mvmn.patienceajdbc.model.ExaminationData;
import x.mvmn.patienceajdbc.model.IllnessPhase;

public interface ExaminationsService {

	public ExaminationData get(long id, boolean fetchMedications);

	public ExaminationData getByNumber(int number, boolean fetchMedications);

	public ExaminationData create(long patientId, long illnessId, int number, String matherial, String blood, String mielogramm, String treatmentDescription,
			String comments, Integer examinationDateYear, Integer examinationDateMonth, Integer examinationDateDay, IllnessPhase illnessPhase);

	public int countAll();

	public boolean delete(long examinationId);

	public List<ExaminationData> getByPatient(long patientId, boolean fetchMedications);

	public List<ExaminationData> getByIllness(long illnessId, boolean fetchMedications);

	public List<ExaminationData> getByPatientAndIllness(long patientId, long illnessId, boolean fetchMedications);

	public void update(ExaminationData examData, boolean updateMedications);

	public long getLastExaminationNumber();

}