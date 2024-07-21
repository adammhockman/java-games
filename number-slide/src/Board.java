/**
 *
 */

import edu.princeton.cs.algs4.Picture;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;

public class Board {

    private static final Font NUMBER_FONT = new Font("Courier", Font.BOLD, 32);

    private static final double BOARD_BORDER = 0.0 / 25.0;

    private static final double TILE_BUFFER = 0.02;

    // row x column indexing
    private final int[][] tiles;
    private final String[] tileImages;
    private final int size;

    // cache the location of zero-tile for easy retrieval
    private int zeroRow;
    private int zeroCol;
    private int swapTile;

    // cache the swap-tile (the tile reference preceeding the most immediate swap)
    // note this is a deep copy and not a pointer

    private int manhattanDistance;
    private int hammingDistance;

    // used for drawing the board
    private double xBoardMin;
    private double xBoardMax;
    private double yBoardMin;
    private double yBoardMax;

    private double gridSquareSize;

/* **************************************************************************
 *            * Constructors *
 ***************************************************************************/

    public Board(String puzzleFile) {

        // create initial board from file
        String puzzleFileName = "puzzles/3x3-10.txt";

        In in = new In(puzzleFile);
        this.size = Integer.parseInt(in.readLine());
        // this.tiles = new int[size][size];
        this.tiles = new int[size][size];

        for (int row = 0; row < size; row++)
            for (int col = 0; col < size; col++) {
                int val = in.readInt();
                if (val == 0) {
                    // System.out.println("Found zero at: row = " + row + ", col = " + col);
                    zeroRow = row;
                    zeroCol = col;
                    // initializie swapTile to 0
                    swapTile = 0;
                }
                tiles[row][col] = val;
            }
        tileImages = new String[size * size - 1];
        for (int i = 0; i < tileImages.length; i++) {
            String fileName = String.format("tiles/tile%02d.jpeg", i + 1);
            tileImages[i] = fileName;
        }
        cacheDistance();
    }

    public Board(int size) {

        this.size = size;
        // this.tiles = new int[size][size];
        this.tiles = new int[size][size];

        ArrayList<Integer> tileValues = new ArrayList<>();
        for (int i = 0; i < size * size; i++) {
            tileValues.add(i);
        }

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                int randIndex = StdRandom.uniformInt(tileValues.size());
                int val = tileValues.remove(randIndex);
                if (val == 0) {
                    zeroRow = row;
                    zeroCol = col;
                    // initializie swapTile to 0
                    swapTile = 0;
                }
                tiles[row][col] = val;
            }
        }
        tileImages = new String[size * size - 1];
        for (int i = 0; i < tileImages.length; i++) {
            String fileName = String.format("tiles/tile%02d.jpeg", i + 1);
            tileImages[i] = fileName;
        }
        cacheDistance();
    }

    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board(int[][] tiles) {

        // deep copy the input
        this.size = tiles.length;

        // this.tiles = new int[size][size];
        this.tiles = new int[size][size];

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                int val = tiles[row][col];
                if (val == 0) {
                    // System.out.println("Found zero at: row = " + row + ", col = " + col);
                    zeroRow = row;
                    zeroCol = col;
                    // initializie swapTile to 0
                    swapTile = 0;
                }
                this.tiles[row][col] = val;
            }
        }
        tileImages = new String[size * size - 1];
        for (int i = 0; i < tileImages.length; i++) {
            String fileName = String.format("tiles/tile%02d.jpeg", i + 1);
            tileImages[i] = fileName;
        }
        cacheDistance();
    }

    // helper method for updating cached distance values
    private void cacheDistance() {

        this.hammingDistance = calculateHammingDistance();
        this.manhattanDistance = calculateManhattanDistance();

    }

