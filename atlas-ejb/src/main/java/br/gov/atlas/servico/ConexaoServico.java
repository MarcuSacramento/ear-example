package br.gov.atlas.servico;

import java.sql.Connection;
import java.sql.SQLException;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.hibernate.Session;
import org.hibernate.engine.spi.SessionFactoryImplementor;

import br.gov.atlas.dao.AtlasDao;

/**
 * 
 */
@Stateless(name = "ConexaoServico")
@LocalBean
public class ConexaoServico extends AtlasServicoImpl {
	
	@Override
	public AtlasDao getDao() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Connection conn() {
		try {
			Session session = (Session) em.getDelegate();
			SessionFactoryImplementor sfi = (SessionFactoryImplementor) session.getSessionFactory();
			org.hibernate.engine.jdbc.connections.spi.ConnectionProvider cp = sfi.getConnectionProvider();
			return cp.getConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
