
/**
 * AUTHOR: Justin Nichols
 * FILE: PA9Main.java
 * ASSIGNMENT: Programming Assignment 9 - CrisprGUI
 * COURSE: CSC210 Spring 2019, Section D
 * PURPOSE: simulates an ecosystem to study the effects of releasing mosquitos 
 * with CRISPR genes and displays a graphical output
 *              
 * 
 * USAGE: 
 * java PA8Main nRows nCols
 * 
 * where: nRows and nCols are ints specifying the number of rows and columns in
 * the ecosystem
 *     
 *  SUPPORTED COMMANDS 
 *  
 *  1. CREATE (row,col) mammal-species gender direction
 *  2. CREATE (row, col) bird-species gender side-length
 *  3. CREATE (row, col) insect-species gender clockwise
 *  4. CREATE (row, col) mosquito gender clockwise gene1CRISPR gene2CRISPR
 *  5. REPRODUCE

 *  (where row, col and side-length are ints. gender, direction and all species 
 *  are Strings. clockwise, gene1CRISPR, and gene2CRISPR are booleans.)
 *  
 * all commands are case-insensitive.
 * No support exists for any further commands
 */

import java.util.HashMap;
import java.util.Map;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;


public class PA9Main extends Application {

    private final static int TEXT_SIZE = 120;
    private static final int RECT_SIZE = 40;
    private static int nRows;
    private static int nCols;
    private static Map<String, Color> species2Color = new HashMap<String, Color>();
    private static Ecosys ecosys;

    public static void main(String[] args) {
        nRows = Integer.parseInt(args[0]);
        nCols = Integer.parseInt(args[1]);
        ecosys = new Ecosys(nRows, nCols);
        buildSpecies2Color();

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        BorderPane pane = setUpBorderPane();

        primaryStage.setTitle(
                String.format("Ecosystem Simulation (%d x %d)", nRows, nCols));
        primaryStage.setScene(new Scene(pane));
        primaryStage.show();
    }

    /*
     * sets up and returns the borderpane which contains the info needed to
     * construct primaryStage
     */
    public BorderPane setUpBorderPane() {
        BorderPane pane = new BorderPane();
        TextField cmd_in = new TextField();
        cmd_in.setPrefWidth(Math.max(nCols * RECT_SIZE / 2, 200));
        Button button = new Button("Process");
        final int num_items = 2;
        HBox input_box = new HBox(num_items);
        input_box.getChildren().add(new Text("Enter command here --->"));
        input_box.getChildren().add(cmd_in);
        input_box.getChildren().add(button);

        TextArea output_box = new TextArea();
        output_box.setPrefHeight(TEXT_SIZE);
        output_box.setEditable(false);

        VBox textIO = new VBox(2);
        textIO.getChildren().add(input_box);
        textIO.getChildren().add(output_box);
        pane.setBottom(textIO);

        Canvas canvas = new Canvas(nCols * RECT_SIZE, nRows * RECT_SIZE);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.TAN);
        gc.fillRect(0, 0, nCols * RECT_SIZE, nRows * RECT_SIZE);
        pane.setCenter(canvas);
        button.setOnAction(new HandleInput(cmd_in, output_box, gc));

        return pane;
    }

    /*
     * parses and executes any commands submitted to the input box
     */
    class HandleInput implements EventHandler<ActionEvent> {
        private TextField cmd_in;
        private TextArea outputBox;
        private GraphicsContext gc;

        public HandleInput(TextField cmd_in, TextArea outputBox,
                GraphicsContext gc) {
            this.cmd_in = cmd_in;
            this.outputBox = outputBox;
            this.gc = gc;
        }

        /*
         * parses and executes any commands submitted to the input box
         */
        @Override
        public void handle(ActionEvent event) {
            String input = cmd_in.getText();
            cmd_in.clear();

            String[] infoArray = input.split(" ");
            String cmd = infoArray[0].toUpperCase();

            switch (cmd) {
            case "CREATE":
                ecosys.mkInhab(infoArray);
                break;
            case "MOVE":
                ecosys.mv(infoArray);
                break;
            case "REPRODUCE":
                ecosys.breed(infoArray);
            }

            ecosystemDraw(gc);
            outputBox.appendText(input + "\n");
        }
    }

    /*
     * iterates over the ecoMtx so that each position can be drawn
     * 
     * @param gc
     * GraphicsContext for drawing ecosystem to.
     */
    private void ecosystemDraw(GraphicsContext gc) {
        gc.setFill(Color.TAN);
        gc.fillRect(0, 0, nCols * RECT_SIZE, nRows * RECT_SIZE);

        Pos[][] ecoMtx = ecosys.getEcoMtx();
        for (int i = 0; i < ecoMtx.length; i++) {
            for (int j = 0; j < ecoMtx[0].length; j++) {
                posDraw(gc, ecoMtx, i, j);
            }
        }
    }

    /*
     * draws a given position
     * 
     * @param gc
     * GraphicsContext for drawing ecosystem to.
     * 
     * @param ecoMtx
     * Pos[][] representation of the ecosystem
     * 
     * @param i
     * int, the position's row in the ecoMtx
     * 
     * @param j
     * int, the position's column in the ecoMtx
     */
    private void posDraw(GraphicsContext gc, Pos[][] ecoMtx, int i, int j) {

        Pos pos = ecoMtx[i][j];
        if (!pos.isEmpty()) {
            Animal firstInhab = pos.getInhabs().get(0);
            String species = firstInhab.getSpecies();
            Color color = species2Color.get(species);
            gc.setFill(color);
            gc.fillRect(RECT_SIZE * j, RECT_SIZE * i, RECT_SIZE, RECT_SIZE);
        }
    }

    /*
     * maps each species to a unique color that will be used to represent it
     */
    public static void buildSpecies2Color() {
        // mammals
        species2Color.put("elephant", Color.RED);
        species2Color.put("rhinoceros", Color.ORANGE);
        species2Color.put("lion", Color.YELLOW);
        species2Color.put("giraffe", Color.GREEN);
        species2Color.put("zebra", Color.BLUE);

        // birds
        species2Color.put("thrush", Color.PURPLE);
        species2Color.put("owl", Color.BLACK);
        species2Color.put("warbler", Color.WHITE);
        species2Color.put("shrike", Color.SILVER);

        // insects
        species2Color.put("mosquito", Color.PALEVIOLETRED);
        species2Color.put("bee", Color.LIGHTGOLDENRODYELLOW);
        species2Color.put("fly", Color.BLANCHEDALMOND);
        species2Color.put("ant", Color.CYAN);
    }
}
