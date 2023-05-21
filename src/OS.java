import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Queue;

public class OS {
    private Interpreter interpreter;
    private Scheduler scheduler;
    private String program1;
    private String program2;
    private String program3;
    private int arrivalTime1;
    private int arrivalTime2;
    private int arrivalTime3;
    private int timeSlice;
    private int instructionsPerTimeSlice;
    private int processes;

    public OS(String program1, String program2, String program3, int arrivalTime1, int arrivalTime2, int arrivalTime3, int timeSlice, int instructionsPerTimeSlice) {
        this.program1 = program1;
        this.program2 = program2;
        this.program3 = program3;
        this.arrivalTime1 = arrivalTime1;
        this.arrivalTime2 = arrivalTime2;
        this.arrivalTime3 = arrivalTime3;
        this.timeSlice = timeSlice;
        this.instructionsPerTimeSlice = instructionsPerTimeSlice;
        this.interpreter = new Interpreter();
        this.scheduler = new Scheduler(timeSlice);
        this.processes = 0;
    }

    public void createProcess (String fileName, int arrivalTime)
    {
        ArrayList<String> program = this.interpreter.readProgram(fileName);
        ProcessControlBlock pcb = new ProcessControlBlock(processes++,ProcessState.READY,0,0, 0);
        Process process = new Process(pcb,program,arrivalTime);
        scheduler.addToReadyQueue(process);
        scheduler.run();
    }


    public static void main(String[] args) {
        OS os = new OS("Program_1","Program_2","Program_3", 0, 1, 2,2,2);
        os.createProcess("Program_3",0);
    }
}
