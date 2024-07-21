import java.awt.*;
import java.util.Arrays;
import java.util.Random;


/**
 * A Board is conceptualized similarly to a piece in that we consider a board
 * of size width and height to be a list of coordinate entries:
 * * {0, 1, 2, ... , width * height - 1}
 *
 * However, We will maintain a boolean grid to maintain where each coordinate
 * entry is occupied. Essentially, a board is just a grid of boolean values,
 * where 'true' indicates the board is occupied.
 *
 * We map between coordinate entries, and coordinates themselves by noting
 * * * * entry = row * boardWidth + col
 *
 * A board will always have an active piece once the initial piece has been
 * generated. This piece receives all movement commands.
 *
 * Final resting position of a piece is recorded before generating a new piece.
 */
public class TetrisBoard {

    // default values represent an optimized view in TetrisDisplay
    private static final int DEFAULT_HEIGHT = 20;
    private static final int DEFAULT_WIDTH = 10;
    private static final int MIN_HEIGHT = 4;
    private static final int MIN_WIDTH = 4;
    private static final double DEFAULT_ASP_RATIO = ((double) DEFAULT_HEIGHT) / DEFAULT_WIDTH;

    // arrays to store the libraries of piece types and colors
    private static final char[] pieceTypeLibrary = {'O', 'I', 'S', 'Z', 'L', 'J', 'T'};

    // define color space for pieces
    private static final Color AZURE_WHITE = new Color(219, 233, 244);
    private static final Color MIDNIGHT = new Color(46, 60, 99);
    private static final Color NAVY = new Color(0, 0, 128);
    private static final Color BABY_BLUE = new Color(137, 207, 240);
    private static final Color SLATE = new Color(112, 128, 144);
    private static final Color FOREST = new Color(74, 103, 65);
    private static final Color LEAF = new Color(52, 194, 48);

    public static final Color[] colorLibrary = {AZURE_WHITE, MIDNIGHT, NAVY, BABY_BLUE, SLATE, FOREST, LEAF};

    // the number of rows are columns in the board, respectively
    private final int height;
    private final int width;

    // ----------------------------------------------------------------------------

    // a boolean array to store which grid squares are occupied and which are vacant
    private boolean[][] grid;
    // an array used to keep track of which color each grid square should be drawn as
    private Color[][] gridColors;

    // pointers to the current and next piece
    private TetrisPiece activePiece;
    private TetrisPiece nextPiece;
    private char nextPieceType;

    // keeps track of a user's score
    private int linesCleared;

    // fields used for drawing the board
    private double xMin;
    private double xMax;
    private double yMin;
    private double yMax;
    private double gridSquareSize;



// ***************************************************************************
//   * Constructors.
// ***************************************************************************

    /**
     * Default constructor.
     * Calls the main constructor with the default values as parameters.
     */
    public TetrisBoard() {

        this(DEFAULT_HEIGHT, DEFAULT_WIDTH);

    }

    /**
     * Constructor initializes a new TetrisBoard with provided width and height.
     *
     * Set initial volume to 0.
     * The activePiece is initially set to null. Once a piece is generated,
     * and can never again be null.
     *
     * @param height The height of the TetrisBoard to be initialized.
     * @param width The width of the TetrisBoard to be initialized
     */
    public TetrisBoard(int height, int width) {

        // check for minimum dimensions
        if (height < MIN_HEIGHT || width < MIN_WIDTH)
            throw new IllegalArgumentException("Cannot create a board using dimensions provided.");

        // assign the received parameters
        this.width = width;
        this.height = height;

        this.grid = new boolean[height][width];
        for (boolean[] arr : grid)
            Arrays.fill(arr, false);

        this.gridColors = new Color[height][width];
        for (Color[] arr : gridColors)
            Arrays.fill(arr, null);

        // initialize score to 0
        this.linesCleared = 0;

        // until we generate a piece, we keep it null for display purposes
        this.activePiece = null;

        // initialize the next piece to be generated
        queueNextPiece();

    }


