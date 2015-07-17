$.browser.msie && document.execCommand("BackgroundImageCache",false,true) ;
(function($) {
    $.fn.lazyload = function(options) {
		
		this.each(function() {
            var self = $(this), srcImg = self.attr("src");
			self.get(0).onerror = function() {
				self.attr("src", srcImg);
			}
           	self.attr("src", self.attr("original"));
			}) ;
			/*var imgObject = [];
			function handle(type, src , Jelem, i) {
				switch(type) {
					case "load" :
						Jelem.attr("src", src);
						break;
					case "error":
					case "abort":
				}
				this = null;
				//document.removeChild(imgObject[i]);
				imgObject[i] = null;
			}
			this.each(function() {
				var self = $(this), srcImg = self.attr("src"), elem = self.get(0), original = self.attr("original");
				var img = new Image();
				img.onload  	= function(){ handle.call(this, 'load', original, self, imgObject.length); };
				img.onerror 	= function(){ handle.call(this, 'error', original, self, imgObject.length); };
				img.onabort 	= function(){ handle.call(this, 'abort', original, self, imgObject.length); };
				img.original    = original;
				imgObject.push(img);
			});
			for(var i = 0 , L = imgObject.length; i < L; i++) 
				imgObject[i].src = imgObject[i].original;*/
	}
})(jQuery);
var tool = {
	trim:$.trim,
	empty:function(str) {
		return $.trim(str) == "";
	},
	
	isEmptyorundefined:function(el) {
		return tool.isUndefined(el) || tool.empty(el);
	},
	
	isUndefined:function(el) {
		
		return typeof el == "undefined";
	},
	
	urlParam:function(url, name) {
		url = decodeURI(url).split("#")[0];
		var r = new RegExp("(?:\\b"+name+"\\b\\s*=\\s*)([^&]*)?", "ig");
		var parm = r.exec(url);
		return parm ?  parm[1] : null;
	}
}


