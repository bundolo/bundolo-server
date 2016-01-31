package org.bundolo.model;

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
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.bundolo.model.enumeration.RatingKindType;
import org.bundolo.model.enumeration.RatingStatusType;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "rating")
public class Rating implements java.io.Serializable {

    private static final long serialVersionUID = 7440297955003302414L;

    @Id
    @Column(name = "rating_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "rating_id_seq")
    @SequenceGenerator(name = "rating_id_seq", sequenceName = "rating_id_seq")
    private Long ratingId;

    @Column(name = "author_username")
    private String authorUsername;

    @Column(name = "kind")
    @Enumerated(EnumType.STRING)
    private RatingKindType kind;

    @Column(name = "last_activity")
    private Date lastActivity;

    @Column(name = "rating_status")
    @Enumerated(EnumType.STRING)
    private RatingStatusType ratingStatus;

    @Column(name = "value")
    private Long value;

    @Column(name = "historical")
    private Long historical;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_content_id", nullable = true)
    @JsonBackReference
    private Content parentContent;

    public Rating() {
	super();
    }

    public Rating(Long ratingId, String authorUsername, RatingKindType kind, Date lastActivity,
	    RatingStatusType ratingStatus, Long value, Long historical, Content parentContent) {
	super();
	this.ratingId = ratingId;
	this.authorUsername = authorUsername;
	this.kind = kind;
	this.lastActivity = lastActivity;
	this.ratingStatus = ratingStatus;
	this.value = value;
	this.historical = historical;
	this.parentContent = parentContent;
    }

    public Long getRatingId() {
	return ratingId;
    }

    public void setRatingId(Long ratingId) {
	this.ratingId = ratingId;
    }

    public String getAuthorUsername() {
	return authorUsername;
    }

    public void setAuthorUsername(String authorUsername) {
	this.authorUsername = authorUsername;
    }

    public RatingKindType getKind() {
	return kind;
    }

    public void setKind(RatingKindType kind) {
	this.kind = kind;
    }

    public RatingStatusType getRatingStatus() {
	return ratingStatus;
    }

    public void setRatingStatus(RatingStatusType ratingStatus) {
	this.ratingStatus = ratingStatus;
    }

    public Long getValue() {
	return value;
    }

    public void setValue(Long value) {
	this.value = value;
    }

    public Content getParentContent() {
	return parentContent;
    }

    public void setParentContent(Content parentContent) {
	this.parentContent = parentContent;
    }

    public Date getLastActivity() {
	return lastActivity;
    }

    public void setLastActivity(Date lastActivity) {
	this.lastActivity = lastActivity;
    }

    public Long getHistorical() {
	return historical;
    }

    public void setHistorical(Long historical) {
	this.historical = historical;
    }

    @Override
    public String toString() {
	return "Rating [ratingId=" + ratingId + ", authorUsername=" + authorUsername + ", kind=" + kind
		+ ", lastActivity=" + lastActivity + ", ratingStatus=" + ratingStatus + ", value=" + value + "]";
    }
}