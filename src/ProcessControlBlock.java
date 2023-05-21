public class ProcessControlBlock {
    private int processId;
    private ProcessState processState;
    private int programCounter;
    private int startBoundary;
    private int endBoundary;

    public ProcessControlBlock(int processId, ProcessState processState, int programCounter, int startBoundary, int endBoundary) {
        this.processId = processId;
        this.processState = processState;
        this.programCounter = programCounter;
        this.startBoundary = startBoundary;
        this.endBoundary = endBoundary;
    }

    public int getProcessId() {
        return processId;
    }

    public void setProcessId(int processId) {
        this.processId = processId;
    }

    public ProcessState getProcessState() {
        return processState;
    }

    public void setProcessState(ProcessState processState) {
        this.processState = processState;
    }

    public int getProgramCounter() {
        return programCounter;
    }

    public void setProgramCounter(int programCounter) {
        this.programCounter = programCounter;
    }

    public int getStartBoundary() {
        return startBoundary;
    }

    public void setStartBoundary(int startBoundary) {
        this.startBoundary = startBoundary;
    }

    public int getEndBoundary() {
        return endBoundary;
    }

    public void setEndBoundary(int endBoundary) {
        this.endBoundary = endBoundary;
    }

    @Override
    public String toString() {
        return "Process id: " + this.processId + " Process State: " + processState + " PC: " + programCounter + " Start Boundary: " + startBoundary
        + " End Boundary: " + endBoundary;
    }
}
