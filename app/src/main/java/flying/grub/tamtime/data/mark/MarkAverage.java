package flying.grub.tamtime.data.mark;

public class MarkAverage {
    private double value;
    private int countMarks;

    public MarkAverage(double value, int countMarks)
    {
        this.value = value;
        this.countMarks = countMarks;
    }

    public double getValue()
    {
        return value;
    }

    public void Setvalue(double value)
    {
        this.value = value;
    }

    public int getCountMarks()
    {
        return countMarks;
    }

    public void setCountMarks(int countMarks)
    {
        this.countMarks = countMarks;
    }
}
