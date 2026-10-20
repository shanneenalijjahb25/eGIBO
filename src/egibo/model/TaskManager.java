/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package egibo.model;

import java.time.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.util.List;
import jdk.jfr.Description;

public class TaskManager {
    private static final Comparator<Task> RANK = new Comparator<Task>(){
        @Override
        public int compare(Task a, Task b) {
        int byStatus = a.getStatus().compareTo(b.getStatus());
        if (byStatus != 0) {
            return byStatus;
        }
        
        int byImportance = Integer.compare(b.getImportance(), a.getImportance());
        if (byImportance != 0) {
                return byImportance;
        }
        
        return a.getDueDate().compareTo(b.getDueDate());
        }
    };
    
    private final List<Task> tasks;
    
    public TaskManager(){
        this.tasks = new ArrayList<>();
    }
    
    public void addFlexibleTask(String title, String description, double hours, int importance, LocalDate dueDate) {
        FlexibleTask newFlexible = new FlexibleTask(title, description, hours, importance, dueDate);
        tasks.add(newFlexible);
    }
    public void addFixedTask(LocalTime startTime, LocalTime endTime, String title, String description,  double hours, int importance, LocalDate dueDate){
        FixedTask newFixed = new FixedTask(startTime, endTime, title, description, 0.0, importance, dueDate);
        tasks.add(newFixed);
    }
    
    public void removeTask(Task t){
        tasks.remove(t);
    }
    
    public void markTaskDone(Task t){
        if (tasks.contains(t)){
            t.markDone();
        }
    }
    
    //daily reset
    public void clear(){
        tasks.clear();
    }
   
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks);
    }
    
        public List<Task> getPendingTasks() {
        List<Task> result = new ArrayList<>();
        for (Task t : tasks) {
            if (t.getStatus() == Status.PENDING) {
                result.add(t);
            }
        }
        return result;
    }

    public List<Task> getFinishedTasks() {
        List<Task> result = new ArrayList<>();
        for (Task t : tasks) {
            if (t.getStatus() == Status.DONE) {
                result.add(t);
            }
        }
        return result;
    }
    
    public List<Task> getRankedTasks() {
        
        List<Task> sorted = new ArrayList<>(tasks);
        Collections.sort(sorted, RANK);
        return sorted;
    }
    
    public double getTotalRemainingHours() {
    double total = 0.0;
    for (Task t : tasks) {
        total += t.getRemainingHours(); // Uses the method inside your Task class
    }
    return total;
    }
}

