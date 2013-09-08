package x.mvmn.patienceajdbc.dao;

import java.util.Date;

import org.springframework.transaction.annotation.Transactional;

import x.mvmn.patienceajdbc.model.impl.ExaminationDataImpl;

public interface ExaminationDao {

	public ExaminationDataImpl get(long id);

	public ExaminationDataImpl getByNumber(int number);

	public ExaminationDataImpl create(long patientId, long illnessId, int number, String matherial, String blood, String mielogramm,
			String treatmentDescription, String comments, Date examinationDate, String illnessPhase, String typeName, String nomenclaturalDescription,
			String examinationComments);

	public long countAll();

	@Transactional
	public boolean delete(long examinationId);

}