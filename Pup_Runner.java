import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
public class Pup_Runner {
    public static void main(String[] args) {
        System.out.println("Welcome to PUP, the world's simplest programming language.\n" +
                "PUP is designed to be an introduction to programming for young students (3rd - 8th grade).");
        System.out.println("\nYou may now begin using PUP. Code away!\n");
        Scanner UI = new Scanner(System.in);
        ArrayList<String> l_program = new ArrayList<>();
        ArrayList<String> r_program = new ArrayList<>();
        boolean enterown = false;


        try (BufferedReader br = new BufferedReader(new FileReader("src\\program.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if(line.equals("run")){
                    break;
                }
                r_program.add(line);
                l_program.add(line.toLowerCase());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Programming:
        while(true){
            String command = UI.nextLine();
            if(command.equals("")) {
                continue;
            } else if(command.equalsIgnoreCase("run")){
                break Programming;
            }
            l_program.add(command.toLowerCase(Locale.ROOT));
            r_program.add(command);
        }

        Interpreter PUP = new Interpreter(l_program, r_program);
    }
}
