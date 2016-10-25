package br.gov.atlas.servico;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import br.gov.atlas.dao.OrgaoDao;
import br.gov.atlas.dto.DadosEmBlocoDTO;
import br.gov.atlas.dto.EnderecoDTO;
import br.gov.atlas.dto.OrgaoDTO;
import br.gov.atlas.dto.TotalizadorOrgaoDTO;
import br.gov.atlas.entidade.EmailUsuario;
import br.gov.atlas.entidade.Municipio;
import br.gov.atlas.entidade.Orgao;
import br.gov.atlas.entidade.Ramo;
import br.gov.atlas.entidade.Representante;
import br.gov.atlas.entidade.Servico;
import br.gov.atlas.entidade.Telefone;
import br.gov.atlas.entidade.Tema;
import br.gov.atlas.entidade.TipoOrgao;
import br.gov.atlas.entidade.UF;
import br.gov.atlas.entidade.Usuario;
import br.gov.atlas.entity.ListaPaginada;
import br.gov.atlas.enums.IndicadorMapa;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;

@Stateless(name = "OrgaoServico")
@LocalBean
public class OrgaoServico extends AtlasServicoImpl {

	private OrgaoDao dao;

	@Override
	public OrgaoDao getDao() {
		if (dao == null) {
			dao = new OrgaoDao(em);
		}
		return dao;
	}

	public Orgao buscarPorIdLazyLoad(Integer id)
			throws AtlasAcessoBancoDadosException {
		Orgao orgao = getDao().pesquisarPorId(id, Orgao.class);
		//Carregando as listas
		orgao.getServicos().size();
		orgao.getProblemas().size();
		if(orgao.getOrgaoSuperior() != null){
			orgao.getOrgaoSuperior().getNomeReduzido();
		}
		return orgao;
	}

	public List<Orgao> buscarTodos(int offset, int tamanhoLote)
			throws AtlasAcessoBancoDadosException {
		return getDao().buscarTodos(offset, tamanhoLote);
	}

	public List<Orgao> recuperarOrgaos(OrgaoDTO orgaoFiltro)
			throws AtlasAcessoBancoDadosException {
		return getDao().recuperarOrgaos(orgaoFiltro);
	}

	public List<Orgao> recuperarLonLatOrgaos(OrgaoDTO orgaoFiltro)
			throws AtlasAcessoBancoDadosException {
		return getDao().recuperarLonLatOrgaos(orgaoFiltro);
	}

	public List<Orgao> recuperarOrgaos(Municipio municipio, List<TipoOrgao> list)
			throws AtlasAcessoBancoDadosException {
		return getDao().recuperarOrgaos(municipio, list);
	}

	public List<Orgao> recuperarOrgaosSemAtualizacao(Municipio municipio,
			List<TipoOrgao> tiposOrgao, int tamanhoLote)
					throws AtlasAcessoBancoDadosException {
		return getDao().recuperarOrgaosSemAtualizacao(municipio, tiposOrgao,
				tamanhoLote);
	}

	public int getIdProximoRegistroAPreencher()
			throws AtlasAcessoBancoDadosException {
		return getDao().getIdProximoRegistroAPreencher();
	}

	public List<Orgao> recuperarOrgaosSemLatitude()
			throws AtlasAcessoBancoDadosException {
		return getDao().recuperarOrgaosSemLatitude();
	}

	public List<Telefone> recuperarTelefones(Orgao orgao)
			throws AtlasAcessoBancoDadosException {
		return getDao().recuperarTelefones(orgao);
	}

	public List<EmailUsuario> recuperarEmails(Orgao orgao)
			throws AtlasAcessoBancoDadosException {
		return getDao().recuperarEmails(orgao);
	}

	public List<TipoOrgao> recuperarTiposOrgao(Ramo ramo)
			throws AtlasAcessoBancoDadosException {
		return getDao().recuperarTiposOrgao(ramo);

	}

	public boolean possuiOrgaos(TipoOrgao tipoOrgao)
			throws AtlasAcessoBancoDadosException {
		int quantidadeResgitros = getDao().possuiOrgaos(tipoOrgao);
		return quantidadeResgitros > 0;
	}

	public ListaPaginada<Orgao> recuperarOrgaosPaginados(OrgaoDTO orgaoFiltro)
			throws AtlasAcessoBancoDadosException {
		return this.recuperarOrgaosPaginados(orgaoFiltro, null, null);
	}

	public ListaPaginada<Orgao> recuperarOrgaosPaginados(OrgaoDTO orgaoFiltro,
			String sortField, String sortOrder)
					throws AtlasAcessoBancoDadosException {
		ListaPaginada<Orgao> listaPaginada = new ListaPaginada<Orgao>();
		return this.recuperarOrgaosPaginados(orgaoFiltro, sortField, sortOrder, listaPaginada);
	}

	public ListaPaginada<Orgao> recuperarOrgaosPaginados(OrgaoDTO orgaoFiltro,
			String sortField, String sortOrder,
			ListaPaginada<Orgao> listaPaginada)
					throws AtlasAcessoBancoDadosException {
		return getDao().recuperarOrgaos(orgaoFiltro, sortField, sortOrder,
				listaPaginada);
	}

	public List<Orgao> recuperarOrgaosSemAtualizacao(UF uf,
			List<TipoOrgao> tiposOrgao, int qtdMaximaRegistros)
					throws AtlasAcessoBancoDadosException {
		return getDao().recuperarOrgaosSemAtualizacao(uf, tiposOrgao,
				qtdMaximaRegistros);
	}

	public List<Tema> recuperarTemas(Orgao orgao)
			throws AtlasAcessoBancoDadosException {
		return getDao().recuperarTemas(orgao);
	}

