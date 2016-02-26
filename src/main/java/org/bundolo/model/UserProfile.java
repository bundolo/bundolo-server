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
import javax.validation.constraints.NotNull;

import org.bundolo.model.enumeration.DigestKindType;
import org.bundolo.model.enumeration.UserProfileGenderType;
import org.bundolo.model.enumeration.UserProfileStatusType;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "user_profile")
public class UserProfile implements java.io.Serializable {

    private static final long serialVersionUID = -4042511031705727688L;

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "user_id_seq")
    @SequenceGenerator(name = "user_id_seq", sequenceName = "user_id_seq")
    private Long userId;

    @Column(name = "username")
    @NotNull
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "salt")
    private String salt;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy.")
    @Column(name = "birth_date")
    private Date birthDate;

    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private UserProfileGenderType gender;

    @Column(name = "email")
    private String email;

    @Column(name = "show_personal")
    private Boolean showPersonal;

    @Column(name = "signup_date")
    private Date signupDate;

    @Column(name = "last_login_date")
    private Date lastLoginDate;

    @Column(name = "last_ip")
    private String lastIp;

    @Column(name = "user_profile_status")
    @Enumerated(EnumType.STRING)
    private UserProfileStatusType userProfileStatus;

    @Column(name = "nonce")
    private String nonce;

    @Column(name = "new_email")
    private String newEmail;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "description_content_id")
    // @Transient
    private Content descriptionContent;

    @Column(name = "newsletter_subscription")
    private Boolean newsletterSubscription;

    @Column(name = "newsletter_sending_date")
    private Date newsletterSendingDate;

    @Column(name = "previous_activity")
    private Date previousActivity;

    @Column(name = "digest_subscription")
    @Enumerated(EnumType.STRING)
    private DigestKindType digestSubscription;

    public UserProfile() {
	super();
    }

    public Long getUserId() {
	return userId;
    }

    public void setUserId(Long userId) {
	this.userId = userId;
    }

    public String getUsername() {
	return username;
    }

    public void setUsername(String username) {
	this.username = username;
    }

    public String getPassword() {
	return password;
    }

    public void setPassword(String password) {
	this.password = password;
    }

    public String getSalt() {
	return salt;
    }

    public void setSalt(String salt) {
	this.salt = salt;
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

    public UserProfileGenderType getGender() {
	return gender;
    }

    public void setGender(UserProfileGenderType gender) {
	this.gender = gender;
    }

    public String getEmail() {
	return email;
    }

    public void setEmail(String email) {
	this.email = email;
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

    public String getLastIp() {
	return lastIp;
    }

    public void setLastIp(String lastIp) {
	this.lastIp = lastIp;
    }

    public UserProfileStatusType getUserProfileStatus() {
	return userProfileStatus;
    }

    public void setUserProfileStatus(UserProfileStatusType userProfileStatus) {
	this.userProfileStatus = userProfileStatus;
    }

    public Date getBirthDate() {
	return birthDate;
    }

    public void setBirthDate(Date birthDate) {
	this.birthDate = birthDate;
    }

    public String getNonce() {
	return nonce;
    }

    public void setNonce(String nonce) {
	this.nonce = nonce;
    }

    public String getNewEmail() {
	return newEmail;
    }

    public void setNewEmail(String newEmail) {
	this.newEmail = newEmail;
    }

    public Content getDescriptionContent() {
	return descriptionContent;
    }

    public void setDescriptionContent(Content descriptionContent) {
	this.descriptionContent = descriptionContent;
    }

    public Boolean getNewsletterSubscription() {
	return newsletterSubscription;
    }

    public void setNewsletterSubscription(Boolean newsletterSubscription) {
	this.newsletterSubscription = newsletterSubscription;
    }

    public Date getNewsletterSendingDate() {
	return newsletterSendingDate;
    }

    public void setNewsletterSendingDate(Date newsletterSendingDate) {
	this.newsletterSendingDate = newsletterSendingDate;
    }

    public Date getPreviousActivity() {
	return previousActivity;
    }

    public void setPreviousActivity(Date previousActivity) {
	this.previousActivity = previousActivity;
    }

    public DigestKindType getDigestSubscription() {
	return digestSubscription;
    }

    public void setDigestSubscription(DigestKindType digestSubscription) {
	this.digestSubscription = digestSubscription;
    }

    @Override
    public String toString() {
	return "UserProfile [userId=" + userId + ", username=" + username + ", password=" + password + ", salt=" + salt
		+ ", firstName=" + firstName + ", lastName=" + lastName + ", birthDate=" + birthDate + ", gender="
		+ gender + ", email=" + email + ", showPersonal=" + showPersonal + ", signupDate=" + signupDate
		+ ", lastLoginDate=" + lastLoginDate + ", lastIp=" + lastIp + ", userProfileStatus="
		+ userProfileStatus + ", nonce=" + nonce + ", newEmail=" + newEmail + ", descriptionContent="
		+ descriptionContent + ", newsletterSubscription=" + newsletterSubscription
		+ ", newsletterSendingDate=" + newsletterSendingDate + ", previousActivity=" + previousActivity
		+ ", digestSubscription=" + digestSubscription + "]";
    }
}
