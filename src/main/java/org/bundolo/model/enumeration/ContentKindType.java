package org.bundolo.model.enumeration;

public enum ContentKindType {
    page_description(""), page_comment(""), forum_group(""), forum_topic("topic"), forum_post(""), text("text"), text_description(
	    ""), text_comment(""), item_list_description("item_list"), item_list_comment(""), connection_group(""), connection_description(
	    "connection"), connection_comment(""), news("announcement"), news_comment(""), contest_description(
	    "contest"), contest_comment(""), episode_group("serial"), episode_group_comment(""), episode("episode"), episode_comment(
	    ""), event(""), event_comment(""), label(""), label_comment(""), user_description("author"), user_comment(
	    "");

    private final String localizedName;

    private ContentKindType(String localizedName) {
	this.localizedName = localizedName;
    }

    // TODO this should use Spring i18n
    public String getLocalizedName() {
	return localizedName;
    }

    @Override
    public String toString() {
	return name();
    }
}