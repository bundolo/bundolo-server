package org.bundolo.model.enumeration;

public enum AuthorColumnType {
    author("username", ColumnDataType.text), firstName("first_name", ColumnDataType.text), lastName("last_name",
	    ColumnDataType.text), gender("gender", ColumnDataType.text), birthDate("birth_date", ColumnDataType.date), date(
	    "signup_date", ColumnDataType.date), lastLoginDate("last_login_date", ColumnDataType.date), description(
	    "descriptionContent.text", ColumnDataType.text), name("descriptionContent.name", ColumnDataType.text), activity(
	    "descriptionContent.lastActivity", ColumnDataType.date);
    // TODO check why descriptionContent.name is here

    private final String columnName;
    private final ColumnDataType columnDataType;

    private AuthorColumnType(String columnName, ColumnDataType columnDataType) {
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
