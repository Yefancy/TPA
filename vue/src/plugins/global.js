import Vue from 'vue'
export default {
    install () {
        Vue.prototype.$global = {
            mapType: ['Number', 'Categorical'],
            dragged: null,
        }
    }
}