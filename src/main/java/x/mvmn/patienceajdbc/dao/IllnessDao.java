package x.mvmn.patienceajdbc.dao;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import x.mvmn.patienceajdbc.model.Illness;
import x.mvmn.patienceajdbc.model.impl.IllnessImpl;

public interface IllnessDao {

	public IllnessImpl create(String illnessName);

	public long countAll();

	public List<IllnessImpl> listAll();

	public IllnessImpl get(long illnessId);

	public IllnessImpl findByName(String illnessName);

	public boolean delete(Illness illness);

	@Transactional
	public boolean delete(long illnessId);

	@Transactional
	public boolean deleteByName(String illnessName);

	public String getNameById(long illnessId);

	public long getIdByName(String illnessName);

	public boolean updateName(long illnessId, String newName);

}