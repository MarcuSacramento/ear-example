package br.gov.atlas.bean;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import br.gov.atlas.component.model.AtlasLazyDataModel;
import br.gov.atlas.entidade.Projeto;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;
import br.gov.atlas.servico.AtlasServicoImpl;
import br.gov.atlas.servico.ManterProjetoServico;
import br.gov.atlas.util.Valida;

@ManagedBean(name = "manterProjetoBean")
@SessionScoped
public class ManterProjetoBean extends AtlasCrudBean<Projeto>{

	private static final long serialVersionUID = -3399988042302003631L;
	
	@EJB(name = "ManterProjetoServico")
	ManterProjetoServico projetoServico;
	private String termoPesquisa;
	private Projeto filtro;
	private AtlasLazyDataModel<Projeto> projetos;

	@Override
	public Boolean validar() {
		boolean valido = true;
		StringBuffer campoObrigatorio = new StringBuffer();
		
		if (getEntidade().getNome() == null	|| getEntidade().getNome().trim().isEmpty()) {
			valido = false;
			campoObrigatorio.append(campoObrigatorio.toString().trim().isEmpty() ? "Nome" : ", Nome");
		}
		if (getEntidade().getUrlImagem() == null || getEntidade().getUrlImagem().trim().isEmpty()) {
			valido = false;
			campoObrigatorio.append(campoObrigatorio.toString().trim().isEmpty() ? "Marca do Projeto" : ", Marca do Projeto");
		}
		if (getEntidade().getHomepage() == null	|| getEntidade().getHomepage().trim().isEmpty()) {
			valido = false;
			campoObrigatorio.append(campoObrigatorio.toString().trim().isEmpty() ? "Site" : ", Site");
		}
		if (!valido) {
			error(null, "MSG20", campoObrigatorio.toString());
		}
		
		if (!getEntidade().getHomepage().isEmpty()) {
			if (!Valida.validaURL(getEntidade().getHomepage())) {
				error(null, "MSG_URL_INVALIDA");
				valido = false;
			}
		}
		
		return valido; 
	}
	
	public void limpar() {
		this.termoPesquisa = null;
	}

	public void limparPesquisar() throws AtlasAcessoBancoDadosException {
		limpar();
		carregarProjetos(null);
	}
	
	@Override
	protected void efetuarPesquisa() throws AtlasAcessoBancoDadosException {
		projetos.setListaPaginada(projetoServico.recuperarProjetos(filtro));
	}
	
	public void carregarProjetos(String textoParcial) throws AtlasAcessoBancoDadosException {
		projetos = new AtlasLazyDataModel<Projeto>();
		projetos.setServico(projetoServico);
		projetos.setMetodo("recuperarProjetos");

		filtro = new Projeto();
		filtro.setNome(textoParcial);
		projetos.setParametros(filtro);
		pesquisar();
	}

	public void pesquisarTermo() throws AtlasAcessoBancoDadosException {
		if (this.termoPesquisa!=null && !this.termoPesquisa.trim().isEmpty()){
			carregarProjetos("%" + this.termoPesquisa + "%");
		} else {
			carregarProjetos(null);
		}
	}
	
	public final String novo() {
		setEntidade(new Projeto());
		return "informarProjetos";
	}
	
	@Override
	public String cancelar() {
		limpar();
		return "/admin/conteudo/projetos/exibirProjetos.faces";
	}
	
	@Override
	public String salvar() {
		if (validar()) {
			super.salvar();
			return "pesquisarProjeto";
		}
		return null;
	}
	
	public void doUpload(FileUploadEvent fileUploadEvent) throws IOException {
		UploadedFile uploadedFile = fileUploadEvent.getFile();
		getEntidade().setByteArquivo(uploadedFile.getContents());
		getEntidade().setUrlImagem(uploadedFile.getFileName());
	}
	
	public StreamedContent getImagemOpen() {
		return new DefaultStreamedContent(new ByteArrayInputStream(getEntidade().getByteArquivo()));	
	}
	
	public StreamedContent getImagem() {
		FacesContext context = FacesContext.getCurrentInstance();
		Projeto projeto = null;
        if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            // So, we're rendering the HTML. Return a stub StreamedContent so that it will generate right URL.
            return new DefaultStreamedContent();
        }
        else {
            // So, browser is requesting the image. Return a real StreamedContent with the image bytes.
            String projetoId = context.getExternalContext().getRequestParameterMap().get("projetoId");
			try {							
				projeto = (Projeto) projetoServico.pesquisarPorId(Integer.valueOf(projetoId), Projeto.class);
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (AtlasAcessoBancoDadosException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (projeto.getByteArquivo() != null){
				return new DefaultStreamedContent(new ByteArrayInputStream(projeto.getByteArquivo()));		
			}else{
				FileInputStream fis = null;
				try {
					fis = new FileInputStream(FacesContext.getCurrentInstance().getExternalContext().getRealPath("/pub/_imagens/semImagem.jpg"));
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return new DefaultStreamedContent(fis);
			}
        }
	}
	
	public final String editar(Projeto projeto) {
		try {
			setEntidade(projeto);
			initCarregarEntidade();
		} catch (AtlasAcessoBancoDadosException e) {
			error(e);
		}
		return getTelaCadastro();
	}
	
	@Override
	protected AtlasServicoImpl getServico() {
		return projetoServico;
	}
	
	@Override
	protected String getTelaPesquisa() {
		return "projetoConsulta";
	}
	
	@Override
	protected String getTelaCadastro() {
		return "informarProjetos";
	}
	
	public String getTermoPesquisa() {
		return termoPesquisa;
	}
	public void setTermoPesquisa(String termoPesquisa) {
		this.termoPesquisa = termoPesquisa;
	}
	
	public AtlasLazyDataModel<Projeto> getProjetos() throws AtlasAcessoBancoDadosException {
		if (projetos == null) {
			carregarProjetos(null);
		}
		return projetos;
	}
	public void setProjetos(AtlasLazyDataModel<Projeto> projetos) {
		this.projetos = projetos;
	}

}