    // public method to set the drawing area for the tetris board within an ambient canvas
    public void setScale(double xMin, double yMin, double xMax, double yMax) {

        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
        this.yMax = yMax;

        // assign the calculated parameters
        double aspRatio = ((double) height) / width;

        if (aspRatio < DEFAULT_ASP_RATIO) {
            gridSquareSize = (xMax - xMin) / width;
            this.yMax = aspRatio * (xMax - xMin) + yMin;
        } else {
            gridSquareSize = (yMax - yMin) / height;
            this.xMax = (1 / aspRatio) * (yMax - yMin) + xMin;
        }


    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int getLinesCleared() {
        return linesCleared;
    }

    public TetrisPiece getNextPiece() {
        return nextPiece;
    }

// ***************************************************************************
//   * Methods to draw the board to StdOut
// ***************************************************************************

    public void draw() {

        drawGameGrid();
        drawBoard();
        drawActivePiece();

    }

    public void draw(double a) {

        drawGameGrid();
        drawBoard(a);
        drawActivePiece(a);

    }


    /**
     * Private method used to draw the outer ambient frame that displays the game.
     * This is necessary when refreshing the display.
     */
    private synchronized void drawGameGrid() {

        // draw the "viewing area in bold black"
        /*
        StdDraw.setPenColor(Color.BLACK);
        StdDraw.setPenRadius(0.005);
        StdDraw.rectangle(0.5, 1.0, 0.5, 1.0);
         */

        // draw the ambient grid lines in light grey
        StdDraw.setPenColor(Color.LIGHT_GRAY);
        StdDraw.setPenRadius(0.001);

        // draw the horizontal grid lines
        for (int row = 1; row < height; row++) {
            double y = yMax - row * gridSquareSize;
            StdDraw.line(xMin, y, xMax, y);
        }

        // draw the vertical grid lines
        for (int col = 1; col < width; col++) {
            double x = xMin + col * gridSquareSize;
            StdDraw.line(x, yMin, x, yMax);
        }

    }

    /**
     * Private auxiliary method used to display the currently stored pieces
     * in the board.
     * Note this does not include the active piece, since its coordinates are
     * not stored in grid[][] until the next piece is generated.
     */
    private void drawBoard() {

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                if (grid[row][col]) {
                    drawPieceSquare(row, col);
                }
            }
        }
    }

    private void drawBoard(double a) {

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                if (grid[row][col]) {
                    drawPieceSquare(row, col, a);
                }
            }
        }
    }

    /**
     * Private auxiliary method used to display the current active piece.
     * Note the grid coordinate locations and the active piece locations
     * are tracked separately.
     */
    private void drawActivePiece() {

        if (activePiece == null)
            return;

        int[] pieceCoordinates = activePiece.getCoordinates();
        for (int i = 0; i < pieceCoordinates.length; i++) {
            int coordinate = pieceCoordinates[i];
            int cRow = coordinate / width;
            int cCol = coordinate % width;
            drawPieceSquare(cRow, cCol);
        }

    }

    private void drawActivePiece(double a) {

        if (activePiece == null)
            return;

        int[] pieceCoordinates = activePiece.getCoordinates();
        for (int i = 0; i < pieceCoordinates.length; i++) {
            int coordinate = pieceCoordinates[i];
            int cRow = coordinate / width;
            int cCol = coordinate % width;
            drawPieceSquare(cRow, cCol, a);
        }

    }

    /**
     * Private helper method used to draw a single piece square at a given row and
     * column position
     * Method uses board width to determine the size of a single square on a [0, 1]
     * horizontal scale.
     * @param row the row of the square to be drawn
     * @param col the column of the dquare to be drawn
     */
    private synchronized void drawPieceSquare(int row, int col) {

        // DEBUG
        // System.out.println("Printing square at row = " + row + " and col = " + col);

        // get square half-length
        double squareCenter = 0.5 * gridSquareSize;

        double x = xMin + squareCenter + col * gridSquareSize;
        // flip y since origin is at bottom left
        double y = yMax - squareCenter - row * gridSquareSize;

        Color squareColor;
        if (gridColors[row][col] == null)
            squareColor = activePiece.getPieceColor();
        else
            squareColor = gridColors[row][col];

        // fill interior of square with squareColor
        StdDraw.setPenColor(squareColor);
        StdDraw.filledSquare(x, y, squareCenter);

        // lightly outline each square in black
        StdDraw.setPenColor(Color.BLACK);
        StdDraw.setPenRadius(0.0005);
        StdDraw.square(x, y, squareCenter);

    }

    private synchronized void drawPieceSquare(int row, int col, double a) {

        // DEBUG
        // System.out.println("Printing square at row = " + row + " and col = " + col);

        // get square half-length
        double squareCenter = 0.5 * gridSquareSize;

        double x = xMin + squareCenter + col * gridSquareSize;
        // flip y since origin is at bottom left
        double y = yMax - squareCenter - row * gridSquareSize;

        Color squareColor;
        if (gridColors[row][col] == null)
            squareColor = activePiece.getPieceColor();
        else
            squareColor = gridColors[row][col];

        // fill interior of square with squareColor
        int alpha = (int) (a * 255);
        squareColor = new Color(squareColor.getRed(), squareColor.getGreen(), squareColor.getBlue(), alpha);
        StdDraw.setPenColor(squareColor);
        StdDraw.filledSquare(x, y, squareCenter);

        // lightly outline each square in black
        int bAlpha = (int) (0.60 * 255);
        Color aBlack = new Color(0, 0, 0, bAlpha);

        StdDraw.setPenColor(aBlack);
        StdDraw.setPenRadius(0.0005);
        StdDraw.square(x, y, squareCenter);


    }

