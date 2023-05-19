import java.util.LinkedList;
import java.util.Queue;

public class Mutexes {
    private boolean userInputKey;
    private boolean userOutputKey;
    private boolean fileKey;
    private Queue<Integer> blockedUserInput;
    private Queue<Integer> blockedUserOutput;
    private Queue<Integer> blockedFile;
    private int userInputOwner;
    private int userOutputOwner;
    private int fileOwner;
    private Queue<Integer> generalBlocked;
    private Queue<Integer> readyQueue;
    private Memory memory;

    public Mutexes(Queue<Integer> generalBlocked, Queue<Integer> readyQueue, Memory memory) {
        this.userInputKey = true;
        this.userOutputKey = true;
        this.fileKey = true;
        this.blockedUserInput = new LinkedList<>();
        this.blockedUserOutput = new LinkedList<>();
        this.blockedFile = new LinkedList<>();
        this.generalBlocked = generalBlocked;
        this.readyQueue = readyQueue;
        this.memory = memory;
    }

    //we have to manipulate the memory but not sure about its format
    public void semWait (String resource, ProcessControlBlock pcb)
    {
        resource = resource.toLowerCase();

        switch (resource)
        {
            case "userinput":
                if (userInputKey)
                {
                    this.userInputOwner = pcb.getProcessId();
                    this.userInputKey = false;
                }
                else
                {
                    this.blockedUserInput.add(pcb.getProcessId());
                    this.generalBlocked.add(pcb.getProcessId());
                    pcb.setProcessState(ProcessState.BLOCKED);
                    deleteFromQueue(readyQueue, pcb.getProcessId());
                }
                break;

            case "useroutput":
                if (userOutputKey)
                {
                    this.userOutputOwner = pcb.getProcessId();
                    this.userOutputKey = false;
                }
                else
                {
                    this.blockedUserOutput.add(pcb.getProcessId());
                    this.generalBlocked.add(pcb.getProcessId());
                    pcb.setProcessState(ProcessState.BLOCKED);
                    deleteFromQueue(readyQueue, pcb.getProcessId());
                }
                break;

            case "file":
                if (fileKey)
                {
                    this.fileOwner = pcb.getProcessId();
                    this.userOutputKey = false;
                }
                else
                {
                    this.blockedFile.add(pcb.getProcessId());
                    this.generalBlocked.add(pcb.getProcessId());
                    deleteFromQueue(readyQueue, pcb.getProcessId());
                }
                break;
        }
    }

    public void semSignal (String resource, ProcessControlBlock pcb)
    {
        resource = resource.toLowerCase();

        switch (resource)
        {
            case "userinput":
                if (this.userInputOwner == pcb.getProcessId())
                {
                    if (!this.blockedUserInput.isEmpty())
                    {
                        int processId = this.blockedUserInput.poll();
                        this.readyQueue.add(processId);
                        pcb.setProcessState(ProcessState.READY);
                        deleteFromQueue(this.blockedUserInput, pcb.getProcessId());
                    }
                    else
                    {
                        userInputKey = true;
                    }
                }
                break;

            case "useroutput":
                if (this.userOutputOwner == pcb.getProcessId())
                {
                    if (!this.blockedUserOutput.isEmpty())
                    {
                        int processId = this.blockedUserOutput.poll();
                        this.readyQueue.add(processId);
                        pcb.setProcessState(ProcessState.READY);
                        deleteFromQueue(this.blockedUserOutput, pcb.getProcessId());
                    }
                    else
                    {
                        userOutputKey = true;
                    }
                }
                break;

            case "file":
                if (this.fileOwner == pcb.getProcessId())
                {
                    if (!this.blockedFile.isEmpty())
                    {
                        int processId = this.blockedFile.poll();
                        this.readyQueue.add(processId);
                        pcb.setProcessState(ProcessState.READY);
                        deleteFromQueue(this.blockedFile, pcb.getProcessId());
                    }
                    else
                    {
                        fileKey = true;
                    }
                }
                break;
        }
    }

    public void deleteFromQueue (Queue<Integer> queue, Integer processId)
    {
        Queue<Integer> result = new LinkedList<>();
        while (!queue.isEmpty())
        {
            int target = queue.poll();
            if (! (processId == target))
            {
                result.add(target);
            }
        }

        while (!result.isEmpty())
        {
            int target = result.poll();
            queue.add(target);
        }
    }

    public static void main(String[] args) {
    }


}
