package br.gov.atlas.bean;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.mail.MessagingException;

import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.ToggleSelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.event.data.PageEvent;
import org.primefaces.model.UploadedFile;

import br.gov.atlas.component.model.AtlasLazyDataModel;
import br.gov.atlas.dto.OrgaoDTO;
import br.gov.atlas.entidade.Anexo;
import br.gov.atlas.entidade.Mensageria;
import br.gov.atlas.entidade.MensageriaOrgao;
import br.gov.atlas.entidade.Municipio;
import br.gov.atlas.entidade.Orgao;
import br.gov.atlas.entidade.Representante;
import br.gov.atlas.entidade.TipoOrgao;
import br.gov.atlas.entidade.UF;
import br.gov.atlas.enums.AmbitoAtuacaoOrgao;
import br.gov.atlas.enums.MensagemTag;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;
import br.gov.atlas.exception.AtlasNegocioException;
import br.gov.atlas.servico.AnexoServico;
import br.gov.atlas.servico.AtlasServicoImpl;
import br.gov.atlas.servico.ManterMensageriaServico;
import br.gov.atlas.servico.MunicipioServico;
import br.gov.atlas.servico.OrgaoServico;
import br.gov.atlas.servico.RepresentanteServico;

@ManagedBean(name = "manterMensageriaBean")
@SessionScoped
public class ManterMensageriaBean extends AtlasCrudBean<Mensageria> {


	private static final long serialVersionUID = 5594149656040358135L;

	@EJB(name = "ManterMensageriaServico")
	private ManterMensageriaServico mensageriaServico;

	@EJB(name = "MunicipioServico")
	private MunicipioServico municipioServico;

	@EJB(name = "OrgaoServico")
	private OrgaoServico orgaoServico;

	@EJB(name = "RepresentanteServico")
	private RepresentanteServico representanteServico;

	@EJB(name = "AnexoServico")
	private AnexoServico anexoServico;

	private AtlasLazyDataModel<Mensageria> mensagens;

	private Mensageria mensagemFiltro;
	private String nomeDestinatario;
	private Orgao destinatarios;
	private List<MensageriaOrgao> emailDestinatario;
	private boolean enderecoAlterado;
	private String selectedTag;
	private List<Anexo> files;
	private int qtdMaximaAnexos = 3;

	// Atributos de controle de seleção de destinatários
	private Map<Integer, List<Orgao>> destinatariosSelecaoPorPagina;
	private AtlasLazyDataModel<Orgao> destinatariosPesquisa;
	private OrgaoDTO orgaoFiltro;
	private List<Orgao> destinatariosSelecionados;
	private List<Orgao> destinatariosSelecionadosGeral;
	private Integer numPagina = 0;
	private Integer numPaginaAnterior = 0;

	// Atributos de filtro de pesquisa de destinatários
	private String nomeOrgaoPesquisa;
	private Municipio municipioCadastro;
	private TipoOrgao tipoOrgao;
	private UF ufCadastro;
	private AmbitoAtuacaoOrgao ambitoAtuacaoOrgao;
	private List<AmbitoAtuacaoOrgao> listaAmbitoAtuacao;

	// Filtros de data
	private Date filtroDataInicio;
	private Date filtroDataFim;

	public void init() throws AtlasAcessoBancoDadosException{
		this.setFiltro(new Mensageria());
		carregarMensagens(getFiltroDataInicio(), getFiltroDataFim());
	}

	private void carregarMensagens(Date filtroDataInicio, Date filtroDataFim) {
		mensagens = new AtlasLazyDataModel<Mensageria>();
		mensagens.setServico(mensageriaServico);
		mensagens.setMetodo("recuperarMensagens");
		mensagemFiltro = new Mensageria();
		mensagemFiltro.setDataInicio(filtroDataInicio);
		mensagemFiltro.setDataFim(filtroDataFim);
		mensagens.setParametros(mensagemFiltro);
		pesquisar();
	}

	@Override
	protected void efetuarPesquisa() throws AtlasAcessoBancoDadosException {
		mensagens.setListaPaginada(mensageriaServico.recuperarMensagens(mensagemFiltro));
	}

	@Override
	public void inicializarObjetos() throws AtlasAcessoBancoDadosException {
		super.inicializarObjetos();
	}

