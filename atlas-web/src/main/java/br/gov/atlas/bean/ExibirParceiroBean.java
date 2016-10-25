package br.gov.atlas.bean;

import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import br.gov.atlas.entidade.Parceiro;
import br.gov.atlas.entity.ParceiroLinhaComTresRegistrosAdapter;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;
import br.gov.atlas.exception.AtlasNegocioException;
import br.gov.atlas.servico.AtlasServicoImpl;
import br.gov.atlas.servico.ExibirParceiroServico;


@ManagedBean(name="exibirParceiroBean")
@SessionScoped
public class ExibirParceiroBean extends AtlasPaginatedBean {

	private static final long serialVersionUID = 1L;
	
	@EJB(name = "ExibirParceiroServico")
	ExibirParceiroServico exibirParceiroServico;
	
	private List<ParceiroLinhaComTresRegistrosAdapter> parceiroLinhaComTresRegistros;
	
	public void init(){
		pesquisar();
	}
	
	@Override
	protected void efetuarPesquisa() throws AtlasAcessoBancoDadosException {
		efetuarPesquisaPorPagina(getCurrentPage());
	}
	
	public void efetuarPesquisaPorPagina(Integer page) throws AtlasAcessoBancoDadosException {
		this.parceiroLinhaComTresRegistros = exibirParceiroServico.recuperarParceirosComLinhasDeTresRegistros(page);
	}
	
	@Override
	public Integer recuperarTotalDePaginas() throws AtlasAcessoBancoDadosException {
		return exibirParceiroServico.recuperarTotalDePaginas();
	}
	
	public String recuperarCaminhoImagem(Parceiro parceiro) {
		String result = "";
		if(parceiro != null){
			if(parceiro.getUrlImagem() != null){
				result = "/pub/_imagens/parceiros/" + parceiro.getUrlImagem();
			}
		}
		return result;
	}
	
	@Override
	protected AtlasServicoImpl getServico() {
		return exibirParceiroServico;
	}
	
	@Override
	protected String getTelaPesquisa() {
		return "parceiroConsulta";
	}
	@Override
	protected String getTelaCadastro() {
		return null;
	}
	
	@Override
	public Boolean validar() throws AtlasNegocioException {
		return null;
	}
	
	public List<ParceiroLinhaComTresRegistrosAdapter> getParceiroLinhaComTresRegistros() {
		return parceiroLinhaComTresRegistros;
	}
	public void setParceiroLinhaComTresRegistros(
			List<ParceiroLinhaComTresRegistrosAdapter> parceiroLinhaComTresRegistros) {
		this.parceiroLinhaComTresRegistros = parceiroLinhaComTresRegistros;
	}	
	
}
