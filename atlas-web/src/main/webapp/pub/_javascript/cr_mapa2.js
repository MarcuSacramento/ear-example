// JavaScript Document
var estado = "";
var positionArrow = "";

jQuery(".prev").mouseover(function(e){e.preventDefault()});
jQuery(".next").mouseover(function(e){e.preventDefault()});


var mapa = {
	init : function(){
		mapa.listaEstados();
		mapa.mapa();
	},
	listaEstados : function(){
		$('.lstEstados').jCarouselLite({
			btnNext    : '.boxSelecioneEstados .next',
			btnPrev    : '.boxSelecioneEstados .prev',
			vertical   : true,
			circular   : false,
			visible    : 12, 
			scroll 	   : 3,
			speed      : 1000
		});	
	},
	mapa : function(){
		$('.imgMapa map area').mouseover(function(e){
			$('.boxMiddleCinza ul').html('');

			if('.arrow'+positionArrow != '.arrow'){
				$('.arrow'+positionArrow).removeClass('arrow' + positionArrow);
			}
			
			if($(this).attr('class') == 'ativo'){
				$('.ativo').removeClass('ativo');
				$('#mapa').attr('src', '/pub/_imagens/mapa/blank.png');
				$('.boxNoticias').removeClass('boxNoticias-' + estado).hide();
				return false;
			}
			
			if($('.ativo'))
			{
				$('.ativo').removeClass('ativo');
				$('#mapa').attr('src', '/pub/_imagens/mapa/blank.png');
				$('.boxNoticias').removeClass('boxNoticias-' + estado).hide();
			}
			
			estado = $(this).attr('alt');
			
			$(this).addClass('ativo');

			$('#mapa').attr('src', '/pub/_imagens/mapa/estados/img-' + estado + '.png');

			e.preventDefault();
		});

		var strHtml = "";
	}
}

$(document).ready(function(){
	mapa.init();
});
