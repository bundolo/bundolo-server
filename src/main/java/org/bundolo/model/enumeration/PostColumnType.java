package org.bundolo.model.enumeration;

public enum PostColumnType {
	author("authorUsername", ColumnDataType.text), text("text", ColumnDataType.text), date("creationDate",
			ColumnDataType.date), topic("parentContent.slug",
					ColumnDataType.text), parentActivity("parentContent.lastActivity", ColumnDataType.date);

	private final String columnName;
	private final ColumnDataType columnDataType;

	private PostColumnType(String columnName, ColumnDataType columnDataType) {
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