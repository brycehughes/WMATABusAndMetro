/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wmatabusandmetro;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Bryce Hughes
 */
public class WMATABusAndMetro {

    private static HttpURLConnection connection;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
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
           JSONArray jArray = (JSONArray)js.get(trainkey);
            for(int i=0;i<jArray.length();i++){
                JSONObject train = (JSONObject)jArray.get(i);
                System.out.println(train.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            //return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

}
