/**
 * The driver for this particular implementation of Tetris.
 * The main method performs the following:
 *  (1) Initialize a new TetrisDisplay
 *  (2) Initialize a new ThreadTetrisGame thread using the display.
 *  (3) Run the thread.
 *
 * @author adamm.hockman@gmail.com
 */
public class Main {

    /**
     * Executes our implementation of Tetris.
     * @param args String[] standard input args
     */
    public static void main(String[] args) {

        System.out.println("Welcome to Tetris");

        // set height and width
        int DEFAULT_HEIGHT = 20;
        int DEFAULT_WIDTH = 10;

        // create a new tetris display
        TetrisDisplay display = new TetrisDisplay(DEFAULT_HEIGHT, DEFAULT_WIDTH);

        // create the primary thread that runs the game
        ThreadTetrisGame tetrisGame = new ThreadTetrisGame(display);

        // begin the game
        tetrisGame.start();

    }

}