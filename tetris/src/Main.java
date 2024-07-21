/**
 *
 */

public class Main {



    public static void main(String[] args) {

        System.out.println("Welcome to Tetris");

        int TEST_HEIGHT = 20;
        int TEST_WIDTH = 10;

        // GUI TEST CLIENT
        // instantiate a new game board and pass to new display
        TetrisDisplay display = new TetrisDisplay(TEST_HEIGHT, TEST_WIDTH);

        // create the primary thread - the drop thread
        // it begins the game by generating a piece and beginning the drop sequence
        ThreadTetrisGame tetrisGame = new ThreadTetrisGame(display);
        tetrisGame.start();

        // create the UI thread - allows the user to input controls to
        // move the active piece
        ThreadTetrisUI threadTetrisUI = new ThreadTetrisUI(display);
        threadTetrisUI.start();

    }

}