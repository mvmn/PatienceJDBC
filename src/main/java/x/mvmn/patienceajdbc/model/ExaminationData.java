package x.mvmn.patienceajdbc.model;

import java.util.Date;
import java.util.List;

public interface ExaminationData extends PersistentEntity {

	public long getPatientId();

	public long getIllnessId();

	public int getNumber();

	public String getMatherial();

	public String getBlood();

	public String getMielogramm();

	public List<? extends Medication> getTreatment();

	public String getTreatmentDescription();

	public String getComments();

	public Date getExaminationDate();

	public IllnessPhase getPhase();

	public FishExaminationResults getFishExaminationResults();

	public CariotypeExaminationResults getCariotypeExaminationResults();

	public List<? extends Tag> getKeywords();
}
