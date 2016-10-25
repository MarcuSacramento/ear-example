package br.gov.atlas.entidade;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Table(name="atlas.termo_busca")
@SequenceGenerator(name="atlas.termo_busca_id_termo_busca_seq", sequenceName="atlas.termo_busca_id_termo_busca_seq", allocationSize=1)
@Entity
public class Termo extends AtlasEntidade {


	private static final long serialVersionUID = 6545894070618763442L;

	@Id
	@Column(name="id_termo_busca")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="atlas.termo_busca_id_termo_busca_seq")
	private Integer id;

	@Column(name="nme_termo_busca")
	private	String nome;

	@Column(name="dta_criacao")
	private Date dataCriacao;

	@Column(name="dta_atualizacao")
	private Date dataAtualizacao;

	@Column(name="dta_bloqueio")
	private Date dataBloqueio;

	@Column(name="qtd_termo_busca")
	private Integer qtdTermoBusca;

	@Column(name = "sit_localizado")
	private Boolean sitLocalizado = Boolean.TRUE;

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
		Termo other = (Termo) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Date getDataCriacao() {
		return dataCriacao;
	}

	public void setDataCriacao(Date dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	public Date getDataAtualizacao() {
		return dataAtualizacao;
	}

	public void setDataAtualizacao(Date dataAtualizacao) {
		this.dataAtualizacao = dataAtualizacao;
	}

	public Date getDataBloqueio() {
		return dataBloqueio;
	}

	public void setDataBloqueio(Date dataBloqueio) {
		this.dataBloqueio = dataBloqueio;
	}

	public Boolean getSitLocalizado() {
		if (sitLocalizado == null) {
			sitLocalizado = Boolean.TRUE;
		}
		return sitLocalizado;
	}

	public void setSitLocalizado(Boolean sitLocalizado) {
		this.sitLocalizado = sitLocalizado;
	}

	public Integer getQtdTermoBusca() {
		return qtdTermoBusca;
	}

	public void setQtdTermoBusca(Integer qtdTermoBusca) {
		this.qtdTermoBusca = qtdTermoBusca;
	}

}