package pe.joedayz.publimetro.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by josediaz on 2/20/16.
 */
public class Establecimiento implements Serializable{

    private String codigo;
    private String categoriaCodigo;
    private String titulo;

    private String resumen;
    private String detalle;



    private String fileName;
    private String fileNameLogo;

    private String horario;
    private String facebook;
    private String web;

    private List<String> telefonos;
    private List<Ubicacion> ubicaciones;

    private int zoom;



    public Establecimiento(String codigo, String categoriaCodigo, String titulo, String resumen, String detalle, String fileName, String fileNameLogo, String horario, String facebook, String web, List<String> telefonos, List<Ubicacion> ubicaciones, int zoom) {
        this.codigo = codigo;
        this.categoriaCodigo = categoriaCodigo;
        this.titulo = titulo;
        this.resumen = resumen;
        this.detalle = detalle;
        this.fileName = fileName;
        this.fileNameLogo = fileNameLogo;
        this.horario = horario;
        this.facebook = facebook;
        this.web = web;
        this.telefonos = telefonos;
        this.ubicaciones = ubicaciones;
        this.zoom =zoom;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getCategoriaCodigo() {
        return categoriaCodigo;
    }

    public void setCategoriaCodigo(String categoriaCodigo) {
        this.categoriaCodigo = categoriaCodigo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileNameLogo() {
        return fileNameLogo;
    }

    public void setFileNameLogo(String fileNameLogo) {
        this.fileNameLogo = fileNameLogo;
    }


    public String getResumen() {
        return resumen;
    }

    public void setResumen(String resumen) {
        this.resumen = resumen;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getWeb() {
        return web;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    public List<String> getTelefonos() {
        return telefonos;
    }

    public void setTelefonos(List<String> telefonos) {
        this.telefonos = telefonos;
    }

    public List<Ubicacion> getUbicaciones() {
        return ubicaciones;
    }

    public void setUbicaciones(List<Ubicacion> ubicaciones) {
        this.ubicaciones = ubicaciones;
    }


    public int getZoom() {
        return zoom;
    }

    public void setZoom(int zoom) {
        this.zoom = zoom;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Establecimiento that = (Establecimiento) o;

        return codigo.equals(that.codigo);

    }

    @Override
    public int hashCode() {
        return codigo.hashCode();
    }

    @Override
    public String toString() {
        return this.titulo;
    }
}
