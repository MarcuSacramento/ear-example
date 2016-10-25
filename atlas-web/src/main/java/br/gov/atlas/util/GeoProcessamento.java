package br.gov.atlas.util;

import java.net.ConnectException;
import java.text.Normalizer;
import java.text.ParseException;

import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;

import br.gov.atlas.bean.ManterOrgaoBean;
import br.gov.atlas.entidade.Municipio;
import br.gov.atlas.entidade.Orgao;

import com.google.code.geocoder.Geocoder;
import com.google.code.geocoder.GeocoderRequestBuilder;
import com.google.code.geocoder.model.GeocodeResponse;
import com.google.code.geocoder.model.GeocoderAddressComponent;
import com.google.code.geocoder.model.GeocoderRequest;
import com.google.code.geocoder.model.GeocoderResult;
import com.google.code.geocoder.model.GeocoderStatus;

public class GeoProcessamento {

	public void processarLatitudeLongitude(Orgao orgao)
			throws ParseException, ConnectException {
		processarLatitudeLongitude(orgao, null);
	}

	public void processarLatitudeLongitude(Orgao orgao, String[] parametrosPesquisaMapa)
			throws ConnectException, ParseException {
		processarLatitudeLongitude(orgao, parametrosPesquisaMapa, null);
	}

	public void processarLatitudeLongitude(Orgao orgao, String[] parametrosPesquisaMapa, StringBuffer valorPesquisadoGoogle)
			throws ConnectException, ParseException {

		if ((orgao.getLatitude() == null || orgao.getLatitude().intValue() == 0)
				&& (orgao.getLongitude() == null || orgao.getLongitude().intValue() == 0)) {
			Municipio municipio = orgao.getMunicipio();
			if(municipio != null){
				String municipioESigla = municipio.getNome() + " - "+ municipio.getUf().getSigla();
				StringBuffer enderecoCompleto = new StringBuffer(orgao.getEndereco());
				if(orgao.getBairro() != null && !orgao.getBairro().isEmpty()){
					enderecoCompleto.append(", ");
					enderecoCompleto.append(orgao.getBairro());
				}
				GeocoderResult result = null;
				if(parametrosPesquisaMapa != null && parametrosPesquisaMapa.length > 0){
					StringBuffer filtro = new StringBuffer();
					for (String parametro : parametrosPesquisaMapa) {
						if(parametro.equals(ManterOrgaoBean.PESQUISA_CEP)){
							filtro.append(orgao.getCepFormatado()+", ");
						}else if(parametro.equals(ManterOrgaoBean.PESQUISA_ENDERECO)){
							filtro.append(enderecoCompleto.toString()+", ");
						}else{
							filtro.append(orgao.getNome()+", ");
						}
					}
					filtro.append(municipioESigla);
					GeocodeResponse geocoder = recuperarCoordenadas(filtro.toString());
					result = recuperaResultado(geocoder);
					if(valorPesquisadoGoogle != null){
						valorPesquisadoGoogle.append(filtro.toString());
					}
				}else{
					String nomeOrgao = orgao.getNome() + ", " + municipioESigla;
					String endereco = enderecoCompleto.toString() + ", " + municipioESigla;
					String cep = orgao.getCepFormatado() + ", " + municipioESigla;

					GeocodeResponse geocoderNome = recuperarCoordenadas(nomeOrgao);
					GeocodeResponse geocoderEndereco = recuperarCoordenadas(endereco);
					GeocodeResponse geocoderPostalCode = recuperarCoordenadas(cep);

					result = processarResultado(geocoderNome, geocoderEndereco, geocoderPostalCode);
				}
				if(result != null && validarResultado(result, orgao)){
					// GRAVANDO A INFORMAÇÃO DA LATITUDE E LONGITUDE
					orgao.setLatitude(result.getGeometry().getLocation().getLat());
					orgao.setLongitude(result.getGeometry().getLocation().getLng());
				}
			}
		}
	}

	private GeocoderResult processarResultado(GeocodeResponse geocoderNome,
			GeocodeResponse geocoderEndereco, GeocodeResponse geocoderPostalCode) {
		GeocoderResult resultadoFinal = null;
		GeocoderResult resultadoNome = recuperaResultado(geocoderNome);
		GeocoderResult resultadoEndereco = recuperaResultado(geocoderEndereco);
		GeocoderResult resultadoPostalCode = recuperaResultado(geocoderPostalCode);
		int qtdResultadoNome = (resultadoNome != null && !resultadoNome
				.getAddressComponents().isEmpty()) ? resultadoNome
						.getAddressComponents().size() : 0;
						int qtdResultadoEndereco = (resultadoEndereco != null && !resultadoEndereco
								.getAddressComponents().isEmpty()) ? resultadoEndereco
										.getAddressComponents().size() : 0;
										int qtdResultadoPostalCode = (resultadoPostalCode != null && !resultadoPostalCode
												.getAddressComponents().isEmpty()) ? resultadoPostalCode
														.getAddressComponents().size() : 0;
														// VERIFICANDO QUAIS DOS TRÊS RESULTADOS RECUPEROU A RESPOSTA MAIS COMPLETA
														if(qtdResultadoNome >= qtdResultadoEndereco && qtdResultadoNome >= qtdResultadoPostalCode){
															resultadoFinal = resultadoNome;
														}else if(qtdResultadoEndereco >= qtdResultadoNome && qtdResultadoEndereco >= qtdResultadoPostalCode){
															resultadoFinal = resultadoEndereco;
														}else{
															resultadoFinal = resultadoPostalCode;
														}

														return resultadoFinal;
	}

