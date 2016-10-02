package org.bundolo.model.enumeration;

public enum ItemListColumnType {
	author("authorUsername", ColumnDataType.text), date("creationDate", ColumnDataType.date), description(
			"descriptionContent.text",
			ColumnDataType.text), title("descriptionContent.name", ColumnDataType.text), activity(
					"descriptionContent.lastActivity", ColumnDataType.date), kind("kind", ColumnDataType.text);

	private final String columnName;
	private final ColumnDataType columnDataType;

	private ItemListColumnType(String columnName, ColumnDataType columnDataType) {
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
