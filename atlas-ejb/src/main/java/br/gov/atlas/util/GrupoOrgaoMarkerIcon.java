package br.gov.atlas.util;

public enum GrupoOrgaoMarkerIcon {

	/*public static int ID_GRUPO_ORGAO_CARTORIO = 1;
	public static int ID_GRUPO_ORGAO_FORUM = 2;
	public static int ID_GRUPO_ORGAO_PROCON = 3;
	public static int ID_GRUPO_ORGAO_MINISTERIO_PUBLICO = 4;
	public static int ID_GRUPO_ORGAO_DEFENSORIA = 5;
	public static int ID_GRUPO_ORGAO_PENITENCIARIA = 6;
	public static int ID_GRUPO_ORGAO_OAB = 7;*/
	
	ICON_CARTORIO(
			1,
			"http://chart.apis.google.com/chart?chst=d_map_pin_letter&chld=%E2%80%A2|cc0000"), ICON_DEFENSORIA(
					2,
					"http://chart.apis.google.com/chart?chst=d_map_pin_letter&chld=%E2%80%A2|ffa52c"), ICON_FORUM(
							3,
							"http://chart.apis.google.com/chart?chst=d_map_pin_letter&chld=%E2%80%A2|85ff2c"), ICON_MINISTERIO_PUBLICO(
									4,
									"http://chart.apis.google.com/chart?chst=d_map_pin_letter&chld=%E2%80%A2|6600ff"), ICON_OAB(
											5,
											"http://chart.apis.google.com/chart?chst=d_map_pin_letter&chld=%E2%80%A2|66340e"), ICON_PENITENCIARIA(
													6,
													"http://chart.apis.google.com/chart?chst=d_map_pin_letter&chld=%E2%80%A2|ff2c86"), ICON_PROCON(
															7,
															"http://chart.apis.google.com/chart?chst=d_map_pin_letter&chld=%E2%80%A2|192e5b"), DEFAULT(
																	0,
																	"http://chart.apis.google.com/chart?chst=d_map_pin_letter&chld=%E2%80%A2|FE7569");
	;

	private int idGrupoOrgao;
	private String markerIcon;

	private GrupoOrgaoMarkerIcon(int idGrupoOrgao, String markerIcon) {
		this.idGrupoOrgao = idGrupoOrgao;
		this.markerIcon = markerIcon;
	}

	public int getIdGrupoOrgao() {
		return idGrupoOrgao;
	}

	public void setIdGrupoOrgao(int idGrupoOrgao) {
		this.idGrupoOrgao = idGrupoOrgao;
	}

	public String getMarkerIcon() {
		return markerIcon;
	}

	public void setMarkerIcon(String markerIcon) {
		this.markerIcon = markerIcon;
	}

	public static String getMarkerIcon(int idGrupoOrgao){
		String markerIcon = DEFAULT.getMarkerIcon();
		for (GrupoOrgaoMarkerIcon mi : GrupoOrgaoMarkerIcon.values()) {
			if(mi.getIdGrupoOrgao() == idGrupoOrgao){
				markerIcon = mi.getMarkerIcon();
				break;
			}
		}
		return markerIcon;
	}

}
