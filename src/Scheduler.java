import java.util.Hashtable;
import java.util.Vector;

public class Scheduler {

    private Memory memory;

    public Scheduler() {
        this.memory = new Memory();

    }

    public Memory getMemory() {
        return memory;
    }

    public void setMemory(Memory memory) {
        this.memory = memory;
    }

}
