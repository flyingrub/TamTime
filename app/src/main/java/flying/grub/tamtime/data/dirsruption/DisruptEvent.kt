package flying.grub.tamtime.data.dirsruption

import android.content.Context
import android.os.AsyncTask
import android.text.Html
import android.text.Spanned
import android.util.Log

import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Calendar

import de.greenrobot.event.EventBus
import flying.grub.tamtime.data.Data
import flying.grub.tamtime.data.connection.JsonArrayReq
import flying.grub.tamtime.data.map.Line
import flying.grub.tamtime.data.update.MessageUpdate
import flying.grub.tamtime.data.connection.VolleyApp


class DisruptEvent(private val context: Context) {
    var disruptList: ArrayList<Disrupt>


    init {
        disruptList = ArrayList()
    }

    fun getDisrupts() {
        val req = JsonObjectRequest(Request.Method.GET,
                JSON_ALL_DISRUPT, null,
                Response.Listener(this::setDisrupt), Response.ErrorListener { error -> Log.d(TAG, "Error: $error") })
        VolleyApp.getInstance(context).addToRequestQueue(req)
    }

    fun setDisrupt(disruptJson : JSONObject) {
        try {
            for (lineNumber in disruptJson.keys()) {
                val disrupts = disruptJson.getJSONArray(lineNumber)
                for (i in 0..disrupts.length()-1) {
                    val disrupt = disrupts.getJSONObject(i)
                    val start_date = Calendar.getInstance()
                    val end_date = Calendar.getInstance()
                    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss")
                    start_date.setTime(sdf.parse(disrupt.getString("start_date")))
                    end_date.setTime(sdf.parse(disrupt.getString("end_date")))
                    val title = disrupt.getString("title")
                    val description = stripHtml(disrupt.getString("description"))
                    val line = Data.getData().map.getLineByCitiwayId(lineNumber.toInt())
                    addDisrupt(Disrupt(line, start_date, end_date, description, title))
                }
            }
        } catch (e : JSONException) {
            e.printStackTrace()
        }
    }

    fun addDisrupt(disrupt: Disrupt) {
        disruptList.add(disrupt)
        disrupt.line.addDisruptEvent(disrupt)
    }

    fun stripHtml (html : String) : String {
       return Html.fromHtml(html).toString()
    }

    companion object {

        private val TAG = DisruptEvent::class.java.getSimpleName()

        private val JSON_ALL_DISRUPT = "https://apimobile.tam-voyages.com/api/v1/disruption/lines"
    }
}
