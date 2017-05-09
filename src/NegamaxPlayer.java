/**
 * Created by Manpreet on 5/7/2017.
 */
public class NegamaxPlayer {
    public static void main(String[]args){
        State board = new State();
        while(!board.over){
            System.out.println(board.print());

            MoveInfo test = board.bestMove();
            board = test.state;
            System.out.println(board.move + " Score: " + test.score);
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
