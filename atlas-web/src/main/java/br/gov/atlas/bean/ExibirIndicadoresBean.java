package br.gov.atlas.bean;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.omnifaces.util.Faces;

import br.gov.atlas.dto.GraficoAtributosDTO;
import br.gov.atlas.dto.GraficoDTO;
import br.gov.atlas.entidade.Indicador;
import br.gov.atlas.entidade.Regiao;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;
import br.gov.atlas.servico.IndicadorServico;
import br.gov.atlas.servico.RegiaoServico;
import br.gov.atlas.util.RelatorioUtil;

@ManagedBean(name = "exibirIndicadoresBean")
@ViewScoped
public class ExibirIndicadoresBean extends AtlasBean {

	private static final long serialVersionUID = -1786939894192775844L;
	
	private List<Regiao> regioes;
	private List<Indicador> indicadores;
	
	private Regiao regiao;
	private Indicador indicador;
	private String ano;
	
	private String grafico;
	private List<GraficoDTO> resultadoGrafico;
	
	@EJB(name = "RegiaoServico")
	private RegiaoServico regiaoServico;
	
	@EJB(name = "IndicadorServico")
	private IndicadorServico indicadorServico;
	
	private GraficoAtributosDTO atributos;
	private String tipoRelatorio;
	private boolean possuiDados;

	/*
	 * (non-Javadoc)
	 * @see br.gov.atlas.bean.AtlasBean#efetuarPesquisa()
	 */
	@Override
	protected void efetuarPesquisa() throws AtlasAcessoBancoDadosException {
		this.grafico = null;
		this.atributos = null;
		//this.resultadoGrafico = getServico().buscaIndicador(getIndicador(), regiao, Integer.parseInt(ano));
	}

