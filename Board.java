
/**
 * Name: Meghna Satish
 * Date: February 5, 2016
 * File: Board.java
 * Represents a board for the game 2048. Each row and column intersect to form
 * tiles, which contain numbers (powers of 2 or 0). The user's aim is to shift 
 * values of the board right, left, up, and down to combine identical values on
 * the board and avoid 'locking' the board (in which tiles have no way to move).
 * 
 **/

import java.util.*;
import java.io.*;

/*
 * Name: Board
 * Purpose: To represent a board for a 2048 game
 */
public class Board {
   public final int NUM_START_TILES = 2;
   public final int TWO_PROBABILITY = 90;
   public final int GRID_SIZE;

   private final Random random;
   private int[][] grid;
   private int score;

   // next 2 instance variables are part of extra credit method implementation
   private int[][] undoGrid;
   private int undoScore;
   /*
    * Name: Board
    * Purpose: constructor; 
    *    constructs board w/ random tiles when no input file selected;
    *    initializes grid and score instance variables
    * Parameters: boardSize (int) - dimension of board;
    *    random (Random) - object of Random class   
    * Return: constructor has no return type 
    */
   public Board(int boardSize, Random random) {
      this.random = random; 
      this.GRID_SIZE = boardSize; 
      this.grid = new int[boardSize][boardSize];

      //set first 2 random tiles
      for (int i = 0; i < this.NUM_START_TILES; i++) {
         this.addRandomTile();
      }
   }

   /*
    * Name: Board 
    * Purpose: constructor
    *    initializes instance variables;
    *    loads boards using filename passed through inputBoard parameter
    * Parameters: inputBoard(String) - to be passed to file constructor;
    *    random (Random) - object of Random class
    * Return: constructor has no return type
    */
   public Board(String inputBoard, Random random) throws IOException {
      this.random = random;
      File inputFile = new File (inputBoard);
      Scanner scanner = new Scanner (inputFile);

      //first line of file is size of board
      this.GRID_SIZE = scanner.nextInt();
      this.grid = new int[GRID_SIZE][GRID_SIZE];

      //second line of file is the score
      this.score = scanner.nextInt();

      //remaining lines of file are the actual board
      for (int row = 0; row < this.GRID_SIZE; row++) {
         for (int col = 0; col < this.GRID_SIZE; col++) {
            this.grid[row][col] = scanner.nextInt();
         }
      }
   }

   /*
    * Name: saveBoard
    * Purpose: to save the current board to a file (specified by parameter)
    * Parameters: outputBoard (String) - to specify file to save board to
    * Return: void
    */
   public void saveBoard(String outputBoard) throws IOException {
      File outputFile = new File (outputBoard);
      PrintWriter output = new PrintWriter (outputFile);
      output.println(this.GRID_SIZE);
      output.println(this.score);

      for (int row = 0; row < this.GRID_SIZE; row++) {
         for (int col = 0; col < this.GRID_SIZE; col++) {
            // write output board to file
            output.print(this.grid[row][col] + " ");
         }
         output.println(); 
      }
      output.close();
   }

   /*
    * Name: getOpenTilesCount
    * Purpose: private method to find the count of open tiles
    * Parameters: grid (int[][]) - 2048 grid
    * Return: int (count of open tiles)
    */
   private int getOpenTilesCount(int[][] grid) {
      int count = 0;
      for (int row = 0; row < grid.length; row++) {
         for (int col = 0; col < grid.length; col++) {
            //if tile is open, count it
            if (grid[row][col] == 0) {
               count++;
            }
         }
      }
      return count;
   }

   /*
    * Name: addRandomTile 
    * Purpose: to add a random tile (2 or 4) to an open space on the board
    * Parameters: none
    * Return: void
    */
   public void addRandomTile() {
      int count = getOpenTilesCount(this.grid);

      //if no empty tiles, return without changing board
      if (count == 0) {
         return;
      }
      int location = this.random.nextInt(count);
      int value = this.random.nextInt(100);

      /*
       * Variable index will track empty spots until it equals location.
       * Index is initialized to 0 and can be incremented at each empty
       * spot that isn't the location'th one
       */
      int index = 0;

      for (int row = 0; row < this.GRID_SIZE; row++) {
         for (int col = 0; col < this.GRID_SIZE; col++) {
            if (this.grid[row][col] == 0) {

               //first possible location is at index 0
               if (index == location) {
                  if (value < TWO_PROBABILITY) {
                     this.grid[row][col] = 2;
                  }
                  else {
                     this.grid[row][col] = 4;
                  }
               }
               index++;
            }
         }
      }

   } 

