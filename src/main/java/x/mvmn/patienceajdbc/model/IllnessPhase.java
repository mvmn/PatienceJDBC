package x.mvmn.patienceajdbc.model;

public enum IllnessPhase {

	UNSET(""), CHRONIC("Хронічна"), ACCELERATION("Акселерація"), BLAST_CRISIS("Бластна криза");

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
