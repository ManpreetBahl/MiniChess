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
                System.out.println(m.toString());
                /*
                System.out.println("Possible Move " + i);
                State newMove = test.move(m);
                System.out.println(newMove.print());
                */
                i++;
            }
        }
        catch(IOException e){
            System.out.println("Unable to read file");
            System.exit(-1);
        }


    }
}
