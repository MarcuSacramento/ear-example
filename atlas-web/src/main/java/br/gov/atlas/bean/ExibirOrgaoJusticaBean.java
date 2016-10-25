package br.gov.atlas.bean;

import java.text.ParseException;
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

import org.primefaces.event.TabChangeEvent;

import br.gov.atlas.entidade.Municipio;
import br.gov.atlas.entidade.Orgao;
import br.gov.atlas.entidade.Tema;
import br.gov.atlas.entidade.TipoOrgao;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;
import br.gov.atlas.servico.ManterTemaServico;
import br.gov.atlas.servico.RepresentanteServico;
import br.gov.atlas.servico.TipoOrgaoServico;
import br.gov.atlas.servico.UFServico;
import br.gov.atlas.util.RelatorioUtil;

@ManagedBean(name = "exibirOrgaoJusticaBean")
@ViewScoped
public class ExibirOrgaoJusticaBean extends ExibirMapaBean {

	private static final long serialVersionUID = -9180126522393607105L;

	@EJB(name = "UFServico")
	private UFServico ufServico;

	@EJB(name = "TipoOrgaoServico")
	private TipoOrgaoServico tipoOrgaoServico;

	@EJB(name = "ManterTemaServico")
	private ManterTemaServico temaServico;

	@EJB(name = "RepresentanteServico")
	private RepresentanteServico representanteServico;

	private String siglaUF;

	private List<TipoOrgaoSelecao> tipoOrgaoSelecao;

	private List<TemaSelecao> temaSelecao;

	private String tipoRelatorio;

	private int abaAtual;

	public String getTipoRelatorio() {
		return tipoRelatorio;
	}

	public void setTipoRelatorio(String tipoRelatorio) {
		this.tipoRelatorio = tipoRelatorio;
	}

	@Override
	@SuppressWarnings("unchecked")
	@PostConstruct
	public void init() {
		try {
			super.init();
			List<TipoOrgao> tipoOrgaos = tipoOrgaoServico
					.recuperarTiposOrgaoValidos();
			this.tipoOrgaoSelecao = new ArrayList<TipoOrgaoSelecao>();
			for (TipoOrgao tipoOrgao : tipoOrgaos) {
				this.tipoOrgaoSelecao.add(new TipoOrgaoSelecao(tipoOrgao));
			}

			String sigla = (String) getParameter("UF");
			if (sigla != null) {
				setUf(ufServico.recuperarUFPorSigla(sigla));
			}

			List<Tema> temas = temaServico.buscarTodos(Tema.class, "nome");
			this.temaSelecao = new ArrayList<TemaSelecao>();
			for (Tema tema : temas) {
				this.temaSelecao.add(new TemaSelecao(tema));
			}
		} catch (AtlasAcessoBancoDadosException e) {
			error(e);
		}

	}

	@Override
	public void limpar() {
		init();
		setResultadoPesquisa(new ArrayList<Orgao>() );
	}

	public Boolean desabilitarBotaoLocalizar() {
		boolean isMunicipioNaoInformado = getMunicipio() == null;
		boolean isTipoOrgaoNaoInformado = this.getTipoOrgaoSelecionados() == null
				|| this.getTipoOrgaoSelecionados().isEmpty();
		boolean isTemaNaoInformado = this.getTemasSelecionados() == null
				|| this.getTemasSelecionados().isEmpty();
		boolean isDesabilitarBotao = isMunicipioNaoInformado
				|| (isTipoOrgaoNaoInformado && isTemaNaoInformado);
		return isDesabilitarBotao;
	}

	public void carregarOrgaos() throws AtlasAcessoBancoDadosException,
	ParseException {
		setLocalizou(true);

		Municipio municipio = getMunicipio();

		boolean isTipoOrgaoSelecionado = this.getTipoOrgaoSelecionados() != null
				&& !this.getTipoOrgaoSelecionados().isEmpty();
		boolean isTemaSelecionado = this.getTemasSelecionados() != null
				&& !this.getTemasSelecionados().isEmpty();

		if (municipio != null && (isTipoOrgaoSelecionado || isTemaSelecionado)) {
			if (isTemaSelecionado) {
				setResultadoPesquisa(getOrgaoServico().recuperarOrgaosPorTema(
						municipio, this.getTemasSelecionados()));
			} else {
				setResultadoPesquisa(getOrgaoServico().recuperarOrgaos(
						municipio, this.getTipoOrgaoSelecionados()));
			}

			if (getMapModel().getMarkers() != null) {
				getMapModel().getMarkers().clear();
			}
			setPodeImprimirRota(false);

			if (getResultadoPesquisa() == null || getResultadoPesquisa().isEmpty()) {
				error("MSG_REGISTROS_NAO_ENCONTRADOS");

			} else {
				for (Orgao orgao : getResultadoPesquisa()) {
					orgao.setTelefones(getOrgaoServico().recuperarTelefones(
							orgao));
					orgao.setRepresentantes(representanteServico
							.buscarDivulgaveis(orgao));
					// Caso o órgão não possua LATITUDE e LONGITUDE, será
					// informada a coordenada do município.
					if (orgao.getLatitude() == null
							&& orgao.getLongitude() == null) {
						orgao.setLatitude(municipio.getLatitude());
						orgao.setLongitude(municipio.getLongitude());
					}
					adicionarMarcador(orgao);
				}
			}

			setLatitudeBase(municipio.getLatitude().doubleValue());
			setLongitudeBase(municipio.getLongitude().doubleValue());
			setZoom(12);

			String clientId = "form:directionsPanel";
			UIComponent comp = FacesContext.getCurrentInstance().getViewRoot()
					.findComponent(clientId);
			if (comp != null) {
				comp.getChildren().clear();
			}
			info("MSG_REPORTE_INF_MAPA");

		} else if (this.getTipoOrgaoSelecionados().isEmpty()) {
			error("MSG_TIPO_ORGAO_OBRIGATORIO");

		} else {
			error("MSG1");
		}

	}

