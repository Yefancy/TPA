import Vue from 'vue'
import VueRouter from 'vue-router'
import UploadFile from "@/components/Page1/UploadFile";
import SheetPage from "@/components/Sheet/SheetPage";
import UtilityAnalysis from "@/components/Utility/UtilityAnalysis";

Vue.use(VueRouter)

export default new VueRouter({
    routes: [
        { path: '/', redirect: '/upload' },
        { path: '/sheet', name:'sheet', component: SheetPage},
        { path: '/upload', name:'upload', component: UploadFile },
        { path: '/utility', name:'utility', component: UtilityAnalysis },
    ],
})
