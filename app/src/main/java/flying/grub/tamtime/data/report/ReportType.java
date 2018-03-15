package flying.grub.tamtime.data.report;

public enum ReportType {
    CONTROLE_Q(1), CONTROLE_T(2), INCIDENT(3), AUTRE(4);
    private int value;

    ReportType(int value) {
        this.value = value;
    }

    public int getValue() {
       return this.value;
    }

    public static ReportType reportFromId(int id) {
        switch (id) {
            case 1:
                return ReportType.CONTROLE_Q;
            case 2:
                return ReportType.CONTROLE_T;
            case 3:
                return ReportType.INCIDENT;
            case 4:
                return ReportType.AUTRE;
            default:
                return null;
        }
    }

    public static ReportType reportFromPosition(int pos) {
        return reportFromId(pos +1);
    }

    public int getValueForString() {
        return this.value -1;
    }
}
