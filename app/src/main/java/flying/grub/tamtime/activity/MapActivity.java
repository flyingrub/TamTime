package flying.grub.tamtime.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;

import flying.grub.tamtime.R;
import flying.grub.tamtime.data.Data;
import flying.grub.tamtime.data.map.Direction;
import flying.grub.tamtime.data.map.Line;
import flying.grub.tamtime.data.map.PointOfInterest;
import flying.grub.tamtime.data.map.Stop;

public class MapActivity extends FragmentActivity {

    private Data data;
    private IMapController mapController;
    private RadioGroup radioGroup;
    private ArrayList<Polyline> displayed_network = new ArrayList<>();
    private ArrayList<Marker> displayed_marker = new ArrayList<>();
    private MapView mapView;

    @SuppressLint("ClickableViewAccessibility")
    @Override public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        setContentView(R.layout.activity_map);


        //Map Creation
        mapView = (MapView) findViewById(R.id.mapview);

        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);

        //Set the starter point to Montpellier
        mapController = mapView.getController();
        mapController.setZoom(20);
        GeoPoint startPoint = new GeoPoint(43.610769, 3.876716);
        mapController.setCenter(startPoint);

        data = Data.getData();
        data.init(this);

        //Set POI on the map
        setPOIMarker(data, mapView);

        // Allow user to put some marker on the map
        MapEventsReceiver mReceiver = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {

                return false;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                Marker marker = new Marker(mapView);
                marker.setPosition(p);
                marker.setIcon(getResources().getDrawable(R.drawable.ic_action_personal_interest));
                mapView.getOverlays().add(marker);
                return false;
            }
        };
        MapEventsOverlay overlayEvents = new MapEventsOverlay(mReceiver);
        mapView.getOverlays().add(overlayEvents);

        // permit to find what the user want to display.
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup_map);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId){
                switch (checkedId){
                    case R.id.radioButton_tram:
                        removeLinesNetwork(mapView);
                        removeDisplayedMarker(mapView);
                        setLinesNetwork(data, mapView, "Tram");
                        setTramStopMarker(data, mapView);
                        break;
                    case R.id.radioButton_bus:
                        removeLinesNetwork(mapView);
                        removeDisplayedMarker(mapView);
                        setLinesNetwork(data, mapView, "Bus");
                        setBusStopMarker(data, mapView);
                        break;
                }
            }
        });

    }

    public void onResume(){
        super.onResume();


        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        mapView.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    public void onPause(){
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        mapView.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }

    private void setTramStopMarker(Data data, MapView map){
        ArrayList<Line> lines = data.getMap().getLines();
        ArrayList<Stop> stops = new ArrayList<>();

        for (Line line : lines){
            if(line.getTam_id() <= 4) {

                for(Direction direction : line.getDirections()){

                    stops.addAll(direction.getStops());

                    for(Stop stop : stops){
                        Marker stopMarker = new Marker(map);
                        GeoPoint stopPoint = stop.getLocalisation();
                        stopMarker.setPosition(stopPoint);
                        stopMarker.setIcon(getResources().getDrawable(R.drawable.ic_action_tram));
                        stopMarker.setTitle(stop.getStopZone().getName());
                        stopMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                        map.getOverlays().add(stopMarker);

                        displayed_marker.add(stopMarker);
                    }
                }
            }
        }
    }

    private void setBusStopMarker(Data data, MapView map){
        ArrayList<Line> lines = data.getMap().getLines();
        ArrayList<Stop> stops = new ArrayList<>();

        for (Line line : lines){
            if(line.getTam_id() > 4) {

                for(Direction direction : line.getDirections()){

                    stops.addAll(direction.getStops());

                    for(Stop stop : stops){
                        Marker stopMarker = new Marker(map);
                        GeoPoint stopPoint = stop.getLocalisation();
                        stopMarker.setPosition(stopPoint);
                        stopMarker.setIcon(getResources().getDrawable(R.drawable.ic_action_bus));
                        stopMarker.setTitle(stop.getStopZone().getName());
                        stopMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                        map.getOverlays().add(stopMarker);
                        displayed_marker.add(stopMarker);
                    }
                }
            }
        }
    }

    private void setPOIMarker(Data data, MapView map){
        ArrayList<PointOfInterest> pointOfInterests = data.getMap().getPointOfInterests();
        for(PointOfInterest poi : pointOfInterests){
            Marker poiMarker = new Marker(map);
            GeoPoint poiPoint = poi.getLocation();
            poiMarker.setPosition(poiPoint);
            setIconForPOI(poiMarker, poi.getType());
            poiMarker.setTitle(poi.getName());
            poiMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            map.getOverlays().add(poiMarker);
        }
    }

    private void removeDisplayedMarker(MapView mapView){
        mapView.getOverlays().removeAll(displayed_marker);
        if(!displayed_marker.isEmpty()){
            displayed_marker.clear();
        }
    }

    private void setLinesNetwork(Data data, MapView mapView, String choice){
        ArrayList<Line> lines = data.getMap().getLines();

        for (Line line : lines) {

            if(choice.equals("Tram")){
                if(line.getTam_id() <= 4){
                    Polyline polyline = new Polyline();
                    polyline.setColor(line.getColor());
                    polyline.setPoints(line.getPolyline_A());
                    polyline.setWidth(10);
                    polyline.setOnClickListener(new Polyline.OnClickListener() {
                        @Override
                        public boolean onClick(Polyline polyline, MapView mapView, GeoPoint eventPos) {
                            Toast.makeText(mapView.getContext(), "ligne " + line.getTam_id(), Toast.LENGTH_LONG).show();
                            return false;
                        }
                    });
                    displayed_network.add(polyline);
                    mapView.getOverlayManager().add(polyline);
                }
            }
            else if(choice.equals("Bus")){
                if(line.getTam_id() > 4){
                    Polyline polyline = new Polyline();
                    polyline.setColor(line.getColor());
                    polyline.setPoints(line.getPolyline_A());
                    polyline.setWidth(10);
                    polyline.setOnClickListener(new Polyline.OnClickListener() {
                        @Override
                        public boolean onClick(Polyline polyline, MapView mapView, GeoPoint eventPos) {
                            Toast.makeText(mapView.getContext(), "polyline with " + polyline.getPoints().size() + "pts was tapped", Toast.LENGTH_LONG).show();
                            return false;
                        }
                    });
                    displayed_network.add(polyline);
                    mapView.getOverlayManager().add(polyline);
                }
            }
        }
    }

    private void removeLinesNetwork(MapView mapView){
        mapView.getOverlayManager().removeAll(displayed_network);
        if(!displayed_network.isEmpty()){
            displayed_network.clear();
        }
    }

    private void setIconForPOI(Marker poiMarker, String type){
        switch (type){
            case "Parking P+tram abonnés" :
                poiMarker.setIcon(getResources().getDrawable(R.drawable.ic_action_parking));

                break;
            case "Espaces mobilité" :
                poiMarker.setIcon(getResources().getDrawable(R.drawable.ic_action_covoiturage));
                break;
            case "Stations d'autopartage" :
                poiMarker.setIcon(getResources().getDrawable(R.drawable.ic_action_covoiturage));
                break;
            case "Aires de covoiturage" :
                poiMarker.setIcon(getResources().getDrawable(R.drawable.ic_action_covoiturage));
                break;
            case "Sites TaM" :
                poiMarker.setIcon(getResources().getDrawable(R.drawable.ic_action_tam));
                break;
            case "Points de vente de proximité" :
                poiMarker.setIcon(getResources().getDrawable(R.drawable.ic_action_localmarket));
                break;
            case "Écoles" :
                poiMarker.setIcon(getResources().getDrawable(R.drawable.ic_action_school));
                break;
            case "Collèges - Lycées" :
                poiMarker.setIcon(getResources().getDrawable(R.drawable.ic_action_school));
                break;
            case "Enseignement Supérieur" :
                poiMarker.setIcon(getResources().getDrawable(R.drawable.ic_action_school));
                break;
            case "Sport" :
                poiMarker.setIcon(getResources().getDrawable(R.drawable.ic_action_sport));
                break;
            case "Stades" :
                poiMarker.setIcon(getResources().getDrawable(R.drawable.ic_action_stadium));
                break;
            case "Administrations" :
                poiMarker.setIcon(getResources().getDrawable(R.drawable.ic_action_administration));
                break;
            case "Arènes" :
                poiMarker.setIcon(getResources().getDrawable(R.drawable.ic_action_arena));
                break;
            case "Autres équipements" :
                poiMarker.setIcon(getResources().getDrawable(R.drawable.ic_action_location));
                break;
            case "Bibliothèques - Médiathèques" :
                poiMarker.setIcon(getResources().getDrawable(R.drawable.ic_action_library));
                break;
            case "Centres commerciaux" :
                poiMarker.setIcon(getResources().getDrawable(R.drawable.ic_action_commercial));
                break;
            case "Châteaux - Domaines" :
                poiMarker.setIcon(getResources().getDrawable(R.drawable.ic_action_castle));
                break;
            case "Cimetières" :
                poiMarker.setIcon(getResources().getDrawable(R.drawable.ic_action_cimetary));
                break;
            case "Lieux de culte" :
                poiMarker.setIcon(getResources().getDrawable(R.drawable.ic_action_culteplace));
                break;
            case "Hôpitaux - Cliniques" :
                poiMarker.setIcon(getResources().getDrawable(R.drawable.ic_action_hospital));
                break;
            case "Loisirs - Culture" :
                poiMarker.setIcon(getResources().getDrawable(R.drawable.ic_action_loisir));
                break;
            case "Mairies" :
                poiMarker.setIcon(getResources().getDrawable(R.drawable.ic_action_townhall));
                break;
            case "Maisons de quartier" :
                poiMarker.setIcon(getResources().getDrawable(R.drawable.ic_action_home));
                break;
            case "Musées" :
                poiMarker.setIcon(getResources().getDrawable(R.drawable.ic_action_museum));
                break;
            case "La Poste" :
                poiMarker.setIcon(getResources().getDrawable(R.drawable.ic_action_poste));
                break;
            case "Campings" :
                poiMarker.setIcon(getResources().getDrawable(R.drawable.ic_action_campings));
                break;
            case "Vélomagg' plage" :
                poiMarker.setIcon(getResources().getDrawable(R.drawable.ic_action_velomag));
                break;
            case "Commune" :
                Marker.ENABLE_TEXT_LABELS_WHEN_NO_IMAGE = true;
                poiMarker.setTextLabelFontSize(12);
                poiMarker.setTextLabelForegroundColor(Color.BLACK);
                poiMarker.setIcon(null);
                break;
            case "Vélomagg' libre-service" :
                poiMarker.setIcon(getResources().getDrawable(R.drawable.ic_action_velomag));
                break;
            case "Vélomagg' véloparcs" :
                poiMarker.setIcon(getResources().getDrawable(R.drawable.ic_action_velomag));
                break;
            case "Parkings P+tram" :
                poiMarker.setIcon(getResources().getDrawable(R.drawable.ic_action_parking));
                break;
            case "Parkings de proximité" :
                poiMarker.setIcon(getResources().getDrawable(R.drawable.ic_action_parking));
                break;
            case "Parkings centre-ville" :
                poiMarker.setIcon(getResources().getDrawable(R.drawable.ic_action_parking));
                break;
        }
    }


}