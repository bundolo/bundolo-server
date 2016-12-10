package org.bundolo.model.enumeration;

public enum CommentColumnType {
	author("authorUsername", ColumnDataType.text), text("text", ColumnDataType.text), date("creationDate",
			ColumnDataType.date), activity("lastActivity", ColumnDataType.date), random("RANDOM()",
					ColumnDataType.text), ancestorActivity("ancestorContent.lastActivity", ColumnDataType.date);

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