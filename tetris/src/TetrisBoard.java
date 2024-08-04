import java.awt.Color;
import java.util.Arrays;
import java.util.Random;


/**
 * A Board is conceptualized as a boolean grid where true indicates the
 * location is occupied. We also have an active piece, stored separately.
 * <p>
 * We map between coordinate entries, and coordinates themselves by noting
 * ** entry = row * boardWidth + col
 * <p>
 * A board will always have an active piece once the initial piece has been
 * generated. This piece receives all movement commands.
 * Final resting position of a piece is recorded before generating a new piece.
 *
 * @author adamm.hockman@gmail.com
 */
public class TetrisBoard {

    // ----- Static Fields
    // default values represent an optimized view in TetrisDisplay
    private static final int DEFAULT_HEIGHT = 20;
    private static final int DEFAULT_WIDTH = 10;
    private static final int MIN_HEIGHT = 6;
    private static final int MIN_WIDTH = 6;
    private static final double DEFAULT_ASP_RATIO = ((double) DEFAULT_HEIGHT) / DEFAULT_WIDTH;
    private static final double SQUARE_OUTLINE_RADIUS = 0.0005;
    private static final Color SQUARE_OUTLINE_COLOR = Color.BLACK;
    private static final Color GRID_LINES_COLOR = Color.LIGHT_GRAY;

    // define color space for pieces
    private static final Color AZURE_WHITE = new Color(219, 233, 244);
    private static final Color MIDNIGHT = new Color(46, 60, 99);
    private static final Color NAVY = new Color(0, 0, 128);
    private static final Color BABY_BLUE = new Color(137, 207, 240);
    private static final Color SLATE = new Color(112, 128, 144);
    private static final Color FOREST = new Color(74, 103, 65);
    private static final Color LEAF = new Color(52, 194, 48);

    // declare the libraries of piece types and colors
    private static final char[] PIECE_TYPE_LIBRARY = {'O', 'I', 'S', 'Z', 'L', 'J', 'T'};
    private static final Color[] COLOR_LIBRARY = {AZURE_WHITE, MIDNIGHT, NAVY, BABY_BLUE, SLATE, FOREST, LEAF};


    // ----- Board Specific Fields
    // number of rows and cols, respectively
    private final int height;
    private final int width;

    // boolean array to indicate which squares are occupied and which are vacant
    private final boolean[][] grid;
    // Color array to indicate the color of each grid square
    private final Color[][] gridColors;


    // ----- Dynamic Fields
    // pointers to the current and next piece
    private TetrisPiece activePiece;
    private TetrisPiece nextPiece;

    // user score
    private int linesCleared;

    // drawing parameters
    private double xMin;
    private double xMax;
    private double yMin;
    private double yMax;
    private double gridSquareSize;


/* ***************************************************************************
 *    * Constructors and Initialization
 ****************************************************************************/

    /**
     * Constructor initializes a new TetrisBoard with provided width and height.
     * <p>
     * The activePiece is initially set to null. Once a piece is generated,
     * it can never again be null.
     *
     * @param height int number of rows
     * @param width int number of cols
     */
    public TetrisBoard(int height, int width) {

        // check for minimum dimensions
        if (height < MIN_HEIGHT || width < MIN_WIDTH)
            throw new IllegalArgumentException("Cannot create a board using dimensions provided.");

        // assign the received parameters
        this.width = width;
        this.height = height;

        // initialize remaining fields
        this.grid = new boolean[height][width];
        for (boolean[] arr : grid)
            Arrays.fill(arr, false);

        this.gridColors = new Color[height][width];
        for (Color[] arr : gridColors)
            Arrays.fill(arr, null);

        // initialize score to 0
        this.linesCleared = 0;

        // null until we generate a piece
        this.activePiece = null;

        // initialize the next piece to be generated
        queueNextPiece();

    }

