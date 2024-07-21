
import java.awt.*;

public class Tile implements Comparable<Tile> {

    public static final Color[] COLOR_LIBRARY = { Color.BLACK, Color.MAGENTA, Color.RED, Color.YELLOW, Color.ORANGE,
            Color.GREEN, Color.BLUE};

    private static final Font NUMBER_FONT = new Font("Courier", Font.BOLD, 32);
    private static final Color FONT_COLOR = Color.BLACK;

    private static final double TILE_BUFFER = 0.01;

    private static final int minOpacity = 100;
    private static final int maxOpacity = 200;

    private final int value;

    private Color color;
    private String tileImage;

    public Tile(int value) {

        this.value = value;
        this.color = Color.WHITE;

        setBackground();

    }

    private void setBackground() {

        if (value == 0) {
            color = Color.WHITE;
            return;
        }

        this.tileImage = String.format("tiles/tile%02d.jpeg",value);
        // System.out.println("background file name:" + this.tileImage);

        Picture p = new Picture(this.tileImage);



        int alpha = StdRandom.uniformInt(minOpacity, maxOpacity);

        int colorIndex = StdRandom.uniformInt(COLOR_LIBRARY.length);
        Color color0 = COLOR_LIBRARY[colorIndex];

        int r = color0.getRed();
        int g = color0.getGreen();
        int b = color0.getBlue();

        this.color = new Color(r, g, b, alpha);


    }

    public int value() {
        return value;
    }

    public int compareTo(Tile x) {

        return Integer.compare(this.value, x.value());

    }

    public boolean equals(Tile x) {

        return (compareTo(x) == 0);

    }

    public void draw(double x, double y, double width) {

        // draw tile outline
        StdDraw.setPenColor(color);
        StdDraw.setPenRadius(0.01);
        StdDraw.filledSquare(x, y, width - TILE_BUFFER);

        // label tile number
        if (value == 0)
            return;

        StdDraw.setPenColor(FONT_COLOR);
        StdDraw.setFont(NUMBER_FONT);
        StdDraw.text(x, y, Integer.toString(value));

    }


}
