package br.gov.atlas.bean;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.servlet.http.HttpSession;

import org.omnifaces.util.Faces;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import br.gov.atlas.entidade.Multimidia;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;
import br.gov.atlas.exception.AtlasNegocioException;
import br.gov.atlas.servico.AtlasServicoImpl;
import br.gov.atlas.servico.ExibirMultimidiaServico;

@SuppressWarnings("rawtypes")
@ManagedBean(name = "exibirDownloadBean")
@javax.faces.bean.ViewScoped
public class ExibirDownloadBean extends AtlasPaginatedBean {

	private static final long serialVersionUID = 1L;

	@EJB(name = "ExibirMultimidiaServico")
	ExibirMultimidiaServico exibirMultimidiaServico;

	private List<Multimidia> multimidias;

	public void init() {
		pesquisar();
	}

	public void download(String url) throws IOException{
		File file = new File(url);
		Faces.sendFile(file, true);
	}

	@Override
	public Integer recuperarTotalDePaginas()
			throws AtlasAcessoBancoDadosException {
		return exibirMultimidiaServico.recuperarTotalDePaginasTexto();
	}

	@Override
	protected void efetuarPesquisa() throws AtlasAcessoBancoDadosException {
		efetuarPesquisaPorPagina(getCurrentPage());
	}

	public void efetuarPesquisaPorPagina(Integer page) throws AtlasAcessoBancoDadosException {
		this.multimidias = exibirMultimidiaServico.recuperarMultimidiasTexto(page);
	}

	@Override
	protected AtlasServicoImpl getServico() {
		return exibirMultimidiaServico;
	}

	@Override
	protected String getTelaPesquisa() {
		return "abcDosDireitosConsulta";
	}

	@Override
	protected String getTelaCadastro() {
		return null;
	}

	public String recuperarCaminhoImagem(Multimidia multimidia) {
		String result = "";
		if (multimidia != null) {
			if (multimidia.getUrl() != null) {
				if (multimidia.getUrl().toLowerCase().endsWith(".pdf")) {
					result = "/pub/_imagens/icones/icon_pdf.png";
				} else if (multimidia.getUrl().toLowerCase().endsWith(".doc")
						|| multimidia.getUrl().toLowerCase().endsWith(".docx")) {
					result = "/pub/_imagens/icones/icon_word.png";
				} else if (multimidia.getUrl().toLowerCase().endsWith(".xls")
						|| multimidia.getUrl().toLowerCase().endsWith(".xlsx")) {
					result = "/pub/_imagens/icones/icon_excel.png";
				}
			}
		}
		return result;
	}

	public String recuperarTipoArquivo(Multimidia multimidia) {
		String result = "";
		if (multimidia.getUrl() != null) {
			if (multimidia.getUrl().toLowerCase().endsWith(".pdf")) {
				result = "PDF";
			} else if (multimidia.getUrl().toLowerCase().endsWith(".doc")
					|| multimidia.getUrl().toLowerCase().endsWith(".docx")) {
				result = "Word";
			} else if (multimidia.getUrl().toLowerCase().endsWith(".xls")
					|| multimidia.getUrl().toLowerCase().endsWith(".xlsx")) {
				result = "Excel";
			}
		}
		return result;
	}

	public StreamedContent getFile(Integer id) {
		Multimidia multimidia = null;
		try {
			multimidia = (Multimidia) exibirMultimidiaServico.pesquisarPorId(id, Multimidia.class);
		} catch (AtlasAcessoBancoDadosException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DefaultStreamedContent streamedContent = null;
		if (multimidia.getByteArquivo() != null) {
			streamedContent = new DefaultStreamedContent(new ByteArrayInputStream(multimidia.getByteArquivo()),
					"application/pdf", multimidia.getUrl());
		}
		return streamedContent;
	}

	public List<Multimidia> getMultimidias() {
		return multimidias;
	}

	public void setMultimidias(List<Multimidia> multimidias) {
		this.multimidias = multimidias;
	}

	@Override
	public Boolean validar() throws AtlasNegocioException {
		return null;
	}
}
