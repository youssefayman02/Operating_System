import java.io.FileReader;
import java.util.*;

public class Scheduler {
    private Memory memory;
    private SystemCalls systemCalls;
    private Mutexes mutexes;
    private Queue<Process> readyQueue;
    private Queue<Process> blockedQueue;
    private int instructionPerClkCycle;
    private int clkCycles;

    public Scheduler(int instructionPerClkCycle)
    {
        this.instructionPerClkCycle = instructionPerClkCycle;
        this.memory = new Memory();
        this.systemCalls = new SystemCalls();
        this.mutexes = new Mutexes();
        this.readyQueue = new LinkedList<>();
        this.blockedQueue = new LinkedList<>();
        this.clkCycles = 0;

    }

    public Queue<Process> getReadyQueue() {
        return readyQueue;
    }

    public void setReadyQueue(Queue<Process> readyQueue) {
        this.readyQueue = readyQueue;
    }

    public Queue<Process> getBlockedQueue() {
        return blockedQueue;
    }

    public void setBlockedQueue(Queue<Process> blockedQueue) {
        this.blockedQueue = blockedQueue;
    }

    public void run()
    {
        System.out.println("---------------------------------------- Scheduler Starts ----------------------------------------");
        while (!readyQueue.isEmpty())
        {
            Process process = readyQueue.poll();
            int processId = process.getPcb().getProcessId();
            if (process.getArrivalTime() > clkCycles)
            {
                this.readyQueue.add(process);
                this.clkCycles++;
            }
            else
            {
                System.out.println("---------------------------------------- Clk Cycle " + clkCycles + " ----------------------------------------");
                System.out.println("---------------------------------------- Process " + processId + " runs ----------------------------------------");
                System.out.println("Ready Queue: " + this.readyQueue);
                System.out.println("Blocked Queue: " + this.blockedQueue);

                if (!isProcessExistInMemory(processId))
                {
                    checkPlaceInMemory(process);
                }
                memory.printMemory();
                setProcessStatusInMemory(ProcessState.RUNNING, processId);

                int programCounter = process.getPcb().getProgramCounter();
                ArrayList<String> program = process.getProgram();

                boolean isBlocked = false;
                int i = instructionPerClkCycle;
                while (i-- > 0)
                {
                    String[] args = program.get(programCounter).split(" ");
                    switch (args[0])
                    {
                        case "print":
                            executePrint(args, process);
                            break;
                        case "assign":
                            executeAssign(args, process);
                            break;
                        case "writeFile":
                            executeWriteFile(args, process);
                            break;
                        case "printFromTo":
                            executePrintFromTo(args, process);
                            break;
                        case "semWait":
                            isBlocked = !executeSemWait(args, process);
                            break;
                        case "semSignal":
                            executeSemSignal(args, process);
                            break;
                        default :
                            System.out.println("Instruction is not supported"); break;

                    }

                    programCounter++;
                    process.getPcb().setProgramCounter(programCounter);
                    clkCycles++;

                    if (isBlocked)
                    {
                        System.out.println("---------------------------------------- Block Event Occurred ----------------------------------------");
                        System.out.println("Ready Queue: " + this.readyQueue);
                        System.out.println("Blocked Queue: " + this.blockedQueue);
                        process.getPcb().setProcessState(ProcessState.BLOCKED);
                        blockedQueue.add(process);
                        setProcessStatusInMemory(ProcessState.BLOCKED, process.getPcb().getProcessId());
                        break;
                    }
                    else if (programCounter == program.size())
                    {
                        System.out.println("---------------------------------------- Process " + processId + " finished ----------------------------------------");
                        System.out.println("Ready Queue: " + this.readyQueue);
                        System.out.println("Blocked Queue: " + this.blockedQueue);
                        process.getPcb().setProcessState(ProcessState.FINISHED);
                        removeFromMemory(processId);
                        break;
                    }
                }

                if (!isBlocked && programCounter < program.size())
                {
                    this.readyQueue.add(process);
                }

            }
        }
    }

    public void executePrint(String[] args, Process process)
    {
        System.out.println("---------------------------------------- Print Instruction ----------------------------------------");
        Object var = getVarFromMemory(args[1], process.getPcb().getProcessId());
        systemCalls.printData(var);
    }

    public void executeAssign(String[] args, Process process)
    {
        System.out.println("---------------------------------------- Assign Instruction ----------------------------------------");
        String address = args[1];
        String value;
        if (args[2].equals("input"))
        {
            value = systemCalls.takeInput();
        }
        else
        {
            value = executeReadFile(args, process);
        }

        assignVariableInMemory(address, value, process.getPcb().getProcessId());

    }

