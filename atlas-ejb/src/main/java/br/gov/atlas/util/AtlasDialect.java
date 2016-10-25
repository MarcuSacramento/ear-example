package br.gov.atlas.util;

import java.sql.Types;

import org.hibernate.dialect.PostgreSQLDialect;

public class AtlasDialect extends PostgreSQLDialect{
	
	public AtlasDialect() {
		super();
		 registerColumnType( Types.BLOB, "oid" );
		 registerColumnType( Types.CLOB, "oid" );
		 registerColumnType( Types.NCLOB, "oid" );
	}
}
