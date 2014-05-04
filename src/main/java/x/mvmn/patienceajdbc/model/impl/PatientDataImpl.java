package x.mvmn.patienceajdbc.model.impl;

import java.util.List;

import x.mvmn.patienceajdbc.model.Medication;
import x.mvmn.patienceajdbc.model.PatientData;

public final class PatientDataImpl implements PatientData {

	private long id = -1L;
	private String firstName;
	private String lastName;
	private String patronymicName;
	private String address;
	private String anamnesis;
	private List<Medication> previousTreatments;
	private boolean dead;

	private Integer birthDateYear;
	private Integer birthDateMonth;
	private Integer birthDateDay;

	private Integer diagnosisDateYear;
	private Integer diagnosisDateMonth;
	private Integer diagnosisDateDay;

	private Integer deathDateYear;
	private Integer deathDateMonth;
	private Integer deathDateDay;

	// private Date dateOfBirth;
	// private Date dateOfDiagnosis;
	// private Date dateOfDeath;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPatronymicName() {
		return patronymicName;
	}

	public void setPatronymicName(String patronymicName) {
		this.patronymicName = patronymicName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAnamnesis() {
		return anamnesis;
	}

	public void setAnamnesis(String anamnesis) {
		this.anamnesis = anamnesis;
	}

	public boolean isDead() {
		return dead;
	}

	public void setDead(boolean dead) {
		this.dead = dead;
	}

	public List<Medication> getPreviousTreatments() {
		return previousTreatments;
	}

	public void setPreviousTreatments(List<Medication> previousTreatments) {
		this.previousTreatments = previousTreatments;
	}

	public Integer getBirthDateYear() {
		return birthDateYear;
	}

	public void setBirthDateYear(Integer birthDateYear) {
		this.birthDateYear = birthDateYear;
	}

	public Integer getBirthDateMonth() {
		return birthDateMonth;
	}

	public void setBirthDateMonth(Integer birthDateMonth) {
		this.birthDateMonth = birthDateMonth;
	}

	public Integer getBirthDateDay() {
		return birthDateDay;
	}

	public void setBirthDateDay(Integer birthDateDay) {
		this.birthDateDay = birthDateDay;
	}

	public Integer getDiagnosisDateYear() {
		return diagnosisDateYear;
	}

	public void setDiagnosisDateYear(Integer diagnosisDateYear) {
		this.diagnosisDateYear = diagnosisDateYear;
	}

	public Integer getDiagnosisDateMonth() {
		return diagnosisDateMonth;
	}

	public void setDiagnosisDateMonth(Integer diagnosisDateMonth) {
		this.diagnosisDateMonth = diagnosisDateMonth;
	}

	public Integer getDiagnosisDateDay() {
		return diagnosisDateDay;
	}

	public void setDiagnosisDateDay(Integer diagnosisDateDay) {
		this.diagnosisDateDay = diagnosisDateDay;
	}

	public Integer getDeathDateYear() {
		return deathDateYear;
	}

	public void setDeathDateYear(Integer deathDateYear) {
		this.deathDateYear = deathDateYear;
	}

	public Integer getDeathDateMonth() {
		return deathDateMonth;
	}

	public void setDeathDateMonth(Integer deathDateMonth) {
		this.deathDateMonth = deathDateMonth;
	}

	public Integer getDeathDateDay() {
		return deathDateDay;
	}

	public void setDeathDateDay(Integer deathDateDay) {
		this.deathDateDay = deathDateDay;
	}
}