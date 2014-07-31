package org.bundolo.model.enumeration;

public enum SerialColumnType {
    author("author_username"), title("content_name"), text("content_text"), date("creation_date"), activity(
	    "last_activity");

    private final String serialColumnName;

    private SerialColumnType(String serialColumnName) {
	this.serialColumnName = serialColumnName;
    }

    public String getSerialColumnName() {
	return serialColumnName;
    }

    @Override
    public String toString() {
	return name() + ": " + getSerialColumnName();
    }
}