	public void pesquisarMensagens() throws AtlasAcessoBancoDadosException {
		if(getFiltroDataInicio() != null && getFiltroDataFim() != null){
			if(getFiltroDataFim().before(getFiltroDataInicio())){
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "ATENÇÃO - A DATA DE INÍCIO DEVE SER MENOR QUE A DATA FINAL!", null));
				return;
			}
		}
		carregarMensagens(getFiltroDataInicio(), getFiltroDataFim());
	}

	private void limparCampos() {
		getEntidade().setRemetente("");
		getEntidade().setAssunto("");
		getEntidade().setMensagem("");
		files = null;
		this.destinatarios = new Orgao();
		this.destinatariosSelecionadosGeral = null;
	}

	public String novaMensagem(){
		super.novoRegistro();
		limparCampos();
		StringBuffer sb = new StringBuffer("Prezad["+MensagemTag.DESINENCIA_REPRESENTANTE+"] ");
		sb.append("["+MensagemTag.PRONOME_REPRESENTANTE+"] ");
		sb.append("["+MensagemTag.CARGO_REPRESENTANTE+"] ");
		sb.append("["+MensagemTag.NOME_REPRESENTANTE+"] <br /><br />");
		sb.append("Levo a conhecimento do ["+MensagemTag.DESINENCIA+"] ["+MensagemTag.NOME_ORGAO+"],");
		sb.append(" que em nosso cadastro seus dados de contato atualmente são: <br />");
		sb.append("- Endereço: ["+MensagemTag.ENDERECO_ORGAO+"] <br />");
		sb.append("- Telefone: ["+MensagemTag.TELEFONES_ORGAO+"] <br />");
		sb.append("- E-mail instituicional: ["+MensagemTag.EMAIL_ORGAO+"] <br /><br />");
		sb.append("Att., <br />");
		sb.append("Secretaria Nacional do Consumidor <br />");
		sb.append("Esplanada dos Ministérios, Bloco T, Ministério da Justiça, 5º andar, sala 538, Brasília/DF, CEP: 70064-900 <br />");
		sb.append("Telefone: (61) 2025-3112 <br />");
		sb.append("http://www.mj.gov.br/senacon");
		getEntidade().setMensagem(sb.toString());
		return getTelaCadastro();
	}

	public String editarMensagem(Mensageria mensageria) throws AtlasAcessoBancoDadosException{
		limparCampos();
		setEntidade(mensageria);
		recuperarAnexos(mensageria);
		recuperarDestinatarios(mensageria);
		return getTelaCadastro();
	}

	private void recuperarDestinatarios(Mensageria mensageria) throws AtlasAcessoBancoDadosException {
		List<MensageriaOrgao> mensagensOrgaos = mensageria.getMensagensOrgaos();
		if(mensagensOrgaos == null || mensagensOrgaos.isEmpty()){
			destinatariosSelecionadosGeral = representanteServico.recuperarDestinatarios(mensageria);
		}
	}

	private void recuperarAnexos(Mensageria mensageria) throws AtlasAcessoBancoDadosException {
		if(mensageria.getAnexo() == null || mensageria.getAnexo().isEmpty()){
			files = anexoServico.recuperarAnexosMensagem(mensageria);
		}
	}

	public List<String> getTagsGerais() {
		return MensagemTag.getTags();
	}

	public void removerArquivo(Anexo file){
		if(file != null){
			files.remove(file);
		}
	}

	public void onRowSelectTags(SelectEvent event) {
		RequestContext.getCurrentInstance().execute("inserirTexto('["+event.getObject().toString()+"]')");
	}

	public void handleFileUpload(FileUploadEvent event) {
		UploadedFile arquivoUpload = event.getFile();
		if(files == null){
			files = new ArrayList<>();
			anexar(arquivoUpload);
		}else if(files.size() < qtdMaximaAnexos){
			anexar(arquivoUpload);
		}else{
			warn(null,"QTD_LIMITE_ARQUIVO_ATINGIDO", qtdMaximaAnexos);
		}
	}

	private void anexar(UploadedFile arquivoUpload) {
		double tamanho = (double)arquivoUpload.getSize()/1024;
		Anexo anexo = new Anexo();
		anexo.setArquivo(arquivoUpload.getContents());
		anexo.setNome(arquivoUpload.getFileName());
		anexo.setTamanho(tamanho);
		anexo.setMimetype(arquivoUpload.getContentType());
		files.add(anexo);
		info("ARQUIVO_ANEXADO");
	}

	/**
	 * Metodo utilizado para enviar o e-mail, no meu caso deve ser boolean, pois
	 * preciso saber se a mensagem foi enviada, sendo enviada, devo gravar a
	 * menagem e a data do envio no banco.
	 *
	 * @return
	 * @throws AtlasAcessoBancoDadosException
	 * @throws MessagingException
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws AtlasNegocioException
	 * @throws ParseException
	 */
	public void enviarMensagem() throws AtlasAcessoBancoDadosException,
	MessagingException, FileNotFoundException, IOException,
	AtlasNegocioException, ParseException {
		try {
			getEntidade().setRemetente("atlas.acessoajustica@gmail.com");
			boolean valido = true;
			if(destinatariosSelecionadosGeral == null || destinatariosSelecionadosGeral.isEmpty()){
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "Atenção - Informe ao menos um destinatário!", null));
				valido = false;
			}
			if(getEntidade().getAssunto() == null || getEntidade().getAssunto().isEmpty()){
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "Atenção - Informe o Assunto!", null));
				valido = false;
			}
			if(valido){
				mensageriaServico.enviarEmail(getEntidade(), destinatariosSelecionadosGeral, files);
				mensageriaServico.salvarMensagem(getEntidade(), destinatariosSelecionadosGeral);
				anexoServico.salvarAnexos(getEntidade(), files);
				info("REGISTRO_GRAVADO");
			}
		} catch (AtlasAcessoBancoDadosException e) {
			e.printStackTrace();
			error(e);
		} catch (SecurityException e){
			error(null, "MSG0", "Não foi possível enviar e-mail de aviso sobre o órgão! "+e.getMessage());
			e.printStackTrace();
		} catch (Exception e){
			StringBuffer sb = new StringBuffer();
			if(e.getMessage().indexOf("SecurityException") != -1){
				sb.append("Não foi possível remeter a mala direta aos órgãos, envio de e-mail com problemas! ");
			}
			sb.append(e.getMessage());
			error(null, "MSG0", sb.toString());
			e.printStackTrace();
		}

	}

	protected void efetuarPesquisaOrgao() throws AtlasAcessoBancoDadosException {
		mensagens.setListaPaginada(mensageriaServico.recuperarDestinatarios(mensagemFiltro));
	}

	public void carregarOrgaos(String textoParcial)
			throws AtlasAcessoBancoDadosException {
		mensagens = new AtlasLazyDataModel<Mensageria>();
		mensagens.setServico(mensageriaServico);
		mensagens.setMetodo("recuperarDestinatarios");
		mensagemFiltro = new Mensageria();
		mensagemFiltro.setNomeOrgao(textoParcial);
		mensagemFiltro.setTipoOrgao(tipoOrgao);
		mensagemFiltro.setMunicipio(municipioCadastro);
		mensagens.setParametros(mensagemFiltro);
		efetuarPesquisaOrgao();
	}

	public void pesquisarDestinatarios() throws AtlasAcessoBancoDadosException{
		orgaoFiltro = new OrgaoDTO();
		orgaoFiltro.setNome(nomeOrgaoPesquisa);
		orgaoFiltro.setMunicipio(municipioCadastro);
		orgaoFiltro.setUf(ufCadastro);
		orgaoFiltro.setNome(nomeOrgaoPesquisa);
		orgaoFiltro.setTipoOrgao(tipoOrgao);
		orgaoFiltro.setAmbitoAtuacaoOrgao(ambitoAtuacaoOrgao);
		destinatariosPesquisa = new AtlasLazyDataModel<Orgao>();
		destinatariosPesquisa.setParametros(orgaoFiltro);
		destinatariosPesquisa.setServico(orgaoServico);
		destinatariosPesquisa.setMetodo("recuperarOrgaosPaginados");
		destinatariosPesquisa.setListaPaginada(orgaoServico.recuperarOrgaosPaginados(orgaoFiltro));
	}

	public void pesquisarOrgaos() throws AtlasAcessoBancoDadosException {
		if (this.nomeOrgaoPesquisa != null && !this.nomeOrgaoPesquisa.trim().isEmpty()) {
			carregarOrgaos("%" + this.nomeOrgaoPesquisa + "%");
		} else {
			carregarOrgaos(null);
		}
	}

	private List<Mensageria> listaDestinatarios;

	public void consultarDestinatarios(Mensageria mensagem) throws AtlasAcessoBancoDadosException {
		listaDestinatarios = mensageriaServico.consultarDestinatarios(mensagem);
	}


	@Override
	public Boolean validar() throws AtlasNegocioException {

		boolean valido = true;
		StringBuilder campoObrigatorio = new StringBuilder();

		if (getEntidade().getRemetente() == null || getEntidade().getRemetente().trim().isEmpty()){
			valido = false;
			campoObrigatorio.append(campoObrigatorio.toString().trim().isEmpty() ? "Enviador Por":", Enviador Por");
		}

		if(getEntidade().getAssunto() == null || getEntidade().getAssunto().trim().isEmpty()){
			valido = false;
			campoObrigatorio.append(campoObrigatorio.toString().trim().isEmpty() ? "Assunto": ", Assunto");
		}

		if(getEntidade().getMensagem() == null || getEntidade().getMensagem().trim().isEmpty()){
			valido = false;
			campoObrigatorio.append(campoObrigatorio.toString().trim().isEmpty() ? "Conteúdo Mensagem" : ", Conteúdo Mensagem");
		}

		if (!valido) {
			error(null, "MSG20", campoObrigatorio.toString());
		}

		return valido;
	}

	public void limparPesquisa(){
		filtroDataInicio = null;
		filtroDataFim = null;
	}

	public List<Municipio> getMunicipios()
			throws AtlasAcessoBancoDadosException {
		if (getUfCadastro() != null) {
			return municipioServico.recuperarMunicipios(getUfCadastro());
		} else {
			return null;
		}
	}

	public void limpar() {
		this.nomeOrgaoPesquisa = null;
		this.nomeDestinatario = null;
		this.emailDestinatario = null;
		this.municipioCadastro = null;
		this.tipoOrgao = null;
		this.ufCadastro = null;
		this.ambitoAtuacaoOrgao = null;
	}

	public void carregarDadosDlgDestinatarios(){
		limpar();
		destinatariosPesquisa = null;
		destinatariosSelecaoPorPagina = null;
	}

	public void limparPesquisar() throws AtlasAcessoBancoDadosException {
		limpar();
		carregarOrgaos(null);
	}

	@SuppressWarnings("unchecked")
	public List<UF> getUfs() throws AtlasAcessoBancoDadosException {
		return mensageriaServico.buscarTodos(UF.class, "nome");
	}

	@SuppressWarnings("unchecked")
	public List<TipoOrgao> getTiposOrgaos()
			throws AtlasAcessoBancoDadosException {
		return mensageriaServico.buscarTodos(TipoOrgao.class, "nome");
	}

	public void alterarFlagEnderecoAlterado(AjaxBehaviorEvent event) {
		setEnderecoAlterado(true);
	}

	@Override
	protected AtlasServicoImpl getServico() {
		return mensageriaServico;
	}

	@Override
	protected String getTelaPesquisa() {
		return "exibirPesquisa";
	}

	@Override
	protected String getTelaCadastro() {
		return "informarMensagem";
	}

	public String voltarTelaPesquisa() throws AtlasAcessoBancoDadosException{
		filtroDataInicio = null;
		filtroDataFim = null;
		return "exibirPesquisa";
	}

	public String voltarTelaCadastro() throws AtlasAcessoBancoDadosException, AtlasNegocioException, ParseException{
		mensagemFiltro.setDataInicio(null);
		mensagemFiltro.setDataFim(null);
		mensagens.setListaPaginada(mensageriaServico.recuperarMensagens(mensagemFiltro));
		return getTelaCadastro();
	}

	public AtlasLazyDataModel<Mensageria> getMensagens() {
		if(mensagens == null){
			mensagens = new AtlasLazyDataModel<Mensageria>();
		}
		return mensagens;
	}

	public void setMensagens(AtlasLazyDataModel<Mensageria> mensagens) {
		this.mensagens = mensagens;
	}


	public String getNomeOrgaoPesquisa() {
		return nomeOrgaoPesquisa;
	}

	public void setNomeOrgaoPesquisa(String mensagemPesquisa) {
		this.nomeOrgaoPesquisa = mensagemPesquisa;
	}

	public Mensageria getMensagemFiltro() {
		return mensagemFiltro;
	}

	public void setFiltro(Mensageria mensagemFiltro) {
		this.mensagemFiltro = mensagemFiltro;
	}

	public Orgao getDestinatarios() {
		return destinatarios;
	}

	public void setDestinatarios(Orgao destinatarios) {
		this.destinatarios = destinatarios;
	}

	public int getTotalDestinatarios() {
		int total = 0;
		if (this.mensagens != null) {
			for (Mensageria mensageria : this.mensagens.getListaPaginada()
					.getResultadoRetorno()) {
				if (mensageria != null
						&& mensageria.getTotalDestinatarios() != null) {
					total += mensageria.getTotalDestinatarios();
				}
			}
		}
		return total;
	}

	public int getTotalMensagensAbertas() {
		int total = 0;
		if (this.mensagens != null) {
			for (Mensageria mensageria : this.mensagens.getListaPaginada()
					.getResultadoRetorno()) {
				if (mensageria != null && mensageria.getTotalAbertos() != null) {
					total += mensageria.getTotalAbertos();
				}
			}
		}
		return total;
	}

	public int getTotalMensagensEnviadas() {
		return this.mensagens.getListaPaginada().getResultadoRetorno().size();
	}

	public String getNomeDestinatario() {
		return nomeDestinatario;
	}

	public void setNomeDestinatario(String nomeDestinatario) {
		this.nomeDestinatario = nomeDestinatario;
	}

	public List<MensageriaOrgao> getEmailDestinatario() {
		return emailDestinatario;
	}

	public void setEmailDestinatario(List<MensageriaOrgao> emailDestinatario) {
		this.emailDestinatario = emailDestinatario;
	}

	public Municipio getMunicipioCadastro() {
		return municipioCadastro;
	}

	public void setMunicipioCadastro(Municipio municipioCadastro) {
		this.municipioCadastro = municipioCadastro;
	}

	public UF getUfCadastro() {
		return ufCadastro;
	}

	public void setUfCadastro(UF ufCadastro) {
		this.ufCadastro = ufCadastro;
	}

	public TipoOrgao getTipoOrgao() {
		return tipoOrgao;
	}

	public void setTipoOrgao(TipoOrgao tipoOrgao) {
		this.tipoOrgao = tipoOrgao;
	}

	public boolean isEnderecoAlterado() {
		return enderecoAlterado;
	}

	public void setEnderecoAlterado(boolean enderecoAlterado) {
		this.enderecoAlterado = enderecoAlterado;
	}

	/**
	 * @return the selectedTag
	 */
	public String getSelectedTag() {
		return selectedTag;
	}

	/**
	 * @param selectedTag the selectedTag to set
	 */
	public void setSelectedTag(String selectedTag) {
		this.selectedTag = selectedTag;
	}

	/**
	 * @return the files
	 */
	public List<Anexo> getFiles() {
		return files;
	}

	/**
	 * @param files the files to set
	 */
	public void setFiles(List<Anexo> files) {
		this.files = files;
	}

	/**
	 * @return the qtdMaximaAnexos
	 */
	public int getQtdMaximaAnexos() {
		return qtdMaximaAnexos;
	}

	/**
	 * @param qtdMaximaAnexos the qtdMaximaAnexos to set
	 */
	public void setQtdMaximaAnexos(int qtdMaximaAnexos) {
		this.qtdMaximaAnexos = qtdMaximaAnexos;
	}

	public Map<Integer, List<Orgao>> getDestinatariosSelecaoPorPagina() {
		return destinatariosSelecaoPorPagina;
	}

	public void setDestinatariosSelecaoPorPagina(Map<Integer, List<Orgao>> mapSelecaoDestinatarios) {
		this.destinatariosSelecaoPorPagina = mapSelecaoDestinatarios;
	}

	public AtlasLazyDataModel<Orgao> getDestinatariosPesquisa() {
		return destinatariosPesquisa;
	}

	public void setDestinatariosPesquisa(AtlasLazyDataModel<Orgao> destinatariosPesquisa) {
		this.destinatariosPesquisa = destinatariosPesquisa;
	}

	/**
	 * @return the orgaoFiltro
	 */
	public OrgaoDTO getOrgaoFiltro() {
		return orgaoFiltro;
	}

	/**
	 * @param orgaoFiltro the orgaoFiltro to set
	 */
	public void setOrgaoFiltro(OrgaoDTO orgaoFiltro) {
		this.orgaoFiltro = orgaoFiltro;
	}

	public List<Orgao> getDestinatariosSelecionados() {
		return destinatariosSelecionados;
	}

	public void setDestinatariosSelecionados(
			List<Orgao> destinatariosSelecionados) {
		this.destinatariosSelecionados = destinatariosSelecionados;
	}

	public void handleUnselect(UnselectEvent event){
		Orgao representanteExcluido = (Orgao) event.getObject();
		if(this.destinatariosSelecionadosGeral != null){
			this.destinatariosSelecionadosGeral.remove(representanteExcluido);
		}
	}

	public void handleSelect(SelectEvent event){
		Orgao representanteExcluido = (Orgao) event.getObject();
		if(this.destinatariosSelecionadosGeral == null){
			this.destinatariosSelecionadosGeral = new ArrayList<>();
			this.destinatariosSelecionadosGeral.remove(representanteExcluido);
		}
	}

	public List<Orgao> getDestinatariosSelecionadosGeral() {
		return destinatariosSelecionadosGeral;
	}

	public void setDestinatariosSelecionadosGeral(
			List<Orgao> destinatariosSelecionadosGeral){
		if(destinatariosSelecionadosGeral != null && !destinatariosSelecionadosGeral.isEmpty()){
			for (Orgao orgaoDestinatario : destinatariosSelecionadosGeral) {
				if(!this.destinatariosSelecionadosGeral.contains(orgaoDestinatario)){
					this.destinatariosSelecionadosGeral.add(orgaoDestinatario);
				}
			}
		}else{
			this.destinatariosSelecionadosGeral = destinatariosSelecionadosGeral;
		}
	}

	public void incluirDestinatariosTabelaAction(ActionEvent event){
		if(destinatariosSelecaoPorPagina != null && !destinatariosSelecaoPorPagina.isEmpty()){
			Set<Integer> paginas = destinatariosSelecaoPorPagina.keySet();
			for (Integer pagina : paginas) {
				List<Orgao> listDestinatarios = destinatariosSelecaoPorPagina.get(pagina);
				for (Orgao orgaoDestinatario : listDestinatarios) {
					inserirDestinatario(orgaoDestinatario);
				}
			}
		}else if(destinatariosSelecionados != null && !destinatariosSelecionados.isEmpty()){
			for (Orgao orgaoDestinatario : destinatariosSelecionados) {
				inserirDestinatario(orgaoDestinatario);
			}
		}
	}

	private void inserirDestinatario(Orgao orgaoDestinatario) {
		if (destinatariosSelecionadosGeral != null
				&& !destinatariosSelecionadosGeral.isEmpty()
				&& !destinatariosSelecionadosGeral.contains(orgaoDestinatario)) {
			destinatariosSelecionadosGeral.add(orgaoDestinatario);
		}else if(destinatariosSelecionadosGeral == null
				|| destinatariosSelecionadosGeral.isEmpty()){
			destinatariosSelecionadosGeral = new ArrayList<>();
			destinatariosSelecionadosGeral.add(orgaoDestinatario);
		}
	}

	private void atribuirDestinatariosPaginaAtual(Integer numPaginaAnt) {
		Integer numPaginaAux = (numPaginaAnt == null) ? numPagina : numPaginaAnt;
		if (destinatariosSelecionados != null && !destinatariosSelecionados.isEmpty()) {
			destinatariosSelecaoPorPagina.put(numPaginaAux, destinatariosSelecionados);
		}
	}

	private void inicializarOrgaosSelecao() {
		if(destinatariosSelecaoPorPagina == null){
			destinatariosSelecaoPorPagina = new HashMap<>();
		}
		if (destinatariosSelecaoPorPagina.keySet().isEmpty()
				|| !destinatariosSelecaoPorPagina.keySet().contains(numPagina)) {
			if(numPagina == null){
				numPagina = 0;
			}
			destinatariosSelecaoPorPagina.put(numPagina, new ArrayList<Orgao>());
		}
	}

	public void onRowSelect(SelectEvent event){
		inicializarOrgaosSelecao();
		Orgao destinatarioAdicionado = ((Orgao)event.getObject());
		List<Orgao> listDestinatarios = destinatariosSelecaoPorPagina.get(numPagina);
		if(!listDestinatarios.contains(destinatarioAdicionado)){
			listDestinatarios.add(destinatarioAdicionado);
		}
	}

	public void onRowUnselect(UnselectEvent event){
		inicializarOrgaosSelecao();
		Orgao destinatarioRemovido = ((Orgao)event.getObject());
		List<Orgao> listDestinatario = destinatariosSelecaoPorPagina.get(numPagina);
		if(listDestinatario.contains(destinatarioRemovido)){
			listDestinatario.remove(destinatarioRemovido);
		}
	}

	public void onToggleSelect(ToggleSelectEvent event){
		inicializarOrgaosSelecao();
		List<Orgao> listDestinatarios = destinatariosSelecaoPorPagina.get(numPagina);
		if(event.isSelected()){
			for (Orgao destinatarioSelecionado : destinatariosSelecionados) {
				if(!listDestinatarios.contains(destinatarioSelecionado)){
					listDestinatarios.add(destinatarioSelecionado);
				}
			}
		}else{
			destinatariosSelecaoPorPagina.get(numPagina).removeAll(listDestinatarios);
		}
	}

	public void onPageChange(PageEvent event){
		atribuirDestinatariosPaginaAtual(numPaginaAnterior);

		numPagina = event.getPage();
		inicializarOrgaosSelecao();
		// Marca os órgãos da página que está sendo exibida
		if (destinatariosSelecaoPorPagina.get(numPagina) != null
				&& !destinatariosSelecaoPorPagina.get(numPagina).isEmpty()) {
			destinatariosSelecionados = destinatariosSelecaoPorPagina.get(numPagina);
		}

		numPaginaAnterior = numPagina;

		System.out.println("Pagina: "+numPagina);
		for (Orgao destinatario : destinatariosSelecionados) {
			System.out.println("Destinatário Selecionado: "+destinatario.getId());
		}
		for (Integer pagina : destinatariosSelecaoPorPagina.keySet()) {
			for (Orgao destinatario : destinatariosSelecaoPorPagina.get(pagina)) {
				System.out.println(pagina+" - "+destinatario.getId());
			}
		}
	}

	public List<Representante> completeDestinatarios(String consulta){
		try {
			if(consulta != null && !consulta.trim().equals("")){
				return representanteServico.recuperarRepresentantes(consulta);
			}
			return null;
		} catch (AtlasAcessoBancoDadosException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public AmbitoAtuacaoOrgao getAmbitoAtuacaoOrgao() {
		return ambitoAtuacaoOrgao;
	}

	public void setAmbitoAtuacaoOrgao(AmbitoAtuacaoOrgao ambitoAtuacaoOrgao) {
		this.ambitoAtuacaoOrgao = ambitoAtuacaoOrgao;
	}

	public List<AmbitoAtuacaoOrgao> getListaAmbitoAtuacao() {
		if (listaAmbitoAtuacao == null) {
			listaAmbitoAtuacao = Arrays.asList(AmbitoAtuacaoOrgao.values());
		}
		return listaAmbitoAtuacao;
	}

	public Date getFiltroDataInicio() {
		return filtroDataInicio;
	}

	public void setFiltroDataInicio(Date filtroDataInicio) {
		this.filtroDataInicio = filtroDataInicio;
	}

	public Date getFiltroDataFim() {
		return filtroDataFim;
	}

	public void setFiltroDataFim(Date filtroDataFim) {
		this.filtroDataFim = filtroDataFim;
	}

	public List<Mensageria> getListaDestinatarios() {
		return listaDestinatarios;
	}

	public void setListaDestinatarios(List<Mensageria> listaDestinatarios) {
		this.listaDestinatarios = listaDestinatarios;
	}
}