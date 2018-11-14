import java.util.ArrayList;
import java.util.List;

public class TorreMedicion {

    private String barrio;
    private String direccion;
    private List<MedicionSonora> mediciones;

    public TorreMedicion(String barrio, String direccion) {
        this.barrio = barrio;
        this.direccion = direccion;
        this.setMediciones(new ArrayList<>());
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

	public List<MedicionSonora> getMediciones() {
		return mediciones;
	}

	public void setMediciones(List<MedicionSonora> mediciones) {
		this.mediciones = mediciones;
	}
}
