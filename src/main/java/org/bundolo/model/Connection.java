package org.bundolo.model;

import java.util.Date;

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

import org.bundolo.CustomDateSerializer;
import org.bundolo.model.enumeration.ConnectionKindType;
import org.bundolo.model.enumeration.ConnectionStatusType;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Entity
@Table(name = "connection")
public class Connection implements java.io.Serializable {

    private static final long serialVersionUID = 7440297955003302414L;

    @Id
    @Column(name = "connection_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "connection_id_seq")
    @SequenceGenerator(name = "connection_id_seq", sequenceName = "connection_id_seq")
    private Long connectionId;

    @Column(name = "author_username")
    private String authorUsername;

    @Column(name = "parent_content_id")
    private Long parentContentId;

    // @Column(name = "description_content_id")
    // private Long descriptionContentId;

    @Column(name = "kind")
    @Enumerated(EnumType.STRING)
    private ConnectionKindType kind;

    @Column(name = "creation_date")
    private Date creationDate;

    @Column(name = "connection_status")
    @Enumerated(EnumType.STRING)
    private ConnectionStatusType connectionStatus;

    @Column(name = "email")
    private String email;

    @Column(name = "url")
    private String url;

    @OneToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "description_content_id", nullable = true)
    // @Transient
    private Content descriptionContent;

    public Connection() {
	super();
    }

    public Connection(Long connectionId, String authorUsername, Long parentContentId, /*Long descriptionContentId,*/
	    ConnectionKindType kind, Date creationDate, ConnectionStatusType connectionStatus, String email,
	    String url, Content descriptionContent) {
	super();
	this.connectionId = connectionId;
	this.authorUsername = authorUsername;
	this.parentContentId = parentContentId;
	// this.descriptionContentId = descriptionContentId;
	this.kind = kind;
	this.creationDate = creationDate;
	this.connectionStatus = connectionStatus;
	this.email = email;
	this.url = url;
	this.descriptionContent = descriptionContent;
    }

    public Long getConnectionId() {
	return connectionId;
    }

    public void setConnectionId(Long connectionId) {
	this.connectionId = connectionId;
    }

    public String getAuthorUsername() {
	return authorUsername;
    }

    public void setAuthorUsername(String authorUsername) {
	this.authorUsername = authorUsername;
    }

    // public Long getDescriptionContentId() {
    // return descriptionContentId;
    // }
    //
    // public void setDescriptionContentId(Long descriptionContentId) {
    // this.descriptionContentId = descriptionContentId;
    // }

    public ConnectionKindType getKind() {
	return kind;
    }

    public void setKind(ConnectionKindType kind) {
	this.kind = kind;
    }

    @JsonSerialize(using = CustomDateSerializer.class)
    public Date getCreationDate() {
	return creationDate;
    }

    public void setCreationDate(Date creationDate) {
	this.creationDate = creationDate;
    }

    public ConnectionStatusType getConnectionStatus() {
	return connectionStatus;
    }

    public void setConnectionStatus(ConnectionStatusType connectionStatus) {
	this.connectionStatus = connectionStatus;
    }

    public String getEmail() {
	return email;
    }

    public void setEmail(String email) {
	this.email = email;
    }

    public String getUrl() {
	return url;
    }

    public void setUrl(String url) {
	this.url = url;
    }

    public Long getParentContentId() {
	return parentContentId;
    }

    public void setParentContentId(Long parentContentId) {
	this.parentContentId = parentContentId;
    }

    public Content getDescriptionContent() {
	return descriptionContent;
    }

    public void setDescriptionContent(Content descriptionContent) {
	this.descriptionContent = descriptionContent;
    }
}