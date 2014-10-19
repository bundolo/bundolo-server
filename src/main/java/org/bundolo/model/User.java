package org.bundolo.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.bundolo.model.enumeration.UserProfileGenderType;

@Entity
@Table(name = "user_profile")
public class User implements java.io.Serializable {

    private static final long serialVersionUID = -4042511031705727688L;

    @Id
    @Column(name = "username")
    @NotNull
    private String username;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "birth_date")
    private Date birthDate;

    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private UserProfileGenderType gender;

    @Column(name = "show_personal")
    private Boolean showPersonal;

    @Column(name = "signup_date")
    private Date signupDate;

    @Column(name = "last_login_date")
    private Date lastLoginDate;

    @Column(name = "avatar_url")
    private String avatarUrl;

    // @Column(name = "session_id")
    // private String sessionId;

    // @Transient
    // private String rememberMeCookie;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "description_content_id")
    private Content descriptionContent;

    // @Column(name = "user_profile_status")
    // @Enumerated(EnumType.STRING)
    // @Transient
    // private UserProfileStatusType userStatus;

    public User() {
	super();
    }

    public String getUsername() {
	return username;
    }

    public void setUsername(String username) {
	this.username = username;
    }

    public String getFirstName() {
	return firstName;
    }

    public void setFirstName(String firstName) {
	this.firstName = firstName;
    }

    public String getLastName() {
	return lastName;
    }

    public void setLastName(String lastName) {
	this.lastName = lastName;
    }

    public Date getBirthDate() {
	return birthDate;
    }

    public void setBirthDate(Date birthDate) {
	this.birthDate = birthDate;
    }

    public UserProfileGenderType getGender() {
	return gender;
    }

    public void setGender(UserProfileGenderType gender) {
	this.gender = gender;
    }

    public Boolean getShowPersonal() {
	return showPersonal;
    }

    public void setShowPersonal(Boolean showPersonal) {
	this.showPersonal = showPersonal;
    }

    public Date getSignupDate() {
	return signupDate;
    }

    public void setSignupDate(Date signupDate) {
	this.signupDate = signupDate;
    }

    public Date getLastLoginDate() {
	return lastLoginDate;
    }

    public void setLastLoginDate(Date lastLoginDate) {
	this.lastLoginDate = lastLoginDate;
    }

    public String getAvatarUrl() {
	return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
	this.avatarUrl = avatarUrl;
    }

    // public String getSessionId() {
    // return sessionId;
    // }
    //
    // public void setSessionId(String sessionId) {
    // this.sessionId = sessionId;
    // }

    // public String getRememberMeCookie() {
    // return rememberMeCookie;
    // }
    //
    // public void setRememberMeCookie(String rememberMeCookie) {
    // this.rememberMeCookie = rememberMeCookie;
    // }

    public Content getDescriptionContent() {
	return descriptionContent;
    }

    public void setDescriptionContent(Content descriptionContent) {
	this.descriptionContent = descriptionContent;
    }

    // public UserProfileStatusType getUserStatus() {
    // return userStatus;
    // }
    //
    // public void setUserStatus(UserProfileStatusType userStatus) {
    // this.userStatus = userStatus;
    // }
}
