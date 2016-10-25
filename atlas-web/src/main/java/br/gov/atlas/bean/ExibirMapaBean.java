package br.gov.atlas.bean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.map.OverlaySelectEvent;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;

import com.google.code.geocoder.Geocoder;
import com.google.code.geocoder.GeocoderRequestBuilder;
import com.google.code.geocoder.model.GeocodeResponse;
import com.google.code.geocoder.model.GeocoderRequest;
import com.google.code.geocoder.model.GeocoderStatus;

import br.gov.atlas.entidade.Municipio;
import br.gov.atlas.entidade.Orgao;
import br.gov.atlas.entidade.Problema;
import br.gov.atlas.entidade.UF;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;
import br.gov.atlas.exception.AtlasNegocioException;
import br.gov.atlas.servico.AtlasServicoImpl;
import br.gov.atlas.servico.MunicipioServico;
import br.gov.atlas.servico.OrgaoServico;
import br.gov.atlas.servico.UFServico;
import br.gov.atlas.servico.WebMailServico;
import br.gov.atlas.util.GrupoOrgaoMarkerIcon;
import br.gov.atlas.util.RelatorioUtil;

public abstract class ExibirMapaBean extends AtlasBean {

	private static final long serialVersionUID = -9180126522393607105L;

	private MapModel mapModel;

	private Marker marker;

	private String rota;

	private Integer zoom = 4;

	private Double latitudeBase = -14.5983942;

	private Double longitudeBase = -56.7043044;

	private Boolean exibirOrigem;

	private Boolean podeImprimirRota;

	private String enderecoOrigem;

	private boolean localizou;

	private List<String> tiposErro;

	private List<UF> ufs;

	private Orgao orgaoSelecionado;

	private String comentarioErro;

	private UF uf;

	private Municipio municipio;

	private List<Orgao> resultadoPesquisa;

	private String geolocalizacao;

	@EJB(name = "UFServico")
	private UFServico ufServico;

	@EJB(name = "MunicipioServico")
	private MunicipioServico municipioServico;

	@EJB(name = "OrgaoServico")
	private OrgaoServico orgaoServico;

	@EJB(name = "WebMailServico")
	private WebMailServico mailServico;

	public void init() throws AtlasAcessoBancoDadosException {
		mapModel = new DefaultMapModel();
		setExibirOrigem(false);
		carregarUfs();
	}

	@SuppressWarnings("unchecked")
	private void carregarUfs() throws AtlasAcessoBancoDadosException {
		this.ufs = ufServico.buscarTodos(UF.class, "nome");
	}

	public void onMarkerSelect(OverlaySelectEvent event) {
		setMarker((Marker) event.getOverlay());
		if(getMarker() != null){
			setOrgaoSelecionado((Orgao) getMarker().getData());
		}
		setExibirOrigem(false);
	}

	public void onRowSelect(SelectEvent event) {
		Integer idOrgao = ((Orgao) event.getObject()).getId();
		RequestContext context = RequestContext.getCurrentInstance();

		context.execute(String.format("simularClick('%d')", idOrgao));
	}

	public void calcularRota() throws AtlasNegocioException, IOException,
	InterruptedException {
		if (getEnderecoOrigem() == null || getEnderecoOrigem().trim().isEmpty()) {
			error("MSG_ENDERECO_ORIGEM_OBRIGATORIO");
		} else {

			com.google.code.geocoder.model.LatLng pontoInicial = null;
			LatLng pontoFinal = null;
			GeocodeResponse geocoderResponse = encontrarEndereco(getEnderecoOrigem());
			Thread.sleep(2000);
			if (geocoderResponse != null) {

				if (geocoderResponse.getStatus().equals(
						GeocoderStatus.ZERO_RESULTS)) {
					error("MSG_ENDERECO_NAO_ENCONTRADO");
					setPodeImprimirRota(false);
				} else {
					if (geocoderResponse.getResults().size() > 0) {
						pontoInicial = geocoderResponse.getResults().get(0)
								.getGeometry().getLocation();
						pontoFinal = getMarker().getLatlng();
						RequestContext.getCurrentInstance().execute(
								"desenharRota(" + pontoInicial.getLat() + ","
										+ pontoInicial.getLng() + ","
										+ pontoFinal.getLat() + ","
										+ pontoFinal.getLng() + ")");
					}
					setPodeImprimirRota(true);
					RequestContext.getCurrentInstance().execute(
							"dialogComoChegarWidge.hide()");
				}
			} else {
				error("MSG3");
			}
		}
		setEnderecoOrigem("");

	}

