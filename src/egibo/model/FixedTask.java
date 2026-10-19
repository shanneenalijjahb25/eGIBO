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

    public FixedTask(LocalTime startTime, LocalTime endTime, String title, double hours, int importance, LocalDate eventDate) {
        super(title, computeHours(startTime, endTime), importance, eventDate);
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
    public boolean occupiesTime(LocalDateTime now) {
        if (!now.toLocalDate().equals(getScheduledDate())) {
            return false;
        }
        
        LocalTime currentTime = now.toLocalTime();
        return !currentTime.isBefore(startTime) && !currentTime.isAfter(endTime);
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
