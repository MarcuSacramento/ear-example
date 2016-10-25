package br.gov.atlas.servico;

import javax.ejb.Local;
import javax.naming.NamingException;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import br.gov.atlas.dto.TermoBuscaDTO;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;

@Local
public interface WebServicesServico
{	
	
	public Response listarUfs() throws AtlasAcessoBancoDadosException, NamingException;
	
	public Response listarMunicipiosDaUf(@PathParam("idUf") int idUf) throws AtlasAcessoBancoDadosException, NamingException;	

	public Response listarTiposDeOrgaosDoMunicipio(@PathParam("idMunicipio") int idMunicipio) throws AtlasAcessoBancoDadosException, NamingException ;
	
	public Response listarTiposDeOrgaosDoMunicipio(@PathParam("idMunicipio") int idMunicipio, @PathParam("idTipoOrgao") int idTipoOrgao) throws AtlasAcessoBancoDadosException, NamingException;
	
	public Response listarVideos() throws AtlasAcessoBancoDadosException, NamingException;
	
	public Response listarPublicacoes() throws AtlasAcessoBancoDadosException, NamingException;
	
	public Response listarGlossarios(@PathParam("termo") String termo) throws AtlasAcessoBancoDadosException, NamingException;

	public Response listarTermos(@PathParam("letra") String letra) throws AtlasAcessoBancoDadosException, NamingException;
	
	public Response informarProblema(@PathParam("idOrgao") int idOrgao, 
									 @PathParam("idTipoProblema") int idTipoProblema, 
									 @PathParam("dscProblema") String dscProblema) throws AtlasAcessoBancoDadosException, NamingException;
	
	public Response listarTemas() throws AtlasAcessoBancoDadosException, NamingException;
	
	public Response listarOrgaosPorTema(@PathParam("idMunicipio") int idMunicipio, @PathParam("idTema") int idTema) throws AtlasAcessoBancoDadosException, NamingException ;

	public Response listarMunicipiosDaUf(@PathParam("sgUf") String sgUf) throws AtlasAcessoBancoDadosException, NamingException;

    public Response salvarTermoBusca(TermoBuscaDTO termoBuscaDTO) throws AtlasAcessoBancoDadosException, NamingException;        
    
    public Response listaTermosBuscaMaisUtilizados(int limite) throws AtlasAcessoBancoDadosException, NamingException;            
}