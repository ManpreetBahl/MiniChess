/**
 * Created by Manpreet on 5/3/2017.
 */
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class HumanPlayer {
    static Random rand = new Random();

    public static void main(String[]args){
        State newGame = new State();
        Scanner scan = new Scanner(System.in);

        System.out.println("Game has begun!");
        while(!newGame.over){
            System.out.println(newGame.print());
            System.out.print("Enter move: ");
            String move = scan.nextLine();

            newGame = newGame.move(move);

            System.out.println(newGame.print());
            System.out.println();
            System.out.println("Computer is making move...");

            ArrayList<Move>moves = newGame.moveList();
            newGame = newGame.move(moves.get(rand.nextInt(moves.size())));
        }

        switch (newGame.winner) {
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
