/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package egibo.model;

public class StudentProfile {
    String name;
    double dailyCapacity;
    
    public StudentProfile(String name, double dailyCapacity){
        if (dailyCapacity <= 0) {
            throw new IllegalArgumentException("Your Daily Capacity can't be that low ? :<");
        }
        
        this.name = name;
        this.dailyCapacity = dailyCapacity;
    }
    
    public String name(){
    return name;}
    public double dailyCapacity(){
    return dailyCapacity;}
}
