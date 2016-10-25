package br.gov.atlas.bean;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import br.gov.atlas.entidade.Projeto;
import br.gov.atlas.entity.ProjetoLinhaComTresRegistrosAdapter;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;
import br.gov.atlas.exception.AtlasNegocioException;
import br.gov.atlas.servico.AtlasServicoImpl;
import br.gov.atlas.servico.ExibirProjetoServico;


@ManagedBean(name="exibirProjetoBean")
@ViewScoped
public class ExibirProjetoBean extends AtlasPaginatedBean {

	private static final long serialVersionUID = 1L;
	
	@EJB(name = "ExibirProjetoServico")
	ExibirProjetoServico exibirProjetoServico;
	
	private List<ProjetoLinhaComTresRegistrosAdapter> projetoLinhaComTresRegistros;

	public void init(){
		pesquisar();
	}
	
	@Override
	protected void efetuarPesquisa() throws AtlasAcessoBancoDadosException {
		efetuarPesquisaPorPagina(getCurrentPage());
	}
	
	public void efetuarPesquisaPorPagina(Integer page) throws AtlasAcessoBancoDadosException {
		this.projetoLinhaComTresRegistros = exibirProjetoServico.recuperarProjetosComLinhasDeTresRegistros(page);
	}
	
	@Override
	public Integer recuperarTotalDePaginas() throws AtlasAcessoBancoDadosException {
		return exibirProjetoServico.recuperarTotalDePaginas();
	}
	
	public String recuperarCaminhoImagem(Projeto projeto) {
		String result = "";
		if(projeto != null){
			if(projeto.getUrlImagem() != null){
				result = "/pub/_imagens/projetos/" + projeto.getUrlImagem();
			}
		}
		return result;
	}
	
	@Override
	protected AtlasServicoImpl getServico() {
		return exibirProjetoServico;
	}
	
	@Override
	protected String getTelaPesquisa() {
		return "projetoConsulta";
	}
	@Override
	protected String getTelaCadastro() {
		return null;
	}
	
	@Override
	public Boolean validar() throws AtlasNegocioException {
		return null;
	}
	
	public List<ProjetoLinhaComTresRegistrosAdapter> getProjetoLinhaComTresRegistros() {
		return projetoLinhaComTresRegistros;
	}
	public void setProjetoLinhaComTresRegistros(
			List<ProjetoLinhaComTresRegistrosAdapter> projetoLinhaComTresRegistros) {
		this.projetoLinhaComTresRegistros = projetoLinhaComTresRegistros;
	}
	
	public StreamedContent getImagem() {
		FacesContext context = FacesContext.getCurrentInstance();
		if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
			return new DefaultStreamedContent();
		} else {
			String url = context.getExternalContext().getRequestParameterMap().get("url");

			url = "/usr/local/share/jboss/standalone/data/arquivos/parceiros/" + url;
			File file = new File(url);
			try {
				return new DefaultStreamedContent(new FileInputStream(file));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return null;
			}
		}
	}
	
}
