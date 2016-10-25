package br.gov.atlas.bean;

import java.math.BigInteger;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.primefaces.model.map.DefaultMapModel;

import br.gov.atlas.dto.TotalizadorOrgaoDTO;
import br.gov.atlas.dto.TotalizadorOrgaoDTO.NivelTotalizadorOrgao;
import br.gov.atlas.entidade.Orgao;
import br.gov.atlas.entidade.TipoOrgao;
import br.gov.atlas.enums.IndicadorMapa;
import br.gov.atlas.enums.TipoRamo;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;
import br.gov.atlas.servico.ConexaoServico;
import br.gov.atlas.servico.RegiaoServico;
import br.gov.atlas.servico.RepresentanteServico;
import br.gov.atlas.servico.TelefoneServico;
import br.gov.atlas.servico.TipoOrgaoServico;
import br.gov.atlas.util.RelatorioUtil;

@ManagedBean(name = "mapaConsumidorBean")
@ViewScoped
public class MapaConsumidorBean extends ExibirMapaBean {

	private static final long serialVersionUID = 1L;

	private List<String> tiposOrgaoSelecionados;

	private TreeNode rootTotalizadorBrasil;

	private TreeNode rootTotalizadorRegiao;

	private Map<TipoRamo, List<TipoOrgao>> tiposOrgaoRelacionadosEnte = new HashMap<TipoRamo, List<TipoOrgao>>();

	@EJB(name = "TipoOrgaoServico")
	private TipoOrgaoServico tipoOrgaoServico;

	@EJB(name = "RegiaoServico")
	private RegiaoServico regiaoServico;

	@EJB(name = "RepresentanteServico")
	private RepresentanteServico representanteServico;

	@EJB(name = "TelefoneServico")
	private TelefoneServico telefoneServico;

	@EJB(name = "ConexaoServico")
	private ConexaoServico conexaoServico;

	@Override
	@PostConstruct
	public void init() {
		try {
			super.init();
			preencherTiposOrgaoSelecionadosDefault();
			carregarOrgaos(false);
		} catch (AtlasAcessoBancoDadosException e) {
			error("ERRO_QUERY");
		}
	}

	private void preencherTiposOrgaoSelecionadosDefault()
			throws AtlasAcessoBancoDadosException {
		if (tiposOrgaoSelecionados == null || tiposOrgaoSelecionados.isEmpty()) {
			tiposOrgaoSelecionados = new ArrayList<>();
			tiposOrgaoSelecionados.add(TipoOrgao.ID_TIPO_PROCON.toString());
		}
	}

	public void carregarOrgaos(boolean estaPesquisando)
			throws AtlasAcessoBancoDadosException {
		setLocalizou(estaPesquisando);
		if (estaPesquisando
				&& (getMunicipio() == null || tiposOrgaoSelecionados == null || tiposOrgaoSelecionados
				.isEmpty())) {
			error("MSG_TIPO_ORGAO_OBRIGATORIO");
		} else {
			setMapModel(new DefaultMapModel());
			setResultadoPesquisa(getOrgaoServico()
					.recuperarOrgaosPorMunicipioTipo(getMunicipio(),
							getTiposOrgaoSelecionadosInteger(), TipoOrgao.ID_TIPO_PROCON));
			if (getResultadoPesquisa() != null
					&& !getResultadoPesquisa().isEmpty()) {
				for (Orgao orgao : getResultadoPesquisa()) {
					if (isLocalizou()) {
						orgao.setTelefones(telefoneServico
								.recuperarTelefones(orgao));
						orgao.setRepresentantes(representanteServico
								.buscarDivulgaveis(orgao));
					}
					if (orgao.getLatitude() != null
							&& orgao.getLongitude() != null) {
						adicionarMarcador(orgao);
					}
				}
			} else {
				error("MSG_REGISTROS_NAO_ENCONTRADOS");
			}

			if (estaPesquisando) {
				setLatitudeBase(getMunicipio().getLatitude().doubleValue());
				setLongitudeBase(getMunicipio().getLongitude().doubleValue());
				setZoom(12);
			}

			String clientId = "form:directionsPanel";
			UIComponent comp = FacesContext.getCurrentInstance().getViewRoot()
					.findComponent(clientId);
			if (comp != null) {
				comp.getChildren().clear();
			}
			if(estaPesquisando){
				info("MSG_REPORTE_INF_MAPA");
			}
		}
	}

