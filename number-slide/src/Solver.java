import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;

import java.util.Comparator;

/**
 * Class implements the A* search algorithm for the Number Slide
 * puzzle game.
 * This implementation uses the manhattan distance to find the solution
 * to a given board configuration.
 */
public class Solver {

    // caching variables
    private int moves;
    private boolean solvable;
    private Stack<Board> solution;

    /**
     * Private inner class Node used to implement and optimize the A*
     * search algorithm.
     * All fields are directly assigned and accessed.
     * 0 - Board has associated with it:
     * 1 - How many moves did it take to get to this board configuration
     *     from the initial board
     * 2 - Manhattan priority = moves + board's manhattan score
     * 3 - Reference to the board that resulted in getting to this board
     */
    private static class Node {
        Board board;
        int moves;
        int priority;
        Node prev;
    }


/* **************************************************************************
 *            * Constructor (A* Implementation) *
 ***************************************************************************/

    /**
     * Implementation of the A* search algorithm to find (and show the steps
     * leading to) the solution.
     * Uses the manhattan priority function:
     *   Priority(B) = Manhattan(B) + MovesFromRoot(B)
     *
     * @param initial initial Board to solve (root)
     */
    public Solver(Board initial) {

        if (initial == null)
            throw new IllegalArgumentException("Initial board provided is null");

        // instantiate and initialize all search parameters
        moves = -1;
        solvable = true;
        solution = new Stack<>();
        MinPQ<Node> pq = init(initial);
        MinPQ<Node> pqTwin = init(initial.twin());

        // algorithm will break once finished
        while (true) {
            // de-queue the next node
            Node searchNode = pq.delMin();
            Node prevNode = searchNode.prev;
            Board searchBoard = searchNode.board;

            // if we are at the goal, break
            if (searchBoard.isGoal()) {
                solvable = true;
                moves = searchNode.moves;
                solution = findRoot(searchNode);
                break;
            }

            // for each neighboring board of the current board
            for (Board nbr : searchBoard.neighbors()) {
                // optimization condition - don't place the board you just
                // came from back onto the queue
                if (prevNode == null || !prevNode.board.equals(nbr)) {
                    Node tmp = new Node();
                    tmp.board = nbr;
                    tmp.prev = searchNode;
                    tmp.moves = searchNode.moves + 1;
                    tmp.priority = tmp.moves + nbr.manhattan();
                    pq.insert(tmp);
                }
            }

            // create twin search node and run in parallel to ensure runoff
            // condition when a board is unsolvable
            Node twinSearchNode = pqTwin.delMin();
            Node twinPrevNode = twinSearchNode.prev;
            Board twinSearchBoard = twinSearchNode.board;

            // stop if twin board reaches the goal first
            if (twinSearchBoard.isGoal()) {
                solvable = false;
                break;
            }

            // otherwise process just as above
            for (Board tNbr : twinSearchBoard.neighbors()) {
                if (twinPrevNode == null || !twinPrevNode.board.equals(tNbr)) {
                    Node tmp = new Node();
                    tmp.prev = twinSearchNode;
                    tmp.board = tNbr;
                    tmp.moves = twinSearchNode.moves + 1;
                    tmp.priority = tmp.moves + tNbr.manhattan();
                    pqTwin.insert(tmp);
                }
            }
        }
    }


/* **************************************************************************
 *            * Accessor Methods *
 ***************************************************************************/

    /**
     * Accessor method used to check whether a Solver instance is solvable.
     * @return true if root board found the goal board first in constructor
     */
    public boolean unsolvable() {

        return (!solvable);

    }

    /**
     * Returns an Iterable of Boards leading from the initial (root)
     * board to the solution board, if solvable.
     * Returns null if Board is unsolvable.
     *
     * @return Iterable of Boards leading to solution from root
     */
    public Iterable<Board> solution() {
        if (!solvable)
            return null;
        return solution;
    }

    /**
     * Creates and returns an instance of the Comparator ByPriority.
     * This allows for comparison of two custom inner search nodes.
     *
     * @return ByPriority a Comparator for our custom search node
     */
    private Comparator<Node> priority() {

        return new ByPriority();

    }

    /**
     * Private inner class used to create a comparator for our custom inner class Node.
     * Compares by priority of Node.
     */
    private static class ByPriority implements Comparator<Node> {
        public int compare(Node x, Node y) {
            return Integer.compare(x.priority, y.priority);
        }
    }

    /**
     * Private helper method used to implement the search algorithm.
     * Initializes the root to be the Board provided,
     * @param rootBoard the board to begin the search
     * @return MinPQ of custom Node used to execute the algorithm in the constructor
     */
    private MinPQ<Node> init(Board rootBoard) {

        Node rootNode = new Node();
        rootNode.board = rootBoard;
        rootNode.moves = 0;
        rootNode.priority = rootBoard.manhattan();
        rootNode.prev = null;
        MinPQ<Node> pq = new MinPQ<>(priority());
        pq.insert(rootNode);
        return pq;

    }

    /**
     * Private helper method used to return the solution path from the Board
     * provided (root) to the solution.
     *
     * @param leaf the Node to begin the path to solution from
     * @return Stack representing the sequence of Boards from leaf to solution
     */
    private Stack<Board> findRoot(Node leaf) {

        Stack<Board> solution = new Stack<>();
        Node tmp = leaf;
        do {
            solution.push(tmp.board);
            tmp = tmp.prev;
        } while (tmp != null);
        return solution;

    }


/* **************************************************************************
             * Debug / Test Client
 ***************************************************************************/

    // DEBUG: Test Client
    /*
    public static void main(String[] args) {

        // create initial board from file
        String puzzleFileName = "puzzles/puzzle3x3-10.txt";

        System.out.println("Welcome");

        In in = new In(puzzleFileName);
        int n = Integer.parseInt(in.readLine());
        Tile[][] tiles = new Tile[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                tiles[i][j] = new Tile(in.readInt(), n);
        Board initial = new Board(tiles);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }


    }
     */

}
