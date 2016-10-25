package br.gov.atlas.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;

import javax.swing.text.MaskFormatter;

public class FormataValor {
	public static final String FORMATO_DATA_AAAA_MM_DD = "yyyy-MM-dd";
	public static final String FORMATO_DATA_DD_MM_AAAA = "dd/MM/yyyy";
	public static final String FORMATO_MOEDA = "#,###,###.00";

	public static Date formataData(String data) {
		return formataData(data, FormataValor.FORMATO_DATA_AAAA_MM_DD);
	}

	public static Date formataData(String data, String formato) {
		if (data == null || data.equals(""))
			return null;

		Date date = null;
		try {
			DateFormat formatter = new SimpleDateFormat(formato);
			date = formatter.parse(data);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * Retorna um objeto do tipo String a partir de um Date
	 * 
	 * @param formato Exemplos: dd/MM/yyyy, yyyy-MM-dd, etc.
	 * @param data Objeto que ser· convertido.
	 * @return String no formato escolhido.
	 */
	public static String dateToStr(String formato, Date data) {
		if (data == null)
			return "";
		SimpleDateFormat df = new SimpleDateFormat(formato);
		return df.format(data);
	}

	/**
	 * Converte uma data em String para TimeStamp
	 * 
	 * @param data String a ser convertida
	 * @return data em TimeStamp no formato dd/MM/yyyy
	 */
	public static Timestamp strToTimeStamp(String dataBanco) {
		return strToTimeStamp(dataBanco, FormataValor.FORMATO_DATA_DD_MM_AAAA);
	}

	/**
	 * Converte uma data em String para TimeStamp
	 * 
	 * @param formato String da data
	 * @param data String a ser convertida
	 * @return data em TimeStamp no formato informado
	 */
	public static Timestamp strToTimeStamp(String formato, String dataBanco) {
		return dateToTimeStamp(formato, strToDate(formato, dataBanco));
	}

	/**
	 * Converte uma data em Date para TimeStamp
	 * 
	 * @param data Date a ser convertida
	 * @return data em TimeStamp no formato dd/MM/yyyy
	 */
	public static Timestamp dateToTimeStamp(Date dataBanco) {
		return dateToTimeStamp(FormataValor.FORMATO_DATA_DD_MM_AAAA, dataBanco);
	}

	/**
	 * Converte uma data em Date para TimeStamp
	 * 
	 * @param formato String da data
	 * @param data Date a ser convertida
	 * @return data em TimeStamp no formato indicado
	 */
	public static Timestamp dateToTimeStamp(String formato, Date dataBanco) {
		Timestamp data = new Timestamp(dataBanco.getTime());
		return data;
	}

	/**
	 * Retorna um objeto do tipo date a partir de uma string
	 * 
	 * @param formato Exemplos: dd/MM/yyyy, yyyy-MM-dd, etc.
	 * @param data String que sera convertida.
	 * @return null caso a convers„o nao funcione.
	 */
	public static Date strToDate(String formato, String data) {
		Date parse = null;
		SimpleDateFormat df = new SimpleDateFormat(formato);
		try {
			parse = df.parse(data);
		} catch (ParseException pe) {
			pe.printStackTrace();
		}
		return parse;
	}

	/**
	 * Converte um valor de objeto String em Integer, caso ocorra Exception, ser· logado e retornado o(zero).
	 * 
	 * @param string
	 * @return
	 */
	public static Integer doStringToInteger(String string) {
		if (string == null || string.trim().length() == 0)
			return Integer.valueOf(0);
		try {
			return Integer.valueOf(string);
		} catch (Exception erro) {
			erro.printStackTrace();
			return Integer.valueOf(0);
		}
	}

	/**
	 * Converte um valor de objeto String em Double, caso ocorra Exception, ser· logado e retornado o(zero).
	 * 
	 * @param string
	 * @return
	 */
	public static Double doStringToDouble(String string) {
		if (string == null || string.trim().length() == 0)
			return Double.valueOf(0);
		try {
			string = string.replace(".", "");
			string = string.replace(",", ".");
			return Double.valueOf(string);
		} catch (Exception erro) {
			erro.printStackTrace();
			return Double.valueOf(0);
		}
	}

	public static int strToInt(String str) {
		return strToInt(str, 0);
	}

	public static int strToInt(String str, int padrao) {
		int retorno = padrao;
		try {
			retorno = (int) Double.parseDouble(str); //
		} catch (Exception ex) { // Exception ao invez de NumberFormatException pois a String pode ser null, gerando um NullPointerException
			retorno = padrao;
		}
		return retorno;
	}

	/**
	 * Retona a String padrao caso a String str seja null ou vazia.
	 * 
	 * @param str
	 * @param padrao
	 * @return Retona a String padrao caso a String str seja null ou vazia.
	 */

	public static String strNull(String str) {
		if (str == null) {
			str = "";
		}
		if ("NULL".equals(str.toUpperCase())) {
			str = "";
		}
		return str;
	}

	/**
	 * Retona a String padrao caso a String str seja null ou vazia.
	 * 
	 * @param str
	 * @param padrao
	 * @return Retona a String padrao caso a String str seja null ou vazia.
	 */

	public static String strNull(Object obj, String valor) {
		String retorno = "";
		if (obj == null) {
			retorno = valor;
		}
		return retorno;
	}

	/**
	 * Verifica se o objeto È nulo e retorna uma string vazia
	 * 
	 * @param obj
	 * @return
	 */

	public static String strNull(Object obj) {
		return strNull(obj, "");
	}

	public static String strNull(Integer num) {
		if (strNull(num, "").equals("")) {
			return num.toString();
		}
		return strNull(num, "--");
	}

	public static int boolToInt(boolean is) {
		int retorno = 0;

		if (is) {
			retorno = 1;
		}

		return retorno;
	}

	/**
	 * Converte uma string em boolean
	 * 
	 * @param strBoolean
	 * @return
	 */
	public static boolean strToBoolean(String strBoolean) {
		if (strBoolean == null)
			return false;
		return Boolean.valueOf(strBoolean).booleanValue();
	}

	/**
	 * Converte uma string em boolean, nesse caso, ela verifica campos que retornam os valores 0 ou 1
	 * */
	public static boolean novaStrToBoolean(String strBoolean) {

		boolean resultado = false;

		if (strBoolean.equals("0")) {
			resultado = false;
		} else {
			resultado = true;
		}
		return resultado;
	}

	/**
	 * Converte um integer em string com formataÁ„o.
	 * 
	 * @param valor
	 * @param formato - ex.: 0000 = 0001
	 * @return
	 */
	public static String doIntegerToString(Integer valor, String formato) {
		try {
			DecimalFormat format = new DecimalFormat(formato);
			return format.format(valor);
		} catch (Exception erro) {
			erro.printStackTrace();
		}
		return valor.toString();
	}

	/**
	 * Converte uma data de java.util.Date para java.sql.Date.
	 * 
	 * @param valor
	 * @return
	 */
	public static java.sql.Date doUtilsDateToSqlDate(Date data) {
		return new java.sql.Date(data.getTime());
	}

	/**
	 * Converte um objeto Double em uma String formatada
	 * 
	 * @param valor
	 * @param formato
	 * @return
	 */
	public static String doDoubleToString(Double valor, String formato) {
		try {
			DecimalFormat format = new DecimalFormat(formato);
			return format.format(valor);
		} catch (Exception erro) {
			erro.printStackTrace();
		}
		return valor.toString();
	}

	public static String formatDecimal(double valor) {
		DecimalFormat nf = new DecimalFormat("#,##0.00");
		// log.info("double = " + valor);
		String numFormatado = nf.format(valor);

		boolean multipleVir = checkMultipleChar(numFormatado, ',');
		int localVirg = numFormatado.indexOf(',');
		if (multipleVir || localVirg < 0) { // Peguei os que tem mais de uma vÌrgula ou nenhuma
			String inteiro = numFormatado.substring(0, numFormatado.indexOf('.')).replace(',', '.');
			String resto = numFormatado.substring(numFormatado.indexOf('.') + 1, numFormatado.length());
			numFormatado = inteiro + "," + resto;
		} else {
			String largura = numFormatado.substring(numFormatado.indexOf(',') + 1, numFormatado.length());
			if (largura.length() > 2) {
				String inteiro = numFormatado.substring(0, numFormatado.indexOf('.')).replace(',', '.');
				String resto = numFormatado.substring(numFormatado.indexOf('.') + 1, numFormatado.length());
				numFormatado = inteiro + "," + resto;
			}
		}
		return numFormatado;
	}

	/**
	 * Verifica se um determinado caracter se repete mais de uma vez em uma String.
	 * 
	 * @param numero
	 * @return true (caracter se repete mais de 1 vez) | false (caracter se repete 1 vez ou nenhuma)
	 */
	public static boolean checkMultipleChar(String numero, char separador) {
		int repeticao = 0;
		for (int i = 0; i < numero.length(); i++) {
			char caracter = numero.charAt(i);
			if (caracter == separador) {
				repeticao++;
			}
		}
		if (repeticao > 1) {
			return true;
		} else {
			return false;
		}
	}

	/* FunÁ„o para remover acentos de uma string */
	public static String removeAcentos(String parametro) {
		parametro = parametro.replaceAll("[ËÈÍÎ]", "e");
		parametro = parametro.replaceAll("[˚˘˙]", "u");
		parametro = parametro.replaceAll("[ÔÓÌÏ]", "i");
		parametro = parametro.replaceAll("[‡‚·]", "a");
		parametro = parametro.replaceAll("‘”“", "o");

		parametro = parametro.replaceAll("[»… À]", "E");
		parametro = parametro.replaceAll("[€Ÿ⁄]", "U");
		parametro = parametro.replaceAll("[œŒÕÃ]", "I");
		parametro = parametro.replaceAll("[¿¬¡]", "A");
		parametro = parametro.replaceAll("‘", "O");
		parametro = parametro.replaceAll(" ", "");
		return parametro;

	}

	public static String removeAcento(String parametro) {
		parametro = parametro.replace("/", "");
		parametro = parametro.replace(".", "");
		parametro = parametro.replace("-", "");
		parametro = parametro.replace(" ", "");

		return parametro;
	}

	public static String formataCPF(String valor) throws ParseException {
		MaskFormatter mf = new MaskFormatter("###.###.###-##");  
	    mf.setValueContainsLiteralCharacters(false);  
	    return mf.valueToString(valor); 
	}

	public static String removeMascaraCPFCNPJ(String cpfCnpj) {

		StringBuilder saida = new StringBuilder();
		for (int i = 0; i < cpfCnpj.length(); i++) {
			if (cpfCnpj.charAt(i) == '.' || cpfCnpj.charAt(i) == '-' || cpfCnpj.charAt(i) == '/') {
				saida.append("");
			} else {
				saida.append(cpfCnpj.charAt(i));
			}
		}
		return saida.toString();
	}

	/**
	 * Remove elementos duplicados de um array
	 * 
	 * @param array Array contendo os elementos duplicados
	 * @return String[] Array sem os elementos duplicados
	 * */
	public static String[] removerElementosDuplicados(String[] array) {
		// HashSet È uma coleÁ„o que n„o aceita elementos duplicados
		HashSet<String> fases = new HashSet<String>();
		for (String fase : array) {
			fases.add(fase);
		}
		// Retorna um array sem elementos repetidos
		return fases.toArray(new String[fases.size()]);

	}
}