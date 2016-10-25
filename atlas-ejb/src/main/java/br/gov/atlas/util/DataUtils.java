package br.gov.atlas.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class DataUtils {

	public static final String FORMATO_DATA_DDYYAAAA = "dd/MM/yyyy";
	public static final String FORMATO_HORA_MINUTO = "hh:mm";

	public static Collection<Integer> getListaAnos(Integer anoInicial) {

		GregorianCalendar gcAnoAtual = (GregorianCalendar) GregorianCalendar.getInstance();
		gcAnoAtual.setTime(new Date());
		Collection<Integer> listaAnos = new LinkedHashSet<Integer>();
		for (int i = gcAnoAtual.get(GregorianCalendar.YEAR); i >= anoInicial; i--) {
			listaAnos.add(i);
		}
		return listaAnos;
	}

	public static List<Integer> getListaAnos() {

		GregorianCalendar gcAnoAtual = (GregorianCalendar) GregorianCalendar.getInstance();
		gcAnoAtual.setTime(new Date());
		List<Integer> listaAnos = new LinkedList<Integer>();
		for (int i = gcAnoAtual.get(GregorianCalendar.YEAR); i >= 2000; i--) {
			listaAnos.add(i);
		}
		return listaAnos;
	}

	public static Timestamp dateToTimeStamp(String formato, Date dataBanco) {
		Timestamp data = new Timestamp(dataBanco.getTime());
		return data;
	}

	public static Timestamp strToTimeStamp(String dataBanco) {
		return strToTimeStamp(dataBanco, "dd/MM/yyyy");
	}

	public static Timestamp strToTimeStamp(String formato, String dataBanco) {
		return dateToTimeStamp(DataUtils.strToDate(formato, dataBanco));
	}

	public static Timestamp dateToTimeStamp(Date dataBanco) {
		return dateToTimeStamp("dd/MM/yyyy", dataBanco);
	}

	public static Date strToDate(String formato, String data) {
		Date parse = null;
		SimpleDateFormat df = new SimpleDateFormat(formato);
		try {
			parse = df.parse(data);
		} catch (ParseException pe) {

		}
		return parse;
	}
	
	 public static String dateToString (Date data,String formatoDestino) throws Exception {
       String dataString = "";
       try {  
    	   SimpleDateFormat sdfDestino  = new SimpleDateFormat(formatoDestino); 
           dataString = sdfDestino.format(data);
        } catch (Exception e) {  
        	e.printStackTrace();
          throw e;
        } 
        return dataString;
	 }

	public static Date getData(String data, int horas, int minutos, String formato) {

		GregorianCalendar gcData = (GregorianCalendar) GregorianCalendar.getInstance();
		gcData.setTime(strToDate(formato, data));

		GregorianCalendar gcDataHora = new GregorianCalendar(new Locale("pt", "BR"));
		gcDataHora.set(gcData.get(GregorianCalendar.YEAR), gcData.get(GregorianCalendar.MONTH), gcData.get(GregorianCalendar.DAY_OF_MONTH), horas,
				minutos);

		return gcDataHora.getTime();
	}

	public static Timestamp dateToTimestamp(java.util.Date data) {
		Timestamp dataHoraTp = new Timestamp(data.getTime());
		return dataHoraTp;
	}

	public static String formatDateToString(Date data, String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(data);
	}

	public static String TimestampToStr(String formato, Timestamp dataBanco) {
		DateFormat dataEntrada = new SimpleDateFormat(formato);
		Date data = new Date(dataBanco.getTime());
		return dataEntrada.format(data);
	}

	/**
	 * Verifica se a Data Inicial é maior que a Data Final (dd/MM/yyyy)
	 * 
	 * @param dataInicial Data inicial.
	 * @param dataFinal Data final.
	 */
	public static boolean checkDataMaior(Date dataInicial, Date dataFinal) {
		boolean ret = false;

		if (dataInicial.after(dataFinal)) {
			ret = true;
		} else if (dataInicial.before(dataFinal)) {
			ret = false;
		}

		return ret;
	}
	
	public static String getHojePorExtenso(){
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");  
        Date hoje = Calendar.getInstance(Locale.getDefault()).getTime();  
        return sdf.format(hoje);
	}    

}
