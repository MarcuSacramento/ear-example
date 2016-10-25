package br.gov.atlas.bean;

import java.sql.Date;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import br.gov.atlas.entidade.FonteReferencia;
import br.gov.atlas.entidade.TermoGlossario;
import br.gov.atlas.entity.LetraGlossario;
import br.gov.atlas.entity.LinksPaginacao;
import br.gov.atlas.entity.TermoGlossarioLinhaComTresRegistrosAdapter;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;
import br.gov.atlas.exception.AtlasException;
import br.gov.atlas.exception.AtlasNegocioException;
import br.gov.atlas.servico.AtlasServicoImpl;
import br.gov.atlas.servico.ExibirGlossarioServico;

@ManagedBean(name = "exibirGlossarioBean")
@SessionScoped
public class ExibirGlossarioBean extends AtlasCrudBean<TermoGlossario> {

	private static final long serialVersionUID = -5250866836573228668L;

	@EJB(name = "exibirGlossarioServico")
	ExibirGlossarioServico exibirGlossarioServico;

	protected Integer currentPage = 1;
	private String termoPesquisa;
	private String origemAcao = "";
	private String letraSelecionada = "A";

	private TermoGlossario termoFiltro;
	private List<TermoGlossarioLinhaComTresRegistrosAdapter> termosComLinhasDeTresRegistros;


	public void init() throws AtlasAcessoBancoDadosException {

		/* Esta verificação foi colocada para resolver o bug que ocorre no navegador Firefox.
		 * Somente no Firefox, o método init é acionado duas vezes a cada requisição. */
		ExternalContext ectx = FacesContext.getCurrentInstance().getExternalContext();
		HttpServletRequest req = (HttpServletRequest) ectx.getRequest();
		FacesContext context = FacesContext.getCurrentInstance();
		context.renderResponse();

		/* Na segunda requisição o método init não é totalmente executado. */
		if(req.getParameter("javax.faces.ViewState")==null){
			return;
		}

		if(origemAcao.equals("L")){

			efetuarPesquisaPorPagina();
			this.termoPesquisa = "";
			this.origemAcao = "";

		}else if (origemAcao.equals("P")){

			if(this.termoPesquisa != null && !this.termoPesquisa.trim().isEmpty()) {
				termoPesquisa = Normalizer.normalize(termoPesquisa, Normalizer.Form.NFD);
				termoPesquisa = termoPesquisa.replaceAll("[^\\p{ASCII}]", "");
				termoPesquisa = termoPesquisa.toUpperCase();
				termoFiltro = new TermoGlossario();
				termoFiltro.setNome("%"+termoPesquisa+"%");
				efetuarPesquisaPorPagina();
				this.origemAcao = "";
				this.letraSelecionada = "";
			}

		}else {

			this.termoFiltro = new TermoGlossario();
			this.termoFiltro.setNome("A%");
			efetuarPesquisaPorPagina();
			this.origemAcao = "";
			this.termoPesquisa = "";
			this.letraSelecionada = "A";
		}
	}

	@Override
	public Boolean validar() throws AtlasNegocioException {
		return true;
	}
	@Override
	protected AtlasServicoImpl getServico() {
		return exibirGlossarioServico;
	}
	@Override
	protected String getTelaPesquisa() {
		return null;
	}
	@Override
	protected String getTelaCadastro() {
		return null;
	}
	protected String getTelaDetalhe() {
		return "termoGlossarioDetalhe";
	}

	@Override
	protected void efetuarPesquisa() throws AtlasAcessoBancoDadosException {
		setCurrentPage(currentPage);
		efetuarPesquisaPorPagina();
	}

	public void pesquisarTermo(String letra) throws AtlasAcessoBancoDadosException {
		origemAcao = "L";
		letraSelecionada = letra;
		termoPesquisa = letra;
		termoFiltro = new TermoGlossario();
		termoFiltro.setNome(letra+"%");
	}

	public void pesquisarTermo() throws AtlasAcessoBancoDadosException {
		origemAcao = "P";
	}

