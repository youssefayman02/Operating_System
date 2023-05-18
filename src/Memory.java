public class Memory {
    private Object[] memory;
    private final int memorySize = 40;

    public Memory() {
        this.memory = new Object[memorySize];
    }

    public Object[] getMemory() {
        return memory;
    }

    public void setMemory(Object[] memory) {
        this.memory = memory;
    }
}
