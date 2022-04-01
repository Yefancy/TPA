import Vue from 'vue'
import App from './App.vue'
import * as d3 from 'd3'
import $ from 'jquery'
import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap/dist/js/bootstrap.min.js';
import {BootstrapVue, IconsPlugin} from 'bootstrap-vue';
import store from "@/store";
import router from "@/router";
import vuetify from '@/plugins/vuetify' // path to vuetify export
import * as axios from "@/plugins/axios";
import global from '@/plugins/global'

Vue.use(global)
Vue.use(BootstrapVue)
Vue.use(IconsPlugin)
Vue.config.productionTip = false
axios.config()

Set.prototype.equal = function (obj){
  let set = new Set(obj)
  if(set.size !== this.size) return false
  for (let v of set) { // 遍历Set
    if(!this.has(v)) return false
  }
  return true;
}

Array.prototype.equal = function (obj){
  return new Set(this).equal(obj)
}

Array.prototype.remove = function (obj){
  return this.splice(this.indexOf(obj), 1)
}

window.deepCopy = function(o) {
  if (typeof o === 'string' || typeof o === 'number' || typeof o === 'boolean' || typeof o === 'undefined') {
    return o
  } else if (Array.isArray(o)) { // 如果是数组，则定义一个新数组，完成复制后返回
    // 注意，这里判断数组不能用typeof，因为typeof Array 返回的是object
    return [...o]
  } else if (typeof o === 'object') {
    const _o = {};
    for (let key in o) {
      _o[key] = window.deepCopy(o[key])
    }
    return _o
  }
}

window.vue = new Vue({
  render: h => h(App),
  store,
  router,
  vuetify,
})
window.vue.$mount('#app')
window.d3 = d3
window.$ = $