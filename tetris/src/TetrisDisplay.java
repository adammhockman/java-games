import java.awt.Color;
import java.awt.Font;

/**
 * A Display is conceptualized as a background display (an image), as well as
 * various components sitting on top that are dynamically generated / refreshed.
 * * (1) Background Image - contains the background picture as well as various
 * *     cosmetic in-lays and menu icons.
 * * (2) Board - the TetrisBoard itself. Can be called to draw after setting
 * *     certain display/canvas parameters.
 * * (3) Next Piece - The next TetrisPiece to be generated. Shown within a small
 * *     ambient grid and a text label above.
 * * (4) Score - Dynamic text label showing the total lines cleared so far.
 *
 * @author adamm.hockman@gmail.com
 */
public class TetrisDisplay {

    // ----- Static Constants
    // canvas size
    private static final int CANVAS_WIDTH_PIXELS = 800;
    private static final int CANVAS_HEIGHT_PIXELS = 800;

    // background display
    private static final String BACKGROUND_PICTURE = "graphics/tetris_display_800px.png";
    private static final String BACKGROUND_PICTURE_FADE = "graphics/tetris_display_fade_800px.png";
    private static final String NEW_GAME_MENU_PICTURE = "graphics/new_game_icon.png";
    private static final String QUIT_GAME_MENU_PICTURE = "graphics/quit_game_icon.png";

    // display constants
    private static final double NEW_GAME_OPACITY = 0.80;
    private static final int NUM_PIECE_SQUARES = 4;

    // mini-grid display
    private static final int MINI_GRID_SIZE = 4;
    private static final double MINI_GRID_SQUARE_SIZE = 1.0 / 24.0;
    private static final double MINI_GRID_RADIUS = 0.001;
    private static final Color MINI_GRID_COLOR = Color.LIGHT_GRAY;

    // next piece display
    private static final Font NEXT_PIECE_FONT = new Font("Courier", Font.BOLD, 24);
    private static final Color NEXT_PIECE_FONT_COLOR = Color.WHITE;

    // score display
    private static final Font SCORE_FONT = new Font("Courier", Font.BOLD, 24);
    private static final Color SCORE_COLOR = Color.WHITE;

    // tetris board layout
    private static final double boardFrameXMin = 3.0 / 24.0;
    private static final double boardFrameXMax = 13.0 / 24.0;
    private static final double boardFrameYMin = 2.0 / 24.0;
    private static final double boardFrameYMax = 22.0 / 24.0;

    // next piece panel (6x8)
    private static final double pieceFrameBuffer = 0.25 / 24.0;
    private static final double pieceFrameXMin = 16.0 / 24.0 - pieceFrameBuffer;
    private static final double pieceFrameXMax = 22.0 / 24.0 - pieceFrameBuffer;
    private static final double pieceFrameYMin = 14.0 / 24.0 - pieceFrameBuffer;
    private static final double pieceFrameYMax = 22.0 / 24.0 - pieceFrameBuffer;

    // mini-grid (4x4) within next piece panel
    private static final double miniGridXMin = pieceFrameXMin + (1.0 / 24.0);
    private static final double miniGridXMax = pieceFrameXMax - (1.0 / 24.0);
    private static final double miniGridYMin = pieceFrameYMin + (1.0 / 24.0);
    private static final double miniGridYMax = pieceFrameYMax - (3.0 / 24.0);

    // score panel
    private static final double scoreFrameXMin = 18.0 / 24.0 - pieceFrameBuffer;
    private static final double scoreFrameXMax = 20.0 / 24.0 - pieceFrameBuffer;
    private static final double scoreFrameYMin = 7.0 / 24.0;
    private static final double scoreFrameYMax = 8.0 / 24.0;

    // menu icons
    private static final double newGameMenuX = 0.5;
    private static final double newGameMenuY = 0.5;
    private static final double newGameMenuSize = 1.0 / 2.0;

    private static final double quitGameMenuX = 1.0 / 10.0;
    private static final double quitGameMenuY = 1.0 / 10.0;
    private static final double quitGameMenuSize = 1.0 / 5.0;

    // ----- Display Specific Fields
    private TetrisBoard board;
    private final int boardHeight;
    private final int boardWidth;


/* ***************************************************************************
 *    * Constructors / Initialization and Get Methods
 ****************************************************************************/