   /*
    * Name: rotate
    * Purpose: to rotate the board clockwise or counterclockswise depending
    *    on the truth value of rotateClockwise;
    *    rotation is 90 degrees
    * Parameters: rotateClockwise (boolean) - determines rotation direction
    * Return: void
    */
   public void rotate(boolean rotateClockwise) {
      int[][] newGrid = new int[GRID_SIZE][GRID_SIZE];
      for (int oldR = 0, newR = 0; oldR < this.GRID_SIZE; oldR++, newR++) {
         for (int oldC = 0, newC = this.GRID_SIZE-1; 
               oldC < this.GRID_SIZE; oldC++, newC--) {
            if (rotateClockwise) {
               //new row values from old column
               //new column from other end of grid from old row
               newGrid[newR][newC] = this.grid[oldC][oldR];
            }
            else {
               newGrid[newC][newR] = this.grid[oldR][oldC];
            }
         }
      }
      this.grid = newGrid;
   }
   /*
    * Name: isPowerOfTwo 
    * Purpose: private method to check if integer is a power of 2
    * Parameters: n (int) - integer to test
    * Return: boolean - true if n is a power of 2, false otherwise
    */
   private static boolean isPowerOfTwo(int n) {
      //exponent has to be at least 1
      //n has to be at least 2, so n > 1
      //check if n is a power of 2
      return ((n>1) && ((n & (n-1)) == 0));
   }

