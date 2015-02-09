package flying.grub.tamtime;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by fly on 09/02/15.
 */
public class DataParser {
    JSONObject data;

    public DataParser(String sdata) throws JSONException {
        this.data = new JSONObject(sdata);
    }

}
