import java.awt.Color;

/**
 * Class used to represent the game console / display.
 * Acts as a high-level controller of the underlying number puzzle board.
 * Also controls the animations when swapping the tiles.
 */
public class NumberDisplay {

    // the main puzzle board
    private Board board;

    // animating the tile swap mechanism
    private static final int SOLUTION_SWAP_TIME = 500;
    private static final int ANIMATED_SWAP_TIME = 150;
    private static final int ANIMATED_FPS = 40;

    // background images
    private static final String BACKGROUND_IMAGE = "graphics/numberslide_display_800px.png";
    private static final String BACKGROUND_NEW_GAME_IMAGE = "graphics/numberslide_display_newgame.png";

    // canvas size
    private static final int CANVAS_WIDTH_PIXELS = 704;
    private static final int CANVAS_HEIGHT_PIXELS = 800;
    private static final double CANVAS_XSCALE = 22.0 / 25.0;
    private static final double CANVAS_YSCALE = 1.0;

    // board panel layout
    private static final double hBuffer = -0.0025;
    private static final double vBuffer = 0.005;
    public static final double boardXMin = 2.0 / 25.0 + hBuffer;
    public static final double boardXMax = 20.0 / 25.0 + hBuffer;
    public static final double boardYMin = 5.0 / 25.0 + vBuffer;
    public static final double boardYMax = 23.0 / 25.0 + vBuffer;

    // solver panel layout
    private static final double solverXMin = 2.0 / 25.0;
    private static final double solverXMax = 4.8 / 25.0;
    private static final double solverYMin = 1.4 / 25.0;
    private static final double solverYMax = 3.4 / 25.0;

    // new game panel layout
    private static final double newGameXMin = 16.8 / 25.0;
    private static final double newGameXMax = 20.0 / 25.0;
    private static final double newGameYMin = 1.0 / 25.0;
    private static final double newGameYMax = 3.5 / 25.0;

    // grid selection layout
    private static final double gridSelectYMin = 5.5 / 25.0;
    private static final double gridSelectYMax = 9.8 / 25.0;

    // used for selecting a new game grid size
    private static final double grid3x3XMin = 3.0 / 25.0;
    private static final double grid3x3XMax = 7.0 / 25.0;
    private static final double grid4x4XMin = 9.0 / 25.0;
    private static final double grid4x4XMax = 13.2 / 25.0;
    private static final double grid5x5XMin = 14.8 / 25.0;
    private static final double grid5x5XMax = 19.3 / 25.0;


/* **************************************************************************
 *            * Constructor and Accessor Methods *
 ***************************************************************************/

    /**
     * Simple constructor. Assigns the active board to null.
     * Initializes the board later when the user chooses a board size.
     * Sets up the canvas and StdDraw with initial settings.
     */
    public NumberDisplay() {

        this.board = null;

        // set up canvas and StdDraw parameters
        StdDraw.enableDoubleBuffering();
        StdDraw.setCanvasSize(CANVAS_WIDTH_PIXELS, CANVAS_HEIGHT_PIXELS);
        StdDraw.setXscale(0.0, CANVAS_XSCALE);
        StdDraw.setYscale(0.0, CANVAS_YSCALE);
        StdDraw.setTitle("Number Puzzle");

    }

    /**
     * Accessor method used to obtain a reference to the Tile at the row and
     * column provided.
     *
     * @param row int row of Tile to return
     * @param col int column of Tile to return
     * @return Tile located at (row,col)
     */
    public Tile getTile(int row, int col) {

        return board.getTile(row, col);

    }


/* **************************************************************************
 *            * Draw Methods *
 ***************************************************************************/

    /**
     * Primary method used for updating the display.
     * Draws the board statically, with colors in their standard scheme.
     */
    public void refresh() {

        // draw standard components
        StdDraw.clear();
        drawDisplayBorder();
        drawBackground();
        // use the board to draw itself
        board.draw();
        StdDraw.show();

    }

