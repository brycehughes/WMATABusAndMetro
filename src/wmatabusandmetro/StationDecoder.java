/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wmatabusandmetro;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import static java.lang.Thread.sleep;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import javafx.scene.paint.Color;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Goalie
 */
public class StationDecoder {

    //Code to Name
    HashMap<String, String> c2n;
    //code to light
    HashMap<String, String> c2l;
    //CircuitInfo
    HashMap<Integer, String> circuitInfo;
    HashMap<String, HashMap> circuitInfo2;
    HashMap<String, JSONArray> lineCircuitInfo;
    //Metro Lines
    String[] lines = {"RD", "YL", "GR", "BL", "OR", "SV"};

    public static Color decodeLine(String in) {
        Color retval = null;
        switch (in) {
            case "RD":
                retval = Color.RED;
                break;
            case "YL":
                retval = Color.YELLOW;
                break;
            case "GR":
                retval = Color.GREEN;
                break;
            case "BL":
                retval = Color.BLUE;
                break;
            case "OR":
                retval = Color.ORANGE;
                break;
            case "SV":
                retval = Color.SILVER;
                break;
        }
        return retval;
    }

    public StationDecoder() {
        c2n = new HashMap<String, String>();
        c2l = new HashMap<String, String>();
        circuitInfo = new HashMap<Integer, String>();
        circuitInfo2 = new HashMap<String, HashMap>();
        this.loadCircuitIdInfo();
    }

    public static void printStationInfo() {
        HashMap<Integer, JSONObject> trainloc = new HashMap<Integer, JSONObject>();
        try {
            //Create connection
            URL yahoo = new URL("https://api.wmata.com/Rail.svc/json/jStations");
            URLConnection yc = yahoo.openConnection();
            yc.setRequestProperty("api_key", "1457227966e0459faea7fd0ed4272b9d");
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            yc.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            JSONObject js = new JSONObject(response.toString());
            JSONArray jarr = js.getJSONArray("Stations");
            for (int i = 0; i < jarr.length(); i++) {
                JSONObject jo = (JSONObject) jarr.get(i);
                String stationName = jo.get("Name").toString();
                String code = jo.get("Code").toString();
                System.out.println(stationName + " - " + code);
            }
        } catch (Exception e) {
            e.printStackTrace();
            //return null;
        } finally {
        }
    }

