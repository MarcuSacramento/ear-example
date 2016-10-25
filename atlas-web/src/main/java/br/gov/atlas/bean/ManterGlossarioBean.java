package br.gov.atlas.bean;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import br.gov.atlas.component.model.AtlasLazyDataModel;
import br.gov.atlas.entidade.Assunto;
import br.gov.atlas.entidade.FonteReferencia;
import br.gov.atlas.entidade.TermoGlossario;
import br.gov.atlas.entity.ListaPaginada;
import br.gov.atlas.enums.SituacaoTermoGloassario;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;
import br.gov.atlas.exception.AtlasException;
import br.gov.atlas.exception.AtlasNegocioException;
import br.gov.atlas.servico.AssuntoServico;
import br.gov.atlas.servico.AtlasServicoImpl;
import br.gov.atlas.servico.TermoGlossarioServico;

@ManagedBean(name = "manterGlossarioBean")
@SessionScoped
public class ManterGlossarioBean extends AtlasCrudBean<TermoGlossario> {

	private static final long serialVersionUID = 8547104624905661929L;

	@EJB(name = "assuntoServico")
	private AssuntoServico assuntoServico;

	@EJB(name = "TermoGlossarioServico")
	TermoGlossarioServico glossarioServico;

	private String situacao;
	private Assunto assunto;
	List<Assunto> assuntos;

	private String termoPesquisa;
	private TermoGlossario termoGlossarioFiltro;
	private AtlasLazyDataModel<TermoGlossario> termosGlossario;
	private AtlasLazyDataModel<TermoGlossario> termosGlossarioSelecionados;
	private AtlasLazyDataModel<FonteReferencia> fontesSugestoes;

	@Override
	public Boolean validar() throws AtlasNegocioException {
		boolean valido = true;

		StringBuffer campoObrigatorio = new StringBuffer();

		if (getEntidade().getAssunto() == null) {
			valido = false;
			campoObrigatorio.append(campoObrigatorio.toString().trim().isEmpty() ? "Assunto" : ", Assunto");
		}
		if (getEntidade().getNome() == null	|| getEntidade().getNome().trim().isEmpty()) {
			valido = false;
			campoObrigatorio.append(campoObrigatorio.toString().trim().isEmpty() ? "Termo" : ", Termo");
		}
		if (getEntidade().getDescricao()== null	|| getEntidade().getDescricao().trim().isEmpty()) {
			valido = false;
			campoObrigatorio.append(campoObrigatorio.toString().trim().isEmpty() ? "Definição" : ", Definição");
		}
		if (!valido){
			error(null, "MSG20", campoObrigatorio.toString());
		}

		return valido;
	}

	@Override
	public String salvar() {
		getEntidade().setAssunto(getEntidade().getAssunto());
		for (FonteReferencia fonteReferencia : getEntidade().getFontesReferencia()) {
			fonteReferencia.setTermoGlossario(getEntidade());
		}
		if (getEntidade().getIndicadorSituacao() == null){
			getEntidade().setIndicadorSituacao(SituacaoTermoGloassario.DISPONIVEL.indicadorSituacao());
		}
		getEntidade().setFontesReferencia(getEntidade().getFontesReferencia());
		super.salvar();
		
		return "pesquisarGlossario";
	}



	@Override
	public String cancelar() {
		limpar();
		return "/admin/conteudo/abcDireitos/exibirAbcDireitos.faces";
	}

	@Override
	protected AtlasServicoImpl getServico() {
		return glossarioServico;
	}

	@Override
	public void inicializarObjetos() throws AtlasAcessoBancoDadosException {
		super.inicializarObjetos();
		getEntidade().setFontesReferencia(new ArrayList<FonteReferencia>());
		termosGlossarioSelecionados = new AtlasLazyDataModel<>();
		fontesSugestoes = new AtlasLazyDataModel<>();
	}

	@Override
	protected void efetuarPesquisa() throws AtlasAcessoBancoDadosException {
		termosGlossario.setListaPaginada(glossarioServico.recuperarTermosGlossario(termoGlossarioFiltro));
		if (situacao != null && situacao.equals(SituacaoTermoGloassario.PROPOSTO.indicadorSituacao())){
			efetuarPesquisaPropostos();
		}
	}

	protected void efetuarPesquisaPropostos() throws AtlasAcessoBancoDadosException {
		termosGlossario.setMetodo("recuperarTermosPropostos");
		termosGlossario.setParametros((ArrayList<TermoGlossario>) termosGlossario.getListaPaginada().getResultadoRetorno());
		termosGlossario.setListaPaginada(glossarioServico.recuperarTermosPropostos((ArrayList<TermoGlossario>) termosGlossario.getListaPaginada().getResultadoRetorno()));
	}

