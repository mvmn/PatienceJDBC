package x.mvmn.patienceajdbc.service.impl;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import x.mvmn.patienceajdbc.dao.IllnessDao;
import x.mvmn.patienceajdbc.model.Illness;
import x.mvmn.patienceajdbc.service.IllnessesService;

public class IllnessesServiceImpl implements IllnessesService {

	private final IllnessDao illnessDao;
	private final ConcurrentHashMap<String, Illness> cache = new ConcurrentHashMap<String, Illness>();
	private final CopyOnWriteArraySet<IllnessesService.IllnessesChangeListener> changeListeners = new CopyOnWriteArraySet<IllnessesService.IllnessesChangeListener>();

	public IllnessesServiceImpl(final IllnessDao illnessDao) {
		this.illnessDao = illnessDao;
		resetCache();
	}

	public boolean addChangeListener(IllnessesService.IllnessesChangeListener changeListener) {
		return changeListeners.add(changeListener);
	}

	public boolean removeChangeListener(IllnessesService.IllnessesChangeListener changeListener) {
		return changeListeners.remove(changeListener);
	}

	protected void resetCache() {
		List<? extends Illness> illnesses = illnessDao.listAll();
		cache.clear();
		for (Illness illness : illnesses) {
			cache.put(illness.getName(), illness);
		}
	}

	public Collection<Illness> getAllIllnesses() {
		return cache.values();
	}

	public Illness getIllness(String name) {
		return cache.get(name);
	}

	public Illness createNewIllness(String name) {
		Illness newIllness = illnessDao.create(name);
		cache.put(newIllness.getName(), newIllness);
		notifyCreation(newIllness);
		return newIllness;
	}

	public Illness renameIllness(Illness illness, String newName) {
		Illness result = null;
		if (illnessDao.updateName(illness.getId(), newName)) {
			result = illnessDao.get(illness.getId());
			if (result != null) {
				cache.remove(illness.getName());
				cache.put(result.getName(), result);
				notifyUpdate(result);
			}
		}
		return result;
	}

	private void notifyCreation(Illness newIllness) {
		for (IllnessesService.IllnessesChangeListener changeListener : changeListeners) {
			try {
				changeListener.onIllnessEntityCreation(newIllness);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void notifyUpdate(Illness updatedIllness) {
		for (IllnessesService.IllnessesChangeListener changeListener : changeListeners) {
			try {
				changeListener.onIllnessEntityUpdate(updatedIllness);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public Illness getIllness(long illnessId) {
		return illnessDao.get(illnessId);
	}

}