var epgkey = {
	//键事件hash表
	keyMethod:{},
	keyValue:{},
	keyValueEvent:null,
	//是否开启键盘监听
	onKeyMonitor:true,
	onKeyRuning:false,
	keyMethodstr:{},
	setKey:function(keycode, fun) {
		var keyA = epgkey.keyMethod;
		keyA[keycode] = fun;
		
	},
	
	execKeyEvent:function(keycode) {
		var A = epgkey.keyMethod[keycode]
		 $.isFunction(A) && A(keycode);
	},

	execKeyvalue:function(key){
		var fun = epgkey.keyValueEvent, keyValue = epgkey.keyValue[key];
		$.isFunction(fun) && fun(key, keyValue);
	},
	
	//键值是数字
	alertKeyValue:function(obj) {
		var keyValue = epgkey.keyValue;
		keyValue ? $.extend(keyValue, obj) : (keyValue = obj);
	},
	
	bindKeypress:function(e) {
		//alert(e.which)
		if (!epgkey.onKeyMonitor || epgkey.onKeyRuning)return;
		epgkey.onKeyRuning = true;
		var keycode = e.which, eo = epgkey.keyMethod, ev = epgkey.keyValue;
		if (keycode in eo) {
			epgkey.execKeyEvent(keycode);
			e.stopPropagation && e.stopPropagation();
			e.preventDefault &&  e.preventDefault();
		} else if(keycode in ev) {
			epgkey.execKeyvalue(keycode);
			e.stopPropagation && e.stopPropagation();
			e.preventDefault &&  e.preventDefault();
		}
		epgkey.onKeyRuning = false;
	},
	//兼容监听事件
	Listener:function(e) {
		e = e || event;
		e.which || (e["which"] = e.keyCode);
		epgkey.bindKeypress({which:e.which, 
			preventDefault:function() {
				if (e.preventDefault) {//如果是FF下执行这个
					e.preventDefault();
				}else{ 
					window.event.returnValue = false;//如果是IE下执行这个
				}
			},
			
			stopPropagation:function() {
				if(e.stopPropagation){
					//因此它支持W3C的stopPropation()方法
					e.stopPropagation();
				}
				else{
					//否则,我们得使用IE的方式来取消事件冒泡
					window.event.cancelBubble = true;
				}
			}
		});
	},
	//启动epg
	start:function(dKey) {
		//$(document).keydown(epgkey.bindKeypress);
		epgkey.initkeyevent(dKey);
		
		if ($.browser.msie) {
			document.attachEvent("onkeydown", epgkey.Listener);
		} else {
			document.addEventListener("keydown", epgkey.Listener, false)
		}
	},
	//{UpCode:38, DCode:40, LCode:37, RCode:39, ECode:13}
	initkeyevent:function(dKey) {
		var cacheKey = epgkey.keyMethod;
		for(var key in dKey) {
			epgkey.setKey(dKey[key], epgkey[key]);
			epgkey.keyMethodstr[dKey[key]] = key;
		}
	}
}
//扩展其他的功能
$.extend(epgkey, {
	rows:0,
	column:0,
	containerItems:{},
	currentContainer:null,
	up:1,
	down:2,
	left:3,
	right:4,
	// 根据不同的方位 获取不同的container 返回false 就是说明焦点不离开当前容器
	getNextPositionContainer:function(c, PositionType) {
		
		var x = c.X, y = c.Y, T = epgkey;
		
		switch (PositionType) {
			case T.up:
				y -= 1;
				y = y < 0 ? T.rows : y;
				break;
			case T.down:
				y += 1;
				y = y > T.rows ? 0 : y;
				break;
			case T.left:
				x -= 1;
				if (x < 0) {
					x = T.column;
					y -= 1;
					y = y < 0 ? T.rows : y; 
				}
				break;
			case T.right:
				//fnMessage("right" + this.xrows)
				x += 1;
				if (x > T.column) {
					x = 0 ;
					y += 1;
					y = y > T.rows ? 0 : y;
				}
				break;
		}
		//alert(["x:", x,"y:", y].join(""))
		return T.currentContainer == T.getXYContainer(x, y);
	},
	
	getXYContainer:function(x, y) {
		var tmpstr = x+""+y, T = epgkey;
		//alert(T.containerItems[tmpstr])
		if (T.containerItems[tmpstr]) {
			//fnMessage("焦点移动到容器:" + tmpstr);
			T.setCurrentContainer(T.containerItems[tmpstr]);
		} else {
			x -= 1;
			if (x < 0) {
				x = T.column ;
				y -= 1;
				y = y < 0 ? T.rows : y;
			}
			return T.getXYContainer(x, y);
		}
		return T.containerItems[tmpstr];
	},
	//管理的一行
	row:function() {
		
		//alert(arguments[0] instanceof epgcontainer)
		var L = arguments.length, tmpstr = "",  T = epgkey;
		if (L > 0) {
			T.column = T.column < L ? L - 1  : T.column;
			for(var i = 0; i < L; i++) {
				
				if (arguments[i] instanceof epgcontainer) {
					tmpstr = i+""+T.rows;
					arguments[i]["X"] = i;
					arguments[i]["Y"] = T.rows;
					//console.log("添加container "+ arguments[i].containerdiv+ " : " + tmpstr);
					arguments[i].ContainerManage = T;
					//alert(arguments[i].ContainerManage)
					this.containerItems[tmpstr] = arguments[i];
				}
			}
			
			//this.containerItems.
		}
		L = tmpstr = null;
		return T;
	},
	
	nextRow:function() {
		var T = epgkey;
		T.rows++;
		T.row.apply(T, arguments);
		return T;
	},
	
	setCurrentContainer:function () {
		
		var T = epgkey;
	//	if (!this.onKeyMonitor) return ;
		T.currentContainer && T.currentContainer.cBlur();
		if (arguments.length > 0) {
			//fnMessage("切换焦点x"+arguments[0].X+"y"+arguments[0].Y);
			arguments[0] instanceof epgcontainer && (T.currentContainer = arguments[0]);
			
		} else {
			T.currentContainer = T.containerItems["00"];
		}
		
		//fnMessage("切换焦点x"+this.currentContainer.X+"y"+this.currentContainer.Y);
		T.currentContainer.cFocus();
		
		
	},
	setCurrent:function(s) {
		var T = epgkey;
		T.currentContainer = s;
		T.currentContainer.cFocus();
	},
	setCode:function(code, e) {
		var T = epgkey;
		//var t  = (new Date()).getTime();
		T.currentContainer && T.currentContainer[code](e);
		//var endt = (new Date()).getTime();
		//Dtip.tip("消耗时间为"+(endt - t))
	}
})
var keyMethod = ["UpCode", "DCode", "LCode", "RCode", "ECode"];
$.each(keyMethod, function(i, n) {epgkey[n] = function(e) {epgkey.setCode(n, e);}});

