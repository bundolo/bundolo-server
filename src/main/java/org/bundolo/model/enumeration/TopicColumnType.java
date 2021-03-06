package org.bundolo.model.enumeration;

public enum TopicColumnType {
	author("authorUsername", ColumnDataType.text), title("name", ColumnDataType.text), date("creationDate",
			ColumnDataType.date), activity("lastActivity", ColumnDataType.date), group("parentContent.name",
					ColumnDataType.text);

	private final String columnName;
	private final ColumnDataType columnDataType;

	private TopicColumnType(String columnName, ColumnDataType columnDataType) {
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