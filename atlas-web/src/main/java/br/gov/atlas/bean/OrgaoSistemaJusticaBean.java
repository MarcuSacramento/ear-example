package br.gov.atlas.bean;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.mindmap.DefaultMindmapNode;
import org.primefaces.model.mindmap.MindmapNode;

import br.gov.atlas.entidade.OrgaoSistemaJustica;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;
import br.gov.atlas.servico.OrgaoSistemaJusticaServico;

@ManagedBean(name = "orgaoSistemaJusticaBean")
@ViewScoped
public class OrgaoSistemaJusticaBean extends AtlasBean {

	private static final long serialVersionUID = 1131788222435030458L;
	
	@EJB(name = "OrgaoSistemaJusticaServico")
	private OrgaoSistemaJusticaServico orgaoSistemaJusticaServico;
	
    private MindmapNode root;
    
    private MindmapNode selectedNode;
    
    private List<String> cores;
    
	/*
	 * (non-Javadoc)
	 * @see br.gov.atlas.bean.AtlasBean#efetuarPesquisa()
	 */
	@Override
	protected void efetuarPesquisa() throws AtlasAcessoBancoDadosException {
	}

	/*
	 * (non-Javadoc)
	 * @see br.gov.atlas.bean.AtlasBean#getServico()
	 */
	@Override
	protected OrgaoSistemaJusticaServico getServico() {
		return orgaoSistemaJusticaServico;
	}

	/*
	 * (non-Javadoc)
	 * @see br.gov.atlas.bean.AtlasBean#getTelaPesquisa()
	 */
	@Override
	protected String getTelaPesquisa() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see br.gov.atlas.bean.AtlasBean#getTelaCadastro()
	 */
	@Override
	protected String getTelaCadastro() {
		return null;
	}

	/**
	 * Seleciona o node
	 * @param event
	 */
    public void onNodeSelect(SelectEvent event) {
        selectedNode = (MindmapNode) event.getObject();
        
        
        if(selectedNode.getChildren().isEmpty()) {
            try {
				buscaFilhos((OrgaoSistemaJustica) selectedNode.getData(), selectedNode);
			} catch (AtlasAcessoBancoDadosException e) {
				e.printStackTrace();
			}
        }
        
    }

	/**
	 * @return the root
	 */
	public MindmapNode getRoot() {
		if (root == null){
			
			try {
				OrgaoSistemaJustica raiz = getServico().buscarOrgaosRaiz();
				raiz.setBreadcrumb(raiz.getNome());
				
				root = new DefaultMindmapNode(getSiglaNome(raiz), raiz, getProximaCor(null), false);

				buscaFilhos(raiz, root);
				
			} catch (AtlasAcessoBancoDadosException e) {
				e.printStackTrace();
			}
			
		}
		return root;
	}

	/**
	 * Busca os filhos
	 * @param objRaiz
	 * @param noRaiz
	 * @throws AtlasAcessoBancoDadosException
	 */
	private void buscaFilhos(OrgaoSistemaJustica objRaiz, MindmapNode noRaiz) throws AtlasAcessoBancoDadosException {
		
		List<OrgaoSistemaJustica> filhos = getServico().buscarOrgaosFilhos(objRaiz);
		
		for (OrgaoSistemaJustica filho : filhos) {
			filho.setBreadcrumb(objRaiz.getBreadcrumb() + " » " + filho.getNome());
			
			MindmapNode nodeFilho = new DefaultMindmapNode(getSiglaNome(filho), filho, getProximaCor(noRaiz.getFill()), true);
			noRaiz.addNode(nodeFilho);
		}
	}

	/**
	 * Recupera a sigla e o nome do node.
	 * @param node
	 * @return sigla#nome
	 */
	private String getSiglaNome(OrgaoSistemaJustica node) {
		return node.getSigla() + "#" + node.getNome();
	}

	/**
	 * @return a proxima cor
	 */
	public String getProximaCor(String corAtual) {
		if (corAtual == null || corAtual.endsWith("6")){
			return getCores().get(0);
		} 
		return getCores().get(Integer.parseInt(corAtual.substring(5)));
	}

	/**
	 * @return the cores
	 */
	public List<String> getCores() {
		if (cores == null){
			cores = new ArrayList<>();
			cores.add("82C541");
			cores.add("FFCC02");
			cores.add("6E9EB3");
			cores.add("B9C064");
			cores.add("FBEDA5");
			cores.add("E3B296");
		}
		return cores;
	}

	/**
	 * @return the breadcrumb
	 */
	public String getBreadcrumb() {
		if (selectedNode == null){
			return ((OrgaoSistemaJustica)getRoot().getData()).getBreadcrumb();
		}
		return ((OrgaoSistemaJustica)selectedNode.getData()).getBreadcrumb();
	}	

}
