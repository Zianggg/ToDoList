package com.example.todolist;

public class ToDoListItems {
    private int ToDoItemID;
    private String title;
    private String description;
    private boolean isCompleted;

    public ToDoListItems(int toDoItemID, String title, String description, boolean isCompleted) {
        ToDoItemID = toDoItemID;
        this.title = title;
        this.description = description;
        this.isCompleted = false;
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

    @Override
    public String toString() {
        return title;
    }
}
