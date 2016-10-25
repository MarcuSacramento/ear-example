package br.gov.atlas.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;

import br.gov.atlas.dto.EnderecoDTO;
import br.gov.atlas.dto.IndiceDTO;
import br.gov.atlas.dto.OrgaoDTO;
import br.gov.atlas.entidade.Municipio;
import br.gov.atlas.entidade.Regiao;
import br.gov.atlas.entidade.TipoOrgao;
import br.gov.atlas.entidade.UF;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;
import br.gov.atlas.servico.ConexaoServico;
import br.gov.atlas.servico.IndicadorServico;
import br.gov.atlas.servico.MunicipioServico;
import br.gov.atlas.servico.OrgaoServico;
import br.gov.atlas.servico.RegiaoServico;
import br.gov.atlas.servico.UFServico;
import br.gov.atlas.util.RelatorioUtil;

@ManagedBean(name = "exibirMonitoramentoIndicadoresBean")
@ViewScoped
public class ExibirMonitoramentoIndicadoresBean {

	@EJB(name = "IndicadorServico")
	private IndicadorServico indicadorServico;

	@EJB(name = "OrgaoServico")
	private OrgaoServico orgaoServico;

	@EJB(name = "RegiaoServico")
	private RegiaoServico regiaoServico;

	@EJB(name = "UFServico")
	private UFServico ufServico;

	@EJB(name = "MunicipioServico")
	private MunicipioServico municipioServico;

	@EJB(name = "ConexaoServico")
	private ConexaoServico conexaoServico;

	private List<Regiao> regioes;

	private List<UF> estados;

	private List<Municipio> municipios;

	private final static String TAB_BRASIL = "BRASIL";

	private List<IndiceDTO> indicesBrasil;

	private List<TipoOrgao> operadoresBrasil;

	private List<OrgaoDTO> tiposOrgaosBrasil;

	private List<IndiceDTO> indicesRegiao;

	private List<TipoOrgao> operadoresRegiao;

	private List<OrgaoDTO> tiposOrgaosRegiao;

	private List<IndiceDTO> indicesEstado;

	private List<TipoOrgao> operadoresEstado;

	private List<OrgaoDTO> tiposOrgaosEstado;

	private List<OrgaoDTO> tiposOrgaosMunicipio;

	private List<EnderecoDTO> enderecosOrgao;

	private Regiao regiaoSelecionado;

	private UF estadoSelecionado;

	private Municipio municipioSelecionado;

	private OrgaoDTO tipoOrgaoSelecionado;
	
	private String formatoSelecionado;
	
	private boolean exportarXls = false;

