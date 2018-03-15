package flying.grub.tamtime.navigation;

import android.graphics.drawable.Drawable;

public class ItemWithDrawable {
    private int id;
    private String text;
    private Drawable drawable;
    private boolean isHeader;

    public ItemWithDrawable(int id, String text, Drawable drawable, boolean header) {
        this.id = id;
        this.text = text;
        this.drawable = drawable;
        this.isHeader = header;
    }

    public String getText() {
        return text;
    }

    public int getId() {
        return id;
    }
    public Drawable getDrawable() {
        return drawable;
    }

    public boolean isHeader() {
        return isHeader;
    }

    @Override
    public String toString() {
        return "ItemWithDrawable{" +
                "text='" + text + '\'' +
                ", drawable=" + drawable +
                ", isHeader=" + isHeader +
                '}';
    }
}
