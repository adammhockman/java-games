

public class Main {


    public static void main(String[] args) {

        // create the primary thread
        // runs the UI allowing the user to click on various tiles
        ThreadNumberGame numberGame = new ThreadNumberGame();
        numberGame.run();

    }

}