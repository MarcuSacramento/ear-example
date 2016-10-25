package br.gov.atlas.visitor;

/**
 * Interface gen�rica para utiliza��o do padr�o de projeto <b>Visitor</b>
 * 
 * @author Vin�cius Rabelo
 * 
 */
public interface Visitable {
	public abstract void accept(Visitor visitor);
}
