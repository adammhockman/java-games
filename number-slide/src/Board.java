import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdRandom;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Class depicting an n x n number slide puzzle board.
 * Maintains the current state of the game by updating the references
 * to tiles in an n by n array.
 */
public class Board {

    // the distance between tiles
    private static final double TILE_BUFFER = 0.10;

    // size of tile grid (number of rows and columns)
    private final int size;

    // (row, col) indexing of tile references
    private final Tile[][] tiles;

    // cache the location of zero-tile for easy retrieval and animation
    private int zeroRow;
    private int zeroCol;

    // cache the most recently swapped tile (for drawing with animations)
    private Tile swapTile;

    // cache the distance used in the A* solver
    private int manhattanDistance;

    // used for drawing the board
    private double xBoardMin;
    private double xBoardMax;
    private double yBoardMin;
    private double yBoardMax;

    private boolean inverted;

    private double gridSquareSize;

/* **************************************************************************
 *            * Constructors / Initialization *
 ***************************************************************************/

    /**
     * Main constructor takes an array of Tiles, performs a deep copy,
     * and instantiates with the copied reference.
     * @param tiles an array of Tile depicting the board configuration
     */
    public Board(Tile[][] tiles) {

        // deep copy the input
        this.size = tiles.length;

        // deep copy the tiles
        this.tiles = new Tile[size][size];
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (tiles[row][col] == null) {
                    zeroRow = row;
                    zeroCol = col;
                    // initialize swapTile to null
                    swapTile = null;
                } else {
                    this.tiles[row][col] = tiles[row][col].copy();
                }
            }
        }

        // default inverted to false
        this.inverted = false;

        // store the distance used in the Solver A* algorithm
        cacheDistance();

    }

    /**
     * Constructor takes the name of a board configuration and loads it from
     * disk.
     * @param puzzleFile String puzzle file name
     */
    public Board(String puzzleFile) {

        In in = new In(puzzleFile);
        int n = Integer.parseInt(in.readLine());

        this.size = n;
        this.tiles = new Tile[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                int entry = in.readInt();
                if (entry == 0) {
                    tiles[i][j] = null;
                    zeroRow = i;
                    zeroCol = j;
                    swapTile = null;
                } else
                    tiles[i][j] = new Tile(entry, n);
            }
        }

        // default inverted to false
        this.inverted = false;

        // store the distance used in the Solver A* algorithm
        cacheDistance();

    }

    /**
     * Method used to set several relevant drawing parameters.
     * Sets the scale (min and max) for the area where the Board displays.
     * Also computes the grid square size.
     *
     * @param xMin double x-coordinate of left side
     * @param yMin double y-coordinate of bottom
     * @param xMax double x-coordinate of right side
     * @param yMax double y-coordinate of top
     */
    public void setScale(double xMin, double yMin, double xMax, double yMax) {

        this.xBoardMin = xMin;
        this.xBoardMax = xMax;
        this.yBoardMin = yMin;
        this.yBoardMax = yMax;

        this.gridSquareSize = (xBoardMax - xBoardMin) / ((double) size);

    }

    /**
     * Used to flip from inverted to non-inverted when drawing the board.
     * This is a toggle-switch for the board colors.
     */
    public void invert() {

        this.inverted = !inverted;

    }


/* **************************************************************************
 *            * Accessor Methods *
 ***************************************************************************/

    /**
     * Accessor method used to obtain the current row of the zero tile.
     * @return int row of zero tile
     */
    public int getZeroRow() {

        return zeroRow;

    }

    /**
     * Accessor method used to obtain the current column of the zero tile.
     * @return int column of zero tile
     */
    public int getZeroCol() {

        return zeroCol;

    }

    /**
     * Accessor method used to extract a reference to the tile located at
     * (row, col) provided.
     * @param row int row value of tile
     * @param col int column value of tile
     * @return reference to Tile at that location
     */
    public Tile getTile(int row, int col) {

        return tiles[row][col];

    }

    /**
     * Accessor method used to obtain the grid size.
     * @return int number or rows / columns
     */
    public int dimension() {

        return tiles.length;

    }

    /**
     * Accessor method used to obtain the manhattan distance for the current board.
     * Manhattan - sum of Manhattan distances between tiles and goal
     * @return int board manhattan score
     */
    public int manhattan() {

        return manhattanDistance;

    }

    // DEBUG: string representation of this board
    /*
    public String toString() {
        // visual depiction of tile arrangement
        StringBuilder tilesString = new StringBuilder();

        // first print the board size on line 1
        tilesString.append(tiles.length);
        tilesString.append("\n");

        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                if (tiles[i][j] == null)
                    tilesString.append(String.format("%2d ", 0));
                else
                    tilesString.append(String.format("%2d ", tiles[i][j].val()));
            }
            tilesString.append("\n");
        }

        return tilesString.toString();
    }
     */


