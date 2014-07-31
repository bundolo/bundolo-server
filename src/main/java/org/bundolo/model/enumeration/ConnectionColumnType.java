package org.bundolo.model.enumeration;

public enum ConnectionColumnType {
    author("author_username"), email("email"), url("url"), date("creation_date"), description("descriptionContent.text"), title(
	    "descriptionContent.name"), activity("descriptionContent.lastActivity");

    private final String connectionColumnName;

    private ConnectionColumnType(String connectionColumnName) {
	this.connectionColumnName = connectionColumnName;
    }

    public String getConnectionColumnName() {
	return connectionColumnName;
    }

    @Override
    public String toString() {
	return name() + ": " + getConnectionColumnName();
    }
}
