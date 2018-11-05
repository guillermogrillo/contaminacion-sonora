public class TorreMedicion {

    private String barrio;
    private String direccion;


    public TorreMedicion() {
    }

    public TorreMedicion(String barrio, String direccion) {
        this.barrio = barrio;
        this.direccion = direccion;
    }

    public String getBarrio() {
        return barrio;
    }

    public void setBarrio(String barrio) {
        this.barrio = barrio;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
}
