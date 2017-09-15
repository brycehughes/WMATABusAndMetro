/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wmatabusandmetro;

import javafx.scene.paint.Color;

/**
 * This class is used in order to keep information collected in a single place. This will include enough information for a 
 * sub program to be able to display information correctly
 * @author Goalie
 */
public class Instruction {
    
    private String station;
    private String directive;
    private int direction;
    private int distanceFromStation;
    private String linecode;
    private Color trainColor;
    
    public Instruction(){
    
    }
    public Instruction(String station, String directive, int distance,int direction, String linecode){
    this.station=station;
    this.directive=directive;
    this.direction=direction;
    this.distanceFromStation=distance;
    this.trainColor = decodeLine(linecode);
    this.linecode=linecode;
    }
    
    // Verbose switch case. Could be shortened but kept longer so that maintenence over long periods of time is easier
    public static Color decodeLine(String in) {
        Color retval = null;
        switch (in) {
            case "RD":
                retval = Color.RED;
                break;
            case "RD2":
                retval = Color.RED;
                break;
            case "YL":
                retval = Color.YELLOW;
                break;
            case "YL2":
                retval = Color.YELLOW;
                break;
            case "GR":
                retval = Color.GREEN;
                break;
            case "GR2":
                retval = Color.GREEN;
                break;
            case "BL":
                retval = Color.BLUE;
                break;
            case "BL2":
                retval = Color.BLUE;
                break;
            case "OR":
                retval = Color.ORANGE;
                break;
            case "OR2":
                retval = Color.ORANGE;
                break;
            case "SV":
                retval = Color.SILVER;
                break;
            case "SV2":
                retval = Color.SILVER;
                break;
        }
        return retval;
    }
    
    /***
    *allows for easy viewing of train information
    */
    public String toString(){
  
        String retval= "[station=" + this.station + ",directive=" + this.directive + ",direction=" + this.direction + ",distance=" + this.distanceFromStation + ",color=" + this.linecode + "]";
        return retval;
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
    public void setDirection(int in){
    this.direction=in;
    }
    public void setColor(String in){
        this.trainColor=decodeLine(in);
        this.linecode=in;
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
    public int getDirection(){
    return this.direction;
    }
    public Color getColor(){
        return this.trainColor;
    }
}
