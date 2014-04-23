package x.mvmn.patienceajdbc.dao;

import java.util.Date;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import x.mvmn.patienceajdbc.model.IllnessPhase;
import x.mvmn.patienceajdbc.model.impl.ExaminationDataImpl;

public interface ExaminationDao {

	public ExaminationDataImpl get(long id);

	public ExaminationDataImpl getByNumber(int number);

	public ExaminationDataImpl create(long patientId, long illnessId, int number, String matherial, String blood, String mielogramm,
			String treatmentDescription, String comments, Date examinationDate, IllnessPhase illnessPhase, String typeName, String nomenclaturalDescription,
			String examinationComments);

	public int countAll();

	public List<ExaminationDataImpl> getByPatient(final long patientId);

	public List<ExaminationDataImpl> getByIllness(final long illnessId);

	public List<ExaminationDataImpl> getByPatientAndIllness(final long patientId, final long illnessId);

	@Transactional
	public boolean delete(long examinationId);

}