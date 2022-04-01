import Vue from 'vue'
import axios from 'axios'
import VueAxios from 'vue-axios'

Vue.use(VueAxios, axios)

function config(){
    axios.defaults.baseURL='http://localhost:8989'
    axios.defaults.withCredentials=true;
    axios.interceptors.request.use(config=>{
        let token = window.sessionStorage.getItem("token");
        if (token) {
            config.headers['Access-Token'] = window.sessionStorage.getItem("token")
        }
        return config
    });
    axios.interceptors.response.use(res=>{
        console.log(res);
        return res;
    })
}

export {config}