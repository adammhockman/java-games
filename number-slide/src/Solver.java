/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

import java.util.Comparator;

public class Solver {

    // caching variables
    private int moves;
    private boolean solvable;
    private Stack<Board> solution;

    // optimized node
    private static class Node {
        Board board;
        int moves;
        int priority;
        Node prev;
    }

    public Solver(Board initial) {

        if (initial == null)
            throw new IllegalArgumentException("Initial board provided is null");

        moves = -1;
        solvable = true;
        solution = new Stack<>();
        MinPQ<Node> pq = init(initial);
        MinPQ<Node> pqTwin = init(initial.twin());

        while (true) {
            Node searchNode = pq.delMin();
            Node prevNode = searchNode.prev;
            Board searchBoard = searchNode.board;

            if (searchBoard.isGoal()) {
                solvable = true;
                moves = searchNode.moves;
                solution = findRoot(searchNode);
                break;
            }

            for (Board nbr : searchBoard.neighbors()) {
                if (prevNode == null) {
                    Node tmp = new Node();
                    tmp.board = nbr;
                    tmp.prev = searchNode;
                    tmp.moves = searchNode.moves + 1;
                    tmp.priority = tmp.moves + nbr.manhattan();
                    pq.insert(tmp);
                } else {
                    if (!prevNode.board.equals(nbr)) {
                        Node tmp = new Node();
                        tmp.prev = searchNode;
                        tmp.board = nbr;
                        tmp.moves = searchNode.moves + 1;
                        tmp.priority = tmp.moves + nbr.manhattan();
                        pq.insert(tmp);
                    }
                }
            }

            Node twinSearchNode = pqTwin.delMin();
            Node twinPrevNode = twinSearchNode.prev;
            Board twinSearchBoard = twinSearchNode.board;

            if (twinSearchBoard.isGoal()) {
                solvable = false;
                break;
            }

            for (Board tNbr : twinSearchBoard.neighbors()) {
                if (twinPrevNode == null) {
                    Node tmp = new Node();
                    tmp.prev = twinSearchNode;
                    tmp.board = tNbr;
                    tmp.moves = twinSearchNode.moves + 1;
                    tmp.priority = tmp.moves + tNbr.manhattan();
                    pqTwin.insert(tmp);
                } else {
                    if (!twinPrevNode.board.equals(tNbr)) {
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
    }



    public boolean isSolvable() {
        return solvable;
    }

    public int moves() {
        return moves;
    }

    public Iterable<Board> solution() {
        if (!solvable)
            return null;
        return solution;
    }

    private Comparator<Node> priority() {
        return new ByPriority();
    }

    private static class ByPriority implements Comparator<Node> {
        public int compare(Node x, Node y) {
            return Integer.compare(x.priority, y.priority);
        }
    }


    private MinPQ<Node> init(Board root) {

        Node rootNode = new Node();
        rootNode.board = root;
        rootNode.moves = 0;
        rootNode.priority = root.manhattan();
        rootNode.prev = null;
        MinPQ<Node> pq = new MinPQ<>(priority());
        pq.insert(rootNode);
        return pq;

    }

    private Stack<Board> findRoot(Node leaf) {
        Stack<Board> path = new Stack<>();
        Node tmp = leaf;
        do {
            path.push(tmp.board);
            tmp = tmp.prev;
        } while (tmp != null);
        return path;
    }

/* **************************************************************************
             * Test Client
 ***************************************************************************/

    // test client (see below)
    public static void main(String[] args) {

        // create initial board from file
        String puzzleFileName = "puzzles/puzzle3x3-10.txt";

        System.out.println("Welcome");

        In in = new In(puzzleFileName);
        int n = Integer.parseInt(in.readLine());
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                tiles[i][j] = in.readInt();
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

}
