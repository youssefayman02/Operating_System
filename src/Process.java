import java.util.ArrayList;

public class Process {
    private ProcessControlBlock pcb;
    private ArrayList<String> program;
    private int arrivalTime;

    public Process(ProcessControlBlock pcb, ArrayList<String> program, int arrivalTime) {
        this.pcb = pcb;
        this.program = program;
        this.arrivalTime = arrivalTime;
    }

    public ProcessControlBlock getPcb() {
        return pcb;
    }

    public void setPcb(ProcessControlBlock pcb) {
        this.pcb = pcb;
    }

    public ArrayList<String> getProgram() {
        return program;
    }

    public void setProgram(ArrayList<String> program) {
        this.program = program;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(int arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    @Override
    public String toString() {
        return "PCB: " + pcb + " Program: " + program.toString() + " ArrivalTime: " + arrivalTime;
    }
}
