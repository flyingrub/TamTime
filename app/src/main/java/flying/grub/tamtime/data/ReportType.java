package flying.grub.tamtime.data;

public enum ReportType {
    AUTRE(1), CONTROLE_Q(2), CONTROLE_T(3), INCIDENT(4), RETARD(5);
    private int value;

    private ReportType(int value) {
        this.value = value;
    }

    public int getValue() {
       return this.value;
    }
}
