import java.util.ArrayList;

public class Process {
    private ProcessControlBlock pcb;
    private ArrayList<String> program;

    private Object temp;

    public Process(ProcessControlBlock pcb, ArrayList<String> program) {
        this.pcb = pcb;
        this.program = program;
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

    public Object getTemp() {
        return temp;
    }

    public void setTemp(Object temp) {
        this.temp = temp;
    }

    @Override
    public String toString() {
        return "PCB: " + pcb + " Program: " + program.toString() + "Temp Variable: " + this.temp;
    }
}