	/*
	 * (non-Javadoc)
	 * @see br.gov.atlas.bean.AtlasBean#getServico()
	 */
	@Override
	protected IndicadorServico getServico() {
		return indicadorServico;
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
	 * @return the regioes
	 */
	@SuppressWarnings("unchecked")
	public List<Regiao> getRegioes() {
		if (regioes == null || regioes.isEmpty()){
			try {
				regioes = regiaoServico.buscarTodos(Regiao.class, "nome");
			} catch (AtlasAcessoBancoDadosException e) {
				regioes = new ArrayList<>();
				e.printStackTrace();
			}
		}
		return regioes;
	}

	/**
	 * @return the regiao
	 */
	public Regiao getRegiao() {
		return regiao;
	}

	/**
	 * @param regiao the regiao to set
	 */
	public void setRegiao(Regiao regiao) {
		this.regiao = regiao;
	}

	/**
	 * @return the indicador
	 */
	public Indicador getIndicador() {
		if (indicador == null){
			indicador = getIndicadores().get(0);
		}
		return indicador;
	}

	/**
	 * @param indicador the indicador to set
	 */
	public void setIndicador(Indicador indicador) {
		this.indicador = indicador;
	}

	/**
	 * @return the indicadores
	 */
	public List<Indicador> getIndicadores() {
		if (indicadores == null || indicadores.isEmpty()){
			try {
				indicadores = indicadorServico.buscarTodos();
			} catch (AtlasAcessoBancoDadosException e) {
				indicadores = new ArrayList<>();
				e.printStackTrace();
			}
		}
		return indicadores;
	}

	/**
	 * @return the ano
	 */
	public String getAno() {
		if (ano == null){
			ano = "2013";
		}
		return ano;
	}

	/**
	 * @param ano the ano to set
	 */
	public void setAno(String ano) {
		this.ano = ano;
	}

	/**
	 * @return the grafico
	 */
	public String getGrafico() {
		if (grafico == null){
			pesquisar();
			grafico = "&DOMId=myChartId&registerWithJS=1&debugMode=0&dataXML=" + gerarGraficoBarra();
		}
		
		return grafico;
	}
	
	/**
	 * Gera o gráfico Barra.
	 * @return grafico em barra
	 */
	public String gerarGraficoBarra(){
		preencheAtributosGrafico();
		
		possuiDados = false;
		for (GraficoDTO dto : resultadoGrafico) {
			if (!BigDecimal.ZERO.setScale(10).max(dto.getResultado()).equals(BigDecimal.ZERO.setScale(10))){
				possuiDados = true;
				break;
			}
		}
		
		if (!possuiDados){
			return "<chart></chart>";
		}
		
		StringBuilder strXML = new StringBuilder("");
		strXML.append("<chart caption='");
		strXML.append(getIndicador().getNome());
		strXML.append("' plotGradientColor=' ' yAxisName='Valor' numberPrefix='' formatNumber='1' rotateValues='1' ");
		strXML.append(" formatNumberScale='0' decimalPrecision='2' decimalSeparator=',' thousandSeparator='.' labelDisplay='rotate' exportEnabled='1' >");

		if(getIndicador().getSiglaIdentificador().equalsIgnoreCase("INAJg") || getIndicador().getSiglaIdentificador().equalsIgnoreCase("INAJess") ){
			for (GraficoDTO grafico : resultadoGrafico) {
				strXML.append("<set label='" + grafico.getUf() + "' value='");
				strXML.append( grafico.getResultado() + "' />");
				grafico.setResultadoCalculado(grafico.getResultado());
			}
		}else{
			for (GraficoDTO grafico : resultadoGrafico) {
				strXML.append("<set label='" + grafico.getUf() + "' value='");
				strXML.append( grafico.getResultadoCalculado(atributos.getMultiplicador(), atributos.getDivisor()) + "' />");
			}
		}
		
	    strXML.append("</chart>");

	    return strXML.toString();
	}

	/**
	 * Preenche atributos grafico.
	 */
	private void preencheAtributosGrafico() {
		atributos = new GraficoAtributosDTO();
		
		for (GraficoDTO grafico : resultadoGrafico) {
			double valor = grafico.getResultado().doubleValue();
			if (valor > atributos.getMaior()){
				atributos.setMaior(valor);
			}
			if (valor < atributos.getMenor()){
				atributos.setMenor(valor);
			}
		}
	}

	/**
	 * @return the resultadoGrafico
	 */
	public List<GraficoDTO> getResultadoGrafico() {
		return resultadoGrafico;
	}
	
	/**
	 * Gerar relatorio indicador.
	 *
	 * @throws Exception
	 */
	public void gerarRelatorioIndicador() throws Exception {
		Map<String, Object> parametros = new HashMap<>();
		
		parametros.put("titulo", getIndicador().getNome() + atributos.getLabelCalculo());
		if (getRegiao() != null){
			parametros.put("regiao", getRegiao().getNome());
		} else {
			parametros.put("regiao", "Brasil");
		}
		parametros.put("ano", getAno());
		parametros.put("descritivo", getIndicador().getExplicativo());
		
		if(!getTipoRelatorio().isEmpty()){
			RelatorioUtil.gerarRelatorio(getResultadoGrafico(), "/relatorios/indicador.jasper", "indicador." + getTipoRelatorio(), parametros, getTipoRelatorio());
		}
	}

	/**
	 * Gerar relatorio pre definido.
	 *
	 * @throws Exception
	 */
	/* Comentado por solicitação do César. Data: 27/11
	public void gerarRelatorioPreDefinido() throws Exception {
		Map<String, Object> parametros = new HashMap<>();
		
		parametros.put("titulo", "QI + POP");
		parametros.put("regiao", "Brasil");
		parametros.put("ano", "2013");
		parametros.put("descritivo", "");
		
		List<GraficoDTO> resultadoPreDefinido = getServico().buscaIndicador(null, null, 2013);
		RelatorioUtil.gerarRelatorio(resultadoPreDefinido, "/relatorios/indicador.jasper", "indicador.pdf", parametros, RelatorioUtil.PDF);
	}*/
	
	
	public void efetuarDownload(String url) throws IOException{
		File file = new File(url);
		Faces.sendFile(file, true);
	}

	/**
	 * @return the atributos
	 */
	public GraficoAtributosDTO getAtributos() {
		return atributos;
	}

	/**
	 * @return the tipoRelatorio
	 */
	public String getTipoRelatorio() {
		if (tipoRelatorio == null){
			tipoRelatorio = "";
		}
		return tipoRelatorio;
	}

	/**
	 * @param tipoRelatorio the tipoRelatorio to set
	 */
	public void setTipoRelatorio(String tipoRelatorio) {
		this.tipoRelatorio = tipoRelatorio;
	}

	/**
	 * @return the possuiDados
	 */
	public boolean isPossuiDados() {
		return possuiDados;
	}


}
