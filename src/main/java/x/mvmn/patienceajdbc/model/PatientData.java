package x.mvmn.patienceajdbc.model;

import java.util.List;

public interface PatientData extends PersistentEntity {

	public String getFirstName();

	public String getLastName();

	public String getPatronymicName();

	public String getAddress();

	public String getAnamnesis();

	public List<Medication> getPreviousTreatments();

	public boolean isDead();

	public long getId();

	public void setFirstName(String firstName);

	public void setLastName(String lastName);

	public void setPatronymicName(String patronymicName);

	public void setAddress(String address);

	public void setAnamnesis(String anamnesis);

	public void setDead(boolean dead);

	public void setPreviousTreatments(List<Medication> previousTreatments);

	public Integer getBirthDateYear();

	public void setBirthDateYear(Integer birthDateYear);

	public Integer getBirthDateMonth();

	public void setBirthDateMonth(Integer birthDateMonth);

	public Integer getBirthDateDay();

	public void setBirthDateDay(Integer birthDateDay);

	public Integer getDiagnosisDateYear();

	public void setDiagnosisDateYear(Integer diagnosisDateYear);

	public Integer getDiagnosisDateMonth();

	public void setDiagnosisDateMonth(Integer diagnosisDateMonth);

	public Integer getDiagnosisDateDay();

	public void setDiagnosisDateDay(Integer diagnosisDateDay);

	public Integer getDeathDateYear();

	public void setDeathDateYear(Integer deathDateYear);

	public Integer getDeathDateMonth();

	public void setDeathDateMonth(Integer deathDateMonth);

	public Integer getDeathDateDay();

	public void setDeathDateDay(Integer deathDateDay);
}
