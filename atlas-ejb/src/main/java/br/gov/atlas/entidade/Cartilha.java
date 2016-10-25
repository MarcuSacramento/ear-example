package br.gov.atlas.entidade;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;

@Table(name = "atlas.cartilha")
@SequenceGenerator(name = "atlas.cartilha_id_cartilha_seq", sequenceName = "atlas.cartilha_id_cartilha_seq", allocationSize = 1)
@Entity
@Audited
@AuditTable(schema="auditoria", value="cartilha_aud")
public class Cartilha extends AtlasEntidade {

	private static final long serialVersionUID = 4486480859586130500L;

	@Id
	@Column(name = "id_cartilha")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "atlas.cartilha_id_cartilha_seq")
	private Integer id;

	@Column(name = "nme_titulo_cartilha")
	private String titulo;

	@Column(name = "lnk_imagem_cartilha")
	private String linkDaImagem;

	@Column(name = "lnk_arquivo_cartilha")
	private String linkDoArquivoFisico;

	@Column(name = "ind_consumidor")
	private String indicadorConsumidor;

	@Column(name = "byte_arquivo")
	@Lob
	private byte[] byteArquivo;

	@Column(name = "byte_arquivo_cartilha")
	@Lob
	private byte[] byteArquivoCartilha;

	@Column(name = "nme_arquivo_cartilha")
	private String nomeArquivoCartilha;

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getLinkDaImagem() {
		return linkDaImagem;
	}

	public void setLinkDaImagem(String linkDaImagem) {
		this.linkDaImagem = linkDaImagem;
	}

	public String getLinkDoArquivoFisico() {
		return linkDoArquivoFisico;
	}

	public void setLinkDoArquivoFisico(String linkDoArquivoFisico) {
		this.linkDoArquivoFisico = linkDoArquivoFisico;
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
		Cartilha other = (Cartilha) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String getLabel() {
		return this.titulo;
	}

	public String getIndicadorConsumidor() {
		return indicadorConsumidor;
	}

	public void setIndicadorConsumidor(String indicadorConsumidor) {
		this.indicadorConsumidor = indicadorConsumidor;
	}

	public byte[] getByteArquivoCartilha() {
		return byteArquivoCartilha;
	}

	public void setByteArquivoCartilha(byte[] byteArquivoCartilha) {
		this.byteArquivoCartilha = byteArquivoCartilha;
	}

	@Transient
	public boolean isArquivoEmLink(){
		return getLinkDoArquivoFisico() != null
				&& !getLinkDoArquivoFisico().trim().equals("");
	}

	public String getNomeArquivoCartilha() {
		return nomeArquivoCartilha;
	}

	public void setNomeArquivoCartilha(String nomeArquivoCartilha) {
		this.nomeArquivoCartilha = nomeArquivoCartilha;
	}

}
