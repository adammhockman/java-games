import java.awt.*;

public class TetrisDisplay {


    private final TetrisBoard board;

    // font declarations
    private static final Font NEXT_PIECE_FONT = new Font("Courier", Font.BOLD, 24);
    private static final Color NEXT_PIECE_FONT_COLOR = Color.WHITE;

    private static final Font SCORE_FONT = new Font("Courier", Font.BOLD, 24);
    private static final Color SCORE_COLOR = Color.WHITE;


    // background image
    private static final String BACKGROUND_PICTURE_FADE = "graphics/tetris_display_newgame.png";
    private static final String BACKGROUND_PICTURE = "graphics/tetris_display.png";
    private static final String NEW_GAME_STONE_PICTURE = "graphics/newgame_stone.png";

    // canvas size
    private static final int CANVAS_WIDTH_PIXELS = 800;
    private static final int CANVAS_HEIGHT_PIXELS = 800;

    // tetris board layout
    private static final double boardFrameXMin = 3.0 / 24.0;
    private static final double boardFrameXMax = 13.0 / 24.0;
    private static final double boardFrameYMin = 2.0 / 24.0;
    private static final double boardFrameYMax = 22.0 / 24.0;

    // next piece panel (6x8)
    private static final double pieceFrameBuffer = 0.25 / 24.0;
    private static final double pieceFrameXMin = 16.0 / 24.0 - pieceFrameBuffer;
    private static final double pieceFrameXMax = 22.0 / 24.0 - pieceFrameBuffer;
    private static final double pieceFrameYMin = 14.0 / 24.0 - pieceFrameBuffer;
    private static final double pieceFrameYMax = 22.0 / 24.0 - pieceFrameBuffer;

    // mini-grid (4x4) within next piece panel
    private static final double miniGridXMin = pieceFrameXMin + (1.0 / 24.0);
    private static final double miniGridXMax = pieceFrameXMax - (1.0 / 24.0);
    private static final double miniGridYMin = pieceFrameYMin + (1.0 / 24.0);
    private static final double miniGridYMax = pieceFrameYMax - (3.0 / 24.0);

    // score panel
    private static final double scoreFrameXMin = 16.0 / 24.0 - pieceFrameBuffer;
    private static final double scoreFrameXMax = 19.0 / 24.0 - pieceFrameBuffer;
    private static final double scoreFrameYMin = 4.0 / 24.0;
    private static final double scoreFrameYMax = 7.0 / 24.0;


    public TetrisDisplay(int boardHeight, int boardWidth) {

        // create a new TetrisBoard with the provided board height and width
        this.board = new TetrisBoard(boardHeight, boardWidth);

        // set the board viewing parameters
        board.setScale(boardFrameXMin, boardFrameYMin, boardFrameXMax, boardFrameYMax);

        // generate the initial piece to drop
        board.generatePiece();

        // initialize StdDraw parameters
        StdDraw.enableDoubleBuffering();
        StdDraw.setCanvasSize(CANVAS_WIDTH_PIXELS, CANVAS_HEIGHT_PIXELS);
        StdDraw.setTitle("Tetris");
        StdDraw.setScale(0.0, 1.0);


    }

    public synchronized void refreshNewGame(double a) {

        StdDraw.clear();
        StdDraw.picture(0.5, 0.5, BACKGROUND_PICTURE_FADE);
        drawAdornments(a);
        board.draw(a);
        drawPieceFrame(a);
        drawScoreFrame(a);
        StdDraw.picture(0.5, 0.5, NEW_GAME_STONE_PICTURE);
        StdDraw.show();

    }

    public synchronized void refresh() {

        StdDraw.clear();
        StdDraw.picture(0.5, 0.5, BACKGROUND_PICTURE);
        drawAdornments();
        board.draw();
        drawPieceFrame();
        drawScoreFrame();
        StdDraw.show();

    }

    private synchronized void drawPieceFrame() {

        // draw label
        double frameTextX = 0.5 * (pieceFrameXMin + pieceFrameXMax);
        double frameTextY = pieceFrameYMin + (7.0 / 8.0) * (pieceFrameYMax - pieceFrameYMin);
        String frameText = "Next Piece";

        StdDraw.setFont(NEXT_PIECE_FONT);
        StdDraw.setPenColor(NEXT_PIECE_FONT_COLOR);
        StdDraw.text(frameTextX, frameTextY, frameText);

        // draw the mini-grid
        drawMiniGrid();

        // draw the active piece in a 4x4 grid below
        drawNextPiece();

    }

