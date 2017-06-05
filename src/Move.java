/**
 * Created by Manpreet on 4/4/2017.
 * This file describes a Move.
 */
public class Move {
    //===================FIELDS==========================
    //Which square to move to
    protected Square to;

    //Which square to move from
    protected Square from;
    //===================================================

    //===================METHODS=========================

    /*===================================
    * Constructor
    * Params:
        1) Square to move to
        2) Square to move from
    ===================================*/
    public Move(Square to, Square from){
        this.to = to;
        this.from = from;
    }
    //===================================================

    /*===================================
    * This function returns a string of the from square and to square in the appropriate game foramt
    * Params:
        1) None.
    * Returns:
        1) String describing the move.
    ===================================*/
    public String toString(){
        return from.toString() + "-" + to.toString();
    }
}
