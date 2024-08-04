import java.awt.Color;

/**
 * Class used to define a single piece in Tetris.
 * <p>
 * A piece cannot be instantiated without an ambient board to hold it.
 * By default, a 4x4 grid is established to hold the piece.
 * When the Board class instantiates a piece, it must pass the board width and height
 * as parameters.
 * <p>
 * All pieces have a pivot location to define where the piece sits within the board.
 * The pivot point is used for piece movement and border logic.
 * Piece is conceptualized as a list of coordinates where the piece is defined + pivot.
 * <p>
 * Example: boardWidth = 4, boardHeight = 4:
 * 00 01 02 03
 * 04 05 06 07
 * 08 09 10 11
 * 12 13 14 15
 * <p>
 * Then the pivot point is 01 and the pieces are given as:
 * O = {1, 2, 5, 6}
 * I = {1, 5, 9, 13}
 * S = {1, 2, 4, 5}
 * Z = {0, 1, 5, 6}
 * L = {1, 5, 9, 10}
 * J = {1, 5, 8, 9}
 * T = {1, 4, 5, 6}
 *
 * @author adamm.hockman@gmail.com
 */
public class TetrisPiece {

    // declare static constants
    private static final int NUM_OF_COORDINATES = 4;
    private static final int MIN_BOARD_HEIGHT = 4;
    private static final int MIN_BOARD_WIDTH = 4;

    // piece specific information
    private final char pieceType;
    private int[] coordinates;
    private int pivot;
    private final Color pieceColor;

    // ambient board information
    private final int boardHeight;
    private final int boardWidth;


/* ***************************************************************************
 *    * Constructors
 ****************************************************************************/

