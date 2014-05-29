package org.bundolo.model.enumeration;

public enum TopicColumnType {
    author("author_username"), email("email"), url("url"), date("creation_date"), description("descriptionContent.text"), name(
	    "descriptionContent.name");

    private final String connectionColumnName;

    private TopicColumnType(String connectionColumnName) {
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
