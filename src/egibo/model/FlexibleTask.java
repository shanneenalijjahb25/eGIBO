/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package egibo.model;

import java.time.*;
import jdk.jfr.Description;

public class FlexibleTask extends Task{

    private LocalTime workStartTime;

    // Updated constructor to accept the new scheduled working time
    public FlexibleTask(String title, String description, double hours, int importance, LocalDate dueDate, LocalTime workStartTime) {
        super(title, description, hours, importance, dueDate);
        this.workStartTime = workStartTime;
    }
    
    // Getter for the scheduled work start time
    public LocalTime getWorkStartTime() {
        return workStartTime;
    }
    
    @Override
    public boolean occupiesTime(Task otherTask) {
        if (this.getDueDate() == null || otherTask.getDueDate() == null) {
            return false;
        }
        // If dates don't match, no conflict
        if (!this.getDueDate().equals(otherTask.getDueDate())) {
            return false;
        }
        
        LocalTime otherTime = null;
        if (otherTask instanceof FixedTask) {
            otherTime = ((FixedTask) otherTask).getStartTime();
        } else if (otherTask instanceof FlexibleTask) {
            otherTime = ((FlexibleTask) otherTask).getWorkStartTime();
        }
        
        return this.workStartTime != null && this.workStartTime.equals(otherTime);
    }
    
    @Override
    public String getTypeLabel(){
        return "Flexible";
    }
    
    @Override
    public String getScheduleText(){
        return workStartTime != null ? "Scheduled at " + workStartTime.toString() : "Anytime today";
    }
   
}
