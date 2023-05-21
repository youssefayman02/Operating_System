import java.util.LinkedList;
import java.util.Queue;

public class Mutexes {
    private boolean userInputKey;
    private boolean userOutputKey;
    private boolean fileKey;
    private Queue<Process> blockedUserInput;
    private Queue<Process> blockedUserOutput;
    private Queue<Process> blockedFile;
    private int userInputOwner;
    private int userOutputOwner;
    private int fileOwner;


    public Mutexes() {
        this.userInputKey = true;
        this.userOutputKey = true;
        this.fileKey = true;
        this.blockedUserInput = new LinkedList<>();
        this.blockedUserOutput = new LinkedList<>();
        this.blockedFile = new LinkedList<>();
    }

    //we have to manipulate the memory but not sure about its format
    public boolean semWait (String resource, Process process)
    {
        int processId = process.getPcb().getProcessId();
        resource = resource.toLowerCase();

        switch (resource)
        {
            case "userinput":
                System.out.println("---------------------------------------- User Input ----------------------------------------");
                if (userInputKey)
                {
                    this.userInputOwner = processId;
                    this.userInputKey = false;
                    return true;
                }
                else
                {
                    System.out.println("---------------------------------------- Process " + processId + " blocked on input resource ----------------------------------------");
                    process.getPcb().setProcessState(ProcessState.BLOCKED);
                    this.blockedUserInput.add(process);
                    return false;
                }

            case "useroutput":
                System.out.println("---------------------------------------- User Output ----------------------------------------");
                if (userOutputKey)
                {
                    this.userOutputOwner = processId;
                    this.userOutputKey = false;
                    return true;
                }
                else
                {
                    System.out.println("---------------------------------------- Process " + processId + " blocked on output resource ----------------------------------------");
                    process.getPcb().setProcessState(ProcessState.BLOCKED);
                    this.blockedUserOutput.add(process);
                    return false;
                }

            case "file":
                System.out.println("---------------------------------------- File ----------------------------------------");
                if (fileKey)
                {
                    this.fileOwner = processId;
                    this.userOutputKey = false;
                    return true;
                }
                else
                {
                    System.out.println("---------------------------------------- Process " + processId + " blocked on file resource ----------------------------------------");
                    process.getPcb().setProcessState(ProcessState.BLOCKED);
                    this.blockedFile.add(process);
                    return false;
                }
        }
        return false;
    }

    public int semSignal (String resource, Process process)
    {
        int processId = process.getPcb().getProcessId();
        resource = resource.toLowerCase();

        switch (resource)
        {
            case "userinput":
                System.out.println("---------------------------------------- User Input ----------------------------------------");
                if (this.userInputOwner == processId)
                {
                    if (!this.blockedUserInput.isEmpty())
                    {
                        Process target = this.blockedUserInput.poll();
                        int targetId = target.getPcb().getProcessId();
                        target.getPcb().setProcessState(ProcessState.READY);
                        deleteFromQueue(this.blockedUserInput, targetId);
                        return targetId;
                    }
                    else
                    {
                        userInputKey = true;
                        return -1;
                    }
                }

            case "useroutput":
                System.out.println("---------------------------------------- User Output ----------------------------------------");
                if (this.userOutputOwner == processId)
                {
                    if (!this.blockedUserOutput.isEmpty())
                    {
                        Process target = this.blockedUserOutput.poll();
                        int targetId = target.getPcb().getProcessId();
                        target.getPcb().setProcessState(ProcessState.READY);
                        deleteFromQueue(this.blockedUserOutput, targetId);
                        return targetId;
                    }
                    else
                    {
                        userOutputKey = true;
                        return -1;
                    }
                }

            case "file":
                System.out.println("---------------------------------------- File ----------------------------------------");
                if (this.fileOwner == processId)
                {
                    if (!this.blockedFile.isEmpty())
                    {
                        Process target = this.blockedFile.poll();
                        int targetId = target.getPcb().getProcessId();
                        target.getPcb().setProcessState(ProcessState.READY);
                        deleteFromQueue(this.blockedFile, targetId);
                        return targetId;
                    }
                    else
                    {
                        fileKey = true;
                        return -1;
                    }
                }
        }
        return -1;
    }

    public void deleteFromQueue (Queue<Process> queue, Integer processId)
    {
        Queue<Process> result = new LinkedList<>();
        while (!queue.isEmpty())
        {
            Process target = queue.poll();

            if (! (processId == target.getPcb().getProcessId()))
            {
                result.add(target);
            }
        }

        while (!result.isEmpty())
        {
            Process target = result.poll();
            queue.add(target);
        }
    }

    public boolean isUserInputKey() {
        return userInputKey;
    }

    public void setUserInputKey(boolean userInputKey) {
        this.userInputKey = userInputKey;
    }

    public boolean isUserOutputKey() {
        return userOutputKey;
    }

    public void setUserOutputKey(boolean userOutputKey) {
        this.userOutputKey = userOutputKey;
    }

    public boolean isFileKey() {
        return fileKey;
    }

    public void setFileKey(boolean fileKey) {
        this.fileKey = fileKey;
    }

    public Queue<Process> getBlockedUserInput() {
        return blockedUserInput;
    }

    public void setBlockedUserInput(Queue<Process> blockedUserInput) {
        this.blockedUserInput = blockedUserInput;
    }

    public Queue<Process> getBlockedUserOutput() {
        return blockedUserOutput;
    }

    public void setBlockedUserOutput(Queue<Process> blockedUserOutput) {
        this.blockedUserOutput = blockedUserOutput;
    }

    public Queue<Process> getBlockedFile() {
        return blockedFile;
    }

    public void setBlockedFile(Queue<Process> blockedFile) {
        this.blockedFile = blockedFile;
    }

    public int getUserInputOwner() {
        return userInputOwner;
    }

    public void setUserInputOwner(int userInputOwner) {
        this.userInputOwner = userInputOwner;
    }

    public int getUserOutputOwner() {
        return userOutputOwner;
    }

    public void setUserOutputOwner(int userOutputOwner) {
        this.userOutputOwner = userOutputOwner;
    }

    public int getFileOwner() {
        return fileOwner;
    }

    public void setFileOwner(int fileOwner) {
        this.fileOwner = fileOwner;
    }

    public static void main(String[] args) {
    }


}
