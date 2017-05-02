/**
 * Created by Manpreet on 4/4/2017.
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

    public String toString(){
        return from.toString() + "-" + to.toString();
    }
}