    /**
     * Alternative method used for updating the display.
     * Draws the board statically, with colors in their inverted scheme.
     */
    public void refreshInverted() {

        // draw standard components
        StdDraw.clear();
        drawDisplayBorder();
        drawBackground();
        // use the board to draw itself after inverting
        board.invert();
        board.draw();
        StdDraw.show();
        board.invert();

    }

    /**
     * Main method used when animating the tile swap mechanism.
     * Determines a wait time based on static display constants,
     * and continuously displays in those intervals.
     * Think of t as representing the proportion of the swap progression
     * t = 0 -> board before the swap
     * t = 1 -> board after the swap
     */
    public void refreshAnimated() {

        int frames = (int)((ANIMATED_SWAP_TIME / 1000.0) * ANIMATED_FPS);
        int waitTime = 1000 / ANIMATED_FPS;

        for (int i = 0; i <= frames; i++) {
            // draw standard components
            StdDraw.clear();
            drawDisplayBorder();
            drawBackground();
            // calculate proportion of swap completed
            double t = (double) i / frames;
            // use the board to draw itself after inverting
            board.drawAnimated(t);
            StdDraw.show();
            sleep(waitTime);

        }

    }

    /**
     * Private method used to simplify drawing the background image in each frame.
     * Simply loads the image defined as a static constant at the center of the canvas.
     */
    private void drawBackground() {

        double x = 0.5 * CANVAS_XSCALE;
        double y = 0.5 * CANVAS_YSCALE;

        StdDraw.picture(x, y, BACKGROUND_IMAGE);

    }

    /**
     * Private method used to simplify drawing the display border in each frame.
     * Border is a simply black outline.
     */
    private void drawDisplayBorder() {

        // draw boundary frame
        StdDraw.setPenColor(Color.BLACK);
        StdDraw.setPenRadius(0.003);

        double xCenter = 0.5 * CANVAS_XSCALE;
        double yCenter = 0.5 * CANVAS_YSCALE;

        StdDraw.rectangle(xCenter, yCenter, xCenter, yCenter);

    }


/* **************************************************************************
 *            * Game Dynamics Methods *
 ***************************************************************************/

    /**
     * Displays the new game screen and allows the user to select the grid
     * size of the new game.
     * Once a grid size is selected, the board is initialized and scale is set
     * before returning.
     */
    public void startNewGame() {

        StdDraw.clear();
        double xCenter = 0.5 * CANVAS_XSCALE;
        double yCenter = 0.5 * CANVAS_YSCALE;
        StdDraw.picture(xCenter, yCenter, BACKGROUND_NEW_GAME_IMAGE);

        // debugDrawGridSelectBoxes();

        StdDraw.show();

        while (true) {

            if (StdDraw.isMousePressed()) {

                double x = StdDraw.mouseX();
                double y = StdDraw.mouseY();

                if (click3x3(x, y)) {
                    System.out.println("clicked 3x3");
                    this.board = Board.createBoard(3);
                    System.out.println("3x3 board generated");
                    this.board.setScale(boardXMin, boardYMin, boardXMax, boardYMax);
                    System.out.println("scale set");
                    return;
                }
                if (click4x4(x, y)) {
                    System.out.println("clicked 4x4");
                    this.board = Board.createBoard(4);
                    System.out.println("4x4 board generated");
                    this.board.setScale(boardXMin, boardYMin, boardXMax, boardYMax);
                    System.out.println("scale set");
                    return;
                }
                if (click5x5(x, y)) {
                    System.out.println("clicked 5x5");
                    this.board = Board.createBoard(5);
                    System.out.println("5x5 board generated");
                    this.board.setScale(boardXMin, boardYMin, boardXMax, boardYMax);
                    System.out.println("scale set");
                    return;
                }
            }
            sleep(100);
        }

    }

