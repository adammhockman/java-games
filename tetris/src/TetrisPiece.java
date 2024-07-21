import java.awt.*;
import java.util.Scanner;

/**
 * Class used to define a single piece in Tetris.
 * A piece cannot be instantiated without an ambient board to hold it.
 * By default, a 4x4 grid is established to hold the piece.
 * When the Board class instantiates a piece, it must pass the board width and height
 * as parameters.
 * All pieces must have a pivot location to define where the piece sits within the board.
 * The pivot point is used for rotation and initialization as well.
 * Piece is conceptualized as a list of coordinates where the piece is defined + pivot.
 * Example: boardWidth = 4, boardHeight = 4:
 * 00 01 02 03
 * 04 05 06 07
 * 08 09 10 11
 * 12 13 14 15
 * Then the pivot point is 1 and the pieces are given as:
 * O = {1, 2, 5, 6}
 * I = {1, 5, 9, 13}
 * S = {1, 2, 4, 5}
 * Z = {0, 1, 5, 6}
 * L = {1, 5, 9, 10}
 * J = {1, 5, 8, 9}
 * T = {1, 4, 5, 6}
 */
public class TetrisPiece {

    public static int NUM_OF_COORDINATES = 4;

    private static int MIN_BOARD_HEIGHT = 4;
    private static int MIN_BOARD_WIDTH = 4;

    private final Color pieceColor;

    private final int boardHeight;
    private final int boardWidth;
    private final char pieceType;

    private int[] coordinates;
    private int pivot;

// ***************************************************************************
//   * Constructors.
// ***************************************************************************

    /**
     * Constructor that takes a specified type and size and initializes a new TetrisPiece.
     * This is the main constructor the other constructors call.
     * Piece coordinates are assigned based on the piece type, and the pivot point.
     * The pivot point is computed automatically. This is used when generating new
     * pieces at the top of the board.
     * Note: Cannot instantiate a piece in the middle of the board, only the top.
     * Throws an exception is dimensions provided are less than the global MIN values.
     *
     * @param pieceType char representing the type of piece to be generated
     * @param boardHeight the height of the ambient board holding the piece
     * @param boardWidth the width of the ambient board holding the piece
     * @throws IllegalArgumentException if the provided board dimensions are too small
     */
    public TetrisPiece(char pieceType, Color pieceColor, int boardHeight, int boardWidth) {

        if (boardHeight < MIN_BOARD_HEIGHT || boardWidth < MIN_BOARD_WIDTH)
            throw new IllegalArgumentException("Board dimensions provided are too small.");

        this.pieceType = pieceType;
        this.pieceColor = pieceColor;
        this.boardHeight = boardHeight;
        this.boardWidth = boardWidth;

        this.pivot = (boardWidth - 1) / 2;

        setInitialPieceCoordinates();

    }

    /**
     * Constructor allows us to assign all instance variables upon creation. Note
     * that the Board should never call this method. The Board will always generate
     * a new piece at the top.
     * This constructor allows for a copy() method. This returns a new reference to
     * a TetrisPiece that has all the same field data (deep copied).
     * This will be necessary in testing for boundaries and collisions when moving
     * the piece.
     * We will attempt to move a copy of the piece, then only update our piece if the
     * new piece is valid.
     * @param pieceType the char piece type
     * @param boardHeight the height of the board
     * @param boardWidth the width of the board
     * @param coordinates the coordinates of the piece
     * @param pivot the current pivot point of the piece
     */
    public TetrisPiece(char pieceType, Color pieceColor, int boardHeight, int boardWidth, int[] coordinates, int pivot) {

        if (boardHeight < MIN_BOARD_HEIGHT || boardWidth < MIN_BOARD_WIDTH)
            throw new IllegalArgumentException("Board dimensions provided are too small.");

        this.pieceType = pieceType;
        this.pieceColor = pieceColor;
        this.boardHeight = boardHeight;
        this.boardWidth = boardWidth;
        this.coordinates = coordinates;
        this.pivot = pivot;

    }

