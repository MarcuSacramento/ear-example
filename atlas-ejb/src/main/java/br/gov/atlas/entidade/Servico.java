package br.gov.atlas.entidade;

import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Table(name="atlas.servico")
@SequenceGenerator(name="atlas.servico_id_servico_seq", sequenceName="atlas.servico_id_servico_seq", allocationSize=1)
@Entity
public class Servico extends AtlasEntidade {


	private static final long serialVersionUID = -1232136726655573195L;

	@Id
	@Column(name="id_servico")
	private Integer id;

	@Column(name="nme_servico")
	private
	String nome;

	@Column(name="dta_atualizacao")
	private Date dataAtualizacao;

	@Column(name="dta_cadastro")
	private Date dataCadastro;

	@ManyToMany(mappedBy = "servicos")
	private Set<Orgao> orgaos;

	@ManyToMany(mappedBy = "servicos")
	private Set<TipoOrgao> tiposOrgao;

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Date getDataAtualizacao() {
		return dataAtualizacao;
	}

	public void setDataAtualizacao(Date dataAtualizacao) {
		this.dataAtualizacao = dataAtualizacao;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	@Override
	public String getLabel() {
		return null;
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
		Servico other = (Servico) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public Set<TipoOrgao> getTiposOrgao() {
		return tiposOrgao;
	}

	public void setTiposOrgao(Set<TipoOrgao> tiposOrgao) {
		this.tiposOrgao = tiposOrgao;
	}

}