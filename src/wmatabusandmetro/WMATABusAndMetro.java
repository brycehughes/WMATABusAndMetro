/*
* This is the visual class. Its used to show information about trains and thier positions. This is not supposed
* to be a final representation. Final representation is a physical map with lightup LED strips. This is a quick and
* dirty solution to ensure that the information is being processed correctly
 */
package wmatabusandmetro;

import wmatabusandmetro.StationDecoder;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Pos;
import javafx.scene.input.MouseEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.DragEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Bryce Hughes
 */
public class WMATABusAndMetro extends Application {

    ArrayList<String> circleLocations;
    ArrayList<Circle> circles;
    AnchorPane root;
    double orgTranslateX, orgTranslateY;
    double orgSceneX, orgSceneY;
    HashMap<String, Circle> hcircle;

    public WMATABusAndMetro() {
        circleLocations = new ArrayList<String>();
        circles = new ArrayList<Circle>();
        hcircle = new HashMap<String, Circle>();

    }

    public void start() {

        launch();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        final WMATABusAndMetro wbm = new WMATABusAndMetro();
        wbm.start();

    }
    
    //Used to speed up finding x,y locations where I need to put circles
    //Just add circles, then print, and then copy that code into the "addAllCircles" function
    public void addCircle() {
        Circle c = new Circle();
        c.setCenterX(100.0f);
        c.setCenterY(100.0f);
        c.setRadius(5.0f);
        c.setOnMouseDragged(circleOnMouseDraggedEventHandler);
        c.setFill(Color.BLUEVIOLET);
        circles.add(c);

        root.getChildren().add(c);

    }
    
