
import edu.princeton.cs.algs4.StdRandom;

import java.util.HashSet;

/**
 * Class used to generate new puzzle boards and write them to disk.
 * For the 3x3 case, we can use total randomness.
 * For the 4x4 and up case, however, the complexity becomes too large
 * for the solver.
 * Instead we start with the goal board, and perform a pre-set number
 * of random tile swaps to arrive at a board configuration.
 * We can then write this to disk to be used later for loading new games.
 */
public class BoardGenerator {

    /**
     * Client implements the logic described in the class description.
     * Creates a new set of boards based on the grid size set.
     * We also keep track of all the boards we currently have to ensure
     * we don't write duplicates to disk.
     *
     * @param args String[] standard input args
     */
    public static void main(String[] args) {

        // total boards and grid size
        int TOTAL_BOARDS = 100;
        int BOARD_SIZE = 4;

        // keep track of boards we've already generated
        HashSet<Board> boards = new HashSet<>();

        // difficulty is defined as the number of random swaps away from the goal
        for (int difficulty = 0; difficulty < 3; difficulty++) {

            int count = 0;
            while (count < TOTAL_BOARDS) {
                // generate a new board randomly
                Board testBoard = randomBoard(BOARD_SIZE, difficulty);
                // check whether the board is already created
                if (!boards.contains(testBoard)) {
                    // create file name
                    String fileName = "puzzles/puzzle4x4/";
                    if (difficulty == 0)
                        fileName += "easy/puzzle4x4_easy";
                    else if (difficulty == 1)
                        fileName += "medium/puzzle4x4_medium";
                    else
                        fileName += "hard/puzzle4x4_hard";
                    fileName += String.format("%02d.txt", count);

                    boards.add(testBoard);
                    writeToFile(testBoard, fileName);
                    count++;
                }
            }
        }

    }

    /**
     * Private helper method used to randomly generate an nxn puzzle board
     * configuration. This is done by taking a number of random swaps after
     * initializing the goal board.
     *
     * @param size int grid size of new board
     * @param difficulty int difficulty
     * @return Board reference to newly generated board
     */
    private static Board randomBoard(int size, int difficulty) {

        int complexity;

        if (difficulty == 2)
            complexity = 100;
        else if (difficulty == 1)
            complexity = 80;
        else
            complexity = 60;

        // Start from Goal Board and make random swaps
        Board board = Board.identity(size);

        for (int i = 0; i < complexity; i++)
            randomSwap(board);

        System.out.println(board);

        return board;

    }

    /**
     * Private helper method used to perform a random swap.
     * Considers all the tiles adjacent to the empty tile, i.e., the
     * neighbors of the board, and randomly chooses one to swap with.
     *
     * @param board reference to board to perform swap on
     */
    private static void randomSwap(Board board) {

        // find the empty tile location
        int zeroRow = board.getZeroRow();
        int zeroCol = board.getZeroCol();
        // select a random neighbor to swap with
        int destRow = randomDestRowCol(zeroRow, board.dimension());
        int destCol = randomDestRowCol(zeroCol, board.dimension());

        // decide whether to swap horizontally or vertically
        int flip = StdRandom.uniformInt(2);
        if (flip == 0)
            board.zeroSwapTile(destRow, zeroCol);
        else
            board.zeroSwapTile(zeroRow, destCol);
    }

    /**
     * Takes a row/col location, and determines a random neighboring
     * location based on the grid size provided.
     *
     * @param zeroPos the row/column of the empty space
     * @param size int number of rows/columns
     * @return int the random neighboring row/column
     */
    private static int randomDestRowCol(int zeroPos, int size) {

        if (zeroPos == 0)
            return (zeroPos + 1);

        if (zeroPos == size - 1)
            return (zeroPos - 1);

        int flip = StdRandom.uniformInt(2);
        if (flip == 0)
            return (zeroPos - 1);
        else
            return (zeroPos + 1);

    }

    /**
     * Private helper method that writes a Board configuration to disk,
     * under the file name provided.
     * Example format: n = 3
     * 1:3
     * 2: 0  1  8  3
     * 3: 5  2  6 15
     * 4:10  7 13  4
     * 5: 9 14 12 11
     *
     * @param board Board with a given tile configuration
     * @param fileName String location to write board to disk
     */
    private static void writeToFile(Board board, String fileName) {

        Out out = new Out(fileName);
        int n = board.dimension();
        out.println(n);
        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                Tile tile = board.getTile(row, col);
                if (tile == null)
                    out.printf("%2d ", 0);
                else
                    out.printf("%2d ", tile.val());
            }
            out.println();
        }
        out.close();
    }

}
