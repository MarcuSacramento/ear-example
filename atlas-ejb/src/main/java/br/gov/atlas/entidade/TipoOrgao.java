package br.gov.atlas.entidade;

import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.envers.NotAudited;

import br.gov.atlas.dto.TipoOrgaoProfissaoDTO;

@Table(name = "tipo_orgao", schema="atlas")
@SequenceGenerator(name = "tipo_orgao_id_tipo_orgao_seq", sequenceName = "tipo_orgao_id_tipo_orgao_seq", allocationSize = 1)
@Entity
public class TipoOrgao extends AtlasEntidade {

	private static final long serialVersionUID = -7099032783924116995L;

	public static final Integer ID_TIPO_PROCON = new Integer(7);

	@Id
	@Column(name = "id_tipo_orgao")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tipo_orgao_id_tipo_orgao_seq")
	private Integer id;

	@Column(name = "nme_tipo_orgao")
	private String nome;

	@Column(name = "dsc_tipo_orgao")
	private String descricao;

	@OneToMany(mappedBy = "tipoOrgao", fetch = FetchType.LAZY)
	private Set<Orgao> orgaos;

	@ManyToOne
	@JoinColumn(name = "id_ramo")
	private Ramo ramo;

	@ManyToMany(fetch = FetchType.EAGER)
	@Fetch(FetchMode.SUBSELECT)
	@JoinTable(name = "atlas.servico_tipo_orgao",
	joinColumns = { @JoinColumn(name = "id_tipo_orgao", nullable = false, updatable = false) },
	inverseJoinColumns = { @JoinColumn(name = "id_servico", nullable = false, updatable = false) })
	@NotAudited
	private List<Servico> servicos;

	@Transient
	private List<TipoOrgaoProfissaoDTO> operadores;

	public TipoOrgao() {
	}

	public TipoOrgao(Integer id, String nome) {
		super();
		this.id = id;
		this.nome = nome;
	}

	public TipoOrgao(Integer id, String nome, Ramo ramo) {
		super();
		this.id = id;
		this.nome = nome;
		this.ramo = ramo;
	}

	public Ramo getRamo() {
		return ramo;
	}

	public void setRamo(Ramo ramo) {
		this.ramo = ramo;
	}

	public Set<Orgao> getOrgaos() {
		return orgaos;
	}

	public void setOrgaos(Set<Orgao> orgaos) {
		this.orgaos = orgaos;
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

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
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
		TipoOrgao other = (TipoOrgao) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public List<TipoOrgaoProfissaoDTO> getOperadores() {
		return operadores;
	}

	public void setOperadores(List<TipoOrgaoProfissaoDTO> operadores) {
		this.operadores = operadores;
	}

	public List<Servico> getServicos() {
		return servicos;
	}

	public void setServicos(List<Servico> servicos) {
		this.servicos = servicos;
	}



}
