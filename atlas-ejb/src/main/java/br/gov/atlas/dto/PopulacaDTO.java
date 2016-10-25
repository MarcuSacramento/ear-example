package br.gov.atlas.dto;

import java.util.Date;
import java.util.List;

import br.gov.atlas.entidade.AtlasEntidade;

@SuppressWarnings("serial")
public class PopulacaDTO extends AtlasEntidade {

		private int ano;
		private String uf;
		private String sexo;
		private int quantidade;
		
		public PopulacaDTO() 
		{
			setPrimeiroRegistro(0);
		}

		@Override
		public Integer getId() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void setId(Integer id) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public String getLabel() {
			// TODO Auto-generated method stub
			return null;
		}

		public int getAno() {
			return ano;
		}

		public void setAno(int ano) {
			this.ano = ano;
		}

		public String getUf() {
			return uf;
		}

		public void setUf(String uf) {
			this.uf = uf;
		}

		public String getSexo() {
			return sexo;
		}

		public void setSexo(String sexo) {
			this.sexo = sexo;
		}

		public int getQuantidade() {
			return quantidade;
		}

		public void setQuantidade(int quantidade) {
			this.quantidade = quantidade;
		}

		
}
