package x.mvmn.patienceajdbc.model;

import x.mvmn.patienceajdbc.model.impl.AbstractExaminationResultsImpl;

public abstract class CariotypeExaminationResults extends AbstractExaminationResultsImpl {

	@Override
	public String getTypeName() {
		return getExaminationTypeName();
	}

	public static String getExaminationTypeName() {
		return "Cariotype";
	}

}
