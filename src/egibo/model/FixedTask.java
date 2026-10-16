/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package egibo.model;
import java.time.*;

public class FixedTask extends Task{
    private final LocalTime startTime;
    private final LocalTime endTime;

    public FixedTask(LocalTime startTime, LocalTime endTime, String title, double hours, int importance, LocalDate dueDate) {
        super(title, hours, importance, dueDate);
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public LocalTime getStartTime(){ 
        return startTime;}
    public LocalTime getEndTime(){ 
        return endTime;}
     
    @Override
    public boolean occupiesTime(LocalDateTime now) {
        if (!now.toLocalDate().equals(getDueDate())) {
            return false;
        }
        
        LocalTime currentTime = now.toLocalTime();
        
        return !currentTime.isBefore(startTime) && !currentTime.isAfter(endTime);
    }
}
