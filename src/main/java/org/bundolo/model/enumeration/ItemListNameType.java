package org.bundolo.model.enumeration;

public enum ItemListNameType {
	new_texts("Latest texts"),
	forum_groups("Forum groups"),
	forum_group_topics("Forum group topics"),
	forum_topic_posts("Forum topic posts"),
	serials("Serials"),
	serial_episodes("Serial episodes"),
	authors("Authors"),
	latest_news("Latest news"),
	new_contests("Latest contests"),
	new_connections("Latest links"),
	new_forum_posts("Latest forum posts"),
	connection_groups("Link groups"),
	connection_group_entries("Link group entries"),
	author_texts("Author texts");
	
	private final String itemListName;
	
	private ItemListNameType(String itemListName) {
		this.itemListName = itemListName;
	}

	public String getItemListName() {
		return itemListName;
	}

	@Override
	public String toString() {
		return name() + ": " + getItemListName();
	}
}