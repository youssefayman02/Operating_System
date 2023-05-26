import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class Interpreter {

    public ArrayList<String> readProgram (String fileName){
        ArrayList<String> program = new ArrayList<>();
        String path = "src/" + fileName + ".txt";

        try {
            FileReader fr = new FileReader(path);
            BufferedReader br = new BufferedReader(fr);
            String line = "";

            while ((line = br.readLine()) != null)
            {

                String[] args = line.split(" ");

                if (args[0].equals("assign"))
                {
                    String res = "";
                    for (int i = 2; i < args.length; i++)
                    {
                        res += args[i] + " ";
                    }

                    program.add(res);
                    program.add(args[0] + " " + args[1]);
                }
                else
                {
                    program.add(line);
                }

            }

            br.close();
            fr.close();
        }
        catch (Exception e)
        {
            System.out.println("Exception in read file method");
        }

        if (program.size() > 12)
        {
            System.out.println("Instructions exceeded its size limit");
        }

        return program;
    }

}