	@Override
	public void limpar() {
		super.limpar();
		tiposOrgaoSelecionados.clear();
		init();
	}

	public Boolean desabilitarBotaoLocalizar() {
		boolean isMunicipioNaoInformado = getMunicipio() == null;
		boolean isTipoOrgaoNaoInformado = getTiposOrgaoSelecionados() == null
				|| getTiposOrgaoSelecionados().isEmpty();
		boolean isDesabilitarBotao = isMunicipioNaoInformado
				|| (isTipoOrgaoNaoInformado);
		return isDesabilitarBotao;
	}

	private void carregarArvoreTotalizadorBrasil()
			throws AtlasAcessoBancoDadosException {
		setRootTotalizadorBrasil(new DefaultTreeNode(new TotalizadorOrgaoDTO(0,
				BigInteger.ZERO, "Brasil", NivelTotalizadorOrgao.BRASIL), null));

		List<TotalizadorOrgaoDTO> totalizadorBrasil = getOrgaoServico()
				.recuperarTotalizadoresPorIndicadorMapaNivelBrasil(IndicadorMapa.CONSUMIDOR);

		for (TotalizadorOrgaoDTO totalizador : totalizadorBrasil) {
			new DefaultTreeNode(new TotalizadorOrgaoDTO(totalizador.getId(),
					totalizador.getTotal(), totalizador.getNomeTotalizador(),
					totalizador.getNivel()), getRootTotalizadorBrasil());
		}
	}

	private void carregarArvoreTotalizadorRegiao()
			throws AtlasAcessoBancoDadosException {
		setRootTotalizadorRegiao(new DefaultTreeNode(new TotalizadorOrgaoDTO(0,
				BigInteger.ZERO, "Brasil", NivelTotalizadorOrgao.BRASIL), null));

		carregarTotalizadoresRegioes(getRootTotalizadorRegiao());
	}

	private void carregarTotalizadoresRegioes(TreeNode rootTotalizadorBrasil)
			throws AtlasAcessoBancoDadosException {

		List<TotalizadorOrgaoDTO> totalizadores = getOrgaoServico()
				.recuperarTotalizadoresPorIndicadorMapaNivelRegiao(IndicadorMapa.CONSUMIDOR);
		TreeNode noRegiao;
		for (TotalizadorOrgaoDTO totalizador : totalizadores) {
			noRegiao = new DefaultTreeNode(new TotalizadorOrgaoDTO(
					totalizador.getId(), totalizador.getTotal(),
					totalizador.getNomeTotalizador(), totalizador.getNivel()),
					rootTotalizadorBrasil);
			carregarTotalizadoresEstados(noRegiao, totalizador.getId());
		}
	}

	private void carregarTotalizadoresEstados(TreeNode noRegiao,
			Integer idRegiao) throws AtlasAcessoBancoDadosException {
		List<TotalizadorOrgaoDTO> totalizadores = getOrgaoServico()
				.recuperarTotalizadoresPorIndicadorMapaNivelEstado(IndicadorMapa.CONSUMIDOR,
						idRegiao);
		TreeNode noEstado;
		for (TotalizadorOrgaoDTO totalizador : totalizadores) {
			noEstado = new DefaultTreeNode(new TotalizadorOrgaoDTO(
					totalizador.getId(), totalizador.getTotal(),
					totalizador.getNomeTotalizador(), totalizador.getNivel()),
					noRegiao);
			carregarTotalizadorAcordoTipoTemasOrgao(noEstado,
					totalizador.getId());
		}
	}

