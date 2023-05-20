import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class Parser {

    public ArrayList<String> parser(String path) throws Exception {
        FileReader fr = new FileReader(path);
        BufferedReader br = new BufferedReader(fr);
        String s;
        ArrayList<String> result = new ArrayList<String>();
        while ((s = br.readLine()) != null) {
            result.add(s);
        }
        br.close();
        fr.close();
        return result;
    }
}
