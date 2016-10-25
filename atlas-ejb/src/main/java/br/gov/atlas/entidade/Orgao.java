package br.gov.atlas.entidade;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.swing.text.MaskFormatter;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;

import br.gov.atlas.enums.AmbitoAtuacaoOrgao;
import br.gov.atlas.enums.TipoSituacaoRegistroOrgao;

@Table(schema="atlas", name="orgao")
@Inheritance(strategy = InheritanceType.JOINED)
@SequenceGenerator(schema="atlas", name = "orgao_id_orgao_seq", sequenceName = "orgao_id_orgao_seq", allocationSize = 1)
@Entity
@Audited
@AuditTable(schema="auditoria", value="orgao_aud")
public class Orgao extends AtlasEntidade {

	private static final long serialVersionUID = -8407851573030720998L;

	@Id
	@Column(name = "id_orgao")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "orgao_id_orgao_seq")
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "id_tipo_orgao")
	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	private TipoOrgao tipoOrgao;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_orgao_superior", referencedColumnName = "id_orgao")
	private Orgao orgaoSuperior;

	@OneToMany(mappedBy = "orgao", cascade=CascadeType.ALL, orphanRemoval=true, fetch = FetchType.LAZY)
	@NotAudited
	private List<Telefone> telefones;

	@OneToMany(mappedBy = "orgao", cascade=CascadeType.ALL, orphanRemoval=true, fetch = FetchType.EAGER)
	@NotAudited
	private List<EmailUsuario> emails;

	@OneToMany(mappedBy = "orgao", cascade=CascadeType.ALL, fetch = FetchType.LAZY)
	@NotAudited
	private List<Problema> problemas;

	@ManyToOne
	@JoinColumn(name = "id_municipio")
	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	private Municipio municipio;

	@OneToMany(mappedBy = "orgao", cascade=CascadeType.ALL, orphanRemoval=true, fetch = FetchType.EAGER)
	@Fetch(FetchMode.SUBSELECT)
	@NotAudited
	private List<Representante> representantes;

	@ManyToMany(fetch = FetchType.EAGER)
	@Fetch(FetchMode.SUBSELECT)
	@JoinTable(name = "atlas.tema_orgao",
	joinColumns = { @JoinColumn(name = "id_orgao", nullable = false, updatable = false) },
	inverseJoinColumns = { @JoinColumn(name = "id_tema", nullable = false, updatable = false) })
	@NotAudited
	private List<Tema> temas;

	@ManyToMany(fetch = FetchType.LAZY)
	@Fetch(FetchMode.SUBSELECT)
	@JoinTable(name = "atlas.servico_orgao",
	joinColumns = { @JoinColumn(name = "id_orgao", nullable = false, updatable = false) },
	inverseJoinColumns = { @JoinColumn(name = "id_servico", nullable = false, updatable = false) })
	@NotAudited
	private List<Servico> servicos;

	@ManyToMany( mappedBy="orgaos")
	@NotAudited
	private Set<Usuario> usuarios;

	@Column(name = "cod_sigla_orgao")
	private String sigla;

	@Column(name = "nme_orgao")
	private String nome;

	@Column(name = "dsc_orgao")
	private String descricao;

	@Column(name = "dsc_endereco")
	private String endereco;

	@Column(name = "dsc_bairro")
	private String bairro;

	@Column(name = "nme_homepage")
	String homePage;

	@Column(name = "nu_cep")
	private String cep;

	@Column(name = "num_longitude")
	private BigDecimal longitude;

	@Column(name = "num_latitude")
	private BigDecimal latitude;

	@Column(name="dta_atualizacao")
	private Date dataAtualizacao;

	@Column(name="dta_cadastro")
	private Date dataCadastro;

	@Column(name="ind_situacao")
	private String indicadorSituacao;

	@Column(name="dsc_desinencia")
	private String desinencia;

	@Column(name = "ind_marcacao_manual")
	private boolean indicadorMarcacaoManual;

	@Column(name="ind_ambito_atuacao")
	@Enumerated(EnumType.STRING)
	private AmbitoAtuacaoOrgao indicadorAmbitoAtuacao;

	@Transient
	private String temasFormatados;

	@Transient
	private String servicosFormatados;

	@Transient
	private String urlImagem;

	@Transient
	private UF uf;

	@Transient
	private String descricaoEmailInstitucional;

	@Transient
	private String primeiroDdd;

	@Transient
	private String primeiroNumeroTelefone;

	@Transient
	private String primeiroTipoTelefone;

	@Transient
	private String segundoDdd;

	@Transient
	private String segundoNumeroTelefone;

	@Transient
	private String segundoTipoTelefone;

	public Orgao() {
		this.dataAtualizacao = new Date();
		this.dataCadastro = new Date();
		this.representantes = new ArrayList<>();
	}

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	public Orgao getOrgaoSuperior() {
		return orgaoSuperior;
	}

	public void setOrgaoSuperior(Orgao orgaoSuperior) {
		this.orgaoSuperior = orgaoSuperior;
	}

	public String getRepresentantesFormatados() {
		StringBuilder resultado = new StringBuilder();
		if (representantes != null && !representantes.isEmpty()) {
			resultado.append("Nome da autoridade: ");
			for (Representante representante : representantes) {
				if (representante.getNome() != null) {
					resultado.append(representante.getNome()).append("; ");
				}
			}
			resultado.deleteCharAt(resultado.length() - 1);
		}
		return resultado.toString();
	}


	public String getTemasFormatados() {
		for (Tema tema : getTemas()) {
			if(temasFormatados == null){
				temasFormatados = tema.getNome();
			}else{
				temasFormatados+="; "+tema.getNome();
			}
		}
		return temasFormatados;
	}

	public void setTemasFormatados(String temasFormatados) {
		this.temasFormatados = temasFormatados;
	}

	public String getServicosFormatados() {
		for (Servico servico : getServicos()) {
			if(servicosFormatados == null){
				servicosFormatados = servico.getNome();
			}else{
				servicosFormatados+="; "+servico.getNome();
			}
		}
		return servicosFormatados;
	}

	public void setServicosFormatados(String servicosFormatados) {
		this.servicosFormatados = servicosFormatados;
	}

	@Override
	public String getLabel() {
		return null;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Municipio getMunicipio() {
		return municipio;
	}

	public void setMunicipio(Municipio municipio) {
		this.municipio = municipio;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public String getNomeReduzido() {
		String nm = nome!=null?nome.replace("\n", " "):"";
		if(nm != null && !nm.equals("") && nm.length() > 50){
			return nm.substring(0, 50)+ "... ";
		}
		return nm;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getEndereco() {
		return endereco;
	}

	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}

	public String getEnderecoFormatado() throws ParseException{
		StringBuilder sb = new StringBuilder();
		sb.append(getEndereco());
		sb.append(isBairroPreenchido() ? " - ".concat(getBairro()) : "");
		sb.append(", CEP: ").append(getCepFormatado()).append(", ");
		sb.append(getMunicipio().getNome()).append(" - ").append(getMunicipio().getUf().getSigla());
		return sb.toString();
	}

	public boolean isBairroPreenchido(){
		if(getBairro() != null && !getBairro().trim().equals("")){
			return true;
		}
		return false;
	}

	public String getBairro() {
		return bairro;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	public String getHomePage() {
		return homePage;
	}

	public void setHomePage(String homePage) {
		this.homePage = homePage;
	}

	public String getCep() {
		return cep;
	}

	public String getCepFormatado() throws ParseException
	{
		if(getCep()!=null && !getCep().contains("-")){
			MaskFormatter mask = new MaskFormatter("#####-###");
			mask.setValueContainsLiteralCharacters(false);
			return mask.valueToString(getCep());
		}
		return getCep();
	}

	public void setCep(String cep) {
		this.cep = cep;
	}

	public BigDecimal getLongitude() {
		return longitude;
	}

	public void setLongitude(BigDecimal longitude) {
		this.longitude = longitude;
	}

	public BigDecimal getLatitude() {
		return latitude;
	}

	public void setLatitude(BigDecimal latitude) {
		this.latitude = latitude;
	}

	public TipoOrgao getTipoOrgao() {
		return tipoOrgao;
	}

	public void setTipoOrgao(TipoOrgao tipoOrgao) {
		this.tipoOrgao = tipoOrgao;
	}

	public boolean isTipoOrgaoProcon(){
		if (this.tipoOrgao != null
				&& this.tipoOrgao.getId().equals(new Integer(7))) {
			return true;
		}
		return false;
	}

	public List<Telefone> getTelefones() {
		return telefones;
	}

	public void setTelefones(List<Telefone> telefones) {
		this.telefones = telefones;
	}

	public String getContatosFormatados() {
		StringBuilder resultado = new StringBuilder();
		if (telefones != null && !telefones.isEmpty()) {
			for (Telefone telefone : telefones) {
				if (telefone.getNumeroTelefone()!=null && telefone.getDdd()!=null && !telefone.getDdd().equals("000")) {
					try {
						resultado.append(telefone.getTelefoneFormatado()).append(" - ");
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
			}
			resultado.replace(resultado.length() - 3, resultado.length(), "");
		}
		return resultado.toString();
	}

	public List<EmailUsuario> getEmails() {
		return emails;
	}

	public EmailUsuario recuperarEmailInstitucionalOuCriar()
	{
		if( emails!=null )
			for (EmailUsuario email : emails)
			{
				if( email.isInstitucional() )
				{
					return email;
				}
			}
		return new EmailUsuario();
	}

	public void setEmails(List<EmailUsuario> emails) {
		this.emails = emails;
	}

	public List<Problema> getProblemas() {
		return problemas;
	}

	public void setProblemas(List<Problema> problemas) {
		this.problemas = problemas;
	}

	public boolean isProblemaResolvido(){
		if(problemas != null && !problemas.isEmpty()){
			for (Problema problema : problemas) {
				if(!problema.isProblemaResolvido()){
					return false;
				}
			}
		}
		return true;
	}

	public List<Representante> getRepresentantes() {
		if( this.representantes == null )
			this.representantes = new ArrayList<>();
			return representantes;
	}

	public void setRepresentantes(List<Representante> representantes) {
		this.representantes = representantes;
	}

	public List<Tema> getTemas() {
		return temas;
	}

	public void setTemas(List<Tema> temas) {
		this.temas = temas;
	}

	public List<Servico> getServicos() {
		return servicos;
	}

	public void setServicos(List<Servico> servicos) {
		this.servicos = servicos;
	}

	public String getUrlImagem() {
		return urlImagem;
	}

	public void setUrlImagem(String urlImagem) {
		this.urlImagem = urlImagem;
	}

	public Date getDataAtualizacao() {
		return dataAtualizacao;
	}

	public void setDataAtualizacao(Date dataAtualizacao) {
		this.dataAtualizacao = dataAtualizacao;
	}

	public String getDataAtualizacaoFormatada(){
		if(dataAtualizacao != null){
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			return sdf.format(dataAtualizacao);
		}
		return "";
	}

	public String getDataCadastroFormatada(){
		if(dataCadastro != null){
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			return sdf.format(dataCadastro);
		}
		return "";
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public String getIndicadorSituacao() {
		return indicadorSituacao;
	}

	public String getIndicadorSituacaoFormatada(){
		return (indicadorSituacao != null && !indicadorSituacao.equals("") ? TipoSituacaoRegistroOrgao
				.create(indicadorSituacao).descricao() : null);
	}

	public void setIndicadorSituacao(String indicadorSituacao) {
		this.indicadorSituacao = indicadorSituacao;
	}

	public UF getUf() {
		return uf;
	}

	public void setUf(UF uf) {
		this.uf = uf;
	}

	public String getDescricaoEmailInstitucional()
	{
		if( this.descricaoEmailInstitucional == null )
			return recuperarEmailInstitucionalOuCriar().getNome();
		return this.descricaoEmailInstitucional;
	}

	public void setDescricaoEmailInstitucional(String email) {
		this.descricaoEmailInstitucional = email;
	}

	public String getPrimeiroDdd() {
		return primeiroDdd;
	}

	public void setPrimeiroDdd(String primeiroDdd) {
		this.primeiroDdd = primeiroDdd;
	}

	public String getPrimeiroNumeroTelefone() {
		return primeiroNumeroTelefone;
	}

	public void setPrimeiroNumeroTelefone(String primeiroNumeroTelefone) {
		this.primeiroNumeroTelefone = primeiroNumeroTelefone;
	}

	public String getPrimeiroTipoTelefone() {
		return primeiroTipoTelefone;
	}

	public void setPrimeiroTipoTelefone(String primeiroTipoTelefone) {
		this.primeiroTipoTelefone = primeiroTipoTelefone;
	}

	public String getSegundoDdd() {
		return segundoDdd;
	}

	public void setSegundoDdd(String segundoDdd) {
		this.segundoDdd = segundoDdd;
	}

	public String getSegundoNumeroTelefone() {
		return segundoNumeroTelefone;
	}

	public void setSegundoNumeroTelefone(String segundoNumeroTelefone) {
		this.segundoNumeroTelefone = segundoNumeroTelefone;
	}

	public String getSegundoTipoTelefone() {
		return segundoTipoTelefone;
	}

	public void setSegundoTipoTelefone(String segundoTipoTelefone) {
		this.segundoTipoTelefone = segundoTipoTelefone;
	}

	public Set<Usuario> getUsuarios() {
		return usuarios;
	}

	public void setUsuarios(Set<Usuario> usuarios) {
		this.usuarios = usuarios;
	}

	public Telefone getPrimeiroTelefoneAsObject()
	{
		boolean isPrimeiroDddFAILED 		   = primeiroDdd==null 			  || primeiroDdd.trim().isEmpty();
		boolean isPrimeiroNumeroTelefoneFAILED = primeiroNumeroTelefone==null || primeiroNumeroTelefone.trim().isEmpty();
		boolean isPrimeiroTipoTelefoneFAILED   = primeiroTipoTelefone==null   || primeiroTipoTelefone.trim().isEmpty();

		boolean isFAILED = isPrimeiroDddFAILED || isPrimeiroNumeroTelefoneFAILED || isPrimeiroTipoTelefoneFAILED;

		if( isFAILED )
			return null;

		Telefone telefone = new Telefone();
		telefone.setOrgao		  ( this					);
		telefone.setDdd			  ( primeiroDdd				);
		telefone.setNumeroTelefone( primeiroNumeroTelefone	);
		telefone.setTipoTelefone  ( primeiroTipoTelefone	);
		return telefone;
	}

	public void setPrimeiroTelefoneAsObject(Telefone telefone)
	{
		this.primeiroDdd = telefone.getDdd();
		this.primeiroNumeroTelefone = telefone.getNumeroTelefone();
		this.primeiroTipoTelefone = telefone.getTipoTelefone();
	}

	public Telefone getSegundoTelefoneAsObject()
	{
		boolean isSegundoDddFAILED 			  = segundoDdd==null 			|| segundoDdd.trim().isEmpty();
		boolean isSegundoNumeroTelefoneFAILED = segundoNumeroTelefone==null || segundoNumeroTelefone.trim().isEmpty();
		boolean isSegundoTipoTelefoneFAILED   = segundoTipoTelefone==null   || segundoTipoTelefone.trim().isEmpty();

		boolean isFAILED = isSegundoDddFAILED || isSegundoNumeroTelefoneFAILED || isSegundoTipoTelefoneFAILED;

		if( isFAILED )
			return null;

		Telefone telefone = new Telefone();
		telefone.setOrgao		  ( this					);
		telefone.setDdd			  ( segundoDdd				);
		telefone.setNumeroTelefone( segundoNumeroTelefone	);
		telefone.setTipoTelefone  ( segundoTipoTelefone		);
		return telefone;
	}

	public void setSegundoTelefoneAsObject(Telefone telefone)
	{
		this.segundoDdd = telefone.getDdd();
		this.segundoNumeroTelefone = telefone.getNumeroTelefone();
		this.segundoTipoTelefone = telefone.getTipoTelefone();
	}

	public boolean isLatitudeLongitudePreenchido(){
		return this.longitude != null
				&& !this.longitude.equals(BigDecimal.ZERO)
				&& this.latitude != null
				&& !this.latitude.equals(BigDecimal.ZERO);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Orgao other = (Orgao) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public void limparCoordenadas(){
		setLatitude(null);
		setLongitude(null);
	}

	public String getDesinencia() {
		return desinencia;
	}

	public void setDesinencia(String desinencia) {
		this.desinencia = desinencia;
	}

	public boolean isIndicadorMarcacaoManual() {
		return indicadorMarcacaoManual;
	}

	public void setIndicadorMarcacaoManual(boolean indicadorMarcacaoManual) {
		this.indicadorMarcacaoManual = indicadorMarcacaoManual;
	}

	public AmbitoAtuacaoOrgao getIndicadorAmbitoAtuacao() {
		return indicadorAmbitoAtuacao;
	}

	public void setIndicadorAmbitoAtuacao(AmbitoAtuacaoOrgao indicadorAmbitoAtuacao) {
		this.indicadorAmbitoAtuacao = indicadorAmbitoAtuacao;
	}

}