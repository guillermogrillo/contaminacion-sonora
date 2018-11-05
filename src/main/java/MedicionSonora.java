import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MedicionSonora {

	private Integer promedioMedicion;
	private LocalDateTime fechaHoraMedicion;
	
	public MedicionSonora() {
	
	}
	
	
	public MedicionSonora(Integer promedioMedicion,
			String fechaHoraMedicion) {
		super();
		this.promedioMedicion = promedioMedicion;
		DateTimeFormatter formatterHH = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
		DateTimeFormatter formatterH = DateTimeFormatter.ofPattern("dd/MM/yyyy H:mm");

		try {
			this.fechaHoraMedicion  = LocalDateTime.parse(fechaHoraMedicion, formatterHH);
		} catch(Exception e){
			this.fechaHoraMedicion  = LocalDateTime.parse(fechaHoraMedicion, formatterH);
		}

	}
	

	public Integer getPromedioMedicion() {
		return promedioMedicion;
	}
	public void setPromedioMedicion(Integer promedioMedicion) {
		this.promedioMedicion = promedioMedicion;
	}
	public LocalDateTime getFechaHoraMedicion() {
		return fechaHoraMedicion;
	}
	public void setFechaHoraMedicion(LocalDateTime fechaHoraMedicion) {
		this.fechaHoraMedicion = fechaHoraMedicion;
	}
	
	
	
}
