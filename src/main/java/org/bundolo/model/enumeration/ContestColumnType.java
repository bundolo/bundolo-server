package org.bundolo.model.enumeration;

public enum ContestColumnType {
    author("author_username", ColumnDataType.text), date("creationDate", ColumnDataType.date), url("url",
	    ColumnDataType.text), expirationDate("expiration_date", ColumnDataType.text), description(
	    "descriptionContent.text", ColumnDataType.text), title("descriptionContent.name", ColumnDataType.text), activity(
	    "descriptionContent.lastActivity", ColumnDataType.date);
    // TODO url is probably not needed

    private final String columnName;
    private final ColumnDataType columnDataType;

    private ContestColumnType(String columnName, ColumnDataType columnDataType) {
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
