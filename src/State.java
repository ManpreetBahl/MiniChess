import java.net.CookieHandler;
import java.util.*;
import java.lang.System;
/**
 * Created by Manpreet on 4/3/2017.
 */
public class State {
    //===================FIELDS==========================
    //Chess board
    protected char [][] board;

    //Keep track of whose turn is it
    protected char move;

    //Keep track of the turn count
    protected int turn;

    //Determine if game is over
    protected boolean over;

    //Keep track of who won
    protected char winner;

    //Start Search Time
    protected long startTime;

    //Elapsed Time
    protected double elapsedTime;

    //Time Limit
    protected double timeLimit;

    //===================================================

    //===================METHODS=========================

    //Default Constructor
    public State(){
        //Creates a 5 x 6 board
        board = new char[6][5];

        //Black Pieces (lowercase)
        char [] blackPieces = { 'k','q', 'b', 'n', 'r' };

        //White Pieces (uppercase)
        char [] whitePieces = { 'R', 'N', 'B', 'Q', 'K'};

        //Initialize the board to starting positions
        for(int i = 0; i < board.length; i++){
            for(int j = 0; j < board[i].length; j++){
                switch(i){
                    case 0:
                        board[i][j] = blackPieces[j];
                        break;
                    case 1:
                        board[i][j] = 'p';
                        break;
                    case 4:
                        board[i][j] = 'P';
                        break;
                    case 5:
                        board[i][j] = whitePieces[j];
                        break;
                    default:
                        board[i][j] = '.';
                }
            }
        }
        //White goes first
        move = 'W';

        //First turn
        turn = 1;

        //Game is not over
        over = false;

        //No winner
        winner = '?';

        //Start time is 0
        startTime = 0;

        //Elapsed time is also 0
        elapsedTime = 0;

        //Time limit for move
        timeLimit = 5;
    }

    //Copy Constructor
    public State(char[][]newBoard, char move, int turn, boolean over, char winner, long startTime){
        //Initialize board size to matching board size
        board = new char[newBoard.length][newBoard[0].length];
        //Make deep copy of the board
        for(int i = 0; i < board.length; i++){
            for(int j = 0; j < board[0].length; j++){
                board[i][j] = newBoard[i][j];
            }
        }
        //Set move to parameter
        this.move = move;

        //Set turn count
        this.turn = turn;

        //Set game over state
        this.over = over;

        //Set winner
        this.winner = winner;

        //Start time
        this.startTime = startTime;

        //Elapsed time
        this.elapsedTime = 0;

        //Time limit
        this.timeLimit = 5;

    }

    //Prints the current board state
    public String print(){
        String boardState = turn + " " + move + "\n";

        for (char[] row : board){
            boardState += String.valueOf(row) + "\n";
        }

        return boardState;
    }

    //Update board with new board state
    public void read(String newBoard){
        Scanner scan = new Scanner(newBoard);

        turn = Integer.parseInt(scan.next());
        move = scan.next().charAt(0);
        scan.nextLine();

        for(int i = 0; scan.hasNextLine(); i++){
            String row = scan.nextLine();
            for(int j = 0; j < board[i].length; j++){
                board[i][j] = row.charAt(j);
            }
        }
    }

    //Handles the pawn promotion when moving a piece
    public char promotePawn(char piece, int row){
        if(piece == 'P' && move == 'W' && row == 0){
            return 'Q';
        }
        else if(piece == 'p' && move == 'B' && row == 5){
            return 'q';
        }
        else{
            return piece;
        }
    }

