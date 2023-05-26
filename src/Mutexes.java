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


    public Mutexes() {
        this.userInputKey = true;
        this.userOutputKey = true;
        this.fileKey = true;
        this.blockedUserInput = new LinkedList<>();
        this.blockedUserOutput = new LinkedList<>();
        this.blockedFile = new LinkedList<>();
    }

    public boolean semWait (String resource, Integer processId)
    {
        resource = resource.toLowerCase();

        switch (resource)
        {
            case "userinput":
                System.out.println("---------------------------------------- User Input Resource----------------------------------------");
                if (userInputKey)
                {
                    this.userInputOwner = processId;
                    this.userInputKey = false;
                    return true;
                }
                else
                {
                    System.out.println("---------------------------------------- Process " + processId + " blocked on input resource ----------------------------------------");
                    this.blockedUserInput.add(processId);
                    return false;
                }

            case "useroutput":
                System.out.println("---------------------------------------- User Output Resource ----------------------------------------");
                if (userOutputKey)
                {
                    this.userOutputOwner = processId;
                    this.userOutputKey = false;
                    return true;
                }
                else
                {
                    System.out.println("---------------------------------------- Process " + processId + " blocked on output resource ----------------------------------------");
                    this.blockedUserOutput.add(processId);
                    return false;
                }

            case "file":
                System.out.println("---------------------------------------- File Resource ----------------------------------------");
                if (fileKey)
                {
                    this.fileOwner = processId;
                    this.fileKey = false;
                    return true;
                }
                else
                {
                    System.out.println("---------------------------------------- Process " + processId + " blocked on file resource ----------------------------------------");
                    this.blockedFile.add(processId);
                    return false;
                }
        }
        return false;
    }

    public int semSignal (String resource, Integer processId)
    {
        resource = resource.toLowerCase();

        switch (resource)
        {
            case "userinput":
                System.out.println("---------------------------------------- User Input Resource ----------------------------------------");
                if (this.userInputOwner == processId)
                {
                    userInputKey = true;
                    if (!this.blockedUserInput.isEmpty())
                    {
                        int targetId = this.blockedUserInput.poll();
                        deleteFromQueue(this.blockedUserInput, targetId);
                        return targetId;
                    }
                    else
                    {
                        return -1;
                    }
                }

            case "useroutput":
                System.out.println("---------------------------------------- User Output ----------------------------------------");
                if (this.userOutputOwner == processId)
                {
                    userOutputKey = true;
                    if (!this.blockedUserOutput.isEmpty())
                    {
                        int targetId = this.blockedUserOutput.poll();
                        deleteFromQueue(this.blockedUserOutput, targetId);
                        return targetId;
                    }
                    else
                    {
                        return -1;
                    }
                }

            case "file":
                System.out.println("---------------------------------------- File ----------------------------------------");
                if (this.fileOwner == processId)
                {
                    fileKey = true;
                    if (!this.blockedFile.isEmpty())
                    {
                        int targetId = this.blockedFile.poll();
                        deleteFromQueue(this.blockedFile, targetId);
                        return targetId;
                    }
                    else
                    {
                        return -1;
                    }
                }
        }
        return -1;
    }

    public void deleteFromQueue (Queue<Integer> queue, Integer processId)
    {
        Queue<Integer> result = new LinkedList<>();
        while (!queue.isEmpty())
        {
            Integer targetId = queue.poll();

            if (! (processId == targetId))
            {
                result.add(targetId);
            }
        }

        while (!result.isEmpty())
        {
            Integer target = result.poll();
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

    public Queue<Integer> getBlockedUserInput() {
        return blockedUserInput;
    }

    public void setBlockedUserInput(Queue<Integer> blockedUserInput) {
        this.blockedUserInput = blockedUserInput;
    }

    public Queue<Integer> getBlockedUserOutput() {
        return blockedUserOutput;
    }

    public void setBlockedUserOutput(Queue<Integer> blockedUserOutput) {
        this.blockedUserOutput = blockedUserOutput;
    }

    public Queue<Integer> getBlockedFile() {
        return blockedFile;
    }

    public void setBlockedFile(Queue<Integer> blockedFile) {
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



}
