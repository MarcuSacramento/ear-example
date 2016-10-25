package br.gov.atlas.entity;

import org.hibernate.envers.RevisionListener;

import br.gov.atlas.util.ThreadLocalUsuarioLogado;

public class AtlasRevisionListener implements RevisionListener {
	
	public void newRevision(Object revisionEntity) {
		 
		AtlasRevisionEntity revEntity = (AtlasRevisionEntity) revisionEntity;
		String usuarioLogado;
		
		if(ThreadLocalUsuarioLogado.currentInstance() == null){
			usuarioLogado = "Usuário não identificado";
		}else{
			usuarioLogado = ThreadLocalUsuarioLogado.currentInstance().getUsuarioLogado();
		}
		revEntity.setNome( usuarioLogado );
	}
}