    //Returns an updated state after a move has been made
    public State move(Move mov){
        //Get the piece at the from Square
        char source = board[mov.from.x][mov.from.y];
        char dest = board[mov.to.x][mov.to.y];

        //Check if there's a piece at the from square and belongs to current player
        if(source != '.' && pieceColor(source) == move){
            //Create a new state with current values
            State newState = new State(board, move, turn, over, winner, 0);

            //Move the piece and handle any pawn promotions if need be
            newState.board[mov.from.x][mov.from.y] = '.';
            newState.board[mov.to.x][mov.to.y] = promotePawn(source, mov.to.x);

            //Update whose turn it is as well as the turn count
            switch(newState.move){
                case 'W':
                    newState.move = 'B';
                    break;
                case 'B':
                    newState.move = 'W';
                    newState.turn++;
                    break;
                default:
                    throw new IllegalStateException("Invalid player! Player can only be (W)hite or (B)lack!");
            }

            //Check the 40 turn limit
            if(newState.turn > 40){
                newState.over = true;
                newState.winner = 'D'; //The game is a draw
            }
            //Check if the player can't make a move
            else if(newState.moveList().isEmpty()){
                newState.over = true;
                newState.winner = move; //Current player has won
            }
            //Check for king capture
            else{
                switch(dest){
                    //White wins in the case of black king capture
                    case 'k':
                        newState.over = true;
                        newState.winner = 'W';
                        break;
                    //Black wins in the case of white king capture
                    case 'K':
                        newState.over = true;
                        newState.winner = 'B';
                        break;
                }
            }
            return newState;
        }
        else{
            throw new IllegalArgumentException("Invalid move detected!");
        }
    }
    //===================================================

    //Moves based on a string format
    public State move(String mov){
        //Get a list of legal moves
        ArrayList<Move> moves = moveList();
        //Compare the move string for each move with the parameter
        for(Move m : moves){
            if(m.toString().equals(mov)){
                return move(m);
            }
        }
        throw new IllegalArgumentException("Invalid move string detected!");
    }


    /*====================================================
   This function determines the color of a given piece.
   Params:
       1) Piece: A character representing the piece.
   Returns:
       1) 'W' or 'B' indicating that the piece is either white or black.
   ====================================================*/
    public char pieceColor(char piece){
        //Check if the piece is white
        if(piece == 'P' || piece == 'R' || piece == 'N' || piece == 'B' || piece == 'Q' || piece == 'K'){
            return 'W';
        }
        //Check if the piece is black
        else if(piece == 'p' || piece == 'r' || piece == 'n' || piece == 'b' || piece == 'q' || piece == 'k'){
            return 'B';
        }
        //Invalid character on the board
        else{
            throw new IllegalStateException("Invalid piece detected on board");
        }
    }

    /*====================================================
    This function determines whether the coordinates are within the board limits.
    Params:
        1) Row: The row coordinate.
        2) Col: The column coordinate
    Returns:
        1) True or False depending on whether it's in the board or not.
    ====================================================*/
    public boolean inBounds(int row, int col){
        if (row < 6 && col < 5 && row >= 0 && col >= 0) {
            return true;
        }
        return false;
    }


    /*====================================================
    This function generates a move list by scanning how far the chosen piece can go in a certain direction.
    Params:
        1) Piece: A character representing the piece.
        2) Row: Starting row position.
        3) Col: Starting col position.
        4) dRow: Row direction to scan.
        5) dCol: Col direction to scan.
        6) Capture: Indicate whether it can capture, not capture, or capture only.
            -1 for capture means false, 0 for capture means capture only, 1 for capture means can capture
        7) stopShort
    Returns:
        1) None.
    ====================================================*/
    public void scan(ArrayList<Move> moves, char piece, int row, int col, int dRow, int dCol, int capture, boolean stopShort){
        int r = row;
        int c = col;
        char color = pieceColor(piece);
        do{
            r += dRow;
            c += dCol;
            //Check to make it's in bounds
            if(!inBounds(r,c)){
                break;
            }
            //Check if there's a piece at r,c
            if(board[r][c] != '.'){
                //Check if the piece at that square is not of the same color
                if(pieceColor(board[r][c]) == color){
                    break;
                }
                //Check if capture is false
                if(capture == -1){
                    break;
                }
                stopShort = true;
            }
            //Capture = only
            else if(capture == 0){
                break;
            }
            moves.add(new Move(new Square(r, c), new Square(row, col)));
        }while(!stopShort);
    }