// ***************************************************************************
//   * Boolean methods.
// ***************************************************************************

    /**
     * A method that allows a TetrisBoard to check if a given TetrisPiece
     * causes any "collisions" (i.e. locations where both the grid and the
     * are occupied)
     * For each coordinate value in the test piece's coordinates array,
     * we find the corresponding row and col in grid[][] using modular
     * arithmetic. If that location is occupied (true), then we have
     * a collision.
     * @param piece any TetrisPiece with same board dimensions
     * @return true if there are any collision locations
     */
    public boolean collisions(TetrisPiece piece) {

        if (piece == null)
            throw new IllegalArgumentException("Cannot supply null to collisions()");

        if (piece.getBoardHeight() != height || piece.getBoardWidth() != width)
            throw new UnsupportedOperationException("Piece and Board have different dimensions");

        int[] pieceCoordinates = piece.getCoordinates();
        for (int i = 0; i < pieceCoordinates.length; i++) {
            int coordinate = pieceCoordinates[i];
            int cRow = coordinate / width;
            int cCol = coordinate % width;
            if (grid[cRow][cCol])
                return true;
        }

        return false;
    }

// ***************************************************************************
//   * Piece creation.
// ***************************************************************************

    /**
     * Primary method for piece creation.
     * * (1) First we check for a current activePiece. If we already have one,
     * *     we first store the activePiece and update board coordinates.
     * * (2) It chooses a piece type from piece library at random (with
     * *     uniform probability).
     * * (3) Then instantiate the piece and assign it to activePiece reference.
     */
    public boolean generatePiece() {

        // we store the current active piece on the board
        if (activePiece != null)
            storeActivePiece();

        // next clear any full lines
        clearLines();

        // check that there is room for the next piece in queue
        if (collisions(nextPiece))
            return false;

        // update the active piece to the new piece
        activePiece = nextPiece;

        // queue the next piece
        queueNextPiece();

        return true;

    }

    private void queueNextPiece() {

        Random random = new Random();
        int pieceTypeIndex = random.nextInt(pieceTypeLibrary.length);
        char nextPieceType = pieceTypeLibrary[pieceTypeIndex];

        int pieceColorIndex = random.nextInt(colorLibrary.length);
        Color pieceColor = colorLibrary[pieceColorIndex];

        this.nextPiece = new TetrisPiece(nextPieceType, pieceColor, height, width);

    }

    /**
     * Private method used to store the active piece on the board.
     * This maps the coordinate entries of the active piece to their row and
     * column positions, then sets them to true in board grid.
     *
     * NOTE: This method assumes collisions have already been accounted for--
     * it will not check if the grid is occupied before setting it to occupied
     * Use the collision detection method to prevent this apriori.
     *
     * We also assume a piece has CORRECT coordinates between 0 and
     * width * height - 1. Otherwise, we will get an ArrayIndexOutOfBounds Error
     */
    private void storeActivePiece() {
        // verify the active piece is non-null
        if (activePiece == null)
            throw new UnsupportedOperationException("Cannot store active piece, currently null.");

        int[] activePieceCoordinates = activePiece.getCoordinates();
        for (int i = 0; i < activePieceCoordinates.length; i++) {
            int entryVal = activePieceCoordinates[i];
            int entryRow = entryVal / width;
            int entryCol = entryVal % width;
            grid[entryRow][entryCol] = true;
            gridColors[entryRow][entryCol] = activePiece.getPieceColor();
        }

    }

    /**
     * A private method used to check if there are any rows with every entry
     * occupied. When this happens, clear the entries, and shift ALL entries
     * above the deleted line down.
     */
    private void clearLines() {

        // check each row, beginning at the bottom, stopping at 2 from the top
        // note it is impossible to have full lines at the top 2 rows
        int row = height - 1;

        while (row > 1) {
            // if row is full, shift all entries down and check row again
            // increase score as well
            if (isRowFull(row)) {
                linesCleared++;
                shiftRowsDown(row);
            }
            else {
                row--;
            }

        }
    }

    /**
     * Private helper method called by the clearLines() method.
     * Checks whether a given row is full.
     *
     * @param row the row to check if full
     * @return boolean indicating true if the row is full, false otherwise
     */
    private boolean isRowFull(int row) {

        boolean fullRow = true;
        int col = 0;

        while (fullRow && col < width) {
            if (!grid[row][col])
                fullRow = false;
            col++;
        }

        return fullRow;
    }

    /**
     * Sets the entries in the given row to false, and shifts all entries
     * above down by one row.
     * @param row the row to start at, then descend
     */
    private void shiftRowsDown(int row) {

        // set all entries in row to false
        for (int i = 0; i < width; i++) {
            grid[row][i] = false;
            gridColors[row][i] = null;
        }
        // shift all other entries down (visually)
        // note that means shifting up in our representation
        for (int shiftRow = row; shiftRow > 1; shiftRow--) {
            for (int shiftCol = 0; shiftCol < width; shiftCol++) {
                // place entries from (shiftRow - 1) into (shiftRow)
                grid[shiftRow][shiftCol] = grid[shiftRow - 1][shiftCol];
                gridColors[shiftRow][shiftCol] = gridColors[shiftRow - 1][shiftCol];
            }
        }


    }

