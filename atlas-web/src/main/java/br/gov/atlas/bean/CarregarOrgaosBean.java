package br.gov.atlas.bean;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.ws.rs.core.Response.Status;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.omnifaces.util.Faces;
import org.primefaces.event.map.OverlaySelectEvent;
import org.primefaces.model.map.Circle;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;

import br.gov.atlas.cargaTemporaria.entidade.OABSeccional;
import br.gov.atlas.dto.OrgaoDTO;
import br.gov.atlas.entidade.Municipio;
import br.gov.atlas.entidade.Orgao;
import br.gov.atlas.entidade.Ramo;
import br.gov.atlas.entidade.Tema;
import br.gov.atlas.entidade.TipoOrgao;
import br.gov.atlas.entidade.UF;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;
import br.gov.atlas.exception.AtlasNegocioException;
import br.gov.atlas.servico.ManterRamoServico;
import br.gov.atlas.servico.ManterTemaServico;
import br.gov.atlas.servico.MunicipioServico;
import br.gov.atlas.servico.OrgaoServico;
import br.gov.atlas.servico.TipoOrgaoServico;
import br.gov.atlas.servico.UFServico;

import com.google.code.geocoder.Geocoder;
import com.google.code.geocoder.GeocoderRequestBuilder;
import com.google.code.geocoder.model.GeocodeResponse;
import com.google.code.geocoder.model.GeocoderAddressComponent;
import com.google.code.geocoder.model.GeocoderRequest;
import com.google.code.geocoder.model.GeocoderResult;
import com.google.code.geocoder.model.GeocoderStatus;

@ManagedBean(name = "carregarOrgaosBean")
@ViewScoped
public class CarregarOrgaosBean extends AtlasFacesBean {

	private static final long serialVersionUID = -9180126522393607105L;

	@EJB(name = "UFServico")
	private UFServico ufServico;

	@EJB(name = "MunicipioServico")
	private MunicipioServico municipioServico;

	@EJB(name = "OrgaoServico")
	private OrgaoServico orgaoServico;

	@EJB(name = "TipoOrgaoServico")
	private TipoOrgaoServico tipoOrgaoServico;
	
	@EJB(name = "ManterTemaServico")
	private ManterTemaServico temaServico;
	
	@EJB(name = "ManterRamoServico")
	private ManterRamoServico ramoServico;

	private MapModel mapModel;

	String url = "http://maps.googleapis.com/maps/api/geocode/json?";

	Double latitudeBase = -15.79713;

	Double longitudeBase = -47.865918;

	private Marker marker;

	Integer zoom = 5;

	private UF uf;

	private Municipio municipio;

	private List<UF> ufs;

	private Municipio municipioPesquisa;

	private TipoOrgao tipoOrgaoPesquisa;

	private List<TipoOrgao> tipoOrgaos;
	
	private Tema tema;
	
	private List<Tema> temas;
	
	private Ramo ramo;
	
	private List<Ramo> ramos;

	private List<Orgao> orgaosNaoEncontrados;

	private int numeroInicioRegistro;

	private int quantidadeRegistros;
	
	private String visualizacaoMapa;
	
	private String[] parametrosPesquisaMapa;

	@SuppressWarnings("unchecked")
	public void init() throws AtlasAcessoBancoDadosException {
		mapModel = new DefaultMapModel();
		numeroInicioRegistro = orgaoServico.getIdProximoRegistroAPreencher();
		quantidadeRegistros = 2000;
		this.ufs = ufServico.buscarTodos(UF.class, "nome");
		setTipoOrgaos(tipoOrgaoServico.recuperarTiposOrgaoValidos());
	}
	
	public void carregarCombos() throws AtlasAcessoBancoDadosException {
		this.ramos = ramoServico.buscarTodos(Ramo.class);
		/* O Atlas somente com 'JUDICIAL, ESSENCIAL À JUSTIÇA, EXTRAJUDICIAL'
		 * Os outros ramos cadastrados no banco não serão usados por enquanto.
		 */
		while (ramos.size()>3) {
			ramos.remove(ramos.size()-1);
		}
		this.temas = temaServico.buscarTodos(Tema.class);
	}
	
