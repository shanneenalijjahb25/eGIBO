/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package egibo.model;

import java.time.*;
import jdk.jfr.Description;

public class FlexibleTask extends Task{

    private LocalTime workStartTime;

    
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
        if (this.getDueDate() == null || otherTask.getDueDate() == null) return false;
        if (!this.getDueDate().equals(otherTask.getDueDate())) return false;
        if (this.workStartTime == null) return false;
        
        // Calculate this flexible task's end time based on its working hours
        long thisMinutes = Math.round(this.getHours() * 60);
        LocalTime thisEnd = this.workStartTime.plusMinutes(thisMinutes);

        LocalTime otherStart = null;
        LocalTime otherEnd = null;

        if (otherTask instanceof FixedTask) {
            FixedTask otherFixed = (FixedTask) otherTask;
            otherStart = otherFixed.getStartTime();
            otherEnd = otherFixed.getEndTime();
        } else if (otherTask instanceof FlexibleTask) {
            FlexibleTask otherFlex = (FlexibleTask) otherTask;
            otherStart = otherFlex.getWorkStartTime();
            if (otherStart != null) {
                long otherMinutes = Math.round(otherFlex.getHours() * 60);
                otherEnd = otherStart.plusMinutes(otherMinutes);
            }
        }

        if (otherStart == null || otherEnd == null) {
            return false;
        }

        // Interval overlap formula: (StartA < EndB) && (EndA > StartB)
        return this.workStartTime.isBefore(otherEnd) && thisEnd.isAfter(otherStart);
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
