package org.bundolo.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.lang3.StringUtils;
import org.bundolo.Constants;
import org.bundolo.SecurityUtils;
import org.bundolo.dao.ContentDAO;
import org.bundolo.dao.ItemListDAO;
import org.bundolo.model.Content;
import org.bundolo.model.ItemList;
import org.bundolo.model.Rating;
import org.bundolo.model.enumeration.ContentKindType;
import org.bundolo.model.enumeration.ContentStatusType;
import org.bundolo.model.enumeration.ItemListKindType;
import org.bundolo.model.enumeration.ItemListStatusType;
import org.bundolo.model.enumeration.RatingKindType;
import org.bundolo.model.enumeration.RatingStatusType;
import org.bundolo.model.enumeration.ReturnMessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("itemListService")
public class ItemListServiceImpl implements ItemListService {

    private static final Logger logger = Logger.getLogger(ItemListServiceImpl.class.getName());

    @Autowired
    private ItemListDAO itemListDAO;

    @Autowired
    private ContentDAO contentDAO;

    @PostConstruct
    public void init() throws Exception {
    }

    @PreDestroy
    public void destroy() {
    }

    @Override
    public ItemList findItemList(Long itemListId) {
	ItemList result = itemListDAO.findById(itemListId);
	return result;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    private ReturnMessageType saveItemList(ItemList itemList) {
	try {
	    if (itemListDAO.findItemList(itemList.getAuthorUsername(), itemList.getDescriptionContent().getName()) != null) {
		// contest title already taken
		return ReturnMessageType.title_taken;
	    }
	    itemList.setItemListStatus(ItemListStatusType.active);
	    itemList.setCreationDate(new Date());
	    itemList.setKind(ItemListKindType.personal);
	    Content descriptionContent = itemList.getDescriptionContent();
	    descriptionContent.setAuthorUsername(itemList.getAuthorUsername());
	    descriptionContent.setContentStatus(ContentStatusType.active);
	    descriptionContent.setCreationDate(itemList.getCreationDate());
	    descriptionContent.setKind(ContentKindType.item_list_description);
	    descriptionContent.setLocale(Constants.DEFAULT_LOCALE);
	    descriptionContent.setLastActivity(itemList.getCreationDate());
	    itemListDAO.persist(itemList);
	    return ReturnMessageType.success;
	} catch (Exception ex) {
	    logger.log(Level.SEVERE, "saveItemList exception: " + ex);
	    return ReturnMessageType.exception;
	}
    }

    @Override
    public List<ItemList> findItemLists(Integer start, Integer end, String[] orderBy, String[] order,
	    String[] filterBy, String[] filter) {
	return itemListDAO.findItemLists(start, end, orderBy, order, filterBy, filter);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ItemList findItemList(String title) {
	ItemList itemList = itemListDAO.findItemList(SecurityUtils.getUsername(), title);
	if (itemList != null) {
	    Collection<Rating> ratings = itemList.getDescriptionContent().getRating();
	    if (ratings == null) {
		ratings = new ArrayList<Rating>();
		itemList.getDescriptionContent().setRating(ratings);
	    }
	    Rating rating = itemList.getDescriptionContent().getRating().size() > 0 ? (Rating) itemList
		    .getDescriptionContent().getRating().toArray()[0] : null;
	    // if user that requested this is the author, do not increase rating
	    long ratingIncrement = itemList.getAuthorUsername() != null
		    && itemList.getAuthorUsername().equals(SecurityUtils.getUsername()) ? 0
		    : Constants.DEFAULT_RATING_INCREMENT;
	    Date lastActivity = itemList.getAuthorUsername() == null
		    || !itemList.getAuthorUsername().equals(SecurityUtils.getUsername()) || rating == null ? new Date()
		    : rating.getLastActivity();
	    if (rating == null) {
		rating = new Rating(null, null, RatingKindType.general, lastActivity, RatingStatusType.active,
			ratingIncrement, itemList.getDescriptionContent());
		itemList.getDescriptionContent().getRating().add(rating);
	    } else {
		rating.setValue(rating.getValue() + ratingIncrement);
		rating.setLastActivity(lastActivity);
	    }
	    itemListDAO.merge(itemList);
	}
	return itemList;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ReturnMessageType saveOrUpdateItemList(ItemList itemList) {
	try {
	    if (itemList == null || itemList.getDescriptionContent() == null
		    || StringUtils.isBlank(itemList.getDescriptionContent().getName())) {
		return ReturnMessageType.no_data;
	    }
	    String senderUsername = SecurityUtils.getUsername();
	    if (senderUsername != null) {
		if (itemList.getItemListId() == null) {
		    itemList.setAuthorUsername(senderUsername);
		    return saveItemList(itemList);
		} else {
		    ItemList itemListDB = itemListDAO.findById(itemList.getItemListId());
		    if (itemListDB == null) {
			// no such contest
			return ReturnMessageType.not_found;
		    } else {
			if (itemListDB.getAuthorUsername() == null
				|| !itemListDB.getAuthorUsername().equals(senderUsername)) {
			    // user is not the owner
			    return ReturnMessageType.not_owner;
			}
			Content descriptionContent = itemList.getDescriptionContent();
			Content descriptionContentDB = itemListDB.getDescriptionContent();
			if (!descriptionContentDB.getName().equals(descriptionContent.getName())
				&& itemListDAO.findItemList(itemListDB.getAuthorUsername(),
					descriptionContent.getName()) != null) {
			    // new item list title already taken
			    return ReturnMessageType.title_taken;
			}
			descriptionContentDB.setName(descriptionContent.getName());
			descriptionContentDB.setText(descriptionContent.getText());
			descriptionContentDB.setLastActivity(new Date());
			itemListDB.setQuery(itemList.getQuery());
			itemListDAO.merge(itemListDB);
			return ReturnMessageType.success;
		    }
		}
	    } else {
		return ReturnMessageType.anonymous_not_allowed;
	    }
	} catch (Exception ex) {
	    logger.log(Level.SEVERE, "saveOrUpdateItemList exception: " + ex);
	    return ReturnMessageType.exception;
	}
    }

    @Override
    public void clearSession() {
	itemListDAO.clear();
    }

    @Override
    public ItemList findNext(Long itemListId, String orderBy, String fixBy, boolean ascending) {
	return itemListDAO.findNext(itemListId, orderBy, fixBy, ascending);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Long deleteItemList(String title) {
	logger.log(Level.INFO, "deleteItemList: title: " + title);
	ItemList itemList = itemListDAO.findItemList(SecurityUtils.getUsername(), title);
	if (itemList == null) {
	    // no such content
	    return null;
	} else {
	    if (!itemList.getAuthorUsername().equals(SecurityUtils.getUsername())) {
		// user is not the owner
		return null;
	    }
	    contentDAO.disable(itemList.getDescriptionContent());
	    itemList.setItemListStatus(ItemListStatusType.disabled);
	    itemListDAO.merge(itemList);
	    return itemList.getItemListId();
	}
    }
}