// ***************************************************************************
//   * Piece movement.
// ***************************************************************************

    /**
     * Public accessor method used to move the active piece to the left.
     */
    public boolean left() {

        if (activePiece == null)
            throw new UnsupportedOperationException("Cannot move a null piece.");

        return moveActivePiece('L');

    }

    /**
     * Public accessor method used to move the active piece down.
     */
    public boolean down() {

        if (activePiece == null)
            throw new UnsupportedOperationException("Cannot move a null piece.");

        return moveActivePiece('D');

    }

    /**
     * Public accessor method used to move the active piece to the right.
     */
    public boolean right() {

        if (activePiece == null)
            throw new UnsupportedOperationException("Cannot move a null piece.");

        return moveActivePiece('R');

    }

    /**
     * Public accessor method used to rotate the active piece.
     */
    public boolean rotate() {

        if (activePiece == null)
            throw new UnsupportedOperationException("Cannot move a null piece.");

        return moveActivePiece('F');

    }

    /**
     * This general purpose private method attempts to apply the appropriate
     * motion to the active piece, returning false if the movie causes any
     * collisions.
     *
     * Note that boundary crossing is checked by the piece itself.
     *
     * General Idea:
     * * (1) create a copy of the piece
     * * (2) apply the movement to the copy piece
     * * (3) check the copy piece for collisions
     * * (4) if no collisions, update activePiece
     *
     * @param movement char describing the type of movement to perform
     * @return true if successful, false if piece cannot move in such a way
     */
    private synchronized boolean moveActivePiece(char movement) {

        // 1 - create a copy of the piece;
        TetrisPiece trialPiece = activePiece.copy();

        // 2 - do the corresponding movement to the piece (if piece itself
        // returns true as well)
        boolean pieceMoved;
        switch (movement) {
            case 'L':
                pieceMoved = trialPiece.left();
                break;
            case 'D':
                pieceMoved = trialPiece.down();
                break;
            case 'R':
                pieceMoved = trialPiece.right();
                break;
            case 'F':
                pieceMoved = trialPiece.rotate();
                break;
            default:
                throw new IllegalArgumentException("Not a valid movement type: " + movement);
        }
        // we don't need to check for collisions if the piece didn't move
        if (!pieceMoved)
            return false;

        // 3 - now check for collisions with the current board and the trial piece
        if (collisions(trialPiece))
            return false;

        // if board.collisions is false - reassign the active piece
        activePiece = trialPiece;
        return true;

    }


