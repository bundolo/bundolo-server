package org.bundolo.model.enumeration;

public enum TopicColumnType {
    author("author_username"), title("content_name"), date("creation_date"), activity("last_activity");

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
