package org.bundolo.model;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
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

	// @Column(name = "description_content_id")
	// private Long descriptionContentId;

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "description_content_id")
	// @Transient
	private Content descriptionContent;

	@Transient
	private List<Content> items;

	public ItemList() {
		super();
	}

	public ItemList(Long itemListId, String authorUsername, ItemListStatusType itemListStatus, ItemListKindType kind,
			Date creationDate, String query, Content descriptionContent) {
		super();
		this.itemListId = itemListId;
		this.authorUsername = authorUsername;
		this.itemListStatus = itemListStatus;
		this.kind = kind;
		this.creationDate = creationDate;
		this.query = query;
		this.descriptionContent = descriptionContent;
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

	public Content getDescriptionContent() {
		return descriptionContent;
	}

	public void setDescriptionContent(Content descriptionContent) {
		this.descriptionContent = descriptionContent;
	}

	public List<Content> getItems() {
		return items;
	}

	public void setItems(List<Content> items) {
		this.items = items;
	}

	@Override
	public String toString() {
		return "ItemList [itemListId=" + itemListId + ", authorUsername=" + authorUsername + ", itemListStatus="
				+ itemListStatus + ", kind=" + kind + ", creationDate=" + creationDate + ", query=" + query
				+ ", descriptionContent=" + descriptionContent + "]";
	}

}
