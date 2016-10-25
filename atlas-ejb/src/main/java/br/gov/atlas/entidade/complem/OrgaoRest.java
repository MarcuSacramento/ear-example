package br.gov.atlas.entidade.complem;

import java.math.BigDecimal;
import java.util.List;

public class OrgaoRest {

	public Integer id;
	public String  sigla;
	public String  nome;
	public String endereco;
	public String homePage;
	public Integer cep;
	public BigDecimal latitude;
	public BigDecimal longitude; 
	public List<TelefoneRest> telefones;
	public List<RepresentanteRest> representantes;
}