/* **************************************************************************
 *            * Tile Swap Methods *
 ***************************************************************************/

    /**
     * Methods attempts to take the tile at location (row, col) and swap with
     * the zero tile (blank space). Returns false if not adjacent to blank.
     *
     * @param row int row of tile to swap with zero tile
     * @param col int column of tile to swap with zero tile
     * @return false if the tile can't be swapped, true otherwise
     */
    public boolean zeroSwapTile(int row, int col) {

        // trivial case: user clicks zero tile - return false
        if (row == zeroRow && col == zeroCol)
            return false;

        // if the tile is not adjacent to zero, return false
        if (!spaceAdjacent(row, col))
            return false;

        // at this point we perform the swap operation:
        // 1 - cache the tile value being swapped for later reference in animating
        swapTile = tiles[row][col];

        // 2 - set zero tile to swap value and swap tile location to null
        tiles[zeroRow][zeroCol] = swapTile;
        tiles[row][col] = null;

        // 3 - update zero position caching
        zeroRow = row;
        zeroCol = col;

        // cacheDistance();
        return true;

    }

    /**
     * Private helper method used to check it the tile at (row, col) is
     * adjacent to the zero-tile (blank space)
     * @param row int row of the tile to check
     * @param col int column of the tile to check
     * @return true if tile at (row, col) is adjacent to blank space
     */
    private boolean spaceAdjacent(int row, int col) {

        // check all neighboring boards to see if the zero-tile appears at (row, col)
        for (Board board : neighbors()) {
            if (board.getTile(row, col) == null)
                return true;
        }
        return false;

    }


/* **************************************************************************
 *            * Iterable Methods *
 ***************************************************************************/

    /**
     * Method used to generate all the "neighbors" of a board.
     * A "neighbor" is any one of the boards that results from
     * swapping the blank space with one of the adjacent tiles.
     * @return Iterable of Boards depicting neighboring moves
     */
    public Iterable<Board> neighbors() {

        return new NeighborsIter();

    }

    /**
     * Private nested class used to define an Iterable for the neighbors()
     * implementation.
     * Must provide a way of iterating without altering the current board.
     */
    private class NeighborsIter implements Iterable<Board> {

        private int totalNeighbors;
        private int currentNeighbor;
        private final int[] neighborRows;
        private final int[] neighborCols;

        private int zeroRow;
        private int zeroCol;

        // constructor
        private NeighborsIter() {

            this.totalNeighbors = 0;
            this.currentNeighbor = 0;
            this.neighborRows = new int[4];
            this.neighborCols = new int[4];

            // first we need to determine the row and column of the "0" entry
            this.zeroRow = -1;
            this.zeroCol = -1;

            for (int row = 0; row < size; row++) {
                for (int col = 0; col < size; col++) {
                    if (tiles[row][col] == null) {
                        this.zeroRow = row + 1;
                        this.zeroCol = col + 1;
                    }
                }
            }

        }

        public Iterator<Board> iterator() {

            // store the location of the row positions for each neighbor
            // determine the number of neighbors based on row / column logic
            int neighborRow, neighborCol;

            // left neighbor
            neighborRow = zeroRow;
            neighborCol = zeroCol - 1;
            if (isValidTileLocation(neighborRow, neighborCol)) {
                neighborRows[totalNeighbors] = neighborRow;
                neighborCols[totalNeighbors] = neighborCol;
                totalNeighbors++;
            }

            // right neighbor
            neighborRow = zeroRow;
            neighborCol = zeroCol + 1;
            if (isValidTileLocation(neighborRow, neighborCol)) {
                neighborRows[totalNeighbors] = neighborRow;
                neighborCols[totalNeighbors] = neighborCol;
                totalNeighbors++;
            }

            // top neighbor
            neighborRow = zeroRow - 1;
            neighborCol = zeroCol;
            if (isValidTileLocation(neighborRow, neighborCol)) {
                // System.out.println("Neighbor Location: VALID");
                neighborRows[totalNeighbors] = neighborRow;
                neighborCols[totalNeighbors] = neighborCol;
                totalNeighbors++;
            }

            // bottom neighbor
            neighborRow = zeroRow + 1;
            neighborCol = zeroCol;
            if (isValidTileLocation(neighborRow, neighborCol)) {
                // System.out.println("Neighbor Location: VALID");
                neighborRows[totalNeighbors] = neighborRow;
                neighborCols[totalNeighbors] = neighborCol;
                totalNeighbors++;
            }

            // set current neighbor to 1 (we are labelling neighbors 1 -> n)
            if (totalNeighbors > 0)
                currentNeighbor = 1;

            return new Iterator<>() {

                public boolean hasNext() {

                    if (totalNeighbors == 0)
                        return false;
                    else
                        return (currentNeighbor <= totalNeighbors);

                }

                public Board next() {

                    int neighborRow = neighborRows[currentNeighbor - 1];
                    int neighborCol = neighborCols[currentNeighbor - 1];
                    currentNeighbor++;

                    return swap(neighborRow, neighborCol);

                }

                // private helper method used to create a new Board that has the
                // zero entry swapped with the entry at the row and col provided
                private Board swap(int row, int col) {

                    // create a new tile arrangement and copy from the Board
                    Tile[][] swapTiles = new Tile[size][size];
                    for (int i = 0; i < size; i++) {
                        for (int j = 0; j < size; j++) {
                            if (tiles[i][j] != null)
                                swapTiles[i][j] = tiles[i][j].copy();
                            else
                                swapTiles[i][j] = null;
                        }
                    }

                    // put the entry into the current zero entry position
                    swapTiles[zeroRow - 1][zeroCol - 1] = swapTiles[row - 1][col - 1];

                    // put the zero entry where (row, col) is entry from above
                    swapTiles[row - 1][col - 1] = null;

                    return new Board(swapTiles);

                }
            };
        }

        // private helper method used to determine if a given row and col
        // correspond to a corner location
        private boolean isValidTileLocation(int row, int col) {

            boolean validRow = (1 <= row) && (row <= size);
            boolean validCol = (1 <= col) && (col <= size);

            return (validRow && validCol);
        }

    }

    /**
     * Used to obtain a twin of the instance board.
     * A twin is any board that is obtained by exchanging any pair of tiles.
     * Note: The zero entry (blank space) does not count as a tile
     * @return reference to a twin Board
     */
    public Board twin() {

        Tile[][] twinTiles = new Tile[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (tiles[i][j] != null)
                    twinTiles[i][j] = tiles[i][j].copy();
                else
                    twinTiles[i][j] = null;
            }
        }

        // try to swap the first two entries, unless one of them is the zero entry,
        // then swap the last two entries
        int swapRow1 = 0;
        int swapRow2 = 0;

        int swapCol1 = 0;
        int swapCol2 = 1;

        if (tiles[swapRow1][swapCol1] == null || tiles[swapRow2][swapCol2] == null) {
            swapRow1 = size - 1;
            swapRow2 = size - 1;
            swapCol1 = size - 2;
            swapCol2 = size - 1;
        }

        // perform the swap operation
        Tile tmp = twinTiles[swapRow1][swapCol1];
        twinTiles[swapRow1][swapCol1] = twinTiles[swapRow2][swapCol2];
        twinTiles[swapRow2][swapCol2] = tmp;

        return new Board(twinTiles);

    }


