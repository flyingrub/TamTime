package flying.grub.tamtime.data.map;

import org.osmdroid.util.GeoPoint;

public class PointOfInterest {

    private int _id;
    private int tam_id;
    private String name;
    private GeoPoint location;
    private String type;

    public PointOfInterest(int _id, int tam_id, String name, GeoPoint location, String type){
        this._id =_id;
        this.tam_id = tam_id;
        this.name = name;
        this.location = location;
        this.type = type;
    }

    public String getType(){
        return type;
    }

    public String getName(){
        return name;
    }

    public GeoPoint getLocation(){
        return location;
    }

}
