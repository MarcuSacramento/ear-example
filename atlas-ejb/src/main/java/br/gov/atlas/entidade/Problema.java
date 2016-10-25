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

@Table(name = "atlas.problema")
@Inheritance(strategy = InheritanceType.JOINED)
@SequenceGenerator(name = "atlas.problema_id_problema_seq", sequenceName = "atlas.problema_id_problema_seq", allocationSize = 1)
@Entity
public class Problema extends AtlasEntidade {

	private static final long serialVersionUID = 8781402405277258134L;

	@Id
	@Column(name = "id_problema")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "atlas.problema_id_problema_seq")
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "id_orgao")
	private Orgao orgao;

	@Column(name = "ind_tipo_problema")
	private String tipoProblema;

	@Column(name = "dsc_problema")
	private String descricao;
	
	@Column(name = "dsc_observacao")
	private String observacao;
	
	@Column(name = "ind_status_problema")
	private String statusProblema;

	@Override
	public Integer getId() {
		return null;
	}

	@Override
	public void setId(Integer id) {
	}

	@Override
	public String getLabel() {
		return null;
	}

	public Orgao getOrgao() {
		return orgao;
	}

	public void setOrgao(Orgao orgao) {
		this.orgao = orgao;
	}

	public String getTipoProblema() {
		return tipoProblema;
	}

	public void setTipoProblema(String tipoProblema) {
		this.tipoProblema = tipoProblema;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getTipoProblemaFormatado() {
		if (getTipoProblema() != null) {
			if (getTipoProblema().equals("1")) {
				return "Endereço incorreto";
			} else if (getTipoProblema().equals("2")) {
				return "Localização geográfica incorreta (Latitute/Longitude)";
			} else if (getTipoProblema().equals("3")) {
				return "Outros";
			} else{
				return "";
			}
		} else {
			return "";
		}
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public String getStatusProblema() {
		return statusProblema;
	}

	public void setStatusProblema(String statusProblema) {
		this.statusProblema = statusProblema;
	}
	
	public boolean isProblemaResolvido(){
		return (statusProblema != null && statusProblema.equals("S"));
	}
}
