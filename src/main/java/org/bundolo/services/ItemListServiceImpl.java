package org.bundolo.services;

import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.bundolo.SessionUtils;
import org.bundolo.Utils;
import org.bundolo.dao.ContentDAO;
import org.bundolo.dao.ItemListDAO;
import org.bundolo.model.Content;
import org.bundolo.model.ItemList;
import org.bundolo.model.enumeration.ContentKindType;
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

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void saveItemList(ItemList itemList) throws Exception {
	ItemList itemListDB = null;
	if (itemList.getItemListId() != null) {
	    itemListDB = itemListDAO.findById(itemList.getItemListId());
	}
	// TODO saving description
	if (itemListDB == null) {
	    itemListDB = new ItemList(itemList.getItemListId(), SessionUtils.getUsername(),
		    itemList.getItemListStatus(), itemList.getKind(), itemList.getCreationDate(), itemList.getQuery(),
		    itemList.getDescriptionContentId());
	    try {
		itemListDAO.persist(itemListDB);
	    } catch (Exception ex) {
		throw new Exception("db exception");
	    }
	}
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updateItemList(ItemList itemList) throws Exception {
	ItemList itemListDB = itemListDAO.findById(itemList.getItemListId());

	if (itemListDB != null) {
	    // itemList.setAuthorUsername(itemListDTO.getAuthorUsername());
	    itemListDB.setItemListStatus(itemList.getItemListStatus());
	    itemListDB.setKind(itemList.getKind());
	    // itemList.setCreationDate(itemListDTO.getCreationDate());
	    itemListDB.setQuery(itemList.getQuery());
	    // itemList.setDescriptionContentId(itemListDTO.getDescriptionContent().getContentId());
	}
	try {
	    itemListDAO.merge(itemListDB);
	} catch (Exception ex) {
	    throw new Exception("db exception");
	}
    }

    // @Override
    // @Transactional(propagation=Propagation.REQUIRED,
    // rollbackFor=Exception.class)
    // public void saveOrUpdateItemList(ItemListDTO itemListDTO) throws
    // Exception {
    // ItemList itemList = new ItemList(itemListDTO.getItemListId(),
    // itemListDTO.getAuthorUsername(), itemListDTO.getItemListStatus(),
    // itemListDTO.getKind(), itemListDTO.getCreationDate(),
    // itemListDTO.getQuery(),
    // itemListDTO.getDescriptionContent().getContentId());
    // itemListDAO.merge(itemList);
    // }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deleteItemList(Long itemListId) throws Exception {
	ItemList itemList = itemListDAO.findById(itemListId);
	if (itemList != null)
	    itemListDAO.remove(itemList);
    }

    // @Override
    // public List<ItemListDTO> findAllItemLists() throws Exception {
    // List<ItemList> itemLists = itemListDAO.findAll();
    // List<ItemListDTO> itemListDTOs = new ArrayList<ItemListDTO>();
    // if (itemLists != null) {
    // for (ItemList itemList : itemLists) {
    // itemListDTOs.add(DozerBeanMapperSingletonWrapper.getInstance().map(itemList,
    // ItemListDTO.class));
    // }
    // }
    // return itemListDTOs;
    // }

    @Override
    public ItemList findItemListByName(String itemListName, String authorUsername) {
	ItemList result = itemListDAO.findByName(itemListName, authorUsername);
	if (result != null) {
	    Content descriptionContent = contentDAO.findContentForLocale(result.getDescriptionContentId(),
		    ContentKindType.item_list_description, SessionUtils.getUserLocale());
	    if (descriptionContent != null) {
		result.setDescriptionContent(descriptionContent);
	    }
	}
	return result;
    }

    @Override
    public ItemList findItemListByName(String itemListName, String authorUsername, List<String> params) {
	ItemList result = findItemListByName(itemListName, authorUsername);
	if (result != null && Utils.hasText(result.getQuery()) && Utils.hasElements(params)) {
	    String modifiedQuery = result.getQuery();
	    for (int i = 0; i < params.size(); i++) {
		String param = params.get(i);
		modifiedQuery = modifiedQuery.replace("%" + i + "%", param);
	    }
	    result.setQuery(modifiedQuery);
	}
	return result;
    }

    @Override
    public ItemList findItemList(Long itemListId, List<String> params) {
	ItemList result = findItemList(itemListId);
	if (result != null && Utils.hasText(result.getQuery()) && Utils.hasElements(params)) {
	    String modifiedQuery = result.getQuery();
	    for (int i = 0; i < params.size(); i++) {
		String param = params.get(i);
		modifiedQuery = modifiedQuery.replace("%" + i + "%", param);
	    }
	    result.setQuery(modifiedQuery);
	}
	return result;
    }
}
