package x.mvmn.patienceajdbc.model.impl;

import x.mvmn.patienceajdbc.model.ExaminationResults;

public abstract class AbstractExaminationResultsImpl implements ExaminationResults {

	private long id = -1L;
	private String nomenclaturalDescription;
	private String comments;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getNomenclaturalDescription() {
		return nomenclaturalDescription;
	}

	public void setNomenclaturalDescription(String nomenclaturalDescription) {
		this.nomenclaturalDescription = nomenclaturalDescription;
	}

	public abstract String getTypeName();

}
