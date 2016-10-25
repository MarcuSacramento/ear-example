package br.gov.atlas.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 * Classe utilitária para manipulação de arquivos XLS
 * 
 * @author Vinícius Rabelo
 * 
 */
public final class XlsUtil {

	public static LinkedList<LinkedList<Object>> lerPlanilha(
			String nomeArquivo, int numeroPlanilha, int quantidadeCelulas)
					throws FileNotFoundException, IOException {
		HSSFSheet planilha = obterPlanilha(nomeArquivo, numeroPlanilha);
		return lerPlanilha(planilha, quantidadeCelulas);
	}

	private static HSSFSheet obterPlanilha(String nomeArquivo,
			int numeroPlanilha) throws IOException, FileNotFoundException {
		HSSFWorkbook arquivoExcel = obterArquivoExcel(nomeArquivo);
		HSSFSheet planilha = obterPlanilha(arquivoExcel, numeroPlanilha);
		return planilha;
	}

	public static HSSFSheet obterPlanilha(HSSFWorkbook arquivoExcel,
			int numeroPlanilha) throws IOException, FileNotFoundException {
		HSSFSheet planilha = arquivoExcel.getSheetAt(numeroPlanilha);
		return planilha;
	}

	public static HSSFWorkbook obterArquivoExcel(String nomeArquivo)
			throws IOException, FileNotFoundException {
		HSSFWorkbook arquivoExcel = new HSSFWorkbook(new FileInputStream(
				nomeArquivo));
		return arquivoExcel;
	}

	private static LinkedList<LinkedList<Object>> lerPlanilha(
			HSSFSheet planilha, int quantidadeCelulas) {
		LinkedList<LinkedList<Object>> valoresPlanilha = new LinkedList<LinkedList<Object>>();
		int quantidadeLinhas = planilha.getPhysicalNumberOfRows();
		HSSFRow linha;
		for (int r = 0; r < quantidadeLinhas; r++) {
			linha = planilha.getRow(r);
			valoresPlanilha.add(extrairValoresLinha(linha, quantidadeCelulas));
		}
		return valoresPlanilha;
	}

	public static LinkedList<Object> extrairValoresLinha(HSSFRow linha,
			int quantidadeCelulas) {
		LinkedList<Object> valoresLinha = new LinkedList<Object>();
		if (linha != null) {
			HSSFCell celula;
			for (int c = 0; c <= quantidadeCelulas; c++) {
				celula = linha.getCell(c);
				try {
					celula.setCellType(HSSFCell.CELL_TYPE_STRING);
				} catch (NullPointerException e) {
					// Essa exceção não precisa ser tratada, é lançada quando a
					// célula não possui valor
				}
				valoresLinha.add(extrairValorCelula(celula));
			}
		}
		return valoresLinha;

	}

	public static LinkedList<Object> extrairValoresLinha(HSSFRow linha) {
		int quantidadeCelulas = linha.getLastCellNum();
		return extrairValoresLinha(linha, quantidadeCelulas);
	}

	public static Object extrairValorCelula(HSSFCell celula) {
		Object valor = null;
		if (celula != null) {
			switch (celula.getCellType()) {

			case HSSFCell.CELL_TYPE_FORMULA:
				valor = celula.getCellFormula();
				break;

			case HSSFCell.CELL_TYPE_NUMERIC:
				valor = celula.getNumericCellValue();
				break;

			case HSSFCell.CELL_TYPE_STRING:
				valor = celula.getStringCellValue();
				break;

			default:
			}
		}
		return valor;
	}
}