    /*====================================================
    This function tries all four rotational symmetries of a given scan.
    Params:
        1) Piece: A character representing the piece.
        2) Row: Starting row position.
        3) Col: Starting col position.
        4) dRow: Row direction to scan.
        5) dCol: Col direction to scan.
        6) Capture: Indicate whether it can capture, not capture, or capture only.
            -1 for capture means false, 0 for capture means capture only, 1 for capture means can capture
        7) stopShort
    Returns:
        1) Arraylist of possible moves.
    ====================================================*/
    public void symmscan(ArrayList<Move> moves, char piece, int row, int col, int dRow, int dCol, int capture, boolean stopShort){
        for(int i = 0; i < 4; i++){
            scan(moves, piece, row, col, dRow, dCol, capture, stopShort);
            //Exchange dx with dy
            //Code obtained from http://javarevisited.blogspot.com/2013/02/swap-two-numbers-without-third-temp-variable-java-program-example-tutorial.html
            dRow = dRow ^ dCol;
            dCol = dRow ^ dCol;
            dRow = dRow ^ dCol;

            //Negate dy
            dCol = -dCol;
        }
    }

    public ArrayList<Move> moveList(){
        ArrayList<Move> moves = new ArrayList<>();
        for(int x = 0; x < board.length; x++){
            for(int y = 0; y < board[x].length; y++){
                char piece = board[x][y];
                if(piece != '.' && pieceColor(piece) == move){
                    //Convert it to lower case for shorter switch case
                    switch(Character.toLowerCase(piece)){
                        case 'k':
                            symmscan(moves, piece, x, y, 0, 1, 1, true);
                            symmscan(moves, piece, x, y, 1, 1, 1, true);
                            break;
                        case 'q':
                            symmscan(moves, piece, x, y, 0 ,1, 1, false);
                            symmscan(moves, piece, x, y, 1, 1, 1, false);
                            break;
                        case 'r':
                            symmscan(moves, piece, x, y, 0, 1, 1, false);
                            break;
                        case 'b':
                            //Bad bishop, can move N, S, E, W by 1 space but can't capture
                            symmscan(moves, piece, x, y, 0, 1, -1, true);
                            //Normal bishop rules
                            symmscan(moves, piece, x, y, 1, 1, 1, false);
                            break;
                        case 'n':
                            symmscan(moves, piece, x, y, 1, 2, 1, true);
                            symmscan(moves, piece, x, y, -1, 2, 1, true);
                            break;
                        case 'p':
                            int dir = 1;
                            if(move == 'B'){
                                dir = -1;
                            }
                            scan(moves, piece, x, y, -dir, -1,0, true);
                            scan(moves, piece, x, y, -dir, 1, 0, true);
                            scan(moves, piece, x, y, -dir, 0, -1, true);
                            break;
                        default:
                            throw new IllegalStateException("Invalid piece on the board");
                    }
                }
            }
        }
        return moves;
    }

    public int eval(){
        int blackScore = 0, whiteScore = 0;
        boolean blackKingGone = true, whiteKingGone = true;

        for(char[]row : board){
            for(char piece : row){
                //If it's not a piece, just continue
                if(piece == '.'){
                    continue;
                }
                switch(piece){
                    case 'k':
                        blackKingGone = false;
                        break;
                    case 'K':
                        whiteKingGone = false;
                        break;
                    case 'p':
                        blackScore += 100;
                        break;
                    case 'P':
                        whiteScore += 100;
                        break;
                    case 'b':
                        blackScore += 300;
                        break;
                    case 'B':
                        whiteScore += 300;
                        break;
                    case 'n':
                        blackScore += 300;
                        break;
                    case 'N':
                        whiteScore += 300;
                        break;
                    case 'r':
                        blackScore += 500;
                        break;
                    case 'R':
                        whiteScore += 500;
                        break;
                    case 'q':
                        blackScore += 900;
                        break;
                    case 'Q':
                        whiteScore += 900;
                        break;
                    default:
                        break;
                }
            }
        }

        //Black king has been captured
        if(blackKingGone){
            //System.out.println("Black king gone! Move: " + move);
            if(move == 'B'){
                return -10000;
            }
            return 10000;
        }
        //White king is gone
        else if(whiteKingGone){
            //System.out.println("White king gone! Move: " + move);
            if(move == 'W'){
                return -10000;
            }
            return 10000;
        }
        else{
            if(move == 'W'){
                //return whiteScore - blackScore;
                return whiteScore;
            }
            else if(move == 'B'){
                //return blackScore - whiteScore;
                return blackScore;
            }
            else{
                throw new IllegalStateException("Invalid move while evaluating game state!");
            }
        }
    }

