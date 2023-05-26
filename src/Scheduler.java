import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

public class Scheduler {
    private Memory memory;
    private SystemCalls systemCalls;
    private Mutexes mutexes;
    private Interpreter interpreter;
    private Queue<Integer> readyQueue;
    private Queue<Integer> blockedQueue;
    private int processes;
    private String fileName1;
    private String fileName2;
    private String fileName3;
    private int arrivalTime1;
    private int arrivalTime2;
    private int arrivalTime3;
    private int quantum;
    private String temp1;
    private String temp2;
    private String temp3;

    public Scheduler(String fileName1, String fileName2, String fileName3, int arrivalTime1, int arrivalTime2, int arrivalTime3, int quantum) {
        this.fileName1 = fileName1;
        this.fileName2 = fileName2;
        this.fileName3 = fileName3;
        this.arrivalTime1 = arrivalTime1;
        this.arrivalTime2 = arrivalTime2;
        this.arrivalTime3 = arrivalTime3;
        this.quantum = quantum;
        this.memory = new Memory();
        this.systemCalls = new SystemCalls();
        this.mutexes = new Mutexes();
        this.interpreter = new Interpreter();
        this.readyQueue = new LinkedList<>();
        this.blockedQueue = new LinkedList<>();
        this.processes = 0;
    }

    public void run ()
    {
        int clkCycles = 0;
        boolean isProcess1Finished = false;
        boolean isProcess2Finished = false;
        boolean isProcess3Finished = false;
        int timeSlice = 0;
        int currProcessId = -1;
        boolean isBlocked = false;

        System.out.println("---------------------------------------- Scheduler Starts ----------------------------------------");

        while (true)
        {
            if (quantum <= 0)
            {
                System.out.println("Quantum can't be zero");
                break;
            }

            if (isProcess1Finished && isProcess2Finished && isProcess3Finished)
            {
                System.out.println("---------------------------------------- Processes are done ----------------------------------------");
                System.out.println("Ready Queue: " + this.readyQueue);
                System.out.println("Blocked Queue: " + this.blockedQueue);
                memory.printMemory();
                break;
            }

            System.out.println("---------------------------------------- Clk Cycle: " + clkCycles + " ----------------------------------------");
            if (clkCycles == this.arrivalTime1)
            {
                System.out.println("---------------------------------------- " + fileName1 + " arrived ----------------------------------------");
                this.readyQueue.add(createProcessAndAllocate(fileName1));
                memory.printMemory();
            }

            if (clkCycles == this.arrivalTime2)
            {
                System.out.println("---------------------------------------- " + fileName2 + " arrived ----------------------------------------");
                this.readyQueue.add(createProcessAndAllocate(fileName2));
                memory.printMemory();
            }

            if (clkCycles == this.arrivalTime3)
            {
                System.out.println("---------------------------------------- " + fileName3 + " arrived ----------------------------------------");
                this.readyQueue.add(createProcessAndAllocate(fileName3));
                memory.printMemory();
            }


            if (!this.readyQueue.isEmpty() && timeSlice == 0)
            {
                currProcessId = this.readyQueue.poll();
                timeSlice = quantum;
                System.out.println("---------------------------------------- Process " + currProcessId + " is chosen to run ----------------------------------------");
                System.out.println("Ready Queue: " + this.readyQueue);
                System.out.println("Blocked Queue: " + this.blockedQueue);

                if (!isProcessExistInMemory(currProcessId))
                {
                    ArrayList<String> info = readFromDisk();
                    loadToDisk(0, 19);
                    memory.initializeFirstPartition();
                    System.out.println("---------------------------------------- Process " + currProcessId + " is swapped from disk ----------------------------------------");
                    loadToMemory(info);
                }
                else
                {
                    setProcessStatusInMemory(ProcessState.RUNNING, currProcessId);
                }

                memory.printMemory();
            }

            clkCycles++;

            if (currProcessId == -1)
            {
                continue;
            }

            int programCounter = getPCFromMemory(currProcessId);
            String[] args = getInstructionFromMemory(programCounter, currProcessId).split(" ");

            switch (args[0])
            {
                case "input":
                    executeInput(currProcessId);
                    break;
                case "print":
                    executePrint(args, currProcessId);
                    break;
                case "assign":
                    executeAssign(args, currProcessId);
                    break;
                case "readFile":
                    executeReadFile(args, currProcessId);
                    break;
                case "writeFile":
                    executeWriteFile(args, currProcessId);
                    break;
                case "printFromTo":
                    executePrintFromTo(args, currProcessId);
                    break;
                case "semWait":
                    isBlocked = !executeSemWait(args, currProcessId);
                    break;
                case "semSignal":
                    executeSemSignal(args, currProcessId);
                    break;
                default :
                    System.out.println("Instruction is not supported"); break;
            }

            if (isBlocked)
            {
                this.blockedQueue.add(currProcessId);
                setProcessStatusInMemory(ProcessState.BLOCKED, currProcessId);
                timeSlice = 0;
                System.out.println("Ready Queue: " + this.readyQueue);
                System.out.println("Blocked Queue: " + this.blockedQueue);
                isBlocked = false;
                continue;
            }

            programCounter++;
            setProgramCounterInMemory(programCounter, currProcessId);
            memory.printMemory();

            if (checkFinishedInstructions(programCounter, currProcessId))
            {
                setProcessStatusInMemory(ProcessState.FINISHED, currProcessId);
                timeSlice = 0;

                if (currProcessId == 0)
                {
                    isProcess1Finished = true;
                }
                else if (currProcessId == 1)
                {
                    isProcess2Finished = true;
                }
                else if (currProcessId == 2)
                {
                    isProcess3Finished = true;
                }

                System.out.println("Ready Queue: " + this.readyQueue);
                System.out.println("Blocked Queue: " + this.blockedQueue);
                continue;
            }

            timeSlice--;

            if (timeSlice == 0)
            {
                System.out.println("---------------------------------------- Processes " + currProcessId +" finished its time slice ----------------------------------------");
                this.readyQueue.add(currProcessId);
                setProcessStatusInMemory(ProcessState.READY, currProcessId);
            }

            System.out.println("---------------------------------------- Remaining Time Slice: " + timeSlice + " ----------------------------------------");

        }
    }

