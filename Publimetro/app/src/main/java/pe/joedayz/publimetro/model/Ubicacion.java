package pe.joedayz.publimetro.model;

import java.io.Serializable;

/**
 * Created by josediaz on 2/20/16.
 */
public class Ubicacion implements Serializable {

    private String latitude;
    private String longitude;
    private String direccion;


    public Ubicacion(String latitude, String longitude, String direccion) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.direccion = direccion;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
}