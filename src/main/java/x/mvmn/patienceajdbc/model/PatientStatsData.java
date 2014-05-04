package x.mvmn.patienceajdbc.model;

import java.util.Date;

public interface PatientStatsData {

	public long getPatientId();

	public String getFullName();

	public String getDateOfBirth();

	public String getAddress();

	public Date getDateOfFirstExamination();

	public int getVisitsCount();

}