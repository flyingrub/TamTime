package flying.grub.tamtime.navigation;

import android.graphics.drawable.Drawable;

public class ItemWithDrawable {
    private String text;
    private Drawable drawable;
    private boolean isHeader;

    public ItemWithDrawable(String text, Drawable drawable, boolean header) {
        this.text = text;
        this.drawable = drawable;
        this.isHeader = header;
    }

    public String getText() {
        return text;
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
