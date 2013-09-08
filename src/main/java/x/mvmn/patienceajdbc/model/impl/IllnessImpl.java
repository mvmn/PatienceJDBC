package x.mvmn.patienceajdbc.model.impl;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import x.mvmn.patienceajdbc.model.Illness;

public final class IllnessImpl implements Illness {

	private final long id;
	private final String name;
	private final int hashCode;

	public IllnessImpl(final long id, final String name) {
		this.id = id;
		this.name = name;
		HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
		hashCodeBuilder.append(name);
		this.hashCode = hashCodeBuilder.hashCode();
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int hashCode() {
		return hashCode;
	}

	public boolean equals(Object obj) {
		if (obj instanceof Illness) {
			Illness illness = (Illness) obj;
			EqualsBuilder eqBuilder = new EqualsBuilder();
			eqBuilder.append(name, illness.getName());
			return eqBuilder.isEquals();
		} else {
			return false;
		}
	}
}