	public void efetuarPesquisaPorPagina() throws AtlasAcessoBancoDadosException{
		this.termosComLinhasDeTresRegistros = exibirGlossarioServico.recuperarTermosComLinhasDeTresRegistros(termoFiltro, currentPage);
	}

	public Integer getQtdRegistros(){
		if (termosComLinhasDeTresRegistros == null || termosComLinhasDeTresRegistros.isEmpty()){
			return 0;
		}

		TermoGlossarioLinhaComTresRegistrosAdapter ultimo = getTermosComLinhasDeTresRegistros().get(getTermosComLinhasDeTresRegistros().size()-1);

		if (ultimo.getRegistroCentro().getId() == null){
			return (termosComLinhasDeTresRegistros.size() * 3) - 2;
		} else if (ultimo.getRegistroDireita().getId() == null){
			return (termosComLinhasDeTresRegistros.size() * 3) - 1;
		}
		return (termosComLinhasDeTresRegistros.size() * 3);
	}

	public List<LetraGlossario> montarAlfabeto(){
		List<LetraGlossario> alfabeto = new ArrayList<LetraGlossario>();

		for(char ch = 'A'; ch <= 'Z'; ch++){
			String letraAtual = Character.toString(ch);
			String nomeClasseCss = letraAtual.equals(letraSelecionada) ? "atlas-abas-menu-lista-ativa" : "";
			alfabeto.add(new LetraGlossario(letraAtual, nomeClasseCss) );
		}
		return alfabeto;
	}

	public String detalharTermo(Integer pk) {
		try {
			setEntidade((TermoGlossario)getServico().pesquisarPorId(pk, getPersistentClass()));
			inicializarFontesReferencia();
		} catch (AtlasAcessoBancoDadosException e) {
			error(e);
		}
		return "termoGlossario";
	}

	private void inicializarFontesReferencia() throws AtlasAcessoBancoDadosException {
		TermoGlossario termo = getEntidade();
		List<FonteReferencia> fontes = exibirGlossarioServico.recuperarFontesReferencia(termo);
		termo.setFontesReferencia(fontes);
	}

	public Integer recuperarTotalDePaginas() throws AtlasAcessoBancoDadosException {
		return exibirGlossarioServico.recuperarTotalDePaginas(termoFiltro);
	}

	public List<TermoGlossarioLinhaComTresRegistrosAdapter> getTermosComLinhasDeTresRegistros() {
		return termosComLinhasDeTresRegistros;
	}

	public void setTermosComLinhasDeTresRegistros(List<TermoGlossarioLinhaComTresRegistrosAdapter> termosComLinhasDeTresRegistros) {
		this.termosComLinhasDeTresRegistros = termosComLinhasDeTresRegistros;
	}

	public String getTermoPesquisa() {
		return termoPesquisa;
	}

	public void setTermoPesquisa(String termoPesquisa) {
		this.termoPesquisa = termoPesquisa;
	}

	public TermoGlossario getTermoFiltro() {
		return termoFiltro;
	}

	public void setTermoFiltro(TermoGlossario termoFiltro) {
		this.termoFiltro = termoFiltro;
	}

	public String getOrigemAcao() {
		return origemAcao;
	}

	public void setOrigemAcao(String origemAcao) {
		this.origemAcao = origemAcao;
	}

	public String getLetraSelecionada() {
		return letraSelecionada;
	}

	public void setLetraSelecionada(String letraSelecionada) {
		this.letraSelecionada = letraSelecionada;
	}


	/*PAGINAÇÃO*/
	public void configurarPaginaAtual(Integer currentPage) {
		this.currentPage = currentPage;
	}

	public Integer getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(Integer currentPage) {
		this.currentPage = currentPage;
	}

	public void setCurrentPage(String currentPage) {
		this.currentPage = Integer.valueOf(currentPage);
	}

	public void goToNextPage() {
		this.currentPage++;
	}

	public void goToPreviousPage() {
		this.currentPage--;
	}

	public void goToFirstPage() {
		this.currentPage = 1;
	}

	public void goToLastPage() throws AtlasAcessoBancoDadosException {
		this.currentPage = recuperarTotalDePaginas();
	}

