package br.gov.atlas.visitor;

/**
 * Interface genérica para utilização do padrão de projeto <b>Visitor</b>
 * 
 * @author Vinícius Rabelo
 * 
 */
public interface Visitable {
	public abstract void accept(Visitor visitor);
}
