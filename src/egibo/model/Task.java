/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package egibo.model;
import java.time.*;

public abstract class Task {
    private static int taskCounter = 0;
    private String title;
    private double hours;
    private int importance;
    private LocalDate dueDate;
    protected Status status = Status.PENDING;
    private String description;
    private int taskId = -1;

    // --- Lifecycle metadata (Phase 3) ---
    private LocalDateTime createdAt = LocalDateTime.now();  // set once, when the task is created
    private LocalDateTime completedAt;                       // null until marked DONE
    
    Task (String title, String description, double hours, int importance, LocalDate dueDate){
         if (Double.isNaN(hours) || hours <= 0) {
            throw new IllegalArgumentException("Hours must be greater than 0.");
        }  
        if (title == null || title.trim().isEmpty()){
            this.title = "Task" + ++taskCounter;
        } 
        else {
            this.title = title.trim();
        }
        
        this.description = description;
        
        if (hours > StudentProfile.MAX_CAPACITY){
            throw new IllegalArgumentException("A single task cannot be more than " + (int) StudentProfile.MAX_CAPACITY + "hours.");
        }
        
        if (importance < 1 || importance > 5){
            throw new IllegalArgumentException("Importance must be from 1 to 5.");
        }
        
        this.hours = hours;
        this.importance = importance;
        // FIX: the null default used to be overwritten by the plain assignment, leaving dueDate null
        this.dueDate = (dueDate == null) ? LocalDate.now() : dueDate;
        this.status = Status.PENDING;
    }
    
    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public void markDone(){
        setStatus(Status.DONE);   // goes through setStatus so the completion time is stamped
    }
    
    public double getRemainingHours(){
        return (status == Status.DONE) ? 0.0 : hours;
    }
    
    public boolean isOverdue(){
        return status == Status.PENDING && LocalDate.now().isAfter(dueDate);
    }
    
    public String getPriorityString(){
        if (importance >= 4) { 
            return "High"; 
        }
        else if (importance == 3) { 
            return "Medium"; 
        }
        
        return "Low";
    }
    
    public String getTitle(){
        return title;}
    public double getHours(){ 
        return hours;}
    public LocalDate getDueDate(){ 
        return dueDate;}
    public int getImportance(){ 
        return importance; }
    public Status getStatus() { 
        return status; }

    /** Stamps completedAt when a task becomes DONE and clears it when it stops being DONE. */
    public void setStatus(Status newStatus) {
        if (newStatus == Status.DONE && this.status != Status.DONE) {
            this.completedAt = LocalDateTime.now();
        } else if (newStatus != Status.DONE) {
            this.completedAt = null;
        }
        this.status = newStatus;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    // Use only when loading saved tasks from a file/database.
    // Call setStatus(...) first, then setCompletedAt(...), so the saved value isn't overwritten.
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
    
    public abstract boolean occupiesTime(Task otherTask);
    public abstract String getTypeLabel();
    public abstract String getScheduleText();
}