package org.bundolo.model;

import java.util.Collection;
import java.util.Date;

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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.bundolo.model.enumeration.ContentKindType;
import org.bundolo.model.enumeration.ContentStatusType;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "content")
public class Comment implements java.io.Serializable {

	private static final long serialVersionUID = 7440297955003302414L;

	@Id
	@Column(name = "content_id")
	// @GeneratedValue(strategy = GenerationType.SEQUENCE,
	// generator="page_id_seq")
	@GeneratedValue(strategy = GenerationType.IDENTITY, generator = "content_id_seq")
	@SequenceGenerator(name = "content_id_seq", sequenceName = "content_id_seq")
	private Long contentId;

	@Column(name = "author_username")
	private String authorUsername;

	@Column(name = "kind")
	@Enumerated(EnumType.STRING)
	private ContentKindType kind;

	@Column(name = "content_text")
	private String text;

	@Column(name = "locale")
	private String locale;

	@Column(name = "creation_date")
	private Date creationDate;

	@Column(name = "last_activity")
	private Date lastActivity;

	@Column(name = "content_status")
	@Enumerated(EnumType.STRING)
	private ContentStatusType contentStatus;

	// not used for now. it will be needed for personalised hiding of comments
	// @OneToOne(mappedBy = "parentContent", cascade = CascadeType.ALL)
	// @OneToOne(cascade=CascadeType.ALL)
	@Transient
	private Rating rating;

	@ManyToOne(optional = true)
	@JoinColumn(name = "parent_content_id", referencedColumnName = "content_id")
	// @JsonBackReference
	private Content parentContent;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "parentContent", fetch = FetchType.EAGER)
	// @JsonManagedReference
	@Where(clause = "kind like '%_comment'")
	@OrderBy("creationDate")
	private Collection<Comment> comments;

	@ManyToOne(optional = true)
	@JoinColumn(name = "ancestor_content_id", referencedColumnName = "content_id")
	// @JsonBackReference
	private Content ancestorContent;

	@Column(name = "avatar_url")
	private String avatarUrl;

	public Comment() {
		super();
	}

	public Long getContentId() {
		return contentId;
	}

	public void setContentId(Long contentId) {
		this.contentId = contentId;
	}

	public String getAuthorUsername() {
		return authorUsername;
	}

	public void setAuthorUsername(String authorUsername) {
		this.authorUsername = authorUsername;
	}

	public ContentKindType getKind() {
		return kind;
	}

	public void setKind(ContentKindType kind) {
		this.kind = kind;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public ContentStatusType getContentStatus() {
		return contentStatus;
	}

	public void setContentStatus(ContentStatusType contentStatus) {
		this.contentStatus = contentStatus;
	}

	public Rating getRating() {
		return rating;
	}

	public void setRating(Rating rating) {
		this.rating = rating;
	}

	public Content getParentContent() {
		return parentContent;
	}

	public void setParentContent(Content parentContent) {
		this.parentContent = parentContent;
	}

	public Collection<Comment> getComments() {
		return comments;
	}

	public void setComments(Collection<Comment> comments) {
		this.comments = comments;
	}

	public Date getLastActivity() {
		return lastActivity;
	}

	public void setLastActivity(Date lastActivity) {
		this.lastActivity = lastActivity;
	}

	public Content getAncestorContent() {
		return ancestorContent;
	}

	public void setAncestorContent(Content ancestorContent) {
		this.ancestorContent = ancestorContent;
	}

	public String getAvatarUrl() {
		return avatarUrl;
	}

	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}

	@Override
	public String toString() {
		return "Comment [contentId=" + contentId + ", authorUsername=" + authorUsername + ", kind=" + kind + ", text="
				+ text + ", locale=" + locale + ", creationDate=" + creationDate + ", lastActivity=" + lastActivity
				+ ", contentStatus=" + contentStatus + ", rating=" + rating + ", parentContent=" + parentContent
				+ ", ancestorContent=" + ancestorContent + ", avatarUrl=" + avatarUrl + ", comments=" + comments + "]";
	}

}