package org.bundolo.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.bundolo.model.enumeration.ItemListKindType;
import org.bundolo.model.enumeration.ItemListStatusType;

@Entity
@Table(name = "item_list")
public class ItemList implements java.io.Serializable {

    /**
	 * 
	 */
    private static final long serialVersionUID = -6244912323759787467L;

    @Id
    @Column(name = "item_list_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "item_list_id_seq")
    @SequenceGenerator(name = "item_list_id_seq", sequenceName = "item_list_id_seq")
    private Long itemListId;

    @Column(name = "author_username")
    private String authorUsername;

    @Column(name = "item_list_status")
    @Enumerated(EnumType.STRING)
    private ItemListStatusType itemListStatus;

    @Column(name = "kind")
    @Enumerated(EnumType.STRING)
    private ItemListKindType kind;

    @Column(name = "creation_date")
    private Date creationDate;

    @Column(name = "query")
    private String query;

    @Column(name = "description_content_id")
    private Long descriptionContentId;

    @Transient
    private Content descriptionContent;

    public ItemList() {
	super();
    }

    public ItemList(Long itemListId, String authorUsername, ItemListStatusType itemListStatus, ItemListKindType kind,
	    Date creationDate, String query, Long descriptionContentId) {
	super();
	this.itemListId = itemListId;
	this.authorUsername = authorUsername;
	this.itemListStatus = itemListStatus;
	this.kind = kind;
	this.creationDate = creationDate;
	this.query = query;
	this.descriptionContentId = descriptionContentId;
    }

    public Long getItemListId() {
	return itemListId;
    }

    public void setItemListId(Long itemListId) {
	this.itemListId = itemListId;
    }

    public ItemListStatusType getItemListStatus() {
	return itemListStatus;
    }

    public void setItemListStatus(ItemListStatusType itemListStatus) {
	this.itemListStatus = itemListStatus;
    }

    public ItemListKindType getKind() {
	return kind;
    }

    public void setKind(ItemListKindType kind) {
	this.kind = kind;
    }

    public Date getCreationDate() {
	return creationDate;
    }

    public void setCreationDate(Date creationDate) {
	this.creationDate = creationDate;
    }

    public String getQuery() {
	return query;
    }

    public void setQuery(String query) {
	this.query = query;
    }

    public String getAuthorUsername() {
	return authorUsername;
    }

    public void setAuthorUsername(String authorUsername) {
	this.authorUsername = authorUsername;
    }

    public Long getDescriptionContentId() {
	return descriptionContentId;
    }

    public void setDescriptionContentId(Long descriptionContentId) {
	this.descriptionContentId = descriptionContentId;
    }

    public Content getDescriptionContent() {
	return descriptionContent;
    }

    public void setDescriptionContent(Content descriptionContent) {
	this.descriptionContent = descriptionContent;
    }

}
