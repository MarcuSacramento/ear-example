package br.gov.atlas.bean;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import br.gov.atlas.component.model.AtlasLazyDataModel;
import br.gov.atlas.entidade.Cargo;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;
import br.gov.atlas.exception.AtlasNegocioException;
import br.gov.atlas.servico.AtlasServicoImpl;
import br.gov.atlas.servico.ManterCargoServico;
import br.gov.atlas.servico.RepresentanteServico;

@ManagedBean(name = "manterCargoBean")
@SessionScoped
public class ManterCargoBean extends AtlasCrudBean<Cargo> {

	private static final long serialVersionUID = -4769886649977493565L;

	@EJB(name = "ManterCargoServico")
	ManterCargoServico cargoServico;

	@EJB(name = "RepresentanteServico")
	private RepresentanteServico representanteServico;

	private AtlasLazyDataModel<Cargo> cargos;

	private Cargo cargoFiltro;

	private String cargoPesquisa;
	private String tratamento;
	
	public void init() throws AtlasAcessoBancoDadosException {
		this.setCargoFiltro(new Cargo());
		carregarCargos(null);
	}

	@Override
	protected void efetuarPesquisa() throws AtlasAcessoBancoDadosException {
		cargos.setListaPaginada(cargoServico.recuperarCargos(cargoFiltro));
	}

	public void carregarCargos(String textoParcial)
			throws AtlasAcessoBancoDadosException {
		cargos = new AtlasLazyDataModel<Cargo>();
		cargos.setServico(cargoServico);
		cargos.setMetodo("recuperarCargos");
		cargoFiltro = new Cargo();
		cargoFiltro.setNome(textoParcial);
		cargoFiltro.setTratamento(tratamento);
		cargos.setParametros(cargoFiltro);
		pesquisar();
	}

	public String novoRegistroManterCargo() {
		super.novoRegistro();
		return getTelaCadastro();
	}

	public void pesquisarCargo() throws AtlasAcessoBancoDadosException {
		if (this.cargoPesquisa != null && !this.cargoPesquisa.trim().isEmpty()) {
			carregarCargos("%" + this.cargoPesquisa + "%");
		} else {
			carregarCargos(null);
		}
	}

	public void limpar() {
		this.cargoPesquisa = null;
		this.tratamento = null;
	}

	public void limparPesquisar() throws AtlasAcessoBancoDadosException {
		limpar();
		carregarCargos(null);
	}

	@Override
	public void inicializarObjetos() throws AtlasAcessoBancoDadosException {
		super.inicializarObjetos();
	}

	@Override
	public Boolean validar() throws AtlasNegocioException {
		if (getEntidade().getNome() == null
				|| getEntidade().getNome().toString().trim().equals("")) {
			error(null, "MSG20", "Nome do cargo");
			return false;
		}

		if (getEntidade().getTratamento() == null
				|| getEntidade().getTratamento().toString().trim().equals("")) {
			error(null, "MSG20", "Nome do tratamento");
			return false;
		}
		return true;
	}

	public String editarManterCargo(Integer pk) {
		super.editar(pk);
		return getTelaCadastro();
	}

	
	public void excluirCargo(Cargo cargo) throws AtlasAcessoBancoDadosException, AtlasNegocioException	{
		if( verificarIsCargoJaAssociadoAAlgumRepresentante(cargo) ) {
			error(null, "MSG35", cargo.getNome());
		} else {
			super.excluir(cargo);
		}
	}
	
	private Boolean verificarIsCargoJaAssociadoAAlgumRepresentante(Cargo cargo) throws AtlasAcessoBancoDadosException
	{
		Boolean isCargoJaAssociado = cargoServico.isCargoAssociadoAAlgumRepresentante(cargo);
		return isCargoJaAssociado;
	}
	
	@Override
	public String cancelar() {
		limpar();
		return "/admin/gerenciamento/cargos/exibirCargos.faces";
	}

	@Override
	protected AtlasServicoImpl getServico() {
		return cargoServico;
	}
	
	@Override
	protected String getTelaPesquisa() {
		return "exibirCargos";
	}

	public String voltarTelaPesquisa() {
		return "exibirCargos";
	}

	public String getCargoPesquisa() {
		return cargoPesquisa;
	}

	public void setCargoPesquisa(String cargoPesquisa) {
		this.cargoPesquisa = cargoPesquisa;
	}

	@Override
	protected String getTelaCadastro() {
		return "informarCargos";
	}

	public AtlasLazyDataModel<Cargo> getCargos() {
		return cargos;
	}

	public void setCargos(AtlasLazyDataModel<Cargo> cargos) {
		this.cargos = cargos;
	}

	public Cargo getCargoFiltro() {
		return cargoFiltro;
	}

	public void setCargoFiltro(Cargo cargoFiltro) {
		this.cargoFiltro = cargoFiltro;
	}

	public String getTratamento() {
		return tratamento;
	}

	public void setTratamento(String tratamento) {
		this.tratamento = tratamento;
	}

}