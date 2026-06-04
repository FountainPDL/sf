package com.surffountain.browser.models;

public class ChatMessage {

    public enum Role { USER, ASSISTANT, SYSTEM }

    private final String content;
    private final Role role;
    private final long timestamp;

    public ChatMessage(String content, Role role) {
        this.content = content;
        this.role = role;
        this.timestamp = System.currentTimeMillis();
    }

    public String getContent() { return content; }
    public Role getRole() { return role; }
    public long getTimestamp() { return timestamp; }
    public boolean isUser() { return role == Role.USER; }
}