    public void executeWriteFile(String[] args, Process process)
    {
        System.out.println("---------------------------------------- Write File Instruction ----------------------------------------");
        String fileName = (String) getVarFromMemory(args[1], process.getPcb().getProcessId());
        String value = (String) getVarFromMemory(args[2], process.getPcb().getProcessId());
        systemCalls.writeFile(fileName, value);

    }

    public String executeReadFile(String[] args, Process process)
    {
        System.out.println("---------------------------------------- Read File Instruction ----------------------------------------");
        String fileName = (String) getVarFromMemory(args[3], process.getPcb().getProcessId()) ;
        String data = systemCalls.readFile(fileName);
        return data;

    }

    public void executePrintFromTo(String[] args, Process process)
    {
        System.out.println("---------------------------------------- PrintFromTo Instruction ----------------------------------------");
        int var1 = Integer.parseInt((String) getVarFromMemory(args[1], process.getPcb().getProcessId()));
        int var2 = Integer.parseInt((String) getVarFromMemory(args[2], process.getPcb().getProcessId()));
        if (var1 < var2)
        {
            for (int i = var1; i <= var2; i++)
            {
                systemCalls.printData(i);
            }
        }
        else
        {
            for (int i = var2; i <= var1; i++)
            {
                systemCalls.printData(i);
            }
        }

    }

    public boolean executeSemWait(String[] args, Process process)
    {
        System.out.println("---------------------------------------- Sem Wait Instruction ----------------------------------------");

        boolean flag = mutexes.semWait(args[1], process);

        System.out.println("User Input Queue: " + mutexes.getBlockedUserInput().toString());
        System.out.println("User Output Queue: " + mutexes.getBlockedUserOutput().toString());
        System.out.println("File  Queue: " + mutexes.getBlockedFile().toString());

        if (!flag)
        {
            this.blockedQueue.add(process);
            process.getPcb().setProcessState(ProcessState.BLOCKED);
            setProcessStatusInMemory(ProcessState.BLOCKED, process.getPcb().getProcessId());
        }

        return flag;
    }

    public void executeSemSignal(String[] args, Process process)
    {
        System.out.println("---------------------------------------- Sem Signal Instruction ----------------------------------------");

        int processId = mutexes.semSignal(args[0], process);

        System.out.println("User Input Queue: " + mutexes.getBlockedUserInput().toString());
        System.out.println("User Output Queue: " + mutexes.getBlockedUserOutput().toString());
        System.out.println("File  Queue: " + mutexes.getBlockedFile().toString());

        if (processId > -1)
        {
            deleteFromBlocked(processId);
        }

    }

    public void deleteFromBlocked(int processId)
    {
        Queue<Process> result = new LinkedList<>();

        while (!blockedQueue.isEmpty())
        {
            Process target = blockedQueue.poll();

            if (! (processId == target.getPcb().getProcessId()))
            {
                result.add(target);
            }
            else
            {
                this.readyQueue.add(target);
            }
        }

        while (!result.isEmpty())
        {
            Process target = result.poll();
            blockedQueue.add(target);
        }
    }

    public void addToReadyQueue (Process p)
    {
        this.readyQueue.add(p);
    }

    public Object getVarFromMemory(String address, int processId)
    {
        if (!memory.isFirstPartitionEmpty())
        {
            int firstPortionId = (int) memory.getMemoryWords()[0].getData();
            if (firstPortionId == processId)
            {
                String var1 = memory.getMemoryWords()[5].getAddress();
                Object value1 = memory.getMemoryWords()[5].getData();

                String var2 = memory.getMemoryWords()[6].getAddress();
                Object value2 = memory.getMemoryWords()[6].getData();

                String var3 = memory.getMemoryWords()[7].getAddress();
                Object value3 = memory.getMemoryWords()[7].getData();

                if (address.equals(var1))
                {
                    return value1;
                }
                else if (address.equals(var2))
                {
                    return value2;
                }
                else
                {
                    return value3;
                }
            }
        }
        else if (!memory.isSecondPartitionEmpty())
        {
            int secondPortionId = (int) memory.getMemoryWords()[20].getData();
            if (secondPortionId == processId)
            {
                String var1 = memory.getMemoryWords()[25].getAddress();
                Object value1 = memory.getMemoryWords()[25].getData();

                String var2 = memory.getMemoryWords()[26].getAddress();
                Object value2 = memory.getMemoryWords()[26].getData();

                String var3 = memory.getMemoryWords()[27].getAddress();
                Object value3 = memory.getMemoryWords()[27].getData();

                if (address.equals(var1))
                {
                    return value1;
                }
                else if (address.equals(var2))
                {
                    return value2;
                }
                else
                {
                    return value3;
                }
            }
        }
        System.out.println("Logic error in getVarFromMemory method");
        return null;
    }

