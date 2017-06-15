/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wmatabusandmetro;

/**
 *
 * @author Goalie
 */
public class Instruction {
    
    private String station;
    private String directive;
    private int distanceFromStation;
    
    public Instruction(){
    
    }
    public Instruction(String station, String directive, int distance){
    
    }
    
    public void setStation(String in){
    this.station=in;
    }
    public void setDirective(String in){
    this.directive=in;
    }
    public void setDistance(int in){
    this.distanceFromStation=in;
    }
    
    public String getStation(){
    return this.station;
    }
    public String getDirective(){
    return this.directive;
    }
    public int getDistance(){
    return this.distanceFromStation;
    }
    
}
