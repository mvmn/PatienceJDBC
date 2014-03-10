package x.mvmn.patienceajdbc.util.collections;

import x.mvmn.patienceajdbc.model.Illness;
import x.mvmn.patienceajdbc.model.Medication;
import x.mvmn.util.collections.CollectionsHelper.Checker;

public class MedicationIllnessFilter implements Checker<Medication> {

	protected final Illness illness;

	public MedicationIllnessFilter(Illness illness) {
		this.illness = illness;
	}

	@Override
	public boolean check(Medication val) {
		return val != null && val.getIllnessId() == illness.getId();
	}

}
