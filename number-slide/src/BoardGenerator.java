import java.util.ArrayList;

public class BoardGenerator {

    private final int size;

    public BoardGenerator(int size) {

        this.size = size;

    }

    // if solvable is false, randomly generate board
    public Board generateBoard(boolean solvable) {

        int[][] tiles = new int[size][size];

        ArrayList<Integer> tileValues = new ArrayList<>();
        for (int i = 0; i < size * size; i++) {
            tileValues.add(i);
        }

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                int randIndex = StdRandom.uniformInt(tileValues.size());
                int val = tileValues.remove(randIndex);
                tiles[row][col] = val;
            }
        }

        Board testBoard = new Board(tiles);

        if (solvable) {
            Solver solver = new Solver(testBoard);
            if (!solver.isSolvable())
                testBoard = testBoard.twin();
        }

        return testBoard;

    }



}
