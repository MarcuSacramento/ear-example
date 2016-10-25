package br.gov.atlas.servico;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import br.gov.atlas.dao.AnexoDao;
import br.gov.atlas.entidade.Anexo;
import br.gov.atlas.entidade.Mensageria;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;
import br.gov.atlas.exception.AtlasNegocioException;

@Stateless(name = "AnexoServico")
@LocalBean
public class AnexoServico extends AtlasServicoImpl {

	private AnexoDao dao;
	
	@Override
	public AnexoDao getDao() {
		if (dao == null) {
			dao = new AnexoDao(em);
		}
		return dao;
	}

	public void salvarAnexos(Mensageria mensagem, List<Anexo> anexos)
			throws AtlasAcessoBancoDadosException, AtlasNegocioException {
		if(mensagem.getId() != null){
			// Recuperando os anexos do banco para remoção
			List<Anexo> anexosBanco = recuperarAnexosMensagem(mensagem);
			if(anexosBanco != null && !anexosBanco.isEmpty()){
				for (Anexo anexoBanco : anexosBanco) {
					getDao().remover(anexoBanco);
				}
			}
			
			// Inserindo os novos anexos
			if(anexos != null){
				for (Anexo anexo : anexos) {
					anexo.setMensagem(mensagem);
				}
				getDao().salvar(anexos);
			}
		}
	}

	public List<Anexo> recuperarAnexosMensagem(Mensageria mensageria) throws AtlasAcessoBancoDadosException {
		return getDao().recuperarAnexosMensagem(mensageria);
	}

}