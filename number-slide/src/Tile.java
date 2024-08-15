import java.awt.Color;
import java.awt.Font;

/**
 * Class used to define a single Tile in the Number Slide game.
 * The Tile class allows for the more complicated drawing methods
 * to be abstracted.
 *
 * @author adamm.hockman@gmail.com
 */
public class Tile {

    // proportion of the tile width for border
    private static final double BORDER_WIDTH = 0.15;

    // parameters for drawing lines
    private static final int TOTAL_LINES = 10;
    private static final double LINE_WIDTH = 0.002;

    // color / font space
    private static final Color GOLD = new Color(245, 180, 0);
    private static final Color FADE_GOLD = new Color(245, 180, 0, (int)(0.8 * 255));
    private static final Color BLACK = new Color(0, 0, 0);
    private static final Color FADE_BLACK = new Color(0, 0, 0, (int)(0.8 * 255));

    // tile specific font
    private final Font numberFont;

    // basic tile info
    private final int val;
    private final int gridSize;
    private final int goalRow;
    private final int goalCol;

    // drawing parameters
    private double borderHalfWidth;
    private double linesHalfWidth;
    private double lineGap;
    private boolean inverted;

    // positioning
    private double x0, y0;
    private double xCenter, yCenter;

/* ***************************************************************************
 *    * Constructors
 ****************************************************************************/

    /**
     * Constructor takes tile value and board grid size, and assigns
     * them accordingly.
     * @param val the numerical value of the tile
     * @param gridSize the size of the board containing the tile
     */
    public Tile(int val, int gridSize) {

        this.val = val;
        this.gridSize = gridSize;
        this.goalRow = (val - 1) / gridSize;
        this.goalCol = (val - 1) % gridSize;

        // set font size
         if (gridSize == 3)
             this.numberFont = new Font("Courier", Font.BOLD, 32);
         else if (gridSize == 4)
             this.numberFont = new Font("Courier", Font.BOLD, 26);
         else
             this.numberFont = new Font("Courier", Font.BOLD, 20);

    }


/* ***************************************************************************
 *    * Accessor / Copy Methods
 ****************************************************************************/

    /**
     * Public accessor method used to get the value of the tile
     * @return int tile display value
     */
    public int val() {

        return val;

    }

    /**
     * Returns a new instance to a Tile with the same internal fields.
     * @return Tile reference to new copy
     */
    public Tile copy() {

        return new Tile(this.val, this.gridSize);

    }


/* ***************************************************************************
 *    * General Draw Methods.
 ****************************************************************************/

    /**
     * Draws the tile with given width, centered at the coordinates provided.
     * @param xCenter double x-coordinate of tile center
     * @param yCenter double y-coordinate of tile center
     * @param width double full width of the tile
     * @param inverted boolean flag the colors should be drawn inverted
     */
    public void draw(double xCenter, double yCenter, double width, boolean inverted) {

        // update drawing parameters
        this.inverted = inverted;
        this.xCenter = xCenter;
        this.yCenter = yCenter;

        // overall tile half width
        double halfWidth = 0.5 * width;

        // half width of dist from middle of border to middle of border across tile
        this.borderHalfWidth = halfWidth * (1.0 - 0.5 * BORDER_WIDTH);

        // half of width of area inside of border
        this.linesHalfWidth = halfWidth * (1.0 - BORDER_WIDTH);

        // distance between adjacent line segment endpoints along the same edge
        this.lineGap = linesHalfWidth / ((double) TOTAL_LINES - 1.0);

        // define origin (0, 0) position
        this.x0 = xCenter - linesHalfWidth;
        this.y0 = yCenter - linesHalfWidth;

        // create inner tile color
        if (inverted)
            StdDraw.setPenColor(FADE_GOLD);
        else
            StdDraw.setPenColor(FADE_BLACK);

        StdDraw.filledSquare(xCenter, yCenter, halfWidth);

        // draw the three tile elements
        drawBorder();
        drawLines();
        drawValue();

    }

