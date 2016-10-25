package br.gov.atlas.bean;

import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import br.gov.atlas.entidade.Multimidia;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;
import br.gov.atlas.exception.AtlasNegocioException;
import br.gov.atlas.servico.AtlasServicoImpl;
import br.gov.atlas.servico.ExibirMultimidiaServico;

@ManagedBean(name = "exibirMultimidiaBean")
@ViewScoped
public class ExibirMultimidiaBean extends AtlasPaginatedBean {

	private static final long serialVersionUID = -7198037614072830443L;
	private String tipo = "V";
	private List<Multimidia> multimidias;
	
	@EJB(name = "ExibirMultimidiaServico")
	ExibirMultimidiaServico exibirMultimidiaServico;
	
	public void init(){
		pesquisar();
	}
	
	@Override
	protected void efetuarPesquisa() throws AtlasAcessoBancoDadosException{
		efetuarPesquisaPorPagina(this.currentPage, this.tipo); 
	}
	
	public void efetuarPesquisaPorPagina(Integer page, String tipo) throws AtlasAcessoBancoDadosException {
		this.multimidias = exibirMultimidiaServico.recuperarMultimidiasPorTipo(page, tipo);
	}
	
	@Override
	protected AtlasServicoImpl getServico() {
		return exibirMultimidiaServico;
	}

	@Override
	protected String getTelaPesquisa(){
		return "videoConsulta";
	}

	@Override
	protected String getTelaCadastro(){
		return null;
	}
	
	public String getTipo(){
		return tipo;
	}
	
	public void setTipo(String tipo){
		this.tipo = tipo;
	}
	
	public List<Multimidia> getMultimidias() {
		return multimidias;
	}

	public void setMultimidias(List<Multimidia> multimidias) {
		this.multimidias = multimidias;
	}

	@Override
	public Integer recuperarTotalDePaginas()
			throws AtlasAcessoBancoDadosException {
		return exibirMultimidiaServico.recuperarTotalDePaginasPorTipo(getTipo());
	}

	@Override
	public Boolean validar() throws AtlasNegocioException {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Multimidia getVideoTelaPrincipal() throws AtlasAcessoBancoDadosException{
		return exibirMultimidiaServico.recuperarVideoTelaPrincipal();
	}

}
