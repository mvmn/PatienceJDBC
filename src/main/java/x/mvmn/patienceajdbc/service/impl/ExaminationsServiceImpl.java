package x.mvmn.patienceajdbc.service.impl;

import java.util.Date;
import java.util.List;

import x.mvmn.patienceajdbc.dao.ExaminationDao;
import x.mvmn.patienceajdbc.model.ExaminationData;
import x.mvmn.patienceajdbc.model.IllnessPhase;
import x.mvmn.patienceajdbc.model.impl.ExaminationDataImpl;
import x.mvmn.patienceajdbc.service.ExaminationsService;

public class ExaminationsServiceImpl implements ExaminationsService {

	protected final ExaminationDao examinationDao;

	public ExaminationsServiceImpl(final ExaminationDao examinationDao) {
		this.examinationDao = examinationDao;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see x.mvmn.patienceajdbc.service.impl.ExaminationsService#get(long)
	 */
	public ExaminationDataImpl get(long id, boolean fetchMedications) {
		return examinationDao.get(id, fetchMedications);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see x.mvmn.patienceajdbc.service.impl.ExaminationsService#getByNumber(int)
	 */
	public ExaminationDataImpl getByNumber(int number, boolean fetchMedications) {
		return examinationDao.getByNumber(number, fetchMedications);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see x.mvmn.patienceajdbc.service.impl.ExaminationsService#create(long, long, int, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String, java.util.Date, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public ExaminationDataImpl create(long patientId, long illnessId, int number, String matherial, String blood, String mielogramm,
			String treatmentDescription, String comments, Date examinationDate, IllnessPhase illnessPhase) {
		return examinationDao.create(patientId, illnessId, number, matherial, blood, mielogramm, treatmentDescription, comments, examinationDate, illnessPhase);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see x.mvmn.patienceajdbc.service.impl.ExaminationsService#countAll()
	 */
	public int countAll() {
		return examinationDao.countAll();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see x.mvmn.patienceajdbc.service.impl.ExaminationsService#delete(long)
	 */
	public boolean delete(long examinationId) {
		return examinationDao.delete(examinationId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see x.mvmn.patienceajdbc.service.impl.ExaminationsService#getByPatient(int)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<ExaminationData> getByPatient(long patientId, boolean fetchMedications) {
		return (List) examinationDao.getByPatient(patientId, fetchMedications);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see x.mvmn.patienceajdbc.service.impl.ExaminationsService#getByIllness(int)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<ExaminationData> getByIllness(long illnessId, boolean fetchMedications) {
		return (List) examinationDao.getByIllness(illnessId, fetchMedications);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see x.mvmn.patienceajdbc.service.impl.ExaminationsService#getByPatientAndIllness(int, int)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<ExaminationData> getByPatientAndIllness(long patientId, long illnessId, boolean fetchMedications) {
		return (List) examinationDao.getByPatientAndIllness(patientId, illnessId, fetchMedications);
	}

	@Override
	public void update(ExaminationData examData, boolean updateMedications) {
		examinationDao.update(examData, updateMedications);
	}

	@Override
	public long getLastExaminationNumber() {
		return examinationDao.getLastExaminationNumber();
	}
}