    /**
     * Private method used to assign the initial piece coordinates to the class variable.
     * Also assumes the initial pivot point has already been set.
     * This is used later for moving pieces.
     * This method uses the values assigned to the global class variables in the constructor.
     * Separated as a separate method for code cleanliness and organization only.
     * Called by main constructor.
     */
    private void setInitialPieceCoordinates() {

        // initialize the coordinates array
        this.coordinates = new int[NUM_OF_COORDINATES];

        switch (pieceType) {
            case ('O'):
                coordinates[0] = pivot;
                coordinates[1] = pivot + 1;
                coordinates[2] = pivot + boardWidth;
                coordinates[3] = pivot + boardWidth + 1;
                break;
            case ('I'):
                coordinates[0] = pivot;
                coordinates[1] = pivot + boardWidth;
                coordinates[2] = pivot + boardWidth * 2;
                coordinates[3] = pivot + boardWidth * 3;
                break;
            case ('S'):
                coordinates[0] = pivot;
                coordinates[1] = pivot + 1;
                coordinates[2] = pivot + boardWidth - 1;
                coordinates[3] = pivot + boardWidth;
                break;
            case ('Z'):
                coordinates[0] = pivot - 1;
                coordinates[1] = pivot;
                coordinates[2] = pivot + boardWidth;
                coordinates[3] = pivot + boardWidth + 1;
                break;
            case ('L'):
                coordinates[0] = pivot;
                coordinates[1] = pivot + boardWidth;
                coordinates[2] = pivot + boardWidth * 2;
                coordinates[3] = pivot + boardWidth * 2 + 1;
                break;
            case ('J'):
                coordinates[0] = pivot;
                coordinates[1] = pivot + boardWidth;
                coordinates[2] = pivot + boardWidth * 2 - 1;
                coordinates[3] = pivot + boardWidth * 2;
                break;
            case ('T'):
                coordinates[0] = pivot;
                coordinates[1] = pivot + boardWidth - 1;
                coordinates[2] = pivot + boardWidth;
                coordinates[3] = pivot + boardWidth + 1;
                break;
            default:
        }
    }


// ***************************************************************************
//   * Accessor methods for encapsulating piece information.
// ***************************************************************************

    /**
     * Accessor method used to extract coordinates. Needed for equals() method.
     *
     * @return the array of coordinates for the instance piece
     */
    public int[] getCoordinates() {

        return coordinates;

    }

    /**
     * Accessor method used to extract ambient boardHeight. Needed for equals() method.
     *
     * @return the ambient board height containing the instance piece
     */
    public int getBoardHeight() {

        return boardHeight;

    }

    /**
     * Accessor method used to extract ambient boardWidth. Needed for equals() method.
     *
     * @return the ambient board width containing the instance piece
     */
    public int getBoardWidth() {

        return boardWidth;

    }

    public char getPieceType() {
        return pieceType;
    }

    public Color getPieceColor() { return pieceColor; }

    // ***************************************************************************
//   * Method for copying instances.
// ***************************************************************************

    /**
     * Public method used to obtain a new reference (separate from the instance
     * piece) to a TetrisPiece containing the same fields as the instance
     * calling it.
     * @return pointer to a new TetrisPiece with the same fields
     */
    public TetrisPiece copy() {

        // deep copy the coordinates array before initializing
        int[] copyCoordinates = new int[NUM_OF_COORDINATES];
        for (int i = 0; i < NUM_OF_COORDINATES; i++) {
            copyCoordinates[i] = coordinates[i];
        }

        return new TetrisPiece(pieceType, pieceColor, boardHeight, boardWidth, copyCoordinates, pivot);

    }

// ***************************************************************************
//   * Methods for rotating piece.
// ***************************************************************************

    /**
     * Method rotates the instance piece calling it by updating the piece's
     * coordinates. Logic is determined by the pieceType and the pivot position.
     * Overwrites the coordinates list with the coordinates of the resulting rotated piece.
     * Must check that rotation won't cross board boundaries.
     */
    public boolean rotate() {

        // piece type 'O' can always be rotated
        if (pieceType == 'O')
            return true;

        if (pivotAlongEdge())
            return false;


        // first rotate the underlying grid for all pieces, using a 4x4 grid
        // for the 'I' piece, and a 3x3 for the other pieces.
        if (pieceType == 'I')
            rotateNxN(4);
        else
            rotateNxN(3);

        // then shift up the pieces to their pivot, if necessary.
        if (pieceType == 'I' || pieceType == 'S' || pieceType == 'Z')
            shiftToPivot();

        return true;

    }

