package org.bundolo.model.enumeration;

public enum ContestColumnType {
    author("author_username"), date("creation_date"), url("url"), expirationDate("expiration_date"), description(
	    "descriptionContent.text"), title("descriptionContent.name"), activity("descriptionContent.lastActivity");

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