	public GeocodeResponse encontrarEndereco(String endereco) {
		Geocoder geocoder = new Geocoder();
		GeocoderRequest geocoderRequest = new GeocoderRequestBuilder()
				.setAddress(endereco + " , República Federativa do Brasil")
				.getGeocoderRequest();
		geocoderRequest.setRegion("BR");
		geocoderRequest.setLanguage("pt-BR");
		return geocoder.geocode(geocoderRequest);

	}

	public void adicionarMarcador(Orgao orgao) {
		if (orgao.getLatitude() != null && orgao.getLongitude() != null) {
			Marker marker = new Marker(new LatLng(orgao.getLatitude()
					.doubleValue(), orgao.getLongitude().doubleValue()),
					orgao.getNome());
			marker.setData(orgao);
			marker.setZindex(orgao.getId());
			marker.setIcon(GrupoOrgaoMarkerIcon.getMarkerIcon(orgao
					.getTipoOrgao().getRamo().getId()));
			getMapModel().addOverlay(marker);
			orgao.setUrlImagem(marker.getIcon());
		}
	}

	public String novoOrgao() {
		setLocalizou(true);

		CadastrarOrgaoBean cadastrarBean = getBean("cadastrarOrgaoBean");

		cadastrarBean.setUf(getUf());
		cadastrarBean.getEntidade().setMunicipio(getMunicipio());

		return "/pub/sobre/portasDaJustica/informarPortasDaJustica.faces";
	}

	public void limpar() {
		setLocalizou(false);
		setExibirOrigem(false);
		setMunicipio(null);
		setUf(null);

		if (getMapModel().getMarkers() != null) {
			getMapModel().getMarkers().clear();
		}

		String clientId = "form:directionsPanel";
		UIComponent comp = FacesContext.getCurrentInstance().getViewRoot()
				.findComponent(clientId);
		comp.getChildren().clear();
		setPodeImprimirRota(false);

	}

	public void gerarRelatorioRota() throws Exception {
		String conteudoHTML = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap().get("rota");
		Map<String, Object> parametros = new HashMap<>();
		parametros.put("ROTA", conteudoHTML);
		getRota();
		RelatorioUtil.gerarRelatorio(null, "/relatorios/relatorioRota.jasper",
				"relatorioRota.pdf", parametros);

	}

	public List<Municipio> getMunicipios()
			throws AtlasAcessoBancoDadosException {
		municipio = null;
		if (getUf() != null) {
			return municipioServico.recuperarMunicipios(getUf());
		} else {
			return null;
		}
	}

	public void enviarErro() throws AtlasAcessoBancoDadosException,
	AtlasNegocioException {
		Orgao orgao = (Orgao) getMarker().getData();
		Problema problema = null;
		if (getTiposErro().isEmpty()) {
			problema = new Problema();
			problema.setOrgao(orgao);
			problema.setDescricao(getComentarioErro());
			problema.setTipoProblema("3");
			getOrgaoServico().salvar(problema);
		} else {
			for (String tipoErro : getTiposErro()) {
				problema = new Problema();
				problema.setOrgao(orgao);
				problema.setDescricao(getComentarioErro());
				problema.setTipoProblema(tipoErro);
				getOrgaoServico().salvar(problema);
			}
		}
		setTiposErro(new ArrayList<String>());
		setComentarioErro("");
		RequestContext.getCurrentInstance().execute("dlg1.hide()");

		info("MSG14");

		// Envia email para MJ informando o cadastro de um novo problema
		try {
			mailServico.sendInformarProblema(problema);
		} catch (AddressException e) {
			error(null, "MSG0", e.getMessage());
			e.printStackTrace();
		} catch (MessagingException e) {
			error(null, "MSG0", e.getMessage());
			e.printStackTrace();
		} catch (Exception e){
			error(null, "MSG0", e.getMessage());
			e.printStackTrace();
		}

	}

