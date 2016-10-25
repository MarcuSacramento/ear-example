package br.gov.atlas.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ValueChangeEvent;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.primefaces.model.UploadedFile;

import br.gov.atlas.dto.OrgaoDTO;
import br.gov.atlas.entidade.Cargo;
import br.gov.atlas.entidade.EmailUsuario;
import br.gov.atlas.entidade.Municipio;
import br.gov.atlas.entidade.Orgao;
import br.gov.atlas.entidade.Representante;
import br.gov.atlas.entidade.Telefone;
import br.gov.atlas.entidade.TipoOrgao;
import br.gov.atlas.entidade.UF;
import br.gov.atlas.enums.TipoSituacaoRegistroOrgao;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;
import br.gov.atlas.exception.AtlasNegocioException;
import br.gov.atlas.servico.AtlasServicoImpl;
import br.gov.atlas.servico.EmailServico;
import br.gov.atlas.servico.ManterCargoServico;
import br.gov.atlas.servico.MunicipioServico;
import br.gov.atlas.servico.OrgaoServico;
import br.gov.atlas.servico.RepresentanteServico;
import br.gov.atlas.servico.TelefoneServico;
import br.gov.atlas.util.AtlasWebUtil;
import br.gov.atlas.util.Valida;

@ManagedBean(name = "cadastrarOrgaoBean")
@SessionScoped
public class CadastrarOrgaoBean extends AtlasCrudBean<Orgao> {

	private static final long serialVersionUID = 8547104624905661929L;

	@EJB(name = "OrgaoServico")
	OrgaoServico orgaoServico;

	@EJB(name = "MunicipioServico")
	private MunicipioServico municipioServico;

	@EJB(name = "RepresentanteServico")
	private RepresentanteServico representanteServico;

	@EJB(name = "TelefoneServico")
	private TelefoneServico telefoneServico;

	@EJB(name = "EmailServico")
	private EmailServico emailServico;

	@EJB(name = "ManterCargoServico")
	ManterCargoServico cargoServico;

	private String termoPesquisa;

	private UploadedFile file;

	private OrgaoDTO orgaoFiltro;

	private UF uf;

	private Municipio municipio;

	private TipoOrgao tipoOrgao;

	private String carregarOrgaosErro;

	private String carregarOrgaosInformacaoLatitudeLongitude;

	private Municipio oldValueMunicipio;

	private Municipio newValueMunicipio;

	private Municipio originalMunicipio;

	private Representante representanteEmFoco;

	private List<Representante> listaComRepresentantes;

	private boolean erroRepresentante = false;

	private boolean valido = true;


	public CadastrarOrgaoBean() {
		limpar();
	}

	public void init() {

	}

	public void limpar() {
		setEntidade(new Orgao());
		this.uf = null;
		this.municipio = null;
		this.listaComRepresentantes = new ArrayList<>();
		this.representanteEmFoco = new Representante();
		getEntidade().setIndicadorSituacao(
				TipoSituacaoRegistroOrgao.EM_ANALISE.indiceSituacao());
	}

	public void armazenarRepresentanteOuGravarImediatamente()
			throws CloneNotSupportedException {
		this.oldValueMunicipio = getEntidade().getMunicipio();

		if (listaComRepresentantes == null)
			listaComRepresentantes = new ArrayList<>();
			if (validarRepresentante()) {
				if (!listaComRepresentantes.contains(representanteEmFoco)) {
					Representante clone = (Representante) representanteEmFoco
							.clone();
					listaComRepresentantes.add(clone);
				}
				erroRepresentante = false;
				inicializarRepresentanteNovo();
			}
	}


	private Boolean validarRepresentante() {
		valido = true;
		boolean emailValido = true;

		StringBuffer campoObrigatorio = new StringBuffer();

		if (representanteEmFoco.getNome().isEmpty()) {
			valido = false;
			campoObrigatorio.append(campoObrigatorio.toString().trim()
					.isEmpty() ? "Nome" : ", Nome");
		}
		if (representanteEmFoco.getCargo() == null) {
			valido = false;
			campoObrigatorio.append(campoObrigatorio.toString().trim()
					.isEmpty() ? "Cargo" : ", Cargo");
		}
		if (representanteEmFoco.getEmail().isEmpty()) {
			valido = false;
			campoObrigatorio.append(campoObrigatorio.toString().trim()
					.isEmpty() ? "Email" : ", Email");
		}

		if (representanteEmFoco.getEmail() != null
				&& !representanteEmFoco.getEmail().isEmpty()) {
			if (!Valida.validaEmail(representanteEmFoco.getEmail())) {
				error(null, "MSG8");
				emailValido = false;
			}
		}

		if (!valido) {
			erroRepresentante = true;
			error(null, "MSG20", campoObrigatorio.toString());
		}else{
			erroRepresentante = false;
		}

		if (!emailValido) {
			valido = emailValido;
		}

		return valido;

	}

