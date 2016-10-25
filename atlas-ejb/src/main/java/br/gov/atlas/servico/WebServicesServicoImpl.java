package br.gov.atlas.servico;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.naming.NamingException;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import br.gov.atlas.dto.TermoBuscaDTO;
import br.gov.atlas.entidade.complem.GlossarioRest;
import br.gov.atlas.entidade.complem.MunicipioRest;
import br.gov.atlas.entidade.complem.OrgaoRest;
import br.gov.atlas.entidade.complem.PublicacaoRest;
import br.gov.atlas.entidade.complem.RepresentanteRest;
import br.gov.atlas.entidade.complem.TelefoneRest;
import br.gov.atlas.entidade.complem.TemaRest;
import br.gov.atlas.entidade.complem.TipoDeOrgaoRest;
import br.gov.atlas.entidade.complem.UFRest;
import br.gov.atlas.entidade.complem.VideoRest;
import br.gov.atlas.enums.TipoSituacaoRegistroOrgao;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;

@Stateless
public class WebServicesServicoImpl implements WebServicesServico {

    @EJB
    private ConexaoServico conexaoServico;

    public static final String SQL_UFS = "SELECT id_uf as id, cod_uf as sigla, nme_uf as nome FROM atlas.uf ORDER BY nme_uf";
    public static final String SQL_MUNICIPIOS_DA_UF = "SELECT id_municipio id, nme_municipio nome FROM atlas.municipio WHERE id_uf = ? ORDER BY nme_municipio";
    public static final String SQL_MUNICIPIOS_DA_SG_UF = "SELECT id_municipio id, nme_municipio nome FROM MUNICIPIO MUN JOIN UF UF ON MUN.ID_UF = UF.ID_UF AND UF.COD_UF = ? ";
    public static final String SQL_TIPOS_ORGAOS_DO_MUNICIPIO
            = " SELECT   R.nme_ramo ramo, T.id_tipo_orgao id, T.nme_tipo_orgao nome "
            + " FROM     ramo R, tipo_orgao T  "
            + " WHERE    R.id_ramo = T.id_ramo AND T.id_tipo_orgao IN (  "
            + "	SELECT id_tipo_orgao  "
            + "	FROM   atlas.orgao  "
            + "	WHERE  id_municipio = ? )  "
            + " ORDER BY R.nme_ramo ";

    public static final String SQL_ORGAOS_DO_MUNICIPIO_E_TIPO_ORGAO = ""
            + " SELECT id_orgao id, cod_sigla_orgao sigla, nme_orgao nome, dsc_endereco endereco, nu_cep cep, "
            + "        nme_homepage homepage, num_latitude latitude, num_longitude longitude "
            + " FROM atlas.orgao WHERE id_municipio = ? AND id_tipo_orgao = ? AND ind_situacao = ?";

    public static final String SQL_TELEFONES_ORGAO
            = " SELECT 	ID_TELEFONE AS ID, NU_DDD AS DDD, "
            + " 			NU_TELEFONE AS TELEFONE, IND_TIPO_TELEFONE AS TIPO, DSC_TELEFONE AS DESCRICAO "
            + " FROM 		ATLAS.TELEFONE WHERE ID_ORGAO = ? ";

    public static final String SQL_REPRESENTANTES_PROCON
            = "	SELECT 	ROR.NME_REPRESENTANTE AS NOME, ROR.NME_CARGO AS CARGO "
            + "	FROM 	ATLAS.REPRESENTANTE_ORGAO ROR "
            + "	JOIN	ATLAS.ORGAO ORG ON ORG.ID_ORGAO = ROR.ID_ORGAO "
            + "	AND	ORG.ID_TIPO_ORGAO = 7 "
            + "	AND	ORG.ID_ORGAO = ? ";

