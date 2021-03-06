package x.mvmn.patienceajdbc.model.impl;

import java.util.List;

import x.mvmn.patienceajdbc.model.ExaminationData;
import x.mvmn.patienceajdbc.model.IllnessPhase;
import x.mvmn.patienceajdbc.model.Medication;

public final class ExaminationDataImpl implements ExaminationData {

	private long id = -1L;
	private long PatientId;
	private long illnessId;
	private int number;
	private String matherial;
	private String blood;
	private String mielogramm;
	private List<Medication> treatment;
	private String treatmentDescription;
	private String comments;
	// private Date examinationDate;
	private Integer examinationDateYear;
	private Integer examinationDateMonth;
	private Integer examinationDateDay;
	private IllnessPhase phase = IllnessPhase.UNSET;
	private FishExaminationResultsImpl fishExaminationResults;
	private CariotypeExaminationResultsImpl cariotypeExaminationResults;
	private List<TagImpl> keywords;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getPatientId() {
		return PatientId;
	}

	public void setPatientId(long patientId) {
		PatientId = patientId;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getMatherial() {
		return matherial;
	}

	public void setMatherial(String matherial) {
		this.matherial = matherial;
	}

	public String getBlood() {
		return blood;
	}

	public void setBlood(String blood) {
		this.blood = blood;
	}

	public String getMielogramm() {
		return mielogramm;
	}

	public void setMielogramm(String mielogramm) {
		this.mielogramm = mielogramm;
	}

	public List<Medication> getTreatment() {
		return treatment;
	}

	public void setTreatment(List<Medication> treatment) {
		this.treatment = treatment;
	}

	public String getTreatmentDescription() {
		return treatmentDescription;
	}

	public void setTreatmentDescription(String treatmentDescription) {
		this.treatmentDescription = treatmentDescription;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public IllnessPhase getPhase() {
		IllnessPhase currentPhase = phase;
		return currentPhase != null ? currentPhase : IllnessPhase.UNSET;
	}

	public void setPhase(IllnessPhase phase) {
		this.phase = phase;
	}

	public FishExaminationResultsImpl getFishExaminationResults() {
		return fishExaminationResults;
	}

	public void setFishExaminationResults(FishExaminationResultsImpl fishExaminationResults) {
		this.fishExaminationResults = fishExaminationResults;
	}

	public CariotypeExaminationResultsImpl getCariotypeExaminationResults() {
		return cariotypeExaminationResults;
	}

	public void setCariotypeExaminationResults(CariotypeExaminationResultsImpl cariotypeExaminationResults) {
		this.cariotypeExaminationResults = cariotypeExaminationResults;
	}

	public List<TagImpl> getKeywords() {
		return keywords;
	}

	public void setKeywords(List<TagImpl> keywords) {
		this.keywords = keywords;
	}

	public long getIllnessId() {
		return illnessId;
	}

	public void setIllnessId(long illnessId) {
		this.illnessId = illnessId;
	}

	public Integer getExaminationDateYear() {
		return examinationDateYear;
	}

	public void setExaminationDateYear(Integer examinationDateYear) {
		this.examinationDateYear = examinationDateYear;
	}

	public Integer getExaminationDateMonth() {
		return examinationDateMonth;
	}

	public void setExaminationDateMonth(Integer examinationDateMonth) {
		this.examinationDateMonth = examinationDateMonth;
	}

	public Integer getExaminationDateDay() {
		return examinationDateDay;
	}

	public void setExaminationDateDay(Integer examinationDateDay) {
		this.examinationDateDay = examinationDateDay;
	}
}