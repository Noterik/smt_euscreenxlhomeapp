/**
 * @license almond 0.2.9 Copyright (c) 2011-2014, The Dojo Foundation All Rights Reserved.
 * Available via the MIT or new BSD license.
 * see: http://github.com/jrburke/almond for details
 */

(function(){var e,t,n;(function(r){function v(e,t){return h.call(e,t)}function m(e,t){var n,r,i,s,o,u,a,f,c,h,p,v=t&&t.split("/"),m=l.map,g=m&&m["*"]||{};if(e&&e.charAt(0)===".")if(t){v=v.slice(0,v.length-1),e=e.split("/"),o=e.length-1,l.nodeIdCompat&&d.test(e[o])&&(e[o]=e[o].replace(d,"")),e=v.concat(e);for(c=0;c<e.length;c+=1){p=e[c];if(p===".")e.splice(c,1),c-=1;else if(p===".."){if(c===1&&(e[2]===".."||e[0]===".."))break;c>0&&(e.splice(c-1,2),c-=2)}}e=e.join("/")}else e.indexOf("./")===0&&(e=e.substring(2));if((v||g)&&m){n=e.split("/");for(c=n.length;c>0;c-=1){r=n.slice(0,c).join("/");if(v)for(h=v.length;h>0;h-=1){i=m[v.slice(0,h).join("/")];if(i){i=i[r];if(i){s=i,u=c;break}}}if(s)break;!a&&g&&g[r]&&(a=g[r],f=c)}!s&&a&&(s=a,u=f),s&&(n.splice(0,u,s),e=n.join("/"))}return e}function g(e,t){return function(){return s.apply(r,p.call(arguments,0).concat([e,t]))}}function y(e){return function(t){return m(t,e)}}function b(e){return function(t){a[e]=t}}function w(e){if(v(f,e)){var t=f[e];delete f[e],c[e]=!0,i.apply(r,t)}if(!v(a,e)&&!v(c,e))throw new Error("No "+e);return a[e]}function E(e){var t,n=e?e.indexOf("!"):-1;return n>-1&&(t=e.substring(0,n),e=e.substring(n+1,e.length)),[t,e]}function S(e){return function(){return l&&l.config&&l.config[e]||{}}}var i,s,o,u,a={},f={},l={},c={},h=Object.prototype.hasOwnProperty,p=[].slice,d=/\.js$/;o=function(e,t){var n,r=E(e),i=r[0];return e=r[1],i&&(i=m(i,t),n=w(i)),i?n&&n.normalize?e=n.normalize(e,y(t)):e=m(e,t):(e=m(e,t),r=E(e),i=r[0],e=r[1],i&&(n=w(i))),{f:i?i+"!"+e:e,n:e,pr:i,p:n}},u={require:function(e){return g(e)},exports:function(e){var t=a[e];return typeof t!="undefined"?t:a[e]={}},module:function(e){return{id:e,uri:"",exports:a[e],config:S(e)}}},i=function(e,t,n,i){var s,l,h,p,d,m=[],y=typeof n,E;i=i||e;if(y==="undefined"||y==="function"){t=!t.length&&n.length?["require","exports","module"]:t;for(d=0;d<t.length;d+=1){p=o(t[d],i),l=p.f;if(l==="require")m[d]=u.require(e);else if(l==="exports")m[d]=u.exports(e),E=!0;else if(l==="module")s=m[d]=u.module(e);else if(v(a,l)||v(f,l)||v(c,l))m[d]=w(l);else{if(!p.p)throw new Error(e+" missing "+l);p.p.load(p.n,g(i,!0),b(l),{}),m[d]=a[l]}}h=n?n.apply(a[e],m):undefined;if(e)if(s&&s.exports!==r&&s.exports!==a[e])a[e]=s.exports;else if(h!==r||!E)a[e]=h}else e&&(a[e]=n)},e=t=s=function(e,t,n,a,f){if(typeof e=="string")return u[e]?u[e](t):w(o(e,t).f);if(!e.splice){l=e,l.deps&&s(l.deps,l.callback);if(!t)return;t.splice?(e=t,t=n,n=null):e=r}return t=t||function(){},typeof n=="function"&&(n=a,a=f),a?i(r,e,t,n):setTimeout(function(){i(r,e,t,n)},4),s},s.config=function(e){return s(e)},e._defined=a,n=function(e,t,n){t.splice||(n=t,t=[]),!v(a,e)&&!v(f,e)&&(f[e]=[e,t,n])},n.amd={jQuery:!0}})(),n("../vendor/almond",function(){}),n("layout-item",[],function(){var e=function(){};e.prototype.element=null,e.prototype.htmlClass="ntk-layout-item",e.prototype.create=function(){this.element.addClass(this.htmlClass)},e.prototype.destroy=function(){this.element.removeClass(this.htmlClass)}}),n("layout",["./layout-item"],function(e){var t=function(e){var t=this;jQuery.extend(this,e),jQuery(window).on("resize",function(){t.afterResize.call(t)}),this.beforeCreateCallbacks=[],this.afterCreateCallbacks=[],this.afterResizeCallbacks=[],this.destroyCallbacks=[];for(var n=0;n<this.mixins.length;n++){var r=this.mixins[n];r.call(this)}this.create()};return t.prototype.element=null,t.prototype.htmlClass="ntk-layout",t.prototype.noLayout=null,t.prototype.setElement=function(e){e instanceof jQuery||(e=$(e)),this.element=e},t.prototype.afterResize=function(){for(var e=0;e<this.afterResizeCallbacks.length;e++){var t=this.afterResizeCallbacks[e];t.call(this)}},t.prototype.beforeCreate=function(){console.log("Layout.prototype.beforeCreate()"),console.log(this);for(var e=0;e<this.beforeCreateCallbacks.length;e++){var t=this.beforeCreateCallbacks[e];t.call(this)}},t.prototype.afterCreate=function(){for(var e=0;e<this.afterCreateCallbacks.length;e++){var t=this.afterCreateCallbacks[e];t.call(this)}},t.prototype.create=function(){this.beforeCreate(),this.element.data("layout",this),this.afterCreate()},t.prototype.destroy=function(){var e=this;for(var t=0;t<this.destroyCallbacks.length;t++){var n=this.destroyCallbacks[t];n.call(e)}},t.prototype.itemClass=e,t}),n("mix-ins/scroller",[],function(){var e=function(e){var t=this;return this.boundaryBottom=50,this.bottomReachedTriggered=!1,this.beforeCreateCallbacks.push(function(){$(window).on("scroll",function(){t.scrollingCallback.apply(t,arguments)})}),this.destroyCallbacks.push(function(){$(window).off("scroll")}),this};return e}),n("mix-ins/squared",[],function(){var e=function(e){var t=this,n,r=0,i=function(){t.noLayout?n=t.element.children().not(t.noLayout):n=t.element.children()},s=function(){return i(),n.not(".positioned")};this.reset=function(){n.removeClass(".positioned"),offset=0},this.createGrid=function(e){var n=[],r=0;while(r<e){var i=[],s=t.columns,o=0,u=0,a=2;while(s>0){var f;if(s>=2&&a==2)f=Math.round(Math.random())+1;else if(s==1||a==1)f=1;i.push(f),f==1&&u==0?(u=1,a=1):(u=0,s-=f,a=2,o+=f),o+1>t.columns&&(r++,s=0,o=0)}n.push(i)}return n};var o=function(){var e=s(),n=t.columns,i=2;e.each(function(){var e=$(this),s;if(n>=2&&i==2)s=Math.round(Math.random())+1;else if(n==1||i==1)s=1;e.data("column",column),e.data("row",r),e.data("subrow",subrow),e.data("weight",s),s==1&&subrow==0?(subrow=1,i=1):s==2&&(subrow=0,n-=s,i=2,column+=s),column+1>t.columns&&(r++,n=t.columns,column=0),e.addClass("positioned")})},u=function(e){var t=s(),n=0,i,o,u;for(i=0;i<e.length;i++){o=0,u=0;var a=e[i];for(var f=0;f<a.length;f++){var l=a[f],c=jQuery(t.get(n));c.data("row",r),c.data("column",o),c.data("subrow",u),c.data("weight",l),l==1&&u==0?u++:(o+=l,u=0),n++}r++}};this.create=function(e){e?u(e):o(),a()};var a=function(){i();var e=t.element.width();if(e>0){var r=n.last().data("row")+1,s=t.gutter,o=Math.round((e-s*(t.columns-1))/t.columns),u=Math.ceil(o/t.aspectRatio),a=0,f=0,l=t.columns*o+(t.columns-1)*s;t.element.width(l);var c=0;n.each(function(){var e=$(this),t=e.data("weight"),n=e.data("row"),r=e.data("column"),i=e.data("subrow"),l=t*o+(t-1)*s,c=t*u+(t-1)*s,h=r*o+r*s,p=n*2+i;a=p+(t-1),a>f&&(f=a);var d=p*u+p*s;e.css("width",l),e.css("height",c),e.css("left",h),e.css("top",d)}),f++;var h=f*u+f*s;t.element.height(h)}};return this.afterResizeCallbacks.push(a),this.beforeCreateCallbacks.push(this.apply),this};return e}),n("squared-layout",["./layout","./mix-ins/scroller","./mix-ins/squared"],function(e,t,n){var r=function(r){this.mixins=[t,n],e.apply(this,arguments),this.element.data("layout",this),this.columns=this.element.data("ntk-columns")?this.element.data("ntk-columns"):this.columns,this.aspectRatio=this.element.data("ntk-aspect-ratio")?this.element.data("ntk-aspect-ratio"):this.aspectRatio,this.gutter=this.element.data("ntk-gutter")?this.element.data("ntk-gutter"):this.gutter};return r.prototype=Object.create(e.prototype),r.prototype.htmlClass="ntk-layout-squared",r.prototype.gutter=5,r.prototype.columns=4,r.prototype.aspectRatio=4/3,r.prototype.scrollingCallback=function(){console.log("SquaredLayout.scrollingCallback()")},r.prototype.create=function(){e.prototype.create.apply(this)},r}),e.config({appDir:"."}),e(["squared-layout"],function(e){window.noterik||(window.noterik={}),window.noterik.layout||(noterik.layout={}),noterik.layout.squared=e}),n("main",function(){}),t(["main"])})();