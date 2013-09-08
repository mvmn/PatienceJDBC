package x.mvmn.patienceajdbc.model;

import java.util.Date;
import java.util.List;

public interface PatientData extends PersistentEntity {

	public String getFirstName();

	public String getLastName();

	public String getPatronymicName();

	public Date getDateOfBirth();

	public Date getDateOfDiagnosis();

	public String getAddress();

	public String getAnamnesis();

	public List<Medication> getPreviousTreatments();

	public boolean isDead();

	public Date getDateOfDeath();

	public long getId();

	public void setFirstName(String firstName);

	public void setLastName(String lastName);

	public void setPatronymicName(String patronymicName);

	public void setDateOfBirth(Date dateOfBirth);

	public void setDateOfDiagnosis(Date dateOfDiagnosis);

	public void setAddress(String address);

	public void setAnamnesis(String anamnesis);

	public void setDateOfDeath(Date dateOfDeath);

	public void setDead(boolean dead);

	public void setPreviousTreatments(List<Medication> previousTreatments);

}
