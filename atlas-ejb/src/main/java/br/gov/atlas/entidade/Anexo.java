package br.gov.atlas.entidade;

import java.text.DecimalFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Table(name = "anexo", schema="atlas")
@SequenceGenerator(name = "id_anexo_seq", sequenceName = "id_anexo_seq", allocationSize = 1)
@Entity
public class Anexo extends AtlasEntidade {

	private static final long serialVersionUID = -7775592923988187735L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "id_anexo_seq")
	@Column(name = "id_anexo")
	private Integer id;

	@Column(name = "nme_anexo")
	private String nome;
	
	@Column(name = "dsc_mimetype")
	private String mimetype;
	
	@Column(name = "num_tamanho")
	private Double tamanho;

	@Column(name = "arquivo")
	@Lob
	byte[] arquivo;

	@ManyToOne
	@JoinColumn(name="id_mensageria", insertable =  true, updatable = true, nullable = false)
	private Mensageria mensagem;
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj){
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Anexo other = (Anexo) obj;
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

	public Mensageria getMensagem() {
		return mensagem;
	}

	public void setMensagem(Mensageria mensagem) {
		this.mensagem = mensagem;
	}
	
	public String getNome(){
		return nome;
	}

	public void setNome( String nome){
		this.nome = nome;
	}
	
	public String getMimetype() {
		return mimetype;
	}

	public void setMimetype(String mimetype) {
		this.mimetype = mimetype;
	}

	public Double getTamanho() {
		return tamanho;
	}

	public void setTamanho(Double tamanho) {
    	DecimalFormat df = new DecimalFormat("########.00");
		this.tamanho = Double.valueOf(df.format(tamanho.doubleValue()).replace(",", "."));
	}

	public byte[] getArquivo(){
		return arquivo;
	}
	
	public void setArquivo(byte[] arquivo) {
		this.arquivo = arquivo;
	}
}
