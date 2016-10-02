package org.bundolo.controller;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bundolo.Constants;
import org.bundolo.model.ItemList;
import org.bundolo.model.enumeration.ContentKindType;
import org.bundolo.services.ContentService;
import org.bundolo.services.ItemListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ItemListController {

	private static final Logger logger = Logger.getLogger(ItemListController.class.getName());

	@Autowired
	private ItemListService itemListService;

	@Autowired
	private ContentService contentService;

	@RequestMapping(value = Constants.REST_PATH_ITEM_LIST + "/{slug}", method = RequestMethod.GET)
	public @ResponseBody ItemList itemList(@PathVariable String slug) {
		logger.log(Level.INFO, "itemList, slug: " + slug);
		ItemList result = itemListService
				.findItemList(ContentKindType.item_list_description.getLocalizedName() + "/" + slug);
		if (result != null) {
			result.setItems(contentService.findItemListItems(result.getQuery(), 0, -1, null, null, null, null));
		}
		return result;
	}

	@RequestMapping(value = Constants.REST_PATH_ITEM_LIST + "/{slug}", method = RequestMethod.DELETE)
	public @ResponseBody Boolean delete(@PathVariable String slug) {
		return itemListService
				.deleteItemList(ContentKindType.item_list_description.getLocalizedName() + "/" + slug) != null;
	}

	@RequestMapping(value = Constants.REST_PATH_ITEM_LIST, method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> saveOrUpdate(@RequestBody final ItemList itemList) {
		logger.log(Level.INFO, "saveOrUpdate, itemList: " + itemList);
		ResponseEntity<String> result = itemListService.saveOrUpdateItemList(itemList);
		if (HttpStatus.OK.equals(result.getStatusCode())) {
			itemListService.clearSession();
		}
		return result;
	}

}