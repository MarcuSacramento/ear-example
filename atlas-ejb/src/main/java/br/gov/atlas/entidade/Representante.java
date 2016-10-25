package br.gov.atlas.entidade;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Table(name = "representante_orgao", schema="atlas")
@Inheritance(strategy = InheritanceType.JOINED)
@SequenceGenerator(schema="atlas", name = "representante_orgao_id_representante_seq", sequenceName = "representante_orgao_id_representante_seq", allocationSize = 1)
@Entity
public class Representante extends AtlasEntidade implements Cloneable
{
	private static final long serialVersionUID = -3273256820327695266L;



	@Id
	@Column(name = "id_representante")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "representante_orgao_id_representante_seq")
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "id_orgao", insertable = true, updatable = true)
	private Orgao orgao;

	@ManyToOne
	@JoinColumn(name = "id_cargo")
	private Cargo cargo;

	@Column(name = "nme_representante")
	private String nome;

	@Column(name = "nme_email")
	private String email;

	@Column(name = "nme_email_2")
	private String segundoEmail;

	@Column(name = "nu_ddd_1")
	private String primeiroDdd;

	@Column(name = "nu_telefone_1")
	private String primeiroTelefone;

	@Column(name = "ind_tipo_telefone_1")
	private String primeiroTipoTelefone;

	@Column(name = "nu_ddd_2")
	private String segundoDdd;

	@Column(name = "nu_telefone_2")
	private String segundoTelefone;

	@Column(name = "ind_tipo_telefone_2")
	private String segundoTipoTelefone;

	@Column(name = "ind_status_divulga")
	private String statusDivulgacao = "N";

	@Column(name="dsc_desinencia")
	private String desinencia;

	@Column(name="dsc_vocativo")
	private String vocativo;

	@Column(name="dsc_pronome")
	private String pronome;

	public static final String DIVULGAVEL = "S";
	public static final String DIVULGAVEL_EXTENSO = "SIM";

	public static final String NAO_DIVULGAVEL = "N";
	public static final String NAO_DIVULGAVEL_EXTENSO = "NÃO";

	@Override
	public Integer getId()
	{
		return this.id;
	}

	@Override
	public void setId(Integer id)
	{
		this.id = id;
	}

	@Override
	public String getLabel() {
		return this.nome;
	}

	@Override
	public Object clone() throws CloneNotSupportedException
	{
		Representante reprOrgao = new Representante();
		reprOrgao.id 					= this.id;
		reprOrgao.orgao 				= this.orgao;
		reprOrgao.nome 					= this.nome;
		reprOrgao.email 				= this.email;
		reprOrgao.cargo 				= this.cargo;
		reprOrgao.primeiroDdd 			= this.primeiroDdd;
		reprOrgao.primeiroTelefone 		= this.primeiroTelefone;
		reprOrgao.primeiroTipoTelefone	= this.primeiroTipoTelefone;
		reprOrgao.segundoDdd 			= this.segundoDdd;
		reprOrgao.segundoTelefone 		= this.segundoTelefone;
		reprOrgao.segundoTipoTelefone	= this.segundoTipoTelefone;
		reprOrgao.statusDivulgacao		= this.statusDivulgacao;
		return reprOrgao;
	}



	public Orgao getOrgao() {
		return orgao;
	}

	public void setOrgao(Orgao orgao) {
		this.orgao = orgao;
	}

	public Cargo getCargo() {
		return cargo;
	}

	public void setCargo(Cargo cargo) {
		this.cargo = cargo;
	}



	public String getNome() {
		return nome;
	}
	
	public String getNomeLimitado() {
		return nome!=null?(nome.length()<30?nome:nome.substring(0, 30)+"..."):"";
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPrimeiroDdd() {
		return primeiroDdd;
	}

	public void setPrimeiroDdd(String primeiroDdd) {
		this.primeiroDdd = primeiroDdd;
	}

	public String getPrimeiroTelefone() {
		return primeiroTelefone;
	}

	public void setPrimeiroTelefone(String primeiroTelefone) {
		this.primeiroTelefone = primeiroTelefone;
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

	public String getSegundoTelefone() {
		return segundoTelefone;
	}

	public void setSegundoTelefone(String segundoTelefone) {
		this.segundoTelefone = segundoTelefone;
	}

	public String getSegundoTipoTelefone() {
		return segundoTipoTelefone;
	}

	public void setSegundoTipoTelefone(String segundoTipoTelefone) {
		this.segundoTipoTelefone = segundoTipoTelefone;
	}

	public String getStatusDivulgacao() {
		return statusDivulgacao;
	}

	public void setStatusDivulgacao(String statusDivulgacao) {
		this.statusDivulgacao = statusDivulgacao;
	}

	public String getStatusDivulgacaoExtenso() {
		if(statusDivulgacao != null && statusDivulgacao.equals(DIVULGAVEL)){
			return DIVULGAVEL_EXTENSO;
		}
		if(statusDivulgacao != null && statusDivulgacao.equals(NAO_DIVULGAVEL)){
			return NAO_DIVULGAVEL_EXTENSO;
		}
		return null;
	}

	public void setStatusDivulgacaoExtenso(String statusDivulgacaoExtenso) {
		if(statusDivulgacaoExtenso != null && statusDivulgacaoExtenso.equals(DIVULGAVEL_EXTENSO)){
			setStatusDivulgacao(DIVULGAVEL);
		}else if(statusDivulgacaoExtenso != null && statusDivulgacaoExtenso.equals(NAO_DIVULGAVEL_EXTENSO)){
			setStatusDivulgacao(NAO_DIVULGAVEL);
		}
	}

	public boolean isDivulgavel(){
		if(statusDivulgacao == null || statusDivulgacao.equals("")){
			return false;
		}
		return (statusDivulgacao.equals(DIVULGAVEL));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((cargo == null) ? 0 : cargo.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((nome == null) ? 0 : nome.hashCode());
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
		Representante other = (Representante) obj;
		if(other.getId()!=null&&other.getId().equals(getId())){
			return true;
		}
		if (cargo == null) {
			if (other.cargo != null)
				return false;
		} else if (!cargo.equals(other.cargo))
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (nome == null) {
			if (other.nome != null)
				return false;
		} else if (!nome.equals(other.nome))
			return false;
		return true;
	}

	public String getDesinencia() {
		return desinencia;
	}

	public void setDesinencia(String desinencia) {
		this.desinencia = desinencia;
	}

	public String getVocativo() {
		return vocativo;
	}

	public void setVocativo(String vocativo) {
		this.vocativo = vocativo;
	}

	public String getPronome() {
		return pronome;
	}

	public void setPronome(String pronome) {
		this.pronome = pronome;
	}

	public String getSegundoEmail() {
		return segundoEmail;
	}

	public void setSegundoEmail(String segundoEmail) {
		this.segundoEmail = segundoEmail;
	}

}