	private void carregarTotalizadorAcordoTipoTemasOrgao(TreeNode noEstado,
			Integer idEstado) throws AtlasAcessoBancoDadosException {
		List<TotalizadorOrgaoDTO> totalizadores = getOrgaoServico()
				.recuperarTotalizadoresPorIndicadorMapaEstado(IndicadorMapa.CONSUMIDOR,
						idEstado);
		for (TotalizadorOrgaoDTO totalizador : totalizadores) {
			new DefaultTreeNode(new TotalizadorOrgaoDTO(totalizador.getId(),
					idEstado, totalizador.getTotal(),
					totalizador.getNomeTotalizador(), totalizador.getNivel()),
					noEstado);
		}
	}

	public void downloadTotalizador(Integer id, Integer idNoPai,
			NivelTotalizadorOrgao nivel) throws Exception {

		Connection conn = conexaoServico.conn();

		Map<String, Object> parametros = new HashMap<>();
		parametros.put("ID", id);
		parametros.put("ID_NO_PAI", idNoPai);
		parametros.put("NIVEL", nivel.getCodigo());

		RelatorioUtil.gerarRelatorio(null,
				"/relatorios/relatorioOrgaosMapaConsumidor.jasper",
				"relatorioOrgaosMapaConsumidor.xls", parametros,
				RelatorioUtil.XLS, conn);
	}

	public TipoRamo[] getTiposRamo() {
		return TipoRamo.values();
	}

	public List<TipoOrgao> getTiposOrgao(String idTipoRamo)
			throws AtlasAcessoBancoDadosException {
		TipoRamo tipoRamo = TipoRamo.recuperarPeloCodigo(idTipoRamo);
		List<TipoOrgao> tipoOrgaos = null;
		// A lista foi colocada dentro de um Map pelo fato da view invocar esse
		// método mais de uma vez, sendo uma na renderização do fieldset e outra
		// para preencher os checks. Apenas para evitar consulta desnecessária.
		if (tiposOrgaoRelacionadosEnte.containsKey(tipoRamo)) {
			tipoOrgaos = tiposOrgaoRelacionadosEnte.get(tipoRamo);
		} else {
			tipoOrgaos = tipoOrgaoServico
					.recuperarTiposOrgaoRelacionadosEnteIndicadorMapa(Integer.parseInt(idTipoRamo),
							IndicadorMapa.CONSUMIDOR);
			tiposOrgaoRelacionadosEnte.put(tipoRamo, tipoOrgaos);
		}

		return tipoOrgaos;
	}

	public List<String> getTiposOrgaoSelecionados() {
		return tiposOrgaoSelecionados;
	}

	private List<Integer> getTiposOrgaoSelecionadosInteger() {
		List<Integer> tiposOrgaoSelecionadosInteger = new ArrayList<Integer>();
		if (tiposOrgaoSelecionados != null && !tiposOrgaoSelecionados.isEmpty()) {
			for (String idTipoOrgao : tiposOrgaoSelecionados) {
				tiposOrgaoSelecionadosInteger.add(Integer.valueOf(idTipoOrgao));
			}
		}
		return tiposOrgaoSelecionadosInteger;
	}

	public void setTiposOrgaoSelecionados(List<String> tiposOrgaoSelecionados) {
		this.tiposOrgaoSelecionados = tiposOrgaoSelecionados;
	}

	public TreeNode getRootTotalizadorBrasil() throws AtlasAcessoBancoDadosException {
		if(rootTotalizadorBrasil == null){
			carregarArvoreTotalizadorBrasil();
		}
		return rootTotalizadorBrasil;
	}

	public void setRootTotalizadorBrasil(TreeNode rootTotalizadorBrasil) {
		this.rootTotalizadorBrasil = rootTotalizadorBrasil;
	}

	public TreeNode getRootTotalizadorRegiao() throws AtlasAcessoBancoDadosException {
		if(rootTotalizadorRegiao == null){
			carregarArvoreTotalizadorRegiao();
		}
		return rootTotalizadorRegiao;
	}

	public void setRootTotalizadorRegiao(TreeNode rootTotalizadorRegiao) {
		this.rootTotalizadorRegiao = rootTotalizadorRegiao;
	}

}
