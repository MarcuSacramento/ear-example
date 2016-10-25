package br.gov.atlas.restful;

import javax.faces.bean.ApplicationScoped;
import javax.inject.Inject;
import javax.naming.NamingException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import br.gov.atlas.dto.TermoBuscaDTO;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;
import br.gov.atlas.servico.WebServicesServico;

@Path("/json")
@ApplicationScoped
public class RestFullWebServices {

    @Inject
    private WebServicesServico servico;

    @GET
    @Path("/ufs")
    @Produces("application/json")
    public Response listarUfs() throws AtlasAcessoBancoDadosException, NamingException {
        return servico.listarUfs();
    }

    @GET
    @Path("/municipios/{idUf}")
    @Produces("application/json")
    public Response listarMunicipiosDaUf(@PathParam("idUf") int idUf) throws AtlasAcessoBancoDadosException, NamingException {
        return servico.listarMunicipiosDaUf(idUf);
    }

    @GET
    @Path("/municipios/sigla/{sgUf}")
    @Produces("application/json")
    public Response listarMunicipiosDaUf(@PathParam("sgUf") String sgUf) throws AtlasAcessoBancoDadosException, NamingException {
        return servico.listarMunicipiosDaUf(sgUf);
    }

    @GET
    @Path("/tiposDeOrgaos/{idMunicipio}")
    @Produces("application/json")
    public Response listarTiposDeOrgaosDoMunicipio(@PathParam("idMunicipio") int idMunicipio) throws AtlasAcessoBancoDadosException, NamingException {
        return servico.listarTiposDeOrgaosDoMunicipio(idMunicipio);
    }

    @GET
    @Path("/orgaos/{idMunicipio}/{idTipoOrgao}")
    @Produces("application/json")
    public Response listarTiposDeOrgaosDoMunicipio(@PathParam("idMunicipio") int idMunicipio, @PathParam("idTipoOrgao") int idTipoOrgao) throws AtlasAcessoBancoDadosException, NamingException {
        return servico.listarTiposDeOrgaosDoMunicipio(idMunicipio, idTipoOrgao);
    }

    @GET
    @Path("/videos")
    @Produces("application/json")
    public Response listarVideos() throws AtlasAcessoBancoDadosException, NamingException {
        return servico.listarVideos();
    }

    @GET
    @Path("/publicacoes")
    @Produces("application/json")
    public Response listarPublicacoes() throws AtlasAcessoBancoDadosException, NamingException {
        return servico.listarPublicacoes();
    }

    @GET
    @Path("/glossarios/{termo}")
    @Produces("application/json")
    public Response listarGlossarios(@PathParam("termo") String termo) throws AtlasAcessoBancoDadosException, NamingException {
        return servico.listarGlossarios(termo);
    }

    @GET
    @Path("/termos/{letra}")
    @Produces("application/json")
    public Response listarTermos(@PathParam("letra") String letra) throws AtlasAcessoBancoDadosException, NamingException {
        return servico.listarTermos(letra);
    }

    @GET
    @Path("/problemas/{idOrgao}/{idTipoProblema}/{dscProblema}")
    @Produces("application/json")
    public Response informarProblema(@PathParam("idOrgao") int idOrgao,
            @PathParam("idTipoProblema") int idTipoProblema,
            @PathParam("dscProblema") String dscProblema) throws AtlasAcessoBancoDadosException, NamingException {

        return servico.informarProblema(idOrgao, idTipoProblema, dscProblema);
    }

    @GET
    @Path("/temas")
    @Produces("application/json")
    public Response listarTemas() throws AtlasAcessoBancoDadosException, NamingException {
        return servico.listarTemas();
    }

    @GET
    @Path("/orgaosPorTema/{idMunicipio}/{idTema}")
    @Produces("application/json")
    public Response listarOrgaosPorTema(@PathParam("idMunicipio") int idMunicipio, @PathParam("idTema") int idTema) throws AtlasAcessoBancoDadosException, NamingException {
        return servico.listarOrgaosPorTema(idMunicipio, idTema);
    }

    @POST
    @Path("/termobusca")
    @Consumes({"application/json"})
    public void salvarTermoBusca(TermoBuscaDTO termoBuscaDTO) throws AtlasAcessoBancoDadosException, NamingException {
    	servico.salvarTermoBusca(termoBuscaDTO);
    }
    
    @GET
    @Path("/termosbusca/{limite}")
    @Produces("application/json")
    public Response listaTermosBuscaMaisUtilizados(@PathParam("limite") int limite) throws AtlasAcessoBancoDadosException, NamingException {
        return servico.listaTermosBuscaMaisUtilizados(limite);
    }

}
