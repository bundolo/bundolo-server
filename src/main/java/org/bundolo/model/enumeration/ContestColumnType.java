package org.bundolo.model.enumeration;

public enum ContestColumnType {
    author("author_username"), date("creation_date"), url("url"), expirationDate("expiration_date"), description(
	    "descriptionContent.text"), name("descriptionContent.name");

    private final String contestColumnName;

    private ContestColumnType(String contestColumnName) {
	this.contestColumnName = contestColumnName;
    }

    public String getContestColumnName() {
	return contestColumnName;
    }

    @Override
    public String toString() {
	return name() + ": " + getContestColumnName();
    }
}
