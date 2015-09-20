package flying.grub.tamtime.data;

import java.util.ArrayList;

public class Itinerary {
    private Stop start;
    private Stop stop;
    private Route route;
    private Itinerary nextItinerary;
    private int nbrStop;

    private Itinerary(Stop start, Stop stop, Route route, int nbrStop) {
        this(start, stop, route, nbrStop, null);
    }

    private Itinerary(Stop start, Stop stop, Route route, int nbrStop, Itinerary nextItinerary) {
        this.start = start;
        this.stop = stop;
        this.route = route;
        this.nbrStop = nbrStop;
        this.nextItinerary = nextItinerary;
    }

    /* Ici, on fait de l'exploration de toile, on part d'un point et on crée des bout d'itinéraire pour chaque changement de ligne.
    Affin d'obteir un trajet d'un point a à b avec la suite dans l'ordre, on part de l'arret final jusqu'a retrouver l'arret de départ.
    (Puisque dans le parcour on ne peut construire un Itinerary qu'avec sont précédent et pas le suivant)
    Etape 1 :
    On prend un premier point (l'arrivée), et on regarde toute les lignes qui y passent.
    Pour chacune de ces lignes, on examine tout les point et on test : Si c'est l'arret qu'on recherche alors on return l'itinéraire
                                                                    Si c'est un noeud (croisement de lignes) alors on stocke l'itinéraire
    Etape 2 :
    Tant que l'infini : (On admet que les deux arret sont forcement liable, On peut brider aud nombre maximum de corespondance dans un itinéraire)
    Pour tous les itinéraire stocké (trié par longueur (nbrStop)), on regarde toutes les lignes qui passent par l'arret start (chemin a l'envers).
    Idem que pour la partie 1, on examine tout les point de toute les lignes et on return/stock l'itinéraire en précisant l'itinéraire précedent (donc suivant). */
    public static Itinerary build(Stop start, Stop stop) {
        ArrayList<Itinerary> curntItList = new ArrayList<Itinerary>();
        ArrayList<Itinerary> newtItList = new ArrayList<Itinerary>();
        ArrayList<Route> curntRoutesList = new ArrayList<Route>();
        int stopNum;
        Stop curntStop;

        // Part 1
        for (Line l : stop.getLines()) { // Pour toute ligne du terminus
            curntRoutesList.addAll(l.getRoutes());
        }

        for (Route curntRoute : curntRoutesList) { // Pour toutes routes de cette ligne
            stopNum = curntRoute.getStopNum(stop); // on note la position de l'arret sur la route

            for (int i=stopNum-1; i>=0; i--) { // On parcour la route depuis l'arret jusqu'au départ (puisqu'on remonte a l'envers)
                curntStop = curntRoute.getStopByNum(i);
                if (start == curntStop) return new Itinerary(start, stop, curntRoute, stopNum-i); // Si les 2 arret sont sur la même route on return

                if (curntStop.getLines().size() > 1) { // Si l'arret est un noeud
                    for (Line curntLine : curntStop.getLines()) {
                        if (curntLine != curntRoute.getLine()) {
                            curntItList.add(new Itinerary(curntStop, stop, curntRoute, stopNum-i)); // On ajoute l'itinéraire jusqu'au noeud dans la liste
                        }
                    }
                }
            }
        }
        curntItList = orderItineraryList(curntItList); // On met la liste des noeud dans l'ordre (du plus court au plus long)
        curntRoutesList.clear();
        // Part 2
        while (true) { // Oui c'est dégueulasse est risqué
            for (Itinerary curntItinerary : curntItList) { // Pour tout les itinéraire préalabement créer
                for (Line l : curntItinerary.start.getLines()) {
                    curntRoutesList.addAll(l.getRoutes());
                }
                                                                        // Idem Part 1
                for (Route curntRoute : curntRoutesList) {
                    stopNum = curntRoute.getStopNum(curntItinerary.start);

                    for (int i=stopNum-1; i>=0; i--) {
                        curntStop = curntRoute.getStopByNum(i); // Pour le constructeur on rajoute l'itinerary precedent (donc suivant)
                        if (start == curntStop) return new Itinerary(curntStop, curntItinerary.start, curntRoute, stopNum-i, curntItinerary);

                        if (curntStop.getLines().size() > 1) {
                            for (Line curntLine : curntStop.getLines()) {
                                if (curntLine != curntRoute.getLine()) { // Dans newItList car on parcour curntItList
                                    newtItList.add(new Itinerary(curntStop, curntItinerary.start, curntRoute, stopNum-i, curntItinerary));
                                }
                            }
                        }
                    }
                }
            }
            curntItList = orderItineraryList(newtItList);
            newtItList.clear();
            curntRoutesList.clear();
        }
    }

    private static ArrayList<Itinerary> orderItineraryList(ArrayList<Itinerary> startList) {
        ArrayList<Itinerary> resList = new ArrayList<Itinerary>();
        int i=0;
        for (Itinerary curntIt : startList) {
            while (i<resList.size() && resList.get(i).nbrStop <= curntIt.nbrStop) {
                i++;
            }
            resList.add(i, curntIt);
            i=0;
        }
        return resList;
    }

    // Test & Bullshit
    public String toString() {
        String res = "From " + this.start.getName() + " To " + this.stop.getName() + "\n";
        res += "By " + this.route.getLine() + " Direct. " + this.route.getDirection() + "\n";

        Itinerary curntIt = this;
        while (curntIt.nextItinerary != null) {
            curntIt = curntIt.nextItinerary;
            res += "From " + curntIt.start.getName() + " To " + curntIt.stop.getName() + "\n";
            res += "By " + curntIt.route.getLine() + " Direct. " + curntIt.route.getDirection() + "\n";
        }
        return res;
    }

}
