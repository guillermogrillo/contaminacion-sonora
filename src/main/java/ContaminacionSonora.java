import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ContaminacionSonora {
	
	public static void main(String[] args) throws IOException {

		System.out.println("Comienza la carga de torres de medicion");
		Stream<String> torres = Files.lines(Paths.get("src/main/resources/estaciones/torres-de-monitoreo-inteligente.csv")).skip(1);
		Map<String, TorreMedicion> mapaTorres =
				torres.map(line -> line.split(","))
						.collect(Collectors.toMap(line -> line[0], line -> new TorreMedicion(line[1],line[2])));
		System.out.println("Finaliza la carga de torres de medicion");


		Map<String, List<MedicionSonora>> mapaMediciones = new HashMap<>();


		Files.walk(Paths.get("src/main/resources/mediciones"))
				.filter(Files::isRegularFile)
				.forEach(f->cargarMediciones(f, mapaMediciones));

		System.out.println("Finaliza la carga de mediciones");
		System.out.println("Finaliza la carga de los archivos");

        //ejemplo. 1305 es almagro
        List<MedicionSonora> medicionSonoras = mapaMediciones.get("1305");
        List<MedicionSonora> medicionesTardeNoche = medicionSonoras.stream().filter(medicionSonora ->
                medicionSonora.getFechaHoraMedicion().getHour() > 18 || medicionSonora.getFechaHoraMedicion().getHour() < 7)
                .collect(Collectors.toList()); //mediciones con hora despues de las 18 hasta las 7 de la mañana (tarde/noche)
        int sumMedicionesTardeNoche = medicionesTardeNoche.stream().mapToInt(MedicionSonora::getPromedioMedicion).sum();
        int countMedicionesTardeNoche = medicionesTardeNoche.size();
        System.out.println("Medicion promedio tarde/noche en Almagro: " + (sumMedicionesTardeNoche/countMedicionesTardeNoche));

    }

	private static void cargarMediciones(Path f, Map<String, List<MedicionSonora>> mapaMediciones) {
        try {
            System.out.println("Cargando mediciones "+f.getFileName());
            Stream<String> mediciones = Files.lines(f.toAbsolutePath()).skip(1);
            mediciones.forEach(line -> {
                String[] split = line.split(";");
                if (!split[1].equals("")) {
                    MedicionSonora medicionSonora = new MedicionSonora(Integer.valueOf(split[1]), split[2]);
                    List<MedicionSonora> medicionesSonoras = mapaMediciones.get(split[0]);
                    if (medicionesSonoras != null) {
                        medicionesSonoras.add(medicionSonora);
                        mapaMediciones.put(split[0], medicionesSonoras);
                    } else {
                        List<MedicionSonora> medicionSonorasNew = new ArrayList<>();
                        medicionSonorasNew.add(medicionSonora);
                        mapaMediciones.put(split[0], medicionSonorasNew);
                    }
                }
            });
        } catch (IOException e) {
            System.out.println("Archivo no leido, siguiendo con el proximo");
        }
    }
}
