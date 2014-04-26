package x.mvmn.patienceajdbc.dao;

import java.util.Date;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import x.mvmn.patienceajdbc.model.ExaminationData;
import x.mvmn.patienceajdbc.model.IllnessPhase;
import x.mvmn.patienceajdbc.model.impl.ExaminationDataImpl;

public interface ExaminationDao {

	public ExaminationDataImpl get(long id, boolean fetchMedications);

	public ExaminationDataImpl getByNumber(int number, boolean fetchMedications);

	@Transactional
	public ExaminationDataImpl create(long patientId, long illnessId, int number, String matherial, String blood, String mielogramm,
			String treatmentDescription, String comments, Date examinationDate, IllnessPhase illnessPhase);

	public int countAll();

	public List<ExaminationDataImpl> getByPatient(final long patientId, boolean fetchMedications);

	public List<ExaminationDataImpl> getByIllness(final long illnessId, boolean fetchMedications);

	public List<ExaminationDataImpl> getByPatientAndIllness(final long patientId, final long illnessId, boolean fetchMedications);

	@Transactional
	public boolean delete(long examinationId);

	@Transactional
	public void update(ExaminationData examData, boolean updateMedications);

	public long getLastExaminationNumber();

}