    public void setProcessStatusInMemory(ProcessState state, int processId)
    {
        if (!memory.isFirstPartitionEmpty())
        {
            int firstPortionId = (int) memory.getMemoryWords()[0].getData();
            if (firstPortionId == processId)
            {
                memory.getMemoryWords()[1].setData(state);
            }
        }
        else if (!memory.isSecondPartitionEmpty())
        {
            int secondPortionId = (int) memory.getMemoryWords()[20].getData();
            if (secondPortionId == processId)
            {
                memory.getMemoryWords()[21].setData(state);
            }
        }

    }

    public void checkPlaceInMemory(Process process)
    {
        if (memory.isFirstPartitionEmpty())
        {
            process.getPcb().setStartBoundary(0);
            process.getPcb().setEndBoundary(19);
            populateMemory(process);
            return;
        }
         if (memory.isSecondPartitionEmpty())
        {
            process.getPcb().setStartBoundary(21);
            process.getPcb().setEndBoundary(memory.getMemorySize() - 1);
            populateMemory(process);
            return;
        }

        swapToDisk(process);

    }

    public void assignVariableInMemory(String address, String value, int processId)
    {
        if (!memory.isFirstPartitionEmpty())
        {
            int firstPortionId = (int) memory.getMemoryWords()[0].getData();
            if (firstPortionId == processId)
            {
                for (int i = 5; i < 8; i++)
                {
                    Object data = memory.getMemoryWords()[i].getData();
                    if (data == null)
                    {
                        memory.getMemoryWords()[i].setAddress(address);
                        memory.getMemoryWords()[i].setData(value);
                        return;
                    }
                }
            }
        }
        else if (!memory.isSecondPartitionEmpty())
        {
            int secondPortionId = (int) memory.getMemoryWords()[20].getData();
            if (secondPortionId == processId)
            {
                for (int i = 25; i < 28; i++)
                {
                    Object data = memory.getMemoryWords()[i].getData();
                    if (data == null)
                    {
                        memory.getMemoryWords()[i].setAddress(address);
                        memory.getMemoryWords()[i].setData(value);
                        return;
                    }
                }
            }
        }
        System.out.println("Logical error in assignVariableInMemory");
    }

    public void removeFromMemory(int processId)
    {
        if (!memory.isFirstPartitionEmpty())
        {
            int firstPortionId = (int) memory.getMemoryWords()[0].getData();
            if (firstPortionId == processId)
            {
                memory.initializeFirstPartition();
            }
        }
        else if (!memory.isSecondPartitionEmpty())
        {
            int secondPortionId = (int) memory.getMemoryWords()[20].getData();
            if (secondPortionId == processId) {
                memory.initializeSecondPartition();
            }
        }
    }


    public void populateMemory(Process process)
    {
        int processId = process.getPcb().getProcessId();
        int start = process.getPcb().getStartBoundary();
        int end = process.getPcb().getEndBoundary();
        ArrayList<String> program = process.getProgram();

        systemCalls.writeMemory(process.getPcb().getProcessId(), start,memory);
        systemCalls.writeMemory(process.getPcb().getProcessState(), start+1,memory);
        systemCalls.writeMemory(process.getPcb().getProgramCounter(), start+2,memory);
        systemCalls.writeMemory(process.getPcb().getStartBoundary(), start+3,memory);
        systemCalls.writeMemory(process.getPcb().getEndBoundary(), start+4,memory);
//        memory.getMemoryWords()[start].setData(process.getPcb().getProcessId());
//        memory.getMemoryWords()[start + 1].setData(process.getPcb().getProcessState());
//        memory.getMemoryWords()[start + 2].setData(process.getPcb().getProgramCounter());
//        memory.getMemoryWords()[start + 3].setData(process.getPcb().getStartBoundary());
//        memory.getMemoryWords()[start + 4].setData(process.getPcb().getEndBoundary());

        start = start + 8;
        for (String s : program)
        {
            systemCalls.writeMemory(s, start,memory);
//            memory.getMemoryWords()[start].setData(s);
            start++;
        }

    }

    public boolean isProcessExistInMemory(int processId)
    {
        if (!memory.isFirstPartitionEmpty())
        {
            if ((int) memory.getMemoryWords()[0].getData() == processId)
            {
                return true;
            }
        }
        else if (!memory.isSecondPartitionEmpty())
        {
            if ((int) memory.getMemoryWords()[20].getData() == processId)
            {
                return true;
            }
        }
        return false;
    }

    public void swapToDisk(Process process)
    {

    }
}