	public Marker getMarker() {
		return marker;
	}

	public MapModel getMapModel() {
		return mapModel;
	}

	public void setMapModel(MapModel mapModel) {
		this.mapModel = mapModel;
	}

	public void setMarker(Marker marker) {
		this.marker = marker;
	}

	@Override
	protected void efetuarPesquisa() throws AtlasAcessoBancoDadosException {
		// TODO Auto-generated method stub

	}

	@Override
	protected AtlasServicoImpl getServico() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getTelaPesquisa() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getTelaCadastro() {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer getZoom() {
		return zoom;
	}

	public void setZoom(Integer zoom) {
		this.zoom = zoom;
	}

	public Double getLatitudeBase() {
		return latitudeBase;
	}

	public void setLatitudeBase(Double latitudeBase) {
		this.latitudeBase = latitudeBase;
	}

	public Double getLongitudeBase() {
		return longitudeBase;
	}

	public void setLongitudeBase(Double longitudeBase) {
		this.longitudeBase = longitudeBase;
	}

	public String getRota() {
		return rota;
	}

	public void setRota(String rota) {
		this.rota = rota;
	}

	public Boolean getExibirOrigem() {
		return exibirOrigem;
	}

	public void setExibirOrigem(Boolean exibirOrigem) {
		this.exibirOrigem = exibirOrigem;
	}

	public void inverterExibirOrigem() {
		this.exibirOrigem = !exibirOrigem;
	}

	public Boolean getPodeImprimirRota() {
		return podeImprimirRota;
	}

	public void setPodeImprimirRota(Boolean podeImprimirRota) {
		this.podeImprimirRota = podeImprimirRota;
	}

	public String getEnderecoOrigem() {
		return enderecoOrigem;
	}

	public void setEnderecoOrigem(String enderecoOrigem) {
		this.enderecoOrigem = enderecoOrigem;
	}

	public UF getUf() {
		return uf;
	}

	public void setUf(UF uf) {
		this.uf = uf;
	}

	public Municipio getMunicipio() {
		return municipio;
	}

	public void setMunicipio(Municipio municipio) {
		this.municipio = municipio;
	}

	public boolean isLocalizou() {
		return localizou;
	}

	public void setLocalizou(boolean localizou) {
		this.localizou = localizou;
	}

	public List<String> getTiposErro() {
		return tiposErro;
	}

	public void setTiposErro(List<String> tiposErro) {
		this.tiposErro = tiposErro;
	}

	public Orgao getOrgaoSelecionado() {
		return orgaoSelecionado;
	}

	public void setOrgaoSelecionado(Orgao orgaoSelecionado) {
		this.orgaoSelecionado = orgaoSelecionado;
	}

	public String getComentarioErro() {
		return comentarioErro;
	}

	public void setComentarioErro(String comentarioErro) {
		this.comentarioErro = comentarioErro;
	}

	public List<UF> getUfs() {
		return ufs;
	}

	public OrgaoServico getOrgaoServico() {
		return orgaoServico;
	}

	public List<Orgao> getResultadoPesquisa() {
		return resultadoPesquisa;
	}

	public void setResultadoPesquisa(List<Orgao> resultadoPesquisa) {
		this.resultadoPesquisa = resultadoPesquisa;
	}

	public String getGeolocalizacao() {
		return geolocalizacao;
	}

	public void setGeolocalizacao(String geolocalizacao)
			throws AtlasAcessoBancoDadosException {
		this.geolocalizacao = geolocalizacao;

		geolocalizacao = geolocalizacao.replaceAll(", ", ",");
		String[] split = geolocalizacao.split(",");
		geolocalizacao = split[0];
		if(geolocalizacao != null && geolocalizacao.contains(" - ")){
			split = geolocalizacao.split(" - ");
		}
		String nomeMunicipio = split[0];
		String siglaUf = split[1];

		setUf(ufServico.recuperarUFPorSigla(siglaUf));
		if (getUf() != null) {
			setMunicipio(municipioServico.recuperPorNomeMunicipioSiglaUf(
					nomeMunicipio, siglaUf));
		}
	}
}
