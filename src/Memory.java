public class Memory {
    private Object[] data;
    private final int memorySize = 40;

    public Memory() {
        this.data = new Object[memorySize];
    }

    public Object[] getData() {
        return data;
    }

    public void setData(Object[] data) {
        this.data = data;
    }
    public boolean isEmpty(int startIndex, int endIndex)
    {
        if (endIndex < startIndex)
        {
            return false;
        }

        for (int i = startIndex; i < endIndex; i++)
        {
            if (this.data[i] != null)
            {
                return false;
            }
        }

        return true;
    }

}
