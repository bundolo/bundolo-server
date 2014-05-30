package org.bundolo.model.enumeration;

public enum AnnouncementColumnType {
    author("author_username"), title("content_name"), text("content_text"), date("creation_date");

    private final String announcementColumnName;

    private AnnouncementColumnType(String announcementColumnName) {
	this.announcementColumnName = announcementColumnName;
    }

    public String getAnnouncementColumnName() {
	return announcementColumnName;
    }

    @Override
    public String toString() {
	return name() + ": " + getAnnouncementColumnName();
    }
}
