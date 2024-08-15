
import edu.princeton.cs.algs4.StdRandom;

import java.util.HashSet;

public class BoardGenerator {

    public static void main(String[] args) {

        int TOTAL_BOARDS = 100;
        int BOARD_SIZE = 4;

        HashSet<Board> boards = new HashSet<>();

        for (int difficulty = 0; difficulty < 3; difficulty++) {

            int count = 0;
            while (count < TOTAL_BOARDS) {

                Board testBoard = randomBoard(BOARD_SIZE, difficulty);

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

    private static void randomSwap(Board board) {

        int zeroRow = board.getZeroRow();
        int zeroCol = board.getZeroCol();

        int destRow = randomDestRowCol(zeroRow, board.dimension());
        int destCol = randomDestRowCol(zeroCol, board.dimension());

        // choose destination row
        int flip = StdRandom.uniformInt(2);
        if (flip == 0) {
            board.zeroSwapTile(destRow, zeroCol);
            /*
            if (board.zeroSwapTile(destRow, zeroCol))
                System.out.println("Swap successful");
            else
                System.out.printf("Couldn't swap: destRow = %d, zeroCol = %d\n", destRow, zeroCol);
             */
        } else {
            board.zeroSwapTile(zeroRow, destCol);
            /*
            if (board.zeroSwapTile(zeroRow, destCol))
                System.out.println("Swap successful");
            else
                System.out.printf("Couldn't swap: zeroRow = %d, destCol = %d\n", zeroRow, destCol);
             */
        }
    }

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
