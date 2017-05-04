import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Manpreet on 5/3/2017.
 */
public class RandomSelf {
    static Random rand = new Random();

    public static void main(String[]args){
        State game = new State();
        while(!game.over){
            //Make random moves
            ArrayList<Move> moves = game.moveList();
            game = game.move(moves.get(rand.nextInt(moves.size())));
            System.out.println(game.print());
        }

        switch (game.winner) {
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
