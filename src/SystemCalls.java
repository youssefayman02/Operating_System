import java.io.*;
import java.util.Scanner;

public class SystemCalls {
    private Scheduler scheduler;

    public SystemCalls(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public String readFile(String fileName) {
        StringBuilder content = new StringBuilder();
        try {
            FileReader fr = new FileReader("src/" + fileName + ".txt");
            BufferedReader br = new BufferedReader(fr);
            String line;

            while ((line = br.readLine()) != null) {
                content.append(line + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
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

    public void printData(String data) {
        System.out.println(data);
    }

    public String takeInput() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Please enter a value");
        return sc.nextLine();
    }

    public Object readMemory(String address) {
        for (MemoryWord index : scheduler.getMemory().getMemoryWords()) {
            if (index.getAddress().equals(address)) {
                return index;

            }
        }
        return null;
    }

    public void writeMemory(String address, Object data) {
        if (scheduler.getMemory().getMemoryWords().size() == 40) {
            MemoryWord olWord = scheduler.getMemory().getMemoryWords().remove(0);

            String diskInput = olWord.getAddress() + "," + olWord.getData() + "\n";

            this.writeFile("Disk", diskInput);

        }
        MemoryWord newWord = new MemoryWord();
        newWord.setAddress(address);
        newWord.setData(data);
        scheduler.getMemory().getMemoryWords().add(newWord);

    }

    public static void main(String[] args) {

    }

}
