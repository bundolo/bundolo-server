package org.bundolo.services;

import java.util.List;

import org.bundolo.model.ItemList;
import org.springframework.http.ResponseEntity;

public interface ItemListService {

    public ItemList findItemList(Long itemListId);

    public ItemList findItemList(String slug);

    public ResponseEntity<String> saveOrUpdateItemList(ItemList itemList);

    public List<ItemList> findItemLists(Integer start, Integer end, String[] orderBy, String[] order,
	    String[] filterBy, String[] filter);

    public ItemList findNext(Long itemListId, String orderBy, String fixBy, boolean ascending);

    public void clearSession();

    public Long deleteItemList(String slug);
}