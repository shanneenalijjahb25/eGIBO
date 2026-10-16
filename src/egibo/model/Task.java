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
    private Status status;
    
    Task (String title, double hours, int importance, LocalDate dueDate){
        if (title == null || title.trim().isEmpty()){
            this.title = "Task" + ++taskCounter;
        }
        if (hours <= 0){
            throw new IllegalArgumentException("Hours must be greater than 0 :\\");
        }
        
        this.title = title;
        this.hours = hours;
        this.importance = importance;
        this.dueDate = dueDate;
        this.status = Status.PENDING;
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
    
    public abstract boolean occupiesTime(LocalDateTime now);
}