    //Visual Representation. 
    public void addAllCircles() {
        hcircle.put("N06", new Circle(109.0, 455.0, 5.0));
        hcircle.put("D02", new Circle(458.0, 550.0, 5.0));
        hcircle.put("D01-2", new Circle(483.0, 530.0, 5.0));
        hcircle.put("K01", new Circle(286.0, 541.0, 5.0));
        hcircle.put("K01-2", new Circle(286.0, 523.0, 5.0));
        hcircle.put("K02", new Circle(264.0, 543.0, 5.0));
        hcircle.put("K02-2", new Circle(266.0, 522.0, 5.0));
        hcircle.put("K03", new Circle(246.0, 542.0, 5.0));
        hcircle.put("K03-2", new Circle(246.0, 523.0, 5.0));
        hcircle.put("K04", new Circle(225.0, 542.0, 5.0));
        hcircle.put("K04-2", new Circle(226.0, 523.0, 5.0));
        hcircle.put("K05", new Circle(199.0, 541.0, 5.0));
        hcircle.put("K05-2", new Circle(200.0, 525.0, 5.0));
        hcircle.put("K06", new Circle(165.0, 541.0, 5.0));
        hcircle.put("K06-2", new Circle(165.0, 527.0, 5.0));
        hcircle.put("K07", new Circle(136.0, 541.0, 5.0));
        hcircle.put("K07-2", new Circle(137.0, 528.0, 5.0));
        hcircle.put("K08", new Circle(109.0, 542.0, 5.0));
        hcircle.put("K08-2", new Circle(109.0, 525.0, 5.0));
        hcircle.put("N01-2", new Circle(175.0, 500.0, 5.0));
        hcircle.put("N01", new Circle(164.0, 510.0, 5.0));
        hcircle.put("N02-2", new Circle(162.0, 487.0, 5.0));
        hcircle.put("N02", new Circle(151.0, 496.0, 5.0));
        hcircle.put("N03-2", new Circle(148.0, 474.0, 5.0));
        hcircle.put("N03", new Circle(138.0, 481.0, 5.0));
        hcircle.put("N04-2", new Circle(135.0, 459.0, 5.0));
        hcircle.put("N04", new Circle(124.0, 469.0, 5.0));
        hcircle.put("N06-2", new Circle(120.0, 444.0, 5.0));
        hcircle.put("D01", new Circle(458.0, 531.0, 5.0));
        hcircle.put("A01", new Circle(470.0, 516.0, 5.0));
        hcircle.put("A01-2", new Circle(470.0, 484.0, 5.0));
        hcircle.put("C01", new Circle(457.0, 500.0, 5.0));
        hcircle.put("C01-2", new Circle(487.0, 501.0, 5.0));
        hcircle.put("C02", new Circle(447.0, 479.0, 5.0));
        hcircle.put("C02-2", new Circle(448.0, 451.0, 5.0));
        hcircle.put("C03", new Circle(408.0, 476.0, 5.0));
        hcircle.put("C03-2", new Circle(409.0, 450.0, 5.0));
        hcircle.put("C04", new Circle(386.0, 477.0, 5.0));
        hcircle.put("C04-2", new Circle(385.0, 451.0, 5.0));
        hcircle.put("C05", new Circle(330.0, 504.0, 5.0));
        hcircle.put("C05-2", new Circle(301.0, 502.0, 5.0));
        hcircle.put("D10", new Circle(729.0, 511.0, 5.0));
        hcircle.put("D09", new Circle(712.0, 529.0, 5.0));
        hcircle.put("D09-2", new Circle(703.0, 515.0, 5.0));
        hcircle.put("D08", new Circle(661.0, 563.0, 5.0));
        hcircle.put("D08-2", new Circle(662.0, 534.0, 5.0));
        hcircle.put("D07", new Circle(639.0, 564.0, 5.0));
        hcircle.put("D07-2", new Circle(621.0, 546.0, 5.0));
        hcircle.put("D06", new Circle(624.0, 579.0, 5.0));
        hcircle.put("D06-2", new Circle(607.0, 559.0, 5.0));
        hcircle.put("D05", new Circle(588.0, 587.0, 5.0));
        hcircle.put("D05-2", new Circle(588.0, 563.0, 5.0));
        hcircle.put("D04", new Circle(559.0, 587.0, 5.0));
        hcircle.put("D04-2", new Circle(559.0, 561.0, 5.0));
        hcircle.put("F03", new Circle(541.0, 576.0, 5.0));
        hcircle.put("F03-2", new Circle(513.0, 575.0, 5.0));
        hcircle.put("D03", new Circle(526.0, 591.0, 5.0));
        hcircle.put("D03-2", new Circle(527.0, 560.0, 5.0));
        hcircle.put("D02-2", new Circle(484.0, 551.0, 5.0));
        hcircle.put("G05", new Circle(825.0, 564.0, 5.0));
        hcircle.put("G05-2", new Circle(824.0, 545.0, 5.0));
        hcircle.put("G04", new Circle(804.0, 564.0, 5.0));
        hcircle.put("F11", new Circle(742.0, 562.0, 5.0));
        hcircle.put("F11-2", new Circle(742.0, 547.0, 5.0));
        hcircle.put("D13-2", new Circle(770.0, 450.0, 5.0));
        hcircle.put("D13", new Circle(780.0, 460.0, 5.0));
        hcircle.put("D12-2", new Circle(753.0, 468.0, 5.0));
        hcircle.put("D12", new Circle(763.0, 479.0, 5.0));
        hcircle.put("D11-2", new Circle(737.0, 483.0, 5.0));
        hcircle.put("D11", new Circle(747.0, 494.0, 5.0));
        hcircle.put("D10-2", new Circle(720.0, 498.0, 5.0));
        hcircle.put("G04-2", new Circle(803.0, 546.0, 5.0));
        hcircle.put("G03", new Circle(783.0, 564.0, 5.0));
        hcircle.put("G03-2", new Circle(783.0, 546.0, 5.0));
        hcircle.put("G02", new Circle(762.0, 563.0, 5.0));
        hcircle.put("G02-2", new Circle(762.0, 547.0, 5.0));
        hcircle.put("A08-2", new Circle(352.0, 352.0, 5.0));
        hcircle.put("A08", new Circle(343.0, 362.0, 5.0));
        hcircle.put("A09-2", new Circle(332.0, 333.0, 5.0));
        hcircle.put("A09", new Circle(323.0, 343.0, 5.0));
        hcircle.put("A10-2", new Circle(311.0, 311.0, 5.0));
        hcircle.put("A10", new Circle(302.0, 322.0, 5.0));
        hcircle.put("A11-2", new Circle(291.0, 290.0, 5.0));
        hcircle.put("A11", new Circle(280.0, 301.0, 5.0));
        hcircle.put("A12-2", new Circle(270.0, 269.0, 5.0));
        hcircle.put("A12", new Circle(261.0, 280.0, 5.0));
        hcircle.put("A13-2", new Circle(249.0, 250.0, 5.0));
        hcircle.put("A13", new Circle(239.0, 259.0, 5.0));
        hcircle.put("A14-2", new Circle(227.0, 227.0, 5.0));
        hcircle.put("A14", new Circle(218.0, 238.0, 5.0));
        hcircle.put("A15-2", new Circle(210.0, 207.0, 5.0));
        hcircle.put("A15", new Circle(196.0, 217.0, 5.0));
        hcircle.put("B02-2", new Circle(563.0, 490.0, 5.0));
        hcircle.put("F01", new Circle(537.0, 502.0, 5.0));
        hcircle.put("F01-2", new Circle(517.0, 504.0, 5.0));
        hcircle.put("B01", new Circle(526.0, 514.0, 5.0));
        hcircle.put("B01-2", new Circle(527.0, 488.0, 5.0));
        hcircle.put("A02-2", new Circle(442.0, 436.0, 5.0));
        hcircle.put("A02", new Circle(430.0, 438.0, 5.0));
        hcircle.put("A03-2", new Circle(431.0, 407.0, 5.0));
        hcircle.put("A03", new Circle(418.0, 418.0, 5.0));
        hcircle.put("A04-2", new Circle(412.0, 391.0, 5.0));
        hcircle.put("A04", new Circle(402.0, 403.0, 5.0));
        hcircle.put("A05-2", new Circle(399.0, 379.0, 5.0));
        hcircle.put("A05", new Circle(388.0, 388.0, 5.0));
        hcircle.put("A06-2", new Circle(387.0, 365.0, 5.0));
        hcircle.put("A06", new Circle(377.0, 376.0, 5.0));
        hcircle.put("A07-2", new Circle(367.0, 360.0, 5.0));
        hcircle.put("A07", new Circle(357.0, 374.0, 5.0));
        hcircle.put("E06-2", new Circle(546.0, 346.0, 5.0));
        hcircle.put("B06-2", new Circle(542.0, 365.0, 5.0));
        hcircle.put("B06", new Circle(565.0, 344.0, 5.0));
        hcircle.put("B05", new Circle(611.0, 412.0, 5.0));
        hcircle.put("B05-2", new Circle(590.0, 412.0, 5.0));
        hcircle.put("B04", new Circle(611.0, 437.0, 5.0));
        hcircle.put("B04-2", new Circle(590.0, 436.0, 5.0));
        hcircle.put("B35", new Circle(611.0, 461.0, 5.0));
        hcircle.put("B35-2", new Circle(591.0, 461.0, 5.0));
        hcircle.put("B03", new Circle(611.0, 486.0, 5.0));
        hcircle.put("B03-2", new Circle(590.0, 486.0, 5.0));
        hcircle.put("B02", new Circle(563.0, 512.0, 5.0));
        hcircle.put("J02", new Circle(338.0, 839.0, 5.0));
        hcircle.put("J02-2", new Circle(337.0, 818.0, 5.0));
        hcircle.put("J03", new Circle(311.0, 875.0, 5.0));
        hcircle.put("J03-2", new Circle(293.0, 874.0, 5.0));
        hcircle.put("B11", new Circle(499.0, 177.0, 5.0));
        hcircle.put("B11-2", new Circle(481.0, 177.0, 5.0));
        hcircle.put("B10", new Circle(500.0, 199.0, 5.0));
        hcircle.put("B10-2", new Circle(481.0, 199.0, 5.0));
        hcircle.put("B09", new Circle(501.0, 223.0, 5.0));
        hcircle.put("B09-2", new Circle(480.0, 222.0, 5.0));
        hcircle.put("B08", new Circle(500.0, 247.0, 5.0));
        hcircle.put("B08-2", new Circle(480.0, 246.0, 5.0));
        hcircle.put("B07-2", new Circle(494.0, 309.0, 5.0));
        hcircle.put("B07", new Circle(507.0, 294.0, 5.0));
        hcircle.put("E06", new Circle(561.0, 362.0, 5.0));
        hcircle.put("C08-2", new Circle(392.0, 702.0, 5.0));
        hcircle.put("C09", new Circle(425.0, 725.0, 5.0));
        hcircle.put("C09-2", new Circle(415.0, 743.0, 5.0));
        hcircle.put("C10", new Circle(458.0, 750.0, 5.0));
        hcircle.put("C10-2", new Circle(440.0, 750.0, 5.0));
        hcircle.put("C12", new Circle(460.0, 774.0, 5.0));
        hcircle.put("C12-2", new Circle(441.0, 774.0, 5.0));
        hcircle.put("C15", new Circle(466.0, 874.0, 5.0));
        hcircle.put("C15-2", new Circle(447.0, 874.0, 5.0));
        hcircle.put("C14", new Circle(467.0, 841.0, 5.0));
        hcircle.put("C14-2", new Circle(446.0, 841.0, 5.0));
        hcircle.put("C13", new Circle(463.0, 806.0, 5.0));
        hcircle.put("C13-2", new Circle(440.0, 807.0, 5.0));
        hcircle.put("E05", new Circle(510.0, 378.0, 5.0));
        hcircle.put("E05-2", new Circle(498.0, 362.0, 5.0));
        hcircle.put("E04", new Circle(492.0, 393.0, 5.0));
        hcircle.put("E04-2", new Circle(473.0, 392.0, 5.0));
        hcircle.put("E03-2", new Circle(500.0, 425.0, 5.0));
        hcircle.put("E03", new Circle(511.0, 410.0, 5.0));
        hcircle.put("E02", new Circle(535.0, 443.0, 5.0));
        hcircle.put("E02-2", new Circle(517.0, 443.0, 5.0));
        hcircle.put("E01", new Circle(536.0, 469.0, 5.0));
        hcircle.put("E01-2", new Circle(518.0, 470.0, 5.0));
        hcircle.put("F02", new Circle(535.0, 530.0, 5.0));
        hcircle.put("F02-2", new Circle(518.0, 530.0, 5.0));
        hcircle.put("C06", new Circle(370.0, 602.0, 5.0));
        hcircle.put("C06-2", new Circle(358.0, 616.0, 5.0));
        hcircle.put("C07", new Circle(412.0, 666.0, 5.0));
        hcircle.put("C07-2", new Circle(394.0, 667.0, 5.0));
        hcircle.put("C08", new Circle(413.0, 702.0, 5.0));
        hcircle.put("F08-2", new Circle(653.0, 703.0, 5.0));
        hcircle.put("F08", new Circle(653.0, 681.0, 5.0));
        hcircle.put("F09-2", new Circle(676.0, 696.0, 5.0));
        hcircle.put("F09", new Circle(691.0, 681.0, 5.0));
        hcircle.put("F10-2", new Circle(693.0, 713.0, 5.0));
        hcircle.put("F10", new Circle(707.0, 697.0, 5.0));
        hcircle.put("F11-2", new Circle(710.0, 730.0, 5.0));
        hcircle.put("F11", new Circle(724.0, 715.0, 5.0));
        hcircle.put("E10", new Circle(640.0, 281.0, 5.0));
        hcircle.put("E10-2", new Circle(626.0, 266.0, 5.0));
        hcircle.put("E09", new Circle(623.0, 298.0, 5.0));
        hcircle.put("E09-2", new Circle(609.0, 285.0, 5.0));
        hcircle.put("E08", new Circle(606.0, 315.0, 5.0));
        hcircle.put("E08-2", new Circle(592.0, 300.0, 5.0));
        hcircle.put("E07", new Circle(588.0, 331.0, 5.0));
        hcircle.put("E07-2", new Circle(575.0, 319.0, 5.0));
        hcircle.put("F05-2", new Circle(580.0, 661.0, 5.0));
        hcircle.put("F05", new Circle(579.0, 641.0, 5.0));
        hcircle.put("F06-2", new Circle(614.0, 674.0, 5.0));
        hcircle.put("F06", new Circle(627.0, 658.0, 5.0));
        hcircle.put("F07-2", new Circle(627.0, 688.0, 5.0));
        hcircle.put("F07", new Circle(640.0, 673.0, 5.0));
        hcircle.put("F04-2", new Circle(556.0, 660.0, 5.0));
        hcircle.put("F04", new Circle(557.0, 640.0, 5.0));
        hcircle.put("G01-2", new Circle(741.0, 546.0, 5.0));
        hcircle.put("G01", new Circle(742.0, 565.0, 5.0));
        for (String s : hcircle.keySet()) {
            root.getChildren().add(hcircle.get(s));
        }
    }

