package pe.joedayz.publimetro.model;

import java.io.Serializable;

/**
 * Created by josediaz on 3/19/16.
 */
public class Oferta implements Serializable {

    private String codigo;
    private String ofertaCodigo;
    private String categoriaCodigo;
    private String detalle;
    private String fileName;


    public Oferta(String codigo, String ofertaCodigo, String categoriaCodigo, String detalle, String fileName) {
        this.codigo = codigo;
        this.ofertaCodigo = ofertaCodigo;
        this.categoriaCodigo = categoriaCodigo;
        this.detalle = detalle;
        this.fileName = fileName;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getOfertaCodigo() {
        return ofertaCodigo;
    }

    public void setOfertaCodigo(String ofertaCodigo) {
        this.ofertaCodigo = ofertaCodigo;
    }

    public String getCategoriaCodigo() {
        return categoriaCodigo;
    }

    public void setCategoriaCodigo(String categoriaCodigo) {
        this.categoriaCodigo = categoriaCodigo;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Oferta oferta = (Oferta) o;

        return !(codigo != null ? !codigo.equals(oferta.codigo) : oferta.codigo != null);

    }

    @Override
    public int hashCode() {
        return codigo != null ? codigo.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Oferta{" +
                "codigo='" + codigo + '\'' +
                ", ofertaCodigo='" + ofertaCodigo + '\'' +
                ", categoriaCodigo='" + categoriaCodigo + '\'' +
                ", detalle='" + detalle + '\'' +
                ", fileName='" + fileName + '\'' +
                '}';
    }
}
