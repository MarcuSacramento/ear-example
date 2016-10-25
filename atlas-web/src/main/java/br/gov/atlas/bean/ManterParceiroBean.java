package br.gov.atlas.bean;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.ejb.EJB;
import javax.ejb.TransactionAttribute;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import br.gov.atlas.component.model.AtlasLazyDataModel;
import br.gov.atlas.entidade.Parceiro;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;
import br.gov.atlas.servico.AtlasServicoImpl;
import br.gov.atlas.servico.ManterParceiroServico;
import br.gov.atlas.util.Valida;

@ManagedBean(name = "manterParceiroBean")
@SessionScoped
public class ManterParceiroBean extends AtlasCrudBean<Parceiro> {

	private static final long serialVersionUID = -3399988042302003631L;

	@EJB(name = "ManterParceiroServico")
	ManterParceiroServico parceiroServico;
	private String termoPesquisa;
	private Parceiro filtro;
	private AtlasLazyDataModel<Parceiro> parceiros;

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
			campoObrigatorio.append(campoObrigatorio.toString().trim().isEmpty() ? "Selecionar imagem" : ", Selecionar imagem");
		}
		if (getEntidade().getHomepage() == null	|| getEntidade().getHomepage().trim().isEmpty()) {
			valido = false;
			campoObrigatorio.append(campoObrigatorio.toString().trim().isEmpty() ? "Site" : ", Site");
		}
		if (getEntidade().getNomeResponsavel() == null || getEntidade().getNomeResponsavel().trim().isEmpty()) {
			valido = false;
			campoObrigatorio.append(campoObrigatorio.toString().trim().isEmpty() ? "Responsável" : ", Responsável");
		}

		if (!valido) {
			error(null, "MSG20", campoObrigatorio.toString());
		}
		
		if (!getEntidade().getEmail().isEmpty()) {
			if (!Valida.validaEmail(getEntidade().getEmail())) {
				error(null, "MSG8");
				valido = false;
			}
		}

		/*if (!getEntidade().getHomepage().isEmpty()) {
			if (!Valida.validaURL(getEntidade().getHomepage())) {
				error(null, "MSG_URL_INVALIDA");
				valido = false;
			}
		}*/

		return valido;
	}

	public void limpar() {
		this.termoPesquisa = null;
	}

	public void limparPesquisar() throws AtlasAcessoBancoDadosException {
		limpar();
		carregarParceiros(null);
	}	
	
	@Override
	protected void efetuarPesquisa() throws AtlasAcessoBancoDadosException {
		parceiros.setListaPaginada(parceiroServico.recuperarParceiros(filtro));
	}

	public void carregarParceiros(String textoParcial)
			throws AtlasAcessoBancoDadosException {
		parceiros = new AtlasLazyDataModel<Parceiro>();
		parceiros.setServico(parceiroServico);
		parceiros.setMetodo("recuperarParceiros");

		filtro = new Parceiro();
		filtro.setNome(textoParcial);
		parceiros.setParametros(filtro);
		pesquisar();
	}

	public void pesquisarTermo() throws AtlasAcessoBancoDadosException {
		if (this.termoPesquisa != null && !this.termoPesquisa.trim().isEmpty()) {
			carregarParceiros("%" + this.termoPesquisa + "%");
		} else {
			carregarParceiros(null);
		}
	}

	public final String novo() {
		setEntidade(new Parceiro());
		return "informarParceiros";
	}

	@Override
	public String salvar() {
		if (validar()) {
			super.salvar();
			return "pesquisarParceiro";
		}
		return null;
	}
	
	@Override
	public String cancelar() {
		limpar();
		return "/admin/conteudo/parceiros/exibirParceiros.faces";
	}

	public void doUpload(FileUploadEvent fileUploadEvent) throws IOException {
		UploadedFile uploadedFile = fileUploadEvent.getFile();
		getEntidade().setByteArquivo(uploadedFile.getContents());
		getEntidade().setUrlImagem(uploadedFile.getFileName());
	}
	
	public StreamedContent getImagemOpen() {
		return new DefaultStreamedContent(new ByteArrayInputStream(getEntidade().getByteArquivo()));	
	}

	@TransactionAttribute
	public StreamedContent getImagem() {
		FacesContext context = FacesContext.getCurrentInstance();
		Parceiro parceiro = null;
        if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            // So, we're rendering the HTML. Return a stub StreamedContent so that it will generate right URL.
            return new DefaultStreamedContent();
        }
        else {
            // So, browser is requesting the image. Return a real StreamedContent with the image bytes.
            String parceiroId = context.getExternalContext().getRequestParameterMap().get("parceiroId");
			try {							
				parceiro = (Parceiro) parceiroServico.pesquisarPorId(Integer.valueOf(parceiroId), Parceiro.class);
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (AtlasAcessoBancoDadosException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (parceiro.getByteArquivo() != null){
				return new DefaultStreamedContent(new ByteArrayInputStream(parceiro.getByteArquivo()));		
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

	public final String editar(Parceiro parceiro) {
		try {
			setEntidade(parceiro);
			initCarregarEntidade();
		} catch (AtlasAcessoBancoDadosException e) {
			error(e);
		}
		return getTelaCadastro();
	}

	@Override
	protected AtlasServicoImpl getServico() {
		return parceiroServico;
	}

	@Override
	protected String getTelaPesquisa() {
		return "parceiroConsulta";
	}

	@Override
	protected String getTelaCadastro() {
		return "informarParceiros";
	}

	public String getTermoPesquisa() {
		return termoPesquisa;
	}

	public void setTermoPesquisa(String termoPesquisa) {
		this.termoPesquisa = termoPesquisa;
	}

	public AtlasLazyDataModel<Parceiro> getParceiros() throws AtlasAcessoBancoDadosException {
		if (parceiros == null) {
			carregarParceiros(null);
		}
		return parceiros;
	}

	public void setParceiros(AtlasLazyDataModel<Parceiro> parceiros) {
		this.parceiros = parceiros;
	}

}