    public static final String SQL_SELECT_MULTIMIDIAS = ""
            + "SELECT id_multimidia AS ID, nme_multimidia AS NOME, dsc_multimidia AS DESCRICAO, "
            + "nme_recurso AS NOME_RECURSO, url_recurso AS URL, ind_tipo_recurso AS TIPO, "
            + "dta_publicacao AS DATA, ind_tela_inicial AS TELA_INICIAL "
            + "FROM atlas.multimidia multimidia ";

    public static final String SQL_SELECT_PUBLICACOES = ""
            + "SELECT id_cartilha AS ID, nme_titulo_cartilha AS NOME, lnk_imagem_cartilha AS IMAGEM, lnk_arquivo_cartilha AS ARQUIVO "
            + "FROM atlas.cartilha cartilha ";

    public static final String SQL_SELECT_TERMOS = ""
            + "SELECT id_termo AS ID, nme_termo AS NOME, dsc_termo AS DESCRICAO, dsc_saiba_mais AS MAIS "
            + "FROM atlas.termo_glossario as termo "
            + "WHERE atlas.retirar_acento(termo.nme_termo) like UPPER(?) ";

    public static final String INSERT_INFORMAR_PROBLEMA = ""
            + "INSERT INTO atlas.problema (ID_ORGAO, IND_TIPO_PROBLEMA, DSC_PROBLEMA) "
            + "VALUES (?, ?, ?)";

    public static final String SQL_TEMAS = "SELECT id_tema as id, nme_tema as nome FROM atlas.tema ORDER BY nme_tema";

    public static final String SQL_ORGAOS_POR_TEMA = ""
            + " SELECT org.id_orgao id, org.cod_sigla_orgao sigla, "
            + "        org.nme_orgao nome, org.dsc_endereco endereco, "
            + "        org.nu_cep cep, org.nme_homepage homepage, "
            + "        org.num_latitude latitude, org.num_longitude longitude "
            + " FROM orgao org"
            + " JOIN tema_orgao tema on tema.id_orgao = org.id_orgao and tema.id_tema = ? "
            + " WHERE org.id_municipio = ?";

    private Connection openConnection() {
        return conexaoServico.conn();
    }

    private void closeConnection(Connection connection) {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Response listarUfs() throws AtlasAcessoBancoDadosException, NamingException {

        List<UFRest> ufs = new ArrayList<>();

        Connection connection = openConnection();
        try {
            PreparedStatement statement = connection.prepareStatement(SQL_UFS);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                UFRest ufRest = new UFRest();
                ufRest.id = resultSet.getInt("id");
                ufRest.sigla = resultSet.getString("sigla");
                ufRest.nome = resultSet.getString("nome");
                ufs.add(ufRest);
            }
        } catch (SQLException e) {
            throw new AtlasAcessoBancoDadosException("Erro ao tentar executar a consulta: " + e.getMessage());
        } finally {
            closeConnection(connection);
        }

        return Response.status(200).entity(ufs).header("Access-Control-Allow-Origin", "*").build();
    }

    public Response listarMunicipiosDaUf(@PathParam("idUf") int idUf) throws AtlasAcessoBancoDadosException, NamingException {

        List<MunicipioRest> municipios = new ArrayList<>();

        Connection connection = openConnection();
        try {
            PreparedStatement statement = connection.prepareStatement(SQL_MUNICIPIOS_DA_UF);
            statement.setInt(1, idUf);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                MunicipioRest municipioRest = new MunicipioRest();
                municipioRest.id = resultSet.getInt("id");
                municipioRest.nome = resultSet.getString("nome");
                municipios.add(municipioRest);
            }
        } catch (SQLException e) {
            throw new AtlasAcessoBancoDadosException("Erro ao tentar executar a consulta: " + e.getMessage());
        } finally {
            closeConnection(connection);
        }