    public void printAll() {
        for (Circle c : circles) {
            String str = "new Circle(" + (c.centerXProperty().floatValue() + c.translateXProperty().floatValue()) + "," + (c.centerYProperty().floatValue() + c.translateYProperty().floatValue()) + "," + c.radiusProperty().floatValue() + ");";
            System.out.println(str);
        }
    }
    
    /***
    * Clear formatting and past instructions
    */
    public void clearStations(){
        for(String key:hcircle.keySet()){
            Circle c = hcircle.get(key);
            c.setFill(Color.BLACK);
            c.setStrokeWidth(0);
            
        }
    }

    /***
    * Take instruction set and show them onthe map
    */
    public void displayPositions(HashMap<String, Instruction> instructions) {
        clearStations();
        for (String s : instructions.keySet()) {
            Instruction instruction = instructions.get(s);
            Circle c = hcircle.get(s);
            try {
                c.setFill(instruction.getColor());
                if (instruction.getDirective().equals("blink")) {
                    c.setStrokeType(StrokeType.OUTSIDE);
                    c.setStroke(Color.web("black", 0.8));
                    c.setStrokeWidth(4);
                }
            } catch (Exception e) {
                //e.printStackTrace();
                System.out.println(s);
            }
        }
    }

    
    /***
    *  Creates visual and gets instructions on a regular basis. Again, quick and dirty.
    */
    @Override
    public void start(Stage primaryStage) throws Exception {

        Button addCircle = new Button("Add Circle");
        addCircle.setOnMouseClicked(addCircleEvent);
        Button printCircles = new Button("Print Circles");
        printCircles.setOnMouseClicked(printAllEvent);
        printCircles.setLayoutX(110);
        primaryStage.setTitle("Hello World!");
        root = new AnchorPane();
        root.setId("background");
        root.getChildren().add(addCircle);
        root.getChildren().add(printCircles);
        addAllCircles();
        StationDecoder sd = new StationDecoder();
        displayPositions(sd.getInstructions());
        Scene scene = new Scene(root, 900, 1000);
        scene.getStylesheets().add("resources/WMATA.css");
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });
        // Multi-thread so it doesn't block main program
        TimerTask t = new TimerTask(){
             @Override
            public void run() {
                StationDecoder sd = new StationDecoder();
                displayPositions(sd.getInstructions());

            }
        };
        Timer timer = new Timer();
        //Do this every 10 Seconds
        timer.schedule(t, 10000, 10000);
        /*
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(WMATABusAndMetro.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    StationDecoder sd = new StationDecoder();
                    displayPositions(sd.getInstructions());
                }
            }
        });*/
    }

    EventHandler<DragEvent> circleOnDragEnteredEventHandler
            = new EventHandler<DragEvent>() {

        @Override
        public void handle(DragEvent t) {
            orgSceneX = t.getSceneX();
            orgSceneY = t.getSceneY();
            orgTranslateX = ((Circle) (t.getSource())).getTranslateX();
            orgTranslateY = ((Circle) (t.getSource())).getTranslateY();
        }
    };

    EventHandler addCircleEvent
            = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent t) {
            addCircle();
        }
    };

    EventHandler printAllEvent
            = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent t) {
            printAll();
        }
    };

    EventHandler<MouseEvent> circleOnMouseDraggedEventHandler
            = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent t) {
            double currentX = t.getSceneX();
            double currentY = t.getSceneY();
            double sizeX = 100;
            double sizeY = 100;

            ((Circle) (t.getSource())).setTranslateX(currentX - sizeX);
            ((Circle) (t.getSource())).setTranslateY(currentY - sizeY);
        }
    };
}