	public void inicializarRepresentanteNovo() {
		if (!erroRepresentante) {
			this.representanteEmFoco = new Representante();
		}

	}

	public void excluirRepresentante() {
		this.listaComRepresentantes.remove(this.representanteEmFoco);
	}

	@Override
	public void inicializarObjetos() throws AtlasAcessoBancoDadosException {
		this.uf = getEntidade().getMunicipio().getUf();
	}

	/*
	 * 
	 * TODO: Talvéz seja necessario refatorar esta parte do código, pois
	 * coloquei os insertes em casaca. Pode ser que seja necessario colocar
	 * estes insertes na camada de servico/controller
	 * 
	 * @see br.gov.atlas.bean.AtlasCrudBean#salvar()
	 */
	@Override
	public String salvar() {
		corrigirComboMunicipio();

		this.getEntidade().setIndicadorSituacao(
				TipoSituacaoRegistroOrgao.PROPOSTO.indiceSituacao());
		Orgao orgao = getEntidade();

		EmailUsuario emailInstitucional = orgao.recuperarEmailInstitucionalOuCriar();
		emailInstitucional.setOrgao(orgao);
		emailInstitucional.setNome(AtlasWebUtil.estaVazio(getEntidade()
				.getDescricaoEmailInstitucional()) ? null : getEntidade()
						.getDescricaoEmailInstitucional());
		emailInstitucional.setAsInstitucional();

		super.salvar();

		enviarEmail();

		if (getEntidade().getId() != null) {

			getEntidade().setEmails(
					salvarEmail(getEntidade(), emailInstitucional));
			getEntidade().setTelefones(salvarTelefone(getEntidade()));
			getEntidade().setRepresentantes(salvarRepresentante(getEntidade()));

			if (valido) {
				limpar();
			}

		}
		return null;
	}

	private List<EmailUsuario> salvarEmail(Orgao orgao, EmailUsuario emailInstitucional) {

		List<EmailUsuario> listaComEmailInstitucional = new ArrayList<>();
		listaComEmailInstitucional.add(emailInstitucional);
		for (EmailUsuario email : listaComEmailInstitucional) {
			if (email != null) {
				email.setOrgao(orgao);
				try {
					emailServico.salvar(email);
				} catch (AtlasAcessoBancoDadosException | AtlasNegocioException e) {
					e.printStackTrace();
				}
			}
		}
		getEntidade().setEmails(listaComEmailInstitucional);

		return listaComEmailInstitucional;
	}

	private List<Telefone> salvarTelefone(Orgao orgao) {

		List<Telefone> listaComTelefones = new ArrayList<>();
		listaComTelefones.add(orgao.getPrimeiroTelefoneAsObject());
		listaComTelefones.add(orgao.getSegundoTelefoneAsObject());
		if (!listaComTelefones.isEmpty()) {
			for (Telefone telefone : listaComTelefones) {
				if (telefone != null) {
					telefone.setOrgao(orgao);
					try {
						telefoneServico.salvar(telefone);
					} catch (AtlasAcessoBancoDadosException
							| AtlasNegocioException e) {
						e.printStackTrace();
					}
				}
			}
			getEntidade().setTelefones(listaComTelefones);
		}

		return listaComTelefones;
	}

	private List<Representante> salvarRepresentante(Orgao orgao) {
		List<Representante> listaComRepresentantesLocal = new ArrayList<>();
		for (Representante representante : this.listaComRepresentantes) {
			representante.setOrgao(orgao);
			try {
				representanteServico.salvar(representante);
			} catch (AtlasAcessoBancoDadosException e) {
				e.printStackTrace();
			} catch (AtlasNegocioException e) {
				e.printStackTrace();
			}
			listaComRepresentantesLocal.add(representante);
		}

		return listaComRepresentantesLocal;
	}

	private void corrigirComboMunicipio() {
		Municipio municipioAceito = null;

		if (this.newValueMunicipio == null && this.oldValueMunicipio != null)
			municipioAceito = this.oldValueMunicipio;
		else if (getEntidade().getMunicipio() == null
				&& this.originalMunicipio != null)
			municipioAceito = this.originalMunicipio;
		else
			municipioAceito = getEntidade().getMunicipio();

		Orgao orgao = getEntidade();
		orgao.setMunicipio(municipioAceito);
	}

