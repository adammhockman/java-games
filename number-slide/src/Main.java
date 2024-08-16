
/**
 * The driver for this implementation of Number Slide puzzle.
 * The main method performs the following:
 *  (1) Initialize a new ThreadNumberGame thread.
 *  (2) Run the thread.
 *
 * @author adamm.hockman@gmail.com
 */
public class Main {

    /**
     * Executes our implementation of the Number Slide game.
     * @param args String[] standard input args
     */
    public static void main(String[] args) {

        // create the primary thread
        // runs the UI allowing the user to click on various tiles
        ThreadNumberGame numberGame = new ThreadNumberGame();
        numberGame.run();

    }

}