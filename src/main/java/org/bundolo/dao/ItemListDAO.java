package org.bundolo.dao;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Query;

import org.apache.commons.lang3.ArrayUtils;
import org.bundolo.SecurityUtils;
import org.bundolo.model.ItemList;
import org.bundolo.model.enumeration.ContentKindType;
import org.bundolo.model.enumeration.ItemListKindType;
import org.bundolo.model.enumeration.ItemListStatusType;
import org.springframework.stereotype.Repository;

@Repository("itemListDAO")
public class ItemListDAO extends JpaDAO<Long, ItemList> {

	private static final Logger logger = Logger.getLogger(ItemListDAO.class.getName());

	// used only during saving to validate
	@SuppressWarnings("unchecked")
	public ItemList findItemList(String username, String title) {
		if (username == null || title == null) {
			return null;
		}
		StringBuilder queryString = new StringBuilder();
		queryString.append("SELECT i FROM ItemList i, Content c");
		queryString.append(" WHERE i.itemListStatus='" + ItemListStatusType.active + "'");
		queryString.append(" AND i.authorUsername =?1");
		queryString.append(" AND i.descriptionContent.contentId=c.contentId");
		queryString.append(" AND c.kind = '" + ContentKindType.item_list_description + "'");
		queryString.append(" AND c.name =?2");
		logger.log(Level.INFO, "queryString: " + queryString.toString() + ", title: " + title);
		Query q = entityManager.createQuery(queryString.toString());
		q.setParameter(1, username);
		q.setParameter(2, title);
		q.setMaxResults(1);
		List<ItemList> resultList = q.getResultList();
		if (resultList != null && resultList.size() > 0) {
			return resultList.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public ItemList findItemList(String slug) {
		if (slug == null) {
			return null;
		}
		StringBuilder queryString = new StringBuilder();
		queryString.append("SELECT i FROM ItemList i, Content c");
		queryString.append(" WHERE i.itemListStatus='" + ItemListStatusType.active + "'");
		queryString.append(" AND (i.kind = '" + ItemListKindType.general + "' OR i.kind = '" + ItemListKindType.elected
				+ "' OR i.kind = '" + ItemListKindType.named + "'");
		String senderUsername = SecurityUtils.getUsername();
		if (senderUsername != null) {
			queryString.append(
					" OR (i.kind = '" + ItemListKindType.personal + "' AND i.authorUsername='" + senderUsername + "')");
		}
		queryString.append(")");
		queryString.append(" AND i.descriptionContent.contentId=c.contentId");
		queryString.append(" AND c.kind = '" + ContentKindType.item_list_description + "'");
		queryString.append(" AND c.slug =?1");
		queryString.append(" ORDER BY i.kind DESC");
		// if they have the same title, personal item lists have the highest
		// priority. then named, general and elected
		logger.log(Level.FINE, "queryString: " + queryString.toString() + ", slug: " + slug);
		Query q = entityManager.createQuery(queryString.toString());
		q.setParameter(1, slug);
		q.setMaxResults(1);
		List<ItemList> resultList = q.getResultList();
		if (resultList != null && resultList.size() > 0) {
			return resultList.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<ItemList> findItemLists(Integer start, Integer end, String[] orderBy, String[] order, String[] filterBy,
			String[] filter) {
		int filterParamCounter = 0;
		StringBuilder queryString = new StringBuilder();
		queryString.append("SELECT i FROM ItemList i WHERE item_list_status='active'");
		queryString.append(" AND (i.kind = '" + ItemListKindType.general + "' OR i.kind = '" + ItemListKindType.elected
				+ "' OR i.kind = '" + ItemListKindType.named + "'");
		String senderUsername = SecurityUtils.getUsername();
		if (senderUsername != null) {
			queryString.append(
					" OR (i.kind = '" + ItemListKindType.personal + "' AND i.authorUsername='" + senderUsername + "')");
		}
		queryString.append(")");
		if (ArrayUtils.isNotEmpty(filterBy)) {
			String prefix = " AND LOWER(";
			String suffix = ") LIKE '%";
			String postfix = "%'";
			for (int i = 0; i < filterBy.length; i++) {
				queryString.append(prefix);
				queryString.append(filterBy[i]);
				queryString.append(suffix);
				filterParamCounter++;
				queryString.append("'||?" + filterParamCounter + "||'");
				queryString.append(postfix);
			}
		}
		if (ArrayUtils.isNotEmpty(orderBy) && ArrayUtils.isSameLength(orderBy, order)) {
			String firstPrefix = " ORDER BY ";
			String nextPrefix = ", ";
			String prefix = firstPrefix;
			String suffix = " ";
			for (int i = 0; i < orderBy.length; i++) {
				queryString.append(prefix);
				queryString.append(orderBy[i]);
				queryString.append(suffix);
				queryString.append(order[i]);
				prefix = nextPrefix;
			}
		}
		int maxResults = end - start + 1;
		logger.log(Level.INFO,
				"queryString: " + queryString.toString() + ", start: " + start + ", max results: " + maxResults);
		Query q = entityManager.createQuery(queryString.toString());
		if (filterParamCounter > 0) {
			for (int i = 0; i < filterBy.length; i++) {
				q.setParameter(i + 1, filter[i].toLowerCase());
			}
		}
		q.setFirstResult(start);
		if (maxResults > 0) {
			q.setMaxResults(maxResults);
		}

		// TODO if we strip item lists and update them later, we could delete
		// something
		return q.getResultList();
	}

	public ItemList findNext(Long itemListId, String orderBy, String fixBy, boolean ascending) {
		// TODO backlog: implement findNext ItemList
		return null;
	}
}