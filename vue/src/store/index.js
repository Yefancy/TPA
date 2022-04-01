import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

export default new Vuex.Store({
    state: {
        token: sessionStorage.getItem("token"),
        fileName: '',
        headers: [],
        sensitive: [],
        mapSchema: [],
        treeRoot: null,
        schema: [],
        sensitives: [],
        filters: [],
        currentIndex: -1,
        history: [],
        operators: [],
        selectedRow: new Set(),
    },
    mutations: {
        pushTData(state, tData) {
            state.history.splice(state.currentIndex + 1, state.history.length - state.currentIndex - 1)
            state.history.push(tData)
            state.currentIndex++;
            state.selectedRow.clear()
        },
        setToken(state, token){
            state.token = token
            sessionStorage.setItem("token", token)
        },
        setObj(state, newObj){
            state[newObj.type] = newObj.value
        },
        selectedAdd(state, obj){
            if (Array.isArray(obj)) {
                obj.forEach(_obj=>state.selectedRow.add(_obj))
            } else {
                state.selectedRow.add(obj)
            }
            state.selectedRow = new Set(state.selectedRow)
        },
        selectedRemove(state, obj){
            if (Array.isArray(obj)) {
                obj.forEach(_obj=>state.selectedRow.delete(_obj))
            } else {
                state.selectedRow.delete(obj)
            }
            state.selectedRow = new Set(state.selectedRow)
        },
    },
    getters:{
        headersName(state){
            return state.headers.map(h=>h.text)
        },
        tData(state){
            return state.history[state.currentIndex] ? state.history[state.currentIndex].tData : undefined
        }
    },
})