/* **************************************************************************
 *            * Accessor Methods *
 ***************************************************************************/


    public int getTile(int row, int col) {

        return tiles[row][col];

    }

    // string representation of this board
    public String toString() {
        // visual depiction of tile arrangement
        String tilesString = "";

        // first print the board size on line 1
        tilesString += Integer.toString(tiles.length) + "\n";


        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                tilesString += tiles[i][j] + " ";
            }
            tilesString += "\n";
        }

        return tilesString.substring(0, tilesString.length() - 1);
    }

    // board dimension n
    public int dimension() {
        // return tiles.length;
        return tiles.length;
    }



/* **************************************************************************
 *            * Tile Swap Methods *
 ***************************************************************************/

    /*
     * Methods attempts to take the tile at location (row, col) and swap with
     * the zero tile (blank space). Returns false if not adjacent to blank.
     *
     * Should return the tile that was swapped with zero, or null
     * it the location is already 0
     */
    public boolean zeroSwapTile(int row, int col) {

        // System.out.println("Starting swap operation on Tile:");
        // System.out.printf("[tile = %d, row = %d, col = %d]\n",tiles[row][col], row, col);
        // System.out.println("swapTile = " + this.swapTile);

        // trivial case: user clicks zero tile - return false
        if (row == zeroRow && col == zeroCol) {
            // System.out.println("Swap zero (return)");
            return false;
        }

        // if the tile is not adjacent tp the zero, return false
        if (!spaceAdjacent(row, col)) {
            // System.out.println("Tile not adjacent to zero");
            return false;
        }

        // at this point we perform the swap operation
        // cache the tile value being swapped for later reference in animating
        swapTile = tiles[row][col];

        // set zero tile to swap value and swap value to 0
        tiles[zeroRow][zeroCol] = swapTile;

        tiles[row][col] = 0;

        // update zero position caching
        zeroRow = row;
        zeroCol = col;

        // System.out.println("Updated Zero Row: " + zeroRow);
        // System.out.println("Updated Zero Col: " + zeroCol);

        // System.out.println("Updated SwapTile: " + swapTile);

        // update hamming and manhattan distances
        cacheDistance();

        return true;

    }

    // checks it the tile at (row, col) is adjacent to the zero-tile
    private boolean spaceAdjacent(int row, int col) {

        // check all neighboring boards to see if the zero-tile appears at (row, col)
        for (Board board : neighbors()) {
            if (board.getTile(row, col) == 0)
                return true;
        }
        return false;

    }

