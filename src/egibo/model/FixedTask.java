/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package egibo.model;
import java.time.*;
import java.time.format.DateTimeFormatter;

public class FixedTask extends Task{
    
    private static final DateTimeFormatter CLOCK = DateTimeFormatter.ofPattern("HH:mm");
    private final LocalTime startTime;
    private final LocalTime endTime;

    public FixedTask(LocalTime startTime, LocalTime endTime, String title, String description, double hours, int importance, LocalDate eventDate) {
        super(title, description, computeHours(startTime, endTime), importance, eventDate);
        this.startTime = startTime;
        this.endTime = endTime;
    }
    public static double computeHours(LocalTime start, LocalTime end){
        if (start == null || end == null){
            throw new IllegalArgumentException("Start time and End time are required.");
        }
        if (!end.isAfter(start)){
            throw new IllegalArgumentException("End time must be after the start time.");
        }
        return Duration.between(start, end).toMinutes() / 60.0;
    }
    
    public LocalTime getStartTime(){ 
        return startTime;}
    public LocalTime getEndTime(){ 
        return endTime;}
    
    public LocalDate getScheduledDate(){
        return super.getDueDate();
    }
     
    @Override
    public boolean occupiesTime(Task otherTask) {
        if (this.getDueDate() == null || otherTask.getDueDate() == null) return false;
        if (!this.getDueDate().equals(otherTask.getDueDate())) return false;
        if (this.startTime == null || this.endTime == null) return false;

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
                // Calculate the end time of the flexible task using its estimated hours
                long minutes = Math.round(otherFlex.getHours() * 60);
                otherEnd = otherStart.plusMinutes(minutes);
            }
        }

        // If the other task has no specific time assigned, no time conflict occurs
        if (otherStart == null || otherEnd == null) return false;

        // Interval overlap formula: (StartA < EndB) && (EndA > StartB)
        return this.startTime.isBefore(otherEnd) && this.endTime.isAfter(otherStart);
    }
    
    @Override
    public String getTypeLabel(){
        return "Fixed";
    }
    
    @Override
    public String getScheduleText(){
        return startTime.format(CLOCK) + " - " + endTime.format(CLOCK);
    }
}
