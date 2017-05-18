import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Scanner;

/**
 * Created by Manpreet on 5/1/2017.
 */
public class MoveTests {

    public static void main(String[] args){

        State test = new State();
        String filename;
        ArrayList<String>errors = new ArrayList<>();

        try{
            String newBoard = new String(Files.readAllBytes(Paths.get("moveTests/kingCapture")));
            test.read(newBoard);

            /*
            //Get list of moves and store the readable moves in arraylist
            ArrayList<Move> moves = test.moveList();
            ArrayList<String> result = new ArrayList<>();
            ArrayList<State> states = new ArrayList<>();
            for(Move m : moves){
                result.add(m.toString());
                states.add(test.move(m));
            }
            //System.out.println(result);
            for(State s: states){
                System.out.println(s.print());
                System.out.println("Score: " + s.eval() + "\tWinner: " + s.winner);
            }
            */
            MoveInfo best = test.bestMove();
            System.out.println("Best Move: " + best.move);
            System.out.println("Best Move State: " + best.state.print());
            System.out.println("Best Move Score: " + best.score);

        }
        catch(IOException e){
            System.out.println("Unable to read file");
            System.exit(-1);
        }


        for(int i = 0; i < 3; i++){
            switch (i){
                case 0:
                    filename = "king-vs-king";
                    break;
                case 1:
                    filename = "loss-no-moves";
                    break;
                case 2:
                    filename = "promote-and-capture";
                    break;
                default:
                    throw new IllegalStateException("Invalid file path!");
            }
            try{
                String newBoard = new String(Files.readAllBytes(Paths.get("genmoves-tests/" + filename + ".in")));
                test.read(newBoard);

                //Get list of moves and store the readable moves in arraylist
                ArrayList<Move> moves = test.moveList();
                ArrayList<String> result = new ArrayList<>();
                for(Move m : moves){
                    result.add(m.toString());
                }

                //Read output
                Scanner s = new Scanner(new File("genmoves-tests/" + filename+".out"));
                ArrayList<String> expected = new ArrayList<>();
                while (s.hasNext()){
                    expected.add(s.next());
                }
                s.close();

                //Sort the lists
                Collections.sort(expected);
                Collections.sort(result);

                if(!result.equals(expected)){
                    errors.add(filename);
                }
            }
            catch(IOException e){
                System.out.println("Unable to read file");
                System.exit(-1);
            }
        }

        //Try the random tests
        for(int i = 1; i < 101; i++){
            try{
                filename = "random-" + i;
                String newBoard = new String(Files.readAllBytes(Paths.get("genmoves-tests/" + filename + ".in")));
                test.read(newBoard);

                //Get list of moves and store the readable moves in arraylist
                ArrayList<Move> moves = test.moveList();
                ArrayList<String> result = new ArrayList<>();
                for(Move m : moves){
                    result.add(m.toString());
                }

                //Read output
                Scanner s = new Scanner(new File("genmoves-tests/" + filename+".out"));
                ArrayList<String> expected = new ArrayList<>();
                while (s.hasNext()){
                    expected.add(s.next());
                }
                s.close();

                //Sort the lists
                Collections.sort(expected);
                Collections.sort(result);

                if(!result.equals(expected)){
                    System.out.println();
                    System.out.println("Expected:\n" + expected);
                    System.out.println("Result:\n" + result);
                    System.out.println();
                    errors.add(filename);
                }
            }
            catch(IOException e){
                System.out.println("Unable to read file");
                System.exit(-1);
            }
        }

        if(errors.isEmpty()){
            System.out.println("All tests have passed!");
        }
        else{
            System.out.println("The following tests have failed:");
            for(String error: errors){
                System.out.println(error);
            }
        }
    }
}
