package flying.grub.tamtime.data;

public enum ReportType {
    CONTROLE_Q(0), CONTROLE_T(1), INCIDENT(2), AUTRE(3);
    private int value;

    ReportType(int value) {
        this.value = value;
    }

    public int getValue() {
       return this.value;
    }

    public static ReportType reportFromNum(int num) {
        ReportType res;
        switch (num) {
            case 0:
                res = ReportType.CONTROLE_Q;
                break;
            case 1:
                res = ReportType.CONTROLE_T;
                break;
            case 2:
                res = ReportType.INCIDENT;
                break;
            case 3:
                res = ReportType.AUTRE;
                break;
            default:
                res = null;
                break;
        }
        return res;
    }
}