    public boolean checkFinishedInstructions(int programCounter, int processId)
    {
        if (existsInFirstPartition(processId))
        {
            Object instruction =  memory.getMemoryWords()[8 + programCounter].getData();

            if (programCounter  > 11 || instruction == null)
            {
                return true;
            }
        }

        if (existsInSecondPartition(processId))
        {
            Object instruction =  memory.getMemoryWords()[28 + programCounter].getData();

            if (programCounter > 11 || instruction == null)
            {
                return true;
            }
        }
        return false;
    }

    public void setProcessStatusInMemory(ProcessState state, int processId)
    {
        if (existsInFirstPartition(processId))
        {
            memory.getMemoryWords()[1].setData(state);
            return;
        }

        if (existsInSecondPartition(processId))
        {
            memory.getMemoryWords()[21].setData(state);
        }
    }

    public void setProgramCounterInMemory(int programCounter, int processId)
    {
        if (existsInFirstPartition(processId))
        {
            memory.getMemoryWords()[2].setData(programCounter);
            return;
        }

        if (existsInSecondPartition(processId))
        {
            memory.getMemoryWords()[22].setData(programCounter);
        }
    }

    public int createProcessAndAllocate(String fileName)
    {
        ArrayList<String> program = interpreter.readProgram(fileName);
        ProcessControlBlock pcb = new ProcessControlBlock(processes++, ProcessState.READY, 0,0,0);

        if (memory.isFirstPartitionEmpty())
        {
            pcb.setStartBoundary(0);
            pcb.setEndBoundary(19);
            populateMemory(pcb, program);
        }
        else if (memory.isSecondPartitionEmpty())
        {
            pcb.setStartBoundary(20);
            pcb.setEndBoundary(memory.getMemorySize() - 1);
            populateMemory(pcb, program);
        }
        else
        {
            ProcessState firstPartitionState = (ProcessState) memory.getMemoryWords()[1].getData();
            ProcessState secondPartitionState = (ProcessState) memory.getMemoryWords()[21].getData();

            if (firstPartitionState != ProcessState.RUNNING)
            {
                pcb.setStartBoundary(0);
                pcb.setEndBoundary(19);
                loadToDisk(pcb.getStartBoundary(), pcb.getEndBoundary());
                memory.initializeFirstPartition();
            }
            else if (secondPartitionState != ProcessState.RUNNING)
            {
                pcb.setStartBoundary(20);
                pcb.setEndBoundary(memory.getMemorySize() - 1);
                loadToDisk(pcb.getStartBoundary(), pcb.getEndBoundary());
                memory.initializeSecondPartition();

            }
            populateMemory(pcb, program);
        }

        return pcb.getProcessId();
    }

