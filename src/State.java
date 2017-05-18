import java.net.CookieHandler;
import java.util.*;
import java.lang.System;
/**
 * Created by Manpreet on 4/3/2017.
 * This file describes a board state.
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

    /*====================================================
    This function is the default constructor for a State
    Params:
        1) None
    Returns:
        1) Initialized State
    ====================================================*/
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

        //Time limit for move (6 seconds)
        timeLimit = 6;
    }

    /*====================================================
    This function creates a State object based on the parameters passed in. Essentially a copy constructor.
    Params:
        1) Board: The board of the state to copy.
        2) Move: Whose move is it.
        3) Turn: Current turn count.
        4) Over: Is the game over?
        5) Winner: Who is the winner?
        6) Start time: The start time for iterative deepening.
    Returns:
        1) State object.
    ====================================================*/
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
        this.timeLimit = 6;

    }

    /*====================================================
    This function returns a string of the current board state.
    Params:
        1) None.
    Returns:
        1) String containing the current board state.
    ====================================================*/
    public String print(){
        //The first line is turn count followed by whose move it is
        String boardState = turn + " " + move + "\n";

        //Get the board state and append to the string
        for (char[] row : board){
            boardState += String.valueOf(row) + "\n";
        }

        return boardState;
    }

    /*====================================================
    This function updates the board based on the new string board passed in as the parameter.
    Params:
        1) New Board: What to set the current board to.
    Returns:
        1) None
    ====================================================*/
    public void read(String newBoard){
        //Use a scanner to parse the new board string
        Scanner scan = new Scanner(newBoard);

        //Update turn and move
        turn = Integer.parseInt(scan.next());
        move = scan.next().charAt(0);
        scan.nextLine();

        //Update the board
        for(int i = 0; scan.hasNextLine(); i++){
            String row = scan.nextLine();
            for(int j = 0; j < board[i].length; j++){
                board[i][j] = row.charAt(j);
            }
        }
    }

    /*====================================================
    This function handles the pawn promotion to a Queen.
    Params:
        1) Piece: The piece to check.
        2) Row: What row is the piece in.
    Returns:
        1) Character containing the promoted piece or the original piece if it was not a pawn.
    ====================================================*/
    public char promotePawn(char piece, int row){
        //White pawn
        if(piece == 'P' && move == 'W' && row == 0){
            return 'Q';
        }
        //Black pawn
        else if(piece == 'p' && move == 'B' && row == 5){
            return 'q';
        }
        //Not a pawn
        else{
            return piece;
        }
    }

    /*====================================================
    This function returns a new State after making a move and updating the current State values.
    Params:
        1) Mov: Move object containing the move to make on the current state.
    Returns:
        1) Updated State object.
    ====================================================*/
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
                    newState.turn++; //Increment turn count
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

    /*====================================================
    This function returns a new State after making a move which is passed in as a string. It also checks if the move
    passed in is a legal move.
    Params:
        1) Mov: String of the move (in the format a1-b1)
    Returns:
        1) Updated State after the move.
    ====================================================*/
    public State move(String mov){
        //Get a list of legal moves
        ArrayList<Move> moves = moveList();

        //Compare the move string for each move with the parameter
        for(Move m : moves){
            if(m.toString().equals(mov)){
                return move(m); //Make the move if it's legal and return the state
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
        1) Moves: The movelist where any legal moves are added to it.
        2) Piece: A character representing the piece.
        3) Row: Starting row position.
        4) Col: Starting col position.
        5) dRow: Row direction to scan.
        6) dCol: Col direction to scan.
        7) Capture: Indicate whether it can capture, not capture, or capture only.
            -1 for capture means false, 0 for capture means capture only, 1 for capture means can capture
        8) stopShort
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
        1) Moves: The movelist where any legal moves are added to it.
        2) Piece: A character representing the piece.
        3) Row: Starting row position.
        4) Col: Starting col position.
        5) dRow: Row direction to scan.
        6) dCol: Col direction to scan.
        7) Capture: Indicate whether it can capture, not capture, or capture only.
            -1 for capture means false, 0 for capture means capture only, 1 for capture means can capture
        8) stopShort
    Returns:
        1) Arraylist of possible moves.
    ====================================================*/
    public void symmscan(ArrayList<Move> moves, char piece, int row, int col, int dRow, int dCol, int capture, boolean stopShort){
        for(int i = 0; i < 4; i++){
            scan(moves, piece, row, col, dRow, dCol, capture, stopShort);
            //Exchange dx with dy using the properties of XOR
            dRow = dRow ^ dCol;
            dCol = dRow ^ dCol;
            dRow = dRow ^ dCol;

            //Negate dy
            dCol = -dCol;
        }
    }

    /*====================================================
    This function generates a movelist which contains all possible moves that the current State can do.
    Params:
        1) None.
    Returns:
        1) Arraylist of legal moves that can be made.
    ====================================================*/
    public ArrayList<Move> moveList(){
        ArrayList<Move> moves = new ArrayList<>();
        //Iterate through the board
        for(int x = 0; x < board.length; x++){
            for(int y = 0; y < board[x].length; y++){
                char piece = board[x][y];
                if(piece != '.' && pieceColor(piece) == move){
                    //Convert it to lower case for shorter switch case
                    switch(Character.toLowerCase(piece)){
                        case 'k': //King
                            symmscan(moves, piece, x, y, 0, 1, 1, true);
                            symmscan(moves, piece, x, y, 1, 1, 1, true);
                            break;
                        case 'q': //Queen
                            symmscan(moves, piece, x, y, 0 ,1, 1, false);
                            symmscan(moves, piece, x, y, 1, 1, 1, false);
                            break;
                        case 'r': //Rook
                            symmscan(moves, piece, x, y, 0, 1, 1, false);
                            break;
                        case 'b': //Bishop
                            //Bad bishop, can move N, S, E, W by 1 space but can't capture
                            symmscan(moves, piece, x, y, 0, 1, -1, true);
                            //Normal bishop rules
                            symmscan(moves, piece, x, y, 1, 1, 1, false);
                            break;
                        case 'n': //Knight
                            symmscan(moves, piece, x, y, 1, 2, 1, true);
                            symmscan(moves, piece, x, y, -1, 2, 1, true);
                            break;
                        case 'p': //Pawn
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

    /*====================================================
    This function evaluates the current state and returns a score.
    Params:
        1) None.
    Returns:
        1) Integer scoring the current state based on pieces and whose turn it is.
    ====================================================*/
    public int eval(){
        int blackScore = 0, whiteScore = 0;
        boolean blackKingGone = true, whiteKingGone = true;

        //Iterate through the board
        for(int i = 0; i < board.length; i++){
            for(int j = 0; j < board[i].length; j++){
                char piece = board[i][j];
                //If it's not a piece, just continue
                if(piece == '.'){
                    continue;
                }
                switch(piece){
                    case 'k': //Black king
                        blackKingGone = false;
                        break;
                    case 'K': //White king
                        whiteKingGone = false;
                        break;
                    case 'p': //Black pawn
                        blackScore += 1000;
                        break;
                    case 'P': //White pawn
                        whiteScore += 1000;
                        break;
                    case 'b': //Black bishop
                        blackScore += 3000;
                        break;
                    case 'B': //White bishop
                        whiteScore += 3000;
                        break;
                    case 'n': //Black knight
                        blackScore += 3000;
                        break;
                    case 'N': //White knight
                        whiteScore += 3000;
                        break;
                    case 'r': //Black rook
                        blackScore += 5000;
                        break;
                    case 'R': //White rook
                        whiteScore += 5000;
                        break;
                    case 'q': //Black queen
                        blackScore += 9000;
                        break;
                    case 'Q': //White queen
                        whiteScore += 9000;
                        break;
                    default:
                        break;
                }
            }
        }

        //Black king has been captured
        if(blackKingGone){
            if(move == 'B'){
                return -500000; //Lose value
            }
            return 500000; //Win value
        }
        //White king is gone
        else if(whiteKingGone){
            if(move == 'W'){
                return -500000; //Lost value
            }
            return 500000; //Win value
        }
        else{
            //Return the difference between the two sides to determine which move has the greatest impact
            if(move == 'W'){
                return whiteScore - blackScore;
            }
            else if(move == 'B'){
                return blackScore - whiteScore;
            }
            else{
                throw new IllegalStateException("Invalid move while evaluating game state!");
            }
        }
    }

    /*====================================================
    This function implements the alpha beta negamax search to determine the best score for the current side
    Params:
        1) State to evaluate.
        2) Depth: How far in the game tree to search for best move.
        3) Alpha
        4) Beta
    Returns:
        1) Integer with the score of the best move.
    ====================================================*/
    public int negamax(State s, int depth, int alpha, int beta){
        //Take the time
        elapsedTime = (System.nanoTime() - startTime) * 1e-9;

        //Base case (game is over, depth is reached, time has elapsed)
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

        //Call negamax for the first best move based on score
        int value = -(negamax(newStates.get(0), depth - 1, -beta, -alpha));
        if(value > beta){
            return value;
        }
        alpha = Integer.max(alpha, value);

        //Check the remaining moves
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

    /*====================================================
    This function returns a MoveInfo object (object which maps the negamax score with a Move) containing the best move
    to make given the search time and current state.
    Params:
        1) None.
    Returns:
        1) MoveInfo object containing the best move to make
    ====================================================*/
    public MoveInfo bestMove(){
        //Get start time for search
        startTime = System.nanoTime();

        //Get list of all possible moves
        ArrayList<Move> m = moveList();
        int moveSize = m.size();

        //Arraylist to maintain state and score of such moves
        ArrayList<MoveInfo> info = new ArrayList<>();

        //Execute each move and evaluate the score. Add the info to the Info List
        for (int i = 0; i < moveSize; i++) {
            MoveInfo newMove = new MoveInfo();
            newMove.move = m.get(i);
            newMove.state = move(newMove.move);
            newMove.score = -(newMove.state.eval());
            if(newMove.score == 500000){ //Game is over
                return newMove;
            }
            info.add(newMove);
        }

        //Shuffle and sort based on the score
        Collections.shuffle(info);
        Collections.sort(info, (m1, m2) -> Integer.compare(m2.score, m1.score));

        //Set current best move to the first in the sorted move list
        MoveInfo best = info.get(0);
        //MoveInfo best = null;

        //Negaxmax search
        int depth = 0; //Starting depth
        ArrayList<MoveInfo>bestMoves = new ArrayList<>(); //Overall best moves that can be made
        elapsedTime = (System.nanoTime() - startTime) * 1e-9; //Elapsed time

        while(elapsedTime < timeLimit){
            //Keep track of current best moves for current depth
            ArrayList<MoveInfo>currentBest = new ArrayList<>();
            int bestScore = -500000;
            depth++;

            for(int j = 0; j < moveSize; j++){
                MoveInfo current = info.get(j);
                current.score = -negamax(current.state, depth, -500000, 500000);

                if(current.score == 500000){ //Game winner, just return that move
                    return current;
                }

                if(current.score > bestScore){ //Better move has been found
                    bestScore = current.score; //Set best score to that value
                    currentBest.clear(); //Clear current best moves of any previous moves
                    currentBest.add(current); //Add the new move
                    //best = current;
                }
                else if(current.score == bestScore){
                    currentBest.add(current);
                }
            }

            //There's time to possible make another search so save current results
            if(elapsedTime < timeLimit){
                bestMoves.clear(); //Clear the list of best moves so that the updated results can be added
                bestMoves.addAll(currentBest);
            }
        }

        //System.out.println("Depth: " + depth);

        //If there's a list of best possible moves, pick a random one
        if(bestMoves.size() > 0){
            Random rand = new Random();
            int index = rand.nextInt(bestMoves.size());
            best = bestMoves.get(index);
        }
        return best;
    }
}
