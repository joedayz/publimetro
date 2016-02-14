package pe.joedayz.publimetro.model
        ;

import java.io.Serializable;

/**
 * Created by josediaz on 2/13/16.
 */
public class Ciudad implements Serializable {

    private String codigo;
    private String descripcion;
    private String latitude;
    private String longitud;
    private String zoom;

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public String getZoom() {
        return zoom;
    }

    public void setZoom(String zoom) {
        this.zoom = zoom;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Ciudad ciudad = (Ciudad) o;

        return codigo.equals(ciudad.codigo);

    }

    @Override
    public int hashCode() {
        return codigo.hashCode();
    }


    @Override
    public String toString() {
        return this.descripcion;
    }
}