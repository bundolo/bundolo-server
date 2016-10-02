package org.bundolo.model.enumeration;

public enum AnnouncementColumnType {
	author("author_username", ColumnDataType.text), title("name", ColumnDataType.text), text("content_text",
			ColumnDataType.text), date("creationDate",
					ColumnDataType.date), activity("lastActivity", ColumnDataType.date);

	private final String columnName;
	private final ColumnDataType columnDataType;

	private AnnouncementColumnType(String columnName, ColumnDataType columnDataType) {
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