    /**
     * Private method used to verify whether a piece can be rotated.
     * Uses basic logic based on the pivot's row and column value.
     * @return true if the pivot is near the edge and hence, cannot be rotated
     */
    private boolean pivotAlongEdge() {

        // check for left and right edge
        // Fact: If the pivot is along the left or right column,
        // the piece can not be rotated
        int pivotCol = pivot % boardWidth;
        boolean sidesCheck;

        // NOTE: The 'I' piece has a separate check for the right side, and bottom
        // since it needs an additional square to rotate
        if (pieceType == 'I')
            sidesCheck = (pivotCol == 0 || (boardWidth - 1 - pivotCol) < 2);
        else
            sidesCheck = (pivotCol == 0 || (boardWidth - 1 - pivotCol) < 1);


        // check for bottom edge
        // Fact: If the pivot point is 1 row from the bottom row,
        // the piece can not be rotated
        int pivotRow = pivot / boardWidth;
        boolean bottomCheck;

        if (pieceType == 'I')
            bottomCheck = (boardHeight - 1 - pivotRow) < 3;
        else
            bottomCheck = (boardHeight - 1 - pivotRow) < 2;


        return (sidesCheck || bottomCheck);

    }

    /**
     * Private method used to help with rotating the 'I', 'S', and 'Z' piece.
     * These pieces only have one rotation since the second rotation brings them back to their
     * original coordinates, only translated. Therefore, we occasionally need to shift the
     * piece up so that the top row of the piece is the same row as the pivot point (for 'S'
     * or 'Z'), or one below the pivot for 'I'.
     */
    private void shiftToPivot() {

        // check that the row of the tallest piece coordinate is in the
        // same row as the pivot point, and adjust otherwise
        // NOTE: this will naturally leave the 'I' piece one below the
        // pivot, in a horizontal position (as desired)
        int topRow = boardHeight;
        for (int i = 0; i < NUM_OF_COORDINATES; i++) {
            int entry = coordinates[i];
            int entryRow = entry / boardWidth;
            if (entryRow < topRow)
                topRow = entryRow;
        }
        int pivotRow = pivot / boardWidth;
        if (pivotRow < topRow) {
            for (int j = 0; j < NUM_OF_COORDINATES; j++) {
                coordinates[j] = coordinates[j] - boardWidth;
            }
        }

    }

