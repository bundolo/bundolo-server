package org.bundolo.services;

import java.util.List;

import org.bundolo.model.ItemList;
import org.bundolo.model.enumeration.ReturnMessageType;

public interface ItemListService {

    public ItemList findItemList(Long itemListId);

    public ItemList findItemList(String title);

    public ReturnMessageType saveOrUpdateItemList(ItemList itemList);

    public List<ItemList> findItemLists(Integer start, Integer end, String[] orderBy, String[] order,
	    String[] filterBy, String[] filter);

    public ItemList findNext(Long itemListId, String orderBy, String fixBy, boolean ascending);

    public void clearSession();

    public Long deleteItemList(String title);
}