// ***************************************************************************
//   * Methods for displaying the board.
// ***************************************************************************

    /**
     * Method used to print the board when using the CLI of the game.
     * @return string representation of the current board grid.
     */
    public String toString() {

        String boardString = "";

        // add header
        for (int i = 0; i < width + 1; i++) {
            boardString += "==";
        }
        boardString += "\n";

        for (int i = 0; i < height; i++) {
            boardString += "| ";
            for (int j = 0; j < width; j++) {
                if (grid[i][j] || containsPiece(i,j))
                    boardString += "0 ";
                else
                    boardString += "- ";
            }
            boardString += "|\n";
        }

        // add footer
        for (int i = 0; i < width + 1; i++) {
            boardString += "==";
        }

        return boardString;
    }

    /**
     * Private helper method used to check if a grid location (row, col)
     * is occupied by the activePiece.
     *
     * Since the activePiece does not store its coordinates on the board
     * until a new piece is generated, we need this additional check when
     * printing / displaying.
      */
    private boolean containsPiece(int row, int col) {

        if (activePiece == null)
            return false;

        // converts the (row,col) -> coordinate value
        int coordinateValue = row * width + col;
        int[] activePieceCoordinates = activePiece.getCoordinates();

        // check if any of the piece coordinates are equal to the one received
        for (int i = 0; i < activePieceCoordinates.length; i++) {
            if (activePieceCoordinates[i] == coordinateValue)
                return true;
        }
        return false;

    }


// ***************************************************************************
//   * Test Clients
// ***************************************************************************

    public static void main(String[] args) {

        int TEST_HEIGHT = 16;
        int TEST_WIDTH = 8;


        // CLI TEST CLIENT
        /*
        Scanner scanner = new Scanner(System.in);
        String input;
        char move = 'x';
        TetrisBoard board = new TetrisBoard(TEST_HEIGHT, TEST_WIDTH);

        System.out.println("*******************************");
        System.out.println("Welcome to Tetris!");
        System.out.println("Menu Options:");
        System.out.println("- - (G/g) : generate new piece");
        System.out.println("- - (L/l) : move piece left");
        System.out.println("- - (D/d) : move piece down");
        System.out.println("- - (R/r) : move piece right");
        System.out.println("- - (X/x) : exit game");
        System.out.println("*******************************");
        System.out.println();

        do {
            System.out.println(board);
            System.out.println();
            System.out.print("Option: ");
            input = scanner.nextLine();

            if (input.length() == 0)
                move = 'B';
            else
                move = input.charAt(0);

            switch (move) {
                case 'B':
                    break;
                case 'X':
                case 'x':
                    System.out.println("Exiting game.");
                    break;
                case 'G':
                case 'g':
                    System.out.println("Generating new piece.");
                    board.generatePiece();
                    break;
                case 'L':
                case 'l':
                    System.out.println("Move piece left.");
                    if (!board.left())
                        System.out.println("PIECE CAN NOT MOVE THAT WAY");
                    break;
                case 'D':
                case 'd':
                    System.out.println("Move piece down.");
                    if (!board.down())
                        System.out.println("PIECE CAN NOT MOVE THAT WAY");
                    break;
                case 'R':
                case 'r':
                    System.out.println("Move piece right.");
                    if (!board.right())
                        System.out.println("PIECE CAN NOT MOVE THAT WAY");
                    break;
                case 'F':
                case 'f':
                    System.out.println("Flip (rotate) piece.");
                    if (!board.rotate())
                        System.out.println("PIECE CAN NOT MOVE THAT WAY");
                    break;
                default:
                    System.out.println("Invalid choice.");
                    break;
            }
        } while (move != 'x');
         */

        TetrisBoard tb = new TetrisBoard(TEST_HEIGHT, TEST_WIDTH);

        tb.setScale(0,0.4,0,0.8);

        System.out.println("Board Display Parameters:");
        System.out.println("xMin: " + tb.xMin);
        System.out.println("xMax: " + tb.xMax);
        System.out.println("yMin: " + tb.yMin);
        System.out.println("yMax: " + tb.yMax);
        System.out.println("Grid Square Size: " + tb.gridSquareSize);


    }
}
