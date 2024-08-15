
import java.awt.*;

public class NumberDisplay {

    private Board board;

    private static final boolean ANIMATIONS = false;

    private static final int DEFAULT_GRID_SIZE = 4;

    private static final int SOLUTION_TRANSITION_TIME = 500;
    private static final int SWAP_TIME = 200;
    private static final int FPS = 30;

    private static final String BACKGROUND_IMAGE = "graphics/numberslide_display_800px.png";
    private static final String BACKGROUND_NEW_GAME_IMAGE = "graphics/numberslide_display_newgame.png";

    // canvas size
    private static final int CANVAS_WIDTH_PIXELS = 704;
    private static final int CANVAS_HEIGHT_PIXELS = 800;

    // tetris display layout scale
    private static final double displayXScale = 22.0 / 25.0;
    private static final double displayYScale = 1.0;

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
 *            * Constructors *
 ***************************************************************************/

    public NumberDisplay() {

        this.board = null;

        setupCanvas();

    }

    public NumberDisplay(int boardSize) {

        this.board = Board.createBoard(boardSize);
        this.board.setScale(boardXMin, boardYMin, boardXMax, boardYMax);

        setupCanvas();

    }

/* **************************************************************************
 *            * Accessor Method *
 ***************************************************************************/

    public Tile getTile(int row, int col) {

        return board.getTile(row, col);

    }


/* **************************************************************************
 *            * Click Methods *
 ***************************************************************************/
    public boolean clickSolutionBadge(double x, double y) {

        boolean xBounded = solverXMin < x && x < solverXMax;
        boolean yBounded = solverYMin < y && y < solverYMax;

        return (xBounded && yBounded);
    }

    public boolean clickNewGameBadge(double x, double y) {

        boolean xBounded = newGameXMin < x && x < newGameXMax;
        boolean yBounded = newGameYMin < y && y < newGameYMax;

        return (xBounded && yBounded);

    }

    public boolean clickInsideBoard(double x, double y) {

        boolean xBounded = boardXMin < x && x < boardXMax;
        boolean yBounded = boardYMin < y && y < boardYMax;

        return (xBounded && yBounded);

    }

    public int clickRow(double y) {

        double bracket = (boardYMax - boardYMin) / ((double) board.dimension());

        for (int row = 0; row < board.dimension(); row++) {
            double rowCutoff = boardYMax - (row + 1) * bracket;
            if (y > rowCutoff)
                return row;
        }

        return -1;

    }

    public int clickCol(double x) {

        double bracket = (boardXMax - boardXMin) / ((double) board.dimension());

        for (int col = 0; col < board.dimension(); col++) {
            double colCutoff = boardXMin + (col + 1) * bracket;
            if (x < colCutoff)
                return col;
        }

        return -1;

    }

    private boolean click3x3(double x, double y) {

        boolean xBounded = grid3x3XMin < x && x < grid3x3XMax;
        boolean yBounded = gridSelectYMin < y && y < gridSelectYMax;

        return (xBounded && yBounded);

    }

    private boolean click4x4(double x, double y) {

        boolean xBounded = grid4x4XMin < x && x < grid4x4XMax;
        boolean yBounded = gridSelectYMin < y && y < gridSelectYMax;

        return (xBounded && yBounded);
    }

    private boolean click5x5(double x, double y) {

        boolean xBounded = grid5x5XMin < x && x < grid5x5XMax;
        boolean yBounded = gridSelectYMin < y && y < gridSelectYMax;

        return (xBounded && yBounded);
    }

/* **************************************************************************
 *            * Draw Methods *
 ***************************************************************************/
    public void refresh() {

        StdDraw.clear();
        drawDisplayBorder();
        drawBackground();

        board.draw();
        StdDraw.show();

    }

    public void refreshInverted() {

        StdDraw.clear();
        drawDisplayBorder();
        drawBackground();

        board.invert();
        board.draw();
        StdDraw.show();
        board.invert();

    }

