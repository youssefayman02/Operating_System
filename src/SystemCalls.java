import java.io.*;
import java.util.Scanner;

public class SystemCalls {

    public SystemCalls() {

    }

    public String readFile(String fileName) {
        StringBuilder content = new StringBuilder();
        try {
            FileReader fr = new FileReader("src/" + fileName + ".txt");
            BufferedReader br = new BufferedReader(fr);
            String line;

            while ((line = br.readLine()) != null) {
                content.append(line + " ");
            }

            fr.close();
            br.close();

        } catch (Exception e) {
            System.out.println("File not found");
        }

        return content.toString();
    }

    public void writeFile(String fileName, String data) {
        try {
            File file = new File("src/" + fileName + ".txt");
            if (!file.exists()) {
                file.createNewFile();
            } else {
                System.out.println("File Already Exists");
            }
            FileWriter fw = new FileWriter(file);
            fw.write(data);
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void printData(Object data) {
        System.out.println(data);
    }

    public String takeInput() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Please enter a value ");
        String s = sc.nextLine();
        return s;
    }

    public Object readMemory(int index, Memory memory) {
        if (memory.isValidAddress(index)) {
            return memory.getMemoryWords()[index];
        }
        System.out.println("Invalid address to read from");
        return null;
    }

    public void writeMemory(Object data, int index, Memory memory) {
        if (memory.isValidAddress(index)) {
            memory.getMemoryWords()[index].setData(data);
        } else {
            System.out.println("Invalid address to write to");
        }
    }

}
