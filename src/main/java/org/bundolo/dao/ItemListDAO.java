package org.bundolo.dao;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Query;

import org.bundolo.model.ItemList;
import org.springframework.stereotype.Repository;

@Repository("itemListDAO")
public class ItemListDAO extends JpaDAO<Long, ItemList> {

    private static final Logger logger = Logger.getLogger(ItemListDAO.class.getName());

    @SuppressWarnings("unchecked")
    public ItemList findByName(final String name, final String authorUsername) {
	String queryString = "SELECT l FROM " + entityClass.getName() + " l, Content c";
	queryString += " WHERE c.name " + ((name == null) ? "IS NULL" : "='" + name + "'");
	queryString += " AND c.contentId = l.descriptionContentId";
	queryString += " AND l.authorUsername " + ((authorUsername == null) ? "IS NULL" : "=" + authorUsername);
	logger.log(Level.FINE, "queryString: " + queryString);

	Query q = entityManager.createQuery(queryString);
	q.setMaxResults(1);
	List<ItemList> resultList = q.getResultList();
	if (resultList != null && resultList.size() > 0) {
	    return resultList.get(0);
	} else {
	    return null;
	}
    }

}