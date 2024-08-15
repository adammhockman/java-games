public class ThreadNumberGame {

    private static final boolean ANIMATIONS = false;
    private final NumberDisplay display;

    public ThreadNumberGame(NumberDisplay display) {

        if (display == null)
            throw new IllegalArgumentException("Must supply non-null reference to constructor");

        this.display = display;

    }

    public ThreadNumberGame() {

        this.display = new NumberDisplay();

    }

    public synchronized void run() {

        while (true) {
            display.startNewGame();
            System.out.println("New game started!");
            display.refresh();
            runUI();
        }

    }

    private void runUI() {

        while (!display.isGoal()) {

            if (StdDraw.isMousePressed()) {

                double x = StdDraw.mouseX();
                double y = StdDraw.mouseY();

                if (display.clickInsideBoard(x, y)) {
                    System.out.println("Clicked on Board:");
                    int row0 = display.clickRow(y);
                    int col0 = display.clickCol(x);
                    Tile tile0 = display.getTile(row0, col0);
                    if (tile0 != null)
                        System.out.printf("(row: %d, col: %d, tile: %d)\n", row0, col0, tile0.val());
                    else
                        System.out.printf("(row: %d, col: %d, tile: %d)\n", row0, col0, 0);

                    if (display.zeroSwapTile(row0, col0)) {
                        System.out.println("Swapping with zero tile");
                        display.refresh();

                        // ANIMATIONS
                        /*
                        if (ANIMATIONS)
                            display.animatedSwap();
                        else
                            display.refresh();
                         */
                    }
                }
                System.out.println();

                if (display.clickSolutionBadge(x, y)) {
                    System.out.println("Clicked inside solution badge");
                    display.runSolver();
                }

                if (display.clickNewGameBadge(x, y)) {
                    System.out.println("Clicked inside new game badge");
                    return;

                }
            }
            sleep(100);
        }
        display.refreshInverted();

        while (true) {
            if (StdDraw.isMousePressed()) {

                double x = StdDraw.mouseX();
                double y = StdDraw.mouseY();

                if (display.clickNewGameBadge(x, y)) {
                    System.out.println("Clicked inside new game badge");
                    return;
                }
            }
            sleep(100);
        }
    }

    private void sleep(long millis) {

        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            System.out.println("Sleep interrupted.");
        }

    }




}
