package org.bundolo.model.enumeration;

public enum TopicColumnType {
    author("author_username"), content_text("content_text"), date("creation_date");

    private final String topicColumnName;

    private TopicColumnType(String topicColumnName) {
	this.topicColumnName = topicColumnName;
    }

    public String getTopicColumnName() {
	return topicColumnName;
    }

    @Override
    public String toString() {
	return name() + ": " + getTopicColumnName();
    }
}
