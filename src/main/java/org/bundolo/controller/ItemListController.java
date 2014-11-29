package org.bundolo.controller;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.bundolo.Constants;
import org.bundolo.model.ItemList;
import org.bundolo.model.enumeration.ReturnMessageType;
import org.bundolo.services.ContentService;
import org.bundolo.services.ItemListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.HandlerMapping;

@Controller
public class ItemListController {

    private static final Logger logger = Logger.getLogger(ItemListController.class.getName());

    @Autowired
    private ItemListService itemListService;

    @Autowired
    private ContentService contentService;

    @RequestMapping(value = Constants.REST_PATH_ITEM_LIST + "/**", method = RequestMethod.GET)
    public @ResponseBody
    ItemList itemList(HttpServletRequest request) {
	String restOfTheUrl = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
	String title = restOfTheUrl.substring(Constants.REST_PATH_ITEM_LIST.length() + 1);
	logger.log(Level.INFO, "itemList, title: " + title);
	ItemList result = itemListService.findItemList(title);
	if (result != null) {
	    result.setItems(contentService.findItemListItems(result.getQuery()));
	}
	return result;
    }

    @RequestMapping(value = Constants.REST_PATH_ITEM_LIST + "/**", method = RequestMethod.DELETE)
    public @ResponseBody
    Boolean delete(HttpServletRequest request) {
	String restOfTheUrl = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
	return itemListService.deleteItemList(restOfTheUrl.substring(Constants.REST_PATH_ITEM_LIST.length() + 1)) != null;
    }

    @RequestMapping(value = Constants.REST_PATH_ITEM_LIST + "/{title}", method = RequestMethod.PUT)
    public @ResponseBody
    ReturnMessageType saveOrUpdate(@PathVariable String title, @RequestBody final ItemList itemList) {
	if (!title.matches(Constants.URL_SAFE_REGEX)) {
	    return ReturnMessageType.title_not_url_safe;
	}
	itemList.getDescriptionContent().setName(title.trim());
	logger.log(Level.INFO, "saveOrUpdate, itemList: " + itemList);
	ReturnMessageType result = itemListService.saveOrUpdateItemList(itemList);
	if (ReturnMessageType.success.equals(result)) {
	    itemListService.clearSession();
	}
	return result;
    }

}