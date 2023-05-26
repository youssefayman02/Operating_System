
public class Memory {
    private MemoryWord[] memoryWords;
    private final int memorySize = 40;

    public Memory() {
        this.memoryWords = new MemoryWord[memorySize];
        initializeFirstPartition();
        initializeSecondPartition();
    }

    public MemoryWord[] getMemoryWords() {
        return memoryWords;
    }

    public void setMemoryWords(MemoryWord[] memoryWords) {
        this.memoryWords = memoryWords;
    }

    public int getMemorySize() {
        return memorySize;
    }

    public boolean isValidAddress(int address) {
        return address >= 0 && address < memorySize;
    }

    public void printMemory() {
        System.out.println("Memory Contents: ");
        System.out.println(
                "**************************************** First Partition ****************************************");

        for (int i = 0; i < 20; i++) {
            System.out.println("Address " + i + ": " + this.memoryWords[i].toString());
        }

        System.out.println(
                "**************************************** Second Partition ****************************************");

        for (int i = 20; i < memorySize; i++) {
            System.out.println("Address " + i + ": " + this.memoryWords[i].toString());
        }

        System.out.println(
                "**************************************** End Memory ****************************************");
    }

    public boolean isFirstPartitionEmpty() {
        return memoryWords[0].getData() == null;
    }

    public boolean isSecondPartitionEmpty() {
        return memoryWords[20].getData() == null;
    }

    public boolean isEmpty() {
        return isFirstPartitionEmpty() || isSecondPartitionEmpty();
    }

    public void initializeFirstPartition() {
        for (int i = 0; i < 20; i++) {
            this.memoryWords[i] = new MemoryWord();
        }

        this.memoryWords[0].setAddress("Process Id");
        this.memoryWords[1].setAddress("Process State");
        this.memoryWords[2].setAddress("PC");
        this.memoryWords[3].setAddress("Start Boundary");
        this.memoryWords[4].setAddress("End Boundary");
        this.memoryWords[5].setAddress("Variable 1");
        this.memoryWords[6].setAddress("Variable 2");
        this.memoryWords[7].setAddress("Variable 3");

        for (int i = 8; i < 20; i++) {
            this.memoryWords[i].setAddress("Instruction " + (i - 8));
        }
    }

    public void initializeSecondPartition() {
        for (int i = 20; i < memorySize; i++) {
            this.memoryWords[i] = new MemoryWord();
        }

        this.memoryWords[20].setAddress("Process Id");
        this.memoryWords[21].setAddress("Process State");
        this.memoryWords[22].setAddress("PC");
        this.memoryWords[23].setAddress("Start Boundary");
        this.memoryWords[24].setAddress("End Boundary");
        this.memoryWords[25].setAddress("Variable 1");
        this.memoryWords[26].setAddress("Variable 2");
        this.memoryWords[27].setAddress("Variable 3");

        for (int i = 28; i < memorySize; i++) {
            this.memoryWords[i].setAddress("Instruction " + (i - 28));
        }
    }

}
