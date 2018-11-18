import com.github.signaflo.data.regression.LinearRegression;
import com.github.signaflo.data.visualization.Plots;
import com.github.signaflo.timeseries.TimePeriod;
import com.github.signaflo.timeseries.TimeSeries;
import com.github.signaflo.timeseries.forecast.Forecast;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ContaminacionSonora {

    public static String ID_ALMAGRO = "1305";
    public static String ID_BALVANERA = "1304";
    public static String ID_PALERMO = "1313";
    public static String ID_RECOLETA = "1314";
    public static String ID_CABALLITO = "1315";
    public static String ID_FLORES = "1316";
    public static String ID_CENTRO = "1318";

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

        //Explicar el caso del centro de lunes a viernes de 9 a 18
        horarioLaboralCentro(mapaTorres);
        //Explicar el caso de barrios aledaños al centro de 18 a 19
        //postTrabajoCercaDelCentro(mapaTorres);
        //Explicar el caso de períodos vacacionales en el centro
        //vacacionesEnElCentro(mapaTorres);
        //Estudiar el caso de las distintas estaciones del año, para ver sí hay algo particular.
        //veranoVsInvierno(mapaTorres);
        //Estudiar el caso de navidad y año nuevo, puntualmente a las 12, por la pirotecnia.
        //fiestasEnBarrios(mapaTorres);


    }

    private static void horarioLaboralCentro(Map<String, TorreMedicion> mapaTorres) {

        List<MedicionSonora> medicionSonorasHorarioLaboral =
                mapaTorres.get(ID_CENTRO)
                    .getMediciones()
                    .stream()
                    .filter(ms ->
                            ms.isHorarioLaboral() &&
                            ms.isDiaDeSemana() &&
                            ms.getPromedioMedicion() > 0) //excluimos posibles valores invalidos
                    .collect(Collectors.toList());

        TimeSeries tsHorarioLaboral = getTimeSeries(medicionSonorasHorarioLaboral);
        System.out.println("--------------MEDICIONES TOMADAS EN EL CENTRO, L a V de 9 a 18 ----------------------");
        System.out.println("VALOR PROMEDIO: " + (tsHorarioLaboral.mean()));
        System.out.println("MEDIA: " + (tsHorarioLaboral.median()));
        System.out.println("DESVIO ESTANDAR: " + (tsHorarioLaboral.stdDeviation()));

        Plots.plot(tsHorarioLaboral.aggregate(TimePeriod.oneHour()), "CENTRO-L a V de 9 a 18", "Valor medido");

        List<MedicionSonora> medicionSonorasHorarioNoLaboral =
                mapaTorres.get(ID_CENTRO)
                        .getMediciones()
                        .stream()
                        .filter(ms ->
                                !ms.isHorarioLaboral() &&
                                ms.isDiaDeSemana() &&
                                ms.getPromedioMedicion() > 0) //excluimos posibles valores invalidos
                        .collect(Collectors.toList());

        TimeSeries tsHorarioNoLaboral = getTimeSeries(medicionSonorasHorarioNoLaboral);
        System.out.println("--------------MEDICIONES TOMADAS EN EL CENTRO, L a V antes de las 9 y despues de las 18 ----------------------");
        System.out.println("VALOR PROMEDIO: " + (tsHorarioNoLaboral.mean()));
        System.out.println("MEDIA: " + (tsHorarioNoLaboral.median()));
        System.out.println("DESVIO ESTANDAR: " + (tsHorarioNoLaboral.stdDeviation()));

        Plots.plot(tsHorarioNoLaboral.aggregate(TimePeriod.oneHour()), "CENTRO-L a V de 0 a 8 y de 19 a 23", "Valor medido");
    }

    private static void postTrabajoCercaDelCentro(Map<String, TorreMedicion> mapaTorres) {

        List<MedicionSonora> medicionSonoras =
                mapaTorres.get(ID_BALVANERA)
                        .getMediciones()
                        .stream()
                        .filter(ms ->
                                ms.isHorarioLaboral() &&
                                        ms.isDiaDeSemana() &&
                                        ms.getPromedioMedicion() > 0) //excluimos posibles valores invalidos
                        .collect(Collectors.toList());

        TimeSeries ts = getTimeSeries(medicionSonoras);
        System.out.println("--------------MEDICIONES TOMADAS EN BALVANERA, FUERA DE HORARIOS LABORALES EN DIAS DE SEMANA ----------------------");
        System.out.println("VALOR PROMEDIO: " + (ts.mean()));
        System.out.println("MEDIA: " + (ts.median()));
        System.out.println("DESVIO ESTANDAR: " + (ts.stdDeviation()));


        Plots.plot(ts.aggregate(TimePeriod.oneHour()), "BALVANERA-DIAS LABORALES-HORARIOS NO LABORALES", "Valor medido");

    }

    private static void vacacionesEnElCentro(Map<String, TorreMedicion> mapaTorres) {
        List<MedicionSonora> medicionSonorasEnVacaciones =
                mapaTorres.get(ID_CENTRO)
                        .getMediciones()
                        .stream()
                        .filter(ms ->
                                ms.isHorarioLaboral() &&
                                ms.isVerano() &&
                                ms.isDiaDeSemana() &&
                                ms.getPromedioMedicion() > 0) //excluimos posibles valores invalidos
                        .collect(Collectors.toList());

        TimeSeries ts = getTimeSeries(medicionSonorasEnVacaciones);
        System.out.println("--------------MEDICIONES TOMADAS EN EL CENTRO, EN HORARIOS LABORALES, DIAS DE SEMANA DE VACACIONES----------------------");
        System.out.println("VALOR PROMEDIO: " + (ts.mean()));
        System.out.println("MEDIA: " + (ts.median()));
        System.out.println("DESVIO ESTANDAR: " + (ts.stdDeviation()));


        Plots.plot(ts.aggregate(TimePeriod.oneHour()), "CENTRO-DIA Y HORARIO LABORAL-MESES DE VACACIONES (ENERO-FEBRERO)", "Valor medido");
    }

    private static void veranoVsInvierno(Map<String, TorreMedicion> mapaTorres) {
        List<MedicionSonora> medicionSonorasVerano =
                mapaTorres.get(ID_CENTRO)
                        .getMediciones()
                        .stream()
                        .filter(ms ->
                                ms.isHorarioLaboral() &&
                                ms.isVerano() &&
                                ms.isDiaDeSemana() &&
                                ms.getPromedioMedicion() > 0) //excluimos posibles valores invalidos
                        .collect(Collectors.toList());

        TimeSeries tsVerano = getTimeSeries(medicionSonorasVerano);
        System.out.println("--------------MEDICIONES TOMADAS EN EL CENTRO, EN HORARIOS LABORALES, DIAS DE SEMANA DE VERANO----------------------");
        System.out.println("VALOR PROMEDIO: " + (tsVerano.mean()));
        System.out.println("MEDIA: " + (tsVerano.median()));
        System.out.println("DESVIO ESTANDAR: " + (tsVerano.stdDeviation()));


        Plots.plot(tsVerano.aggregate(TimePeriod.oneHour()), "CENTRO-DIA Y HORARIO LABORAL-VERANO", "Valor medido");


        List<MedicionSonora> medicionSonorasInvierno =
                mapaTorres.get(ID_CENTRO)
                        .getMediciones()
                        .stream()
                        .filter(ms ->
                                ms.isHorarioLaboral() &&
                                ms.isInvierno() &&
                                ms.isDiaDeSemana() &&
                                ms.getPromedioMedicion() > 0) //excluimos posibles valores invalidos
                        .collect(Collectors.toList());

        TimeSeries tsInvierno
                = getTimeSeries(medicionSonorasInvierno);
        System.out.println("--------------MEDICIONES TOMADAS CERCA DEL CENTRO, EN DE HORARIOS LABORALES, DIAS DE SEMANA DE INVIERNO----------------------");
        System.out.println("VALOR PROMEDIO: " + (tsInvierno.mean()));
        System.out.println("MEDIA: " + (tsInvierno.median()));
        System.out.println("DESVIO ESTANDAR: " + (tsInvierno.stdDeviation()));


        Plots.plot(tsInvierno.aggregate(TimePeriod.oneHour()), "CENTRO-DIA Y HORARIO LABORAL-INVIERNO", "Valor medido");


    }

    private static void fiestasEnBarrios(Map<String, TorreMedicion> mapaTorres) {
        List<MedicionSonora> medicionSonorasFiestasCentro =
                mapaTorres.get(ID_CENTRO)
                        .getMediciones()
                        .stream()
                        .filter(ms ->
                                (ms.getFechaHoraMedicion().getHour()==00) &&
                                ms.getFechaHoraMedicion().getDayOfYear() == 1 &&
                                ms.getPromedioMedicion() > 0)
                        .collect(Collectors.toList());

        TimeSeries tsCentro = getTimeSeries(medicionSonorasFiestasCentro);
        System.out.println("--------------MEDICIONES TOMADAS EN EL CENTRO, EN AÑO NUEVO----------------------");
        System.out.println("VALOR PROMEDIO: " + (tsCentro.mean()));
        System.out.println("MEDIA: " + (tsCentro.median()));
        System.out.println("DESVIO ESTANDAR: " + (tsCentro.stdDeviation()));


        Plots.plot(tsCentro.aggregate(TimePeriod.oneHour()), "CENTRO-AÑO NUEVO", "Valor medido");


        List<MedicionSonora> medicionSonorasFiestasCaballito =
                mapaTorres.get(ID_CABALLITO)
                        .getMediciones()
                        .stream()
                        .filter(ms ->
                                (ms.getFechaHoraMedicion().getHour()==00) &&
                                        ms.getFechaHoraMedicion().getDayOfYear() == 1 &&
                                        ms.getPromedioMedicion() > 0)
                        .collect(Collectors.toList());

        TimeSeries tsCaballito = getTimeSeries(medicionSonorasFiestasCaballito);
        System.out.println("--------------MEDICIONES TOMADAS EN CABALLITO, EN FIESTAS----------------------");
        System.out.println("VALOR PROMEDIO: " + (tsCaballito.mean()));
        System.out.println("MEDIA: " + (tsCaballito.median()));
        System.out.println("DESVIO ESTANDAR: " + (tsCaballito.stdDeviation()));


        Plots.plot(tsCaballito.aggregate(TimePeriod.oneHour()), "CABALLITO-AÑO NUEVO", "Valor medido");


        List<MedicionSonora> medicionSonorasFiestasAlmagro =
                mapaTorres.get(ID_ALMAGRO)
                        .getMediciones()
                        .stream()
                        .filter(ms ->
                                (ms.getFechaHoraMedicion().getHour()==00) &&
                                        ms.getFechaHoraMedicion().getDayOfYear() == 1 &&
                                        ms.getPromedioMedicion() > 0)
                        .collect(Collectors.toList());

        TimeSeries tsAlmagro = getTimeSeries(medicionSonorasFiestasAlmagro);
        System.out.println("--------------MEDICIONES TOMADAS EN ALMAGRO, EN FIESTAS----------------------");
        System.out.println("VALOR PROMEDIO: " + (tsAlmagro.mean()));
        System.out.println("MEDIA: " + (tsAlmagro.median()));
        System.out.println("DESVIO ESTANDAR: " + (tsAlmagro.stdDeviation()));


        Plots.plot(tsAlmagro.aggregate(TimePeriod.oneHour()), "ALMAGRO-AÑO NUEVO", "Valor medido");
    }

    private static TimeSeries getTimeSeries(List<MedicionSonora> medicionSonoras) {
        double[] mediciones = medicionSonoras.stream().mapToDouble(medicion -> medicion.getPromedioMedicion()).toArray();

        return TimeSeries.from(
                TimePeriod.oneHour(),
                medicionSonoras.stream().map(medicion -> medicion.getFechaHoraMedicion().atOffset(ZoneOffset.UTC)).collect(Collectors.toList()),
                mediciones
        );
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
