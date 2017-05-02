/**
 * Created by Manpreet on 4/3/2017.
 */
import java.io.IOError;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class StateTest {

    public static void main(String[] args){
        //Test 1: Check the initialized value of a new board
        System.out.print("Test 1: Initialize new board\n");
        String initBoardValue = "1 W\nkqbnr\nppppp\n.....\n.....\nPPPPP\nRNBQK\n";
        State test = new State();
        if(initBoardValue.equals(test.print())){
            System.out.print("Test 1: PASS");
        }
        else{
            System.out.print("Test 1: FAIL");
            System.exit(-1);
        }

        System.out.println('\n');

        //Test 2: Update the board using the read function
        System.out.println("Test 2: Read new board state");
        String newBoard = "4 B\nkqbnr\n.p.pp\np.p..\n.P...\nP.PPP\nRNBQK\n";
        test.read(newBoard);

        if(newBoard.equals(test.print())){
            System.out.println("Test 2: PASS\n");
        }
        else{
            System.out.println("Test 2: FAIL\n");
            System.exit(-1);
        }

        //Test 3: Test the king
        System.out.println("Test 3: King Moves");
        try{
            newBoard = new String(Files.readAllBytes(Paths.get("moveTests/kingMoves")));
            test.read(newBoard);
            ArrayList<Move> moves = test.moveList();
            int i = 1;
            for(Move m : moves){
                System.out.println("Possible Move " + i);
                State newMove = test.move(m);
                System.out.println(newMove.print());
                i++;
            }
        }
        catch(IOException e){
            System.out.println("Unable to read file");
            System.exit(-1);
        }

        //Test 4: Test the queen
        System.out.println("Test 4: Queen Moves");
        try{
            newBoard = new String(Files.readAllBytes(Paths.get("moveTests/queenMoves")));
            test.read(newBoard);
            ArrayList<Move> moves = test.moveList();
            int i = 1;
            for(Move m : moves){
                System.out.println("Possible Move " + i);
                State newMove = test.move(m);
                System.out.println(newMove.print());
                i++;
            }
        }
        catch(IOException e){
            System.out.println("Unable to read file");
            System.exit(-1);
        }

        //Test 5: Test the rook
        System.out.println("Test 5: Rook Moves");
        try{
            newBoard = new String(Files.readAllBytes(Paths.get("moveTests/rookMoves")));
            test.read(newBoard);
            ArrayList<Move> moves = test.moveList();
            int i = 1;
            for(Move m : moves){
                System.out.println("Possible Move " + i);
                State newMove = test.move(m);
                System.out.println(newMove.print());
                i++;
            }
        }
        catch(IOException e){
            System.out.println("Unable to read file");
            System.exit(-1);
        }

        //Test 6: Test the knight
        System.out.println("Test 6: Knight Moves");
        try{
            newBoard = new String(Files.readAllBytes(Paths.get("moveTests/knightMoves")));
            test.read(newBoard);
            ArrayList<Move> moves = test.moveList();
            int i = 1;
            for(Move m : moves){
                System.out.println("Possible Move " + i);
                State newMove = test.move(m);
                System.out.println(newMove.print());
                i++;
            }
        }
        catch(IOException e){
            System.out.println("Unable to read file");
            System.exit(-1);
        }
    }
}
