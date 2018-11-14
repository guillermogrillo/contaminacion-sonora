import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import com.github.signaflo.timeseries.TimeSeries;
import com.github.signaflo.timeseries.forecast.Forecast;
import com.github.signaflo.timeseries.TimePeriod;
import com.github.signaflo.data.visualization.Plots;

public class ContaminacionSonora {
	
	public static void main(String[] args) throws IOException {

		System.out.println("Comienza la carga de torres de medicion");
		Reader in = new FileReader("src/main/resources/estaciones/torres-de-monitoreo-inteligente.csv");
		Iterable<CSVRecord> records = CSVFormat.DEFAULT.withHeader().parse(in);
		Map<String, TorreMedicion> mapaTorres = new HashMap<>();
		records.forEach(record -> mapaTorres.put(record.get("TMI"), new TorreMedicion(record.get("BARRIO"), record.get("DIRECCION"))));
		System.out.println("Finaliza la carga de torres de medicion");

		System.out.println("Comienza la carga de mediciones");
		Files.walk(Paths.get("src/main/resources/mediciones"))
				.filter(Files::isRegularFile)
				.forEach(f->cargarMediciones(f, mapaTorres));
		System.out.println("Finaliza la carga de mediciones");
		
		System.out.println("Finaliza la carga de los archivos");

        //ejemplo. 1305 es almagro
        List<MedicionSonora> medicionSonoras = mapaTorres.get("1295").getMediciones();
        List<MedicionSonora> medicionesTardeNoche = medicionSonoras.stream().filter(medicionSonora ->
                medicionSonora.getFechaHoraMedicion().getHour() > 18 || medicionSonora.getFechaHoraMedicion().getHour() < 7)
                .collect(Collectors.toList()); //mediciones con hora despues de las 18 hasta las 7 de la mañana (tarde/noche)
        double sumMedicionesTardeNoche = medicionesTardeNoche.stream().mapToDouble(MedicionSonora::getPromedioMedicion).sum();
        int countMedicionesTardeNoche = medicionesTardeNoche.size();
        System.out.println("Medicion promedio tarde/noche en Almagro: " + (sumMedicionesTardeNoche/countMedicionesTardeNoche));

        
        //no se como hacer esto bien
        double[] puntos = new double[medicionSonoras.size()];
        for (int i = 0; i < puntos.length; i++) {
           puntos[i] = medicionSonoras.get(i).getPromedioMedicion();
        }
        
        TimeSeries ts = TimeSeries.from(
        		TimePeriod.oneHour(),
        		medicionSonoras.stream().map(medicion -> medicion.getFechaHoraMedicion().atOffset(ZoneOffset.UTC)).collect(Collectors.toList()),
        		puntos
        		);

        System.out.println("Medicion promedio en Almagro: " + (ts.mean()));
        System.out.println("Medicion media en Almagro: " + (ts.median()));
        System.out.println("Desviacion estandar en Almagro: " + (ts.stdDeviation()));
        
        
        Plots.plot(ts.aggregate(TimePeriod.oneWeek()));
    }

	private static void cargarMediciones(Path f, Map<String, TorreMedicion> mapaTorres) {
        try {
            System.out.println("Cargando mediciones "+f.getFileName());

            Reader in = new FileReader(f.toFile());
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.withHeader().parse(in);
            records.forEach(record -> {
            	if (!record.get("PROMEDIO_ENERGETICO_HORA").equals("")) {
                    MedicionSonora medicionSonora = new MedicionSonora(record.get("PROMEDIO_ENERGETICO_HORA"), record.get("FECHA"));
                    mapaTorres.get(record.get("TMI")).getMediciones().add(medicionSonora);
            	}
            });
            
        } catch (IOException e) {
            System.out.println("Archivo no leido, siguiendo con el proximo");
        }
    }
}
