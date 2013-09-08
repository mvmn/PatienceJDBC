package x.mvmn.patienceajdbc.model;

import java.util.Date;

public interface PatientStatsData {

	public long getPatientId();

	public String getFullName();

	public Date getDateOfBirth();

	public String getAddress();

	public Date getDateOfFirstExamination();

	public int getVisitsCount();

}