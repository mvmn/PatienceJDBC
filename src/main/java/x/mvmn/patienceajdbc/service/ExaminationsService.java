package x.mvmn.patienceajdbc.service;

import java.util.Date;
import java.util.List;

import x.mvmn.patienceajdbc.model.ExaminationData;
import x.mvmn.patienceajdbc.model.IllnessPhase;

public interface ExaminationsService {

	public ExaminationData get(long id);

	public ExaminationData getByNumber(int number);

	public ExaminationData create(long patientId, long illnessId, int number, String matherial, String blood, String mielogramm, String treatmentDescription,
			String comments, Date examinationDate, IllnessPhase illnessPhase, String typeName, String nomenclaturalDescription, String examinationComments);

	public int countAll();

	public boolean delete(long examinationId);

	public List<ExaminationData> getByPatient(long patientId);

	public List<ExaminationData> getByIllness(long illnessId);

	public List<ExaminationData> getByPatientAndIllness(long patientId, long illnessId);

}