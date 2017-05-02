import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * Created by Manpreet on 5/1/2017.
 */
public class MoveTests {

    public static void main(String[] args){

        State test = new State();

        System.out.println("Test 1");
        try{
            String newBoard = new String(Files.readAllBytes(Paths.get("moveTests/genTest2")));
            test.read(newBoard);
            ArrayList<Move> moves = test.moveList();
            int i = 1;
            for(Move m : moves){
                System.out.println("\nPossible Move " + i);
                State newMove = test.move(m);
                System.out.print(newMove.print());
                System.out.println(m.toString());
                i++;
            }
        }
        catch(IOException e){
            System.out.println("Unable to read file");
            System.exit(-1);
        }


    }
}