   /*
    * Name: isInputFileCorrectFormat
    * Purpose: to test if file to be read is in the correct format
    * Parameters: inputFile (String) - file to read from
    * Return: boolean (true if file is in correct format, false otherwise)
    */
   public static boolean isInputFileCorrectFormat(String inputFile) {
      try {
         //2nd line is positive int that shows score
         //1st line is positive int showing grid
         Scanner scanner = new Scanner (inputFile);
         int gridSize = scanner.nextInt();

         // check if gridsize is less than 2
         if (gridSize < 2) {
            return false;
         }

         //check if score is negative
         int scoreVal = scanner.nextInt();

         if (scoreVal < 0) {
            return false;
         }

         for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
               int temp = scanner.nextInt();

               //check if board values aren't powers of 2
               if (!(isPowerOfTwo(temp))) {
                  return false;
               }
            }
         }
         return true;
      }
      catch (Exception e) {
         return false;
      }
   }

   /*
    * Name: canMoveRight
    * Purpose: helper method to check if any row in grid can move right
    * Parameters: none
    * Return: boolean (true if moving right is possible)
    */
   private boolean canMoveRight() {
      // true if nonzero number followed by a 0
      // true if 2 consecutive nonzero numbers that are the same
      for (int rowIndex = 0; rowIndex < this.GRID_SIZE; rowIndex++) {
         for (int i = 0; i < this.GRID_SIZE - 1; i++) {
            if (this.grid[rowIndex][i] != 0) {
               if (this.grid[rowIndex][i+1] == 0) {
                  return true;
               }
               else if (this.grid[rowIndex][i] == 
                     this.grid[rowIndex][i+1]) {
                  return true;
               }
            }
         }
      }
      return false;
   }

   /*
    * Name: canMoveLeft
    * Purpose: helper method to check if any row in grid can move left
    * Parameters: none
    * Return: boolean (true if moving left is possible)
    */
   private boolean canMoveLeft() {
      // true if 0 followed by a nonzero number (from left to right) in row
      // true if 2 consecutive numbers in row that are the same
      for (int rowIndex = 0; rowIndex < this.GRID_SIZE; rowIndex++) {
         for (int i = 0; i < this.GRID_SIZE - 1; i++) {
            if (this.grid[rowIndex][i] == 0) {
               if (this.grid[rowIndex][i+1] != 0) {
                  return true;
               }
            }
            else if (this.grid[rowIndex][i] == this.grid[rowIndex][i+1]) {
               return true;
            }
         }
      }
      return false;
   }

   /*
    * Name: canMoveUp
    * Purpose: helper method to check if any column in grid can move up
    * Parameters: none
    * Return: boolean (true if moving up is possible)
    */
   private boolean canMoveUp() {
      // true if 0 is above any nonzero number in column
      // true if 2 consecutive equal nonzero integers in column
      for (int colIndex = 0; colIndex < this.GRID_SIZE; colIndex++) {
         for (int r = 0; r < this.GRID_SIZE - 1; r++) {
            if (this.grid[r][colIndex] == 0) {
               if (this.grid[r+1][colIndex] != 0) {
                  return true;
               }
            }
            else if (this.grid[r][colIndex] == this.grid[r+1][colIndex]) {
               return true;
            }
         }
      }
      return false;
   }

   /*
    * Name: canMoveDown
    * Purpose: helper method to check if any column in grid can move down
    * Parameters: none
    * Return: boolean (true if moving down is possible)
    */
   private boolean canMoveDown() {
      //true if nonzero number above 0 in column
      //true if 2 consecutive equal nonzero numbers in column
      for (int colIndex = 0; colIndex < this.GRID_SIZE; colIndex++) {
         for (int r = 0; r < this.GRID_SIZE - 1; r++) {
            if (this.grid[r][colIndex] != 0) {
               if (this.grid[r+1][colIndex] == 0) {
                  return true;
               }
               else if (this.grid[r][colIndex] ==
                     this.grid[r+1][colIndex]) {
                  return true;
               }
            }
         }
      }
      return false;
   }

   /*
    * Name: moveRight
    * Purpose: perform the actual move to the right
    * Parameters: none
    * Return: void
    */
   private void moveRight() {
      for (int rowVal = 0; rowVal < this.GRID_SIZE; rowVal++) {
         ArrayList<Integer> row = new ArrayList<Integer>();
         // add to arraylist from row array
         for (int i = 0; i < this.GRID_SIZE; i++) {
            row.add(i, this.grid[rowVal][i]);
         }

         // index = pointer for which square we're looking at
         int index = this.GRID_SIZE - 1;

         // firstNumIndex = closest nonzero number before current index
         // initialized to -1 so we'll know if there are no nonzeros
         int firstNumIndex = -1;

         while (index > 0) {
            if (!(row.get(index).equals(0))) {
               firstNumIndex = -1;
               //loop to find rightmost nonzero num before index
               for (int i = 0; i < index; i++) {
                  if (!(row.get(i).equals(0))) {
                     firstNumIndex = i;
                  }
               }
               if (firstNumIndex >= 0) {
                  if (row.get(index).equals(row.get(firstNumIndex))) {
                     row.set(index, (row.get(index))*2);
                     row.set(firstNumIndex, 0);
                     this.score += row.get(index);
                     index--;
                  }
                  //else if number at index is not same as at firstNumIndex
                  else {
                     //move on; index is now one number to the left
                     index--;
                  }
               }
               //else if no nonzero numbers before last, while loop done
               else {
                  index = 0;
               }
            }
            // else if number at starting index is 0
            else {
               firstNumIndex = -1;
               for (int i = 0; i < index; i++) {
                  if (!(row.get(i).equals(0))) {
                     firstNumIndex = i;
                  }
               }
               // if there exists a nonzero number 
               // beyond current (index) number, switch them
               if (firstNumIndex >= 0) {
                  row.set(index, (row.get(firstNumIndex)));
                  row.set(firstNumIndex, 0);
               }
               // if no nonzero numbers, exit while loop
               else {
                  index = 0;
               }
            }
            //closes while loop  
         }
         for (int i = 0; i < this.GRID_SIZE; i++) {
            this.grid[rowVal][i] = (row.get(i)).intValue();
         }
         //closes for loop that iterates through rows of grid   
      }
   }

   /*
    * Name: moveLeft
    * Purpose: perform the actual move to the left
    * Parameters: none
    * Return: void
    */
   private void moveLeft() {
      for (int rowVal = 0; rowVal < this.GRID_SIZE; rowVal++) {
         ArrayList<Integer> row = new ArrayList<Integer>();
         for (int i = 0; i < this.GRID_SIZE; i++) {
            row.add(i, this.grid[rowVal][i]);
         }

         int index = 0;
         int firstNumIndex = -1;
         while (index < this.GRID_SIZE - 1) {
            if (!(row.get(index).equals(0))) {
               firstNumIndex = -1;

               //loop to find leftmost nonzero number after index
               for (int i = this.GRID_SIZE - 1; i > index; i--) {
                  if (!(row.get(i).equals(0))) {
                     firstNumIndex = i;
                  }
               }
               if (firstNumIndex >= 0) {
                  if (row.get(index).equals(row.get(firstNumIndex))) {
                     row.set(index, (row.get(index))*2);
                     row.set(firstNumIndex, 0);
                     this.score += row.get(index);
                     index++;
                  }
                  else {
                     index++;
                  }
               }
               // if no first nonzero number, exit while loop
               else {
                  index = this.GRID_SIZE - 1;
               }
            }

            // else if number at starting index is 0
            else {
               firstNumIndex = -1;
               for (int i = this.GRID_SIZE - 1; i > index; i--) {
                  if (!(row.get(i).equals(0))) {
                     firstNumIndex = i;
                  }
               }
               // if nonzero number exists beyond leading 0, switch w/ index
               if (firstNumIndex >= 0) {
                  row.set(index, (row.get(firstNumIndex)));
                  row.set(firstNumIndex, 0);
               }

               // else if no nonzero numbers, exit while loop
               else {
                  index = this.GRID_SIZE - 1;
               }
            }
         }

         for (int i = 0; i < this.GRID_SIZE; i++) {
            this.grid[rowVal][i] = (row.get(i)).intValue();
         }
      }
   }

   /*
    * Name: moveUp
    * Purpose: perform the actual move up
    * Parameters: none
    * Return: void
    */
   private void moveUp() {
      // moving up is the same as rotating counterclockwise and moving left
      // then rotate clockwise
      this.rotate(false);
      this.moveLeft();
      this.rotate(true);
   }

   /*
    * Name: moveDown
    * Purpose: perform the actual move down
    * Parameters: none
    * Return: void
    */
   private void moveDown() {
      //moving down is same as rotating counterclockwise and moving right
      //then rotate clockwise
      this.rotate(false);
      this.moveRight();
      this.rotate(true);
   }

   /*
    * Name: move
    * Purpose: Performs a move operation in the specified direction
    * Parameters: direction (Direction) - indicates which direction to move
    * Return: boolean (true if move occurs successfully, false otherwise)
    */
   public boolean move(Direction direction) {
      if (!this.canMove(direction)) {
         return false;
      } 
      // if canMove returns true, enable undo
      this.enableUndo();

      // move in specified direction

      if (direction.equals(Direction.RIGHT)) {
         this.moveRight();
      }
      else if (direction.equals(Direction.LEFT)) {
         this.moveLeft();
      }
      else if (direction.equals(Direction.UP)) {
         this.moveUp();
      }
      else if (direction.equals(Direction.DOWN)) {
         this.moveDown();
      }
      return true;
   }

   /*
    * Name: isGameOver
    * Purpose: check to see if game is over
    * Parameters: none
    * Return: boolean (true if cannot move in any direction)
    */
   public boolean isGameOver() {
      //game is over if cannot move in any direction
      if (!canMoveLeft() && !canMoveRight() &&
            !canMoveUp() && !canMoveDown()) { 
         System.out.println("Game Over!");
         return true;
      }
      return false;
   }

   /*
    * Name: canMove
    * Purpose: determine if we can move in a given direction
    * Parameters: direction (Direction) - which direction to move
    * Return: boolean (true if can move in specified direction)
    */
   public boolean canMove(Direction direction) {
      // check which direction is passed in
      // check if canMove in that direction by calling helper canMove method
      if (direction.equals(Direction.RIGHT)) {
         return this.canMoveRight();
      }
      else if (direction.equals(Direction.LEFT)) {
         return this.canMoveLeft();
      }
      else if (direction.equals(Direction.UP)) {
         return this.canMoveUp();
      }
      else if (direction.equals(Direction.DOWN)) {
         return this.canMoveDown();
      }
      return false;
   }

   /*
    * Name: enableUndo
    * Purpose: helper method to initialize undoGrid and undoScore
    *  to stored values in grid and score;
    *  this method helps in the implementation of the extra credit method 
    * Parameters: none
    * Return: void
    */
   private void enableUndo() {
      // undoGrid is initialized to hold same values as original grid
      // undoScore is initialized 
      this.undoGrid = new int[GRID_SIZE][GRID_SIZE];
      for (int i = 0; i < this.GRID_SIZE; i++) {
         for (int j = 0; j < this.GRID_SIZE; j++) {
            undoGrid[i][j] = this.grid[i][j];
         }
      }
      this.undoScore = this.score;
   }

   /*
    * Name: canUndo
    * Purpose: helper method to check if undo can be called
    * Parameters: none
    * Return: boolean (true if undo can be called, false otherwise)
    */
   public boolean canUndo() {
      // if undoGrid is null, undo has already been called
      // it cannot be called twice in a row
      if (this.undoGrid == null) {
         return false;
      }
      return true;
   }

   /*
    * Name: undo
    * Purpose: undo last move (should be called when 'u' key is pressed)
    * Parameters: none
    * Return: void
    */
   public void undo() {
      // if undo is not possible, then return right away
      if (!canUndo()) {
         return;
      }
      // if undo is possible and is called, set grid and score to undoGrid
      //   and undoScore
      this.grid = this.undoGrid;
      this.score = this.undoScore;
      this.undoGrid = null;
   }


   // Return the reference to the 2048 Grid
   public int[][] getGrid() {
      return grid;
   }

   // Return the score
   public int getScore() {
      return score;
   }

   @Override
      public String toString() {
         StringBuilder outputString = new StringBuilder();
         outputString.append(String.format("Score: %d\n", score));
         for (int row = 0; row < GRID_SIZE; row++) {
            for (int column = 0; column < GRID_SIZE; column++)
               outputString.append(grid[row][column] == 0 ? "    -" :
                     String.format("%5d", grid[row][column]));

            outputString.append("\n");
         }
         return outputString.toString();
      }
}