	private void enviarEmail() {
		final String username = "atlasdajustica@gmail.com";
		final String password = "atlasjustica";

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("atlasdajustica@gmail.com"));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse("atlasdajustica@gmail.com"));
			message.setSubject("Novo Orgao Cadastrado");
			message.setText("Foi cadastrado um novo orgao no Módulo Público.");

			Transport.send(message);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	@Override
	public Boolean validar() {

		valido = true;
		boolean emailValido = true;

		StringBuffer campoObrigatorio = new StringBuffer();

		if (getEntidade().getSigla() == null
				|| getEntidade().getSigla().trim().isEmpty()) {
			valido = false;
			campoObrigatorio.append(campoObrigatorio.toString().trim()
					.isEmpty() ? "Sigla" : ", Sigla");
		}
		if (getEntidade().getTipoOrgao() == null) {
			valido = false;
			campoObrigatorio.append(campoObrigatorio.toString().trim()
					.isEmpty() ? "Tipo do Órgão" : ", Tipo do Órgão");
		}

		if (getEntidade().getNome() == null
				|| getEntidade().getNome().trim().isEmpty()) {
			valido = false;
			campoObrigatorio.append(campoObrigatorio.toString().trim()
					.isEmpty() ? "Nome" : ", Nome");
		}
		if (getEntidade().getDescricao() == null
				|| getEntidade().getDescricao().trim().isEmpty()) {
			valido = false;
			campoObrigatorio.append(campoObrigatorio.toString().trim()
					.isEmpty() ? "Descrição" : ", Descrição");
		}
		if (getUf() == null  ) {
			valido = false;
			campoObrigatorio.append(campoObrigatorio.toString().trim()
					.isEmpty() ? "Estado" : ", Estado");
		}

		if (getEntidade().getMunicipio() == null||getEntidade().getMunicipio().getId()==null||getEntidade().getMunicipio().getNome().isEmpty()){
			valido = false;
			campoObrigatorio.append(campoObrigatorio.toString().trim()
					.isEmpty() ? "Municipio" : ", Municipio");
		}

		if (getEntidade().getEndereco() == null
				|| getEntidade().getEndereco().trim().isEmpty()) {
			valido = false;
			campoObrigatorio.append(campoObrigatorio.toString().trim()
					.isEmpty() ? "Endereço" : ", Endereço");
		}

		if (getEntidade().getBairro() == null
				|| getEntidade().getBairro().trim().isEmpty()) {
			valido = false;
			campoObrigatorio.append(campoObrigatorio.toString().trim()
					.isEmpty() ? "Bairro" : ", Bairro");
		}

		if (getEntidade().getCep() == null || getEntidade().getCep().isEmpty()) {
			valido = false;
			campoObrigatorio.append(campoObrigatorio.toString().trim()
					.isEmpty() ? "CEP" : ", CEP");
		}
		if (getEntidade().getDescricaoEmailInstitucional() != null
				&& !getEntidade().getDescricaoEmailInstitucional().isEmpty()) {
			if (!Valida.validaEmail(getEntidade()
					.getDescricaoEmailInstitucional())) {
				error(null, "MSG8");
				emailValido = false;
			}
		}
		if (!valido) {
			error(null, "MSG20", campoObrigatorio.toString());
		}

		if (!emailValido) {
			valido = emailValido;
		}
		return valido;

	}

	@Override
	protected void initCarregarEntidade() throws AtlasAcessoBancoDadosException {
		this.uf = getEntidade().getMunicipio().getUf();
		this.listaComRepresentantes = carregarRepresentantes();
		this.originalMunicipio = getEntidade().getMunicipio();

		List<Telefone> listaComTelefones = orgaoServico
				.recuperarTelefones(getEntidade());
		if (listaComTelefones != null) {
			if (listaComTelefones.size() >= 1)
				getEntidade().setPrimeiroTelefoneAsObject(
						listaComTelefones.get(0));

			if (listaComTelefones.size() >= 2)
				getEntidade().setSegundoTelefoneAsObject(
						listaComTelefones.get(1));
		}
	}

	private List<Representante> carregarRepresentantes()
			throws AtlasAcessoBancoDadosException {
		Orgao orgao = getEntidade();
		List<Representante> representantes = representanteServico
				.buscarTodos(orgao);
		return representantes;
	}

	@Override
	protected AtlasServicoImpl getServico() {
		return orgaoServico;
	}

	@Override
	public String cancelar() {
		limpar();
		return "/pub/sobre/portasDaJustica/consultarPortasDaJustica.faces";
	}

	@Override
	protected String getTelaPesquisa() {
		return "/pub/sobre/portasDaJustica/consultarPortasDaJustica.faces";
	}

	@Override
	protected String getTelaCadastro() {
		return "orgaoCadastro";
	}

	public String getTermoPesquisa() {
		return termoPesquisa;
	}

	public void setTermoPesquisa(String termoPesquisa) {
		this.termoPesquisa = termoPesquisa;
	}

	public UploadedFile getFile() {
		return file;
	}

	public void setFile(UploadedFile file) {
		this.file = file;
	}

	public OrgaoDTO getOrgaoFiltro() {
		return orgaoFiltro;
	}

	public void setOrgaoFiltro(OrgaoDTO orgaoFiltro) {
		this.orgaoFiltro = orgaoFiltro;
	}

	public UF getUf() {
		return uf;
	}

	public void setUf(UF uf) {
		this.uf = uf;
	}

	public Municipio getMunicipio() {
		return municipio;
	}

	public void setMunicipio(Municipio municipio) {
		this.municipio = municipio;
	}

	public void assinalarMudancaMunicipioNoCadastro(ValueChangeEvent e) {
		this.oldValueMunicipio = (Municipio) e.getOldValue();
		this.newValueMunicipio = (Municipio) e.getNewValue();
	}

	public TipoOrgao getTipoOrgao() {
		return tipoOrgao;
	}

	public void setTipoOrgao(TipoOrgao tipoOrgao) {
		this.tipoOrgao = tipoOrgao;
	}

	@SuppressWarnings("unchecked")
	public List<TipoOrgao> getTiposOrgaos()
			throws AtlasAcessoBancoDadosException {
		return orgaoServico.buscarTodos(TipoOrgao.class, "nome");
	}

	@SuppressWarnings("unchecked")
	public List<Cargo> getCargos() throws AtlasAcessoBancoDadosException {
		return cargoServico.buscarTodos(Cargo.class, "nome");
	}

	@SuppressWarnings("unchecked")
	public List<UF> getUfs() throws AtlasAcessoBancoDadosException {
		return orgaoServico.buscarTodos(UF.class, "nome");
	}

	public List<Municipio> getMunicipios()
			throws AtlasAcessoBancoDadosException {
		if (getUf() != null) {
			return municipioServico.recuperarMunicipios(getUf());
		} else {
			return null;
		}
	}

	public String getCarregarOrgaosErro() {
		return carregarOrgaosErro;
	}

	public void setCarregarOrgaosErro(String carregarOrgaosErro) {
		this.carregarOrgaosErro = carregarOrgaosErro;
	}

	public String getCarregarOrgaosInformacaoLatitudeLongitude() {
		return carregarOrgaosInformacaoLatitudeLongitude;
	}

	public void setCarregarOrgaosInformacaoLatitudeLongitude(
			String carregarOrgaosInformacaoLatitudeLongitude) {
		this.carregarOrgaosInformacaoLatitudeLongitude = carregarOrgaosInformacaoLatitudeLongitude;
	}

	@Override
	protected void efetuarPesquisa() throws AtlasAcessoBancoDadosException {
		//
	}

	public String novoOrgao(Municipio municipio, UF uf) {
		getEntidade().setMunicipio(municipio);
		this.uf = uf;
		return "/pub/manterOrgaoJustica/orgaoCadastro.faces";
	}

	public Representante getRepresentanteEmFoco() {
		return representanteEmFoco;
	}

	public void setRepresentanteEmFoco(Representante representanteEmFoco) {
		this.representanteEmFoco = representanteEmFoco;
	}

	public Municipio getOldValueMunicipio() {
		return oldValueMunicipio;
	}

	public void setOldValueMunicipio(Municipio oldValueMunicipio) {
		this.oldValueMunicipio = oldValueMunicipio;
	}

	public Municipio getNewValueMunicipio() {
		return newValueMunicipio;
	}

	public void setNewValueMunicipio(Municipio newValueMunicipio) {
		this.newValueMunicipio = newValueMunicipio;
	}

	public List<Representante> getListaComRepresentantes() {
		return listaComRepresentantes;
	}

	public void setListaComRepresentantes(
			List<Representante> listaComRepresentantes) {
		this.listaComRepresentantes = listaComRepresentantes;
	}

	public boolean isErroRepresentante() {
		return erroRepresentante;
	}

	public void setErroRepresentante(boolean erroRepresentante) {
		this.erroRepresentante = erroRepresentante;
	}


}