//焦点容器
var epgcontainer  = function(id, rowTag, columnTag) {
	this.Elements = {};
	this.rows = 0;
	this.column = 0;
	//每个焦点的事件 
	this.SAE = {}; //当某个按下确认键时候 
	this.SBE = {}; //.....失去焦点的时候
	this.SFE = {}; //.....得到焦点的时候
	//不区分某个焦点 
	this.AAE = [];
	this.ABE = [];
	this.AFE = [];
	
	//容器焦点事件
	this.BCUPE = []; //当向上离开容器之前触发执行事件
	this.BCDE  = [];
	this.BCLE  = [];
	this.BCRE  = [];
	
	//容器得到焦点时候
	this.CFE   = [];
	this.CBE   = [];
	
	//当前容器的焦点对象 JQuery
	this.current = null;
	this.ContainerManage = null;
	this.containerdiv = null;
	//当光标移到到最右边边光标是否自动往下一行
	this.rightCanDown = true;
	//当光标移到到最左边光标是否自动往上一行
	this.leftCanUp = true;
	//焦点是否在当前容器上
	this.hasFocus = false;
	//容器焦点左边是否保持焦点
	this.containerLeftFocus = false;
	this.containerRightFocus = false;
	this.containerUpFocus = false;
	this.containerDownFocus = false;
	this.autoSetNext = true;
	this.id = id;
	this.rowTag = rowTag;
	this.columnTag = columnTag;
	this.cacheLiveclick = null;
	this.init();
	
}

