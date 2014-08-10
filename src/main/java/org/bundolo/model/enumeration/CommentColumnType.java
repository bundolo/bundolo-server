package org.bundolo.model.enumeration;

public enum CommentColumnType {
    author("author_username", ColumnDataType.text), text("content_text", ColumnDataType.text), date("creation_date",
	    ColumnDataType.date), activity("last_activity", ColumnDataType.date);

    private final String columnName;
    private final ColumnDataType columnDataType;

    private CommentColumnType(String columnName, ColumnDataType columnDataType) {
	this.columnName = columnName;
	this.columnDataType = columnDataType;
    }

    public String getColumnName() {
	return columnName;
    }

    public ColumnDataType getColumnDataType() {
	return columnDataType;
    }

    @Override
    public String toString() {
	return name() + ": " + getColumnName();
    }
}