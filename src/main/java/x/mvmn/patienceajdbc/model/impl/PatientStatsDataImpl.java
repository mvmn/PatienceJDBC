package x.mvmn.patienceajdbc.model.impl;

import java.util.Date;

import x.mvmn.patienceajdbc.model.PatientStatsData;

public class PatientStatsDataImpl implements PatientStatsData {
	private final long patientId;
	private final String fullName;
	private final Date dateOfBirth;
	private final String address;
	private final Date dateOfFirstExamination;
	private final int visitsCount;

	public PatientStatsDataImpl(long patientId, String fullName, Date dateOfBirth, String address, Date dateOfFirstExamination, int visitsCount) {
		super();
		this.patientId = patientId;
		this.fullName = fullName;
		this.dateOfBirth = dateOfBirth;
		this.address = address;
		this.dateOfFirstExamination = dateOfFirstExamination;
		this.visitsCount = visitsCount;
	}

	public long getPatientId() {
		return patientId;
	}

	public String getFullName() {
		return fullName;
	}

	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	public String getAddress() {
		return address;
	}

	public Date getDateOfFirstExamination() {
		return dateOfFirstExamination;
	}

	public int getVisitsCount() {
		return visitsCount;
	}
}
