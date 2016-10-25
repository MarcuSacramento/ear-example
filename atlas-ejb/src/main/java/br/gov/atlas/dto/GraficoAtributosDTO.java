package br.gov.atlas.dto;


public class GraficoAtributosDTO {
	
	public static final double MIL 		= 1000; 
	public static final double MILHAO 	= 1000000; 
	public static final double BILHAO	= 1000000000; 

	private static final Double UM = Double.valueOf(1);
	
	private Double divisor;
	private Double multiplicador;
	
	private Double maior;
	private Double menor;
	
	
	/**
	 * @return the labelCalculo
	 */
	public String getLabelCalculo() {
		if (!getMultiplicador().equals(UM)){
			return " ( / " + multiplicador.longValue() + ")";
		} else if (!getDivisor().equals(UM)){
			if (divisor.equals(MIL)){
				return " (Mil)";
			} else if (divisor.equals(MILHAO)){
				return " (Milhão)";
			} else if (divisor.equals(BILHAO)){
				return " (Bilhão)";
			}
		}
		
		return "";
	}
	
	/**
	 * @return the divisor
	 */
	public Double getDivisor() {
		if (divisor == null && !getMaior().equals(Double.MIN_VALUE)){
			if (getMaior() > GraficoAtributosDTO.MIL){
				divisor = Double.valueOf(GraficoAtributosDTO.MIL); 
			} else if (getMaior() > GraficoAtributosDTO.MILHAO){
				divisor = Double.valueOf(GraficoAtributosDTO.MILHAO); 
			} else if (getMaior() > GraficoAtributosDTO.BILHAO){
				divisor = Double.valueOf(GraficoAtributosDTO.BILHAO); 
			} else {
				divisor = UM;
			}
		}
		return divisor;
	}
	
	/**
	 * @param divisor the divisor to set
	 */
	public void setDivisor(Double divisor) {
		this.divisor = divisor;
	}
	
	/**
	 * @return the multiplicador
	 */
	public Double getMultiplicador() {
		if (multiplicador == null){
			multiplicador = UM;
			Double maiorCalculo = getMaior();
			
			if (maiorCalculo < 1){
				do {
					multiplicador *= 10;
					maiorCalculo *= 10;
				} while (maiorCalculo < 1);
			}
			
		}
		return multiplicador;
	}

	/**
	 * @param multiplicador the multiplicador to set
	 */
	public void setMultiplicador(Double multiplicador) {
		this.multiplicador = multiplicador;
	}

	/**
	 * @return the maior
	 */
	public Double getMaior() {
		if (maior == null){
			maior = Double.MIN_VALUE;
		}
		return maior;
	}

	/**
	 * @param maior the maior to set
	 */
	public void setMaior(Double maior) {
		this.maior = maior;
	}

	/**
	 * @return the menor
	 */
	public Double getMenor() {
		if (menor == null){
			menor = Double.MAX_VALUE;
		}
		return menor;
	}

	/**
	 * @param menor the menor to set
	 */
	public void setMenor(Double menor) {
		this.menor = menor;
	}

}
