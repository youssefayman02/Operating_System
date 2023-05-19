import java.io.*;
import java.util.Scanner;

public class SystemCalls {
    private Scheduler scheduler;

    public SystemCalls() {
        scheduler = new Scheduler();
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
        return scheduler.getMemory().get(address);
    }

    public void writeMemory(String address, Object data) {
        if (scheduler.getMemorySize() == 0) {

            scheduler.setMemorySize(scheduler.getMemorySize() + 1);
            Object value = scheduler.getMemory().remove(scheduler.getAddresses().get(0));
            String varibale = scheduler.getAddresses().remove(0);
            String diskInput = varibale + "," + value;
            this.writeFile("Disk", diskInput);

        }
        scheduler.setMemorySize(scheduler.getMemorySize() - 1);
        scheduler.putAddresses(address);
        scheduler.getMemory().put(address, data);
    }

    public static void main(String[] args) {
        SystemCalls sc = new SystemCalls();
        sc.writeMemory("Ahmed", 12);
        sc.writeMemory("Ali", 13);
        sc.writeMemory("Mohamed", 14);
        for (String key : sc.scheduler.getMemory().keySet()) {
            System.out.println(key + " " + sc.scheduler.getMemory().get(key));
        }
    }

}