epgcontainer.fn = epgcontainer.prototype = {
	init:function () {
		var T = this, c = $(this.id), elements = c.find(this.rowTag+" "+this.columnTag);
		
		c.find(this.rowTag).each($.proxy(this.setRow, this));
		T.cacheLiveclick = $.proxy(this.mouseSupport, this);
		elements.css("cursor", "pointer").live("click", T.cacheLiveclick);
		this.containerdiv = c;
		var fcss = c.attr("fcss"), bcss =  c.attr("bcss"), ac =  c.attr("ac"), rep = c.attr("rep");
		tool.isEmptyorundefined(fcss) || this.setAFE(function(e){$.browser.msie ? e["__elm"].setAttribute("className", fcss) : e["__elm"].setAttribute("class", fcss);});
		tool.isEmptyorundefined(bcss) || this.setABE(function(e){$.browser.msie ? e["__elm"].setAttribute("className", bcss) : e["__elm"].setAttribute("class", bcss);});
		tool.isEmptyorundefined(ac) || this.setAAE(function(e){ var fun = eval(ac); fun.call(this, e)});
		tool.isEmptyorundefined(rep) || this.setrep(rep);
		
		
	},
	
	//支持鼠标
	mouseSupport:function(e) {
		var htmlElement = e.target;
		
		while(htmlElement) {
			if (htmlElement.nodeType == 1) {
				if (htmlElement.tagName.toLowerCase() == this.columnTag.toLowerCase()) {
					
					break;
				}
				htmlElement = htmlElement.parentNode;
			}
		}
		
		var current = $(htmlElement), x = current.attr("x"), y = current.attr("y");
		this.setNoAction(x, y);
		epgkey.setCurrentContainer(this);
		this.ECode(this.current);
	},
	
	//重新渲染，但是不重新设置事件
	ReRender:function() {
		this.Elements = {};
		this.current = null;
		this.rows = 0;
		this.column = 0;
		this.containerdiv.find(this.rowTag).each($.proxy(this.setRow, this));
	},
	//交替样式
	setrep:function(rep) {
		var L = rep.length;
		this.setAFE(function(e){
				
				var  __className = e.attr("class");
				e["__fcss"] = __className.slice(-L) != rep ? [__className, rep].join("") : __className;
			
				$.browser.msie ? e["__elm"].setAttribute("className", e["__fcss"]) : e["__elm"].setAttribute("class", e["__fcss"])
			//e.removeClass().addClass(e["__fcss"]);
			// e.attr("class", __className.slice(-L) != rep ? [__className, rep].join("") : __className);
		});
		
		this.setABE(function(e){ 
			
			var  __className = e.attr("class");
			e["__bcss"] = __className.slice(-L) != rep ? __className : __className.substr(0, __className.length -L);
				
			$.browser.msie ? e["__elm"].setAttribute("className", e["__bcss"]) : e["__elm"].setAttribute("class", e["__bcss"])
			
			 //e.attr("class", __className.slice(-L) != rep ? __className : __className.substr(0, __className.length -L));
		});
	},

	setRow:function(i, o) {
		this.rows = i;
		var dt = $(o).find(this.columnTag);
		
		this.column = this.column < dt.length ? dt.length - 1 : this.column;
		/*if (this.column == 3) {
			document.title = "异常出来了"+"再次寻找dom:"+$(o).find(this.columnTag).length;
			
		}*/
		dt.each($.proxy(this.setColumn, this));
		
	},
	
	setColumn:function(i, o) {
		var elem = $(o);
		elem._x = i;
		elem._y = this.rows;
		this.Elements[i+""+this.rows] = elem;
		elem["__elm"] = elem.get(0);
		//debug调试用
		elem.attr({"x":i, "y":this.rows});
		
	},
	
	execEvent:function(events, args) {
			
			for(var i in events) {
				var fun = events[i];
				$.isFunction(fun) && fun.apply(this, args ? args : []);
			}
	},
	
	cBlur:function(e) {
		if (this.current) {
			this.blur(this.current);
		} 
		this.execEvent(this.CBE);
		this.hasFocus = false;
	},
	
	cFocus:function(e) {
		
		if (this.current) {
			this.focus(this.current);
		} else {
			
			this.set(null, null);
		}
		
		this.execEvent(this.CFE);
		this.hasFocus = true;
	},
	
	blur:function(e) {
		this.execEvent(this.ABE, [e]);
		this.SBE[e._x+""+e._y] && this.execEvent(this.SBE[e._x+""+e._y], [e]);
		this.execElemAttr("be", e);
		//e.trigger('blur');
	},
	
	focus:function(e) {
		
		//try{
		this.current = e;
		this.execEvent(this.AFE, [e]);
		this.SFE[e._x+""+e._y] && this.execEvent(this.SFE[e._x+""+e._y], [e]);
		this.execElemAttr("fe", e);
		/*} catch(e) {
			alert(this.containerdiv.attr("id"))
		}*/
		//e.trigger('focus');
	},
	
	execElemAttr:function(attr, e) {
		e["__ece"+attr] || (e["__ece"+attr] = e.attr(attr));
		var a = e["__ece"+attr];
		if (!tool.isEmptyorundefined(a)) {
			var fun = eval(a);
			fun.call(this, e)
		}
	},
	
	execEval:function(attr, e) {
		e["__exe"+attr] || (e["__exe"+attr] = e.attr(attr));
		var a = e["__exe"+attr];
		if (!tool.isEmptyorundefined(a)) {
			eval(a);
		}
	},
	
	setNoAction:function(x, y) {
		if (this.current) {
			this.blur(this.current);
		}
		this.current = this.Elements[x+""+y]
	},
	
	set:function(x, y) {
		x = x ? x : 0;
		y = y ? y : 0;
		
		this.current && this.blur(this.current);
		var elem = this.Elements[x+""+y];
		elem && this.focus(this.Elements[x+""+y]);
		
		tmpstr = elem = null;
	},
	
	
	getx:function(){
		return this.current ? this.current._x : 0;
	},
	
	gety:function(){
		return this.current ? this.current._y : 0;
	},
	
	//当焦点要向上离开容器的时候执行 如果是离开就返回 false 否则就返回 true 焦点还在当前容器上 
	UpLeaveEvent:function(){
		//alert("UpLeaveEvent");
		return this.exePositionEvent(this.containerUpFocus, epgkey.up, this.BCUPE);
	},
	
	DownLeaveEvent:function() {
		//alert("DownLeaveEvent");
		return this.exePositionEvent(this.containerDownFocus, epgkey.down, this.BCDE);
	},
	
	LeftLeavetEvent:function() {
		//alert("LeftLeavetEvent");
		return this.exePositionEvent(this.containerLeftFocus, epgkey.left, this.BCLE);
	},
	
	RightLeaveEvent:function() {
		//alert("RightLeaveEvent");
		return this.exePositionEvent(this.containerRightFocus, epgkey.right, this.BCRE);
	},
	//检测当前焦点是否保存在当前的容器上面 返回true的话就保持焦点
	exePositionEvent:function(canFocus, PositionType, BeforePositionTypeEvent) {
		BeforePositionTypeEvent.length && this.execEvent(BeforePositionTypeEvent);
		if (this.autoSetNext && !canFocus) {
			if (epgkey.onKeyMonitor && this.ContainerManage) {
				return this.ContainerManage.getNextPositionContainer(this, PositionType);
			}
		}
		return canFocus;
	}
}
var SElemeEvent = ["SAE", "SBE", "SFE"];
$.each(SElemeEvent, function(i, e) {
							 	epgcontainer.fn["set" + e] = function(x, y, fun) {
									var selem = this[e], el = x+""+y;
									$.isArray(selem[el]) || (selem[el] = []);
									selem[el].push(fun);
								}
							 })
