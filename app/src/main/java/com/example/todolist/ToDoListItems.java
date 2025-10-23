package com.example.todolist;

public class ToDoListItems {
    private int ToDoItemID;
    private String title;
    private String description;
    private boolean isCompleted;
    // Unified single datetime (epoch millis), replaces start/end range
    private Long dateTimeMillis; // nullable

    public ToDoListItems(int toDoItemID, String title, String description, boolean isCompleted) {
        ToDoItemID = toDoItemID;    
        this.title = title;
        this.description = description;
        this.isCompleted = isCompleted;
    }

    public ToDoListItems(int toDoItemID, String title, String description, boolean isCompleted, Long startDateMillis, Long endDateMillis) {
        ToDoItemID = toDoItemID;
        this.title = title;
        this.description = description;
        this.isCompleted = isCompleted;
        // For backward-compat: map start to unified datetime, ignore end
        this.dateTimeMillis = startDateMillis;
    }

    // Preferred constructor using unified datetime
    public ToDoListItems(int toDoItemID, String title, String description, boolean isCompleted, Long dateTimeMillis) {
        ToDoItemID = toDoItemID;
        this.title = title;
        this.description = description;
        this.isCompleted = isCompleted;
        this.dateTimeMillis = dateTimeMillis;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public int getToDoItemID() {
        return ToDoItemID;
    }

    public void setToDoItemID(int toDoItemID) {
        ToDoItemID = toDoItemID;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    // New unified datetime accessors
    public Long getDateTimeMillis() {
        return dateTimeMillis;
    }

    public void setDateTimeMillis(Long dateTimeMillis) {
        this.dateTimeMillis = dateTimeMillis;
    }

    // Backward-compat methods to avoid breaking existing calls
    public Long getStartDateMillis() {
        return dateTimeMillis;
    }

    public void setStartDateMillis(Long startDateMillis) {
        this.dateTimeMillis = startDateMillis;
    }

    public Long getEndDateMillis() {
        return null;
    }

    public void setEndDateMillis(Long endDateMillis) {
        // no-op; end datetime removed
    }

    @Override
    public String toString() {
        return title;
    }
}