    /**
     * Constructor uses the provided board height and width to create a new
     * game (and board), and initializes drawing parameters.
     *
     * @param boardHeight int board total rows
     * @param boardWidth int board total columns
     */
    public TetrisDisplay(int boardHeight, int boardWidth) {

        this.boardHeight = boardHeight;
        this.boardWidth = boardWidth;

        // create a new game
        createNewGame();

        // initialize StdDraw parameters
        StdDraw.enableDoubleBuffering();
        StdDraw.setCanvasSize(CANVAS_WIDTH_PIXELS, CANVAS_HEIGHT_PIXELS);
        StdDraw.setTitle("Tetris");
        StdDraw.setScale(0.0, 1.0);

    }

    /**
     * Private helper method used to create a new Tetris game.
     * We overwrite the Tetris board reference with a new board instance.
     * This method is called initially and then to play again after game over.
     */
    public void createNewGame() {

        // create a new TetrisBoard
        this.board = new TetrisBoard(boardHeight, boardWidth);

        // set board draw parameters
        board.setScale(boardFrameXMin, boardFrameYMin, boardFrameXMax, boardFrameYMax);

        // generate initial piece
        board.generatePiece();

    }

    /**
     * Used to create a new game. Creates a faded display of the background
     * and board. If starting from game over, the board will show the final
     * state and the score will be visible.
     */
    public synchronized void displayNewGame() {

        // clear display
        StdDraw.clear();

        // display faded background
        StdDraw.picture(0.5, 0.5, BACKGROUND_PICTURE_FADE);

        // draw everything with opacity set to global constant
        drawAdornments(NEW_GAME_OPACITY);
        board.draw(NEW_GAME_OPACITY);
        drawPieceFrame(NEW_GAME_OPACITY);
        drawScoreFrame(NEW_GAME_OPACITY);

        // show new game menu icon
        StdDraw.picture(newGameMenuX, newGameMenuY, NEW_GAME_MENU_PICTURE, newGameMenuSize, newGameMenuSize);

        // show quit game menu icon
        StdDraw.picture(quitGameMenuX, quitGameMenuY, QUIT_GAME_MENU_PICTURE, quitGameMenuSize, quitGameMenuSize);

        // display canvas
        StdDraw.show();

    }

    /**
     * Accessor method used to extract the current score.
     * @return int total lines cleared
     */
    public int getScore() {

        return board.score();

    }


/* ***************************************************************************
 *    * Draw / Display Methods
 ****************************************************************************/

    /**
     * Primary method used to display the canvas, the various frames and components,
     * and any changes as they happen. Method is called after game has already begun.
     */
    public synchronized void refresh() {

        // clear display
        StdDraw.clear();

        // display background
        StdDraw.picture(0.5, 0.5, BACKGROUND_PICTURE);

        // draw miscellaneous embellishments
        drawAdornments(1.0);

        // draw the board frame
        board.draw(1.0);

        // draw next piece frame
        drawPieceFrame(1.0);

        // draw current score
        drawScoreFrame(1.0);

        // display canvas
        StdDraw.show();

    }

    /**
     * Private method used to label and display the Tetris piece in queue.
     * @param opacity display parameter between 0.0 and 1.0
     */
    private synchronized void drawPieceFrame(double opacity) {

        // calculate display parameters
        String frameText = "Next Piece";
        int alpha = (int)(opacity * 255);
        Color fade = new Color(NEXT_PIECE_FONT_COLOR.getRed(), NEXT_PIECE_FONT_COLOR.getGreen(), NEXT_PIECE_FONT_COLOR.getBlue(), alpha);

        // calculate label center coordinates
        double frameTextX = 0.5 * (pieceFrameXMin + pieceFrameXMax);
        double frameTextY = pieceFrameYMin + (7.0 / 8.0) * (pieceFrameYMax - pieceFrameYMin);

        // draw label
        StdDraw.setFont(NEXT_PIECE_FONT);
        StdDraw.setPenColor(fade);
        StdDraw.text(frameTextX, frameTextY, frameText);

        // draw mini-grid
        drawMiniGrid(opacity);

        // draw active piece within a 4x4 grid
        drawNextPiece(opacity);

    }

