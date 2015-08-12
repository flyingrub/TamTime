package flying.grub.tamtime.data;

import android.os.AsyncTask;

import flying.grub.tamtime.activity.MainActivity;

/**
 * Created by fly on 8/13/15.
 */
public class WaitForData extends AsyncTask<Void, Integer, String> {

    Runnable function;

    public WaitForData(Runnable func){
        function = func;
    }

    @Override
    protected String doInBackground(Void... unused) {
        try {
            MainActivity.getData().getAll();
            while (!MainActivity.getData().asData()) {
                Thread.sleep(500);
            }
        } catch (InterruptedException t) {
            // Gérer l'exception et terminer le traitement
            return ("The sleep operation failed");
        }
        return ("return object when task is finished");
    }

    // Surcharge de la méthode onPostExecute (s'exécute dans la Thread de l'IHM)
    @Override
    protected void onPostExecute(String message) {
        function.run();
    }
}