package org.bundolo.model.enumeration;

public enum AuthorItemsColumnType {
	author("c.authorUsername", ColumnDataType.text), title("c.name", ColumnDataType.text), text("c.text",
			ColumnDataType.text), date("c.creationDate", ColumnDataType.date), activity("c.lastActivity",
					ColumnDataType.date), kind("c.kind", ColumnDataType.text), rating("r.value", ColumnDataType.number);

	private final String columnName;
	private final ColumnDataType columnDataType;

	private AuthorItemsColumnType(String columnName, ColumnDataType columnDataType) {
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
