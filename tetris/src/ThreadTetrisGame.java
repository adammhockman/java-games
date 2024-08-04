
/**
 * A TetrisGame is a thread that uses a shared TetrisDisplay to provide
 * a UI for game dynamics:
 * * (1) Creates a new Tetris game via click-based UI.
 * * (2) Runs automatic drop until new piece cannot be generated.
 * * (3) Provides click-based UI for starting a new game after game-over.
 *
 * @author adamm.hockman@gmail.com
 */
public class ThreadTetrisGame extends Thread {

    // time between drops
    private static final int DROP_TIME = 500;

    // tetris display
    private final TetrisDisplay display;


/* ***************************************************************************
 *    * Constructors and Initialization
 ****************************************************************************/

    /**
     * Constructor takes a reference to a TetrisDisplay.
     * Note this display is shared among other Threads and
     * synchronization should be used where needed.
     * @param display TetrisDisplay provides all interface with user
     */
    public ThreadTetrisGame(TetrisDisplay display) {

        if (display == null)
            throw new IllegalArgumentException("Must supply non-null reference to constructor");
        this.display = display;

    }

    /**
     * Method that is called when thread.start() is executed.
     * Executes all control logic and is synchronized to allow
     * other threads to interact with the Display.
     * <p>
     * Initializes a new game then begins drop loop.
     * Allows user to begin new game after a game-over occurs.
     * <p>
     * Terminates once user clicks quit game from the new-game menu.
     */
    public synchronized void run() {

        boolean initialGame = true;
        display.displayNewGame();

        while (true) {
            // mouse listener
            if (StdDraw.isMousePressed()) {
                double x = StdDraw.mouseX();
                double y = StdDraw.mouseY();

                // user clicks new game icon
                if (display.clickNewGame(x, y)) {
                    if (!initialGame)
                        display.createNewGame();
                    runNewGame();
                    display.displayNewGame();
                    initialGame = false;
                }

                // user clicks quit game icon
                if (display.clickQuitGame(x, y)) {
                    // close the viewing canvas
                    StdDraw.close();
                    // print to terminal
                    System.out.println("Goodbye");
                    // exit control loop
                    return;
                }
            }
            StdDraw.pause(50);
        }

    }

    /**
     * Private helper method that continuously drops an active display.
     * An active display has already initialized the board and generated
     * an active piece.
     * <p>
     * This method also starts and ends the keyboard UI thread that allows
     * for the user to move / drop pieces.
     * Synchronized - called by run().
     */
    private synchronized void runNewGame() {

        // initialize and run the keyboard UI thread
        ThreadTetrisUI keyUI = new ThreadTetrisUI(display);
        keyUI.start();

        // run until a new piece cannot be generated
        do {
            // continue moving the active piece down
            while (display.down()) {
                // update the display
                display.refresh();
                // wait the dropTime
                sleep();
            }
        } while (display.generatePiece());

        // interrupt and close keyboard UI
        keyUI.interrupt();

        // print to terminal
        System.out.println("Game Over - Score: " + display.getScore());

    }

    /**
     * Private helper method used to simplify the drop mechanism in run().
     * Waits the pre-set drop time before returning.
     */
    private void sleep() {

        // sleep for the dropTime set above
        try {
            Thread.sleep(DROP_TIME);
        } catch (InterruptedException e) {
            System.out.println("Sleep was interrupted: " + e.getMessage());
        }

    }


/* ***************************************************************************
 *    * Test Client
 * ***************************************************************************/

    // DEBUG: Test client for debugging
    /*
    public static void main(String[] args) {

        TetrisDisplay display0 = new TetrisDisplay(20,10);
        ThreadTetrisGame myThread = new ThreadTetrisGame(display0);

        myThread.start();

    }
     */

}
