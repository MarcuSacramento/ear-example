//Constantes
var MAX_FONTE = 16;
var MIN_FONTE = 12;
var AUMENTAR = 1;
//Variável que armazena o tamanho corrente
var fonteCorrente = 12; //Definir o tamanho inicial com o tamanho da fonte do css padrão.
var fonteClasse = 12; //Definir o tamanho inicial com o tamanho da fonte do css padrão.
function zoomClasse(tipoOperacao) { //1 para reduzir, 2 para aumentar
	if (tipoOperacao == AUMENTAR) {
		if (fonteClasse < MAX_FONTE) 
			fonteClasse++;
	}
	else { //Se não for aumentar, reduz.
		if (fonteCorrente > MIN_FONTE) 
			fonteClasse--;				
	}
	$('.atlas_geral_miolo, .atlas_geral_miolo *').css({'font-size': fonteClasse + 'px'});
}