    /**
     * Internal method used to draw the border design around the tile.
     * Border consists of overlapping squares of decreasing line width.
     */
    private void drawBorder() {

        int LINES = 4;

        double borderWidth = 0.10 * BORDER_WIDTH;
        double delta = borderWidth / ((double) LINES);

        for (int i = 0; i < LINES; i++) {
            StdDraw.setPenRadius(borderWidth - delta * i);
            if (i % 2 == 0)
                StdDraw.setPenColor(GOLD);
            else
                StdDraw.setPenColor(Color.BLACK);
            StdDraw.square(xCenter, yCenter, borderHalfWidth);
        }

    }

    /**
     * Internal method used to draw the tile value inside the tile center.
     */
    private void drawValue() {

        Color tileColor;
        if (inverted)
            tileColor = BLACK;
        else
            tileColor = GOLD;

        // write tile number
        StdDraw.setPenColor(tileColor);
        StdDraw.setFont(numberFont);
        StdDraw.text(xCenter, yCenter, Integer.toString(this.val));

    }


/* ***************************************************************************
 *    * Line Drawing Methods.
 ****************************************************************************/

    /**
     * Main drawing method for the spiraling lines.
     * Uses the value to determine which lines should be drawn, and calls those
     * methods appropriately.
     */
    private void drawLines() {

        if (inverted)
            StdDraw.setPenColor(BLACK);
        else
            StdDraw.setPenColor(GOLD);

        StdDraw.setPenRadius(LINE_WIDTH);

        // draw spiraling lines pattern
        bottomRightLines();
        rightTopLines();
        topLeftLines();
        leftBottomLines();

    }

    /**
     * Private helper method used to draw the lines from the bottom edge to the
     * right side of the tile.
     * Ignores the case when the goalRow or goalCol falls along the bottom or
     * right side.
     */
    private void bottomRightLines() {

        // don't draw on bottom or right side
        if (goalRow == gridSize - 1 || goalCol == gridSize - 1)
            return;

        double x1, y1, x2, y2;

        y1 = y0;
        x2 = x0 + linesHalfWidth * 2;
        for (int i = 0; i < TOTAL_LINES; i++) {
            x1 = x0 + i * lineGap;
            y2 = yCenter + i * lineGap;
            StdDraw.line(x1, y1, x2, y2);
        }

    }

    /**
     * Private helper method used to draw the lines from the right side to the
     * top edge of the tile.
     * Ignores the case when the goalRow or goalCol falls along the top or
     * right side.
     */
    private void rightTopLines() {

        // don't draw on right side or top
        if (goalRow == 0 || goalCol == gridSize - 1)
            return;

        double x1, y1, x2, y2;

        x1 = x0 + linesHalfWidth * 2;
        y2 = y0 + linesHalfWidth * 2;
        for (int i = 0; i < TOTAL_LINES; i++) {
            y1 = y0 + i * lineGap;
            x2 = xCenter - i * lineGap;
            StdDraw.line(x1, y1, x2, y2);
        }


    }

    /**
     * Private helper method used to draw the lines from the top edge to the
     * left side of the tile.
     * Ignores the case when the goalRow or goalCol falls along the top or
     * left side.
     */
    private void topLeftLines() {

        // don't draw on top or left side
        if (goalRow == 0 || goalCol == 0)
            return;

        double x1, y1, x2, y2;

        y1 = y0 + linesHalfWidth * 2;
        x2 = x0;
        for (int i = 0; i < TOTAL_LINES; i++) {
            x1 = xCenter + linesHalfWidth - i * lineGap;
            y2 = yCenter - i * lineGap;
            StdDraw.line(x1, y1, x2, y2);
        }

    }

    /**
     * Private helper method used to draw the lines from the left side to the
     * bottom edge of the tile.
     * Ignores the case when the goalRow or goalCol falls along the bottom or
     * left side.
     */
    private void leftBottomLines() {

        // don't draw on left side or bottom
        if (goalRow == gridSize - 1 || goalCol == 0)
            return;

        double x1, y1, x2, y2;

        x1 = x0;
        y2 = y0;
        for (int i = 0; i < TOTAL_LINES; i++) {
            y1 = yCenter + linesHalfWidth - i * lineGap;
            x2 = xCenter + i * lineGap;
            StdDraw.line(x1, y1, x2, y2);
        }

    }


}
