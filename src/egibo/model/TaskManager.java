/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package egibo.model;

import java.time.*;
import java.util.ArrayList;
import java.util.List;

public class TaskManager {
    private final List<Task> tasks;
    
    public TaskManager(){
        this.tasks = new ArrayList<>();
    }
    
    public void addFlexibleTask(String title, double hours, int importance, LocalDate dueDate) {
        FlexibleTask newFlexible = new FlexibleTask(title, hours, importance, dueDate);
        tasks.add(newFlexible);
    }
    public void addFixedTask(LocalTime startTime, LocalTime endTime, String title, double hours, int importance, LocalDate dueDate){
        FixedTask newFixed = new FixedTask(startTime, endTime, title, hours, importance, dueDate);
        tasks.add(newFixed);
    }
    
    public List<Task> getAllTasks() {
        return tasks;
    }
    
    public double getTotalRemainingHours() {
    double total = 0.0;
    for (Task t : tasks) {
        total += t.getRemainingHours(); // Uses the method inside your Task class
    }
    return total;
    }
}

