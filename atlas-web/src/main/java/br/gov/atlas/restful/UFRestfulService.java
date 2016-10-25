package br.gov.atlas.restful;

import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import br.gov.atlas.entidade.UF;
import br.gov.atlas.exception.AtlasAcessoBancoDadosException;

@Path("/json")
public class UFRestfulService {
	
	@GET
	@Path("/ufs")
	@Produces("application/json")
	public Response listarUfs() throws AtlasAcessoBancoDadosException, NamingException {
		
		List<UF> ufs = new ArrayList<UF>();
		
		UF ac = new UF("AC", "Acre");			    ufs.add(ac);
		UF al = new UF("AL","Alagoas");		        ufs.add(al);
		UF ap = new UF("AP","Amapá");			    ufs.add(ap);
		UF am = new UF("AM","Amazonas");		    ufs.add(am);
		UF ba = new UF("BA","Bahia");			    ufs.add(ba);
		UF ce = new UF("CE","Ceará");			    ufs.add(ce);
		UF df = new UF("DF","Distrito Federal");    ufs.add(df);
		UF es = new UF("ES","Espírito Santo");      ufs.add(es);
		UF go = new UF("GO","Goiás");			    ufs.add(go);
		UF ma = new UF("MA","Maranhão");		    ufs.add(ma);
		UF mt = new UF("MT","Mato Grosso"); 	    ufs.add(mt);
		UF ms = new UF("MS","Mato Grosso do Sul");  ufs.add(ms);
		UF mg = new UF("MG","Minas Gerais");	    ufs.add(mg);
		UF pa = new UF("PA","Pará");			    ufs.add(pa);
		UF pb = new UF("PB","Paraíba");			    ufs.add(pb);
		UF pr = new UF("PR","Paraná");			    ufs.add(pr);
		UF pe = new UF("PE","Pernambuco");		    ufs.add(pe);
		UF pi = new UF("PI","Piauí");			    ufs.add(pi);
		UF rj = new UF("RJ","Rio de Janeiro");      ufs.add(rj);
		UF rn = new UF("RN","Rio Grande do Norte"); ufs.add(rn);
		UF rs = new UF("RS","Rio Grande do Sul");   ufs.add(rs);
		UF ro = new UF("RO","Rondônia");		    ufs.add(ro);
		UF rr = new UF("RR","Roraima");				ufs.add(rr);
		UF sc = new UF("SC","Santa Catarina");	    ufs.add(sc);
		UF sp = new UF("SP","São Paulo");		    ufs.add(sp);
		UF se = new UF("SE","Sergipe");				ufs.add(se);
		UF to = new UF("TO","Tocantins");			ufs.add(to);
		
		return Response.status(200).entity(ufs).build();
	}
}