    private synchronized void drawPieceFrame(double a) {

        // draw label
        double frameTextX = 0.5 * (pieceFrameXMin + pieceFrameXMax);
        double frameTextY = pieceFrameYMin + (7.0 / 8.0) * (pieceFrameYMax - pieceFrameYMin);
        String frameText = "Next Piece";

        StdDraw.setFont(NEXT_PIECE_FONT);
        Color fade = new Color(NEXT_PIECE_FONT_COLOR.getRed(), NEXT_PIECE_FONT_COLOR.getGreen(), NEXT_PIECE_FONT_COLOR.getBlue(), (int)(a * 255));
        StdDraw.setPenColor(fade);
        StdDraw.text(frameTextX, frameTextY, frameText);

        // draw the mini-grid
        drawMiniGrid(a);

        // draw the active piece in a 4x4 grid below
        drawNextPiece(a);

    }

    private synchronized void drawMiniGrid() {

        int gridSize = 4;

        // draw grid lines
        double squareSize = 1.0 / 24.0;
        StdDraw.setPenColor(Color.LIGHT_GRAY);
        StdDraw.setPenRadius(0.001);
        // draw horizontal lines
        for (int row = 0; row <= gridSize; row++) {
            double y = miniGridYMin + row * squareSize;
            StdDraw.line(miniGridXMin, y, miniGridXMax, y);
        }
        // draw vertical lines
        for (int col = 0; col <= gridSize; col++) {
            double x = miniGridXMin + col * squareSize;
            StdDraw.line(x, miniGridYMin, x, miniGridYMax);
        }

    }

    private synchronized void drawMiniGrid(double a) {

        int gridSize = 4;

        // draw grid lines
        double squareSize = 1.0 / 24.0;

        Color lg = Color.LIGHT_GRAY;
        Color fade = new Color(lg.getRed(), lg.getGreen(), lg.getBlue(), (int)(a * 255));


        StdDraw.setPenColor(fade);
        StdDraw.setPenRadius(0.001);
        // draw horizontal lines
        for (int row = 0; row <= gridSize; row++) {
            double y = miniGridYMin + row * squareSize;
            StdDraw.line(miniGridXMin, y, miniGridXMax, y);
        }
        // draw vertical lines
        for (int col = 0; col <= gridSize; col++) {
            double x = miniGridXMin + col * squareSize;
            StdDraw.line(x, miniGridYMin, x, miniGridYMax);
        }

    }

    private void drawNextPiece() {

        TetrisPiece nextPiece = board.getNextPiece();

        if (nextPiece == null)
            return;

        char nextPieceType = nextPiece.getPieceType();

        // get piece coords
        int[] pieceCoords = new int[4];

        switch (nextPieceType) {
            case 'O':
                pieceCoords[0] = 5;
                pieceCoords[1] = 6;
                pieceCoords[2] = 9;
                pieceCoords[3] = 10;
                break;
            case 'I':
                pieceCoords[0] = 1;
                pieceCoords[1] = 5;
                pieceCoords[2] = 9;
                pieceCoords[3] = 13;
                break;
            case 'S':
                pieceCoords[0] = 6;
                pieceCoords[1] = 7;
                pieceCoords[2] = 9;
                pieceCoords[3] = 10;
                break;
            case 'Z':
                pieceCoords[0] = 4;
                pieceCoords[1] = 5;
                pieceCoords[2] = 9;
                pieceCoords[3] = 10;
                break;
            case 'L':
                pieceCoords[0] = 1;
                pieceCoords[1] = 5;
                pieceCoords[2] = 9;
                pieceCoords[3] = 10;
                break;
            case 'J':
                pieceCoords[0] = 2;
                pieceCoords[1] = 6;
                pieceCoords[2] = 9;
                pieceCoords[3] = 10;
                break;
            case 'T':
                pieceCoords[0] = 2;
                pieceCoords[1] = 5;
                pieceCoords[2] = 6;
                pieceCoords[3] = 10;
                break;
            default:
                throw new UnsupportedOperationException("Next piece type invalid: " + nextPieceType);
        }

        // print each coordinate as an (i,j) entry referring to row i col j
        for (int pieceCoord : pieceCoords) {
            int row = pieceCoord / 4;
            int col = pieceCoord % 4;
            drawNextPieceSquare(row, col);
        }

    }

