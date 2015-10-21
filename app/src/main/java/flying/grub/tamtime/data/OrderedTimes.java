package flying.grub.tamtime.data;

import java.util.Calendar;
import java.text.SimpleDateFormat;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

public class OrderedTimes {
    private int order;
    private Calendar date;
    private OrderedTimes next;

    private static final SimpleDateFormat BEUTIFULSDF = new SimpleDateFormat("dd/MM/yy HH:mm");

    public OrderedTimes(int order, Calendar date) {
        this.order = order;
        this.date = date;
    }

    public static OrderedTimes getOrderedTimesFromJson(String date, int jumpDay, JSONArray jsonDay) throws Exception {
        int cpt = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm");
        Calendar cal = Calendar.getInstance();
        OrderedTimes res, newHead;

        String time = jsonDay.getString(0);
        if (time.contains(" ")) {
            String[] split = time.split(" ");
            cpt += Integer.parseInt(split[0]);
            time = split[1];
        }
        cal.setTime(sdf.parse(date + " " + time));
        cal.add(Calendar.DAY_OF_MONTH, jumpDay);

        res = new OrderedTimes(cpt, cal);

        OrderedTimes curnt = res;
        for (int i=1; i<jsonDay.length(); i++) {
            cal = Calendar.getInstance();
            time = jsonDay.getString(i);
           
            if (time.contains(" ")) {
                String[] split = time.split(" ");
                cpt = Integer.parseInt(split[0]);
                time = split[1];
            } else cpt = 0;

            cal.setTime(sdf.parse(date + " " + time));
            cal.add(Calendar.DAY_OF_MONTH, jumpDay);

            newHead = res.addInOrder(new OrderedTimes(cpt, cal));
            if (newHead != null) res = newHead;
        }
        res.setOrder();
        return res.getFirstValidDate();
    }

    public OrderedTimes getFirstValidDate() { // To test
        Calendar now = Calendar.getInstance();
        OrderedTimes curnt = this;
        while (curnt != null && curnt.date.before(now)) curnt = curnt.next;
        return curnt;
    }

    public OrderedTimes getNext() {
        return this.next;
    }

    public OrderedTimes addInOrder(OrderedTimes newOt) {
        OrderedTimes prevElem = this, curntElem = this;

        if (curntElem.date.compareTo(newOt.date) == 0) return null; // Skip if times are equals

        if (curntElem.date.after(newOt.date)) { // It's before the first so it's the first.
            newOt.next = curntElem;
            return newOt; // Return the new head of the list
        }

        while (curntElem != null && curntElem.date.before(newOt.date)) {
            prevElem = curntElem;
            curntElem = curntElem.next;
        }

        if (curntElem == null) { // All the element.date are < to newOt.date 
            prevElem.next = newOt;
            newOt.next = null;
        } else { // prevElem.date < newOt.date < curntElem.date
            prevElem.next = newOt;
            newOt.next = curntElem;
        }

        return null;
    }

    public void setOrder() {
        int i = 0;
        OrderedTimes curnt = this;
        while (curnt != null) {
            curnt.order = curnt.order + i;
            curnt = curnt.next;
            i++;
        }
    }

    public OrderedTimes setGetNext(OrderedTimes elem) {
        this.next = elem;
        return this.next;
    }

    public Calendar getDate() {
        return this.date;
    }

    public int length() {
        OrderedTimes curnt = this;
        int i=0;
        while (curnt != null) {
            curnt = curnt.getNext();
            i++;
        }
        return i;
    }

    public int getOrder() {
        return this.order;
    }

    public void addToEnd(OrderedTimes nextO) {
        if (this.next == null) this.next = nextO;
        else this.next.addToEnd(nextO);
    }

    // Tests & Bullshit

    public String toString() {
        String res = "";
        OrderedTimes curnt = this;
        while(curnt != null) {
            res += curnt.order + " | " + curnt.date.get(Calendar.DAY_OF_MONTH) + "/" + curnt.date.get(Calendar.MONTH) + "/" + curnt.date.get(Calendar.YEAR) + " " + curnt.date.get(Calendar.HOUR_OF_DAY) + ":" + curnt.date.get(Calendar.MINUTE) + "\n";
            curnt = curnt.next;
        }
        return res;
    }

    public String stringer(OrderedTimes curnt) {
        return curnt.order + " | " + curnt.date.get(Calendar.DAY_OF_MONTH) + "/" + curnt.date.get(Calendar.MONTH) + "/" + curnt.date.get(Calendar.YEAR) + " " + curnt.date.get(Calendar.HOUR_OF_DAY) + ":" + curnt.date.get(Calendar.MINUTE) + "\n";
    }
}