    /**
     * Public method used to set draw parameters. Values represent the location
     * of the board within the ambient game display visual console.
     *
     * @param xMin double location of left side of board
     * @param yMin double location of bottom of board
     * @param xMax double location of right side of board
     * @param yMax double location of top of board
     */
    public void setScale(double xMin, double yMin, double xMax, double yMax) {

        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
        this.yMax = yMax;

        // calculate grid size based on aspect ratio
        double aspRatio = ((double) height) / width;

        if (aspRatio < DEFAULT_ASP_RATIO) {
            gridSquareSize = (xMax - xMin) / width;
            this.yMax = aspRatio * (xMax - xMin) + yMin;
        } else {
            gridSquareSize = (yMax - yMin) / height;
            this.xMax = (1 / aspRatio) * (yMax - yMin) + xMin;
        }

    }


/* ***************************************************************************
 *    * Accessor Methods
 ****************************************************************************/

    /**
     * Accessor method used to get current score.
     * @return int total lines cleared
     */
    public int score() {

        return linesCleared;

    }

    /**
     * Accessor method used to get the next piece in queue. Used for drawing.
     * @return TetrisPiece next piece to become active piece
     */
    public TetrisPiece getNextPiece() {

        return nextPiece;

    }


/* ***************************************************************************
 *    * Draw Methods
 ****************************************************************************/

    /**
     * Draws the current board configuration to Standard Draw.
     * Covers the display area defined by the setScale() initialization.
     */
    public void draw() {

        // draw board grid lines
        drawGameGrid();
        // draw occupied grid squares
        drawBoard();
        // draw the current active piece
        drawActivePiece();

    }

