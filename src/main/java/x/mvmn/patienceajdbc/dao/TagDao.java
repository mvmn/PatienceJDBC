package x.mvmn.patienceajdbc.dao;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import x.mvmn.patienceajdbc.model.Tag;
import x.mvmn.patienceajdbc.model.impl.TagImpl;

public interface TagDao extends DataAccessObject {

	public TagImpl create(String tagName);

	public long countAll();

	public List<TagImpl> listAll();

	public TagImpl get(long tagId);

	public TagImpl findByName(String tagName);

	public boolean delete(Tag tag);

	@Transactional
	public boolean delete(long tagId);

	@Transactional
	public boolean deleteByName(String tagName);

	public String getNameById(long tagId);

	public long getIdByName(String tagName);

	public boolean updateName(long tagId, String newName);

}