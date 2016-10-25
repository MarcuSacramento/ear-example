package br.gov.atlas.cargaTemporaria.entidade;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonProperty;

@XmlRootElement
public class OABSeccional {

	private String nome;
	private String siglaUF;
	private String enderecoLocal;
	private String codCidade;
	private String bairro;
	private String EnderacoLogadroro;
	private String cep;
	private String telefone1;
	private String telefone2;
	private String fax1;
	private String fax2;
	private String paginaWeb;
	private String email;
	private Long totalAdvogadosInscritos;
	private Long totalEstagiariosInscritos;
	private Long totalSuplementaresInscritos;

	public String getNome() {
		return nome;
	}

	@JsonProperty("Nome")
	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getSiglaUF() {
		return siglaUF;
	}

	@JsonProperty("SiglaUF")
	public void setSiglaUF(String siglaUF) {
		this.siglaUF = siglaUF;
	}

	public String getEnderecoLocal() {
		return enderecoLocal;
	}

	@JsonProperty("EnderecoLocal")
	public void setEnderecoLocal(String enderecoLocal) {
		this.enderecoLocal = enderecoLocal;
	}

	public String getCodCidade() {
		return codCidade;
	}

	@JsonProperty("CodCidade")
	public void setCodCidade(String codCidade) {
		this.codCidade = codCidade;
	}

	public String getBairro() {
		return bairro;
	}

	@JsonProperty("Bairro")
	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	public String getCep() {
		return cep;
	}

	@JsonProperty("Cep")
	public void setCep(String cep) {
		this.cep = cep;
	}

	public String getTelefone1() {
		return telefone1;
	}

	@JsonProperty("Telefone1")
	public void setTelefone1(String telefone1) {
		this.telefone1 = telefone1;
	}

	public String getTelefone2() {
		return telefone2;
	}

	@JsonProperty("Telefone2")
	public void setTelefone2(String telefone2) {
		this.telefone2 = telefone2;
	}

	public String getFax1() {
		return fax1;
	}

	@JsonProperty("Fax1")
	public void setFax1(String fax1) {
		this.fax1 = fax1;
	}

	public String getFax2() {
		return fax2;
	}

	@JsonProperty("Fax2")
	public void setFax2(String fax2) {
		this.fax2 = fax2;
	}

	public String getPaginaWeb() {
		return paginaWeb;
	}

	@JsonProperty("PaginaWeb")
	public void setPaginaWeb(String paginaWeb) {
		this.paginaWeb = paginaWeb;
	}

	public String getEmail() {
		return email;
	}

	@JsonProperty("Email")
	public void setEmail(String email) {
		this.email = email;
	}

	public String getEnderacoLogadroro() {
		return EnderacoLogadroro;
	}

	@JsonProperty("EnderacoLogadroro")
	public void setEnderacoLogadroro(String enderacoLogadroro) {
		EnderacoLogadroro = enderacoLogadroro;
	}

	public Long getTotalAdvogadosInscritos() {
		return totalAdvogadosInscritos;
	}

	public void setTotalAdvogadosInscritos(Long totalAdvogadosInscritos) {
		this.totalAdvogadosInscritos = totalAdvogadosInscritos;
	}

	public Long getTotalEstagiariosInscritos() {
		return totalEstagiariosInscritos;
	}

	public void setTotalEstagiariosInscritos(Long totalEstagiariosInscritos) {
		this.totalEstagiariosInscritos = totalEstagiariosInscritos;
	}

	public Long getTotalSuplementaresInscritos() {
		return totalSuplementaresInscritos;
	}

	public void setTotalSuplementaresInscritos(Long totalSuplementaresInscritos) {
		this.totalSuplementaresInscritos = totalSuplementaresInscritos;
	}

	

}