    private void drawNextPiece(double a) {

        TetrisPiece nextPiece = board.getNextPiece();

        if (nextPiece == null)
            return;

        char nextPieceType = nextPiece.getPieceType();

        // get piece coords
        int[] pieceCoords = new int[4];

        switch (nextPieceType) {
            case 'O':
                pieceCoords[0] = 5;
                pieceCoords[1] = 6;
                pieceCoords[2] = 9;
                pieceCoords[3] = 10;
                break;
            case 'I':
                pieceCoords[0] = 1;
                pieceCoords[1] = 5;
                pieceCoords[2] = 9;
                pieceCoords[3] = 13;
                break;
            case 'S':
                pieceCoords[0] = 6;
                pieceCoords[1] = 7;
                pieceCoords[2] = 9;
                pieceCoords[3] = 10;
                break;
            case 'Z':
                pieceCoords[0] = 4;
                pieceCoords[1] = 5;
                pieceCoords[2] = 9;
                pieceCoords[3] = 10;
                break;
            case 'L':
                pieceCoords[0] = 1;
                pieceCoords[1] = 5;
                pieceCoords[2] = 9;
                pieceCoords[3] = 10;
                break;
            case 'J':
                pieceCoords[0] = 2;
                pieceCoords[1] = 6;
                pieceCoords[2] = 9;
                pieceCoords[3] = 10;
                break;
            case 'T':
                pieceCoords[0] = 2;
                pieceCoords[1] = 5;
                pieceCoords[2] = 6;
                pieceCoords[3] = 10;
                break;
            default:
                throw new UnsupportedOperationException("Next piece type invalid: " + nextPieceType);
        }

        // print each coordinate as an (i,j) entry referring to row i col j
        for (int pieceCoord : pieceCoords) {
            int row = pieceCoord / 4;
            int col = pieceCoord % 4;
            drawNextPieceSquare(row, col, a);
        }

    }

    // 4 x 4 representation
    private synchronized void drawNextPieceSquare(int row, int col) {

        // get square half-length
        double squareLength = 1.0 / 24.0;
        double squareHalfLength = 0.5 * squareLength;

        double x = miniGridXMin + squareHalfLength + col * squareLength;
        // flip y since origin is at bottom left
        double y = miniGridYMax - squareHalfLength - row * squareLength;

        Color squareColor = board.getNextPiece().getPieceColor();
        StdDraw.setPenColor(squareColor);
        StdDraw.filledSquare(x, y, squareHalfLength);

        // lightly outline each square in black
        StdDraw.setPenColor(Color.BLACK);
        StdDraw.setPenRadius(0.0005);
        StdDraw.square(x, y, squareHalfLength);


    }

    private synchronized void drawNextPieceSquare(int row, int col, double a) {

        // get square half-length
        double squareLength = 1.0 / 24.0;
        double squareHalfLength = 0.5 * squareLength;

        double x = miniGridXMin + squareHalfLength + col * squareLength;
        // flip y since origin is at bottom left
        double y = miniGridYMax - squareHalfLength - row * squareLength;

        Color squareColor = board.getNextPiece().getPieceColor();
        squareColor = new Color(squareColor.getRed(), squareColor.getGreen(), squareColor.getBlue(), (int)(a * 255));
        StdDraw.setPenColor(squareColor);
        StdDraw.filledSquare(x, y, squareHalfLength);

        // lightly outline each square in black
        Color fadeBlack = new Color(0, 0, 0, (int)(a * 255));
        StdDraw.setPenColor(fadeBlack);
        StdDraw.setPenRadius(0.0005);
        StdDraw.square(x, y, squareHalfLength);


    }

    private synchronized void drawScoreFrame() {

        // display score
        double scoreX = 0.5 * (scoreFrameXMin + scoreFrameXMax);
        double scoreY = 0.5 * (scoreFrameYMin + scoreFrameYMax);
        int score = board.getLinesCleared();
        String textScore = Integer.toString(score);
        StdDraw.setFont(SCORE_FONT);
        StdDraw.setPenColor(SCORE_COLOR);
        StdDraw.text(scoreX, scoreY, textScore);

    }

