package x.mvmn.patienceajdbc.model;

public enum IllnessPhase {

	UNSET(""), CHRONIC("Chronic"), ACCELERATION("Acceleration"), BLAST_CRISIS("Blast Crisis");

	private String name;

	IllnessPhase(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String toString() {
		return getName();
	}
}
