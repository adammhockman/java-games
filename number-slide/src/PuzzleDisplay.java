

import java.awt.*;

public class PuzzleDisplay {

    private final Board board;

    private static final boolean ANIMATIONS = false;

    private static final boolean SOLVABLE = false;

    private static final int SWAP_TIME = 200;
    private static final int FPS = 30;

    private static final String BACKGROUND_IMAGE = "graphics/numberpuzzle_display_800px.png";

    // constants
    private static final Font SOLVER_PANEL_FONT = new Font("Courier", Font.PLAIN, 18);

    private static final int SOLUTION_TRANSITION_TIME = 500;


    // canvas size
    private static final int CANVAS_WIDTH_PIXELS = 704;
    private static final int CANVAS_HEIGHT_PIXELS = 800;

    // tetris display layout scale
    private static final double displayXScale = 22.0 / 25.0;
    private static final double displayYScale = 1.0;

    // board panel layout
    private static final double boardXMin = 2.0 / 25.0;
    private static final double boardXMax = 20.0 / 25.0;
    private static final double boardYMin = 5.0 / 25.0;
    private static final double boardYMax = 23.0 / 25.0;

    // distance panels layout


    // solver panel layout
    private static final double solverXMin = 12.0 / 15.0;
    private static final double solverXMax = 14.0 / 15.0;
    private static final double solverYMin = 8.0 / 15.0;
    private static final double solverYMax = 10.0 / 15.0;

/* **************************************************************************
 *            * Constructors *
 ***************************************************************************/

    public PuzzleDisplay(String boardFile) {

        this.board = new Board(boardFile);
        this.board.setScale(boardXMin, boardYMin, boardXMax, boardYMax);

        setupCanvas();

    }

    public PuzzleDisplay(int boardSize) {

        BoardGenerator bg = new BoardGenerator(boardSize);
        this.board = bg.generateBoard(SOLVABLE);
        this.board.setScale(boardXMin, boardYMin, boardXMax, boardYMax);

        setupCanvas();

    }

/* **************************************************************************
 *            * Constructors *
 ***************************************************************************/
    public void runUI() {

        refresh();

        while (true) {

            if (StdDraw.isMousePressed()) {

                double x = StdDraw.mouseX();
                double y = StdDraw.mouseY();

                if (clickInsideBoard(x, y)) {
                    System.out.println("Clicked on Board:");
                    int row0 = clickRow(y);
                    int col0 = clickCol(x);
                    int tile0 = board.getTile(row0, col0);
                    System.out.printf("(row: %d, col: %d, tile: %d)\n", row0, col0, tile0);

                    if (board.zeroSwapTile(row0, col0)) {
                        System.out.println("Swapping with zero tile");
                        if (ANIMATIONS)
                            animatedSwap();
                        else
                            refresh();
                    }
                }
                System.out.println();

                if (clickInsideSolution(x, y)) {
                    System.out.println("Clicked inside solution box");
                    runSolver();
                }

            }
            sleep(100);
        }

    }

    private void animatedSwap() {

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


    private void runSolver() {

        Solver solver = new Solver(board);

        if (!solver.isSolvable()) {
            System.out.println("Board is unsolvable.");
            return;
        }

        for (Board b : solver.solution()) {
            solutionRefresh(b);
            StdDraw.pause(SOLUTION_TRANSITION_TIME);
        }

    }

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
    private boolean clickInsideSolution(double x, double y) {

        boolean xBounded = solverXMin < x && x < solverXMax;
        boolean yBounded = solverYMin < y && y < solverYMax;

        return (xBounded && yBounded);
    }

    private int clickRow(double y) {

        double bracket = (boardYMax - boardYMin) / ((double) board.dimension());

        for (int row = 0; row < board.dimension(); row++) {
            double rowCutoff = boardYMax - (row + 1) * bracket;
            if (y > rowCutoff)
                return row;
        }

        return -1;

    }

    private int clickCol(double x) {

        double bracket = (boardXMax - boardXMin) / ((double) board.dimension());

        for (int col = 0; col < board.dimension(); col++) {
            double colCutoff = boardXMin + (col + 1) * bracket;
            if (x < colCutoff)
                return col;
        }

        return -1;

    }

    private boolean clickInsideBoard(double x, double y) {

        boolean xBounded = boardXMin < x && x < boardXMax;
        boolean yBounded = boardYMin < y && y < boardYMax;

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
        // drawSidePanel();
        StdDraw.show();

    }

    public void animatedRefresh(double t) {

        StdDraw.clear();
        drawDisplayBorder();
        drawBackground();
        board.animatedDraw(t);
        // drawSidePanel();
        StdDraw.show();

    }

    private void drawBackground() {

        double x = 0.5 * displayXScale;
        double y = 0.5 * displayYScale;

        StdDraw.picture(x, y, BACKGROUND_IMAGE);

    }

    /*
    private void drawSidePanelBackground() {

        double x = 0.5 * (boardXMax + displayXScale);
        double y = 0.5 * displayYScale;

        double width = displayXScale - boardXMax;
        double height = displayYScale;

        StdDraw.picture(x, y, sidePanelImage, width, height);

    }

     */

    public void solutionRefresh(Board dispBoard) {

        dispBoard.setScale(boardXMin, boardYMin, boardXMax, boardYMax);

        StdDraw.clear();
        drawDisplayBorder();
        dispBoard.draw();
        drawSolverPanel();
        StdDraw.show();

    }

    private void drawSolverPanel() {

        double x = 0.5 * (solverXMin + solverXMax);
        double hWidth = 0.5 * (solverXMax - solverXMin);
        double y = 0.5 * (solverYMin + solverYMax);
        double hHeight = 0.5 * (solverYMax - solverYMin);

        StdDraw.setPenColor(Color.BLACK);
        StdDraw.rectangle(x, y, hWidth, hHeight);

        // draw label
        String label = "Solver";
        StdDraw.setFont(SOLVER_PANEL_FONT);
        StdDraw.text(x, y, label);


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

    /*
    public void drawSidePanel() {

        drawSidePanelBackground();
        drawSolverPanel();


    }

     */


}