    /**
     * Runs the solver visualization when a user selects the solve badge icon.
     * Iterates through each Board in the solution given by Solver, updates
     * current board, and displays step to user.
     * Stops once goal is reached, and gameplay is over.
     */
    public void runSolver() {

        board.cacheDistance();
        Solver solver = new Solver(board);

        if (solver.unsolvable()) {
            System.out.println("Board is unsolvable.");
            return;
        }

        for (Board b : solver.solution()) {
            this.board = b;
            b.setScale(boardXMin, boardYMin, boardXMax, boardYMax);
            refresh();
            StdDraw.pause(SOLUTION_SWAP_TIME);
        }

    }

    /**
     * Used to determine whether the board's current state is the target state.
     * Calls the board matching method.
     * Note: When isGoal() is true, the game is over.
     *
     * @return true if all tiles are in their correct location
     */
    public boolean isGoal() {

        return board.isGoal();

    }

    /**
     * Accessor method that allows for swapping a tile.
     * Calls the board matching method.
     *
     * @param row int row of tile to swap
     * @param col int column of tile to swap
     * @return true if swap was successful
     */
    public boolean zeroSwapTile(int row, int col) {

        return board.zeroSwapTile(row, col);

    }

    /**
     * Organizes the Thread sleep() method in one location to reduce code re-use
     * when catching Exceptions / Interruptions
     *
     * @param millis long number of milliseconds to sleep
     */
    private void sleep(long millis) {

        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            System.out.println("Sleep interrupted.");
        }

    }


/* **************************************************************************
 *            * Click Methods *
 ***************************************************************************/

    /**
     * Method used to check whether a user has clicked on the solution badge.
     *
     * @param x double x coordinate of click location
     * @param y double y coordinate of click location
     * @return true if (x,y) is inside the solution badge area
     */
    public boolean clickSolutionBadge(double x, double y) {

        boolean xBounded = solverXMin < x && x < solverXMax;
        boolean yBounded = solverYMin < y && y < solverYMax;

        return (xBounded && yBounded);
    }

    /**
     * Method used to check whether a user has clicked on the new game badge.
     *
     * @param x double x coordinate of click location
     * @param y double y coordinate of click location
     * @return true if (x,y) is inside the new game badge area
     */
    public boolean clickNewGameBadge(double x, double y) {

        boolean xBounded = newGameXMin < x && x < newGameXMax;
        boolean yBounded = newGameYMin < y && y < newGameYMax;

        return (xBounded && yBounded);

    }

    /**
     * Method used to check whether a user has clicked inside puzzle board.
     *
     * @param x double x coordinate of click location
     * @param y double y coordinate of click location
     * @return true if (x,y) is inside the total board area
     */
    public boolean clickInsideBoard(double x, double y) {

        boolean xBounded = boardXMin < x && x < boardXMax;
        boolean yBounded = boardYMin < y && y < boardYMax;

        return (xBounded && yBounded);

    }

    /**
     * Private helper method called when the user clicks inside the board.
     * Finds the nearest row in the board given the click location.
     *
     * @param y double click y-coordinate
     * @return int row closest or containing the y-coordinate
     */
    public int clickRow(double y) {

        double bracket = (boardYMax - boardYMin) / ((double) board.dimension());

        for (int row = 0; row < board.dimension(); row++) {
            double rowCutoff = boardYMax - (row + 1) * bracket;
            if (y > rowCutoff)
                return row;
        }

        return -1;

    }

    /**
     * Private helper method called when the user clicks inside the board.
     * Finds the nearest column in the board given the click location.
     *
     * @param x double click x-coordinate
     * @return int column closest or containing the x-coordinate
     */
    public int clickCol(double x) {

        double bracket = (boardXMax - boardXMin) / ((double) board.dimension());

        for (int col = 0; col < board.dimension(); col++) {
            double colCutoff = boardXMin + (col + 1) * bracket;
            if (x < colCutoff)
                return col;
        }

        return -1;

    }

    /**
     * Private helper method used to determine the grid size selected by the user
     * during new game creation phase. Screens for the 3x3 case.
     *
     * @param x double x-coordinate of the click location
     * @param y double y-coordinate of the click location
     * @return true if the user selected the 3x3 icon
     */
    private boolean click3x3(double x, double y) {

        boolean xBounded = grid3x3XMin < x && x < grid3x3XMax;
        boolean yBounded = gridSelectYMin < y && y < gridSelectYMax;

        return (xBounded && yBounded);

    }

    /**
     * Private helper method used to determine the grid size selected by the user
     * during new game creation phase. Screens for the 4x4 case.
     *
     * @param x double x-coordinate of the click location
     * @param y double y-coordinate of the click location
     * @return true if the user selected the 4x4 icon
     */
    private boolean click4x4(double x, double y) {

        boolean xBounded = grid4x4XMin < x && x < grid4x4XMax;
        boolean yBounded = gridSelectYMin < y && y < gridSelectYMax;

        return (xBounded && yBounded);
    }

    /**
     * Private helper method used to determine the grid size selected by the user
     * during new game creation phase. Screens for the 5x5 case.
     *
     * @param x double x-coordinate of the click location
     * @param y double y-coordinate of the click location
     * @return true if the user selected the 5x5 icon
     */
    private boolean click5x5(double x, double y) {

        boolean xBounded = grid5x5XMin < x && x < grid5x5XMax;
        boolean yBounded = gridSelectYMin < y && y < gridSelectYMax;

        return (xBounded && yBounded);
    }