    /**
     * This is the main constructor the other constructor variations call.
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
     * Constructor allows us to assign all instance variables upon creation.
     * Note that the Board should never call this method. The Board will always generate
     * a new piece at the top.
     * <p>
     * This constructor allows for a copy() method. This returns a new reference to
     * a TetrisPiece that has all the same field data (deep copied).
     * This will be necessary in testing for boundaries and collisions when moving
     * the piece.
     *
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
     * Private method used to assign the initial piece coordinates.
     * Assumes the initial pivot point has already been set.
     * Called by main constructor to simplify code.
     */
    private void setInitialPieceCoordinates() {

        // initialize the coordinates array
        this.coordinates = new int[NUM_OF_COORDINATES];

        // logic is explicitly defined
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


/* ***************************************************************************
 *    * Accessor Methods
 ****************************************************************************/

    /**
     * Accessor method used to extract coordinates.
     * @return piece coordinates
     */
    public int[] getCoordinates() {

        return coordinates;

    }

    /**
     * Accessor method used to extract ambient boardHeight.
     * @return the ambient board height containing the instance piece
     */
    public int getBoardHeight() {

        return boardHeight;

    }

    /**
     * Accessor method used to extract ambient boardWidth.
     * @return the ambient board width containing the instance piece
     */
    public int getBoardWidth() {

        return boardWidth;

    }

    /**
     * Accessor method used to extract piece type.
     * @return the char piece type
     */
    public char getPieceType() {
        return pieceType;
    }

    /**
     * Accessor method used to extract the piece color.
     * @return the piece color
     */
    public Color getPieceColor() { return pieceColor; }


/* ***************************************************************************
 *    * Copy Method
 ****************************************************************************/

    /**
     * Public method used to obtain a new reference (separate from the instance
     * piece) to a TetrisPiece containing the same fields as the instance
     * calling it.
     * @return pointer to a new TetrisPiece with the same fields
     */
    public TetrisPiece copy() {

        // deep copy the coordinates array before initializing
        int[] copyCoordinates = new int[NUM_OF_COORDINATES];
        System.arraycopy(coordinates, 0, copyCoordinates, 0, NUM_OF_COORDINATES);

        return new TetrisPiece(pieceType, pieceColor, boardHeight, boardWidth, copyCoordinates, pivot);

    }


/* ***************************************************************************
 *    * Rotation Methods
 ****************************************************************************/

    /**
     * Method used to rotate piece. Updates the coordinate list.
     * Logic is determined entirely by piece type and pivot location.
     *
     * @return true if rotation was successful, false otherwise
     */
    public boolean rotate() {

        // piece type 'O' can always be rotated
        if (pieceType == 'O')
            return true;

        // check that we have room to rotate along the edge
        if (pivotAlongEdge())
            return false;

        // first rotate the underlying grid for all pieces,
        // using a 4x4 grid for type 'I', and a 3x3 grid for the rest.
        if (pieceType == 'I')
            rotateNxN(4);
        else
            rotateNxN(3);

        // shift up the pieces to their pivot, if necessary.
        if (pieceType == 'I' || pieceType == 'S' || pieceType == 'Z')
            shiftToPivot();

        // at this point the rotation was successful
        return true;

    }

    /**
     * Private method used to verify whether a piece can be rotated.
     * Uses basic logic based on the pivot's row and column value.
     *
     * @return true if the pivot is near the edge and cannot be rotated
     */
    private boolean pivotAlongEdge() {

        // check for side edges
        int pivotCol = pivot % boardWidth;
        boolean sidesCheck;

        // The 'I' piece has a separate check for the right side, and bottom
        // since it needs an additional square to rotate
        if (pieceType == 'I')
            sidesCheck = (pivotCol == 0 || (boardWidth - 1 - pivotCol) < 2);
        else
            sidesCheck = (pivotCol == 0 || (boardWidth - 1 - pivotCol) < 1);

        // check for bottom edge
        int pivotRow = pivot / boardWidth;
        boolean bottomCheck;

        // if the pivot point is 1 row from the bottom row, the piece can not be rotated
        if (pieceType == 'I')
            bottomCheck = (boardHeight - 1 - pivotRow) < 3;
        else
            bottomCheck = (boardHeight - 1 - pivotRow) < 2;

        return (sidesCheck || bottomCheck);

    }

    /**
     * Private method used to help with rotating the 'I', 'S', and 'Z' piece.
     * In classic Tetris, these pieces have only 2 rotations, given certain translations.
     * <p>
     * We need to shift the piece up so that the top row of the piece is the same row as
     * the pivot point (for 'S' or 'Z'), or one below the pivot for 'I'.
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
     * Private method used to rotate the ambient NxN grid holding the piece.
     * The 'I' piece requires a 4x4 grid rotation, whereas the rest use a 3x3 grid.
     * <p>
     * First map the coordinates to a grid representation.
     * Then rotate the grid and map back to coordinates.
     * Mapping will depend on pivot point.
     *
     * @param N int size of ambient grid to rotate
     */
    private void rotateNxN(int N) {

        // initialize a new grid of 1s and 0s to represent the NxN grid coordinates
        int[][] grid = new int[N][N];

        // calculate row and col shift: pivot - 1 = rowShift * (boardWidth) + colShift
        int rowShift = (pivot - 1) / boardWidth;
        int colShift = (pivot - 1) % boardWidth;

        // map convert each coordinate
        for (int i = 0; i < NUM_OF_COORDINATES; i++) {

            // get entry value, row, and col
            int entry = coordinates[i];
            int entryRow = entry / boardWidth;
            int entryCol = entry % boardWidth;

            // apply row and col shift
            entryRow -= rowShift;
            entryCol -= colShift;

            // set location to 1 to indicate piece coordinate
            grid[entryRow][entryCol] = 1;

        }


        // rotate the grid counter-clockwise by 90 degrees
        // NOTE: we can apply a sort of reverse transpose to rotate the matrix
        int[][] rotatedGrid = new int[N][N];
        for (int col = 0; col < N; col++) {
            // apply the values (in reverse) to the corresponding col
            for (int row = 0; row < N; row++)
                rotatedGrid[row][col] = grid[col][N - row - 1];
        }
        grid = rotatedGrid;


        // map the rotated grid back to coordinates list
        int[] rotatedCoordinates = new int[NUM_OF_COORDINATES];

        // tracks which coordinates we map
        int index = 0;

        // look for '1' entries
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
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

    }


/* ***************************************************************************
 *    * Translation Methods: Left, Down, Right
 ****************************************************************************/

    /**
     * Method used to translate piece left. Updates the coordinate list.
     * Logic is determined entirely by piece type and pivot location.
     *
     * @return true if translation was successful, false otherwise
     */
    public boolean left() {

        // update a copy of the coordinates, then update all at once
        int[] shiftCoordinates = new int[NUM_OF_COORDINATES];

        // verify that each coordinate can be translated left,
        // and update the coordinate accordingly
        for (int i = 0; i < NUM_OF_COORDINATES; i++) {

            // get value and column location
            int entryVal = coordinates[i];
            int entryCol = entryVal % boardWidth;

            // cannot shift left if any coordinate is in column 0
            // otherwise, subtract 1 to represent a shift left
            if (entryCol == 0)
                return false;
            else
                shiftCoordinates[i] = entryVal - 1;

        }
        // at this point, all coordinates were shifted left,
        // so update coordinates with shifted coordinates
        coordinates = shiftCoordinates;

        // update the pivot point accordingly
        pivot = pivot - 1;

        return true;
    }

    /**
     * Method used to translate piece right. Updates the coordinate list.
     * Logic is determined entirely by piece type and pivot location.
     *
     * @return true if translation was successful, false otherwise
     */
    public boolean right() {

        // update a copy of the coordinates, then update all at once
        int[] shiftCoordinates = new int[NUM_OF_COORDINATES];

        // verify that each coordinate can be translated right,
        // and update the coordinate accordingly
        for (int i = 0; i < NUM_OF_COORDINATES; i++) {

            // get value and column location
            int entryVal = coordinates[i];
            int entryCol = entryVal % boardWidth;

            // cannot shift right if any coordinate is in column boardWidth - 1
            // otherwise, add 1 to represent a shift right
            if (entryCol == (boardWidth - 1))
                return false;
            else
                shiftCoordinates[i] = entryVal + 1;

        }
        // at this point, all coordinates were shifted right,
        // so update coordinates with shifted coordinates
        coordinates = shiftCoordinates;

        // update the pivot point accordingly
        pivot = pivot + 1;

        return true;
    }

    /**
     * Method used to translate piece down. Updates the coordinate list.
     * Logic is determined entirely by piece type and pivot location.
     *
     * @return true if translation was successful, false otherwise
     */
    public boolean down() {

        // update a copy of the coordinates, then update all at once
        int[] shiftCoordinates = new int[NUM_OF_COORDINATES];

        // verify that each coordinate can be translated down,
        // and update the coordinate accordingly
        for (int i = 0; i < NUM_OF_COORDINATES; i++) {

            // get value and column location
            int entryVal = coordinates[i];
            int entryRow = entryVal / boardWidth;

            // cannot shift right if any coordinate is in row (boardHeight - 1)
            // otherwise, add 1 to represent a shift right
            if (entryRow == (boardHeight - 1))
                return false;
            else
                shiftCoordinates[i] = entryVal + boardWidth;

        }
        // at this point, all coordinates were shifted down,
        // so update coordinates with shifted coordinates
        coordinates = shiftCoordinates;

        // update the pivot point accordingly
        pivot = pivot + boardWidth;

        return true;
    }


/* ***************************************************************************
 *    * Debug Methods
 ****************************************************************************/

    // DEBUG: Allows for checking whether two pieces have the same coordinates
    /*
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
     */

    // DEBUG: Allows for printing a piece in the terminal for testing
    /*
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
     */

    // DEBUG: Used in other debug methods like toString()
    /*
    private boolean containsCoordinate(int coordinate) {

        for (int i = 0; i < NUM_OF_COORDINATES; i++) {
            if (coordinates[i] == coordinate)
                return true;
        }
        return false;

    }
     */

    // DEBUG: Used for print methods in test client
    /*
    public String showCoordinates() {

        String coordString = "[ ";
        for (int i = 0; i < NUM_OF_COORDINATES - 1; i++) {
            coordString += coordinates[i] + ", ";
        }
        coordString += coordinates[NUM_OF_COORDINATES-1] + " ]";


        return coordString;

    }
     */


/* ***************************************************************************
 *    * Test Client
 ****************************************************************************/

    // DEBUG: Test client for debugging
    /*
    public static void main(String[] args) {

        int boardHeight = 6;
        int boardWidth = 5;

        char[] pieceLibrary = {'O','I','S','Z','L','J','T'};

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

    }
     */

    // DEBUG: Used in the test client
    /*
    private static boolean validatePieceType(char[] library, char choice) {

        for (int i = 0; i < library.length; i++) {
            if (choice == library[i])
                return true;
        }
        return false;

    }
     */

}
