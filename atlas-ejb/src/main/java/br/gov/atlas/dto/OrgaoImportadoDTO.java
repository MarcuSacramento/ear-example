package br.gov.atlas.dto;

import java.util.LinkedList;

import br.gov.atlas.entidade.Cargo;
import br.gov.atlas.entidade.Municipio;
import br.gov.atlas.entidade.TipoOrgao;
import br.gov.atlas.visitor.Visitable;
import br.gov.atlas.visitor.Visitor;

/**
 * Classe utilitária utilizada na importação de órgão
 * 
 * @author Vinícius Rabelo
 * 
 */
public class OrgaoImportadoDTO implements Visitable {

	public static final int INDEX_COLUNA_MUNICIPIO = 0;
	public static final int INDEX_COLUNA_UF = 1;
	public static final int INDEX_COLUNA_TIPO_ORGAO = 2;
	public static final int INDEX_COLUNA_SIGLA_ORGAO = 3;
	public static final int INDEX_COLUNA_NOME_ORGAO = 4;
	public static final int INDEX_COLUNA_DESCRICAO_ORGAO = 5;
	public static final int INDEX_COLUNA_ENDEREÇO = 6;
	public static final int INDEX_COLUNA_CEP = 7;
	public static final int INDEX_COLUNA_HOMEPAGE = 8;
	public static final int INDEX_COLUNA_NOME_BAIRRO = 9;
	public static final int INDEX_COLUNA_NOME_REPRESENTANTE = 10;
	public static final int INDEX_COLUNA_EMAIL_REPRESENTANTE = 11;
	public static final int INDEX_COLUNA_CARGO_REPRESENTANTE = 12;
	public static final int INDEX_COLUNA_DDD_TELEFONE_01_REPRESENTANTE = 13;
	public static final int INDEX_COLUNA_TELEFONE_01_REPRESENTANTE = 14;
	public static final int INDEX_COLUNA_IDENTIFICADOR_TELEFONE_01 = 15;
	public static final int INDEX_COLUNA_DDD_TELEFONE_02_REPRESENTANTE = 16;
	public static final int INDEX_COLUNA_TELEFONE_02_REPRESENTANTE = 17;
	public static final int INDEX_COLUNA_IDENTIFICADOR_TELEFONE_02 = 18;
	public static final int INDEX_COLUNA_RESULTADO_IMPORTACAO = 19;

	private LinkedList<Object> linhaOrgao;

	private int numeroLinha;
	private Municipio municipio;
	private TipoOrgao tipoOrgao;
	private Cargo cargo;

	private LinkedList<String> visitorMessages = new LinkedList<String>();

	public OrgaoImportadoDTO(LinkedList<Object> linhaOrgao) {
		this.linhaOrgao = linhaOrgao;
	}

	public String getNomeMunicipio() {
		return (String) linhaOrgao.get(INDEX_COLUNA_MUNICIPIO);
	}

	public String getSiglaUF() {
		return (String) linhaOrgao.get(INDEX_COLUNA_UF);
	}

	public String getNomeTipoOrgao() {
		return (String) linhaOrgao.get(INDEX_COLUNA_TIPO_ORGAO);
	}

	public String getSiglaOrgao() {
		return (String) linhaOrgao.get(INDEX_COLUNA_SIGLA_ORGAO);
	}

	public String getNomeOrgao() {
		return (String) linhaOrgao.get(INDEX_COLUNA_NOME_ORGAO);
	}

	public String getDescricaoOrgao() {
		return (String) linhaOrgao.get(INDEX_COLUNA_DESCRICAO_ORGAO);
	}

	public String getEnderecoOrgao() {
		return (String) linhaOrgao.get(INDEX_COLUNA_ENDEREÇO);
	}

	public String getCepOrgao() {
		return (String) linhaOrgao.get(INDEX_COLUNA_CEP);
	}

	public String getHomepage() {
		return (String) linhaOrgao.get(INDEX_COLUNA_HOMEPAGE);
	}

	//	public String getLatitude() {
	//		return (String) linhaOrgao.get(INDEX_COLUNA_LATITUDE);
	//	}
	//
	//	public String getLongitude() {
	//		return (String) linhaOrgao.get(INDEX_COLUNA_LONGITUDE);
	//	}

	public String getBairro() {
		return (String) linhaOrgao.get(INDEX_COLUNA_NOME_BAIRRO);
	}

	public String getNomeRepresentante() {
		return (String) linhaOrgao.get(INDEX_COLUNA_NOME_REPRESENTANTE);
	}

	public String getEmailRepresentante() {
		return (String) linhaOrgao.get(INDEX_COLUNA_EMAIL_REPRESENTANTE);
	}

	public String getNomeCargoRepresentante() {
		return (String) linhaOrgao.get(INDEX_COLUNA_CARGO_REPRESENTANTE);
	}

	public String getDddTelefone01Representante() {
		return (String) linhaOrgao.get(INDEX_COLUNA_DDD_TELEFONE_01_REPRESENTANTE);
	}

	public String getTelefone01Representante() {
		return (String) linhaOrgao.get(INDEX_COLUNA_TELEFONE_01_REPRESENTANTE);
	}

	public String getIdentificadorTelefone01Representante() {
		return (String) linhaOrgao.get(INDEX_COLUNA_IDENTIFICADOR_TELEFONE_01);
	}

	public String getDddTelefone02Representante() {
		return (String) linhaOrgao
				.get(INDEX_COLUNA_DDD_TELEFONE_02_REPRESENTANTE);
	}

	public String getTelefone02Representante() {
		return (String) linhaOrgao.get(INDEX_COLUNA_TELEFONE_02_REPRESENTANTE);
	}

	public String getIdentificadorTelefone02Representante() {
		return (String) linhaOrgao.get(INDEX_COLUNA_IDENTIFICADOR_TELEFONE_02);
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}

	public LinkedList<String> getVisitorMessages() {
		return visitorMessages;
	}

	public void setVisitorMessages(LinkedList<String> visitorMessages) {
		this.visitorMessages = visitorMessages;
	}

	public String getErrosValidacao() {
		StringBuilder sb = new StringBuilder();
		if(!visitorMessages.isEmpty()){
			for (String mensagem : visitorMessages) {
				sb.append(mensagem).append(", ");
			}
			// remove a última vírgula
			sb.delete(sb.length() - 2, sb.length());
			sb.append(".");
		}
		return sb.toString();
	}

	public Municipio getMunicipio() {
		return municipio;
	}

	public void setMunicipio(Municipio municipio) {
		this.municipio = municipio;
	}

	public TipoOrgao getTipoOrgao() {
		return tipoOrgao;
	}

	public void setTipoOrgao(TipoOrgao tipoOrgao) {
		this.tipoOrgao = tipoOrgao;
	}

	public int getNumeroLinha() {
		return numeroLinha;
	}

	public void setNumeroLinha(int numeroLinha) {
		this.numeroLinha = numeroLinha;
	}

	public Cargo getCargo() {
		return cargo;
	}

	public void setCargo(Cargo cargo) {
		this.cargo = cargo;
	}
}
