import java.awt.event.KeyEvent;

/**
 * A TetrisUI is a thread that uses a shared TetrisDisplay to respond to
 * keyboard events. Must perform the following:
 * * (1) Rotate.
 * * (2) Move Left.
 * * (3) Move Down.
 * * (4) Move Right.
 * * (5) Drop Piece.
 *
 * @author adamm.hockman@gmail.com
 */
public class ThreadTetrisUI extends Thread {

    // time to wait after keyboard events
    private static final int TRANSLATE_PIECE_TIME = 150;
    private static final int ROTATE_PIECE_TIME = 150;
    private static final int DROP_PIECE_TIME = 200;

    // shared tetris display
    private final TetrisDisplay display;

    /**
     * Constructor takes a reference to a TetrisDisplay.
     * Note this display is shared among other Threads and
     * synchronization should be used where needed.
     * @param display TetrisDisplay receives result of keyboard event
     */
    public ThreadTetrisUI(TetrisDisplay display) {

        if (display == null)
            throw new IllegalArgumentException("Must supply non-null reference to constructor");
        this.display = display;

    }

    /**
     * Method that is called when thread.start() is executed.
     * Executes all control logic and is synchronized to allow
     * other threads to interact with the Display.
     * <p>
     * Listens for keyboard events corresponding to the movements
     * outlined in the class summary.
     * Performs all actions on the shared display.
     */
    public synchronized void run() {

        // end once interrupted
        while (!Thread.interrupted()) {
            // move piece down
            if (StdDraw.isKeyPressed(KeyEvent.VK_DOWN)) {
                if (display.down()) {
                    display.refresh();
                    sleep(TRANSLATE_PIECE_TIME);
                }
            }
            // move piece left
            if (StdDraw.isKeyPressed(KeyEvent.VK_LEFT)) {
                if (display.left()) {
                    display.refresh();
                    sleep(TRANSLATE_PIECE_TIME);
                }
            }
            // move piece right
            if (StdDraw.isKeyPressed(KeyEvent.VK_RIGHT)) {
                if (display.right()) {
                    display.refresh();
                    sleep(TRANSLATE_PIECE_TIME);
                }
            }
            // rotate piece
            if (StdDraw.isKeyPressed(KeyEvent.VK_SPACE)) {
                if (display.rotate()) {
                    display.refresh();
                    sleep(ROTATE_PIECE_TIME);
                }
            }
            // drop piece
            if (StdDraw.isKeyPressed(KeyEvent.VK_UP)) {
                display.dropPiece();
                display.refresh();
                sleep(DROP_PIECE_TIME);
            }
        }
    }

    /**
     * Private helper method used to simplify the UI wait times after
     * each command.
     * Waits the provided time before returning.
     * @param waitTime int milliseconds to wait before returning
     */
    private void sleep(int waitTime) {

        // sleep for the given time in milliseconds
        try {
            Thread.sleep(waitTime);
        } catch (InterruptedException e) {
            System.out.println("Sleep was interrupted");
        }

    }

}