/* **************************************************************************
 *            * Iterable Methods *
 ***************************************************************************/

    // all neighboring boards
    public Iterable<Board> neighbors() {

        return new NeighborsIter();

    }

    private class NeighborsIter implements Iterable<Board> {

        private int n;
        private int totalNeighbors;
        private int currentNeighbor;
        private int[] neighborRows;
        private int[] neighborCols;

        private int zeroRow;
        private int zeroCol;

        private NeighborsIter() {

            this.n = tiles.length;
            this.totalNeighbors = 0;
            this.currentNeighbor = 0;
            this.neighborRows = new int[4];
            this.neighborCols = new int[4];

            // first we need to determine the row and column of the "0" entry
            // initialize at a position outside the board to future-proof issues
            // where the zero entry is missing
            this.zeroRow = n + 1;
            this.zeroCol = n + 1;

            for (int row = 0; row < tiles.length; row++) {
                for (int col = 0; col < tiles[row].length; col++) {
                    if (tiles[row][col] == 0) {
                        this.zeroRow = row + 1;
                        this.zeroCol = col + 1;
                    }
                }
            }


        }

        public Iterator<Board> iterator() {

            /*
            // debug statements
            System.out.println("Constructing new iterator.");
            System.out.println("Zero Entry: ( " + zeroRow + ", " + zeroCol + ")");
            System.out.println();

             */


            // store the location of the row positions for each neighbor
            // determine the number of neighbors based on row / column logic
            int neighborRow, neighborCol;

            // left neighbor
            neighborRow = zeroRow;
            neighborCol = zeroCol - 1;

            /*
            // debug statements
            System.out.println("Considering Neighbor LEFT:");
            System.out.println("(row, col) = ( " + neighborRow + ", " + neighborCol + ")");

             */

            if (isValidTileLocation(neighborRow, neighborCol)) {
                // System.out.println("Neighbor Location: VALID");
                neighborRows[totalNeighbors] = neighborRow;
                neighborCols[totalNeighbors] = neighborCol;
                totalNeighbors++;
            }
            /*
            // debug statements
            else {
                System.out.println("Neighbor Location: INVALID");
            }
            System.out.println();

             */

            // right neighbor
            neighborRow = zeroRow;
            neighborCol = zeroCol + 1;

            /*
            / debug statements
            System.out.println("Considering Neighbor RIGHT:");
            System.out.println("(row, col) = ( " + neighborRow + ", " + neighborCol + ")");

             */

            if (isValidTileLocation(neighborRow, neighborCol)) {
                // System.out.println("Neighbor Location: VALID");
                neighborRows[totalNeighbors] = neighborRow;
                neighborCols[totalNeighbors] = neighborCol;
                totalNeighbors++;
            }
            /*
                    // debug statements
            else {
                System.out.println("Neighbor Location: INVALID");
            }
            System.out.println();

             */

            // top neighbor
            neighborRow = zeroRow - 1;
            neighborCol = zeroCol;

            /*
                    // debug statements
            System.out.println("Considering Neighbor TOP:");
            System.out.println("(row, col) = ( " + neighborRow + ", " + neighborCol + ")");

             */

            if (isValidTileLocation(neighborRow, neighborCol)) {
                // System.out.println("Neighbor Location: VALID");
                neighborRows[totalNeighbors] = neighborRow;
                neighborCols[totalNeighbors] = neighborCol;
                totalNeighbors++;
            }
            /*
                    // debug statements
            else {
                System.out.println("Neighbor Location: INVALID");
            }
            System.out.println();

             */

            // bottom neighbor
            neighborRow = zeroRow + 1;
            neighborCol = zeroCol;

            // System.out.println("Considering Neighbor BOTTOM:");
            // System.out.println("(row, col) = ( " + neighborRow + ", " + neighborCol + ")");

            if (isValidTileLocation(neighborRow, neighborCol)) {
                // System.out.println("Neighbor Location: VALID");
                neighborRows[totalNeighbors] = neighborRow;
                neighborCols[totalNeighbors] = neighborCol;
                totalNeighbors++;
            }
            else {
                //    System.out.println("Neighbor Location: INVALID");
            }
            // System.out.println();

            // set current neighbor to 1 (we are labelling neighbors 1 -> n)
            if (totalNeighbors > 0) {
                currentNeighbor = 1;
            }

            /*
            // debug statements
            System.out.println("Preparing to create Iterator object.");
            System.out.println("Total Neighbors  : " + totalNeighbors);
            System.out.println("Current Neighbor : " + currentNeighbor);
            System.out.println("Neighbor Rows    : " + Arrays.toString(neighborRows));
            System.out.println("Neighbor Columns : " + Arrays.toString(neighborCols));

             */


            Iterator<Board> iter = new Iterator<Board>() {

                public boolean hasNext() {

                    /*
                    // debug statements
                    System.out.println("Calling \"hasNext()\"");
                    System.out.println("Current Neighbor : " + currentNeighbor);
                    System.out.println("Total Neighbors  : " + totalNeighbors);

                     */


                    if (totalNeighbors == 0) {
                        // (debug) System.out.println("totalNeighbors == 0");
                        return false;
                    }
                    else if (currentNeighbor > totalNeighbors) {
                        // (debug) System.out.println("currentNeighbor == totalNeighbors");
                        return false;
                    }
                    else {
                        // (debug) System.out.println("returning 'true'");

                        return true;
                    }
                }

                public Board next() {

                    /*
                    // debug statements
                    System.out.println("Calling \"next()\"");
                    System.out.println("Current Neighbor : " + currentNeighbor);
                    System.out.println("Total Neighbors  : " + totalNeighbors);
                     */

                    int neighborRow = neighborRows[currentNeighbor - 1];
                    int neighborCol = neighborCols[currentNeighbor - 1];

                    Board nextBoard = swap(neighborRow, neighborCol);

                    currentNeighbor++;

                    /*
                    // debug statements
                    System.out.println("Checking to see if incremented");
                    System.out.println("Current Neighbor : " + currentNeighbor);
                    System.out.println("Total Neighbors  : " + totalNeighbors);

                     */


                    return nextBoard;
                }

                // private helper method used to create a new Board that has the
                // zero entry swapped with the entry at the row and col provided
                private Board swap(int row, int col) {

                    /*
                    // debug statements
                    System.out.println("Creating Board inside iterator.");
                    System.out.println(
                            "Swapping zero entry with (row, col) = (" + row + ", " + col + ")");
                     */


                    // create a new tiles arrangement and copy from the Board
                    int[][] swapTiles = new int[n][n];
                    for (int i = 0; i < n; i++) {
                        for (int j = 0; j < n; j++) {
                            swapTiles[i][j] = tiles[i][j];

                        }
                    }

                    // put the entry into the current zero entry position
                    swapTiles[zeroRow - 1][zeroCol - 1] = swapTiles[row - 1][col - 1];

                    // put the zero entry where (row, col) is entry from above
                    swapTiles[row - 1][col - 1] = 0;

                    Board swapBoard = new Board(swapTiles);
                    /*
                    // debug statements
                    System.out.println("Returning the following Board:");
                    System.out.println(swapBoard);

                     */

                    return swapBoard;

                }

            };

            return iter;
        }

        // private helper method used to determine if a given row and col
        // correspond to a corner location
        private boolean isValidTileLocation(int row, int col) {

            boolean validRow = (1 <= row) && (row <= n);
            boolean validCol = (1 <= col) && (col <= n);

            return (validRow && validCol);
        }

    }

    // a board that is obtained by exchanging any pair of tiles
    // note that the zero entry does not count as a tile
    public Board twin() {

        int n = tiles.length;
        int[][] twinTiles = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                twinTiles[i][j] = tiles[i][j];
            }
        }

        // try to swap the first two entries, unless one of them is the zero entry,
        // then swap the last two entries

        int swapRow1 = 0;
        int swapRow2 = 0;

        int swapCol1 = 0;
        int swapCol2 = 1;

        if (tiles[swapRow1][swapCol1] == 0 || tiles[swapRow2][swapCol2] == 0) {
            swapRow1 = n - 1;
            swapRow2 = n - 1;
            swapCol1 = n - 2;
            swapCol2 = n - 1;
        }

        // perform the swap operation
        int tmp = twinTiles[swapRow1][swapCol1];
        twinTiles[swapRow1][swapCol1] = twinTiles[swapRow2][swapCol2];
        twinTiles[swapRow2][swapCol2] = tmp;

        Board twinBoard = new Board(twinTiles);

        return twinBoard;

    }