    private synchronized void drawScoreFrame(double a) {

        // display score
        double scoreX = 0.5 * (scoreFrameXMin + scoreFrameXMax);
        double scoreY = 0.5 * (scoreFrameYMin + scoreFrameYMax);
        int score = board.getLinesCleared();
        String textScore = Integer.toString(score);
        StdDraw.setFont(SCORE_FONT);

        Color fade = new Color(SCORE_COLOR.getRed(), SCORE_COLOR.getGreen(), SCORE_COLOR.getBlue(), (int)(a * 255));

        StdDraw.setPenColor(fade);
        StdDraw.text(scoreX, scoreY, textScore);

    }

    private synchronized void drawAdornments() {

        // draw nice solid board around entire canvas
        double blackWidth = 0.01;
        double colorWidth = 0.005;
        double gapDist = 0.003;

        StdDraw.setPenColor(Color.BLACK);
        StdDraw.setPenRadius(blackWidth);
        StdDraw.square(0.5, 0.5, 0.5);
        for (int i = 0; i < TetrisBoard.colorLibrary.length; i++) {
            double sqHalfWidth = 0.5 - gapDist * (i + 1);
            Color sqColor = TetrisBoard.colorLibrary[i];
            StdDraw.setPenColor(sqColor);
            StdDraw.setPenRadius(colorWidth);
            StdDraw.square(0.5, 0.5, sqHalfWidth);
        }
        double innerSqHalf = 0.5 - (TetrisBoard.colorLibrary.length + 1) * gapDist;
        double innerSqWidth = blackWidth / 2;
        StdDraw.setPenColor(Color.BLACK);
        StdDraw.setPenRadius(innerSqWidth);
        StdDraw.square(0.5, 0.5, innerSqHalf);

    }

    private synchronized void drawAdornments(double a) {

        Color fadeBlack = new Color(0, 0, 0, (int)(a * 255));

        // draw nice solid board around entire canvas
        double blackWidth = 0.01;
        double colorWidth = 0.005;
        double gapDist = 0.003;
        StdDraw.setPenColor(fadeBlack);
        StdDraw.setPenRadius(blackWidth);
        StdDraw.square(0.5, 0.5, 0.5);

        for (int i = 0; i < TetrisBoard.colorLibrary.length; i++) {
            double sqHalfWidth = 0.5 - gapDist * (i + 1);
            Color sqColor = TetrisBoard.colorLibrary[i];
            sqColor = new Color(sqColor.getRed(), sqColor.getGreen(), sqColor.getBlue(), (int) (a * 255));
            StdDraw.setPenColor(sqColor);
            StdDraw.setPenRadius(colorWidth);
            StdDraw.square(0.5, 0.5, sqHalfWidth);
        }

        double innerSqHalf = 0.5 - (TetrisBoard.colorLibrary.length + 1) * gapDist;
        double innerSqWidth = blackWidth / 2;
        StdDraw.setPenColor(fadeBlack);
        StdDraw.setPenRadius(innerSqWidth);
        StdDraw.square(0.5, 0.5, innerSqHalf);

    }

/* ***************************************************************************
 * Pass-through methods used to access the inside tetris board.
 * ***************************************************************************/

    public boolean generatePiece() {

        return board.generatePiece();

    }

    public boolean left() {

        return board.left();

    }

    public boolean right() {

        return board.right();
    }

    public boolean down() {

        return board.down();

    }

    public boolean rotate() {

        return board.rotate();
    }


/* ***************************************************************************
 * Test Client
 * ***************************************************************************/

    public static void main(String[] args) {

        int testH = 24;
        int testW = 12;

        TetrisDisplay td = new TetrisDisplay(testH, testW);
        td.refresh();

        // String tetrisImageFileName = "graphics/tetris_image01.jpeg";
        // Picture tetrisPic = new Picture(tetrisImageFileName);

        // System.out.println("Picture statistics:");
        // System.out.println("Width : " + tetrisPic.width());
        // System.out.println("Height: " + tetrisPic.height());

    }

}