    /**
     * Private method used to rotate the ambient NxN grid holding the piece (excl. 'O').
     * The 'I' piece requires a 4x4 rotation, whereas the rest use a 3x3.
     * Uses the representation in coordinates[], and the pivot location, to update coordinates
     * to rotated position. We rely on the relationship: pivot vs boardWidth
     * First map the coordinates to a grid representation.
     * Then rotate the grid and map back to coordinates.
     * Mapping will depend on pivot point.
     */
    private void rotateNxN(int N) {

        // DEBUG
        /*
        System.out.println("rotateNxN() called with N = " + N);
        System.out.println("Coordinates: " + showCoordinates());
        System.out.println("Pivot      : " + pivot);
        System.out.println("----------------------------------------------");
         */

        // set grid size to value N received
        int miniGridSize = N;

        // first: translate coordinates to a NxN grid representation as follows:
        // initialize a new grid of 1s and 0s
        int[][] grid = new int[miniGridSize][miniGridSize];

        // DEBUG
        /*
        System.out.println("Creating grid to visualize transformation matrix.");
        printGrid(grid);

         */

        // first we observe the row and col shift by noting pivot vs boardWidth
        // p - 1 = rowShift * (boardWidth) + colShift
        int rowShift = (pivot - 1) / boardWidth;
        int colShift = (pivot - 1) % boardWidth;

        // DEBUG
        /*
        System.out.println("p - 1 = rowShift * boardWidth + colShift");
        System.out.println("   p - 1 = " + (pivot-1));
        System.out.println("rowShift = " + rowShift);
        System.out.println("colShift = " + colShift);
        System.out.println("----------------------------------------------");


        // DEBUG
        System.out.println("Processing coordinate entries:");
        System.out.println();

         */

        // for each entry in coordinates determine entry location then apply
        // row and column shifts from above
        for (int i = 0; i < NUM_OF_COORDINATES; i++) {
            // entry = entryRow ( boardWidth ) + entryCol
            int entry = coordinates[i];
            int entryRow = entry / boardWidth;
            int entryCol = entry % boardWidth;

            // DEBUG
            /*
            System.out.println("Rotating entry coordinate : " + entry);
            System.out.println();
            System.out.println("row = entry d boardWidth : " + entryRow);
            System.out.println("col = entry % boardWidth : " + entryCol);
            System.out.println();

             */

            // next apply the row and col shift from above to update the entry positions
            entryRow -= rowShift;
            entryCol -= colShift;

            // DEBUG
            /*
            System.out.println("entryRow = entryRow - rowShift : " + entryRow);
            System.out.println("entryCol = entryCol - colShift : " + entryCol);
            System.out.println();

             */


            // set location to 1 to indicate piece coordinate
            grid[entryRow][entryCol] = 1;

            // DEBUG
            /*
            printGrid(grid);
            System.out.println("----------------------------------------------");

             */

        }


        // DEBUG STATEMENT
        /*
        System.out.println("New coordinate grid:");
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j] == 0)
                    System.out.print("- ");
                else
                    System.out.print("0 ");
            }
            System.out.println();
        }

         */


        // second: rotate the NxN grid counter-clockwise
        // we can apply a sort of reverse transpose to rotate the matrix
        int[][] rotatedGrid = new int[miniGridSize][miniGridSize];
        for (int col = 0; col < miniGridSize; col++) {
            // apply the values (in reverse) to the corresponding col
            for (int row = 0; row < miniGridSize; row++) {
                rotatedGrid[row][col] = grid[col][miniGridSize - row - 1];
            }
        }
        grid = rotatedGrid;


        // DEBUG STATEMENT
        /*
        System.out.println("New rotated coordinate grid:");
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j] == 0)
                    System.out.print("- ");
                else
                    System.out.print("0 ");
            }
            System.out.println();
        }

         */


        // third: translate the rotated grid to coordinates list
        // wherever there is a '1' entry, reverse the mapping above
        int[] rotatedCoordinates = new int[NUM_OF_COORDINATES];
        int index = 0;
        for (int i = 0; i < miniGridSize; i++) {
            for (int j = 0; j < miniGridSize; j++) {
                if (grid[i][j] == 1) {
                    // first reverse the row and col shift
                    int entryRow = i + rowShift;
                    int entryCol = j + colShift;

                    // then reverse the mapping by modular arithmetic
                    int entryVal = entryRow * boardWidth + entryCol;

                    // store the new coordinate value
                    rotatedCoordinates[index++] = entryVal;
                }
            }
        }
        // update the coordinates list with the new one
        coordinates = rotatedCoordinates;

        // DEBUG STATEMENT
        /*
        System.out.println("Coordinates for the new piece after rotation:");
        System.out.print("{ ");
        for (int i = 0; i < NUM_OF_COORDINATES; i++) {
            System.out.print(coordinates[i] + ",");
            if (i < NUM_OF_COORDINATES - 1)
                System.out.print(" ");
            else
                System.out.print("}");
        }
        System.out.println();

         */

    }

// ***************************************************************************
//   * Methods for translating piece. Left, Down, Right
// ***************************************************************************

    /**
     * Public method used to translate the piece to the left.
     * Must check if moving the piece would exceed the board boundary.
     * @return true if the piece was able to be moved successfully
     */
    public boolean left() {

        // update a copy of the coordinates, that way we don't override some
        // coordinates but return false on others.
        int[] shiftCoordinates = new int[NUM_OF_COORDINATES];

        // verify that each coordinate can be translated left,
        // and update the coordinate accordingly
        for (int i = 0; i < NUM_OF_COORDINATES; i++) {

            // get value and column location
            int entryVal = coordinates[i];
            int entryCol = entryVal % boardWidth;

            // cannot shift left if any coordinate is in column 0
            // otherwise, subtract 1 to represent a shift left
            if (entryCol == 0) {
                return false;
            } else {
                shiftCoordinates[i] = entryVal - 1;
            }
        }
        // at this point, all coordinates were able to be shifted left,
        // so override coordinates with the new shifted coordinates
        coordinates = shiftCoordinates;
        // and update the pivot point accordingly
        pivot = pivot - 1;

        return true;
    }

