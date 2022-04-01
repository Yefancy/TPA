<template>
  <div class="d-flex">
    <div class="col p-0">
      <v-card elevation="4" shaped tile class="p-lg-2 m-2" style="width: 400px" :disabled="loading">
        <v-card-title>Upload Sheet</v-card-title>
        <div>
          <v-file-input @change="onAddFiles" counter accept=".csv" truncate-length="15" class="w-75 d-lg-inline-flex m-2"></v-file-input>
        </div>
      </v-card>
      <v-card v-if="loaded" elevation="4" shaped tile class="p-lg-2 m-2" style="width: 400px">
        <v-select :items="[0,1]" v-for="(header, index) in this.headers" :key="index" :label="header.text" v-model="header.type">
          <template v-slot:selection="{item}">
            <v-icon v-if="item===1">mdi-ab-testing</v-icon>
            <v-icon v-else>mdi-numeric</v-icon>
            <span class="ml-1">{{mapType[item]}}</span>
          </template>
          <template v-slot:item="{item}">
            {{mapType[item]}}
          </template>
        </v-select>
        <v-card-actions>
          <v-btn outlined rounded text @click="submit">Submit</v-btn>
        </v-card-actions>
      </v-card>
    </div>

    <v-card elevation="4" shaped tile :loading="loading" class="p-lg-2 m-2 w-100" style="height: fit-content">
      <v-card-title>{{this.fileName}}</v-card-title>
      <v-data-table :headers="this.headers" :items="this.tData" :items-per-page="15" class="elevation-1" dense></v-data-table>
    </v-card>
  </div>
</template>

<script>
import xlsx from 'xlsx'
import {mapState, mapMutations, mapGetters} from 'vuex'
export default {
  name: "UploadFile",
  data(){
    return{
      file: null,
      loading: false,
    }
  },
  computed: {
    mapType(){
      return this.$global.mapType
    },
    loaded(){
      return this.headers.length > 0
    },
    ...mapGetters([
        'headersName',
        'tData',
    ]),
    ...mapState([
        'headers',
        'fileName',
    ]),
  },
  methods: {
    submit(){
      let headers = []
      this.headers.forEach( h=> (h.type === 0 || h.type === 1) && headers.push({
          name:h.value,
          type:h.type
        }))
      let data = {
        headers,
        desserts: this.tData,
      }
      if(this.headers.length > 0) {
        let _this = this;
        this.$http.post('uploadExcel', data).then(res=>{
          if(res.data.code === 200) {
            let mapSchema = res.data.data.mapSchema;
            _this.setToken(res.data.data.token);
            _this.setObj({type:'mapSchema', value:mapSchema})
            let filter = []
            let index = 0;
            for (let map of mapSchema) {
              let type = _this.headers[index++].type
              if (type === 1) {
                filter.push([...map.y])
              } else if (type === 0) {
                filter.push([[map.y[0].min, map.y[0].max]])
              }
            }
            _this.setObj({type:'filters', value:filter})
          }
        })
      }
    },
    onAddFiles(file){
      this.file = file
      if (!this.file) return;
      const reader = new FileReader();
      let vue = this
      this.loading = true
      reader.onload = function(e) {
        const result = e.target.result;
        const workbook = xlsx.read(result, {type: 'binary'});
        let data = xlsx.utils.sheet_to_json(workbook.Sheets[workbook.SheetNames[0]])
        vue.headers.length = 0
        Object.keys(data[0]).forEach(key=>{
          vue.headers.push({
            text: key,
            value: key,
            type: Number.isFinite(data[0][key]) ? 0:1,
          })
          for (let datum of data) {
            datum["_"+key] = datum[key]
          }
        })
        for (let i = 0; i < data.length; i++) {
          data[i]['_index'] = i;
        }
        vue.setObj({type:'currentIndex', value:-1})
        vue.pushTData({action:["Base", "upload"], tData:data})
        vue.setObj({type:'fileName', value:vue.file.name})
        vue.name = name
        vue.loading = false
      };
      reader.readAsBinaryString(this.file);
    },
    ...mapMutations([
        'setObj',
        'pushTData',
        'setToken'
    ]),
  }
}
</script>

<style scoped>

</style>