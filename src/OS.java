
public class OS {
    private Scheduler scheduler;

    public OS(String program1, String program2, String program3, int arrivalTime1, int arrivalTime2, int arrivalTime3,int quantum) {
        this.scheduler = new Scheduler(program1, program2, program3, arrivalTime1, arrivalTime2, arrivalTime3, quantum);
    }

    public void simulate()
    {
        scheduler.run();
    }

    public static void main(String[] args) {
        OS os = new OS("Program_1", "Program_2", "Program_3", 0, 1, 4, 2);
        os.simulate();
    }
}