	protected void efetuarPesquisaSelecionado() throws AtlasAcessoBancoDadosException {
		termosGlossarioSelecionados.setListaPaginada(glossarioServico.recuperarTermosGlossario(termoGlossarioFiltro));
	}

	public boolean verificarTermoJaCadastrado() throws AtlasAcessoBancoDadosException{
		boolean termoJaCadastrado;
		Integer qtd = new Integer(glossarioServico.pesquisarTermoPorNome(getEntidade().getNome().toUpperCase()));
		termoJaCadastrado = (qtd > 0) ? true : false;
		return termoJaCadastrado;
	}

	public void carregarTermos(String textoParcial) throws AtlasAcessoBancoDadosException {
		termosGlossario = new AtlasLazyDataModel<TermoGlossario>();
		termosGlossario.setServico(glossarioServico);
		termosGlossario.setMetodo("recuperarTermosGlossario");

		termoGlossarioFiltro = new TermoGlossario();
		termoGlossarioFiltro.setNome(textoParcial);
		termoGlossarioFiltro.setAssunto(assunto);
		if (situacao != null && situacao.equals(SituacaoTermoGloassario.PROPOSTO.indicadorSituacao())){
			termoGlossarioFiltro.setIndicadorSituacao(situacao);
		}
		termosGlossario.setParametros(termoGlossarioFiltro);
		pesquisar();
	}

	public void carregarTermosSelecionado(String textoParcial) throws AtlasAcessoBancoDadosException {
		termosGlossarioSelecionados = new AtlasLazyDataModel<TermoGlossario>();
		termosGlossarioSelecionados.setServico(glossarioServico);
		termosGlossarioSelecionados.setMetodo("recuperarTermosGlossario");

		termoGlossarioFiltro = new TermoGlossario();
		termoGlossarioFiltro.setIndicadorSituacao(SituacaoTermoGloassario.PROPOSTO.indicadorSituacao() + "," + SituacaoTermoGloassario.VERIFICADO.indicadorSituacao()); // retorna os propostos e os verificados.
		termoGlossarioFiltro.setIdTermoOrigem(getEntidade().getId());
		termosGlossarioSelecionados.setParametros(termoGlossarioFiltro);
		efetuarPesquisaSelecionado();
	}

	/**
	 * Carrega todas as fontes sugeridas daquele termo.
	 * @throws AtlasAcessoBancoDadosException
	 */
	public void carregarFontesSugestoes() throws AtlasAcessoBancoDadosException {
		fontesSugestoes = new AtlasLazyDataModel<FonteReferencia>();
		fontesSugestoes.setServico(glossarioServico);
		fontesSugestoes.setMetodo("recuperarFontesReferenciaPaginada");
		
		ArrayList<TermoGlossario> resultadoRetorno = (ArrayList<TermoGlossario>) getTermosGlossarioSelecionados().getListaPaginada().getResultadoRetorno();
		if(getEntidade()!=null&&getEntidade().getId()!=null){
			if(resultadoRetorno ==null||resultadoRetorno.size()==0){
				resultadoRetorno = new ArrayList<>();
			}
			resultadoRetorno.add(getEntidade());
		}
		fontesSugestoes.setParametros(resultadoRetorno);
		fontesSugestoes.setListaPaginada(glossarioServico.recuperarFontesReferenciaPaginada(resultadoRetorno));
	}

	public void pesquisarTermo() throws AtlasAcessoBancoDadosException {
		if (this.termoPesquisa != null && !this.termoPesquisa.trim().isEmpty()) {
			carregarTermos("%" + this.termoPesquisa + "%");
		} else {
			carregarTermos(null);
		}
	}

	/**
	 * Atualiza o campo Definição da tela de cadastro de dicionario.
	 * @param descricao
	 */
	public void aplicarDefinicao(String descricao){
		getEntidade().setDescricao(descricao);
	}

	/**
	 * Atualiza o campo Saiba Mais da tela de cadastro de dicionario.
	 * @param saibaMais
	 */
	public void aplicarSaibaMais(String saibaMais){
		getEntidade().setSaibaMais(saibaMais);
	}

	/**
	 * Insere a fonte na lista.
	 * @param fonteReferencia
	 */
	public void aplicarFonte(FonteReferencia fonteReferencia){
		getEntidade().getFontesReferencia().add(fonteReferencia);
	}

