import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.List;
/**
 * Created by Manpreet on 5/13/2017.
 * This file is the game player
 */
public class MiniChessPlayer {

    //===================FIELDS===========================
    //Server
    private static String server = "imcs.svcs.cs.pdx.edu";

    //Port
    private static int port = 3589;

    //Username
    private static String username = null;

    //Password
    private static String password = null;

    //Client connection to the IMCS server
    private static Client con = null;

    //Game ID
    private static String gameId = "";

    //Player color
    private static char color = '?';

    //Scanner to read in input
    protected static Scanner scan = new Scanner(System.in);
    //======================================================

    /*======================================================
    This function is the main function which starts the game
    playing.
    Params:
        1) Args string array.
    Returns:
        1) None.
    ======================================================*/
    public static void main(String[]args){
        try{
            //Create client object
            con = new Client(server, port);

            //Login in to the server
            login();

            //Display the game menu
            menu();

            //Close connection
            con.close();
        }
        catch(IOException e){
            System.out.println("Error occured!");
            System.out.println(e.getStackTrace());
        }
    }

    /*======================================================
    This function handles the login to the IMCS server.
    Params:
        1) None.
    Returns:
        1) None.
    ======================================================*/
    public static void login() throws IOException{
        while (true) {
            //Get username
            System.out.print("Enter username: ");
            username = scan.nextLine();

            //Get password
            System.out.print("Enter password: ");
            password = scan.nextLine();

            //Attempt to connect to the server
            try{
                con.login(username, password);
                //Successful login so break
                break;
            }
            catch (RuntimeException e){
                System.out.println("Invalid credentials!");
            }
        }
    }

    /*======================================================
    This function displays the game options once a successful login
    has taken place. It allows to offer a game on the IMCS server or
    to accept a game given a list of available Game IDS.
    Params:
        1) None.
    Returns:
        1) None.
    ======================================================*/
    public static void menu() throws IOException{
        int input = 0;
        try{
            System.out.println("=======================================================");
            System.out.println("Welcome to MiniChess! Please select from the following:");
            System.out.println("1: Offer an IMCS game");
            System.out.println("2: Accept an IMCS game");
            System.out.println("3: Quit");
            System.out.print("Enter choice: ");
            input = scan.nextInt();
            switch(input){
                case 1: //Offer an IMCS game
                    offerGame();
                    break;
                case 2: //Accepth an IMCS game
                    scan.nextLine(); //Remove anything that may interfere with game
                    acceptGame();
                    break;
                case 3: //Quit the program
                    System.out.println("Terminating program!");
                    System.out.println("========================================================");
                    System.exit(0);
                    break;
                default: //Invalid option
                    System.out.println("Invalid input!!");
                    System.out.println("========================================================");
                    break;
            }
        }
        catch(InputMismatchException e){
            System.out.println("Invalid input! Please try again!");
            scan.next();
        }
    }

    /*======================================================
    This function allows the user to offer a game on the IMCS
    server.
    Params:
        1) None.
    Returns:
        1) None.
    ======================================================*/
    public static void offerGame(){
        while(true){
            try{
                //Select which color (or random) to offer the game as
                System.out.println("========================OFFER===========================");
                System.out.print("Offer as (W)hite, (B)lack, or (R)andom: ");
                color = Character.toUpperCase(scan.nextLine().charAt(0));
                System.out.println("========================================================");
                switch (color){
                    case 'W': //Offer as white
                        con.offerGameAndWait('W');
                        break;
                    case 'B': //Offer as black
                        con.offerGameAndWait('B');
                        break;
                    case 'R': //Let the server decide
                        color = con.offerGameAndWait();
                        break;
                    default:
                        throw new IllegalArgumentException();
                }
                //Play the game once an offer has been accepted
                playGame();
                break;
            }
            catch (IOException e){
                System.out.println("Unable to offer game! Please try again!");
                System.out.println("========================================================");
            }
            catch (RuntimeException e){
                System.out.println("Unable to offer game! Please try again!");
                System.out.println("========================================================");
            }
        }
    }

    /*======================================================
    This function allows the user to accept a game based on a list of available games that is displayed
    Params:
        1) None.
    Returns:
        1) None.
    ======================================================*/
    public static void acceptGame(){
        while(true){
            try{
                System.out.println("========================ACCEPT==========================");
                //Get the list of games available
                List<IMCSGame> available = con.getGameList();
                if(available.size() == 0){
                    System.out.println("No available games!");
                    return;
                }
                else{
                    System.out.println("Available games: ");
                }

                //Display list of games available
                for (IMCSGame game: available) {
                    if(!game.isRunning) {
                        System.out.println(game.gameId + " " + game.reservedPlayer);
                    }
                }

                //Get desired game ID from the user
                System.out.print("Enter Game ID: ");
                gameId = scan.nextLine();
                System.out.println("========================================================");

                //Let the server decide the color
                color = con.accept(gameId);
                //Play the game
                playGame();
                break;
            }
            catch(IOException e){
                System.out.println("Invalid game ID! Please try again!");
                System.out.println("========================================================");
            }
            catch (RuntimeException e){
                System.out.println("Could not accept game! Please try another game!");
                System.out.println("========================================================");
            }
        }
    }

    /*======================================================
    This function plays the game of Minichess after a game has been accepted. It utilizes the alpha-negamax to
    determine the best move and sends it over the server. It then waits for the move from the opponent.
    Params:
        1) None.
    Returns:
        1) None.
    ======================================================*/
    public static void playGame() throws IOException{
        State board = new State();
        String oppMove = "";
        while(!board.over){
            if(color == 'W'){ //My color is white
                if(board.move == 'W'){ //Board turn is white's
                    MoveInfo move = board.bestMove();
                    board = move.state;
                    con.sendMove(move.move.toString());
                }
                else{ //Wait for opponent's move
                    oppMove = con.getMove();
                    if(oppMove != null){
                        board = board.move(oppMove);
                    }
                    else{
                        break;
                    }
                }
            }
            else{ //My color is black
                if(board.move == 'B'){
                    MoveInfo move = board.bestMove();
                    board = move.state;
                    con.sendMove(move.move.toString());
                }
                else{ //Wait for opponent's move
                    oppMove = con.getMove();
                    if(oppMove != null){
                        board = board.move(oppMove);
                    }
                    else{
                        break;
                    }
                }
            }
        }

        //Game is over so display the results
        if(board.winner == color){ //Won the game
            System.out.println("Game over! You won the match!");
        }
        else if(board.winner == 'D'){ //Game is a draw
            System.out.println("Game over! The game is a draw!");
        }
        else if(oppMove == null){ //Some other message was received
            System.out.println("Game over!");
        }
        else if(board.winner != color){ //Lost the game
            System.out.println("Game over! You lost the match!");
        }
        else{
            throw new IllegalStateException("Something happened! Game is over but no winner?");
        }
    }
}