/* **************************************************************************
 *            * Debug Methods *
 ***************************************************************************/

    // DEBUG - Used for testing the layout
    /*
    private void debugDrawGridSelectBoxes() {

        // DEBUG DRAWING BOXES
        StdDraw.setPenRadius(0.02);
        StdDraw.setPenColor(Color.BLACK);
        double xCenter, yCenter, halfWidth;

        yCenter = 0.5 * (gridSelectYMin + gridSelectYMax);

        xCenter = 0.5 * (grid3x3XMin + grid3x3XMax);
        halfWidth = 0.5 * (grid3x3XMax - grid3x3XMin);
        StdDraw.rectangle(xCenter, yCenter, halfWidth, 0.5 * (gridSelectYMax - gridSelectYMin));

        xCenter = 0.5 * (grid4x4XMin + grid4x4XMax);
        halfWidth = 0.5 * (grid4x4XMax - grid4x4XMin);
        StdDraw.rectangle(xCenter, yCenter, halfWidth, 0.5 * (gridSelectYMax - gridSelectYMin));

        xCenter = 0.5 * (grid5x5XMin + grid5x5XMax);
        halfWidth = 0.5 * (grid5x5XMax - grid5x5XMin);
        StdDraw.rectangle(xCenter, yCenter, halfWidth, 0.5 * (gridSelectYMax - gridSelectYMin));

    }
     */

    /*
    public void drawSolutionArea() {

        StdDraw.setPenColor(Color.BLACK);
        StdDraw.setPenRadius(0.03);
        double xCenter = 0.5 * (solverXMin + solverXMax);
        double yCenter = 0.5 * (solverYMin + solverYMax);
        double halfWidth = 0.5 * (solverXMax - solverXMin);
        StdDraw.square(xCenter, yCenter, halfWidth);

    }
     */

    /*
    public void drawNewGameArea() {

        StdDraw.setPenColor(Color.BLACK);
        StdDraw.setPenRadius(0.03);
        double xCenter = 0.5 * (newGameXMin + newGameXMax);
        double yCenter = 0.5 * (newGameYMin + newGameYMax);
        double halfWidth = 0.5 * (newGameXMax - newGameXMin);
        StdDraw.square(xCenter, yCenter, halfWidth);

    }
     */

}
