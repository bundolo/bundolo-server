package org.bundolo.model.enumeration;

public enum AuthorInteractionsColumnType {
	author("author_username", ColumnDataType.text), title("content_name", ColumnDataType.text), text("content_text",
			ColumnDataType.text), date("creation_date", ColumnDataType.date), activity("last_activity",
					ColumnDataType.date), kind("kind", ColumnDataType.text);
	// TODO ordering by deltas

	private final String columnName;
	private final ColumnDataType columnDataType;

	private AuthorInteractionsColumnType(String columnName, ColumnDataType columnDataType) {
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
