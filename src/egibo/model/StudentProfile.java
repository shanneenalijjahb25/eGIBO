/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package egibo.model;


public class StudentProfile {
    public static final double MAX_CAPACITY = 16.0;
    
    private final String name;
    private double dailyCapacity;
    
    public StudentProfile(String name, double dailyCapacity){
        this.name = checkName(name);
        checkCapacity(dailyCapacity);
        this.dailyCapacity = dailyCapacity;
    }
    
    private static String checkName(String name){
        if (name == null || name.trim().isEmpty()){
            throw new IllegalArgumentException("Your name is required.");
        }
        return name.trim();
    }
    private static void checkCapacity(double capacity){
        if (Double.isNaN(capacity) || capacity <= 0){
            throw new IllegalArgumentException("Daily capacity must be a number greater than 0");
        }
        if (capacity > MAX_CAPACITY){
            throw new IllegalArgumentException("Daily capacity cannot be more than " + (int) MAX_CAPACITY + " hours.");
        }
    }
    
    //used in modifying the capacity and the daily resetter
    public void setDailyCapacity(double capacity){
        checkCapacity(capacity);
        this.dailyCapacity = capacity;
    }
    
    public String name(){
    return name;}
    public double dailyCapacity(){
    return dailyCapacity;}
}