    /**
     * Public method used to translate the piece to the right.
     * Must check if moving the piece would exceed the board boundary
     * @return true if the piece was able to be moved successfully
     */
    public boolean right() {

        // update a copy of the coordinates, that way we don't override some
        // coordinates but return false on others.
        int[] shiftCoordinates = new int[NUM_OF_COORDINATES];

        // verify that each coordinate can be translated right,
        // and update the coordinate accordingly
        for (int i = 0; i < NUM_OF_COORDINATES; i++) {

            // get value and column location
            int entryVal = coordinates[i];
            int entryCol = entryVal % boardWidth;

            // cannot shift right if any coordinate is in column boardWisth - 1
            // otherwise, add 1 to represent a shift right
            if (entryCol == (boardWidth - 1)) {
                return false;
            } else {
                shiftCoordinates[i] = entryVal + 1;
            }
        }
        // at this point, all coordinates were able to be shifted right,
        // so override coordinates with the new shifted coordinates
        coordinates = shiftCoordinates;
        // and update the pivot point accordingly
        pivot = pivot + 1;

        return true;
    }

    /**
     * Public method used to translate the piece down.
     * Must check if moving the piece would exceed the board boundary
     * @return true if the piece was able to be moved successfully
     */
    public boolean down() {

        // update a copy of the coordinates, that way we don't override some
        // coordinates but return false on others.
        int[] shiftCoordinates = new int[NUM_OF_COORDINATES];

        // verify that each coordinate can be translated down,
        // and update the coordinate accordingly
        for (int i = 0; i < NUM_OF_COORDINATES; i++) {

            // get value and column location
            int entryVal = coordinates[i];
            int entryRow = entryVal / boardWidth;

            // cannot shift right if any coordinate is in row (boardHeight - 1)
            // otherwise, add 1 to represent a shift right
            if (entryRow == (boardHeight - 1)) {
                return false;
            } else {
                shiftCoordinates[i] = entryVal + boardWidth;
            }
        }
        // at this point, all coordinates were able to be shifted down,
        // so override coordinates with the new shifted coordinates
        coordinates = shiftCoordinates;
        // and update the pivot point accordingly
        pivot = pivot + boardWidth;

        return true;
    }

// ***************************************************************************
//   * Methods for verifying piece movements.
// ***************************************************************************

    /**
     * Method takes the current state of the piece coordinates and checks for
     * any cases of a piece being 'out of bounds'.
     * This happens when a piece has coordinates in both the left and right
     * column.
     * @return
     */
    private boolean withinBoardBounds() {

        // check each coordinate to see if any entries occupy eiter the
        // left or right columns
        // along the way, if any entry is past the bottom row, we will
        // immediately return false
        boolean leftCol = false;
        boolean rightCol = false;
        for (int i = 0; i < NUM_OF_COORDINATES; i++) {
            // get entry information
            int entry = coordinates[i];
            int entryRow = entry / boardWidth;
            int entryCol = entry % boardWidth;
            if (entryRow >= boardHeight)
                return false;
            if (entryCol == 0)
                leftCol = true;
            if (entryCol == (boardWidth - 1))
                rightCol = true;
        }
        if (leftCol && rightCol)
            return false;
        else
            return true;

    }

// ***************************************************************************
//   * Miscellaneous methods for comparing and printing pieces.
// ***************************************************************************