/* **************************************************************************
 *            * Drawing Methods *
 ***************************************************************************/

    public void draw() {
        drawOuterGrid();
        drawGridSquares();
    }

    public void animatedDraw(double t) {

        // System.out.println();
        // System.out.printf("Starting new animatedDraw(t = %.2f)\n", t);
        drawOuterGrid();
        drawAnimatedGridSquares(t);

    }

    private void drawOuterGrid() {

        StdDraw.setPenColor(Color.BLACK);
        StdDraw.setPenRadius(0.002);

        // first draw rows
        for (int row = 0; row <= size; row++) {
            double y = yBoardMin + row * gridSquareSize;
            StdDraw.line(xBoardMin, y, xBoardMax, y);
        }

        // then draw columns
        for (int col = 0; col <= size; col++) {
            double x = xBoardMin + col * gridSquareSize;
            StdDraw.line(x, yBoardMin, x, yBoardMax);
        }

    }

    private void drawGridSquares() {

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                drawTile(row, col);
            }
        }

    }

    private void drawAnimatedGridSquares(double t) {

        // System.out.printf("Drawing Animated Grid Squares (t = %.2f)\n",t);

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                // System.out.printf("Drawing: [ tile = %d, row = %d, col = %d ]\n", tiles[row][col], row, col);
                // check if this is the swap tile
                if (swapTile == tiles[row][col]) {
                    // System.out.println("Drawing Swap Tile: " + swapTile);
                    drawAnimatedTile(row, col, t);
                } else {
                    // System.out.println("Drawing normally.");
                    drawTile(row, col);

                }

            }
        }

    }

    private void drawTile(int row, int col) {

        if (tiles[row][col] == 0)
            return;

        double squareHalfSize = 0.5 * gridSquareSize;
        double xSquareCenter = xBoardMin + squareHalfSize + col * gridSquareSize;
        double ySquareCenter = yBoardMax - squareHalfSize - row * gridSquareSize;

        // System.out.println("drawing tile : " + tiles[row][col]);
        // System.out.println("image file   : " + tileImages[tiles[row][col] - 1]);
        drawTileXY(tileImages[tiles[row][col] - 1], xSquareCenter, ySquareCenter);


    }

    private void drawAnimatedTile(int row, int col, double t) {

        // System.out.printf("Draw Animated Tile (t = %.2f)\n",t);
        // System.out.printf("[ tile = %d, row = %d, col = %d ]\n", tiles[row][col], row, col);

        if (tiles[row][col] == 0) {
            // System.out.println("Tile value zero. Returning.");
            return;
        }

        double squareHalfSize = 0.5 * gridSquareSize;

        // start position - current position of zero tile
        int startRow = this.zeroRow;
        int startCol = this.zeroCol;
        // System.out.printf("start row : %d, start col : %d", startRow, startCol);

        // end position - current tile position at (row, col)
        int endRow = row;
        int endCol = col;
        // System.out.printf("end row : %d, end col : %d", endRow, endCol);

        // convert to (x,y) coordinates
        double xStart = xBoardMin + squareHalfSize + startCol * gridSquareSize;
        double yStart = yBoardMax - squareHalfSize - startRow * gridSquareSize;

        double xEnd = xBoardMin + squareHalfSize + endCol * gridSquareSize;
        double yEnd = yBoardMax - squareHalfSize - endRow * gridSquareSize;

        // take proportion t of distance from start to end
        double x = xStart + t * (xEnd - xStart);
        double y = yStart + t * (yEnd - yStart);


        drawTileXY(tileImages[tiles[row][col] - 1], x, y);


    }

    private void drawTileXY(String tileImage, double x, double y) {

        // draw tile outline
        double picSize = gridSquareSize - TILE_BUFFER;
        StdDraw.picture(x, y, tileImage, picSize, picSize);

        /*
        StdDraw.setPenColor();
        StdDraw.setPenRadius(0.01);
        StdDraw.filledSquare(x, y, size - TILE_BUFFER);



        // label tile number
        if (val == 0)
            return;

        StdDraw.setPenColor(Color.BLACK);
        StdDraw.setFont(NUMBER_FONT);
        StdDraw.text(x, y, Integer.toString(val));

         */

    }

    public void setScale(double xMin, double yMin, double xMax, double yMax) {

        // System.out.println("yMax - yMin = " + (yMax - yMin));
        // System.out.println("xMax - xMin = " + (xMax - xMin));
        // if ((yMax - yMin) != (xMax - xMin))
        //     throw new IllegalArgumentException("Must use coordinates with a 1:1 ratio");

        this.xBoardMin = xMin + BOARD_BORDER;
        this.xBoardMax = xMax - BOARD_BORDER;
        this.yBoardMin = yMin + BOARD_BORDER;
        this.yBoardMax = yMax - BOARD_BORDER;

        this.gridSquareSize = (xBoardMax - xBoardMin) / 3.0;

    }