	public List<Servico> recuperarServicos(Orgao orgao)
			throws AtlasAcessoBancoDadosException {
		return getDao().recuperarServicos(orgao);
	}

	public List<Servico> recuperarServicosDisponiveisSelecao(Orgao orgao, String nomeServico)
			throws AtlasAcessoBancoDadosException {
		return getDao().recuperarServicosDisponiveisSelecao(orgao, nomeServico);
	}

	public List<Orgao> recuperarOrgaosPorTema(Municipio municipio,
			List<Tema> temasSelecionados) throws AtlasAcessoBancoDadosException {
		return getDao().recuperarOrgaosPorTema(municipio, temasSelecionados);
	}

	public List<OrgaoDTO> recuperarTipoOrgaoPorRegiao(Integer codRegiao)
			throws AtlasAcessoBancoDadosException {
		return getDao().recuperarTipoOrgaoPorRegiaoEstadoMunicipio(codRegiao,
				null, null);
	}

	public List<OrgaoDTO> recuperarTipoOrgaoPorEstado(String siglaUf)
			throws AtlasAcessoBancoDadosException {
		return recuperarTipoOrgaoPorRegiaoEstadoMunicipio(null, siglaUf, null);
	}

	public List<OrgaoDTO> recuperarTipoOrgaoPorMunicipio(String municipio)
			throws AtlasAcessoBancoDadosException {
		return recuperarTipoOrgaoPorRegiaoEstadoMunicipio(null, null, municipio);
	}

	public List<OrgaoDTO> recuperarTipoOrgaoPorRegiaoEstadoMunicipio(
			Integer codRegiao, String siglaUf, String municipio)
					throws AtlasAcessoBancoDadosException {
		return getDao().recuperarTipoOrgaoPorRegiaoEstadoMunicipio(codRegiao,
				siglaUf, municipio);
	}

	public List<OrgaoDTO> recuperarTipoOrgaoPorBrasil()
			throws AtlasAcessoBancoDadosException {
		return getDao().recuperarTipoOrgaoPorBrasil();
	}

	public List<EnderecoDTO> recuperarEnderecoPorTipoMunicipio(String tipo,
			String municipio) throws AtlasAcessoBancoDadosException {
		return getDao().recuperarEnderecoPorTipoMunicipio(tipo, municipio);
	}

	public int salvarEmBloco(DadosEmBlocoDTO dadosEmBlocoDTO)
			throws AtlasAcessoBancoDadosException {
		return getDao().salvarEmBloco(dadosEmBlocoDTO);
	}

	public List<Orgao> recuperarOrgaosPorTema(Integer idTema)
			throws AtlasAcessoBancoDadosException {
		return getDao().recuperarOrgaosPorTema(idTema);
	}

	public List<TotalizadorOrgaoDTO> recuperarTotalizadoresPorIndicadorMapaNivelBrasil(
			IndicadorMapa indicadorMapa) throws AtlasAcessoBancoDadosException {
		return getDao().recuperarTotalizadoresPorIndicadorMapaNivelBrasil(indicadorMapa);
	}

	public List<TotalizadorOrgaoDTO> recuperarTotalizadoresPorIndicadorMapaNivelRegiao(
			IndicadorMapa indicadorMapa) throws AtlasAcessoBancoDadosException {
		return getDao().recuperarTotalizadoresPorIndicadorMapaNivelRegiao(indicadorMapa);
	}

	public List<TotalizadorOrgaoDTO> recuperarTotalizadoresPorIndicadorMapaNivelEstado(
			IndicadorMapa indicadorMapa, Integer idRegiao)
					throws AtlasAcessoBancoDadosException {
		return getDao().recuperarTotalizadoresPorIndicadorMapaNivelEstado(indicadorMapa,
				idRegiao);
	}

	public List<TotalizadorOrgaoDTO> recuperarTotalizadoresPorIndicadorMapaEstado(
			IndicadorMapa indicadorMapa, Integer idEstado)
					throws AtlasAcessoBancoDadosException {
		return getDao().recuperarTotalizadoresPorIndicadorMapa(indicadorMapa, idEstado);
	}

	public List<Orgao> recuperarOrgaosPorMunicipioTipo(Municipio municipio,
			List<Integer> tiposOrgaoSelecionados, Integer idTipoParaOrdenacao)
					throws AtlasAcessoBancoDadosException {
		return getDao().recuperarOrgaosPorMunicipioTipo(municipio,
				tiposOrgaoSelecionados, idTipoParaOrdenacao);
	}

	public Orgao recuperarOrgaoSuperior(Integer idOrgao) throws AtlasAcessoBancoDadosException {
		return getDao().recuperarOrgaoSuperior(idOrgao);
	}

	public List<Orgao> recuperarDadosOgaos(OrgaoDTO orgaoFiltro) throws AtlasAcessoBancoDadosException {
		return getDao().recuperarDadosOgaos(orgaoFiltro);
	}

	public List<Orgao> recuperarOrgaosRelacionadosESubordinados(Usuario usuarioAutenticado) throws AtlasAcessoBancoDadosException {
		if(usuarioAutenticado == null){
			throw new RuntimeException("Usuário está nulo.");
		}
		return getDao().recuperarOrgaosRelacionadosESubordinados(usuarioAutenticado);
	}

	public List<Orgao> recuperarOrgaosRelacionados(Usuario usuarioAutenticado) throws AtlasAcessoBancoDadosException {
		return getDao().recuperarOrgaosRelacionados(usuarioAutenticado);
	}

	public ListaPaginada<Representante> recuperarDestinatarios(Representante representanteFiltro) throws AtlasAcessoBancoDadosException {
		return getDao().recuperarDestinatarios(representanteFiltro);
	}
}