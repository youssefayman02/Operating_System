import java.util.Hashtable;
import java.util.Vector;

public class Scheduler {

    private Hashtable<String, Object> memory;
    private static int memorySize = 40;

    private static Vector<String> addresses;

    public Scheduler() {
        this.memory = new Hashtable<String, Object>();
        this.addresses = new Vector<String>();

    }

    public Hashtable<String, Object> getMemory() {
        return memory;
    }

    public void setMemory(Hashtable<String, Object> memory) {
        this.memory = memory;
    }

    public static int getMemorySize() {
        return memorySize;
    }

    public static void setMemorySize(int memorySize) {
        Scheduler.memorySize = memorySize;
    }

    public static Vector<String> getAddresses() {
        return addresses;
    }

    public static void putAddresses(String address) {
        Scheduler.addresses.add(address);
    }

}