/* **************************************************************************
 *            * Simple Drawing Methods *
 ***************************************************************************/

    /**
     * Main draw method. Called by the ambient display and writes to StdDraw
     */
    public void draw() {

        // DEBUG - draw grid layout
        // drawOuterGrid();

        // draw grid squares
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++)
                drawTile(row, col);
        }

    }

    /**
     * Private helper method used to draw individual tile at the location provided.
     * Basic design: gold border and text, black inside
     * Inverted basic design: black border and text, gold inside
     *
     * @param row int row of tile to be drawn
     * @param col int column of tile to be drawn
     */
    private void drawTile(int row, int col) {

        if (tiles[row][col] == null)
            return;

        double squareHalfSize = 0.5 * gridSquareSize;
        double xCenter = xBoardMin + squareHalfSize + col * gridSquareSize;
        double yCenter = yBoardMax - squareHalfSize - row * gridSquareSize;

        tiles[row][col].draw(xCenter, yCenter, gridSquareSize * (1.0 - TILE_BUFFER), inverted);

    }

    
/* **************************************************************************
 *            * Animated Drawing Methods *
 ***************************************************************************/

    // ANIMATED
    /*
    public void animatedDraw(double t) {

        drawOuterGrid();
        drawAnimatedGridSquares(t);

    }
     */

    // ANIMATED METHOD
    /*
    private void drawValue(double t) {

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
    */

    // DEBUG - testing the layout
    /*
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
     */

    // ANIMATED
    /*
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
     */




    // ANIMATED
    /*
    private void drawAnimatedTile(int row, int col, double t) {

        if (tiles[row][col] == null) {
            return;
        }

        tiles[row][col].drawAnimated(t);

    }
     */


