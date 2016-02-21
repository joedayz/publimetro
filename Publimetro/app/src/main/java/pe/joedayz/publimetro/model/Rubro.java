package pe.joedayz.publimetro.model;

import java.io.Serializable;

/**
 * Created by josediaz on 2/20/16.
 */
public class Rubro implements Serializable {

    private String codigo;
    private String descripcion;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Rubro rubro = (Rubro) o;

        return codigo.equals(rubro.codigo);

    }

    @Override
    public int hashCode() {
        return codigo.hashCode();
    }

    public String toString() {
        return this.descripcion;
    }
}
