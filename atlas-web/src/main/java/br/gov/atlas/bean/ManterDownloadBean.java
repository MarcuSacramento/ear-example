package br.gov.atlas.bean;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import br.gov.atlas.component.model.AtlasLazyDataModel;
import br.gov.atlas.entidade.Assunto;
import br.gov.atlas.entidade.Multimidia;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;
import br.gov.atlas.servico.AtlasServicoImpl;
import br.gov.atlas.servico.ExibirMultimidiaServico;

@ManagedBean(name = "manterDownloadBean")
@SessionScoped
public class ManterDownloadBean extends AtlasCrudBean<Multimidia> {

	private static final long serialVersionUID = 8547104624905661929L;

	@EJB(name = "ExibirMultimidiaServico")
	ExibirMultimidiaServico multimidiaServico;

	private Assunto assunto;
	List<Assunto> assuntos;

	private String termoPesquisa;

	private Multimidia multimidiaFiltro;

	private AtlasLazyDataModel<Multimidia> downloads;

	private UploadedFile file;
	
	@Override
	public Boolean validar() {
		boolean valido = true;
		
		StringBuffer campoObrigatorio = new StringBuffer();
		
		if (getEntidade().getNome() == null	|| getEntidade().getNome().trim().isEmpty()) {
			valido = false;
			campoObrigatorio.append(campoObrigatorio.toString().trim().isEmpty() ? "Título" : ", Título");
		}
		if (getEntidade().getDescricao() == null || getEntidade().getDescricao().trim().isEmpty()) {
			valido = false;
			campoObrigatorio.append(campoObrigatorio.toString().trim().isEmpty() ? "Descrição" : ", Descrição");
		}
		if (getEntidade().getRecurso()== null || getEntidade().getRecurso().trim().isEmpty()) {
			valido = false;
			campoObrigatorio.append(campoObrigatorio.toString().trim().isEmpty() ? "Selecionar arquivo" : ", Selecionar arquivo");
		}
		if (!valido){
			error(null, "MSG20", campoObrigatorio.toString());
		}

		return valido;
	}
	

	public void limpar() {
		this.termoPesquisa = null;
	}

	public void limparPesquisar() throws AtlasAcessoBancoDadosException {
		limpar();
		carregarTermos(null);
	}
	
	public void doUpload(FileUploadEvent fileUploadEvent) throws IOException {
		UploadedFile uploadedFile = fileUploadEvent.getFile();
		getEntidade().setByteArquivo(uploadedFile.getContents());
		getEntidade().setUrl(uploadedFile.getFileName());
		getEntidade().setRecurso(uploadedFile.getFileName());
	}
	
	
	@Override
	public String salvar() {
		if (validar()) {
			getEntidade().setDataPublicacao(new Date());
			getEntidade().setTipoRecurso("T");
			super.salvar();
		}
		return null;
	}
	
	@Override
	public String cancelar() {
		limpar();
		return "/admin/multimidia/downloads/exibirDownloads.faces";
	}

	@Override
	protected AtlasServicoImpl getServico() {
		return multimidiaServico;
	}

	@Override
	protected void efetuarPesquisa() throws AtlasAcessoBancoDadosException {
		downloads.setListaPaginada(multimidiaServico.recuperarMultimidias(multimidiaFiltro, "T"));
	}

	public void carregarTermos(String textoParcial)	throws AtlasAcessoBancoDadosException {
		downloads = new AtlasLazyDataModel<Multimidia>();
		downloads.setServico(multimidiaServico);
		downloads.setMetodo("recuperarMultimidias");

		multimidiaFiltro = new Multimidia();
		multimidiaFiltro.setNome(textoParcial);
		downloads.setParametros(multimidiaFiltro, "T");
		pesquisar();
	}

	public void pesquisarTermo() throws AtlasAcessoBancoDadosException {
		if (this.termoPesquisa != null && !this.termoPesquisa.trim().isEmpty()) {
			carregarTermos("%" + this.termoPesquisa + "%");
		} else {
			carregarTermos(null);
		}
	}

	public final String novo() {
		try {
			inicializarObjetos();
		} catch (AtlasAcessoBancoDadosException e) {
			e.printStackTrace();
		}
		return "informarDownloads";
	}
	
	public final String editar(Multimidia downloads) {
		try {
			setEntidade(downloads);
			initCarregarEntidade();
		} catch (AtlasAcessoBancoDadosException e) {
			error(e);
		}
		return getTelaCadastro();
	}

	@Override
	protected String getTelaPesquisa() {
		return "downloadConsulta";
	}

	@Override
	protected String getTelaCadastro() {
		return "informarDownloads";
	}

	public String getTermoPesquisa() {
		return termoPesquisa;
	}

	public void setTermoPesquisa(String termoPesquisa) {
		this.termoPesquisa = termoPesquisa;
	}

	public Assunto getAssunto() {
		return assunto;
	}

	public void setAssunto(Assunto assunto) {
		this.assunto = assunto;
	}

	public AtlasLazyDataModel<Multimidia> getMultimidias() throws AtlasAcessoBancoDadosException {
		if (downloads == null) {
			carregarTermos(null);
		}
		return downloads;
	}

	public void setMultimidias(AtlasLazyDataModel<Multimidia> multimidias) {
		this.downloads = multimidias;
	}

	public UploadedFile getFile() {
		return file;
	}

	public void setFile(UploadedFile file) {
		this.file = file;
	}

}