        return Response.status(200).entity(municipios).header("Access-Control-Allow-Origin", "*").build();
    }

    public Response listarTiposDeOrgaosDoMunicipio(@PathParam("idMunicipio") int idMunicipio) throws AtlasAcessoBancoDadosException, NamingException {

        List<TipoDeOrgaoRest> tiposDeOrgaos = new ArrayList<>();

        Connection connection = openConnection();
        try {
            PreparedStatement statement = connection.prepareStatement(SQL_TIPOS_ORGAOS_DO_MUNICIPIO);
            statement.setInt(1, idMunicipio);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                TipoDeOrgaoRest tipoDeOrgaoRest = new TipoDeOrgaoRest();
                tipoDeOrgaoRest.tipo_ente = resultSet.getString("tipo_ente");
                tipoDeOrgaoRest.id = resultSet.getInt("id");
                tipoDeOrgaoRest.nome = resultSet.getString("nome");
                tiposDeOrgaos.add(tipoDeOrgaoRest);
            }
        } catch (SQLException e) {
            throw new AtlasAcessoBancoDadosException("Erro ao tentar executar a consulta: " + e.getMessage());
        } finally {
            closeConnection(connection);
        }

        return Response.status(200).entity(tiposDeOrgaos).header("Access-Control-Allow-Origin", "*").build();
    }

    public Response listarTiposDeOrgaosDoMunicipio(@PathParam("idMunicipio") int idMunicipio, @PathParam("idTipoOrgao") int idTipoOrgao) throws AtlasAcessoBancoDadosException, NamingException {

        List<OrgaoRest> orgaos = new ArrayList<>();

        Connection connection = openConnection();
        try {
            PreparedStatement statement = connection.prepareStatement(SQL_ORGAOS_DO_MUNICIPIO_E_TIPO_ORGAO);
            statement.setInt(1, idMunicipio);
            statement.setInt(2, idTipoOrgao);
            statement.setString(3, TipoSituacaoRegistroOrgao.DISPONIVEL.indiceSituacao());
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                OrgaoRest orgaoRest = new OrgaoRest();
                orgaoRest.id = resultSet.getInt("id");
                orgaoRest.sigla = resultSet.getString("sigla");
                orgaoRest.nome = resultSet.getString("nome");
                orgaoRest.endereco = resultSet.getString("endereco");
                String cep = resultSet.getString("cep");
                Integer numCep = 0;
                if (cep != null) {
                    cep = cep.replace("-", "");
                    numCep = Integer.parseInt(cep.trim());
                }
                orgaoRest.cep = numCep;
                orgaoRest.homePage = resultSet.getString("homePage");
                orgaoRest.latitude = resultSet.getBigDecimal("latitude");
                orgaoRest.longitude = resultSet.getBigDecimal("longitude");
                orgaoRest.telefones = recuperarTelefones(resultSet.getInt("id"), connection);
                orgaoRest.representantes = recuperarRepresentantes(resultSet.getInt("id"), connection);
                orgaos.add(orgaoRest);
            }
        } catch (SQLException e) {
            throw new AtlasAcessoBancoDadosException("Erro ao tentar executar a consulta: " + e.getMessage());
        } finally {
            closeConnection(connection);
        }

        return Response.status(200).entity(orgaos).header("Access-Control-Allow-Origin", "*").build();
    }

    private List<RepresentanteRest> recuperarRepresentantes(int idOrgao, Connection connection) throws AtlasAcessoBancoDadosException {
        List<RepresentanteRest> representantes = new ArrayList<>();

        try {
            PreparedStatement statement = connection.prepareStatement(SQL_REPRESENTANTES_PROCON);
            statement.setInt(1, idOrgao);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                RepresentanteRest representanteRest = new RepresentanteRest();
                representanteRest.nome = resultSet.getString("nome");
                representanteRest.cargo = resultSet.getString("cargo");
                representantes.add(representanteRest);
            }
            return representantes;
        } catch (SQLException e) {
            throw new AtlasAcessoBancoDadosException("Erro ao tentar executar a consulta: " + e.getMessage());
        }
    }

    private List<TelefoneRest> recuperarTelefones(int idOrgao, Connection connection) throws AtlasAcessoBancoDadosException {
        List<TelefoneRest> telefones = new ArrayList<>();

        try {
            PreparedStatement statement = connection.prepareStatement(SQL_TELEFONES_ORGAO);
            statement.setInt(1, idOrgao);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                TelefoneRest telefoneRest = new TelefoneRest();
                telefoneRest.id = resultSet.getInt("id");
                telefoneRest.ddd = resultSet.getString("ddd");
                telefoneRest.numeroTelefone = resultSet.getString("telefone");
                telefoneRest.tipoTelefone = resultSet.getString("tipo");
                telefones.add(telefoneRest);
            }
            return telefones;
        } catch (SQLException e) {
            throw new AtlasAcessoBancoDadosException("Erro ao tentar executar a consulta: " + e.getMessage());
        }
    }

    public Response listarVideos() throws AtlasAcessoBancoDadosException, NamingException {

        List<VideoRest> videos = new ArrayList<>();

        Connection connection = openConnection();
        try {
            PreparedStatement statement = connection.prepareStatement(SQL_SELECT_MULTIMIDIAS);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                VideoRest videoRest = new VideoRest();
                videoRest.id = resultSet.getInt("id");
                videoRest.nome = resultSet.getString("nome");
                videoRest.descricao = resultSet.getString("descricao");
                videoRest.nomeRecurso = resultSet.getString("nome_recurso");
                videoRest.urlRecurso = resultSet.getString("url");
                videoRest.tipoRecurso = resultSet.getString("tipo");
                videoRest.dataPublicacao = resultSet.getString("data");
                videoRest.telaInicial = resultSet.getString("tela_inicial");
                videos.add(videoRest);
            }
        } catch (SQLException e) {
            throw new AtlasAcessoBancoDadosException("Erro ao tentar executar a consulta: " + e.getMessage());
        } finally {
            closeConnection(connection);
        }
        return Response.status(200).entity(videos).header("Access-Control-Allow-Origin", "*").build();
    }

    public Response listarPublicacoes() throws AtlasAcessoBancoDadosException, NamingException {

        List<PublicacaoRest> publicacoes = new ArrayList<>();

        Connection connection = openConnection();
        try {
            PreparedStatement statement = connection.prepareStatement(SQL_SELECT_PUBLICACOES);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                PublicacaoRest publicacaoRest = new PublicacaoRest();
                publicacaoRest.id = resultSet.getInt("id");
                publicacaoRest.nome = resultSet.getString("nome");
                publicacaoRest.imagem = resultSet.getString("imagem");
                publicacaoRest.arquivo = resultSet.getString("arquivo");
                publicacoes.add(publicacaoRest);
            }
        } catch (SQLException e) {
            throw new AtlasAcessoBancoDadosException("Erro ao tentar executar a consulta: " + e.getMessage());
        } finally {
            closeConnection(connection);
        }
        return Response.status(200).entity(publicacoes).header("Access-Control-Allow-Origin", "*").build();
    }

    public Response listarGlossarios(@PathParam("termo") String termo) throws AtlasAcessoBancoDadosException, NamingException {

        List<GlossarioRest> glossarios = new ArrayList<>();

        Connection connection = openConnection();
        try {
            PreparedStatement statement = connection.prepareStatement(SQL_SELECT_TERMOS);
            statement.setString(1, termo);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                GlossarioRest glossarioRest = new GlossarioRest();
                glossarioRest.id = resultSet.getInt("id");
                glossarioRest.nome = resultSet.getString("nome");
                glossarioRest.descricao = resultSet.getString("descricao");
                glossarioRest.mais = resultSet.getString("mais");
                glossarios.add(glossarioRest);
            }
        } catch (SQLException e) {
            throw new AtlasAcessoBancoDadosException("Erro ao tentar executar a consulta: " + e.getMessage());
        } finally {
            closeConnection(connection);
        }

        return Response.status(200).entity(glossarios).header("Access-Control-Allow-Origin", "*").build();
    }

    public Response listarTermos(@PathParam("letra") String letra) throws AtlasAcessoBancoDadosException, NamingException {

        List<GlossarioRest> glossarios = new ArrayList<>();

        Connection connection = openConnection();
        try {
            PreparedStatement statement = connection.prepareStatement(SQL_SELECT_TERMOS);
            statement.setString(1, letra + "%");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                GlossarioRest glossarioRest = new GlossarioRest();
                glossarioRest.id = resultSet.getInt("id");
                glossarioRest.nome = resultSet.getString("nome");
                glossarioRest.descricao = resultSet.getString("descricao");
                glossarioRest.mais = resultSet.getString("mais");
                glossarios.add(glossarioRest);
            }
        } catch (SQLException e) {
            throw new AtlasAcessoBancoDadosException("Erro ao tentar executar a consulta: " + e.getMessage());
        } finally {
            closeConnection(connection);
        }

        return Response.status(200).entity(glossarios).header("Access-Control-Allow-Origin", "*").build();
    }

    public Response informarProblema(@PathParam("idOrgao") int idOrgao,
            @PathParam("idTipoProblema") int idTipoProblema,
            @PathParam("dscProblema") String dscProblema) throws AtlasAcessoBancoDadosException, NamingException {

        Connection connection = openConnection();
        try {
            PreparedStatement statement = connection.prepareStatement(INSERT_INFORMAR_PROBLEMA);
            statement.setInt(1, idOrgao);
            statement.setInt(2, idTipoProblema);
            statement.setString(3, dscProblema);

            statement.execute();
            statement.close();

        } catch (SQLException e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Erro ao tentar informar problema: " + e.getMessage()).header("Access-Control-Allow-Origin", "*").build();
        } finally {
            closeConnection(connection);
        }

        return Response.status(Status.OK).entity("Problema informado com Sucesso.").header("Access-Control-Allow-Origin", "*").build();
    }

    @Override
    public Response listarTemas() throws AtlasAcessoBancoDadosException,
            NamingException {
        List<TemaRest> temas = new ArrayList<>();

        Connection connection = openConnection();
        try {
            PreparedStatement statement = connection.prepareStatement(SQL_TEMAS);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                TemaRest temaRest = new TemaRest();
                temaRest.id = resultSet.getInt("id");
                temaRest.nome = resultSet.getString("nome");
                temas.add(temaRest);
            }
        } catch (SQLException e) {
            throw new AtlasAcessoBancoDadosException("Erro ao tentar executar a consulta: " + e.getMessage());
        } finally {
            closeConnection(connection);
        }

        return Response.status(200).entity(temas).header("Access-Control-Allow-Origin", "*").build();
    }

    public Response listarOrgaosPorTema(@PathParam("idMunicipio") int idMunicipio, @PathParam("idTema") int idTema) throws AtlasAcessoBancoDadosException, NamingException {

        List<OrgaoRest> orgaos = new ArrayList<>();

        Connection connection = openConnection();
        try {
            PreparedStatement statement = connection.prepareStatement(SQL_ORGAOS_POR_TEMA);
            statement.setInt(1, idTema);
            statement.setInt(2, idMunicipio);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                OrgaoRest orgaoRest = new OrgaoRest();
                orgaoRest.id = resultSet.getInt("id");
                orgaoRest.sigla = resultSet.getString("sigla");
                orgaoRest.nome = resultSet.getString("nome");
                orgaoRest.endereco = resultSet.getString("endereco");
                String cep = resultSet.getString("cep");
                Integer numCep = 0;
                if (cep != null) {
                    cep = cep.replace("-", "");
                    numCep = Integer.parseInt(cep.trim());
                }
                orgaoRest.cep = numCep;
                orgaoRest.homePage = resultSet.getString("homePage");
                orgaoRest.latitude = resultSet.getBigDecimal("latitude");
                orgaoRest.longitude = resultSet.getBigDecimal("longitude");
                orgaoRest.telefones = recuperarTelefones(resultSet.getInt("id"), connection);
                orgaoRest.representantes = recuperarRepresentantes(resultSet.getInt("id"), connection);
                orgaos.add(orgaoRest);
            }
        } catch (SQLException e) {
            throw new AtlasAcessoBancoDadosException("Erro ao tentar executar a consulta: " + e.getMessage());
        } finally {
            closeConnection(connection);
        }

        return Response.status(200).entity(orgaos).header("Access-Control-Allow-Origin", "*").build();
    }

    @Override
    public Response listarMunicipiosDaUf(String sgUf)
            throws AtlasAcessoBancoDadosException, NamingException {

        List<MunicipioRest> municipios = new ArrayList<>();

        Connection connection = openConnection();
        try {
            PreparedStatement statement = connection.prepareStatement(SQL_MUNICIPIOS_DA_SG_UF);
            statement.setString(1, sgUf);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                MunicipioRest municipioRest = new MunicipioRest();
                municipioRest.id = resultSet.getInt("id");
                municipioRest.nome = resultSet.getString("nome");
                municipios.add(municipioRest);
            }
        } catch (SQLException e) {
            throw new AtlasAcessoBancoDadosException("Erro ao tentar executar a consulta: " + e.getMessage());
        } finally {
            closeConnection(connection);
        }

        return Response.status(200).entity(municipios).header("Access-Control-Allow-Origin", "*").build();

    }

    @Override
    public Response salvarTermoBusca(TermoBuscaDTO termoBuscaDTO) throws AtlasAcessoBancoDadosException, NamingException {
        if (termoBuscaDTO != null && termoBuscaDTO.getNmeTermoBusca() != null && !termoBuscaDTO.getNmeTermoBusca().equals("")) {
            List<String> retorno = new ArrayList<>();
            
            Connection connection = openConnection();

            try {
                String sql = "select f_inserir_termo_busca('"
                        + termoBuscaDTO.getNmeTermoBusca() + "','"
                        + termoBuscaDTO.getCodUf() + "', '"
                        + termoBuscaDTO.getNmeMunicipio() + "', "
                        + termoBuscaDTO.isSitLocalizado() + ") retorno";
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    retorno.add(resultSet.getString("retorno"));
                    return Response.status(200).entity(retorno).header("Access-Control-Allow-Origin", "*").build();
                }
            } catch (SQLException e) {
                throw new AtlasAcessoBancoDadosException("Erro ao tentar executar a consulta: " + e.getMessage());
            } finally {
                closeConnection(connection);
            }
        }
        return Response.status(Status.INTERNAL_SERVER_ERROR)
                .entity("Termo informado vazio ou nulo.")
                .header("Access-Control-Allow-Origin", "*").build();
    }

    @Override
    public Response listaTermosBuscaMaisUtilizados(int limite) throws AtlasAcessoBancoDadosException, NamingException {
        List<TermoBuscaDTO> termos = new ArrayList<>();

        Connection connection = openConnection();
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "select nme_termo_busca, sum(qtd_termo_busca) total " +
                    "  from termo_busca where dta_bloqueio is null " +
                    " group by nme_termo_busca " +
                    " order by 2 desc " +
                    " limit " + limite
            );
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                TermoBuscaDTO termo = new TermoBuscaDTO();
                termo.setNmeTermoBusca(resultSet.getString("nme_termo_busca"));
                termo.setQtdTotal(resultSet.getLong("total"));
                
                termos.add(termo);
            }
        } catch (SQLException e) {
            throw new AtlasAcessoBancoDadosException("Erro ao tentar executar a consulta: " + e.getMessage());
        } finally {
            closeConnection(connection);
        }

        return Response.status(200).entity(termos).header("Access-Control-Allow-Origin", "*").build();
    }

}