	private GeocoderResult recuperaResultado(GeocodeResponse geocoderResponse) {
		if (geocoderResponse != null
				&& !geocoderResponse.getStatus().equals(GeocoderStatus.ZERO_RESULTS)) {
			GeocoderResult resultResponse = geocoderResponse.getResults().get(0);
			if(resultResponse != null && !resultResponse.getAddressComponents().isEmpty()){
				return resultResponse;
			}
		}
		return null;
	}

	private GeocodeResponse recuperarCoordenadas(String parametro) throws ConnectException {
		return encontrarEndereco(parametro);
	}

	public boolean validarResultado(GeocoderResult result, Orgao cartorio) {
		Boolean valido = false;
		String ufEncontrada = null;
		String cepEncontrado = null;
		String prefixoCepEncontrado = null;
		String municipioEncontrado = null;
		String paisEncontrado = null;

		for (GeocoderAddressComponent addressComponent : result.getAddressComponents()) {
			if (addressComponent.getTypes().contains("administrative_area_level_1")) {
				ufEncontrada = addressComponent.getShortName();
			}
			if (addressComponent.getTypes().contains("postal_code")) {
				cepEncontrado = addressComponent.getLongName();
			}
			if (addressComponent.getTypes().contains("postal_code_prefix")) {
				prefixoCepEncontrado = addressComponent.getLongName();
			}
			if (addressComponent.getTypes().contains("locality")) {
				municipioEncontrado = addressComponent.getLongName();
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

		if (cartorio.getCep() != null && prefixoCepEncontrado != null && isCepValido(cartorio.getCep().toString())) {
			if (paisEncontrado.equals("BR")) {
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
		return cep != null && (cep.trim().length() == 8 || cep.replace("-", "").length() == 8);
	}

	public GeocodeResponse encontrarEndereco (String endereco) throws ConnectException{
		Geocoder geocoder = new Geocoder();
		GeocoderRequest geocoderRequest = new GeocoderRequestBuilder().setAddress(endereco + " , República Federativa do Brasil")
				.getGeocoderRequest();
		geocoderRequest.setRegion("BR");
		geocoderRequest.setLanguage("pt-BR");
		GeocodeResponse response = geocoder.geocode(geocoderRequest);
		return response;
	}

	public void adicionarMarcador(Orgao orgao, MapModel mapModel) {
		if (orgao.getLatitude() != null && orgao.getLongitude() != null) {
			Marker marker = new Marker(new LatLng(orgao.getLatitude()
					.doubleValue(), orgao.getLongitude().doubleValue()),
					orgao.getNome());
			marker.setData(orgao);
			marker.setZindex(orgao.getId());
			marker.setDraggable(true);
			/*if (orgao.getTipoOrgao().getGrupoOrgao().getId() == GrupoOrgao.ID_GRUPO_ORGAO_CARTORIO) {
				marker.setIcon("http://chart.apis.google.com/chart?chst=d_map_pin_letter&chld=%E2%80%A2|cc0000");
			} else if (orgao.getTipoOrgao().getGrupoOrgao().getId() == GrupoOrgao.ID_GRUPO_ORGAO_DEFENSORIA) {
				marker.setIcon("http://chart.apis.google.com/chart?chst=d_map_pin_letter&chld=%E2%80%A2|ffa52c");
			} else if (orgao.getTipoOrgao().getGrupoOrgao().getId() == GrupoOrgao.ID_GRUPO_ORGAO_FORUM) {
				marker.setIcon("http://chart.apis.google.com/chart?chst=d_map_pin_letter&chld=%E2%80%A2|85ff2c");
			} else if (orgao.getTipoOrgao().getGrupoOrgao().getId() == GrupoOrgao.ID_GRUPO_ORGAO_MINISTERIO_PUBLICO) {
				marker.setIcon("http://chart.apis.google.com/chart?chst=d_map_pin_letter&chld=%E2%80%A2|6600ff");
			} else if (orgao.getTipoOrgao().getGrupoOrgao().getId() == GrupoOrgao.ID_GRUPO_ORGAO_OAB) {
				marker.setIcon("http://chart.apis.google.com/chart?chst=d_map_pin_letter&chld=%E2%80%A2|66340e");
			} else if (orgao.getTipoOrgao().getGrupoOrgao().getId() == GrupoOrgao.ID_GRUPO_ORGAO_PENITENCIARIA) {
				marker.setIcon("http://chart.apis.google.com/chart?chst=d_map_pin_letter&chld=%E2%80%A2|ff2c86");
			} else if (orgao.getTipoOrgao().getGrupoOrgao().getId() == GrupoOrgao.ID_GRUPO_ORGAO_PROCON) {
				marker.setIcon("http://chart.apis.google.com/chart?chst=d_map_pin_letter&chld=%E2%80%A2|192e5b");
			} else{
				marker.setIcon("http://chart.apis.google.com/chart?chst=d_map_pin_letter&chld=%E2%80%A2|FE7569");
			}*/
			mapModel.addOverlay(marker);
			orgao.setUrlImagem(marker.getIcon());
		}
	}
}