    /**
     * Used to draw the ambient miniature grid holding the next piece.
     * @param opacity display parameter between 0.0 and 1.0
     */
    private synchronized void drawMiniGrid(double opacity) {

        // calculate draw parameters
        int alpha = (int)(opacity * 255);
        Color lg = MINI_GRID_COLOR;
        Color fade = new Color(lg.getRed(), lg.getGreen(), lg.getBlue(), alpha);

        // update StdDraw
        StdDraw.setPenColor(fade);
        StdDraw.setPenRadius(MINI_GRID_RADIUS);

        // draw horizontal lines
        for (int row = 0; row <= MINI_GRID_SIZE; row++) {
            double y = miniGridYMin + row * MINI_GRID_SQUARE_SIZE;
            StdDraw.line(miniGridXMin, y, miniGridXMax, y);
        }

        // draw vertical lines
        for (int col = 0; col <= MINI_GRID_SIZE; col++) {
            double x = miniGridXMin + col * MINI_GRID_SQUARE_SIZE;
            StdDraw.line(x, miniGridYMin, x, miniGridYMax);
        }

    }

    /**
     * Used to draw the next piece in queue to be generated.
     * Shown in a mini-grid separate from the main board display.
     * @param opacity display parameter between 0.0 and 1.0
     */
    private void drawNextPiece(double opacity) {

        // access the next piece from board
        TetrisPiece nextPiece = board.getNextPiece();
        if (nextPiece == null)
            return;

        // get piece info
        char nextPieceType = nextPiece.getPieceType();
        int[] pieceCoords = new int[NUM_PIECE_SQUARES];

        // explicitly update based on Tetris rules
        switch (nextPieceType) {
            case 'O':
                pieceCoords[0] = 5;
                pieceCoords[1] = 6;
                pieceCoords[2] = 9;
                pieceCoords[3] = 10;
                break;
            case 'I':
                pieceCoords[0] = 1;
                pieceCoords[1] = 5;
                pieceCoords[2] = 9;
                pieceCoords[3] = 13;
                break;
            case 'S':
                pieceCoords[0] = 6;
                pieceCoords[1] = 7;
                pieceCoords[2] = 9;
                pieceCoords[3] = 10;
                break;
            case 'Z':
                pieceCoords[0] = 4;
                pieceCoords[1] = 5;
                pieceCoords[2] = 9;
                pieceCoords[3] = 10;
                break;
            case 'L':
                pieceCoords[0] = 1;
                pieceCoords[1] = 5;
                pieceCoords[2] = 9;
                pieceCoords[3] = 10;
                break;
            case 'J':
                pieceCoords[0] = 2;
                pieceCoords[1] = 6;
                pieceCoords[2] = 9;
                pieceCoords[3] = 10;
                break;
            case 'T':
                pieceCoords[0] = 2;
                pieceCoords[1] = 5;
                pieceCoords[2] = 6;
                pieceCoords[3] = 10;
                break;
            default:
                throw new UnsupportedOperationException("Next piece type invalid: " + nextPieceType);
        }

        // print each coordinate as a (row, col) entry
        for (int pieceCoord : pieceCoords) {
            int row = pieceCoord / 4;
            int col = pieceCoord % 4;
            drawNextPieceSquare(row, col, opacity);
        }

    }

    /**
     * Private helper method used to display an individual square of the
     * next piece coordinates.
     * @param row int square row location
     * @param col int square column location
     * @param opacity display parameter between 0.0 and 1.0
     */
    private synchronized void drawNextPieceSquare(int row, int col, double opacity) {

        // calculate display parameters
        double squareHalfLength = 0.5 * MINI_GRID_SQUARE_SIZE;
        double x = miniGridXMin + squareHalfLength + col * MINI_GRID_SQUARE_SIZE;
        double y = miniGridYMax - squareHalfLength - row * MINI_GRID_SQUARE_SIZE;
        int alpha = (int)(opacity * 255);

        // calculate color and draw color interior
        Color sqColor = board.getNextPiece().getPieceColor();
        sqColor = new Color(sqColor.getRed(), sqColor.getGreen(), sqColor.getBlue(), alpha);
        StdDraw.setPenColor(sqColor);
        StdDraw.filledSquare(x, y, squareHalfLength);

        // lightly outline each square in black
        Color fadeBlack = new Color(0, 0, 0, (int)(opacity * 255));
        StdDraw.setPenColor(fadeBlack);
        StdDraw.setPenRadius(0.0005);
        StdDraw.square(x, y, squareHalfLength);

    }

