package br.gov.atlas.bean;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;

import br.gov.atlas.entidade.AtlasEntidade;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;
import br.gov.atlas.exception.AtlasNegocioException;

/**
 * Class AtlasCrudBean.
 * 
 * @param <ENTITY> - tipo genérico
 * @param <Integer> - tipo genérico
 */
public abstract class AtlasCrudBean<ENTITY extends AtlasEntidade> extends AtlasBean implements Serializable {

	private static final long serialVersionUID = -6832767248374872525L;

	private ENTITY entidade;

	/**
	 * Salva a entidade.
	 */
	public String salvar() 
	{
		try {
			if (getEntidade() != null && validar()) {
				getServico().salvar(getEntidade());
				info("REGISTRO_GRAVADO");
			}
		} catch (AtlasAcessoBancoDadosException | AtlasNegocioException e) {
			error(e);
		}
		return null;
	}
	
	public void salvarSemExibirMensagem()
	{
		try {
			if (validar()) {
				if (getEntidade() != null) 
				{
					getServico().salvar(getEntidade());
				}
			}
		} catch (AtlasAcessoBancoDadosException | AtlasNegocioException e) {
			error(e);
		}
	}

	public abstract Boolean validar() throws AtlasNegocioException;

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.atlas.bean.AtlasBean#inicializarObjetos()
	 */
	@Override
	public void inicializarObjetos() throws AtlasAcessoBancoDadosException {
		super.inicializarObjetos();
		setEntidade(null);
	}

	/**
	 * Carrega a tela para Editar a entidade.
	 * 
	 * @param pk - pk
	 */
	@SuppressWarnings("unchecked")
	public String editar(Integer pk) {
		try {
			setEntidade((ENTITY) getServico().pesquisarPorId(pk, getPersistentClass()));
			initCarregarEntidade();

		} catch (AtlasAcessoBancoDadosException e) {
			error(e);
		}
		return getTelaCadastro();
	}

	/**
	 * Excluir a entidade.
	 * 
	 * @param pk - pk
	 */
	public final void excluir(AtlasEntidade pk) {
		try {
			getServico().remover(pk);
			info("MSG6");
			pesquisar();
		} catch (AtlasAcessoBancoDadosException | AtlasNegocioException e) {
			error(e);
		}
	}

	/**
	 * Desativar a entidade.
	 * 
	 * @param pk - pk
	 */
	public final void desativar(Integer pk) {
		try {
			getServico().desativar(pk);
			info("MSG6");
			pesquisar();
		} catch (AtlasAcessoBancoDadosException | AtlasNegocioException e) {
			error(e);
		}
	}

	/**
	 * Ativar a entidade.
	 * 
	 * @param pk - pk
	 */
	public final void ativar(Integer pk) {
		try {
			getServico().ativar(pk);
			info("MSG4");
			pesquisar();
		} catch (AtlasAcessoBancoDadosException | AtlasNegocioException e) {
			error(e);
		}
	}

	/**
	 * Init editar.
	 * 
	 * @throws AtlasAcessoBancoDadosException - atlas acesso banco dados exception
	 */
	protected void initCarregarEntidade() throws AtlasAcessoBancoDadosException {
	}

	/**
	 * Recupera o entidade.
	 * 
	 * @return o atributo entidade
	 */
	@SuppressWarnings("unchecked")
	public ENTITY getEntidade() {
		if (entidade == null) {
			try {
				Class<ENTITY> persistentClass = (Class<ENTITY>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
				entidade = persistentClass.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return entidade;
	}

	/**
	 * Preenche entidade.
	 * 
	 * @param entidade preenche o atributo entidade
	 */
	public void setEntidade(ENTITY entidade) {
		this.entidade = entidade;
	}

	@SuppressWarnings("unchecked")
	public Class<ENTITY> getPersistentClass() throws AtlasAcessoBancoDadosException {
		ParameterizedType paramType = (ParameterizedType) getClass().getGenericSuperclass();
		return (Class<ENTITY>) paramType.getActualTypeArguments()[0];
	}

}
