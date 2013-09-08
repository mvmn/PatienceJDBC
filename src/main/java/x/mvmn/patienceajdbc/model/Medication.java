package x.mvmn.patienceajdbc.model;

public interface Medication extends PersistentEntity {

	public long getIllnessId();

	public String getName();
}
