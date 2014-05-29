package org.bundolo.model.enumeration;

public enum UserProfileColumnType {
    author("username"), firstName("first_name"), lastName("last_name"), gender("gender"), birthDate("birth_date"), date(
	    "signup_date"), lastLoginDate("last_login_date"), description("descriptionContent.text"), name(
	    "descriptionContent.name");

    private final String userProfileColumnName;

    private UserProfileColumnType(String userProfileColumnName) {
	this.userProfileColumnName = userProfileColumnName;
    }

    public String getUserProfileColumnName() {
	return userProfileColumnName;
    }

    @Override
    public String toString() {
	return name() + ": " + getUserProfileColumnName();
    }
}
