/**
 * Implements the game dynamics for the Number Slide puzzle game.
 * (1) - Allows the user to choose the grid size and create a new game
 * (2) - Provides a click-based UI for the user to try to solve the game
 * (3) - Presents a visualization of the solution if the user desires
 */
public class ThreadNumberGame {

    // toggle for animating the tile swaps
    private static final boolean ANIMATIONS = true;

    // menu wait time
    private static final int PAUSE_TIME = 100;

    // reference to the number display, provides all control
    private final NumberDisplay display;


/* **************************************************************************
 *            * Constructor and Accessor Methods *
 ***************************************************************************/

    /**
     * Simple constructor. Initializes a new NumberDisplay.
     */
    public ThreadNumberGame() {

        this.display = new NumberDisplay();

    }


/* **************************************************************************
 *            * User Interface Methods *
 ***************************************************************************/

    /**
     * Method that is called when thread.start() is executed.
     * Executes all control logic and is synchronized to allow
     * other threads to interact with the Display.
     * <p>
     * Listens for mouse events corresponding to the clicking of
     * tiles or menu icons.
     * Performs all actions on the shared display.
     */
    public synchronized void run() {

        // runs until user closes window
        while (!Thread.interrupted()) {
            // create a new game
            display.startNewGame();

            // show the new game setup
            display.refresh();

            // run the UI allowing for click events
            runUI();
        }

    }

    /**
     * Listens for the following mouse events:
     * (1) User clicks on new game
     * (2) User clicks on solve
     * (3) User clicks on tile adjacent to empty space
     */
    private synchronized void runUI() {

        // until the board is in its goal state
        while (!display.isGoal()) {

            if (StdDraw.isMousePressed()) {
                // mouse click (x,y)-coordinates
                double x = StdDraw.mouseX();
                double y = StdDraw.mouseY();

                // listen for clicks inside board area
                if (display.clickInsideBoard(x, y)) {
                    // get clicked tile row and col
                    int row0 = display.clickRow(y);
                    int col0 = display.clickCol(x);
                    Tile tile0 = display.getTile(row0, col0);

                    // check if tile is adjacent to the empty space
                    if (display.zeroSwapTile(row0, col0)) {
                        // refresh using animations if toggled
                        if (ANIMATIONS)
                            display.refreshAnimated();
                        else
                            display.refresh();
                    }
                }

                // listen for clicks inside solve icon area
                if (display.clickSolutionBadge(x, y))
                    display.runSolver();

                // listen for clicks inside new game icon area
                if (display.clickNewGameBadge(x, y))
                    return;
            }
            // brief rest to avoid overloading CPU
            sleep();
        }
        // once the board is in the goal position, invert the display
        display.refreshInverted();

        // listen for the user the select the new game icon
        // allows them to look at the finished game before moving on
        // when the method returns, the game goes back to new-game screen
        while (true) {
            if (StdDraw.isMousePressed()) {

                double x = StdDraw.mouseX();
                double y = StdDraw.mouseY();

                if (display.clickNewGameBadge(x, y))
                    return;
            }
            // brief pause to avoid overload
            sleep();
        }
    }

    /**
     * Private helper method used to organize code for thread sleeping.
     * In particular, catching Interruptions / Exceptions.
     * Waits for the time set as a static constant PAUSE_TIME
     */
    private void sleep() {

        try {
            Thread.sleep(PAUSE_TIME);
        } catch (InterruptedException e) {
            System.out.println("Sleep interrupted.");
        }

    }


}
