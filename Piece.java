package Gam;

import java.awt.Color; //import colors and random
import java.util.Random;

public class Piece {
    private int[][] shape; //
    private Color color;

    //constructor
    public Piece(int[][] shape, Color color) {
        this.shape = shape; //takes shape and color
        this.color = color;
    }

    public Color getColor() { //gets the color
        return color;
    }

    public int[][] getShape() { //gets the shape
        return shape;
    }

    //rotate 90 degrees clockwise
    public void rotate() {
        for (int i = 0; i < shape.length; i++) {
            int x = shape[i][0]; //get x cords
            int y = shape[i][1]; //get y cords
            shape[i][0] = -y; //set new x as negative of current y
            shape[i][1] = x; //set new y as the current x
        }
    }

    //rotate 90 degrees counter-clockwise
    public void rotateBack() {
        for (int i = 0; i < shape.length; i++) {
            int x = shape[i][0];//get x cords
            int y = shape[i][1];//get y cords
            shape[i][0] = y; //set new x as current y
            shape[i][1] = -x; //set new y as negative of current x
        }
    }

    //get a random piece (centered shapes for rotation)
    public static Piece getRandomPiece() {
        Random rand = new Random(); //random number gen
        int pieceType = rand.nextInt(7); //0-6

        switch (pieceType) { //given random number return a piece
            case 0: // I-piece
                return new Piece(new int[][]{
                    {-1, 0}, {0, 0}, {1, 0}, {2, 0}
                }, Color.CYAN);

            case 1: // O-piece (square, does not rotate)
                return new Piece(new int[][]{
                    {0, 0}, {1, 0}, {0, 1}, {1, 1}
                }, Color.YELLOW);

            case 2: // T-piece
                return new Piece(new int[][]{
                    {-1, 0}, {0, 0}, {1, 0}, {0, 1}
                }, Color.MAGENTA);

            case 3: // S-piece
                return new Piece(new int[][]{
                    {0, 0}, {1, 0}, {-1, 1}, {0, 1}
                }, Color.GREEN);

            case 4: // Z-piece
                return new Piece(new int[][]{
                    {-1, 0}, {0, 0}, {0, 1}, {1, 1}
                }, Color.RED);

            case 5: // J-piece
                return new Piece(new int[][]{
                    {-1, 0}, {0, 0}, {1, 0}, {1, 1}
                }, Color.BLUE);

            case 6: // L-piece
                return new Piece(new int[][]{
                    {-1, 0}, {0, 0}, {1, 0}, {-1, 1}
                }, Color.ORANGE);

                //all have their own color
            default:
                return null; //default but shouldn't happen tho
        }
    }
}
