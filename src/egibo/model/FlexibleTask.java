/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package egibo.model;

import java.time.*;

public class FlexibleTask extends Task{

    public FlexibleTask(String title, double hours, int importance, LocalDate dueDate) {
        super(title, hours, importance, dueDate);
    }
    
    @Override
    public boolean occupiesTime(LocalDateTime now){
    return false;
    }
     
}
