import java.util.Vector;

public class Memory {
    private Vector<MemoryWord> memoryWords;
    private final int memorySize = 40;
    private Vector<ProcessControlBlock> pcbs;


    public Memory() {
        this.memoryWords = new Vector<MemoryWord>();
    }

    public Vector<MemoryWord> getMemoryWords() {
        return memoryWords;
    }

    public void setMemoryWords(Vector<MemoryWord> memoryWords) {
        this.memoryWords = memoryWords;
    }

    public int getMemorySize() {
        return memorySize;
    }

    // public boolean isEmpty(int startIndex, int endIndex) {
    // if (endIndex < startIndex) {
    // return false;
    // }

    // for (int i = startIndex; i < endIndex; i++) {
    // if (this.data[i] != null) {
    // return false;
    // }
    // }

    // return true;
    // }

}
