package br.gov.atlas.dto;

import java.math.BigInteger;

public class TotalizadorOrgaoDTO {

	private Integer id;

	private Integer idNoPai;

	private BigInteger total;

	private String nomeTotalizador;

	private NivelTotalizadorOrgao nivel;

	public TotalizadorOrgaoDTO() {

	}

	public TotalizadorOrgaoDTO(Integer id, Integer idNoPai, BigInteger total,
			String nomeTotalizador, NivelTotalizadorOrgao nivel) {
		setId(id);
		setIdNoPai(idNoPai);
		setTotal(total);
		setNomeTotalizador(nomeTotalizador);
		setNivel(nivel);
	}

	public TotalizadorOrgaoDTO(Integer id, BigInteger total,
			String nomeTotalizador, NivelTotalizadorOrgao nivel) {
		setId(id);
		setTotal(total);
		setNomeTotalizador(nomeTotalizador);
		setNivel(nivel);
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public BigInteger getTotal() {
		return total;
	}

	public void setTotal(BigInteger total) {
		this.total = total;
	}

	public String getNomeTotalizador() {
		return nomeTotalizador;
	}

	public void setNomeTotalizador(String nomeTotalizador) {
		this.nomeTotalizador = nomeTotalizador;
	}

	public NivelTotalizadorOrgao getNivel() {
		return nivel;
	}

	public void setNivel(NivelTotalizadorOrgao nivel) {
		this.nivel = nivel;
	}

	public Integer getIdNoPai() {
		return idNoPai;
	}

	public void setIdNoPai(Integer idNoPai) {
		this.idNoPai = idNoPai;
	}

	public enum NivelTotalizadorOrgao {
		BRASIL(1), REGIAO(2), ESTADO(3), TIPO_ORGAO(4);

		private Integer codigo;

		private NivelTotalizadorOrgao(Integer codigo) {
			this.setCodigo(codigo);
		}

		public Integer getCodigo() {
			return codigo;
		}

		public void setCodigo(Integer codigo) {
			this.codigo = codigo;
		}
	}

}