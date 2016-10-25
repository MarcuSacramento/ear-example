package br.gov.atlas.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public final class ArquivoUtil {

	private static final String PROPRIEDADE_PASTA_TEMPORARIA_SISTEMA = "java.io.tmpdir";
	private static final String NOME_PASTA_TEMPORARIA_ATLAS = "atlasacessojustica";
	private static final String PADRAO_DATA_PASTA_TEMPORARIA = "YYYYMMddkkmmss";

	public static final String MSG_TIPO_ARQUIVO_INVALIDO = "TIPO_ARQUIVO_INVALIDO";
	public static final String MSG_TAMANHO_ARQUIVO_INVALIDO = "TAMANHO_ARQUIVO_ULTRAPASSA_LIMITE";

	private ArquivoUtil() {
	};

	private static String obterPastaTemporariaSistema() {
		return System.getProperty(PROPRIEDADE_PASTA_TEMPORARIA_SISTEMA);
	}

	public static File obterPastaTemporariaAtlas() {
		String caminhoPastaAtlas = obterPastaTemporariaSistema().concat(
				File.separator).concat(NOME_PASTA_TEMPORARIA_ATLAS);
		File pastaAtlas = new File(caminhoPastaAtlas);
		if (!pastaAtlas.exists()) {
			pastaAtlas.mkdir();
		}
		return pastaAtlas;
	}

	public static File obterPastaTemporariaAtlasComHorario() {
		SimpleDateFormat df = new SimpleDateFormat(PADRAO_DATA_PASTA_TEMPORARIA);
		File pastaDataAtual = new File(obterPastaTemporariaAtlas(),
				df.format(new Date()));
		if (!pastaDataAtual.exists()) {
			pastaDataAtual.mkdir();
		}
		return pastaDataAtual;
	}

	public static TipoArquivo getTipoArquivo(String nomeArquivo) {

		String ext = nomeArquivo.substring(nomeArquivo.lastIndexOf('.') + 1);
		return getExtensaoPorNome(ext);
	}

	public static boolean isTipoValido(TipoArquivo extensaoArquivo,
			List<TipoArquivo> extensoesValidas) {
		boolean valido = false;
		if (extensaoArquivo != null && extensoesValidas != null
				&& !extensoesValidas.isEmpty()) {
			valido = extensoesValidas.contains(extensaoArquivo);
		}
		return valido;
	}

	public static boolean isTamanhoValido(Long tamanhoArquivo,
			Long tamanhoMaximo) {
		return tamanhoArquivo <= tamanhoMaximo;
	}

	public static TipoArquivo getExtensaoPorNome(String ext) {
		TipoArquivo[] extensoes = TipoArquivo.values();
		TipoArquivo e = null;
		for (TipoArquivo extensaoArquivo : extensoes) {
			if (extensaoArquivo.getTipo().equalsIgnoreCase(ext)) {
				e = extensaoArquivo;
				break;
			}
		}
		return e;
	}

	public static File compactarArquivo(String nomeArquivo, File arquivo)
			throws IOException {

		return compactarArquivo(nomeArquivo, new FileInputStream(arquivo));
	}

	public static File compactarArquivo(String nomeArquivo, InputStream is)
			throws IOException {

		File arquivoZip = new File(obterPastaTemporariaAtlasComHorario(),
				nomeArquivo.substring(0, nomeArquivo.length() - 4).concat(
						".zip"));
		arquivoZip.createNewFile();

		FileOutputStream fos = new FileOutputStream(arquivoZip);

		ZipOutputStream zos = new ZipOutputStream(fos);
		ZipEntry ze = new ZipEntry(nomeArquivo);
		zos.putNextEntry(ze);

		byte[] buffer = new byte[1024];
		int len;
		while ((len = is.read(buffer)) > 0) {
			zos.write(buffer, 0, len);
		}
		zos.closeEntry();
		zos.close();
		is.close();
		fos.close();
		return arquivoZip;
	}

	public static File criarArquivo(String nomeArquivo, byte[] bytes)
			throws IOException {
		File file = new File(obterPastaTemporariaAtlasComHorario(), nomeArquivo);

		FileOutputStream outFile = new FileOutputStream(file);
		outFile.write(bytes);
		outFile.close();
		return file;

	}

}
