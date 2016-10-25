package br.gov.atlas.bean;

import java.util.Date;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import br.gov.atlas.component.model.AtlasLazyDataModel;
import br.gov.atlas.entidade.Ramo;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;
import br.gov.atlas.exception.AtlasNegocioException;
import br.gov.atlas.servico.AtlasServicoImpl;
import br.gov.atlas.servico.ManterRamoServico;
import br.gov.atlas.servico.ManterTipoOrgaoServico;
import br.gov.atlas.servico.OrgaoServico;

@ManagedBean(name = "manterRamoBean")
@SessionScoped
public class ManterRamoBean extends AtlasCrudBean<Ramo> {

	private static final long serialVersionUID = 5288423084607996395L;

	@EJB(name = "ManterRamoServico")
	private ManterRamoServico ramoServico;

	@EJB(name = "OrgaoServico")
	private OrgaoServico orgaoServico;

	@EJB(name = "ManterTipoOrgaoServico")
	private ManterTipoOrgaoServico tipoOrgaoServico;

	private AtlasLazyDataModel<Ramo> ramos;

	private Ramo ramoFiltro;

	public void init() throws AtlasAcessoBancoDadosException {
		this.setRamoFiltro(new Ramo());
		carregarTermos(null);
	}

	public void carregarTermos(String textoParcial)
			throws AtlasAcessoBancoDadosException {
		this.ramos = new AtlasLazyDataModel<Ramo>();
		this.ramos.setServico(this.ramoServico);
		this.ramos.setMetodo("recuperarRamos");
		this.ramoFiltro = new Ramo();
		this.ramoFiltro.setNome(textoParcial);
		this.ramos.setParametros(this.ramoFiltro);
		pesquisar();
	}

	public String novoRegistroManterRamo() {
		super.novoRegistro();
		return getTelaCadastro();
	}

	@Override
	public void inicializarObjetos() throws AtlasAcessoBancoDadosException {
		super.inicializarObjetos();
	}

	@Override
	public String salvar() {
		if (getEntidade().getId() == null) {
			getEntidade().setDataCadastro(new Date());
		}
		super.salvar();
		return null;
	}

	@Override
	public Boolean validar() throws AtlasNegocioException {
		if ((getEntidade().getNome() == null)
				|| getEntidade().getNome().toString().trim().equals("")) {
			error(null, "MSG20", "Nome");
			return false;
		}
		return true;
	}

	public String editarManterRamo(Integer pk) {
		super.editar(pk);
		return getTelaCadastro();
	}

	public void excluirRamo(Ramo ramo) throws AtlasAcessoBancoDadosException,
	AtlasNegocioException {
		if (existeGrupoOrgaoRelacionado(ramo)) {
			error(null, "MSG27", ramo.getNome());
		} else if (existeEnteRelacionado(ramo)) {
			error(null, "MSG29", ramo.getNome());
		} else {
			super.excluir(ramo);
		}
	}

	private boolean existeGrupoOrgaoRelacionado(Ramo ramo)
			throws AtlasAcessoBancoDadosException {
		return tipoOrgaoServico.contarPorRamo(ramo) != 0;
	}

	/**
	 * TODO CESAR Verificar query
	 * @param ramo
	 * @return
	 * @throws AtlasAcessoBancoDadosException
	 */
	private boolean existeEnteRelacionado(Ramo ramo)
			throws AtlasAcessoBancoDadosException {
		//return enteServico.contarPorRamo(ramo) != 0;
		return true;
	}

	@Override
	protected void efetuarPesquisa() throws AtlasAcessoBancoDadosException {
		this.ramos.setListaPaginada(this.ramoServico
				.recuperarRamos(this.ramoFiltro));
	}

	@Override
	protected AtlasServicoImpl getServico() {
		return this.ramoServico;
	}

	@Override
	protected String getTelaPesquisa() {
		return "ramoConsulta";
	}

	public String voltarTelaPesquisa() {
		return "ramoConsulta";
	}

	@Override
	protected String getTelaCadastro() {
		return "ramoCadastro";
	}

	public Ramo getRamoFiltro() {
		return this.ramoFiltro;
	}

	public void setRamoFiltro(Ramo ramoFiltro) {
		this.ramoFiltro = ramoFiltro;
	}

	public AtlasLazyDataModel<Ramo> getRamos() {
		return this.ramos;
	}

	public void setRamos(AtlasLazyDataModel<Ramo> ramos) {
		this.ramos = ramos;
	}
}