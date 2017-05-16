import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.IOException;
import java.text.CharacterIterator;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;
/**
 * Created by Manpreet on 5/13/2017.
 */
public class MiniChessPlayer {

    //Fields
    private static String server = "imcs.svcs.cs.pdx.edu";
    private static String port = "3589";
    private static String username = "TheLegend28";
    private static String password = "EpicPassword";

    private static Client con = null;

    private static String gameId = "";
    private static char color = '?';
    private static String opponent = "?";


    protected static Scanner scan = new Scanner(System.in);


    public static void main(String[]args) throws IOException{
        con = new Client(server, port, username, password);
        menu();
        con.close();
    }

    public static void menu() throws IOException{
        int input = 0;
        System.out.println("=======================================================");
        System.out.println("Welcome to MiniChess! Please select from the following:");
        System.out.println("1: Offer an IMCS game");
        System.out.println("2: Accept an IMCS game");
        System.out.println("3: Quit");
        //Get a valid input
        while(true){
            try{
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
                break;
            }
            catch(InputMismatchException e){
                System.out.println("Invalid input! Please try again!");
                scan.next();
            }
        }
    }

    public static void offerGame() throws IOException{
        //Get a valid input
        while(true){
            try{
                System.out.print("Offer as (W)hite, (B)lack, or (R)andom: ");
                color = Character.toUpperCase(scan.next().charAt(0));
                System.out.println("========================================================");
                switch (color){
                    case 'W':
                        color = con.offer('W');
                        break;
                    case 'B':
                        color = con.offer('B');
                        break;
                    case 'R':
                        color = con.offer('?');
                        break;
                    default:
                        throw new IllegalArgumentException();
                }
                playGame();
                break;
            }
            catch(InputMismatchException e){
                System.out.println("Invalid input! Please try again!");
                System.out.println("========================================================");
            }
            catch(IllegalArgumentException f){
                System.out.println("Invalid input! Please try again!");
                System.out.println("========================================================");
            }
        }
    }


    public static void acceptGame() throws IOException{
        while(true){
            try{
                System.out.print("Enter Game ID: ");
                gameId = scan.nextLine();
                System.out.println("========================================================");
                color = con.accept(gameId, '?'); //Let the server decide what color I'll get for now==========
                playGame();
            }
            catch(IOException e){
                System.out.println("Invalid game ID! Please try again!");
                System.out.println("========================================================");
            }
        }
    }

    public static void playGame() throws IOException{
        State board = new State();
        while(!board.over){
            if(color == 'W'){ //My color is white
                if(board.move == 'W'){ //Board turn is white's
                    MoveInfo move = board.bestMove();
                    board = move.state;
                    con.sendMove(move.move.toString());
                }
                else{ //Wait for opponent's move
                    String oppMove = con.getMove();
                    board = board.move(oppMove);
                }
            }
            else{ //My color is black
                if(board.move == 'B'){
                    MoveInfo move = board.bestMove();
                    board = move.state;
                    con.sendMove(move.move.toString());
                }
                else{ //Wait for opponent's move
                    String oppMove = con.getMove();
                    board = board.move(oppMove);
                }
            }
        }
        switch (board.winner) {
            case 'W':
                System.out.println("Game over! The winner is White!");
                break;
            case 'B':
                System.out.println("Game over! The winner is Black!");
                break;
            case 'D':
                System.out.println("Game over! The game is a draw!");
                break;
            default:
                throw new IllegalStateException("Something happened! Game is over but no winner?");
        }
    }

}