/* **************************************************************************
 *            * Distance Methods *
 ***************************************************************************/

    /**
     * Public method called by the constructor and update methods to cache the
     * A* search algorithm distance heuristic.
     * This implementation uses the manhattan distance.
     * The manhattan distance of a board is the sum of the individual tile
     * manhattan distances.
     */
    public void cacheDistance() {

        int totalDistance = 0;

        // sum the distance for each tile in the board
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++)
                totalDistance += manhattanTileDistance(i, j);
        }

        this.manhattanDistance = totalDistance;

    }

    /**
     * Private helper method used to calculate the manhattan distance of a single
     * tile location.
     *
     * @param row int row of tile to calculate
     * @param col int column of tile to calculate
     * @return int manhattan distance of tile at (row,col)
     */
    private int manhattanTileDistance(int row, int col) {

        // check for zero tile (empty space) location
        if (tiles[row][col] == null)
            return 0;

        // subtract 1 to account for empty space coming last and 1 going first
        int val = tiles[row][col].val() - 1;

        // get distance from goal horizontally and vertically
        int goalRow = val / size;
        int goalCol = val % size;

        return Math.abs(row - goalRow) + Math.abs(col - goalCol);

    }

    /**
     * Used to establish equality between the current board state and the goal state.
     * @return true if the goal is in the target position
     */
    public boolean isGoal() {

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                // calculate the "correct" entry based on indices
                // shift 1 to account for empty space going last
                int goalEntry = (size * row + col) + 1;

                // we don't check the final location (empty space)
                if (goalEntry == size * size)
                    break;

                // if the Tile is null (i.e. the empty space) before the final
                // index, return false
                if (tiles[row][col] == null)
                    return false;

                // finally check based on value
                if (tiles[row][col].val() != goalEntry)
                    return false;
            }
        }
        return true;
    }

    /**
     * Used to compare two board instances to see if their tile configurations are the same.
     * Used in the Solver algorithm to optimize performance.
     * @param y Object is cast immediately to type Board
     * @return true if tile configurations are identical (based on tile values)
     */
    public boolean equals(Object y) {

        // cast y to type Board
        if (y == null || !y.getClass().equals(Board.class))
            return false;
        Board yBoard = (Board) y;

        // check size compatibility
        if (size != yBoard.dimension())
            return false;

        // compare each tile by value
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                Tile a = tiles[row][col];
                Tile b = yBoard.getTile(row, col);

                // if either is null, but both aren't
                if (a == null || b == null) {
                    if (!(a == null && b == null))
                        return false;
                }
                else if (a.val() != b.val())
                    return false;
            }
        }
        return true;

    }


/* **************************************************************************
 *    Static methods
 * *************************************************************************/

    /**
     * Used to generate a (**solvable**) new board of size provided.
     * Random in the 3x3 case, but pre-computed in the 4x4 and 5x5 case
     * @param size int grid size number of rows and columns
     * @return Board reference to new board created
     */
    public static Board createBoard(int size) {

        Tile[][] tiles = new Tile[size][size];
        int TOTAL_BOARDS = 100;

        if (size == 5) {
            int puzzleNumber = StdRandom.uniformInt(TOTAL_BOARDS);
            String puzzleFile = String.format("puzzles/puzzle5x5/medium/puzzle5x5_medium%02d.txt", puzzleNumber);
            return new Board(puzzleFile);
        }

        if (size == 4) {
            int totalBoards = 74;
            int puzzleNumber = StdRandom.uniformInt(totalBoards) + 1;
            String puzzleFile = String.format("puzzles/puzzle4x4/medium/puzzle4x4_medium%02d.txt", puzzleNumber);
            return new Board(puzzleFile);
        }

        if (size == 3) {
            ArrayList<Integer> tileValues = new ArrayList<>();
            for (int i = 0; i < size * size; i++) {
                tileValues.add(i);
            }

            for (int row = 0; row < size; row++) {
                for (int col = 0; col < size; col++) {
                    int randIndex = StdRandom.uniformInt(tileValues.size());
                    int val = tileValues.remove(randIndex);
                    if (val == 0)
                        tiles[row][col] = null;
                    else
                        tiles[row][col] = new Tile(val, size);
                }
            }

            Board testBoard = new Board(tiles);
            Solver solver = new Solver(testBoard);
            if (!solver.isSolvable())
                testBoard = testBoard.twin();

            return testBoard;
        }

        return null;

    }

    /**
     * Private helper method used to generate new puzzle boards.
     * Creates a game board in the goal position. This can then
     * be used to perform random swaps to achieve different boards
     * of various difficulty level.
     * @param size int number of rows and columns
     * @return reference to the goal board
     */
    public static Board identity(int size) {

        Tile[][] tiles = new Tile[size][size];

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                int goalEntry = row * size + col + 1;
                tiles[row][col] = new Tile(goalEntry, size);
            }
        }
        tiles[size-1][size-1] = null;
        return new Board(tiles);

    }

}