var AElemeEvent = ["AAE", "ABE", "AFE", "CFE", "CBE", "BCUPE", "BCDE", "BCLE", "BCRE"];
$.each(AElemeEvent, function(i, e) {epgcontainer.fn["set" + e] = function(fun) {this[e].push(fun);} });
$.extend(epgcontainer.fn, {
		setY:function(x, y) {
		var tmpstr = x+""+y;
		if (this.Elements[tmpstr]) {
			this.set(x, y);
		} else {
			x -= 1;
			if (x < 0) {
				x = this.column ;
				y -= 1;
				y = y < 0 ? this.rows : y;
			}
			this.setY(x, y);
		}
		tmpstr = null;
	},
	setX:function(x, y) {
		
	},
	UpCode: function(e) {
		var x = this.getx(), y = this.gety() - 1;
		//判断是否需要把焦点移动到容器外面,否则继续执行 焦点循环
		if (y < 0 && !this.UpLeaveEvent()) {
			return ;
		}
		
		y = y < 0 ? this.rows : y;
		this.setY(x, y);
		x = y = null;
	},
	
	DCode: function(e) {
		var x = this.getx(), y = this.gety() + 1;
		
		if (y > this.rows && !this.DownLeaveEvent()) {
			//如果焦点需要移开到别的焦点上 则直接返回
			return ;
		}
		
		y = y > this.rows  ? 0 : y;
		this.setY(x, y);
		x = y = null;
	},
	
	
	LCode: function(e) {
		var x = this.getx() -1, y = this.gety();
		if (x < 0 && (!this.leftCanUp || y == 0) && !this.LeftLeavetEvent()) {
			return;
		}
		
		if (x < 0) {
			x = this.column ;
			y -= 1;
			y = y < 0 ? this.rows : y;
		}
		
		this.setY(x, y);
		x = y = null;
	},
	
	RCode: function(e) {
		var x = this.getx() + 1, y = this.gety();
		var tmpstr = x+""+y;
		if ((x >  this.column || !this.Elements[tmpstr]) && (!this.rightCanDown || y == this.rows) && !this.RightLeaveEvent()) {
			return;
		}
		if (x >  this.column || !this.Elements[tmpstr]) {
			
			x = 0 ;
			y += 1;
			y = y > this.rows ? 0 : y;
		} 
		
		this.setY(x, y);
		tmpstr = x = y = null;
	},
	
	ECode: function(e) {
		if (this.current) {
			var x = this.getx() , y = this.gety();
			this.execElemAttr("ac", this.current);
			this.execEval("oc", this.current)
			var tmpstr = x+""+y;
			//alert("cL:  "+this.containerdiv.attr("id")+"  set:")
			this.SAE[tmpstr] && this.execEvent(this.SAE[tmpstr], [this.current]);
			
			this.execEvent(this.AAE, [this.current]);
			ac = null;
		}
		
	}
})
//扩展epgcontainer
var epgext = {
		horizontal:1,
		vertical:0,
		sidebar: function(s, selectKey, selectKeyClass, tag) {
			
			//找出当前属于的频道
			s.selectKey = null;
			s.selectKeyClass = selectKeyClass;
			$.each(s.Elements, function(name, elem) { elem.attr(tag) == selectKey && (s.selectKey = elem);});
			var cssp = s.containerdiv.attr("bcss")
			s.blurClass = tool.isEmptyorundefined(cssp) ? s.containerdiv.attr("scss") : cssp;
			s.setCBE(function(e) {this.selectKey && this.selectKey.attr("class", this.selectKeyClass);});
			s.setCFE(function(e) {this.selectKey && (this.current != this.selectKey) && this.selectKey.attr("class", this.blurClass);}); //end setFocusEvent
			return s;
		},
		/**
		*静态翻页容器
		*@param c  容器
		*@param type 翻页类型竖翻或者横向翻页
		*@param num 相隔几个焦点翻页一次
		*@param pos 翻译像素
		*param e 回调函数
		*/
		staticPageContainer : function(c, type, num, pos, Property, f) {
			
			c.setAFE(function(e){
				var n = parseInt(e.attr(Property), 10);
				switch(type) {
					case epgext.vertical:
						 this.containerdiv.scrollTop(Math.floor(n/num) * pos);
						 
					break;
					case this.horizontal:
						epgext.containerdiv.scrollLeft(Math.floor(n/num) * pos);
					break;
			   }
			   f && f.apply(this, [e]);
			   n = null;
			   return true;
		   });
			return c;
		},
		
		
		pageContainer:function(c, type, f) {
			switch (type) {
				case epgext.horizontal:
					
					c.setBCLE(function(){f("l");});
					c.setBCRE(function(){f("r");});
					
					break;
				case epgext.vertical:
					c.setBCUPE(function(){f("u")});
					c.setBCDE(function(){f("d")});
					break;
			}
			return c;
		}
		
}


