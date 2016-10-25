package br.gov.atlas.entidade;

import java.text.ParseException;

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
import javax.swing.text.MaskFormatter;

@Table(name = "telefone", schema = "atlas")
@Inheritance(strategy = InheritanceType.JOINED)
@SequenceGenerator(name = "telefone_id_telefone_seq", sequenceName = "telefone_id_telefone_seq", allocationSize = 1)
@Entity
public class Telefone extends AtlasEntidade {

	private static final long serialVersionUID = -5927350893754460246L;

	@Id
	@Column(name = "id_telefone")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "telefone_id_telefone_seq")
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "id_Orgao")
	private Orgao orgao;

	@Column(name = "nu_ddd")
	private String ddd;

	@Column(name = "nu_telefone")
	private String numeroTelefone;

	public Telefone() {
	}

	public Telefone(Integer id, Orgao orgao, String ddd, String numeroTelefone,
			String tipoTelefone) {
		super();
		this.id = id;
		this.orgao = orgao;
		this.ddd = ddd;
		this.numeroTelefone = numeroTelefone;
		this.tipoTelefone = tipoTelefone;
	}


	@Column(name = "ind_tipo_telefone")
	private String tipoTelefone;

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
		// TODO Auto-generated method stub
		return null;
	}

	public String getDdd() {
		return ddd;
	}

	public void setDdd(String ddd) {
		this.ddd = ddd;
	}

	public String getNumeroTelefone() {
		return numeroTelefone;
	}

	public void setNumeroTelefone(String numeroTelefone) {
		if( numeroTelefone != null )
			this.numeroTelefone = numeroTelefone.replaceAll("\\D+","");
	}

	public String getTipoTelefone() {
		return tipoTelefone;
	}

	public void setTipoTelefone(String tipoTelefone) {
		this.tipoTelefone = tipoTelefone;
	}

	public Orgao getOrgao() {
		return orgao;
	}

	public void setOrgao(Orgao orgao) {
		this.orgao = orgao;
	}

	public String getTelefoneFormatado() throws ParseException {
		MaskFormatter mask;
		if (getNumeroTelefone().length() == 9) {
			mask = new MaskFormatter("#####-####");
		} else {
			mask = new MaskFormatter("####-####");
		}
		mask.setValueContainsLiteralCharacters(false);
		return "(" + this.getDdd() + ") "
		+ mask.valueToString(getNumeroTelefone());
	}


	public static void main(String[] args) throws ParseException
	{
		MaskFormatter mask;
		mask = new MaskFormatter("####-#####");
		mask.setValueContainsLiteralCharacters(false);

		String originalString = "2222-44444";
		String finalString    = originalString.replaceAll("\\D+","");

		String valorFinal = mask.valueToString(finalString);

		System.out.println(valorFinal);
	}

}
