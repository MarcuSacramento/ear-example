package br.gov.atlas.dto;

import java.util.List;

import br.gov.atlas.entidade.AtlasEntidade;

@SuppressWarnings("serial")
public class ProfissaoDTO extends AtlasEntidade {

		private Integer id;
		private String nome;
		private String tipoProfissao;

		
		private List<ProfissaoDTO> usuarios;

		private List<OrgaoDTO> orgaos;
		
		public ProfissaoDTO() 
		{
			setPrimeiroRegistro(0);
		}
		
		public ProfissaoDTO(Integer id){
			this.id = id;
		}

		@Override
		public Integer getId() {
			return id;
		}
		
		@Override
		public void setId(Integer id) {
			this.id = id;
		}
		
		public List<ProfissaoDTO> getUsuarios() {
			return usuarios;
		}

		public void setUsuarios(List<ProfissaoDTO> usuarios) {
			this.usuarios = usuarios;
		}

		public List<OrgaoDTO> getOrgaos() {
			return orgaos;
		}

		public void setOrgaos(List<OrgaoDTO> orgaos) {
			this.orgaos = orgaos;
		}

		@Override
		public String getLabel() {
			return null;
		}
		public String getNome() {
			return nome;
		}
		public void setNome(String nome) {
			this.nome = nome;
		}

		public String getTipoProfissao() {
			return tipoProfissao;
		}

		public void setTipoProfissao(String tipoProfissao) {
			this.tipoProfissao = tipoProfissao;
		}
		

}