	public String getNomeStyleClassPrimeiraPagina() {
		if (getCurrentPage() != 1) {
			return "atlas_icone_paginacao_itens_anteriores_ativo";
		} else {
			return "atlas_icone_paginacao_itens_anteriores_inativo";
		}
	}

	public String getNomeStyleClassUltimaPagina()
			throws AtlasAcessoBancoDadosException {
		if (getCurrentPage() != recuperarTotalDePaginas()) {
			return "atlas_icone_paginacao_itens_proximos_ativo";
		} else {
			return "atlas_icone_paginacao_itens_proximos_inativo";
		}
	}

	public String getNomeStyleClassProximaPagina()
			throws AtlasAcessoBancoDadosException {
		if (getCurrentPage() != recuperarTotalDePaginas()) {
			return "atlas_icone_paginacao_item_proximo_ativo";
		} else {
			return "atlas_icone_paginacao_item_proximo_inativo";
		}
	}

	public String getNomeStyleClassPaginaAnterior() {
		if (getCurrentPage() != 1) {
			return "atlas-paginacao-link-atual";
		} else {
			return "atlas-paginacao-link-ativo";
		}
	}

	public boolean isPrimeiraPaginaSelecionada() {
		return getCurrentPage() == 1;
	}

	public boolean isUltimaPaginaSelecionada()
			throws AtlasAcessoBancoDadosException {
		return getCurrentPage() == recuperarTotalDePaginas();
	}

	public List<LinksPaginacao> getListaDeLinksDasPaginas()
			throws AtlasAcessoBancoDadosException {
		List<LinksPaginacao> listaDeLinksDasPaginas = new ArrayList<>();
		Integer totalDePaginas = recuperarTotalDePaginas();

		for (int paginaAtual = 1; paginaAtual <= totalDePaginas; paginaAtual++) {
			String paginaAtualAsString = String.valueOf(paginaAtual);
			String nomeClasseCss = (paginaAtual == getCurrentPage()) ? "atlas-paginacao-link-ativo" : "atlas_paginacao_link";
			listaDeLinksDasPaginas.add(new LinksPaginacao(paginaAtualAsString, nomeClasseCss));
		}

		return listaDeLinksDasPaginas;
	}

	public void remover(FonteReferencia fonte) {
		getEntidade().getFontesReferencia().remove(fonte);
	}

	public void adicionarNovo() throws AtlasException {
		FonteReferencia fonte = new FonteReferencia();
		fonte.setTermoGlossario(getEntidade());
		getEntidade().getFontesReferencia().add(fonte);
	}

	/**
	 * Salva o termo novo.
	 */
	public void enviarSugestao() {
		// Pega os dados do antigo glossario e passa para o novo.
		TermoGlossario termoGlossarioNovo = new TermoGlossario();
		termoGlossarioNovo.setAssunto(getEntidade().getAssunto());
		termoGlossarioNovo.setNome(getEntidade().getNome());
		termoGlossarioNovo.setDataInclusao(new Date(System.currentTimeMillis()));
		termoGlossarioNovo.setDescricao(getEntidade().getDescricao());
		termoGlossarioNovo.setSaibaMais(getEntidade().getSaibaMais());
		termoGlossarioNovo.setIndicadorSituacao("P");
		List<FonteReferencia> fontesReferenia = new ArrayList<FonteReferencia>();
		// insere as novas referencias.
		for (FonteReferencia fonteReferencia : getEntidade().getFontesReferencia()) {
			FonteReferencia fonteRefereniaNova = new FonteReferencia();
			fonteRefereniaNova.setLegislacao(fonteReferencia.getLegislacao());
			fonteRefereniaNova.setNome(fonteReferencia.getNome());
			fonteRefereniaNova.setTermoGlossario(termoGlossarioNovo);
			fonteRefereniaNova.setURL(fonteReferencia.getURL());
			fontesReferenia.add(fonteRefereniaNova);
		}

		termoGlossarioNovo.setIdTermoOrigem(getEntidade().getId()); // id do termo de origem/pai
		termoGlossarioNovo.setFontesReferencia(fontesReferenia);
		setEntidade(termoGlossarioNovo);
		super.salvar();
		info("MSG32");
	}

}