    /**
     * Determines whether two pieces have the same coordinates, regardless of rotated state.
     * Must verify the ambient board sizes are the same first. Throw an exception if not.
     *
     * @param newPiece the TetrisPiece to be compared to the instance piece
     * @return true iff all coordinates in the instance and new piece are equal
     */
    public boolean equals(TetrisPiece newPiece) {

        if (boardHeight != newPiece.getBoardHeight() || boardWidth != newPiece.getBoardWidth())
            throw new IllegalArgumentException("Cannot compare two pieces with different ambient board sizes");

        int[] newCoordinates = newPiece.getCoordinates();

        for (int i = 0; i < NUM_OF_COORDINATES; i++) {
            if (coordinates[i] != newCoordinates[i]) {
                return false;
            }
        }

        return true;
    }

    /**
     * Used to display a TetrisPiece in the ambient 4x4 pieceGrid.
     * @return
     */
    public String toString() {

        String pieceString = "";

        for (int i = 0; i < boardHeight; i++) {
            for (int j = 0; j < boardWidth; j++) {

                int index = i * boardWidth + j;

                if (containsCoordinate(index)) {
                    pieceString += "0 ";
                } else {
                    pieceString += "- ";
                }
            }
            if (i < boardHeight - 1)
                pieceString += "\n";
        }

        return pieceString;
    }

    /**
     * Private helper method used in creating the toString.
     * Checks if a given coordinate (arbitrary) is equal to one of the piece coordinates.
     * Used in determining where to print pieces on an ambient board.
     *
     * @param coordinate the location to be checked against
     * @return true if the coordinate location is one of the piece coordinates
     */
    private boolean containsCoordinate(int coordinate) {

        for (int i = 0; i < NUM_OF_COORDINATES; i++) {
            if (coordinates[i] == coordinate)
                return true;
        }
        return false;

    }

    // used for print methods in main
    public String showCoordinates() {

        String coordString = "[ ";
        for (int i = 0; i < NUM_OF_COORDINATES - 1; i++) {
            coordString += coordinates[i] + ", ";
        }
        coordString += coordinates[NUM_OF_COORDINATES-1] + " ]";


        return coordString;

    }


// ***************************************************************************
//   * Test Client.
// ***************************************************************************