	public void adicionar() throws AtlasException {
		FonteReferencia fonte = new FonteReferencia();
		fonte.setTermoGlossario(getEntidade());
		getEntidade().getFontesReferencia().add(fonte);
	}

	public void remover(FonteReferencia fonte) {
		getEntidade().getFontesReferencia().remove(fonte);
	}

	public void excluirFonte(FonteReferencia fonte) throws AtlasAcessoBancoDadosException {
		TermoGlossario entidadeAntiga = getEntidade();
		setEntidade(fonte.getTermoGlossario());
		getEntidade().getFontesReferencia().remove(fonte);
		getEntidade().setAssunto(getEntidade().getAssunto());
		for (FonteReferencia fonteReferencia : getEntidade().getFontesReferencia()) {
			fonteReferencia.setTermoGlossario(getEntidade());
		}
		if (getEntidade().getIndicadorSituacao() == null){
			getEntidade().setIndicadorSituacao(SituacaoTermoGloassario.DISPONIVEL.indicadorSituacao());
		}
		getEntidade().setFontesReferencia(getEntidade().getFontesReferencia());
		try {
			if (getEntidade() != null && validar()) {
				getServico().salvar(getEntidade());
				info("MSG6");
			}
		} catch (AtlasAcessoBancoDadosException | AtlasNegocioException e) {
			error(e);
		}
		setEntidade(entidadeAntiga);
		setFontesSugestoes(null);
		getFontesSugestoes();
	}

	public void verificado(TermoGlossario glossario) throws AtlasAcessoBancoDadosException {
		glossario.setIndicadorSituacao(SituacaoTermoGloassario.VERIFICADO.indicadorSituacao());
		glossario.setFontesReferencia(glossarioServico.recuperarFontesReferencia(glossario));
		setEntidade(glossario);
		salvar();
	}

	public void limpar() {
		this.termoPesquisa = null;
		this.assunto = null;
		this.situacao = null;
		try {
			carregarTermos(null);
		} catch (AtlasAcessoBancoDadosException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void limparPesquisar() throws AtlasAcessoBancoDadosException {
		limpar();
		carregarTermos(null);
	}

	public final String novo() {
		try {
			inicializarObjetos();
		} catch (AtlasAcessoBancoDadosException e) {
			e.printStackTrace();
		}
		return "informarAbcDireitos";
	}

	public final String editar(TermoGlossario termo) {
		try {
			setEntidade(termo);
			initCarregarEntidade();
		} catch (AtlasAcessoBancoDadosException e) {
			error(e);
		}
		return getTelaCadastro();
	}

	@Override
	protected String getTelaPesquisa() {
		return "termoGlossarioConsulta";
	}

	@Override
	protected String getTelaCadastro() {
		return "informarAbcDireitos";
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

	public void setAssunto(Assunto assunto){
		this.assunto = assunto;
	}

	public String getSituacao() {
		return situacao;
	}

	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}

	@SuppressWarnings("unchecked")
	public List<Assunto> getAssuntos() throws AtlasAcessoBancoDadosException {
		return assuntoServico.buscarTodos(Assunto.class,"nome");
	}

	@Override
	public String editar(Integer pk) {
		// antes de editar, limpa a lista de sugestões e fontes da sessão e carrega a lista do termo atual.
		termosGlossarioSelecionados = null;
		fontesSugestoes = null;
		return super.editar(pk);
	}

	public AtlasLazyDataModel<TermoGlossario> getTermosGlossario() throws AtlasAcessoBancoDadosException {
		if (termosGlossario == null) {
			carregarTermos(null);
		}
		return termosGlossario;
	}

	public void setTermosGlossario(AtlasLazyDataModel<TermoGlossario> termosGlossario) {
		this.termosGlossario = termosGlossario;
	}

	public AtlasLazyDataModel<TermoGlossario> getTermosGlossarioSelecionados() throws AtlasAcessoBancoDadosException {
		if (termosGlossarioSelecionados == null) {
			carregarTermosSelecionado(null);
		}
		return termosGlossarioSelecionados;
	}

	public void setTermosGlossarioSelecionado(AtlasLazyDataModel<TermoGlossario> termosGlossarioSelecionados) {
		this.termosGlossarioSelecionados = termosGlossarioSelecionados;
	}

	public AtlasLazyDataModel<FonteReferencia> getFontesSugestoes() throws AtlasAcessoBancoDadosException {
		if (fontesSugestoes == null){
			carregarFontesSugestoes();
		}
		return fontesSugestoes;
	}

	public void setFontesSugestoes(AtlasLazyDataModel<FonteReferencia> fontesSugestoes) {
		this.fontesSugestoes = fontesSugestoes;
	}



}
