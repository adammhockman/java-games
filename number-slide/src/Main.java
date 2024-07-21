

public class Main {



    public static void main(String[] args) {

        String puzzleFile3 = "puzzles/puzzle3x3-09.txt";

        //PuzzleDisplay pd = new PuzzleDisplay(puzzleFile3);
        PuzzleDisplay pd = new PuzzleDisplay(3);
        pd.runUI();
        /*
        pd.refresh();

        while (true) {

            if (StdDraw.isMousePressed()) {

                double x = StdDraw.mouseX();
                double y = StdDraw.mouseY();
                System.out.printf("You clicked at (%5.52f, %5.2f)\n", x, y);

            }


            StdDraw.pause(60);
        }
         */


    }

}