package org.bundolo.model.enumeration;

public enum SerialColumnType {
    author("author_username"), content_name("content_name"), content_text("content_text"), date("creation_date");

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
