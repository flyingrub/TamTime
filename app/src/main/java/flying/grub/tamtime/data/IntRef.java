package flying.grub.tamtime.data;

/**
 * Created by fly on 10/9/15.
 */
public class IntRef {
    private int i;

    public IntRef(int i) {
        this.i = i;
    }

    public int getI() {
        return i;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IntRef intRef = (IntRef) o;

        return i == intRef.i;
    }

    public void setI(int i) {
        this.i = i;
    }

    @Override
    public int hashCode() {
        return i;
    }
}
