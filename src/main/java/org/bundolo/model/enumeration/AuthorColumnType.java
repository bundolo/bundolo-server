package org.bundolo.model.enumeration;

public enum AuthorColumnType {
	author("username", ColumnDataType.text), firstName("firstName", ColumnDataType.text), lastName("lastName",
			ColumnDataType.text), gender("gender", ColumnDataType.text), birthDate("birthDate",
					ColumnDataType.date), date("signupDate", ColumnDataType.date), lastLoginDate("lastLoginDate",
							ColumnDataType.date), description("descriptionContent.text", ColumnDataType.text), name(
									"descriptionContent.name", ColumnDataType.text), activity(
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