/* **************************************************************************
 *            * Distance Methods *
 ***************************************************************************/


    // number of tiles out of place
    public int hamming() {
        return hammingDistance;
    }

    // sum of Manhattan distances between tiles and goal
    public int manhattan() {
        return manhattanDistance;
    }

    // private helper method used to calculate hamming distance once and cache
    private int calculateHammingDistance() {
        int count = 0;
        int n = tiles.length;
        for (int row = 0; row < n; row++) {
            for (int col = 0; col < tiles[row].length; col++) {
                // calculate the "correct" entry based on indices
                int goalEntry = n * row + col + 1;

                /*
                if (goalEntry != n * n && tiles[row][col] != goalEntry) {
                    count++;
                }
                 */

                if (goalEntry != n * n && tiles[row][col] != goalEntry) {
                    count++;
                }

            }
        }
        return count;
    }

    // private helper method used to calculate hamming distance once and cache
    private int calculateManhattanDistance() {

        int totalDistance = 0;

        // sum the distance for each tile in the board
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                totalDistance += manhattanTileDistance(i + 1, j + 1);
            }
        }

        return totalDistance;

    }

    // private method used to calculate the manhattan distance for the tile at
    // the row and column provided
    private int manhattanTileDistance(int row, int col) {

        // int val = tiles[row - 1][col - 1];
        int val = tiles[row - 1][col - 1];
        // int n = tiles.length;
        int n = tiles.length;

        // check for 0 - n^2 condition
        if (val == 0) {
            return 0;
        }

        int goalRow = 1 + (val - 1) / n;
        int goalCol = 1 + (val - 1) % n;

        int dist = Math.abs(row - goalRow) + Math.abs(col - goalCol);

        return dist;
    }

    // is this board the goal board?
    public boolean isGoal() {

        int n = tiles.length;

        for (int row = 0; row < n; row++) {
            for (int col = 0; col < tiles[row].length; col++) {
                // calculate the "correct" entry based on indices
                int goalEntry = n * row + col + 1;
                // check for "zero" entry
                if (goalEntry == n * n) {
                    goalEntry = 0;
                }

                if (tiles[row][col] != goalEntry) {
                    return false;
                }
            }
        }
        return true;
    }

    // does this board equal y?
    public boolean equals(Object y) {

        Board yBoard;

        if (y == null) {
            return false;
        }

        if (y.getClass().equals(Board.class)) {
            // try to cast the generic argument to a Board
            try {
                yBoard = (Board) y;
            }
            catch (ClassCastException e) {
                System.out.println("There was a problem with the Board provided to equals()");
                return false;
            }
        }
        else {
            return false;
        }

        if (tiles.length != yBoard.dimension()) {
            return false;
        }


        // compare each tile
        for (int row = 0; row < tiles.length; row++) {
            for (int col = 0; col < tiles[row].length; col++) {
                if (tiles[row][col] != yBoard.getTile(row, col)) {
                    return false;
                }
            }
        }

        return true;

    }