    //Negamax algorithm with alpha beta pruning
    public int negamax(State s, int depth, int alpha, int beta){
        elapsedTime = (System.nanoTime() - startTime) * 1e-9;
        if (s.over || depth <= 0 || elapsedTime >= timeLimit){
            return s.eval();
        }

        //For all possible moves, generate all possible states
        ArrayList<Move>moves = s.moveList();
        int moveCount = moves.size();
        ArrayList<State> newStates = new ArrayList<>();

        for(int i = 0; i < moveCount; i++){
            newStates.add(s.move(moves.get(i)));
        }

        //Shuffle arraylist then sort by piece evaluation
        Collections.shuffle(newStates);
        Collections.sort(newStates, (m1, m2) -> Integer.compare(m2.eval(), m1.eval()));

        int value = -(negamax(newStates.get(0), depth - 1, -beta, -alpha));
        if(value > beta){
            return value;
        }
        alpha = Integer.max(alpha, value);

        for(int i = 1; i < moveCount; i++){
            int v = -(negamax(newStates.get(i), depth - 1, -beta, -alpha));
            if(v >= beta){
                return v;
            }
            value = Integer.max(value, v);
            alpha = Integer.max(alpha, v);
        }
        return value;
    }


    public MoveInfo bestMove(){
        //Get start time for search
        startTime = System.nanoTime();

        //Get list of all possible moves
        ArrayList<Move> m = moveList();
        int moveSize = m.size();

        //Arraylist to maintain state and score of such moves
        ArrayList<MoveInfo> info = new ArrayList<MoveInfo>();

        //Execute each move and evaluate the score. Add the info to the Info List
        for (int i = 0; i < moveSize; i++) {
            MoveInfo newMove = new MoveInfo();
            newMove.move = m.get(i);
            newMove.state = move(newMove.move);
            newMove.score = -(newMove.state.eval());
            if(newMove.score == 10000){ //Game is over
                return newMove;
            }
            info.add(newMove);
        }

        //Sort based on the score
        Collections.shuffle(info);
        Collections.sort(info, (m1, m2) -> Integer.compare(m2.score, m1.score));

        //Set current best move to the first in the sorted move list
        MoveInfo best = info.get(0);

        //Negaxmax search
        int depth = 0; //Starting depth
        ArrayList<MoveInfo>bestMoves = new ArrayList<>(); //Overall best moves that can be made
        elapsedTime = (System.nanoTime() - startTime) * 1e-9; //Elapsed time

        while(elapsedTime < timeLimit){
            //Keep track of current best moves for current depth
            ArrayList<MoveInfo>currentBest = new ArrayList<>();
            int bestScore = -10000;
            depth++;

            for(int j = 1; j < moveSize; j++){
                MoveInfo current = info.get(j);
                current.score = negamax(current.state, depth, -10000, 10000);

                if(current.score == 10000){ //Game winner, just return that move
                    return current;
                }

                if(current.score > bestScore){ //Better move has been found
                    bestScore = current.score; //Set best score to that value
                    currentBest.clear(); //Clear current best moves of any previous moves
                    currentBest.add(current); //Add the new move
                    //best = current;
                }

                if(current.score == bestScore){
                    currentBest.add(current);
                }
            }

            //There's time to possible make another search so save current results
            if(elapsedTime < timeLimit){
                bestMoves.clear(); //Clear the list of best moves so that the updated results can be added
                bestMoves.addAll(currentBest);
            }
        }

        //If there's a list of best possible moves, pick a random one
        if(bestMoves.size() > 0){
            Random rand = new Random();
            int index = rand.nextInt(bestMoves.size());
            best = bestMoves.get(index);
        }
        return best;
    }
}
