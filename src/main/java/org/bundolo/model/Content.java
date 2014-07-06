package org.bundolo.model;

import java.util.Collection;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.bundolo.CustomDateSerializer;
import org.bundolo.model.enumeration.ContentKindType;
import org.bundolo.model.enumeration.ContentStatusType;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Entity
@FilterDef(name = "descriptionFilter", parameters = @ParamDef(name = "kind", type = "java.lang.String"))
@Table(name = "content")
public class Content implements java.io.Serializable {

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

    // @Column(name = "parent_content_id")
    // @Transient
    // private Long parentContentId;

    @Column(name = "kind")
    @Enumerated(EnumType.STRING)
    private ContentKindType kind;

    @Column(name = "content_name")
    private String name;

    @Column(name = "content_text")
    private String text;

    @Column(name = "locale")
    private String locale;

    @Column(name = "creation_date")
    private Date creationDate;

    @Column(name = "content_status")
    @Enumerated(EnumType.STRING)
    private ContentStatusType contentStatus;

    // @OneToOne(mappedBy = "parentContent", cascade = CascadeType.ALL)
    // @OneToOne(cascade=CascadeType.ALL)
    @Transient
    private Rating rating;

    // @OneToOne(fetch = FetchType.EAGER)
    // @JoinColumn(name = "parent_content_id", referencedColumnName =
    // "content_id")
    // @Transient
    @ManyToOne(optional = true)
    @JoinColumn(name = "parent_content_id", referencedColumnName = "content_id")
    @JsonBackReference
    // prevent circular
    private Content parentContent;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parentContent")
    @JsonManagedReference
    @Where(clause = "kind like '%_description'")
    private Collection<Content> description;

    public Content() {
	super();
    }

    public Content(Long contentId, String authorUsername, ContentKindType kind, String name, String text,
	    String locale, Date creationDate, ContentStatusType contentStatus, Rating rating) {
	super();
	this.contentId = contentId;
	this.authorUsername = authorUsername;
	this.kind = kind;
	this.name = name;
	this.text = text;
	this.locale = locale;
	this.creationDate = creationDate;
	this.contentStatus = contentStatus;
	this.rating = rating;
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

    @JsonSerialize(using = CustomDateSerializer.class)
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

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
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

    public Collection<Content> getDescription() {
	return description;
    }

    public void setDescription(Collection<Content> description) {
	this.description = description;
    }

    // @Override
    // public String toString() {
    // return "Content [contentId=" + contentId + ", authorUsername=" + authorUsername + ", kind=" + kind + ", name="
    // + name + ", text=" + text + ", locale=" + locale + ", creationDate=" + creationDate
    // + ", contentStatus=" + contentStatus + ", rating=" + rating + ", parentContent=" + parentContent
    // + ", description=" + description + "]";
    // }
}