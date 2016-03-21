/**
 * Name: Meghna Satish
 * Date: March 3, 2016
 * File: Gui2048
 *
 * Represents a GUI implementation of the game 2048, 
 * including a square pane of tiles with numbers on them.
 * The user's aim is to shift values of the board right, 
 * left, up, and down to combine identical values on
 * the board and avoid 'locking' the board (in which 
 * tiles have no way to move).
 * 
 **/

import javafx.application.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.event.*;
import javafx.scene.input.*;
import javafx.scene.text.*;
import javafx.geometry.*;
import java.util.*;


import java.io.*;

/*
 * Name: Gui2048
 * Purpose: To represent the GUI aspects of the game 2048
 */
public class Gui2048 extends Application {

   private String outputBoard; // The filename for where to save the Board
   private Board board; // The 2048 Game Board

   private static final int TILE_WIDTH = 106;

   private static final int TEXT_SIZE_LOW = 55; // Low value tiles (2,4,8,etc)
   private static final int TEXT_SIZE_MID = 45; // Mid value tiles 
   //(128, 256, 512)
   private static final int TEXT_SIZE_HIGH = 35; // High value tiles 
   //(1024, 2048, Higher)

   // Fill colors for each of the Tile values
   private static final Color COLOR_EMPTY = Color.rgb(238, 228, 218, 0.35);
   private static final Color COLOR_2 = Color.rgb(238, 228, 218);
   private static final Color COLOR_4 = Color.rgb(237, 224, 200);
   private static final Color COLOR_8 = Color.rgb(242, 177, 121);
   private static final Color COLOR_16 = Color.rgb(245, 149, 99);
   private static final Color COLOR_32 = Color.rgb(246, 124, 95);
   private static final Color COLOR_64 = Color.rgb(246, 94, 59);
   private static final Color COLOR_128 = Color.rgb(237, 207, 114);
   private static final Color COLOR_256 = Color.rgb(237, 204, 97);
   private static final Color COLOR_512 = Color.rgb(237, 200, 80);
   private static final Color COLOR_1024 = Color.rgb(237, 197, 63);
   private static final Color COLOR_2048 = Color.rgb(237, 194, 46);
   private static final Color COLOR_OTHER = Color.BLACK;
   private static final Color COLOR_GAME_OVER = Color.rgb(238, 228, 218, 0.73);

   private static final Color COLOR_VALUE_LIGHT = Color.rgb(249, 246, 242); 
   // For tiles >= 8

   private static final Color COLOR_VALUE_DARK = Color.rgb(119, 110, 101); 
   // For tiles < 8

   /** Add your own Instance Variables here */
   private GridPane pane;
   private StackPane stackpane;
   private int gridSize;


