package br.gov.atlas.entidade;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Table(name = "atlas.parceiro")
@SequenceGenerator(name = "atlas.parceiro_id_parceiro_seq", sequenceName = "atlas.parceiro_id_parceiro_seq", allocationSize = 1)
@Entity
public class Parceiro extends AtlasEntidade {

	private static final long serialVersionUID = 4486480859586130500L;
	public static String SIT_ATIVO   = "A";
	public static String SIT_INATIVO = "I";

	@Id
	@Column(name = "id_parceiro")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "atlas.parceiro_id_parceiro_seq")
	private Integer id;

	@Column(name = "nme_parceiro")
	String nome;

	@Column(name = "url_imagem_parceiro")
	String urlImagem;

	@Column(name = "nme_homepage")
	String homepage;

	@Column(name = "nme_responsavel")
	String nomeResponsavel;
	
	@Column(name = "nme_email")
	private String email;
	
	@Column(name = "nu_ddd")
	private String ddd;

	@Column(name = "nu_telefone")
	private String telefone;
	
	@Column(name = "ind_ativo")
	private String situacao;
	
	@Lob
	@Column(name = "byte_arquivo")
    byte[] byteArquivo; 


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
		return this.nome;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getUrlImagem() {
		return urlImagem;
	}

	public void setUrlImagem(String urlImagem) {
		this.urlImagem = urlImagem;
	}

	public String getHomepage() {
		return homepage;
	}
	
	public String getHomepageComProtocolo() {
		if(homepage==null || homepage.startsWith("http")){
			return homepage;
		} else{
			return "http://"+homepage;
		}
	}

	public void setHomepage(String homepage) {
		this.homepage = homepage;
	}

	public String getNomeResponsavel() {
		return nomeResponsavel;
	}

	public void setNomeResponsavel(String nomeResponsavel) {
		this.nomeResponsavel = nomeResponsavel;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getDdd() {
		return ddd;
	}
	
	public void setDdd(String ddd) {
		this.ddd = ddd;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public String getSituacao() {
		return situacao;
	}
	
	public String getSituacaoFormatada() {
		if (SIT_ATIVO.equals(getSituacao())){
			situacao = "Ativo";
		}else {
			situacao = "Inativo";
		}
		return situacao;
	}

	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}

	public byte[] getByteArquivo() {
		return byteArquivo;
	}

	public void setByteArquivo(byte[] byteArquivo) {
		this.byteArquivo = byteArquivo;
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
		Parceiro other = (Parceiro) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
