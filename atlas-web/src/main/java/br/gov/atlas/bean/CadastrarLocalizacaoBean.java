package br.gov.atlas.bean;

import java.math.BigDecimal;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.event.map.MarkerDragEvent;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;

import br.gov.atlas.entidade.Orgao;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;
import br.gov.atlas.exception.AtlasNegocioException;
import br.gov.atlas.servico.AtlasServicoImpl;
import br.gov.atlas.servico.LocalizacaoServico;

@ManagedBean(name = "cadastrarLocalizacaoBean")
@ViewScoped
public class CadastrarLocalizacaoBean extends AtlasCrudBean<Orgao> {

	private static final long serialVersionUID = -9180126522393607105L;

	private MapModel mapModel;

	@EJB(name = "LocalizacaoServico")
	LocalizacaoServico localizacaoServico;

	public CadastrarLocalizacaoBean() {
		Orgao localizacao = new Orgao();
		mapModel = new DefaultMapModel();

		// Coordenadas Iniciais
		if (localizacao.getLatitude() == null || localizacao.getLongitude() == null) {
			localizacao.setLatitude(new BigDecimal(-15.79713));
			localizacao.setLongitude(new BigDecimal(-47.865918));
		}

		// Adicionar Marcador
		mapModel.addOverlay(new Marker(new LatLng(localizacao.getLatitude().doubleValue(), localizacao.getLongitude().doubleValue()), "Nova Localização"));

		for (Marker marker : mapModel.getMarkers()) {
			marker.setDraggable(true);
		}

		setEntidade(localizacao);
	}

	public void onMarkerDrag(MarkerDragEvent event) {
		Marker marker = event.getMarker();

		getEntidade().setLatitude(new BigDecimal(marker.getLatlng().getLat()));
		getEntidade().setLongitude(new BigDecimal(marker.getLatlng().getLng()));
	}

	public void addMessage(FacesMessage message) {
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	public MapModel getMapModel() {
		return mapModel;
	}

	@Override
	protected void efetuarPesquisa() throws AtlasAcessoBancoDadosException {
		// TODO Auto-generated method stub

	}

	@Override
	protected AtlasServicoImpl getServico() {
		return localizacaoServico;
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

	@Override
	public Boolean validar() throws AtlasNegocioException {
		boolean valido = true;
		if (getEntidade().getDescricao() == null || getEntidade().getDescricao().trim().isEmpty()) {
			error(new AtlasNegocioException("inputTextNome", "MSG1"));
			valido = false;
		}
		return valido;
	}

}
