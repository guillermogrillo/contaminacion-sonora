import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PruebaFechas {


    public static void main(String[] args){


        //String laFecha = "01/01/2012 0:00";
        //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");


        String now = "01/01/2012 0:00";
        DateTimeFormatter formatterUno = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        DateTimeFormatter formatterDos = DateTimeFormatter.ofPattern("dd/MM/yyyy H:mm");


        try {
            LocalDateTime ldt = LocalDateTime.parse(now, formatterUno);
            ldt.getDayOfMonth();
        } catch(Exception e){
            LocalDateTime ldt = LocalDateTime.parse(now, formatterDos);
            ldt.getDayOfMonth();
        }

        System.out.println("asd");



    }

}
