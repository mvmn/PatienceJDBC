package x.mvmn.patienceajdbc.service;

import java.util.Collection;

import x.mvmn.patienceajdbc.model.Illness;

public interface IllnessesService {

	public static interface IllnessesChangeListener {

		public void onIllnessEntityCreation(Illness illness);

		public void onIllnessEntityUpdate(Illness illness);

		// public void onIllnessEntityDeletion(long illnessId);
	}

	public Collection<Illness> getAllIllnesses();

	public Illness getIllness(String name);

	public Illness getIllness(long illnessId);

	public Illness createNewIllness(String name);

	public Illness renameIllness(Illness illness, String newName);
}