   /*
    * Name: start
    * Purpose: create the pane and initialize all rectangle and text objects
    * Parameters: primaryStage (stage)- used to set the scene, as well as
    * 	min and max widths of the pane
    * Return: void
    */
   @Override
      public void start(Stage primaryStage)
      {

         // Process Arguments and Initialize the Game Board
         processArgs(getParameters().getRaw().toArray(new String[0]));

         // Create the pane that will hold all of the visual objects
         pane = new GridPane();
         pane.setAlignment(Pos.CENTER);
         pane.setPadding(new Insets(11.5, 12.5, 13.5, 14.5));
         pane.setStyle("-fx-background-color: rgb(187, 173, 160)");
         // Set the spacing between the Tiles
         pane.setHgap(15); 
         pane.setVgap(15);

         //create stackpane to layer objects
         stackpane = new StackPane();
         stackpane.getChildren().add(pane);
         this.gridSize = board.getGrid().length;

         int initialBoardWidth = gridSize * (TILE_WIDTH + 15);
         int initialBoardHeight = TILE_WIDTH * (gridSize + 1) 
            + 15 * (gridSize + 2);

         Scene scene = new Scene(stackpane, initialBoardWidth, initialBoardHeight);
         primaryStage.setTitle("Gui2048");
         primaryStage.setScene(scene);
         primaryStage.setMinWidth(initialBoardWidth*0.6);
         primaryStage.setMinHeight(initialBoardHeight*0.6);
         scene.setOnKeyPressed(new myKeyHandler());
         primaryStage.show();

         scene.widthProperty().addListener(new ChangeListener<Number>() {
               @Override public void changed(ObservableValue<? extends Number> 
                  observableValue, Number oldValue, Number newValue) {
               refreshDisplay();
               }
               });

         scene.heightProperty().addListener(new ChangeListener<Number>() {
               @Override public void changed(ObservableValue<? extends Number> 
                  observableValue, Number oldValue, Number newValue) {
               refreshDisplay();
               }
               });

         //set header and initial score
         Text header = new Text();
         header.setText("2048");

         Text score = new Text();
         header.setFont(Font.font("Times New Roman", FontWeight.BOLD, 40));
         score.setFont(Font.font("Times New Roman", FontWeight.BOLD, 30));
         score.setText("Score: "+board.getScore());

         //add header text to top of pane
         pane.add(header, 0, 0, 2, 1);
         //add score to top of pane
         pane.add(score, gridSize-2, 0, 2, 1);

         for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {

               Rectangle tile = new Rectangle();
               Text text = new Text();
               //add rectangles and text to pane's rows and cols
               pane.add(tile, j, i+1);
               pane.add(text, j, i+1);

               GridPane.setHalignment(text, HPos.CENTER);
            }
         }
         this.refreshDisplay();
      }


   /*
    * Name: refreshDisplay
    * Purpose: to 'refresh' the tiles of the board after every move
    * Parameters: none
    * Return: void
    */
   public void refreshDisplay() {

      int boardWidth = (int) pane.getScene().getWidth();
      int boardHeight = (int) pane.getScene().getHeight();

      int tileWidth = ((boardWidth - 15*gridSize)/gridSize);
      int tileHeight = ((boardHeight - 15*(gridSize+2))/(gridSize+1));

      ObservableList<Node> children = pane.getChildren();
      //get objects in pane
      for (Node node: children) {
         int textSize = TEXT_SIZE_LOW;
         int row = GridPane.getRowIndex(node);
         int col = GridPane.getColumnIndex(node);
         if (row!= 0) {
            int value = this.board.getGrid()[row-1][col];
            //check if text object
            if (node instanceof Text) { 
               if (value != 0) {
                  ((Text) node).setText(""+value);
                  ((Text) node).setFill(this.getFontColor(value));
                  //if over 3 digits, use high text size
                  if (value > 999) {
                     textSize = TEXT_SIZE_HIGH;
                  }
                  //if over 2 digits (but not over 3), use mid text size
                  else if (value > 99) {
                     textSize = TEXT_SIZE_MID;
                  }
               }
               //if value in tile = 0, have blank square
               else { 
                  ((Text) node).setText("");
               }
               ((Text) node).setFont(Font.font("Times New Roman", 
                     FontWeight.BOLD, textSize));
            }
            else { //if rectangle object
               ((Rectangle) node).setFill(this.getTileColor(value));
               ((Rectangle) node).setWidth(tileWidth);
               ((Rectangle) node).setHeight(tileHeight);
            }
         }
         else if (col == gridSize - 2) { 
            //to update the score
            ((Text) node).setText("Score: "+board.getScore());
         }
      }
   }

   /*
    * Name: getFontColor
    * Purpose: get the appropriate font color based on the value in the tile
    * Parameters: val (int) - value at the specified tile
    * Return: Color (dark if 2 or 4, light otherwise)
    */
   private Color getFontColor(int val) {
      //if 2 or 4, color is dark
      if (val == 2 || val == 4) {
         return COLOR_VALUE_DARK;
      }
      else {
         return COLOR_VALUE_LIGHT;
      }
   }

   /*
    * Name: getTileColor
    * Purpose: get the appropriate tile color based on the value in the tile
    * Parameters: val (int) - value at the specified tile
    * Return: Color (different color for different int values)
    */
   private Color getTileColor(int val) {
      //set tiles based on stored value
      switch(val) {
         case 0:
            return COLOR_EMPTY;
         case 2:
            return COLOR_2;
         case 4:
            return COLOR_4;
         case 8:
            return COLOR_8;
         case 16:
            return COLOR_16;
         case 32:
            return COLOR_32;
         case 64:
            return COLOR_64;
         case 128:
            return COLOR_128;
         case 256:
            return COLOR_256;
         case 512:
            return COLOR_512;
         case 1024:
            return COLOR_1024;
         case 2048:
            return COLOR_2048;
         default:
            return COLOR_OTHER;
      }
   }

   /*
    * Name: main
    * Purpose: Run program
    * Parameters: args (String[]) - arguments
    * Return: void
    */
   public static void main(String[] args) {
      launch(args);
   }

   /*
    * Name: myKeyHandler
    * Purpose: implements EventHandler, handles key presses to move tiles
    */
   private class myKeyHandler implements EventHandler<KeyEvent>{
      @Override

         /*
          * Name: handle
          * Purpose: handle different keypresses by user and move 
          * 	in intended direction
          * Parameters: KeyEvent e (indicates a key has been pressed)
          * Return: void
          */
         public void handle(KeyEvent e){
            if (board.isGameOver()) {
               return;
            }
            Direction d = null;

            /* KeyEvent Processing Code Goes Here */
            //set direction
            switch(e.getCode()) {
               case UP: 
                  d = Direction.UP;
                  break;
               case DOWN:
                  d = Direction.DOWN;
                  break;
               case RIGHT:
                  d = Direction.RIGHT;
                  break;
               case LEFT:
                  d = Direction.LEFT;
                  break;
               case S: //save
                  d = null;
                  System.out.println("Saving board to "+outputBoard);
                  try {
                     board.saveBoard(outputBoard);
                  } catch (IOException ex) {
                     // TODO Auto-generated catch block
                     System.out.println("saveBoard threw an Exception");
                  }
                  break;
               case R: //rotate
                  d = null;
                  System.out.println("Rotating board");
                  board.rotate(true);
                  break;
               default: break;
            }
            //if board can move, move and add random tile
            if ((d!=null) && board.canMove(d)) {
               board.move(d);
               board.addRandomTile();
            }
            refreshDisplay();

            //if board.isgameover, show overlay screen
            if (board.isGameOver()) {
               Rectangle endRect = new Rectangle();
               endRect.setWidth(pane.getWidth());
               endRect.setHeight(pane.getHeight());
               endRect.setFill(COLOR_GAME_OVER);

               Text endText = new Text();
               endText.setText("Game Over!");
               endText.setFont(Font.font("Times New Roman", FontWeight.BOLD, 60));
               stackpane.getChildren().addAll(endRect, endText);
            }
         }
   }


   // The method used to process the command line arguments
   private void processArgs(String[] args)
   {
      String inputBoard = null;   // The filename for where to load the Board
      int boardSize = 0;          // The Size of the Board

      // Arguments must come in pairs
      if((args.length % 2) != 0)
      {
         printUsage();
         System.exit(-1);
      }

      // Process all the arguments 
      for(int i = 0; i < args.length; i += 2)
      {
         if(args[i].equals("-i"))
         {   // We are processing the argument that specifies
            // the input file to be used to set the board
            inputBoard = args[i + 1];
         }
         else if(args[i].equals("-o"))
         {   // We are processing the argument that specifies
            // the output file to be used to save the board
            outputBoard = args[i + 1];
         }
         else if(args[i].equals("-s"))
         {   // We are processing the argument that specifies
            // the size of the Board
            boardSize = Integer.parseInt(args[i + 1]);
         }
         else
         {   // Incorrect Argument 
            printUsage();
            System.exit(-1);
         }
      }

      // Set the default output file if none specified
      if(outputBoard == null)
         outputBoard = "2048.board";
      // Set the default Board size if none specified or less than 2
      if(boardSize < 2)
         boardSize = 4;

      // Initialize the Game Board
      try{
         if(inputBoard != null)
            board = new Board(inputBoard, new Random());
         else
            board = new Board(boardSize, new Random());
      }
      catch (Exception e)
      {
         System.out.println(e.getClass().getName() + 
               " was thrown while creating a " +
               "Board from file " + inputBoard);
         System.out.println("Either your Board(String, Random) " +
               "Constructor is broken or the file isn't " +
               "formated correctly");
         System.exit(-1);
      }
   }

   // Print the Usage Message 
   private static void printUsage()
   {
      System.out.println("Gui2048");
      System.out.println("Usage:  Gui2048 [-i|o file ...]");
      System.out.println();
      System.out.println("  Command line arguments come in pairs of the "+ 
            "form: <command> <argument>");
      System.out.println();
      System.out.println("  -i [file]  -> Specifies a 2048 board that " + 
            "should be loaded");
      System.out.println();
      System.out.println("  -o [file]  -> Specifies a file that should be " + 
            "used to save the 2048 board");
      System.out.println("                If none specified then the " + 
            "default \"2048.board\" file will be used");  
      System.out.println("  -s [size]  -> Specifies the size of the 2048" + 
            "board if an input file hasn't been"); 
      System.out.println("                specified.  If both -s and -i" + 
            "are used, then the size of the board"); 
      System.out.println("                will be determined by the input" +
            " file. The default size is 4.");
   }


}


