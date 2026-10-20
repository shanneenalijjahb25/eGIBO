/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package egibo.model;

import java.time.*;
import jdk.jfr.Description;

public class FlexibleTask extends Task{

    public FlexibleTask(String title, String description, double hours, int importance, LocalDate dueDate) {
        super(title, description, hours, importance, dueDate);
    }
    
    @Override
    public boolean occupiesTime(LocalDateTime now){
    return false;
    }
    
    @Override
    public String getTypeLabel(){
        return "Flexible";
    }
    
    @Override
    public String getScheduleText(){
        return "Anytime today";
    }
     
}