	public MapModel plotarOrgaos() throws AtlasAcessoBancoDadosException, AtlasNegocioException, IOException,
	InterruptedException {
		List<Orgao> orgaos = null;
		OrgaoDTO orgaoFiltro = new OrgaoDTO();
		orgaoFiltro.setTema(tema);
		orgaoFiltro.setRamo(ramo);
		int orgaosSemLatLng = 0;
		if(tema == null & ramo == null){
			return new DefaultMapModel();
		}
		orgaoFiltro.setTipoOrgao(tipoOrgaoPesquisa);
		orgaos = orgaoServico.recuperarLonLatOrgaos(orgaoFiltro);
		try {
			this.mapModel = new DefaultMapModel();
			for (Orgao orgao : orgaos) {
				if(orgao.getLatitude()==null || orgao.getLongitude()==null){
					orgaosSemLatLng++;
					continue;
				}
				LatLng latlng = new LatLng(orgao.getLatitude().doubleValue(),
						orgao.getLongitude().doubleValue());
				if (visualizacaoMapa!= null && visualizacaoMapa.equals("R")) {
					Circle circle = new Circle(latlng, 40000);
					circle.setStrokeColor("red");
					circle.setFillColor("#red");
					circle.setFillOpacity(0.0);
					circle.setData(orgao);
					circle.setZindex(orgao.getId());
					mapModel.addOverlay(circle);
				} else {
					Marker marker = new Marker(latlng, orgao.getNomeReduzido());
					mapModel.addOverlay(marker);
				}
			}
		} catch (Exception e) {
			getFacesContext().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Não foi possível plotar.", null));

		}
		getFacesContext().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO, "Quantidade de órgãos pesquisados: " + orgaos.size(),null));
		if(orgaosSemLatLng>0){
			getFacesContext().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_WARN, "Quantidade de órgãos que não tem informações de Latitude e Longitude: "+ orgaosSemLatLng, null));
					}
		return this.mapModel;
	}
	
	public boolean desabilitarBotaoPlotar(){
		if(this.ramo != null || this.tema != null){
			return false;
		}
		return true;
	}
	
	public void limparSelecaoComboRamos(){
		this.ramo = null;
	}
	
	public void limparSelecaoComboTemas(){
		this.tema = null;
	}

	public void carregarCoordenadasMunicipios() throws AtlasAcessoBancoDadosException, AtlasNegocioException, IOException, InterruptedException {
		System.out.print("teste");
		List<Municipio> municipios = municipioServico.recuperarMunicipiosSemCoordenadas();

		int i = 0;
		for (Municipio municipio : municipios) {
			String endereco = municipio.getNome() + " - " + municipio.getUf().getNome() + " , República Federativa do Brasil";

			GeocodeResponse geocoderResponse = encontrarEndereco(endereco);

			if (geocoderResponse.getStatus().equals(GeocoderStatus.OK)) {
				GeocoderResult result = geocoderResponse.getResults().get(0);

				municipio.setLatitude(result.getGeometry().getLocation().getLat());
				municipio.setLongitude(result.getGeometry().getLocation().getLng());
				municipioServico.salvar(municipio);
			}

			i++;
			if (i > quantidadeRegistros) {
				break;
			}
		}

	}

	public void carregarOrgaosPorFiltro() throws AtlasAcessoBancoDadosException, AtlasNegocioException, IOException, InterruptedException{
		int encontrados = 0;
		int encontradosPorAproximacao = 0;
		int naoEncontrados = 0;
		int enderecoVazio = 0;
		mapModel.getMarkers().clear();
		orgaosNaoEncontrados = new ArrayList<Orgao>();
		try{
			if((municipioPesquisa != null || uf != null) && tipoOrgaoPesquisa != null){
				int qtdMaximaRegistros = 200;
				List<TipoOrgao> tiposOrgao = new ArrayList<>();
				tiposOrgao.add(tipoOrgaoPesquisa);
				List<Orgao> orgaosFiltro = null;
				if(municipioPesquisa != null){
					orgaosFiltro = orgaoServico.recuperarOrgaosSemAtualizacao(municipioPesquisa, tiposOrgao, qtdMaximaRegistros);
				}else{
					orgaosFiltro = orgaoServico.recuperarOrgaosSemAtualizacao(uf, tiposOrgao, qtdMaximaRegistros);
				}
				if(orgaosFiltro != null && !orgaosFiltro.isEmpty()){
					// Percorrendo os órgãos encontrados sem atualização
					for (Orgao orgao : orgaosFiltro) {
						boolean aproximacao = false;
						if (orgao.getMunicipio() == null || orgao.getEndereco() == null || orgao.getEndereco().trim().isEmpty()) {
							// ENDEREÇO INCOMPLETO - FALTAM CAMPOS NO BANCO -
							// ADICIONAR A LISTA DE NÃO ENCONTRADOS
							orgaosNaoEncontrados.add(orgao);
							enderecoVazio++;
						} else {
							String endereco = orgao.getEndereco() + " - " + orgao.getMunicipio().getNome() + " - "
									+ orgao.getMunicipio().getUf().getSigla();
							GeocodeResponse geocoderResponse = encontrarEndereco(endereco);

							if (geocoderResponse.getStatus().equals(GeocoderStatus.ZERO_RESULTS)) {
								// O ENDEREÇO COMPLETO NÃO FOI ENCONTRADO - TENTAR
								// SOMENTE COM MUNICIPIO E UF
								endereco = orgao.getMunicipio() + " - " + orgao.getMunicipio().getUf().getSigla();
								geocoderResponse = encontrarEndereco(endereco);
								aproximacao = true;

							}

							if (geocoderResponse.getStatus().equals(GeocoderStatus.ZERO_RESULTS)) {
								// ENDEREÇO NÃO ENCONTRADO - ADICIONAR A LISTA DE
								// NÃO ENCONTRADOS
								orgaosNaoEncontrados.add(orgao);
								naoEncontrados++;

							} else {

								GeocoderResult result = geocoderResponse.getResults().get(0);

								if (validarResultado(result, orgao)) {
									// RESULTADO VÁLIDO - ADICIONAR PONTO NO MAPA
									if (aproximacao) {
										encontradosPorAproximacao++;
									} else {
										encontrados++;
									}
									orgao.setLatitude(result.getGeometry().getLocation().getLat());
									orgao.setLongitude(result.getGeometry().getLocation().getLng());
									orgao.setDataAtualizacao(new Date());
									orgaoServico.salvar(orgao);
									adicionarMarcador(result, endereco, aproximacao);
								} else {
									// RESULTADO INVÁLIDO - TENTAR NOVAMENTE SOMENTE
									// COM MUNÍCIPIO E UF
									endereco = orgao.getMunicipio().getNome() + " - " + orgao.getMunicipio().getUf().getSigla();
									aproximacao = true;
									geocoderResponse = encontrarEndereco(endereco);

									if (geocoderResponse.getStatus().equals(GeocoderStatus.ZERO_RESULTS)) {
										// ENDEREÇO NÃO ENCONTRADO - ADICIONAR A
										// LISTA DE NÃO ENCONTRADOS
										orgaosNaoEncontrados.add(orgao);
										naoEncontrados++;
									} else {

										result = geocoderResponse.getResults().get(0);

										if (validarResultado(result, orgao)) {
											// RESULTADO VÁLIDO - ADICIONAR PONTO NO
											// MAPA
											if (aproximacao) {
												encontradosPorAproximacao++;
											} else {
												encontrados++;
											}
											orgao.setLatitude(result.getGeometry().getLocation().getLat());
											orgao.setLongitude(result.getGeometry().getLocation().getLng());
											orgao.setDataAtualizacao(new Date());
											orgaoServico.salvar(orgao);
											adicionarMarcador(result, endereco, aproximacao);
										} else {
											// RESULTADO INVÁLIDO - DESISTIR DESTE
											// ÓRGÃO E ADICIONAR A LISTA DE NÃO
											// ENCONTRADOS
											orgaosNaoEncontrados.add(orgao);
											naoEncontrados++;
											System.out.println(endereco);

										}
									}
								}
							}
						}
					}
				}
			}
		} catch (IndexOutOfBoundsException e) {
			getFacesContext().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Não foi possível enviar requisição de Geocode.", null));

		} finally {
			numeroInicioRegistro = orgaoServico.getIdProximoRegistroAPreencher();
			getFacesContext().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, "Encontrados: " + encontrados + "  Encontrados por aproximação: "
							+ encontradosPorAproximacao + " Não encontrados: " + naoEncontrados + " Endereço vazio: " + enderecoVazio, null));
		}
	}

	public void carregarOrgaos(Boolean carregarTodosVazios) throws AtlasAcessoBancoDadosException, AtlasNegocioException, IOException,
	InterruptedException {
		mapModel.getMarkers().clear();

		int i = getNumeroInicioRegistro();
		int encontrados = 0;
		int encontradosPorAproximacao = 0;
		int naoEncontrados = 0;
		int enderecoVazio = 0;

		orgaosNaoEncontrados = new ArrayList<Orgao>();
		List<Orgao> orgaos = null;

		try {
			while (i < getNumeroInicioRegistro() + getQuantidadeRegistros()) {
				if (carregarTodosVazios) {
					orgaos = orgaoServico.recuperarOrgaosSemLatitude();
				} else {
					if (getQuantidadeRegistros() >= 500) {
						orgaos = orgaoServico.buscarTodos(i, 500);
					} else {
						orgaos = orgaoServico.buscarTodos(i, getQuantidadeRegistros());
					}
				}
				if (orgaos == null || orgaos.size() == 0) {
					break;
				}
				for (Orgao orgao : orgaos) {
					boolean aproximacao = false;
					if (!carregarTodosVazios && i >= getNumeroInicioRegistro() + getQuantidadeRegistros()) {
						break;
					}
					if (orgao.getMunicipio() == null || orgao.getEndereco() == null || orgao.getEndereco().trim().isEmpty()) {
						// ENDEREÇO INCOMPLETO - FALTAM CAMPOS NO BANCO -
						// ADICIONAR A LISTA DE NÃO ENCONTRADOS
						orgaosNaoEncontrados.add(orgao);
						enderecoVazio++;
					} else {
						String endereco = orgao.getEndereco() + " - " + orgao.getMunicipio().getNome() + " - "
								+ orgao.getMunicipio().getUf().getSigla();
						GeocodeResponse geocoderResponse = encontrarEndereco(endereco);

						if (geocoderResponse.getStatus().equals(GeocoderStatus.ZERO_RESULTS)) {
							// O ENDEREÇO COMPLETO NÃO FOI ENCONTRADO - TENTAR
							// SOMENTE COM MUNICIPIO E UF
							endereco = orgao.getMunicipio() + " - " + orgao.getMunicipio().getUf().getSigla();
							geocoderResponse = encontrarEndereco(endereco);
							aproximacao = true;

						}

						if (geocoderResponse.getStatus().equals(GeocoderStatus.ZERO_RESULTS)) {
							// ENDEREÇO NÃO ENCONTRADO - ADICIONAR A LISTA DE
							// NÃO ENCONTRADOS
							orgaosNaoEncontrados.add(orgao);
							naoEncontrados++;

						} else {

							GeocoderResult result = geocoderResponse.getResults().get(0);

							if (validarResultado(result, orgao)) {
								// RESULTADO VÁLIDO - ADICIONAR PONTO NO MAPA
								if (aproximacao) {
									encontradosPorAproximacao++;
								} else {
									encontrados++;
								}
								orgao.setLatitude(result.getGeometry().getLocation().getLat());
								orgao.setLongitude(result.getGeometry().getLocation().getLng());
								orgao.setDataAtualizacao(new Date());
								orgaoServico.salvar(orgao);
								adicionarMarcador(result, endereco, aproximacao);
							} else {
								// RESULTADO INVÁLIDO - TENTAR NOVAMENTE SOMENTE
								// COM MUNÍCIPIO E UF
								endereco = orgao.getMunicipio().getNome() + " - " + orgao.getMunicipio().getUf().getSigla();
								aproximacao = true;
								geocoderResponse = encontrarEndereco(endereco);

								if (geocoderResponse.getStatus().equals(GeocoderStatus.ZERO_RESULTS)) {
									// ENDEREÇO NÃO ENCONTRADO - ADICIONAR A
									// LISTA DE NÃO ENCONTRADOS
									orgaosNaoEncontrados.add(orgao);
									naoEncontrados++;
								} else {

									result = geocoderResponse.getResults().get(0);

									if (validarResultado(result, orgao)) {
										// RESULTADO VÁLIDO - ADICIONAR PONTO NO
										// MAPA
										if (aproximacao) {
											encontradosPorAproximacao++;
										} else {
											encontrados++;
										}
										orgao.setLatitude(result.getGeometry().getLocation().getLat());
										orgao.setLongitude(result.getGeometry().getLocation().getLng());
										orgao.setDataAtualizacao(new Date());
										orgaoServico.salvar(orgao);
										adicionarMarcador(result, endereco, aproximacao);
									} else {
										// RESULTADO INVÁLIDO - DESISTIR DESTE
										// ÓRGÃO E ADICIONAR A LISTA DE NÃO
										// ENCONTRADOS
										orgaosNaoEncontrados.add(orgao);
										naoEncontrados++;
										System.out.println(endereco);

									}
								}

							}
						}
					}
					System.out.println(i);
					i++;
				}
			}
		} catch (IndexOutOfBoundsException e) {
			getFacesContext().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Não foi possível enviar requisição de Geocode.", null));

		} finally {
			numeroInicioRegistro = orgaoServico.getIdProximoRegistroAPreencher();
			getFacesContext().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, "Encontrados: " + encontrados + "  Encontrados por aproximação: "
							+ encontradosPorAproximacao + " Não encontrados: " + naoEncontrados + " Endereço vazio: " + enderecoVazio, null));
		}

	}

	public void adicionarMarcador(GeocoderResult result, String enderecoOriginal, boolean aproximacao) {
		Marker marker = new Marker(new LatLng(result.getGeometry().getLocation().getLat().doubleValue(), result.getGeometry().getLocation().getLng()
				.doubleValue()), "Endereco do banco: " + enderecoOriginal + "\n\nEndereço do google: " + result.getFormattedAddress()
				+ (aproximacao ? "     Endereço aproximado" : ""));
		mapModel.addOverlay(marker);
	}

	public Boolean validarResultado(GeocoderResult result, Orgao cartorio) {
		Boolean valido = false;
		String ufEncontrada = null;
		String cepEncontrado = null;
		String municipioEncontrado = null;
		String ruaEncontrada;
		String numeroEncontrado;
		String paisEncontrado = null;

		for (GeocoderAddressComponent addressComponent : result.getAddressComponents()) {
			if (addressComponent.getTypes().contains("administrative_area_level_1")) {
				ufEncontrada = addressComponent.getShortName();
			}
			if (addressComponent.getTypes().contains("postal_code")) {
				cepEncontrado = addressComponent.getLongName();
			}
			if (addressComponent.getTypes().contains("locality")) {
				municipioEncontrado = addressComponent.getLongName();
			}
			if (addressComponent.getTypes().contains("route")) {
				ruaEncontrada = addressComponent.getLongName();
			}
			if (addressComponent.getTypes().contains("street_number")) {
				numeroEncontrado = addressComponent.getLongName();
			}
			if (addressComponent.getTypes().contains("country")) {
				paisEncontrado = addressComponent.getShortName();
			}

		}

		if (cartorio.getCep() != null && isCepValido(cepEncontrado) && isCepValido(cartorio.getCep().toString())) {
			if (paisEncontrado.equals("BR") && cartorio.getCep().toString().equals(cepEncontrado.replace("-", ""))) {
				valido = true;
			}
		}

		if (municipioEncontrado != null
				&& paisEncontrado.equals("BR")
				&& Normalizer.normalize(cartorio.getMunicipio().getNome(), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").toUpperCase()
				.equals(Normalizer.normalize(municipioEncontrado, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").toUpperCase())
				&& (cartorio.getMunicipio().getUf().getSigla().toUpperCase().equals(ufEncontrada.toUpperCase()) || cartorio.getMunicipio().getUf()
						.getNome().toUpperCase().equals(ufEncontrada.toUpperCase()))) {
			valido = true;
		}

		return valido;
	}

	public Boolean isCepValido(String cep) {
		return cep != null && (cep.length() == 8 || cep.replace("-", "").length() == 8);
	}

	public GeocodeResponse encontrarEndereco(String endereco) throws IOException, AtlasNegocioException, InterruptedException {
		Geocoder geocoder = new Geocoder();
		GeocoderRequest geocoderRequest = new GeocoderRequestBuilder().setAddress(endereco + " , República Federativa do Brasil")
				.getGeocoderRequest();
		geocoderRequest.setRegion("BR");
		geocoderRequest.setLanguage("pt-BR");
		GeocodeResponse response = geocoder.geocode(geocoderRequest);
		return response;

	}

	public void gerarCSVAdvogados() {
		try {

			// CABECALHO
			File file = new File("ADVOGADOS.csv");
			FileWriter writer = new FileWriter(file);
			writer.append("Seccional,");
			writer.append("UF,");
			writer.append("Nome,");
			writer.append("Advogados,");
			writer.append("Estagiários,");
			writer.append("Suplementares,");
			writer.append("Total\n");

			for (Object objeto : ufServico.buscarTodos(UF.class)) {
				UF uf = (UF) objeto;

				ClientRequest req = new ClientRequest("http://www.oab.org.br/institucionalseccionais/buscar?uf=" + uf.getSigla());
				ClientResponse<String> res = req.get(String.class);

				if (res.getResponseStatus() != Status.OK) {
					throw new RuntimeException("Failed : HTTP error code : " + res.getResponseStatus());
				}
				String textoJson = res.getEntity();

				JSONParser parser = new JSONParser();
				JSONObject json = (JSONObject) parser.parse(textoJson);

				// SECCIONAIS
				JSONObject jsonSeccional = (JSONObject) json.get("Seccional");

				JSONArray jsonTotais = (JSONArray) json.get("TotalInscritosSubsecao");
				for (Object total : jsonTotais) {
					JSONObject jsonTotal = (JSONObject) total;
					if (!jsonTotal.get("NomeOgan").equals("SECCIONAL")) {
						writer.append("\"" + jsonSeccional.get("Nome") + "\",");
						writer.append("\"" + uf.getSigla() + "\",");
						writer.append("\"" + jsonTotal.get("NomeOgan") + "\",");
						writer.append("\"" + jsonTotal.get("Advogado") + "\",");
						writer.append("\"" + jsonTotal.get("Estagiario") + "\",");
						writer.append("\"" + jsonTotal.get("Suplementar") + "\",");
						writer.append("\"" + jsonTotal.get("Total") + "\"\n");
					}

				}
			}
			writer.flush();
			writer.close();
			Faces.sendFile(file, true);
		} catch (Exception e) {

			e.printStackTrace();

		}
	}

	public void gerarCSVTotalInscritosPorEstado() throws IllegalArgumentException, IOException, AtlasAcessoBancoDadosException {
		try {
			// CABECALHO
			File file = new File("OAB_TOTAL_POR_ESTADO.csv");
			FileWriter writer = new FileWriter(file);
			writer.append("UF,");
			writer.append("Advogados,");
			writer.append("Estagiários,");
			writer.append("Suplementares,");
			writer.append("Total\n");

			for (Object objeto : ufServico.buscarTodos(UF.class)) {
				UF uf = (UF) objeto;

				ClientRequest req = new ClientRequest("http://www.oab.org.br/institucionalseccionais/buscar?uf=" + uf.getSigla());
				ClientResponse<String> res = req.get(String.class);

				if (res.getResponseStatus() != Status.OK) {
					throw new RuntimeException("Failed : HTTP error code : " + res.getResponseStatus());
				}
				String textoJson = res.getEntity();

				JSONParser parser = new JSONParser();
				JSONObject json = (JSONObject) parser.parse(textoJson);

				JSONArray jsonTotais = (JSONArray) json.get("TotalInscritosSubsecao");
				for (Object total : jsonTotais) {
					JSONObject jsonTotal = (JSONObject) total;
					if (jsonTotal.get("NomeOgan").equals("SECCIONAL")) {
						writer.append("\"" + uf.getSigla() + "\",");
						writer.append("\"" + jsonTotal.get("Advogado") + "\",");
						writer.append("\"" + jsonTotal.get("Estagiario") + "\",");
						writer.append("\"" + jsonTotal.get("Suplementar") + "\",");
						writer.append("\"" + jsonTotal.get("Total") + "\"\n");
					}

				}
			}
			writer.flush();
			writer.close();
			Faces.sendFile(file, true);
		} catch (Exception e) {

			e.printStackTrace();

		}

	}

	public void gerarCSVSeccionais() throws IllegalArgumentException, IOException, AtlasAcessoBancoDadosException {

		try {

			List<OABSeccional> seccionais = new ArrayList<>();

			for (Object objeto : ufServico.buscarTodos(UF.class)) {
				UF uf = (UF) objeto;

				ClientRequest req = new ClientRequest("http://www.oab.org.br/institucionalseccionais/buscar?uf=" + uf.getSigla());
				ClientResponse<String> res = req.get(String.class);

				if (res.getResponseStatus() != Status.OK) {
					throw new RuntimeException("Failed : HTTP error code : " + res.getResponseStatus());
				}
				String textoJson = res.getEntity();

				JSONParser parser = new JSONParser();
				JSONObject json = (JSONObject) parser.parse(textoJson);

				// SECCIONAIS
				JSONObject jsonSeccional = (JSONObject) json.get("Seccional");

				ObjectMapper mapper = new ObjectMapper();
				mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				OABSeccional seccional = mapper.readValue(jsonSeccional.toJSONString(), OABSeccional.class);
				seccionais.add(seccional);

				// TOTALIZADORES
				JSONArray jsonTotais = (JSONArray) json.get("TotalInscritosSubsecao");
				for (Object total : jsonTotais) {
					JSONObject jsonTotal = (JSONObject) total;
					if (jsonTotal.get("NomeOgan").equals("SECCIONAL")) {
						seccional.setTotalAdvogadosInscritos((Long) jsonTotal.get("Advogado"));
						seccional.setTotalEstagiariosInscritos((Long) jsonTotal.get("Estagiario"));
						seccional.setTotalSuplementaresInscritos((Long) jsonTotal.get("Suplementar"));
					}

				}
			}

			if (seccionais != null) {
				File file = new File("seccionais.csv");
				FileWriter writer = new FileWriter(file);

				// CABECALHO
				writer.append("nome,");
				writer.append("siglaUF,");
				writer.append("enderecoLocal,");
				writer.append("codCidade,");
				writer.append("bairro,");
				writer.append("EnderacoLogadroro,");
				writer.append("cep,");
				writer.append("telefone1,");
				writer.append("telefone2,");
				writer.append("fax1,");
				writer.append("fax2,");
				writer.append("paginaWeb,");
				writer.append("email,");
				writer.append("Advogados,");
				writer.append("Estagiários,");
				writer.append("Suplementares\n");

				for (OABSeccional seccional : seccionais) {
					writer.append("\"" + seccional.getNome() + "\",");
					writer.append("\"" + seccional.getSiglaUF() + "\",");
					writer.append("\"" + seccional.getEnderecoLocal() + "\",");
					writer.append("\"" + seccional.getCodCidade() + "\",");
					writer.append("\"" + seccional.getBairro() + "\",");
					writer.append("\"" + seccional.getEnderacoLogadroro() + "\",");
					writer.append("\"" + seccional.getCep() + "\",");
					writer.append("\"" + seccional.getTelefone1() + "\",");
					writer.append("\"" + seccional.getTelefone2() + "\",");
					writer.append("\"" + seccional.getFax1() + "\",");
					writer.append("\"" + seccional.getFax2() + "\",");
					writer.append("\"" + seccional.getPaginaWeb() + "\",");
					writer.append("\"" + seccional.getEmail() + "\",");
					writer.append("\"" + seccional.getTotalAdvogadosInscritos() + "\",");
					writer.append("\"" + seccional.getTotalEstagiariosInscritos() + "\",");
					writer.append("\"" + seccional.getTotalSuplementaresInscritos() + "\"\n");

				}
				writer.flush();
				writer.close();
				Faces.sendFile(file, true);
			}
		} catch (Exception e) {

			e.printStackTrace();

		}
	}

	public MapModel getMapModel() {
		return mapModel;
	}

	public void setMapModel(MapModel mapModel) {
		this.mapModel = mapModel;
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

	public Integer getZoom() {
		return zoom;
	}

	public void setZoom(Integer zoom) {
		this.zoom = zoom;
	}

	public void onMarkerSelect(OverlaySelectEvent event) {
		marker = (Marker) event.getOverlay();
	}

	public Marker getMarker() {
		return marker;
	}

	public List<UF> getUfs() throws AtlasAcessoBancoDadosException {
		return ufs;
	}

	public List<Municipio> getMunicipios() throws AtlasAcessoBancoDadosException {
		if (getUf() != null) {
			return municipioServico.recuperarMunicipios(getUf());
		} else {
			return null;
		}
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

	public List<Orgao> getOrgaosNaoEncontrados() {
		return orgaosNaoEncontrados;
	}

	public int getNumeroInicioRegistro() {
		return numeroInicioRegistro;
	}

	public void setNumeroInicioRegistro(int numeroInicioRegistro) {
		this.numeroInicioRegistro = numeroInicioRegistro;
	}

	public int getQuantidadeRegistros() {
		return quantidadeRegistros;
	}

	public void setQuantidadeRegistros(int quantidadeRegistros) {
		this.quantidadeRegistros = quantidadeRegistros;
	}

	public Municipio getMunicipioPesquisa() {
		return municipioPesquisa;
	}

	public void setMunicipioPesquisa(Municipio municipioPesquisa) {
		this.municipioPesquisa = municipioPesquisa;
	}

	public TipoOrgao getTipoOrgaoPesquisa() {
		return tipoOrgaoPesquisa;
	}

	public void setTipoOrgaoPesquisa(TipoOrgao tipoOrgaoPesquisa) {
		this.tipoOrgaoPesquisa = tipoOrgaoPesquisa;
	}

	public List<TipoOrgao> getTipoOrgaos() {
		if(getRamo()!=null){
			try {
				this.tipoOrgaos = tipoOrgaoServico.recuperarTiposOrgaoPorRamo(getRamo().getId());
			} catch (AtlasAcessoBancoDadosException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return tipoOrgaos;
	}

	public void setTipoOrgaos(List<TipoOrgao> tipoOrgaos) {
		this.tipoOrgaos = tipoOrgaos;
	}
	
	public Tema getTema() {
		return tema;
	}

	public void setTema(Tema tema) {
		this.tema = tema;
	}

	public List<Tema> getTemas() {
		return temas;
	}

	public void setTemas(List<Tema> temas) {
		this.temas = temas;
	}

	public Ramo getRamo() {
		return ramo;
	}

	public void setRamo(Ramo ramo) {
		this.ramo = ramo;
	}

	public List<Ramo> getRamos() {
		return ramos;
	}

	public void setRamos(List<Ramo> ramos) {
		this.ramos = ramos;
	}

	public String[] getParametrosPesquisaMapa() {
		return parametrosPesquisaMapa;
	}

	public void setParametrosPesquisaMapa(String[] parametrosPesquisaMapa) {
		this.parametrosPesquisaMapa = parametrosPesquisaMapa;
	}

	public String getVisualizacaoMapa() {
		return visualizacaoMapa;
	}

	public void setVisualizacaoMapa(String visualizacaoMapa) {
		this.visualizacaoMapa = visualizacaoMapa;
	}
	
	
}