    /**
     * Private method used to draw faint grid lines that delineate grid squares.
     * Note this draws the "inner" grid lines only.
     */
    private synchronized void drawGameGrid() {

        // draw the ambient grid lines in light grey
        StdDraw.setPenColor(GRID_LINES_COLOR);
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

    /**
     * Private auxiliary method used to display the current active piece.
     */
    private void drawActivePiece() {

        if (activePiece == null)
            return;

        int[] pieceCoordinates = activePiece.getCoordinates();
        for (int coordinate : pieceCoordinates) {
            int cRow = coordinate / width;
            int cCol = coordinate % width;
            drawPieceSquare(cRow, cCol);
        }

    }

    /**
     * Private helper method used to draw a single piece square at a given row and
     * column position
     * Method uses class variables to determine position, size, and appearance.
     * @param row int row of the square to be drawn
     * @param col int column of the square to be drawn
     */
    private synchronized void drawPieceSquare(int row, int col) {

        // get square half-length / center offset
        double squareCenter = 0.5 * gridSquareSize;

        // get center x and y (flip y since origin is at bottom left)
        double x = xMin + squareCenter + col * gridSquareSize;
        double y = yMax - squareCenter - row * gridSquareSize;

        // get square color
        Color squareColor;
        if (gridColors[row][col] == null)
            squareColor = activePiece.getPieceColor();
        else
            squareColor = gridColors[row][col];

        // fill interior of square with squareColor
        StdDraw.setPenColor(squareColor);
        StdDraw.filledSquare(x, y, squareCenter);

        // lightly outline each square in black
        StdDraw.setPenColor(SQUARE_OUTLINE_COLOR);
        StdDraw.setPenRadius(SQUARE_OUTLINE_RADIUS);
        StdDraw.square(x, y, squareCenter);

    }


/* ***************************************************************************
 *    * Animate Methods
 ****************************************************************************/

    // ----------- JAVADOC ----------- //

    public void draw(double a) {

        drawGameGrid();
        drawBoard(a);
        drawActivePiece(a);

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

    // ----------- JAVADOC ----------- //


/* ***************************************************************************
 *    * Boolean Methods
 ****************************************************************************/

    /**
     * Public method checks whether an arbitrary TetrisPiece will collide with
     * any of the stored piece coordinates.
     *
     * @param piece TetrisPiece with same board dimensions
     * @return true if there are any collision locations
     */
    public boolean collisions(TetrisPiece piece) {

        if (piece == null)
            throw new IllegalArgumentException("Cannot supply null to collisions()");

        if (piece.getBoardHeight() != height || piece.getBoardWidth() != width)
            throw new UnsupportedOperationException("Piece and Board have different dimensions");

        int[] pieceCoordinates = piece.getCoordinates();
        for (int coordinate : pieceCoordinates) {
            int cRow = coordinate / width;
            int cCol = coordinate % width;
            if (grid[cRow][cCol])
                return true;
        }

        return false;
    }

    /**
     * Private helper method called by the clearLines() method.
     * Checks whether a given row is full (i.e. no unoccupied squares).
     *
     * @param row int row to check if full
     * @return boolean true if the row is full, false otherwise
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


/* ***************************************************************************
 *    * Piece Creation
 ****************************************************************************/

    /**
     * Primary method for piece creation.
     * * (1) Store active piece.
     * * (2) Clear any full lines.
     * * (3) Load next piece.
     * * (4) Queue next piece.
     *
     * @return boolean true if new piece was generated, false if failed
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

    /**
     * Private method used to randomly generate a new piece and update the
     * piece in queue.
     */
    private void queueNextPiece() {

        Random random = new Random();

        // get random piece type
        int pieceTypeIndex = random.nextInt(PIECE_TYPE_LIBRARY.length);
        char nextPieceType = PIECE_TYPE_LIBRARY[pieceTypeIndex];

        // get random piece color
        int pieceColorIndex = random.nextInt(COLOR_LIBRARY.length);
        Color pieceColor = COLOR_LIBRARY[pieceColorIndex];

        // update reference
        this.nextPiece = new TetrisPiece(nextPieceType, pieceColor, height, width);

    }

    /**
     * Private method used to store the active piece on the board.
     * This maps the coordinate entries of the active piece to their row and
     * column positions, then sets them to true in board grid.
     * <p>
     * NOTE: This method assumes collisions have already been accounted for--
     * it will not check if the grid is occupied before setting it to occupied
     * Use the collision detection method to prevent this.
     * <p>
     * We also assume a piece has valid coordinates between 0 and
     * width * height - 1. Otherwise, we will get an ArrayIndexOutOfBounds Error
     */
    private void storeActivePiece() {

        // verify the active piece is non-null
        if (activePiece == null)
            throw new UnsupportedOperationException("Cannot store active piece, currently null.");

        // map coordinates to grid locations and update
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
     * occupied. When this happens, clear the row, and shift all entries
     * above the deleted row down.
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
     * Sets the entries in the given row to false, and shifts all entries
     * above down by one row.
     * @param row int row to start at, then descend
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


/* ***************************************************************************
 *    * Piece Movement
 ****************************************************************************/

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
     * motion to the active piece, returning false if the move causes any
     * collisions.
     * Note that boundary crossing is checked by the piece itself.
     * <p>
     * General Idea:
     * * (1) Create a copy of the piece
     * * (2) Apply the movement to the copy piece
     * * (3) Check the copy piece for collisions
     * * (4) If no collisions, update activePiece
     *
     * @param movement char describing the type of movement to perform
     * @return true if successful, false if piece cannot move in such a way
     */
    private synchronized boolean moveActivePiece(char movement) {

        // 1 - create a copy of the piece;
        TetrisPiece trialPiece = activePiece.copy();

        // 2 - do the corresponding movement to the piece (if piece itself
        // returns true as well)
        boolean pieceMoved = switch (movement) {
            case 'L' -> trialPiece.left();
            case 'D' -> trialPiece.down();
            case 'R' -> trialPiece.right();
            case 'F' -> trialPiece.rotate();
            default -> throw new IllegalArgumentException("Not a valid movement type: " + movement);
        };
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


/* ***************************************************************************
 *    * Debug Methods
 ****************************************************************************/

    // DEBUG: Used to print board in terminal
    /*
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
     */

    // DEBUG: Used to print in toString
    /*
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
     */


/* ***************************************************************************
 *    * Test Clients
 ****************************************************************************/

    // DEBUG: test client
    /*
    public static void main(String[] args) {

        int TEST_HEIGHT = 16;
        int TEST_WIDTH = 8;

        // CLI TEST CLIENT
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
    }
     */

}
