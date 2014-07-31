package org.bundolo.model.enumeration;

public enum AuthorColumnType {
    author("username"), firstName("first_name"), lastName("last_name"), gender("gender"), birthDate("birth_date"), date(
	    "signup_date"), lastLoginDate("last_login_date"), description("descriptionContent.text"), name(
	    "descriptionContent.name"), activity("descriptionContent.lastActivity");

    private final String authorColumnName;

    private AuthorColumnType(String authorColumnName) {
	this.authorColumnName = authorColumnName;
    }

    public String getAuthorColumnName() {
	return authorColumnName;
    }

    @Override
    public String toString() {
	return name() + ": " + getAuthorColumnName();
    }
}
