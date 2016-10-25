package br.gov.atlas.bean;

import java.util.Date;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import br.gov.atlas.component.model.AtlasLazyDataModel;
import br.gov.atlas.entidade.Multimidia;
import br.gov.atlas.entidade.Parceiro;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;
import br.gov.atlas.exception.AtlasNegocioException;
import br.gov.atlas.servico.AtlasServicoImpl;
import br.gov.atlas.servico.ExibirMultimidiaServico;

@ManagedBean(name = "manterVideoBean")
@SessionScoped
public class ManterVideoBean extends AtlasCrudBean<Multimidia> {
 
	private static final long serialVersionUID = 8547334624905661929L;

	@EJB(name = "ExibirMultimidiaServico")
	ExibirMultimidiaServico multimidiaServico;

	private String termoPesquisa;

	private Multimidia multimidiaFiltro;

	private AtlasLazyDataModel<Multimidia> videos;

	private final static String TIPO_RECURSO_VIDEO = "V";
	
	public Boolean existeVideoPrincipalDiferente() throws AtlasAcessoBancoDadosException
	{
		Multimidia entidadeAnterior = multimidiaServico.recuperarVideoTelaPrincipal();
		if(entidadeAnterior!=null && !entidadeAnterior.equals(getEntidade())){
			return true;
		} else{
			return false;
		}
	}
	
	@Override
	public String salvar() 
	{
		aplicarCorrecoesNoLink();
		if (validar()) {
			getEntidade().setTipoRecurso(TIPO_RECURSO_VIDEO);
			if (getEntidade().getTelaInicialBoolean()) {
				try {
					Multimidia entidadeAnterior = multimidiaServico.recuperarVideoTelaPrincipal();
					if (entidadeAnterior != null) {
						entidadeAnterior.setTelaInicialBoolean(false);
						getServico().salvar(entidadeAnterior);
						getEntidade().setTelaInicialBoolean(true);
					}
				} catch (AtlasAcessoBancoDadosException | AtlasNegocioException e) {
					e.printStackTrace();
				}
			}

			super.salvar();
		}
		videos = null; // FORÇAR RECARREGAR TODOS OS VÍDEOS NOVAMENTE QUANDO EM
						// 'getVideos()'.
		
		return null;
	}
	
	@Override
	public String cancelar() {
		limpar();
		return "/admin/multimidia/videos/exibirVideos.faces";
	}

	public void limpar() {
		this.termoPesquisa = null;
	}
	
	public void limparPesquisar() throws AtlasAcessoBancoDadosException {
		limpar();
		carregarTermos(null);
	}
	

	private void aplicarCorrecoesNoLink() {
		modificarLinkYoutubeParaEmbeded();
	}

	private void modificarLinkYoutubeParaEmbeded() {
		// ERRADO! --> "http://www.youtube.com/watch?v=7Oh9IucLLyM"
		// CORRETO! --> "http://www.youtube.com/embed/Zvx4QKGYGXw"

		String urlStringOriginal = getEntidade().getUrl();
		String urlStringCorrigida = "";

		if (urlStringOriginal != null && !urlStringOriginal.trim().isEmpty()) {
			urlStringCorrigida = urlStringOriginal.replace("watch?v=", "embed/");

			if (!urlStringOriginal.startsWith("http://") && urlStringOriginal.indexOf(".youtube.") != -1) {
				int lastPositionToReplace = urlStringOriginal.indexOf(".youtube.") + ".youtube.".length();
				urlStringCorrigida = "http://www.youtube." + urlStringCorrigida.substring(lastPositionToReplace);
			}
		}

		getEntidade().setUrl(urlStringCorrigida);
	}

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
		if (getEntidade().getUrl() == null || getEntidade().getUrl().trim().isEmpty()) {
			valido = false;
			campoObrigatorio.append(campoObrigatorio.toString().trim().isEmpty() ? "URL de referência" : ", URL de referência");
		}
		if(!valido){
			error(null, "MSG20", campoObrigatorio.toString());
		}
		
		return valido;
	}
	 
	
	@Override
	protected AtlasServicoImpl getServico() {
		return multimidiaServico;
	}

	@Override
	protected void efetuarPesquisa() throws AtlasAcessoBancoDadosException {
		videos.setListaPaginada(multimidiaServico.recuperarMultimidias(multimidiaFiltro, TIPO_RECURSO_VIDEO));
	}

	public void carregarTermos(String textoParcial) throws AtlasAcessoBancoDadosException {
		videos = new AtlasLazyDataModel<Multimidia>();
		videos.setServico(multimidiaServico);
		videos.setMetodo("recuperarMultimidias");

		multimidiaFiltro = new Multimidia();
		multimidiaFiltro.setNome(textoParcial);
		videos.setParametros(multimidiaFiltro, TIPO_RECURSO_VIDEO);

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
		return "informarVideos";
	}
	
	public final String editar(Multimidia multimidia) {
		try {
			setEntidade(multimidia);
			initCarregarEntidade();
		} catch (AtlasAcessoBancoDadosException e) {
			error(e);
		}
		return getTelaCadastro();
	}
	
	@Override
	public void inicializarObjetos() throws AtlasAcessoBancoDadosException {
		super.inicializarObjetos();
		Multimidia video = new Multimidia();
		video.setDataPublicacao( new Date() ); // DATA DE HOJE, por padrão
		setEntidade(video);
	}

	@Override
	protected String getTelaPesquisa() {
		return "videoConsulta";
	}

	@Override
	protected String getTelaCadastro() {
		return "informarVideos";
	}

	public String getTermoPesquisa() {
		return termoPesquisa;
	}

	public void setTermoPesquisa(String termoPesquisa) {
		this.termoPesquisa = termoPesquisa;
	}

	public AtlasLazyDataModel<Multimidia> getVideos() throws AtlasAcessoBancoDadosException {
		if (videos == null) {
			carregarTermos(null);
		}
		return videos;
	}

	public void setVideos(AtlasLazyDataModel<Multimidia> videos) {
		this.videos = videos;
	}

}
