package br.gov.atlas.dto;

import java.util.Date;
import java.util.List;

import br.gov.atlas.entidade.AtlasEntidade;

@SuppressWarnings("serial")
public class UsuarioDTO extends AtlasEntidade {

		private Integer id;
		private String nome;
		private String cpf;
		private String email;
		private String senha;
		private String ddd;
		private String telefone;
		private String situacao;
		private String sexo;
		private Date dataInclusao;
		
		private List<UsuarioDTO> usuarios;

		private List<OrgaoDTO> orgaos;
		
		public UsuarioDTO() 
		{
			setPrimeiroRegistro(0);
		}
		
		public UsuarioDTO(Integer id){
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
		
		public List<UsuarioDTO> getUsuarios() {
			return usuarios;
		}

		public void setUsuarios(List<UsuarioDTO> usuarios) {
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
		public String getCpf() {
			return cpf;
		}
		public void setCpf(String cpf) {
			this.cpf = cpf;
		}
		public String getEmail() {
			return email;
		}
		public void setEmail(String email) {
			this.email = email;
		}
		public String getSenha() {
			return senha;
		}
		public void setSenha(String senha) {
			this.senha = senha;
		}
		public String getDdd() {
			return ddd;
		}
		public void setDdd(String ddd) {
			this.ddd = ddd;
		}
		public String getTelefone() {
			return telefone;
		}
		public void setTelefone(String telefone) {
			this.telefone = telefone;
		}
		public String getSituacao() {
			return situacao;
		}
		public void setSituacao(String situacao) {
			this.situacao = situacao;
		}
		public String getSexo() {
			return sexo;
		}
		public void setSexo(String sexo) {
			this.sexo = sexo;
		}
		public Date getDataInclusao() {
			return dataInclusao;
		}
		public void setDataInclusao(Date dataInclusao) {
			this.dataInclusao = dataInclusao;
		}

}
