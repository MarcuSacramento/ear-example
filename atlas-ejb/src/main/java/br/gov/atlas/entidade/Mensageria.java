package br.gov.atlas.entidade;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.envers.NotAudited;

import br.gov.atlas.util.DataUtils;

@Table(name = "mensageria", schema="atlas")
@SequenceGenerator(name = "id_mensageria_seq", sequenceName = "id_mensageria_seq", allocationSize = 1)
@Entity
public class Mensageria extends AtlasEntidade {

	private static final long serialVersionUID = -1964767578293135L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "id_mensageria_seq")
	@Column(name = "id_mensageria") 
	private Integer id;

	@Column(name = "dta_envio")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataEnvio;

	@Column(name = "dsc_assunto")
	private String assunto;

	@Column(name = "nme_remetente")
	private String remetente;

	@Column(name = "dsc_mensagem")
	private String mensagem;
	
	@OneToMany(mappedBy = "mensagem", cascade=CascadeType.ALL, fetch = FetchType.LAZY)
	@NotAudited
	private List<Anexo> anexo;
	
	@OneToMany(mappedBy = "orgao", cascade=CascadeType.ALL, fetch = FetchType.LAZY)
	private List<MensageriaOrgao> mensagensOrgaos;
	
	@Transient
	private String nomeDestinatario;
	
	@Transient
	private String nomeOrgao;

	@Transient
	private Date dataInicio;
	
	@Transient
	private Date dataFim;
	
	@Transient
	private Integer totalDestinatarios;
	
	@Transient
	private Integer totalAbertos;
	
	@Transient
	private TipoOrgao tipoOrgao;
	
	@Transient
	private Municipio municipio;
	
	@Transient
	private UF uf;
	
	@Transient
	private String emailDestinatario;
	
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
		Mensageria other = (Mensageria) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	public String getLabel() {
		return null;
	}

	public String getDataEnvio() {
		String dtFormatada = "";
		if (dataEnvio != null) {
			dtFormatada = DataUtils.formatDateToString(dataEnvio, "dd/MM/yyyy");
		}
		return dtFormatada;
	}

	public void setDataEnvio(Date dataEnvio) {
		this.dataEnvio = dataEnvio;
	}

	public String getAssunto() {
		return assunto;
	}

	public void setAssunto(String assunto) {
		this.assunto = assunto;
	}


	public String getRemetente() {
		return remetente;
	}

	public void setRemetente(String remetente) {
		this.remetente = remetente;
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}
	
	public List<Anexo> getAnexo(){
		return anexo;
	}

	public void setAnexo(List<Anexo> idAnexo){
		this.anexo = idAnexo;
	}
	
	public List<MensageriaOrgao> getMensagensOrgaos() {
		return mensagensOrgaos;
	}

	public void setMensagensOrgaos(List<MensageriaOrgao> destinatarios) {
		this.mensagensOrgaos = destinatarios;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	public Integer getTotalDestinatarios() {
		return totalDestinatarios;
	}

	public void setTotalDestinatarios(Integer totalDestinatarios) {
		this.totalDestinatarios = totalDestinatarios;
	}

	public Integer getTotalAbertos() {
		return totalAbertos;
	}

	public void setTotalAbertos(Integer totalAbertos) {
		this.totalAbertos = totalAbertos;
	}
	
	public String getNomeDestinatario() {
		return nomeDestinatario;
	}

	public void setNomeDestinatario(String nomeDestinatario) {
		this.nomeDestinatario = nomeDestinatario;
	}

	public String getNomeOrgao() {
		return nomeOrgao;
	}

	public void setNomeOrgao(String nomeOrgao) {
		this.nomeOrgao = nomeOrgao;
	}
	
	public TipoOrgao getTipoOrgao() {
		return tipoOrgao;
	}

	public void setTipoOrgao(TipoOrgao tipoOrgao) {
		this.tipoOrgao = tipoOrgao;
	}

	public String getEmailDestinatario() {
		return emailDestinatario;
	}

	public void setEmailDestinatario(String emailDestinatario) {
		this.emailDestinatario = emailDestinatario;
	}

	public Municipio getMunicipio() {
		return municipio;
	}

	public void setMunicipio(Municipio municipio) {
		this.municipio = municipio;
	}

	public UF getUf() {
		return uf;
	}

	public void setUf(UF uf) {
		this.uf = uf;
	}
	
}
