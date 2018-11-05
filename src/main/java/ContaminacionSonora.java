import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ContaminacionSonora {
	
	public static void main(String[] args) throws IOException {

		System.out.println("Comienza la carga de torres de medicion");
		Stream<String> torres = Files.lines(Paths.get("/tmp/contaminacion-sonora/torres/torres-de-monitoreo-inteligente.csv")).skip(1);
		Map<String, TorreMedicion> mapaTorres =
				torres.map(line -> line.split(","))
						.collect(Collectors.toMap(line -> line[0], line -> new TorreMedicion(line[1],line[2])));
		System.out.println("Finaliza la carga de torres de medicion");


		Map<String, List<MedicionSonora>> mapaMediciones = new HashMap<>();


		Files.walk(Paths.get("/tmp/contaminacion-sonora/mediciones"))
				.filter(Files::isRegularFile)
				.forEach(f->{
					try {
						System.out.println("Cargando mediciones "+f.getFileName());
						Stream<String> mediciones = Files.lines(f.toAbsolutePath()).skip(1);
						mediciones.map(line -> line.split(";"))
								.collect(
										Collectors.toMap(line -> line[0], line -> new MedicionSonora(Integer.valueOf(line[1]), line[2]))
								);
					} catch (IOException e) {
						System.out.println("Archivo no leido, siguiendo con el proximo");
					}

				});
		System.out.println("Finaliza la carga de mediciones");


		System.out.println("Finaliza la carga de los archivos");
	}

}
