/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package egibo.model;

/**
 *
 * @author Lenovo
 */
public class WorkloadAnalyzer {
    // Centralized workload risk evaluator
    public static String evaluateRisk(double totalHours, double capacity) {
        if (capacity <= 0) {
            return "Low";
        }
        
        if (totalHours > capacity) {
            return "Overloaded! ⚠️";
        } else if (totalHours >= capacity * 0.8) {
            return "High";
        } else if (totalHours >= capacity * 0.5) {
            return "Moderate";
        } else {
            return "Low";
        }
    }
}