    /**
     * Test client for the TetrisPiece class.
     * @param args command line arguments
     */
    public static void main(String[] args) {

        int boardHeight = 6;
        int boardWidth = 5;

        char[] pieceLibrary = {'O','I','S','Z','L','J','T'};

        // Test for initialization of each piece type, and explicit rotations
        /*
        for (int i = 0; i < pieceLibrary.length; i++) {
            char type = pieceLibrary[i];
            System.out.println("Creating piece : '" + type + "'");
            TetrisPiece piece = new TetrisPiece(type, boardHeight, boardWidth);
            TetrisPiece rotaterPiece = new TetrisPiece(type, boardHeight, boardWidth);
            System.out.println(piece);

            System.out.println("Rotating piece : '" + type + "'");
            rotaterPiece.rotate();
            while (!rotaterPiece.equals(piece)) {
                System.out.println(rotaterPiece);
                rotaterPiece.rotate();
            }
        }
         */

        // Test for translations left / right / down and boundary detection
        /*
        for (int i = 0; i < pieceLibrary.length; i++) {

            // create new piece for set type
            char type = pieceLibrary[i];
            System.out.println("Creating piece : '" + type + "'");
            TetrisPiece piece = new TetrisPiece(type, boardHeight, boardWidth);
            TetrisPiece shifterPiece = new TetrisPiece(type, boardHeight, boardWidth);
            System.out.println(piece);

            // System.out.println("Shifting piece left : '" + type + "'");
            // System.out.println("Shifting piece right : '" + type + "'");
            System.out.println("Shifting piece down : '" + type + "'");


            while (shifterPiece.down()) {
                System.out.println(shifterPiece);
            }
        }
         */

        // Test each piece type for dynamic movements: left / right / down / flip
        // Allows for CLI manipulation of individual piece types
        Scanner scanner = new Scanner(System.in);
        char type = 'x';
        do {
            // get piece type from user
            System.out.print("PIECE TYPE ('x' to exit): ");
            type = scanner.nextLine().charAt(0);

            // validate user choice
            if (!validatePieceType(pieceLibrary, type)) {
                if (type == 'X' || type == 'x')
                    System.out.println("Exiting.");
                else
                    System.out.println("Not a valid piece type.");
            } else {
                // create new piece with default board height and width
                TetrisPiece activePiece = new TetrisPiece(type, Color.BLUE, boardHeight, boardWidth);
                System.out.println("Created new piece of type : " + type);
                System.out.println(activePiece);
                System.out.println(activePiece.showCoordinates());

                System.out.println("* * * * *  MENU  * * * * *");
                System.out.println("To move the piece, use the following menu options:");
                System.out.println("L/l : move left");
                System.out.println("R/r : move right");
                System.out.println("D/d : move down");
                System.out.println("F/f : flip (rotate)");
                System.out.println("X/x : return to piece type");


                System.out.println();

                // now we dynamically allow for user manipulation of piece
                // we use a key listener to take input
                char translationChoice = 'x';
                do {
                    System.out.print("MOVE PIECE : ");
                    translationChoice = scanner.nextLine().charAt(0);
                    switch (translationChoice) {
                        case 'L':
                        case 'l':
                            if (activePiece.left()) {
                                System.out.println(activePiece);
                                System.out.println(activePiece.showCoordinates());
                                System.out.println();
                            } else {
                                System.out.println("Boundary Detected");
                                System.out.println(activePiece);
                                System.out.println(activePiece.showCoordinates());
                                System.out.println();
                            }
                            break;
                        case 'R':
                        case 'r':
                            if (activePiece.right()) {
                                System.out.println(activePiece);
                                System.out.println(activePiece.showCoordinates());
                                System.out.println();
                            } else {
                                System.out.println("Boundary Detected");
                                System.out.println(activePiece);
                                System.out.println(activePiece.showCoordinates());
                                System.out.println();
                            }
                            break;
                        case 'D':
                        case 'd':
                            if (activePiece.down()) {
                                System.out.println(activePiece);
                                System.out.println(activePiece.showCoordinates());
                                System.out.println();
                            } else {
                                System.out.println("Boundary Detected");
                                System.out.println(activePiece);
                                System.out.println(activePiece.showCoordinates());
                                System.out.println();
                            }
                            break;
                        case 'F':
                        case 'f':
                            if (activePiece.rotate()) {
                                System.out.println(activePiece);
                                System.out.println(activePiece.showCoordinates());
                                System.out.println();
                            } else {
                                System.out.println("Boundary Detected");
                                System.out.println(activePiece);
                                System.out.println(activePiece.showCoordinates());
                                System.out.println();
                            }
                            break;
                        case 'X':
                        case 'x':
                            System.out.println("Exiting.");
                            break;
                        default:
                            System.out.println("Not a valid translation.");
                    }
                } while (translationChoice != 'X' && translationChoice != 'x');
            }

        } while (type != 'X' && type != 'x');


        /*
        // Test for consistency
        int counter = 0;

        TetrisPiece pieceS = new TetrisPiece('S',5,8);
        TetrisPiece pieceS2 = new TetrisPiece('S',5,7);

        TetrisPiece pieceZ = new TetrisPiece('Z',5,8);
        TetrisPiece pieceZ2 = new TetrisPiece('Z',5,8);

        TetrisPiece rotaterPiece = pieceZ2;
        while (counter < 10) {
            System.out.println("Iteration : " + counter);
            System.out.println(rotaterPiece);
            rotaterPiece.rotate();
            counter++;
        }

         */

        //Scanner scanner = new Scanner(System.in);
        //char input;

        //System.out.println("Welcome, enter 'q' to quit.");

        /*
        do {
            System.out.print("Enter piece type (I,S,Z,L,J,O,T) : ");
            input = scanner.nextLine().charAt(0);

            if (!validPieceType(input)) {
                System.out.println("Not a valid entry");
            } else {
                TetrisPiece piece = new TetrisPiece(input);
                TetrisPiece rotatedPiece = piece;
                do {
                    System.out.println(rotatedPiece);
                    rotatedPiece = rotatedPiece.rotate();
                } while (!rotatedPiece.equals(piece));
            }

        } while (input != 'q');

         */


    }

    private static boolean validatePieceType(char[] library, char choice) {

        for (int i = 0; i < library.length; i++) {
            if (choice == library[i])
                return true;
        }
        return false;

    }

}
