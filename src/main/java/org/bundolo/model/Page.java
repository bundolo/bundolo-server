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
import javax.validation.constraints.NotNull;

import org.bundolo.model.enumeration.PageKindType;
import org.bundolo.model.enumeration.PageStatusType;

@Entity
@Table(name = "page")
public class Page implements java.io.Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 4789175881499639634L;

	@Id
	@Column(name = "page_id")
	// @GeneratedValue(strategy = GenerationType.SEQUENCE,
	// generator="page_id_seq")
	@GeneratedValue(strategy = GenerationType.IDENTITY, generator = "page_id_seq")
	@SequenceGenerator(name = "page_id_seq", sequenceName = "page_id_seq")
	private Long pageId;

	@Column(name = "author_username")
	private String authorUsername;

	@Column(name = "parent_page_id")
	private Long parentPageId;

	@Column(name = "display_order")
	private Integer displayOrder;

	@Column(name = "page_status")
	@Enumerated(EnumType.STRING)
	private PageStatusType pageStatus;

	@Column(name = "kind")
	@Enumerated(EnumType.STRING)
	private PageKindType kind;

	@Column(name = "creation_date")
	private Date creationDate;

	@Column(name = "description_content_id")
	@NotNull
	private Long descriptionContentId;

	// @Column(name="description_content_id")
	// @OneToMany(fetch = FetchType.EAGER, targetEntity=Content.class)
	// @JoinColumn(name="content_id")
	// private Set<Content> descriptionContent;

	public Page() {
		super();
	}

	public Page(Long pageId, String authorUsername, Long parentPageId, Integer displayOrder, PageStatusType pageStatus,
			PageKindType kind, Date creationDate, Long descriptionContentId) {
		super();
		this.pageId = pageId;
		this.authorUsername = authorUsername;
		this.parentPageId = parentPageId;
		this.displayOrder = displayOrder;
		this.pageStatus = pageStatus;
		this.kind = kind;
		this.creationDate = creationDate;
		this.descriptionContentId = descriptionContentId;
	}

	public Long getPageId() {
		return pageId;
	}

	public void setPageId(Long pageId) {
		this.pageId = pageId;
	}

	public Integer getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}

	public PageStatusType getPageStatus() {
		return pageStatus;
	}

	public void setPageStatus(PageStatusType pageStatus) {
		this.pageStatus = pageStatus;
	}

	public PageKindType getKind() {
		return kind;
	}

	public void setKind(PageKindType kind) {
		this.kind = kind;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getAuthorUsername() {
		return authorUsername;
	}

	public void setAuthorUsername(String authorUsername) {
		this.authorUsername = authorUsername;
	}

	public Long getParentPageId() {
		return parentPageId;
	}

	public void setParentPageId(Long parentPageId) {
		this.parentPageId = parentPageId;
	}

	public Long getDescriptionContentId() {
		return descriptionContentId;
	}

	public void setDescriptionContentId(Long descriptionContentId) {
		this.descriptionContentId = descriptionContentId;
	}

	// public Set<Content> getDescriptionContent() {
	// return descriptionContent;
	// }
	//
	// public void setDescriptionContent(Set<Content> descriptionContent) {
	// this.descriptionContent = descriptionContent;
	// }
}
