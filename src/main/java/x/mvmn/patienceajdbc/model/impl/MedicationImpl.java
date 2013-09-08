package x.mvmn.patienceajdbc.model.impl;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import x.mvmn.patienceajdbc.model.Medication;

public final class MedicationImpl implements Medication {

	private final long id;
	private final long illnessId;
	private final String name;
	private final int hashCode;

	public MedicationImpl(final long id, final long illnessId, final String name) {
		this.id = id;
		this.illnessId = illnessId;
		this.name = name;

		HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
		hashCodeBuilder.append(illnessId);
		hashCodeBuilder.append(name);
		this.hashCode = hashCodeBuilder.toHashCode();
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public long getIllnessId() {
		return illnessId;
	}

	public boolean equals(Object obj) {
		if (obj instanceof Medication) {
			Medication med = (Medication) obj;
			EqualsBuilder eqBuilder = new EqualsBuilder();
			eqBuilder.append(illnessId, med.getIllnessId());
			eqBuilder.append(name, med.getName());
			return eqBuilder.isEquals();
		} else {
			return false;
		}
	}

	public int hashCode() {
		return hashCode;
	}
}