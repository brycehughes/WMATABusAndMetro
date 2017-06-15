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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Goalie
 */
public class TrainFinder {

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

    public TrainFinder() {
        c2n = new HashMap<String, String>();
        c2l = new HashMap<String, String>();
        circuitInfo = new HashMap<Integer, String>();
        circuitInfo2 = new HashMap<String, HashMap>();
        this.loadCircuitIdInfo();
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
                if (train.getString("ServiceType") != "Unknown") {
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
 
    public String getStationLine(String stationCode){
        String linecode="";
        try{
            sleep(500); // Otherwise too many requests
            URL yahoo = new URL("https://api.wmata.com/Rail.svc/json/jStationInfo?StationCode="+stationCode);
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
            linecode=js.getString("LineCode1");
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            
        }
        return linecode;
    }
    
    public void loadStations(){
        try{
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
        }catch(Exception e){
            
        }finally{
            
        }
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
                JSONArray tcircuits = (JSONArray) cid.getJSONArray(cid.keys().next());
                for (int j = 0; j < tcircuits.length(); j++) {
                    JSONObject tcir = tcircuits.getJSONObject(j);
                    if(circuitInfo.containsKey((int)tcir.get("CircuitId")))
                        System.out.println(circuitInfo.get((int)tcir.get("CircuitId")) + " - " + tcir);
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
        TrainFinder sd = new TrainFinder();
        //System.out.println(sd.closestStation(2025, 1));
        //sd.getStations();
        //HashMap<String, String> ob = sd.getInstructions();
        //HashMap<Integer,JSONObject> ob = sd.getTrainLocations();
        //System.out.println(ob);
      
        
        //sd.getCircuitIdInfo(); 
    }

}