/* **************************************************************************
 *            * Test Client *
 ***************************************************************************/
    public static void main(String[] args) {
        /*
        // initialize arrangements and goal board
        Board goalBoard = setUpArrangements(0);
        Board board1 = setUpArrangements(1);
        Board board2 = setUpArrangements(2);
        Board board3 = setUpArrangements(3);
        // ---------------------------------------------------------------------

        // display board arrangements
        System.out.println("Goal Board:");
        System.out.println(goalBoard);
        System.out.println();
        System.out.println("Board 1 Arrangement:");
        System.out.println(board1);
        System.out.println();
        System.out.println("Board 2 Arrangement:");
        System.out.println(board2);
        System.out.println();
        System.out.println("Board 3 Arrangement:");
        System.out.println(board3);
        System.out.println();
        // ---------------------------------------------------------------------

        // check individual logic methods
        System.out.println("Checking attributes:");
        System.out.println("Goal Dimension  : " + goalBoard.dimension());
        System.out.println("Arr1?Goal Board : " + board1.isGoal());
        System.out.println("Goal?Goal Board : " + goalBoard.isGoal());
        System.out.println("Arr1 == Goal ?  : " + board1.equals(goalBoard));
        System.out.println("Arr1 == Arr2 ?  : " + board1.equals(board2));
        System.out.println("Arr1 == Arr3 ?  : " + board1.equals(board3));
        System.out.println();
        // ---------------------------------------------------------------------

        System.out.println("Hamming Scores:");
        System.out.println("Goal Board Score : " + goalBoard.hamming());
        System.out.println("Board 1 Score    : " + board1.hamming());
        System.out.println("Board 2 Score    : " + board2.hamming());
        System.out.println("Board 3 Score    : " + board3.hamming());
        System.out.println();
        // ---------------------------------------------------------------------

        System.out.println("Manhattan Scores:");
        System.out.println("Individual Manhattan Tile Scores");
        int mDistG = goalBoard.manhattanTileDistance(2, 3);
        int mDist1 = board1.manhattanTileDistance(1, 2);
        int mDist2 = board2.manhattanTileDistance(3, 3);
        int mDist3A = board3.manhattanTileDistance(1, 2);
        int mDist3B = board3.manhattanTileDistance(3, 1);

        System.out.println("Goal  (2,3) : " + mDistG + "  [Val: " + goalBoard.tiles[1][2] + "]");
        System.out.println("Board1(1,2) : " + mDist1 + "  [Val: " + board1.tiles[0][1] + "]");
        System.out.println("Board2(3,3) : " + mDist2 + "  [Val: " + board2.tiles[2][2] + "]");
        System.out.println("Board3(1,2) : " + mDist3A + "  [Val: " + board3.tiles[0][1] + "]");
        System.out.println("Board3(3,1) : " + mDist3B + "  [Val: " + board3.tiles[2][0] + "]");
        // ---------------------------------------------------------------------

        System.out.println("----------------------------------");
        System.out.println("Board Level Manhattan Scores");
        System.out.println("Goal Board Score : " + goalBoard.manhattan());
        System.out.println("Board 1 Score    : " + board1.manhattan());
        System.out.println("Board 2 Score    : " + board2.manhattan());
        System.out.println("Board 3 Score    : " + board3.manhattan());
        System.out.println();
        // ---------------------------------------------------------------------

        System.out.println("Checking the twin() method");
        System.out.println("Goal Board Twin 1:");
        System.out.println(goalBoard.twin());
        System.out.println("Goal Board Twin 2:");
        System.out.println(goalBoard.twin());
        System.out.println("Board 2 Twin:");
        System.out.println(board2.twin());
        // ---------------------------------------------------------------------

        System.out.println("Goal Board:");
        System.out.println(goalBoard);
        System.out.println();

        System.out.println("Goal Board Neighbors:");
        Iterable<Board> goalNeighbors = goalBoard.neighbors();

        for (Board nbr : goalNeighbors) {
            System.out.println(nbr);
        }
        System.out.println();
        // ---------------------------------------------------------------------

        System.out.println("Board 1:");
        System.out.println(board1);
        System.out.println();

        System.out.println("Board 1 Neighbors:");
        Iterable<Board> board1Neighbors = board1.neighbors();

        for (Board nbr : board1Neighbors) {
            System.out.println(nbr);
        }
        System.out.println();
        // ---------------------------------------------------------------------

        System.out.println("Board 2:");
        System.out.println(board2);
        System.out.println();

        System.out.println("Board 2 Neighbors:");
        Iterable<Board> board2Neighbors = board2.neighbors();

        for (Board nbr : board2Neighbors) {
            System.out.println(nbr);
        }
        System.out.println();
        // ---------------------------------------------------------------------

        System.out.println("Board 3:");
        System.out.println(board3);
        System.out.println();

        System.out.println("Board 3 Neighbors:");
        Iterable<Board> board3Neighbors = board3.neighbors();

        for (Board nbr : board3Neighbors) {
            System.out.println(nbr);
        }
        System.out.println();

         */

        Board b = new Board("puzzles/puzzle3x3-10.txt");
        System.out.println(b);

        b.zeroSwapTile(1,0);
        System.out.println(b);

        b.zeroSwapTile(0,1);
        System.out.println(b);
    }
}
