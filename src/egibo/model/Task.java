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
        
        if (dueDate == null){
            this.dueDate = LocalDate.now();
        }
        
        this.hours = hours;
        this.importance = importance;
        this.dueDate = dueDate;
        this.status = Status.PENDING;
    }
    
    public int getTaskId() {
    return taskId;
    }

    public void setTaskId(int taskId) {
    this.taskId = taskId;
    }
    public void markDone(){
        this.status = Status.DONE;
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
    public void setStatus(Status status) {
            this.status = status;
    }
    public String getDescription() {
    return description;
}
    
    public abstract boolean occupiesTime(Task otherTask);
    public abstract String getTypeLabel();
    public abstract String getScheduleText();
}
