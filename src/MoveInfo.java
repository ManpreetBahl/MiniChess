/**
 * Created by Manpreet on 5/8/2017.
 * This file is used to map a move to the negamax scores to determine the best move possible
 */
public class MoveInfo {
    //Move that was made
    protected Move move;

    //Resulting board state due to the move
    protected State state;

    //Score of the board state
    protected int score;
}