    /**
     * Used to draw the current score.
     * @param opacity display parameter between 0.0 and 1.0
     */
    private synchronized void drawScoreFrame(double opacity) {

        // calculate display parameters
        int alpha = (int)(opacity * 255);
        Color sc = SCORE_COLOR;
        Color fade = new Color(sc.getRed(), sc.getGreen(), sc.getBlue(), alpha);
        double scoreX = 0.5 * (scoreFrameXMin + scoreFrameXMax);
        double scoreY = 0.5 * (scoreFrameYMin + scoreFrameYMax);

        // get score value
        int score = board.score();
        String textScore = Integer.toString(score);

        // update and display
        StdDraw.setFont(SCORE_FONT);
        StdDraw.setPenColor(fade);
        StdDraw.text(scoreX, scoreY, textScore);

    }

    /**
     * Used to draw any embellishments or adornments to the display.
     * Currently displays a layered border around the entire canvas.
     * Can be customized.
     * @param opacity display parameter between 0.0 and 1.0
     */
    private synchronized void drawAdornments(double opacity) {

        // update display parameters
        int alpha = (int)(opacity * 255);

        Color fadeBlack = new Color(0, 0, 0, alpha);
        Color fadeGreen = new Color(74, 103, 65, alpha);

        double blackWidth = 0.001;
        double greenWidth = 0.005;

        double gapDist = 0.005;

        // draw black solid border around entire canvas
        StdDraw.setPenColor(fadeBlack);
        StdDraw.setPenRadius(blackWidth);
        StdDraw.square(0.5, 0.5, 0.5);
        StdDraw.square(0.5, 0.5, 0.5 - gapDist);

        // draw forest green solid inside border around entire canvas
        StdDraw.setPenColor(fadeGreen);
        StdDraw.setPenRadius(greenWidth);
        StdDraw.square(0.5, 0.5, 0.5 - 0.5 * gapDist);

    }


/* ***************************************************************************
 *    * Accessor Methods to Move Active Piece
 * ***************************************************************************/

    /**
     * Public accessor method to access Board controls.
     * Attempts to generate a new piece.
     * @return boolean true to indicate new piece was generated
     */
    public boolean generatePiece() {

        return board.generatePiece();

    }

    /**
     * Public accessor method to access Board controls.
     * Attempts to move the active piece left.
     * @return boolean true to indicate piece was moved
     */
    public boolean left() {

        return board.left();

    }

    /**
     * Public accessor method to access Board controls.
     * Attempts to move the active piece right.
     * @return boolean true to indicate piece was moved
     */
    public boolean right() {

        return board.right();
    }

    /**
     * Public accessor method to access Board controls.
     * Attempts to move the active piece down.
     * @return boolean true to indicate piece was moved
     */
    public boolean down() {

        return board.down();

    }

    /**
     * Public accessor method to access Board controls.
     * Attempts to rotate the active piece.
     * @return boolean true to indicate piece was rotated
     */
    public boolean rotate() {

        return board.rotate();

    }

    /**
     * Public accessor method to access Board controls.
     * Attempts to drop the active piece by repeatedly dropping it..
     */
    public void dropPiece() {

        while (true) {
            if (!board.down())
                return;
        }

    }

/* ***************************************************************************
 *    * Menu Click Methods
 * ***************************************************************************/

    /**
     * Allows for threads to check for click locations when performing
     * UI actions.
     * Takes two coordinates (x,y) both between 0 and 1, and checks if
     * the location is in the new game menu icon window.
     *
     * @return true if (x,y) is in the new game icon window
     */
    public boolean clickNewGame(double x, double y) {

        double halfWindow = 0.5 * newGameMenuSize;

        boolean xWindow = newGameMenuX - halfWindow < x && x < newGameMenuX + halfWindow;
        boolean yWindow = newGameMenuY - halfWindow < y && y < newGameMenuY + halfWindow;

        return (xWindow && yWindow);

    }

    /**
     * Allows for threads to check for click locations when performing
     * UI actions.
     * Takes two coordinates (x,y) both between 0 and 1, and checks if
     * the location is in the quit game menu icon window.
     *
     * @return true if (x,y) is in the quit game icon window
     */
    public boolean clickQuitGame(double x, double y) {

        double halfWindow = 0.5 * quitGameMenuSize;

        boolean xWindow = quitGameMenuX - halfWindow < x && x < quitGameMenuX + halfWindow;
        boolean yWindow = quitGameMenuY - halfWindow < y && y < quitGameMenuY + halfWindow;

        return (xWindow && yWindow);

    }


/* ***************************************************************************
 *    * Test Client
 * ***************************************************************************/

    // DEBUG: Test client for debugging
    /*
    public static void main(String[] args) {

        int testH = 24;
        int testW = 12;

        TetrisDisplay td = new TetrisDisplay(testH, testW);
        td.refresh();

    }
     */

}
