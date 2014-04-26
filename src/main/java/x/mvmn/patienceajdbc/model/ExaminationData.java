package x.mvmn.patienceajdbc.model;

import java.util.Date;
import java.util.List;

import x.mvmn.patienceajdbc.model.impl.TagImpl;

public interface ExaminationData extends PersistentEntity {

	public long getPatientId();

	public long getIllnessId();

	public int getNumber();

	public String getMatherial();

	public String getBlood();

	public String getMielogramm();

	public List<Medication> getTreatment();

	public String getTreatmentDescription();

	public String getComments();

	public Date getExaminationDate();

	public IllnessPhase getPhase();

	public FishExaminationResults getFishExaminationResults();

	public CariotypeExaminationResults getCariotypeExaminationResults();

	public List<? extends Tag> getKeywords();

	public void setNumber(int number);

	public void setMatherial(String matherial);

	public void setBlood(String blood);

	public void setMielogramm(String mielogramm);

	public void setTreatment(List<Medication> treatment);

	public void setTreatmentDescription(String treatmentDescription);

	public void setComments(String comments);

	public void setExaminationDate(Date examinationDate);

	public void setPhase(IllnessPhase phase);

	public void setKeywords(List<TagImpl> keywords);
}