var cookie = {
		//写cookies
		setCookie:function(name, value)
		{
				var Days = 30;
				var exp = new Date();
				exp.setTime(exp.getTime() + Days*24*60*60*1000);
				document.cookie = name + "="+ escape (value) + ";expires=" + exp.toGMTString();
		},
		
		//读取cookies
		getCookie:function(name)
		{
			var arr,reg=new RegExp("(^| )"+name+"=([^;]*)(;|$)");
			if(arr=document.cookie.match(reg)) 
				return unescape(arr[2]);
			else 
				return null;
		},
		
		//删除cookies
		delCookie:function(name)
		{
			var exp = new Date();
			exp.setTime(exp.getTime() - 1);
			var cval= cookie.getCookie(name);
			if(cval!=null) document.cookie= name + "="+cval+";expires="+exp.toGMTString();
		},
		
	//	//使用示例
//		setCookie("name","hayden");
//		alert(getCookie("name"));
		 
		//如果需要设定自定义过期时间
		//那么把上面的setCookie　函数换成下面两个函数就ok;
		 
		//程序代码
		setCookieAndTime:function(name,value,time){
			var strsec = cookie.getsec(time);
			var exp = new Date();
			exp.setTime(exp.getTime() + strsec*1);
			document.cookie = name + "="+ escape (value) + ";expires=" + exp.toGMTString();
		},
		
		getsec:function(str){
		    var str1=str.substring(1,str.length)*1;
		    var str2=str.substring(0,1);
		    if (str2=="s"){
		    return str1*1000;
		     }else if (str2=="h"){
		    return str1*60*60*1000;
		     }else if (str2=="d"){
		    return str1*24*60*60*1000;
		     }
		}
//这是有设定过期时间的使用示例：
//s20是代表20秒
//h是指小时，如12小时则是：h12
//d是天数，30天则：d30
//暂时只写了这三种，不知道谁有更好的方法，呵呵
//setCookie("name","hayden","s20");
}