    public static HashMap<Integer, JSONObject> getTrainLocations() {
        HashMap<Integer, JSONObject> trainloc = new HashMap<Integer, JSONObject>();
        try {
            //Create connection
            URL yahoo = new URL("https://api.wmata.com/TrainPositions/TrainPositions?contentType=json");
            URLConnection yc = yahoo.openConnection();
            yc.setRequestProperty("api_key", "1457227966e0459faea7fd0ed4272b9d");
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            yc.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            JSONObject js = new JSONObject(response.toString());
            Iterator<String> keys = js.keys();
            String trainkey = keys.next();
            JSONArray jArray = (JSONArray) js.get(trainkey);
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject train = (JSONObject) jArray.get(i);
                if (train.getString("ServiceType") != "Unknown" & train.get("LineCode").toString() != "null") {
                    int circuitID = train.getInt("CircuitId");
                    trainloc.put(circuitID, train);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            //return null;
        } finally {
        }
        return trainloc;
    }

    public HashMap<String, String[]> getInstructions() {
        //Station code -> Instruction (Solid,Blink)
        HashMap instructions = new HashMap<String, String>();
        HashMap trains = getTrainLocations();
        //System.out.println(trains);
        //get train locations
        //for each train
        Set<Integer> keyset = trains.keySet();
        //System.out.println(keyset);
        for (Integer circuitId : keyset) {
            //get circuit info
            JSONObject train = new JSONObject(trains.get(circuitId).toString());
            String linecode = train.get("LineCode").toString();
            String olinecode = linecode;
            int direction = train.getInt("DirectionNum");
            if (direction == 2) {
                linecode += 2;
            }
            HashMap<Integer, String> lineCircuitInfo = circuitInfo2.get(linecode);
            String ciString = lineCircuitInfo.get((int) circuitId);
            if (ciString != null) { //Because apparently not every circuit is in the circuit info for some reason
                JSONObject ci = new JSONObject(ciString);
                String stationCode = ci.get("StationCode").toString();
                if (stationCode != "null") {
                    
                    if (direction == 2) {
                        stationCode += "-2";
                    }
                    String[] instruction = {"solid", olinecode};
                    instructions.put(stationCode, instruction);
                    
                } else {
                    try {
                        //find closest station
                        String cs = closestStation(linecode, circuitId, direction);
                       
                        if (direction == 2) {
                            cs += "-2";
                        }
                        System.out.println(olinecode + " - " + cs);
                        if (!instructions.containsKey(cs)) {
                            String[] instruction = {"blink", olinecode};
                            instructions.put(cs, instruction);
                        }
                    } catch (Exception e) {
                        //System.out.println(trains.get(circuitId).toString());
                        e.printStackTrace();
                        //System.out.println("No Closest Station");
                        //no closest station, lets skip this train and pretend it doesn't exist
                    }
                }
            }
        }
        //if hmap.get(circuit#) != null, put (station, Solid)
        //else 
        //try
        //station = findclosestStation(circuit#,direction)
        //if !hmap.has(station), hmap.put(station,blink)
        //catch
        return instructions;
    }

    public String closestStation(String line, int circuit, int direction) throws Exception {
        HashMap<Integer, String> curLine = circuitInfo2.get(line);
        int ocircuit = circuit;
        String station = "null";
        while (station == "null") {
            if (!curLine.containsKey(circuit)) {
                throw new Exception("Station Not Found");
            }
            JSONObject circuitJO = new JSONObject(curLine.get(circuit));
            station = circuitJO.get("StationCode").toString();
            if (direction == 1) {
                circuit += 1;
            }
            if (direction == 2) {
                circuit -= 1;
            }
        }
        return station;
    }

    public String getStationLine(String stationCode) {
        String linecode = "";
        try {
            sleep(500); // Otherwise too many requests
            URL yahoo = new URL("https://api.wmata.com/Rail.svc/json/jStationInfo?StationCode=" + stationCode);
            URLConnection yc = yahoo.openConnection();
            yc.setRequestProperty("api_key", "1457227966e0459faea7fd0ed4272b9d");
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            yc.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            JSONObject js = new JSONObject(response.toString());
            linecode = js.getString("LineCode1");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return linecode;
    }

    public void loadCircuitIdInfo() {
        try {
            URL yahoo = new URL("https://api.wmata.com/TrainPositions/StandardRoutes?contentType=json");
            URLConnection yc = yahoo.openConnection();
            yc.setRequestProperty("api_key", "1457227966e0459faea7fd0ed4272b9d");
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            yc.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            JSONObject js = new JSONObject(response.toString());
            Iterator<String> keys = js.keys();
            String StandardRoutes = keys.next();
            JSONArray jArray = (JSONArray) js.get(StandardRoutes);
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject cid = (JSONObject) jArray.get(i);
                String lineName = cid.get("LineCode").toString();
                JSONArray tcircuits = (JSONArray) cid.getJSONArray(cid.keys().next());
                HashMap<Integer, String> circuitLine = new HashMap<Integer, String>();
                for (int j = 0; j < tcircuits.length(); j++) {
                    JSONObject tcir = tcircuits.getJSONObject(j);
                    int CircuitId = tcir.getInt("CircuitId");
                    circuitLine.put(CircuitId, tcir.toString());
                }
                if (circuitInfo2.containsKey(lineName)) {
                    lineName += 2;
                }
                circuitInfo2.put(lineName, circuitLine);

            }
            //System.out.println(circuitInfo);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }

    /*
    public void loadCircuitIdInfoOld() {
        try {
            URL yahoo = new URL("https://api.wmata.com/TrainPositions/StandardRoutes?contentType=json");
            URLConnection yc = yahoo.openConnection();
            yc.setRequestProperty("api_key", "1457227966e0459faea7fd0ed4272b9d");
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            yc.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            JSONObject js = new JSONObject(response.toString());
            Iterator<String> keys = js.keys();
            String StandardRoutes = keys.next();
            JSONArray jArray = (JSONArray) js.get(StandardRoutes);

            for (int i = 0; i < jArray.length(); i++) {
                JSONObject cid = (JSONObject) jArray.get(i);
                System.out.println(cid.get("LineCode").toString());
                JSONArray tcircuits = (JSONArray) cid.getJSONArray(cid.keys().next());
                for (int j = 0; j < tcircuits.length(); j++) {
                    JSONObject tcir = tcircuits.getJSONObject(j);
                    if(circuitInfo.containsKey((int)tcir.get("CircuitId")))
                        //System.out.println(circuitInfo.get((int)tcir.get("CircuitId")) + " - " + tcir);
                    //System.out.println(tcir);
                    //System.out.println(tcir.get("CircuitId").toString());
                    circuitInfo.put((int) tcir.get("CircuitId"), tcir.toString());
                }
            }
            //System.out.println(circuitInfo);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }
     */
    public void getStations() {
        try {
            String baseRequest = "https://api.wmata.com/Rail.svc/json/jStations?";
            for (String line : lines) {
                baseRequest += line;
            }
            URL yahoo = new URL(baseRequest);
            URLConnection yc = yahoo.openConnection();
            yc.setRequestProperty("api_key", "1457227966e0459faea7fd0ed4272b9d");
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            yc.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            JSONObject js = new JSONObject(response.toString());
            Iterator<String> keys = js.keys();
            String stationkey = keys.next();
            JSONArray jArray = (JSONArray) js.get(stationkey);
            c2n = new HashMap<String, String>();
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject station = (JSONObject) jArray.get(i);
                String stationName = station.getString("Name");
                String stationCode = station.getString("Code");
                c2n.put(stationCode, stationName);
            }
            //System.out.println(c2n);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }

    public static void main(String[] args) throws Exception {
        StationDecoder sd = new StationDecoder();
        printStationInfo();
        HashMap<String, String[]> ob = sd.getInstructions();
        System.out.println(ob);
    }

}
