package org.bundolo.model.enumeration;

public enum ConnectionColumnType {
	author("authorUsername", ColumnDataType.text), email("email", ColumnDataType.text), url("url",
			ColumnDataType.text), date("creationDate", ColumnDataType.date), description("descriptionContent.text",
					ColumnDataType.text), title("descriptionContent.name", ColumnDataType.text), activity(
							"descriptionContent.lastActivity",
							ColumnDataType.date), group("parentContent.name", ColumnDataType.text);

	private final String columnName;
	private final ColumnDataType columnDataType;

	private ConnectionColumnType(String columnName, ColumnDataType columnDataType) {
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