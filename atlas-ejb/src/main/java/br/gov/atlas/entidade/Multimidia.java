package br.gov.atlas.entidade;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;

import br.gov.atlas.util.DataUtils;

@Table(name = "atlas.multimidia")
@SequenceGenerator(name = "atlas.multimidia_id_multimidia_seq", sequenceName = "atlas.multimidia_id_multimidia_seq", allocationSize = 1)
@Entity
@Audited
@AuditTable(schema="auditoria", value="multimidia_aud")
public class Multimidia extends AtlasEntidade {

	private static final long serialVersionUID = -6867513766077541721L;

	@Id
	@Column(name = "id_multimidia")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "atlas.multimidia_id_multimidia_seq")
	private Integer id;

	@Column(name = "nme_multimidia")
	String nome;

	@Column(name = "dsc_multimidia")
	String descricao;

	@Column(name = "nme_recurso")
	String recurso;

	@Column(name = "url_recurso")
	String url;

	@Column(name = "ind_tipo_recurso")
	String tipoRecurso;

	@Column(name = "dta_publicacao")
	Date dataPublicacao;
	
	@Column(name = "ind_tela_inicial")
	String telaInicial;
	
	@Column(name = "byte_arquivo")
	@Lob
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

	public String getDescricao() {
		return descricao;
	}
	
	public String getDescricaoAbreviada() {
		if( descricao!=null && descricao.length() > 100 )
			return descricao.substring(0,99) + "[...]";
		else
			return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getRecurso() {
		return recurso;
	}

	public void setRecurso(String recurso) {
		this.recurso = recurso;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTipoRecurso() {
		return tipoRecurso;
	}

	public void setTipoRecurso(String tipoRecurso) {
		this.tipoRecurso = tipoRecurso;
	}

	public Date getDataPublicacao() {
		return dataPublicacao;
	}
	
	public byte[] getByteArquivo() {
		return byteArquivo;
	}

	public void setByteArquivo(byte[] byteArquivo) {
		this.byteArquivo = byteArquivo;
	}

	public String getDataPublicacaoFormatada() {
		String dtFormatada = "";
		if (dataPublicacao != null) {
			dtFormatada = DataUtils.formatDateToString(dataPublicacao,
					"dd/MM/yyyy");
		}
		return dtFormatada;
	}

	public void setDataPublicacao(Date dataPublicacao) {
		this.dataPublicacao = dataPublicacao;
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
		Multimidia other = (Multimidia) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public String getTelaInicial() {
		return telaInicial;
	}

	public void setTelaInicial(String telaInicial) {
		this.telaInicial = telaInicial;
	}
	
	public Boolean getTelaInicialBoolean() {
		if(telaInicial==null || telaInicial.equals("N")){
			return false;
		} else{
			return true;
		}
	}

	public void setTelaInicialBoolean(Boolean telaInicial) {
		if(telaInicial==null || telaInicial==false){
			this.telaInicial = "N";
		} else{
			this.telaInicial="S";
		}
	}
}
