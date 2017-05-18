import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.IOException;
import java.text.CharacterIterator;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.List;
/**
 * Created by Manpreet on 5/13/2017.
 */
public class MiniChessPlayer {

    //Fields
    private static String server = "imcs.svcs.cs.pdx.edu";
    private static int port = 3589;
    private static String username = null;
    private static String password = null;

    private static Client con = null;

    private static String gameId = "";
    private static char color = '?';

    protected static Scanner scan = new Scanner(System.in);

    public static void main(String[]args) throws IOException{
        try{
            con = new Client(server, port);;
            login();
            menu();
            con.close();
        }
        catch(IOException e){
            System.out.println("Error occured!");
            System.out.println(e.getStackTrace());
        }
    }

    public static void login() throws IOException{
        while (true) {
            System.out.print("Enter username: ");
            username = scan.nextLine();
            System.out.print("Enter password: ");
            password = scan.nextLine();

            try{
                con.login(username, password);
                break; //Successful login so break
            }
            catch (RuntimeException e){
                System.out.println("Invalid credentials!");
            }
        }
    }

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
                case 2:
                    scan.nextLine(); //Remove anything that may interfere with game
                    acceptGame();
                    break;
                case 3:
                    System.out.println("Terminating program!");
                    System.out.println("========================================================");
                    System.exit(0);
                    break;
                default:
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

    public static void offerGame(){
        while(true){
            try{
                System.out.println("========================OFFER===========================");
                System.out.print("Offer as (W)hite, (B)lack, or (R)andom: ");
                color = Character.toUpperCase(scan.nextLine().charAt(0));
                System.out.println("========================================================");
                switch (color){
                    case 'W':
                        con.offerGameAndWait('W');
                        break;
                    case 'B':
                        con.offerGameAndWait('B');
                        break;
                    case 'R':
                        color = con.offerGameAndWait();
                        break;
                    default:
                        throw new IllegalArgumentException();
                }
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
                System.out.print("Enter Game ID: ");
                gameId = scan.nextLine();
                System.out.println("========================================================");
                color = con.accept(gameId); //Let the server decide the color
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
        if(board.winner == color){
            System.out.println("Game over! You won the match!");
        }
        else if(board.winner == 'D'){
            System.out.println("Game over! The game is a draw!");
        }
        else if(board.winner != color){
            System.out.println("Game over! You lost the match!");
        }
        else if(oppMove == null){
            System.out.println("Game over!");
        }
        else{
            throw new IllegalStateException("Something happened! Game is over but no winner?");
        }
    }
}