    // ANIMATED
    /*
    public void animatedRefresh(double t) {

        StdDraw.clear();
        drawDisplayBorder();
        drawBackground();
        board.animatedDraw(t);
        // drawSidePanel();
        StdDraw.show();

    }
     */

    private void drawBackground() {

        double x = 0.5 * displayXScale;
        double y = 0.5 * displayYScale;

        StdDraw.picture(x, y, BACKGROUND_IMAGE);

    }

    private void drawDisplayBorder() {

        // draw boundary frame
        StdDraw.setPenColor(Color.BLACK);
        StdDraw.setPenRadius(0.003);

        double xCenter = 0.5 * displayXScale;
        double yCenter = 0.5 * displayYScale;
        double xHalfWidth = xCenter;
        double yHalfHeight = yCenter;

        StdDraw.rectangle(xCenter, yCenter, xHalfWidth, yHalfHeight);

    }

    private void setupCanvas() {

        StdDraw.enableDoubleBuffering();
        StdDraw.setCanvasSize(CANVAS_WIDTH_PIXELS, CANVAS_HEIGHT_PIXELS);
        StdDraw.setXscale(0.0, displayXScale);
        StdDraw.setYscale(0.0, displayYScale);
        StdDraw.setTitle("Number Puzzle");

    }



/* **************************************************************************
 *            * Game Dynamics Methods *
 ***************************************************************************/

    public void startNewGame() {

        StdDraw.clear();
        double xCenter = 0.5 * displayXScale;
        double yCenter = 0.5 * displayYScale;
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

    public void solutionRefresh(Board dispBoard) {

        dispBoard.setScale(boardXMin, boardYMin, boardXMax, boardYMax);

        StdDraw.clear();
        drawDisplayBorder();
        drawBackground();
        dispBoard.draw();
        // drawSolverPanel();
        StdDraw.show();

    }

    public void runSolver() {

        board.cacheDistance();
        Solver solver = new Solver(board);

        if (!solver.isSolvable()) {
            System.out.println("Board is unsolvable.");
            return;
        }

        for (Board b : solver.solution()) {
            this.board = b;
            b.setScale(boardXMin, boardYMin, boardXMax, boardYMax);
            refresh();
            StdDraw.pause(SOLUTION_TRANSITION_TIME);
        }

    }

    public boolean isGoal() {

        return board.isGoal();

    }

    public boolean zeroSwapTile(int row, int col) {

        return board.zeroSwapTile(row, col);

    }

    // ANIMATED
    /*
    public void animatedSwap() {

        // wait time is
        int frames = (int)((1.0 / 1000.0) * SWAP_TIME * FPS);
        int waitTime = SWAP_TIME / frames;

        System.out.println("Wait Time: " + waitTime);
        System.out.println("Frames: " + frames);


        int frame = 0;
        while (frame <= frames) {
            // refresh
            double t = ((double) frame) / ((double) frames);
            // System.out.println("Step time t = " + t);

            animatedRefresh(t);
            frame++;
            sleep(waitTime);
        }

    }
     */

    private void sleep(long millis) {

        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            System.out.println("Sleep interrupted.");
        }

    }


/* **************************************************************************
 *            * Debug Methods *
 ***************************************************************************/

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

    public void drawSolutionArea() {

        StdDraw.setPenColor(Color.BLACK);
        StdDraw.setPenRadius(0.03);
        double xCenter = 0.5 * (solverXMin + solverXMax);
        double yCenter = 0.5 * (solverYMin + solverYMax);
        double halfWidth = 0.5 * (solverXMax - solverXMin);
        StdDraw.square(xCenter, yCenter, halfWidth);

    }

    public void drawNewGameArea() {

        StdDraw.setPenColor(Color.BLACK);
        StdDraw.setPenRadius(0.03);
        double xCenter = 0.5 * (newGameXMin + newGameXMax);
        double yCenter = 0.5 * (newGameYMin + newGameYMax);
        double halfWidth = 0.5 * (newGameXMax - newGameXMin);
        StdDraw.square(xCenter, yCenter, halfWidth);

    }


}
