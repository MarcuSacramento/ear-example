package br.gov.atlas.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRAbstractExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.util.JRLoader;

public class RelatorioUtil {

	public static final String PDF = "pdf";
	public static final String XLS = "xls";
	public static final String RTF = "rtf";
	private static final String ZIP = "zip";

	/**
	 * Gerar relatorio.
	 *
	 * @param dados the dados
	 * @param caminhoRelatorio the caminho relatorio
	 * @param nomeArquivoGerado the nome arquivo gerado
	 * @throws Exception the exception
	 */
	public static void gerarRelatorio(List<?> dados, String caminhoRelatorio, String nomeArquivoGerado) throws Exception {
		gerarRelatorio(dados, caminhoRelatorio, nomeArquivoGerado, new HashMap<String, Object>(), PDF, null);
	}

	/**
	 * Gerar relatorio.
	 *
	 * @param dados the dados
	 * @param caminhoRelatorio the caminho relatorio
	 * @param nomeArquivoGerado the nome arquivo gerado
	 * @param parameters the parameters
	 * @throws Exception the exception
	 */
	public static void gerarRelatorio(List<?> dados, String caminhoRelatorio, String nomeArquivoGerado, Map<String, Object> parameters) throws Exception {
		gerarRelatorio(dados, caminhoRelatorio, nomeArquivoGerado, parameters, PDF, null);
	}

	/**
	 * Gerar relatorio passando Connection
	 * @param dados
	 * @param caminhoRelatorio
	 * @param nomeArquivoGerado
	 * @param parameters
	 * @throws Exception
	 */
	public static void gerarRelatorio(String caminhoRelatorio, String nomeArquivoGerado, Map<String, Object> parameters, Connection conn) throws Exception {
		gerarRelatorio(null, caminhoRelatorio, nomeArquivoGerado, parameters, PDF, conn);
	}


	public static void gerarRelatorio(List<?> dados, String caminhoRelatorio,
			String nomeArquivoGerado, Map<String, Object> parameters,
			String tipo) throws Exception {
		gerarRelatorio(dados, caminhoRelatorio, nomeArquivoGerado, parameters,
				tipo, null);
	}

	public static void gerarRelatorio(List<?> dados, String caminhoRelatorio,
			String nomeArquivoGerado, Map<String, Object> parameters,
			String tipo,  boolean compactarArquivo) throws Exception {
		gerarRelatorio(dados, caminhoRelatorio, nomeArquivoGerado, parameters,
				tipo, null, compactarArquivo);
	}

	public static void gerarRelatorio(List<?> dados, String caminhoRelatorio,
			String nomeArquivoGerado, Map<String, Object> parameters,
			String tipoRelatorio, Connection conn) throws Exception {
		gerarRelatorio(dados, caminhoRelatorio, nomeArquivoGerado, parameters,
				tipoRelatorio, conn, false);
	}

	/**
	 * Gerar relatorio.
	 *
	 * @param dados the dados
	 * @param caminhoRelatorio the caminho relatorio
	 * @param nomeArquivoGerado the nome arquivo gerado
	 * @param parameters the parameters
	 * @param tipo the tipo
	 * @throws Exception the exception
	 */
	public static void gerarRelatorio(List<?> dados, String caminhoRelatorio,
			String nomeArquivoGerado, Map<String, Object> parameters,
			String tipoRelatorio, Connection conn, boolean compactarArquivo)
					throws Exception {
		BufferedInputStream bis = new BufferedInputStream(
				(new ByteArrayInputStream((new RelatorioUtil()).gerar(dados,
						caminhoRelatorio, parameters, tipoRelatorio, conn))));

		download(tipoRelatorio, nomeArquivoGerado, bis, compactarArquivo);
		FacesContext.getCurrentInstance().responseComplete();
	}

	private static void download(String tipoRelatorio,
			String nomeArquivoGerado, BufferedInputStream bis,
			boolean compactarArquivo) throws IOException {
		HttpServletResponse response = ((HttpServletResponse) FacesContext
				.getCurrentInstance().getExternalContext().getResponse());

		if (compactarArquivo) {
			tipoRelatorio = ZIP;

			File arquivoZip = ArquivoUtil.compactarArquivo(nomeArquivoGerado, bis);
			nomeArquivoGerado = arquivoZip.getName();
			bis = new BufferedInputStream(new FileInputStream(arquivoZip));
		}
		definirCabecalho(tipoRelatorio, nomeArquivoGerado, response);

		int rLength = -1;

		byte[] buffer = new byte[1000];

		while ((rLength = bis.read(buffer, 0, 100)) != -1) {
			response.getOutputStream().write(buffer, 0, rLength);
		}

		response.getOutputStream().flush();

		bis.close();
		response.getOutputStream().close();

	}

	private static void definirCabecalho(String tipoRelatorio,
			String nomeArquivoGerado, HttpServletResponse response) {
		response.reset();

		if (tipoRelatorio.equals(ZIP)) {
			response.setContentType("application/zip");
		} else {
			if (tipoRelatorio.equals(XLS)) {
				response.setContentType("application/vnd.ms-excel");
			} else if (tipoRelatorio.equals(PDF)) {
				response.setContentType("application/pdf");
			} else if (tipoRelatorio.equals(RTF)) {
				response.setContentType("application/rtf");
			}
		}

		response.setHeader("Content-disposition", "attachment; filename=\""
				+ nomeArquivoGerado + "\"");
	}

	/**
	 * Gerar.
	 *
	 * @param dados the dados
	 * @param caminhoRelatorio the caminho relatorio
	 * @param parameters the parameters
	 * @param tipo the tipo
	 * @return the byte[]
	 * @throws Exception the exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private byte[] gerar(List<?> dados, String caminhoRelatorio, Map parameters, String tipoRelatorio, Connection conn) throws Exception {

		JRBeanCollectionDataSource beanColDataSource = new JRBeanCollectionDataSource(dados);

		parameters.put("LOGO",FacesContext.getCurrentInstance().getExternalContext().getRealPath("/pub/_imagens/_logo/logo_atlas.jpg"));
		parameters.put("LOGO_BRANCO",FacesContext.getCurrentInstance().getExternalContext().getRealPath("/pub/_imagens/_logo/logo_atlas_branco.png"));
		parameters.put("FUNDO",FacesContext.getCurrentInstance().getExternalContext().getRealPath("/pub/_imagens/_cabecalho/fundo_cabecalho.jpg"));

		JasperReport jasperReport = (JasperReport) JRLoader.loadObject(getClass().getResourceAsStream(caminhoRelatorio));
		JasperPrint jasperPrint;

		if(conn != null){
			jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, conn);
			conn.close();
		}else{
			jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, beanColDataSource);
		}


		if (tipoRelatorio.equals(XLS)){
			JRXlsExporter exporter = new JRXlsExporter();
			ByteArrayOutputStream saida = new ByteArrayOutputStream();

			exporter.setParameter(JRExporterParameter.CHARACTER_ENCODING, "UTF-8");
			exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
			exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, saida);
			exporter.exportReport();

			return saida.toByteArray();
		} else if (tipoRelatorio.equals(RTF)){
			JRAbstractExporter exporter = new JRRtfExporter();
			ByteArrayOutputStream saida = new ByteArrayOutputStream();

			exporter.setParameter(JRExporterParameter.CHARACTER_ENCODING, "UTF-8");
			exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
			exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, saida);
			exporter.exportReport();

			return saida.toByteArray();
		}

		return JasperExportManager.exportReportToPdf(jasperPrint);
	}
}