//默认实现的键盘操作
var deafautkey = {UpCode:38, DCode:40, LCode:37, RCode:39, ECode:13};
//开始在页面画焦点 deafautkey:{UpCode:38, DCode:40, LCode:37, RCode:39, ECode:13}
var epgKeyManage = {
	//需要渲染的数组 [[渲染的id,渲染的id],[下一行id] ...]
	KeyE:{},
	focusKeyE:null,
	Render:function(elemArray) {
		epgkey.start(deafautkey);
		var Imgcontainer = [];
		for(var i = 0, L = elemArray.length; i < L ; i++) {
			var ElemA = elemArray[i], AR = [];
			for(j = 0, k = ElemA.length; j < k; j++) {
				
				var id = ElemA[j], 
				el = $(id), 
				columnTag = el.attr("cloumnTag"), 
				rowTag = el.attr("rowTag"), 
				autoimg = el.attr("autoimg"), 
				setfocus = el.attr("setfocus"), 
				type = el.attr("type");
				
				if (tool.isEmptyorundefined(epgKeyManage.KeyE[id])) {
					//alert([id, rowTag, columnTag])
					epgKeyManage.KeyE[id] = new epgcontainer(id, rowTag, columnTag);
					
					AR.push(epgKeyManage.KeyE[id]);
					//alert("AR:"+AR.length+":"+(epgKeyManage.KeyE[id] instanceof epgcontainer))
					if (!tool.isUndefined(autoimg)) {
						Imgcontainer.push(epgKeyManage.KeyE[id]);
						
					}
					
					if (!tool.isUndefined(setfocus)) {
						epgKeyManage.focusKeyE = epgKeyManage.KeyE[id];
					}
					
				}
				
			
			}
			
			if (AR.length) {
				if (i === 0) {//如果为0使用row;
					epgkey.row.apply(epgkey, AR);
				} else {
					epgkey.nextRow.apply(epgkey, AR);
				}
			}
			
		}
		
		epgkey.setCurrentContainer(epgKeyManage.focusKeyE);
		$.each(Imgcontainer, function(i, o) {o.containerdiv.find("img").lazyload();})

	},
	//不自动解析焦点的container
	RenderNoAuto:function(CAR) {
		epgkey.start(deafautkey);
		var Imgcontainer = [];
		if ($.isArray(CAR)) {
			for(var i = 0, L = CAR.length; i < L; i++) {
				var id = CAR[i], 
				el = $(id), 
				columnTag = el.attr("cloumnTag"), 
				rowTag = el.attr("rowTag"), 
				autoimg = el.attr("autoimg"), 
				setfocus = el.attr("setfocus"), 
				type = el.attr("type");
				if (tool.isEmptyorundefined(epgKeyManage.KeyE[id])) {
					//alert([id, rowTag, columnTag].join(","))
					epgKeyManage.KeyE[id] = new epgcontainer(id, rowTag, columnTag);
					
					
					//alert("AR:"+AR.length+":"+(epgKeyManage.KeyE[id] instanceof epgcontainer))
					if (!tool.isUndefined(autoimg)) {
						Imgcontainer.push(epgKeyManage.KeyE[id]);
						
					}
					
					if (!tool.isUndefined(setfocus)) {
						epgKeyManage.focusKeyE = epgKeyManage.KeyE[id];
					}
					epgKeyManage.setStrategy(epgKeyManage.KeyE[id]);
				}
			}
			epgkey.setCurrentContainer(epgKeyManage.focusKeyE);
			$.each(Imgcontainer, function(i, o) {o.containerdiv.find("img").lazyload();})
		}
	},
	//设置焦点容器中的移动策略
	//{container:{SL:name, L:name, SR:name, R:name, D:name, U:name}}
	setStrategy:function(container) {
		container.autoSetNext = false;
		var c = container.containerdiv,
		SL = c.attr("SL"), 
		L = c.attr("L"), 
		SR = c.attr("SR"), 
		R = c.attr("R"), 
		D = c.attr("D"), 
		U = c.attr("U"); 
		if (!tool.isEmptyorundefined(SL)) {
			container.leftCanUp = false
			container.setBCLE(new Function("epgkey.setCurrentContainer(epgKeyManage.KeyE['"+SL+"']);"));
		}
		if (!tool.isEmptyorundefined(L)) {
			container.setBCLE(new Function("epgkey.setCurrentContainer(epgKeyManage.KeyE['"+L+"']);"));
		}
		
		if (!tool.isEmptyorundefined(SR)) {
			container.rightCanDown = false
			container.setBCRE(new Function("epgkey.setCurrentContainer(epgKeyManage.KeyE['"+SR+"']);"));
		}
		if (!tool.isEmptyorundefined(R)) {
			container.setBCRE(new Function("epgkey.setCurrentContainer(epgKeyManage.KeyE['"+R+"']);"));
		}
		if (!tool.isEmptyorundefined(U)) {
			container.setBCUPE(new Function("epgkey.setCurrentContainer(epgKeyManage.KeyE['"+U+"']);"));
		}
		if (!tool.isEmptyorundefined(D)) {
			container.setBCDE(new Function("epgkey.setCurrentContainer(epgKeyManage.KeyE['"+D+"']);"));
		}
		
	}
}
//操作xml类
var xPath = {
	getDom:function() {
		var domList = [             
            "MSXML4.DOMDocument",   
            "MSXML3.DOMDocument",   
            "MSXML2.DOMDocument",   
            "MSXML.DOMDocument",   
            "Microsoft.XmlDom" 
			], L = domList.length;     
		for(i=0; i< L; i++){   
			try{   
				var o = new ActiveXObject(domList[i]);   
				return o;   
			}catch(e){}   
		}   
        return false;   
	},
	
	parseXML: function(data) {
		  if (typeof DOMParser !== "undefined") {   
            var domParser = new DOMParser();   
            var o = domParser.parseFromString(data, 'text/xml');      
            return o.documentElement; 
		  } else {
			   var o = this.getDom();   
                 o.loadXML(data);
				 o.async=false; 
                return o.documentElement;   
		  }
        
	},
	
	loadXML: function(data) {
		var xmlDoc=null;   
        if (window.ActiveXObject){   
             xmlDoc=this.getDom();   
         }else if (document.implementation && document.implementation.createDocument){   
             xmlDoc=document.implementation.createDocument("","",null);   
         }else{   
             alert('Your browser cannot handle this script');   
         }   
         xmlDoc.async=false;   
         xmlDoc.load(path);   
        return xmlDoc;   
	},
	
	SelectSingleNode:function (xmlDoc, elementPath) {
		 if(window.ActiveXObject)
		{
			return xmlDoc.selectSingleNode(elementPath);
		}
		else
		{
			var xpe = new XPathEvaluator();
			var nsResolver = xpe.createNSResolver( xmlDoc.ownerDocument == null ? xmlDoc.documentElement : xmlDoc.ownerDocument.documentElement);
			var results = xpe.evaluate(elementPath,xmlDoc,nsResolver, XPathResult.FIRST_ORDERED_NODE_TYPE, null);
			return results.singleNodeValue; 
		}
	},
	
	selectNodes:function(xmlDoc, elementPath) {
		if(window.ActiveXObject)
		{
			return xmlDoc.selectNodes(elementPath);
		}
		else
		{
			var xpe = new XPathEvaluator();
			var nsResolver = xpe.createNSResolver( xmlDoc.ownerDocument == null ? xmlDoc.documentElement : xmlDoc.ownerDocument.documentElement);
			var result = xpe.evaluate(elementPath, xmlDoc, nsResolver, 0, null);
			var found = [];
			var res;
			while  (res = result.iterateNext())
				found.push(res);
			return found;
		}
	}
}