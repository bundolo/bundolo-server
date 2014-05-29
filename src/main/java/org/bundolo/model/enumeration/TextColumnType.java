package org.bundolo.model.enumeration;

public enum TextColumnType {
    // TODO description
    author("author_username"), content_name("content_name"), content_text("content_text"), date("creation_date");

    private final String textColumnName;

    private TextColumnType(String textColumnName) {
	this.textColumnName = textColumnName;
    }

    public String getTextColumnName() {
	return textColumnName;
    }

    @Override
    public String toString() {
	return name() + ": " + getTextColumnName();
    }
}