	public List<IndiceDTO> getIndicesBrasil() {
		if (indicesBrasil == null) {
			try {
				indicesBrasil = indicadorServico.buscarIndicesBrasil();
			} catch (AtlasAcessoBancoDadosException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return indicesBrasil;
	}

	public void setIndicesBrasil(List<IndiceDTO> indicesBrasil) {
		this.indicesBrasil = indicesBrasil;
	}

	public List<Regiao> getRegioes() {
		if (regioes == null) {
			try {
				regioes = regiaoServico.buscarTodos(Regiao.class);
			} catch (AtlasAcessoBancoDadosException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return regioes;
	}

	public List<UF> getEstados() {
		if (estados == null) {
			try {
				estados = ufServico.recuperarTodasAsUFs();
			} catch (AtlasAcessoBancoDadosException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return estados;
	}

	public List<Municipio> getMunicipios() {
		if (estadoSelecionado != null) {
			try {
				setExportarXls(true);
				municipios = municipioServico
						.recuperarMunicipios(estadoSelecionado);
				
			} catch (AtlasAcessoBancoDadosException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			setExportarXls(false);
		}
		return municipios;
	}

	public List<OrgaoDTO> getTiposOrgaosBrasil() {
		if (tiposOrgaosBrasil == null) {
			try {
				tiposOrgaosBrasil = orgaoServico.recuperarTipoOrgaoPorBrasil();
			} catch (AtlasAcessoBancoDadosException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return tiposOrgaosBrasil;
	}

	public void setTiposOrgaosBrasil(List<OrgaoDTO> tiposOrgaosBrasil) {
		this.tiposOrgaosBrasil = tiposOrgaosBrasil;
	}

	public List<TipoOrgao> getOperadoresBrasil() {
		if (operadoresBrasil == null) {
			try {
				operadoresBrasil = indicadorServico.buscarOperadoresBrasil();
			} catch (AtlasAcessoBancoDadosException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return operadoresBrasil;
	}

	public void setOperadoresBrasil(List<TipoOrgao> operadoresBrasil) {
		this.operadoresBrasil = operadoresBrasil;
	}

	public List<IndiceDTO> getIndicesRegiao() {
		if (regiaoSelecionado != null) {
			try {
				indicesRegiao = indicadorServico.buscarIndicesPorRegiao(regiaoSelecionado);
			} catch (AtlasAcessoBancoDadosException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return indicesRegiao;
	}

	public void setIndicesRegiao(List<IndiceDTO> indicesRegiao) {
		this.indicesRegiao = indicesRegiao;
	}

	public List<TipoOrgao> getOperadoresRegiao() {
		if (regiaoSelecionado != null) {
			try {
				operadoresRegiao = indicadorServico.buscarOperadoresPorRegiao(regiaoSelecionado);
			} catch (AtlasAcessoBancoDadosException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return operadoresRegiao;
	}

	public void setOperadoresRegiao(List<TipoOrgao> operadoresRegiao) {
		this.operadoresRegiao = operadoresRegiao;
	}

	public List<OrgaoDTO> getTiposOrgaosRegiao() {
		if (regiaoSelecionado != null) {
			try {
				tiposOrgaosRegiao = new ArrayList<OrgaoDTO>();
				OrgaoDTO orgao = new OrgaoDTO();
				orgao.setNome(regiaoSelecionado.getNome());
				orgao.setOrgaos(orgaoServico.recuperarTipoOrgaoPorRegiao(regiaoSelecionado.getId()));
				tiposOrgaosRegiao.add(orgao);

			} catch (AtlasAcessoBancoDadosException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return tiposOrgaosRegiao;
	}

	public List<IndiceDTO> getIndicesEstado() {
		if (estadoSelecionado != null) {
			try {
				if(estadoSelecionado!=null){					
					indicesEstado = indicadorServico.buscarIndicesPorUf(estadoSelecionado);	
				}
				
			} catch (AtlasAcessoBancoDadosException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return indicesEstado;
	}

	public void setIndicesEstado(List<IndiceDTO> indicesEstado) {
		this.indicesEstado = indicesEstado;
	}

	public List<TipoOrgao> getOperadoresEstado() {
		if (estadoSelecionado != null) {
			try {
				operadoresEstado = indicadorServico.buscarOperadoresPorEstado(estadoSelecionado);
			} catch (AtlasAcessoBancoDadosException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return operadoresEstado;
	}

	public void setOperadoresEstado(List<TipoOrgao> operadoresEstado) {
		this.operadoresEstado = operadoresEstado;
	}

	public List<OrgaoDTO> getTiposOrgaosEstado() {
		if (estadoSelecionado != null) {
			try {
				tiposOrgaosEstado = new ArrayList<OrgaoDTO>();
				OrgaoDTO orgao = new OrgaoDTO();
				orgao.setNome(estadoSelecionado.getNome());
				orgao.setOrgaos(orgaoServico.recuperarTipoOrgaoPorEstado(estadoSelecionado.getSigla()));
				tiposOrgaosEstado.add(orgao);
			} catch (AtlasAcessoBancoDadosException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return tiposOrgaosEstado;
	}

	public void setTiposOrgaosEstado(List<OrgaoDTO> tiposOrgaosEstado) {
		this.tiposOrgaosEstado = tiposOrgaosEstado;
	}

	public List<SelectItem> getCategories() {
		List<SelectItem> categories = null;
		try {
			categories = new ArrayList<SelectItem>();
			List<Regiao> regioes = regiaoServico.buscarTodos(Regiao.class);
			for (Regiao regiao : regioes) {
				SelectItemGroup grupoRegiao = new SelectItemGroup(
						regiao.getNome());
				List<UF> ufs = ufServico.recuperarUFsPorRegiao(regiao.getId());
				SelectItemGroup[] itensUf = new SelectItemGroup[ufs.size()];
				for (int j = 0; j < ufs.size(); j++) {
					SelectItemGroup grupoUf = new SelectItemGroup(ufs.get(j)
							.getNome());
					List<Municipio> municipios = municipioServico
							.recuperarMunicipios(ufs.get(j));
					SelectItem[] itensMunicipio = new SelectItem[municipios
							.size()];
					for (int i = 0; i < municipios.size(); i++) {
						itensMunicipio[i] = new SelectItem(municipios.get(i)
								.getNome(), municipios.get(i).getNome());
					}
					grupoUf.setSelectItems(itensMunicipio);
					itensUf[j] = grupoUf;
				}
				grupoRegiao.setSelectItems(itensUf);
				categories.add(grupoRegiao);
			}

		} catch (AtlasAcessoBancoDadosException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return categories;
	}

	public List<OrgaoDTO> getTiposOrgaosMunicipio() {
		if (municipioSelecionado != null) {
			try {
				tiposOrgaosMunicipio = orgaoServico
						.recuperarTipoOrgaoPorMunicipio(municipioSelecionado
								.getNome());
			} catch (AtlasAcessoBancoDadosException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			return null;
		}
		return tiposOrgaosMunicipio;
	}

	public void setTiposOrgaosMunicipio(List<OrgaoDTO> tiposOrgaosMunicipio) {
		this.tiposOrgaosMunicipio = tiposOrgaosMunicipio;
	}

	public List<EnderecoDTO> getEnderecosOrgao() {
		if (tipoOrgaoSelecionado != null) {
			String tipo = tipoOrgaoSelecionado.getNome();
			String municipio = municipioSelecionado.getNome();
			try {
				enderecosOrgao = orgaoServico
						.recuperarEnderecoPorTipoMunicipio(tipo, municipio);
			} catch (AtlasAcessoBancoDadosException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return enderecosOrgao;
	}

	public void setEnderecosOrgao(List<EnderecoDTO> enderecosOrgao) {
		this.enderecosOrgao = enderecosOrgao;
	}

	public Municipio getMunicipioSelecionado() {
		return municipioSelecionado;
	}

	public void setMunicipioSelecionado(Municipio municipioSelecionado) {
		this.municipioSelecionado = municipioSelecionado;
	}

	public MunicipioServico getMunicipioServico() {
		return municipioServico;
	}

	public void setMunicipioServico(MunicipioServico municipioServico) {
		this.municipioServico = municipioServico;
	}

	public UF getEstadoSelecionado() {
		return estadoSelecionado;
	}

	public void setEstadoSelecionado(UF estadoSelecionado) {
		this.estadoSelecionado = estadoSelecionado;
	}

	public Regiao getRegiaoSelecionado() {
		return regiaoSelecionado;
	}

	public void setRegiaoSelecionado(Regiao regiaoSelecionado) {
		this.regiaoSelecionado = regiaoSelecionado;
	}

	public OrgaoDTO getTipoOrgaoSelecionado() {
		return tipoOrgaoSelecionado;
	}

	public void setTipoOrgaoSelecionado(OrgaoDTO tipoOrgaoSelecionado) {
		this.tipoOrgaoSelecionado = tipoOrgaoSelecionado;
	}
	
	public String getFormatoSelecionado() {
		return formatoSelecionado;
	}

	public void setFormatoSelecionado(String formatoSelecionado) {
		this.formatoSelecionado = formatoSelecionado;
	}

	public boolean isExportarXls() {
		return !exportarXls;
	}

	public void setExportarXls(boolean exportarXls) {
		this.exportarXls = exportarXls;
	}

	public void exportarRelatorio() {
		try {
			if ((getFormatoSelecionado() != null)
					&& (!getFormatoSelecionado().equals("0"))) {
				gerarRelatorioEnderecos(getFormatoSelecionado());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void gerarRelatorioEnderecos(String tipoRelatorio) throws Exception {
		Map<String, Object> parametros = new HashMap<>();
		parametros.put("MUNICIPIO", municipioSelecionado!=null?municipioSelecionado.getNome():null);
		parametros.put("ESTADO", estadoSelecionado!=null?estadoSelecionado.getSigla():null);
		RelatorioUtil.gerarRelatorio(null,
				"/relatorios/relatorioExpEnderecoCompleto.jasper", "relatorioEnderecos."
						+ tipoRelatorio, parametros, tipoRelatorio,
						conexaoServico.conn());
		
	}
	
	public void gerarRelatorioOrgaos(String tipoRelatorio, String filtro) throws Exception {
		Map<String, Object> parametros = new HashMap<>();
		List<?> dados = new ArrayList<>();
		switch (filtro) {
		case "BRASIL":
			dados =  getTiposOrgaosBrasil();
			break;
		case "REGIAO":
			dados = getTiposOrgaosRegiao();
			break;
		case "ESTADO":
			dados = getTiposOrgaosEstado();
			break;
		default:
			break;
		}
		
		RelatorioUtil.gerarRelatorio(dados,
				"/relatorios/relatorioTipoOrgaos.jasper", "relatorioTipoOrgaos."
						+ tipoRelatorio, parametros, tipoRelatorio);
	}	
	

}
