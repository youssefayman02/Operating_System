import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class Interpreter {

    public ArrayList<String> readProgram (String fileName)
    {
        ArrayList<String> program = new ArrayList<>();
        String path = "src/" + fileName + ".txt";

        try {
            FileReader fr = new FileReader(path);
            BufferedReader br = new BufferedReader(fr);
            String line = "";
            while ((line = br.readLine()) != null) {
                program.add(line);
            }
            br.close();
            fr.close();
        }
        catch (Exception e)
        {
            System.out.println("Exception in read file method");
        }

        return program;
    }

}
