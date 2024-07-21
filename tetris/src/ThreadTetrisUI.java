public class ThreadTetrisUI extends Thread {

    private static final int commandBufferTime = 100;

    private final TetrisDisplay display;

    public ThreadTetrisUI(TetrisDisplay display) {

        if (display == null)
            throw new IllegalArgumentException("Must supply non-null reference to constructor");
        this.display = display;

    }

    public synchronized void run() {

        while (true) {

            if (StdDraw.hasNextKeyTyped()) {
                char keyTyped = StdDraw.nextKeyTyped();
                switch (keyTyped) {
                    case '4':
                    case 'J':
                    case 'j':
                        if (display.left()) {
                            display.refresh();
                        }
                        break;
                    case '2':
                    case 'M':
                    case 'm':
                        if (display.down()) {
                            display.refresh();
                        }
                        break;
                    case '6':
                    case 'L':
                    case 'l':
                        if (display.right()) {
                            display.refresh();
                        }
                        break;
                    case '5':
                    case 'K':
                    case 'k':
                        if (display.rotate()) {
                            display.refresh();
                        }
                        break;
                }
                // clear any remaining inputs
                while (StdDraw.hasNextKeyTyped())
                    StdDraw.nextKeyTyped();
                sleep();
            }
        }
    }

    private void sleep() {

        // sleep for the dropTime set above
        try {
            Thread.sleep(commandBufferTime);
        } catch (InterruptedException e) {
            System.out.println("Sleep was interrupted");
        }

    }



}
