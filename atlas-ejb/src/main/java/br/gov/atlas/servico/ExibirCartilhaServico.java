package br.gov.atlas.servico;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.hibernate.Session;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.engine.spi.SessionFactoryImplementor;

import br.gov.atlas.dao.CartilhaDao;
import br.gov.atlas.entidade.Cartilha;
import br.gov.atlas.entity.CartilhaLinhaComTresRegistrosAdapter;
import br.gov.atlas.entity.ListaPaginada;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;

@Stateless(name="ExibirCartilhaServico")
@LocalBean
public class ExibirCartilhaServico extends AtlasServicoImpl {

	private CartilhaDao dao;

	@Override
	public CartilhaDao getDao() {
		if(dao==null)
			dao = new CartilhaDao(em);
		return dao;
	}

	public List<Cartilha> recuperarCartilhas(Integer page, boolean somentePublicacoesConsumidor) throws AtlasAcessoBancoDadosException {
		return getDao().recuperarCartilhas(page, somentePublicacoesConsumidor);
	}

	public List<CartilhaLinhaComTresRegistrosAdapter> recuperarCartilhasComLinhasDeTresRegistros(Integer page, boolean somentePublicacoesConsumidor) throws AtlasAcessoBancoDadosException {
		List<Cartilha> listaDeCartilhas = recuperarCartilhas(page, somentePublicacoesConsumidor);
		LadoProximoRegistro ladoProximoRegistro = LadoProximoRegistro.ESQUERDA;
		CartilhaLinhaComTresRegistrosAdapter estaLinhaComTresRegistros = new CartilhaLinhaComTresRegistrosAdapter();

		List<CartilhaLinhaComTresRegistrosAdapter> resultado = new ArrayList<>();

		for (Cartilha registroCorrente : listaDeCartilhas) {
			if( ladoProximoRegistro.equals(LadoProximoRegistro.ESQUERDA) ) {
				ladoProximoRegistro = LadoProximoRegistro.CENTRO;
				estaLinhaComTresRegistros = new CartilhaLinhaComTresRegistrosAdapter();
				resultado.add(estaLinhaComTresRegistros);
				estaLinhaComTresRegistros.setRegistroAEsquerda(registroCorrente);
				//CENTRO
			}else if ( ladoProximoRegistro.equals(LadoProximoRegistro.CENTRO) ) {
				ladoProximoRegistro = LadoProximoRegistro.DIREITA;
				estaLinhaComTresRegistros.setRegistroCentro(registroCorrente);
				//DIREITA
			} else {
				ladoProximoRegistro = LadoProximoRegistro.ESQUERDA;
				estaLinhaComTresRegistros.setRegistroADireita(registroCorrente);
			}
		}
		return resultado;
	}

	public Integer recuperarTotalDePaginas(boolean somentePublicacoesConsumidor) throws AtlasAcessoBancoDadosException {
		return getDao().recuperarTotalDePaginas(somentePublicacoesConsumidor);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public ListaPaginada<Cartilha> recuperarCartilhas(Cartilha cartilhaFiltro) throws AtlasAcessoBancoDadosException {
		Session session = (Session) em.getDelegate();
		
		SessionFactoryImplementor sfi = (SessionFactoryImplementor) session.getSessionFactory();
		ConnectionProvider cp = sfi.getConnectionProvider();
		 try {
			Connection connection = cp.getConnection();
			connection.setAutoCommit(false);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return getDao().recuperarCartilhas(cartilhaFiltro);
	}

}

//enum LadoProximoRegistro { A_ESQUERDA, A_DIREITA; }
enum LadoProximoRegistro { ESQUERDA, CENTRO, DIREITA; }
