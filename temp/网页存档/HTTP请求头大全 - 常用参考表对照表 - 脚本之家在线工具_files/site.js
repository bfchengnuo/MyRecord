$(document).ready(function(){
	dropdownOpen();
	/* ==========================================================================
	   菜单操作
	   ========================================================================== */
	/**
	 * 解除子菜单的点击切换事件，使一级菜单的链接可以被正常打开
	 */	
	//$(document).off('click.bs.dropdown.data-api');
	/**
	 * 鼠标划过就展开子菜单，免得需要点击才能展开
	 */
	function dropdownOpen() {

		var $dropdownLi = $('li.dropdown');

		$dropdownLi.mouseover(function() {
			$(this).addClass('open');
		}).mouseout(function() {
			$(this).removeClass('open');
		});
	}	
});

//输出HTML
function write_html(str)
{
	document.write(str);	
}

//输出atm边框

function write_atm(str)
{
	write_html('<div class="panel panel-default">');
	write_html('<div class="panel-body">');
	write_html(str);
	write_html('</div>');
	write_html('</div>');
}

//输出详情页ATM
function write_atm_detail(str)
{
	write_html('<div class="panel panel-default">');
	write_html('<div class="panel-body">');
	write_html(str);
	write_html('</div>');
	write_html('</div>');
}