    public void loadToDisk(int start, int end)
    {

        try {
            String diskPath = "src/Disk.txt";
            FileWriter fw = new FileWriter(diskPath);

            System.out.println("---------------------------------------- Process " + memory.getMemoryWords()[start].getData() +" swapped to disk ----------------------------------------");
            System.out.println("**************************************** Disk Contents ****************************************");
            for (int i = start; i <= end; i++)
            {
                String o = "";
                if ( i == 5 || i == 6 || i == 7 || i == 25 || i == 26 || i == 27)
                {
                    o = "" + memory.getMemoryWords()[i].getAddress() + " " + memory.getMemoryWords()[i].getData();
                }
                else
                {
                    o = "" + memory.getMemoryWords()[i].getData();
                }
                System.out.println(o);
                fw.write(o + System.lineSeparator());
            }
            fw.close();
            System.out.println("**************************************** End Disk Contents ****************************************");

        }
        catch (Exception e)
        {
            System.out.println("Exception method in load to disk");
        }
    }

    public void populateMemory(ProcessControlBlock pcb, ArrayList<String> program)
    {
        int start = pcb.getStartBoundary();

        systemCalls.writeMemory(pcb.getProcessId(), start,memory);
        systemCalls.writeMemory(pcb.getProcessState(), start+1,memory);
        systemCalls.writeMemory(pcb.getProgramCounter(), start+2,memory);
        systemCalls.writeMemory(pcb.getStartBoundary(), start+3,memory);
        systemCalls.writeMemory(pcb.getEndBoundary(), start+4,memory);

        start = start + 8;
        for (String s : program)
        {
            systemCalls.writeMemory(s, start,memory);
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

        if (!memory.isSecondPartitionEmpty())
        {
            if ((int) memory.getMemoryWords()[20].getData() == processId)
            {
                return true;
            }
        }
        return false;
    }

    public ArrayList<String> readFromDisk()
    {
        ArrayList<String> program = new ArrayList<>();
        String path = "src/Disk.txt";

        try
        {
            FileReader fr = new FileReader(path);
            BufferedReader br = new BufferedReader(fr);
            String line;

            while ((line = br.readLine()) != null)
            {
                program.add(line);
            }
            br.close();
            fr.close();
        }
        catch (Exception e)
        {
            System.out.println("readFromDisk method exception");
        }

        return program;
    }

    public void loadToMemory(ArrayList<String> info)
    {
        this.memory.getMemoryWords()[0].setData(Integer.parseInt(info.get(0)));
        this.memory.getMemoryWords()[1].setData(ProcessState.RUNNING);
        this.memory.getMemoryWords()[2].setData(Integer.parseInt(info.get(2)));
        this.memory.getMemoryWords()[3].setData(0);
        this.memory.getMemoryWords()[4].setData(19);

        for (int i = 5; i < 8; i++)
        {
            String st = info.get(i);
            if (!st.contains("null"))
            {
                String[] args = st.split(" ");
                String res = args[2];

                for (int j = 3; j < args.length; j++)
                {
                    res += " " + args[j];
                }

                this.memory.getMemoryWords()[i].setAddress("Variable " + args[1]);
                this.memory.getMemoryWords()[i].setData(res);
            }
        }

        for (int i = 8; i < 20; i++)
        {
            String st = info.get(i);

            if (st.contains("null"))
            {
                break;
            }

            this.memory.getMemoryWords()[i].setData(st);
        }

    }

    public int getPCFromMemory(int processId)
    {
        if (!memory.isFirstPartitionEmpty())
        {
            int targetId = (int) memory.getMemoryWords()[0].getData();

            if (targetId == processId)
            {
                return (int) this.memory.getMemoryWords()[2].getData();
            }
            
        }

        if (!memory.isSecondPartitionEmpty())
        {
            int targetId = (int) memory.getMemoryWords()[20].getData();

            if (targetId == processId)
            {
                return (int) this.memory.getMemoryWords()[22].getData();
            }
        }
        
        return -1;
    }

    public String getInstructionFromMemory(int programCounter, int processId)
    {
        if (existsInFirstPartition(processId))
        {
            return (String) memory.getMemoryWords()[8 + programCounter].getData();
        }

        if(existsInSecondPartition(processId))
        {
            return (String) memory.getMemoryWords()[28 + programCounter].getData();
        }

        return "";
    }

    public boolean existsInFirstPartition(int processId)
    {
        if (!memory.isFirstPartitionEmpty())
        {
            int targetId = (int) memory.getMemoryWords()[0].getData();
            if (targetId == processId)
            {
                return true;
            }
        }
        return false;
    }

    public boolean existsInSecondPartition(int processId)
    {
        if (!memory.isSecondPartitionEmpty())
        {
            int targetId = (int) memory.getMemoryWords()[20].getData();
            if (targetId == processId)
            {
                return true;
            }
        }
        return false;
    }

    public void executeInput(int processId)
    {
        System.out.println("---------------------------------------- Input Instruction ----------------------------------------");
        String res = this.systemCalls.takeInput();
        if (processId == 0)
        {
            this.temp1 = res;
            return;
        }

        if (processId == 1)
        {
            this.temp2 = res;
            return;
        }

        if (processId == 2)
        {
            this.temp3 = res;
        }
    }

    public void executePrint(String[] args, int processId)
    {
        System.out.println("---------------------------------------- Print Instruction ----------------------------------------");
        Object var = getVarFromMemory("Variable " + args[1], processId);
        systemCalls.printData(var);
    }

    public void executeAssign(String[] args, int processId)
    {
        System.out.println("---------------------------------------- Assign Instruction ----------------------------------------");
        String address = "Variable " + args[1];
        String value = "";

        if (processId == 0)
        {
            value = this.temp1;
        }
        else if (processId == 1)
        {
            value = this.temp2;
        }
        else if (processId == 2)
        {
            value = this.temp3;
        }

        assignVariableInMemory(address, value, processId);

    }

    public void executePrintFromTo(String[] args, int processId)
    {
        System.out.println("---------------------------------------- PrintFromTo Instruction ----------------------------------------");
        int var1 = Integer.parseInt((String) getVarFromMemory("Variable " + args[1], processId));
        int var2 = Integer.parseInt((String) getVarFromMemory("Variable " + args[2], processId));
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

    public void executeReadFile(String[] args, int processId)
    {
        System.out.println("---------------------------------------- Read File Instruction ----------------------------------------");
        String fileName = (String) getVarFromMemory("Variable " + args[1], processId);
        String data = systemCalls.readFile(fileName);

        if (processId == 0)
        {
            this.temp1 = data;
            return;
        }

        if (processId == 1)
        {
            this.temp2 = data;
            return;
        }

        if (processId == 2)
        {
            this.temp3 = data;
        }
    }

    public boolean executeSemWait(String[] args, int processId)
    {
        System.out.println("---------------------------------------- Sem Wait Instruction ----------------------------------------");

        boolean flag = mutexes.semWait(args[1], processId);

        System.out.println("User Input Queue: " + mutexes.getBlockedUserInput().toString());
        System.out.println("User Output Queue: " + mutexes.getBlockedUserOutput().toString());
        System.out.println("File  Queue: " + mutexes.getBlockedFile().toString());

        return flag;
    }

    public void executeSemSignal(String[] args, int processId)
    {
        System.out.println("---------------------------------------- Sem Signal Instruction ----------------------------------------");

        int targetId = mutexes.semSignal(args[1], processId);

        System.out.println("User Input Queue: " + mutexes.getBlockedUserInput().toString());
        System.out.println("User Output Queue: " + mutexes.getBlockedUserOutput().toString());
        System.out.println("File  Queue: " + mutexes.getBlockedFile().toString());

        if (targetId > -1)
        {
            deleteFromBlocked(targetId);
            setProcessStatusInMemory(ProcessState.READY, targetId);
        }

    }

    public void executeWriteFile(String[] args, int processId)
    {
        System.out.println("---------------------------------------- Write File Instruction ----------------------------------------");
        String fileName = (String) getVarFromMemory("Variable " + args[1], processId);
        String value = (String) getVarFromMemory("Variable " + args[2], processId);
        systemCalls.writeFile(fileName, value);
    }

    public void assignVariableInMemory(String address, String value, int processId)
    {
        if (existsInFirstPartition(processId))
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

        if (existsInSecondPartition(processId))
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

    public Object getVarFromMemory(String address, int processId)
    {
        if (existsInFirstPartition(processId))
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
            else if (address.equals(var3))
            {
                return value3;
            }
        }

        if (existsInSecondPartition(processId))
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
            else if (address.equals(var3))
            {
                return value3;
            }
        }
        return null;
    }

    public void deleteFromBlocked(int processId)
    {
        Queue<Integer> result = new LinkedList<>();

        while (!blockedQueue.isEmpty())
        {
            int targetId = blockedQueue.poll();

            if (! (processId == targetId))
            {
                result.add(targetId);
            }
            else
            {
                this.readyQueue.add(targetId);
            }
        }

        while (!result.isEmpty())
        {
            int targetId = result.poll();
            blockedQueue.add(targetId);
        }
    }
}
