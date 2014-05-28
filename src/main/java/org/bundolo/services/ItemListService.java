package org.bundolo.services;

import java.util.List;

import org.bundolo.model.ItemList;

public interface ItemListService {

    public ItemList findItemList(Long itemListId);

    public void saveItemList(ItemList itemList) throws Exception;

    public void updateItemList(ItemList itemList) throws Exception;

    // public void saveOrUpdateItemList(ItemListDTO itemListDTO) throws
    // Exception;
    public void deleteItemList(Long itemListId) throws Exception;

    // public List<ItemListDTO> findAllItemLists() throws Exception; //TODO
    // probably not needed
    public ItemList findItemListByName(String itemListName, String authorUsername);

    public ItemList findItemListByName(String itemListName, String authorUsername, List<String> params);

    public ItemList findItemList(Long itemListId, List<String> params);
}
