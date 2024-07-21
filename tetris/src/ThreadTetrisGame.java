public class ThreadTetrisGame extends Thread {

    private static final int dropTime = 500;

    private static final double FADE = 0.30;

    private static final double newGameBlockWidth = (125.0 / 800.0);

    private final TetrisDisplay display;

    public ThreadTetrisGame(TetrisDisplay display) {

        if (display == null)
            throw new IllegalArgumentException("Must supply non-null reference to constructor");
        this.display = display;

    }

    public synchronized void run() {

        while (true) {

            display.refreshNewGame(FADE);

            if (StdDraw.isMousePressed()) {
                double x = StdDraw.mouseX();
                double y = StdDraw.mouseY();

                if (clickNewGame(x, y)) {
                    runNewGame();
                    display.refreshNewGame(FADE);
                }

            }

            StdDraw.pause(200);

        }

    }

    private boolean clickNewGame(double x, double y) {

        boolean xWindow = 0.5 - newGameBlockWidth < x && x < 0.5 + newGameBlockWidth;
        boolean yWindow = 0.5 - newGameBlockWidth < y && y < 0.5 + newGameBlockWidth;

        return (xWindow && yWindow);
    }

    private synchronized void runNewGame() {

        // run until the game ends (i.e. a new piece cannot be generated)
        do {

            // keep moving the active piece down until it cannot
            // go any further
            while (display.down()) {
                // update the display after moving the piece down
                display.refresh();
                // wait the dropTime
                sleep();
            }

        } while (display.generatePiece());

        System.out.println("Game Over");
    }

    private void sleep() {

        // sleep for the dropTime set above
        try {
            Thread.sleep(dropTime);
        } catch (InterruptedException e) {
            System.out.println("Sleep was interrupted");
        }

    }

    public static void main(String[] args) {

        TetrisDisplay disp = new TetrisDisplay(20,10);
        ThreadTetrisGame myThread = new ThreadTetrisGame(disp);

        myThread.start();


    }

}