	private List<Tema> getTemasSelecionados() {
		List<Tema> result = new ArrayList<>();
		try {
			for (TemaSelecao temaSelecao : getTemaSelecao()) {
				if (temaSelecao.getSelecionado()) {
					result.add(temaSelecao.getTema());
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

	private List<TipoOrgao> getTipoOrgaoSelecionados() {
		List<TipoOrgao> result = new ArrayList<>();
		try {
			for (TipoOrgaoSelecao tipoOrgaoSelecao : getTipoOrgaoSelecao()) {
				if (tipoOrgaoSelecao.getSelecionado()) {
					result.add(tipoOrgaoSelecao.getTipoOrgao());
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;

	}

	public boolean isAbaTemaDesabilitada() {
		return this.getTipoOrgaoSelecionados() != null
				&& !this.getTipoOrgaoSelecionados().isEmpty();
	}

	public boolean isAbaTipoOrgaoDesabilitada() {
		return this.getTemasSelecionados() != null
				&& !this.getTemasSelecionados().isEmpty();
	}

	public void onTabChange(TabChangeEvent event) {
		String activeTab = event.getTab().getId();

		int abaAtiva = 0;

		// Realiza um loop para identificar qual é a tab que foi selecionada.
		// Obs.: As tabs filhas devem ter um id definido, para que seja
		// facilitada a busca
		// e o entendimento pois o JSF por padrão coloca IDs com nomes que ele
		// mesmo escolhe
		// para as tabs.
		for (UIComponent comp : event.getTab().getParent().getChildren()) {
			if (comp.getId().equals(activeTab)) {
				break;
			}
			abaAtiva++;
		}

		System.out.println("ID da Tab Atual: " + event.getTab().getId());
		System.out.println("Index da Tab Atual: " + abaAtiva);

		setAbaAtual(abaAtiva);
	}

	public int getAbaAtual() {
		return abaAtual;
	}

	public void setAbaAtual(int abaAtual) {
		this.abaAtual = abaAtual;
	}

	public void gerarRelatorioOrgaos() throws Exception {
		Map<String, Object> parametros = new HashMap<>();
		parametros.put("ESTADO", getUf().getNome());
		parametros.put("MUNICIPIO", getMunicipio().getNome());

		String caminhoRelatorio = definirCaminhoRelatorio();
		RelatorioUtil.gerarRelatorio(getResultadoPesquisa(), caminhoRelatorio,
				"relatorioOrgaosJustica." + getTipoRelatorio(), parametros,
				getTipoRelatorio());
	}

	private String definirCaminhoRelatorio() {
		StringBuilder caminhoRelatorio = new StringBuilder("/relatorios/");
		if (RelatorioUtil.XLS.equals(getTipoRelatorio())) {
			caminhoRelatorio.append("relatorioOrgaosJusticaXLS_SemConn");
		} else {
			caminhoRelatorio.append("relatorioOrgaosJustica_SemConn");
		}
		caminhoRelatorio.append(".jasper");
		return caminhoRelatorio.toString();
	}

	public String getSiglaUF() {
		return siglaUF;
	}

	public void setSiglaUF(String siglaUF) {
		this.siglaUF = siglaUF;
	}

	public class TemaSelecao {
		Tema tema;
		Boolean selecionado;

		public TemaSelecao(Tema tema) {
			this.tema = tema;
			this.selecionado = false;
		}

		public Tema getTema() {
			return tema;
		}

		public void setTema(Tema tema) {
			this.tema = tema;
		}

		public Boolean getSelecionado() {
			return selecionado;
		}

		public void setSelecionado(Boolean selecionado) {
			this.selecionado = selecionado;
		}
	}

	public List<TemaSelecao> getTemaSelecao() {
		return temaSelecao;
	}

	public void setTemaSelecao(List<TemaSelecao> temaSelecao) {
		this.temaSelecao = temaSelecao;
	}

	public class TipoOrgaoSelecao {
		TipoOrgao tipoOrgao;
		Boolean selecionado;

		public TipoOrgaoSelecao(TipoOrgao tipoOrgao)
				throws AtlasAcessoBancoDadosException {
			this.tipoOrgao = tipoOrgao;
			this.selecionado = false;
		}

		public TipoOrgao getTipoOrgao() {
			return tipoOrgao;
		}

		public void setTipoOrgao(TipoOrgao tipoOrgao) {
			this.tipoOrgao = tipoOrgao;
		}

		public Boolean getSelecionado() {
			return selecionado;
		}

		public void setSelecionado(Boolean selecionado) {
			this.selecionado = selecionado;
		}

	}

	public List<TipoOrgaoSelecao> getTipoOrgaoSelecao() {
		if(tipoOrgaoSelecao==null){
			tipoOrgaoSelecao = new ArrayList<>();
		}
		return tipoOrgaoSelecao;
	}

	public void setTipoOrgaoSelecao(List<TipoOrgaoSelecao> tipoOrgaoSelecao) {
		this.tipoOrgaoSelecao = tipoOrgaoSelecao;
	}

	public List<TipoOrgaoSelecao> getTipoOrgaoSelecao(String idRamo) {
		List<TipoOrgaoSelecao> result = new ArrayList<TipoOrgaoSelecao>();

		try {
			for (TipoOrgaoSelecao tipoOrgaoSelecao : getTipoOrgaoSelecao()) {

				if (tipoOrgaoSelecao.getTipoOrgao().getRamo()
						.getId().equals(Integer.parseInt(idRamo))) {
					result.add(tipoOrgaoSelecao);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

}
