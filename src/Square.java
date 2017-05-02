import java.util.HashMap;

/**
 * Created by Manpreet on 4/3/2017.
 */
public class Square {
    //===================FIELDS==========================
    //X Coordinate of the Square
    protected int x;

    //Y Coordinate of the Square
    protected int y;
    //===================================================


    //===================METHODS=========================

    /*===================================
    * Constructor
    * Params:
        1) X coordinate of the square
        2) Y coordinate of the square
    ===================================*/
    public Square(int x, int y){
        this.x = x;
        this.y = y;
    }
    //===================================================

    public String toString(){
        switch(y){
            case 0:
                return "a" + (6-x);
            case 1:
                return "b" + (6-x);
            case 2:
                return "c" + (6-x);
            case 3:
                return "d" + (6-x);
            case 4:
                return "e" + (6-x);
            default:
                throw new IllegalStateException("Invalid square coordinates detected!");
        }
    }
}
