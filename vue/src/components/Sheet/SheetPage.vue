<template>
  <div class="d-flex">
    <div class="col-9 p-0">
      <v-card elevation="4" shaped tile class="p-lg-2 m-2 w-100" style="height: 850px">
        <SchemaSelector @change="requestBasicTree"></SchemaSelector>
        <div class="d-flex pl-1">
          <AttributeHeader class="p-2" v-for="(s) in schema" :key="s" :s="s"></AttributeHeader>
        </div>

        <div class="overflow-auto" v-if="overView" style="height:600px">
          <OverviewDataRow v-for="(item) in itemsList" :key="item.originData"
                           :r-data="tData[item.originData]"
                           :schema="schema"
                           :path="item.path"
                           :agg-start="item.aggStart"
                           :agg-end="item.aggEnd">
          </OverviewDataRow>
        </div>
        <v-virtual-scroll
            v-else
            height="600"
            item-height="22"
            :items="itemsList"
            @contextmenu="contextmenu"
        >
          <template v-slot:default="{item}">
            <SDataRow
                v-if="!item.isAgg"
                :r-data="tData[item.originData]"
                :schema="schema"
                :path="item.path"
                :agg-start="item.aggStart"
                :agg-end="item.aggEnd"
                @aggClick="aggClick($event, item)"
            ></SDataRow>
            <SDataGroupRow
                v-else
                :schema="schema"
                :path="item.path"
                :agg-start="item.aggStart"
                :agg-end="item.aggEnd"
                :r-data-group="rDataGroup(item.originData)"
                @aggClick="aggClick($event, item)"
                @mergeGroup="mergeGroup"
            ></SDataGroupRow>
          </template>
        </v-virtual-scroll>
        <v-menu
            v-model="showMenu"
            :position-x="menuX"
            :position-y="menuY"
            absolute
            offset-y
        >

          <v-list flat>
            <v-list-item>
              <DPNoiseSetting></DPNoiseSetting>
              <!--              <v-list-item-content>add dp noise</v-list-item-content>-->
            </v-list-item>

            <v-list-item>
              <DPNoiseSetting></DPNoiseSetting>
            </v-list-item>

            <v-list-item>
              <DPNoiseSetting></DPNoiseSetting>
            </v-list-item>



          </v-list>
        </v-menu>
      </v-card>
    </div>
    <div class="col-3 pt-0 pr-0">
      <v-card elevation="4" shaped tile class="p-lg-2 m-2" style="width: initial">
        <v-switch v-model="overView" label="Overview"></v-switch>
      </v-card>
      <v-card elevation="4" shaped tile class="p-lg-2 m-2" style="width: initial">
        <GlobalTree :tree-root="treeRoot"></GlobalTree>
      </v-card>
      <v-card elevation="4" shaped tile class="p-lg-2 m-2" style="width: initial">
        <v-card-title>History</v-card-title>
        <v-list dense style="height: 300px;" class="overflow-auto">
          <v-list-item-group
              :value="this.currentIndex"
              @change="selectHistory"
              mandatory
              color="primary">
            <v-list-item v-for="(item, i) in this.history" :key="i">
              <v-list-item-action>
                <v-chip :color="
                item.action[0]==='Base' ? 'primary':
                item.action[0]==='Merge' ? 'red':
                item.action[0]==='DP' ? 'green':'secondary'"
                        text-color="white">{{item.action[0]}}</v-chip>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title v-text="item.action[1]"></v-list-item-title>
              </v-list-item-content>
            </v-list-item>
          </v-list-item-group>
        </v-list>
      </v-card>
    </div>
  </div>
</template>

<script>
import {mapGetters, mapMutations, mapState} from 'vuex'
import AttributeHeader from "@/components/Sheet/AttributeHeader";
import SchemaSelector from "@/components/Sheet/SchemaSelector";
import GlobalTree from "@/components/Sheet/GlobalTree/GlobalTree";
import SDataRow from "@/components/Sheet/Row/SDataRow";
import SDataGroupRow from "@/components/Sheet/Row/SDataGroupRow";
import * as mathUtils from "@/plugins/mathUtils";
import DPNoiseSetting from "@/components/Sheet/Dialog/DPNoiseSetting";
import OverviewDataRow from "@/components/Sheet/Row/OverviewDataRow";


export default {
  name: "SheetPage",
  components: {OverviewDataRow, DPNoiseSetting, SDataGroupRow, SDataRow, GlobalTree, SchemaSelector, AttributeHeader},
  data() {
    return {
      selectedItem: 1,
      width: 180,
      itemsList: [],
      showMenu: false,
      overView: false,
      menuX: 0,
      menuY: 0,
    }
  },
  computed: {
    ...mapState([
      'treeRoot',
      'schema',
      'sensitives',
      'headers',
      'mapSchema',
      'filters',
      'currentIndex',
      'history'
    ]),
    ...mapGetters([
      'tData',
      'headersName'
    ]),
  },
  methods: {
    ...mapMutations([
      'pushTData',
    ]),
    selectHistory(e){
      const modify = {}
      let newTData = this.history[e].tData;
      for (let i = 0; i < this.tData.length; i++) {
        if (newTData[i] !== this.tData[i]) {
          let clean = {}
          for (let header of this.headers) {
            clean[header.value] = newTData[i][header.value]
          }
          modify[i] = clean
        }
      }
      this.setObj({type:'currentIndex', value:e})
      let _this = this;
      this.$http.post('updateTableau', {
        modify
      }).then(res => {
        _this.itemsList.length = 0
        _this.setObj({type:'treeRoot', value:_this.handleList(_this.itemsList, res.data.data, [])})
        // _this.setObj({type:'treeRoot', value:res.data.data})
      })
    },
    contextmenu(e) {
      e.preventDefault()
      this.showMenu = false
      this.menuX = e.clientX
      this.menuY = e.clientY
      this.$nextTick(() => {
        this.showMenu = true
      })
    },
    rDataGroup(group) {
      let _this = this;
      return (function dfs(list, root) {
        root.forEach(item => {
          if (item.isAgg) {
            dfs(list, item.originData)
          } else {
            list.push(_this.tData[item.originData])
          }
        })
        return list
      })([], group)
    },
    findNodeByPath(path, root){
      for (let i = 0; i < path.length; i++) {
        root = root.children.find(child=>child.labels.equal(path[i]))
        if(!root) return
      }
      return root;
    },
    mergeGroup(path1, path2){
      const tDataList = [...this.findNodeByPath(path1, this.treeRoot).content.d, ...this.findNodeByPath(path2, this.treeRoot).content.d]
      const set = new Set()
      path1[path1.length - 1].forEach(l=>set.add(l))
      path2[path2.length - 1].forEach(l=>set.add(l))
      const ms = this.mapSchema[this.schema[path1.length - 1]].y
      const label = []
      for (let d of set) {
        label.push(ms[d])
      }
      const value = this.headers[this.schema[path1.length - 1]].value
      const modify = {}
      let newTData = [...this.tData]
      for (let i of tDataList) {
        let pass = true
        for (let s of this.schema) {
          let dataValue = this.tData[i][this.headers[s].value]
          let f = this.filters[s]
          if (this.headers[s].type == 0) {
            if (dataValue < f[0][0] || dataValue > f[0][1]) {
              pass = false;
              break;
            }
          } else {
            if (Array.isArray(dataValue)) {
              for (let label of dataValue) {
                if (f.indexOf(label.toString()) < 0) {
                  pass = false
                  break
                }
              }
            } else {
              if (f.indexOf(dataValue.toString()) < 0) {
                pass = false
                break
              }
            }
          }
          if (!pass) break;
        }
        if (pass) {
          let newRow = window.deepCopy(this.tData[i])
          newTData[newRow['_index']] = newRow
          newRow[value] = label
          let clean = {}
          for (let header of this.headers) {
            clean[header.value] = newRow[header.value]
          }
          modify[i] = clean
        }
      }
      this.pushTData({action:["Merge", "merge"], tData:newTData})
      let _this = this;
      this.$http.post('updateTableau', {
        modify
      }).then(res => {
          _this.itemsList.length = 0
          _this.setObj({type:'treeRoot', value:_this.handleList(_this.itemsList, res.data.data, [])})
          // _this.setObj({type:'treeRoot', value:res.data.data})
      })
    },
    aggClick(index, item){
      let s=0,e=0;
      for (s = this.itemsList.indexOf(item); s >= 0; s--) {
        if(this.itemsList[s].aggStart.has(index)) {
          break
        }
      }
      for (e = this.itemsList.indexOf(item); e < this.itemsList.length; e++) {
        if(this.itemsList[e].aggEnd.has(index)) {
          break
        }
      }
      if(!item.isAgg || index < item.path.length - 1) {
        const groupItem = {
          isAgg: true,
          originData: null,
          aggStart: null,
          aggEnd: null,
          path: null,
        }
        groupItem.originData = this.itemsList.splice(s, e - s + 1, groupItem)
        groupItem.aggStart = new Set(groupItem.originData[0].aggStart)
        groupItem.aggEnd = new Set(groupItem.originData[e - s].aggEnd)
        groupItem.path = groupItem.originData[0].path.slice(0, index + 1)
        this.findNodeByPath(groupItem.path, this.treeRoot).collapse = true;
      } else {
        this.itemsList.splice(s, e - s + 1, ...item.originData)
        this.findNodeByPath(this.treeRoot, item.path).collapse = false;
      }

    },
    requestBasicTree(schema, sensitives) {
      let data = {
        schema: schema,
        sensitives: sensitives
      }
      let _this = this;
      this.$http.post('updateSchema', data).then(res => {
        _this.setObj({type:'schema', value:schema})
        _this.setObj({type:'sensitives', value:sensitives})

        _this.itemsList.length = 0;
        _this.setObj({type:'treeRoot', value:_this.handleList(_this.itemsList, res.data.data, [])})

      })
    },
    handleList(list, root, path) {
      if(!root) return list
      let start = list.length
      let _this = this
      if (root.children.length > 0) {
        root.children.forEach(child => {
          let s = _this.schema[path.length]
          let f = _this.filters[s]
          let found = false
          if (_this.headers[s].type == 0) {
            found = true;
          } else {
            for (let label of child.labels) {
              if (f.indexOf(_this.mapSchema[s].y[label]) >= 0) {
                found = true
                break
              }
            }
          }
          if (found) {
            path.push(child.labels)
            this.handleList(list, child, path)
            path.pop()
          }
        })
      } else {
        root.content.d.forEach(index => {
          let pass = true
          for (let s of _this.schema) {
            if (_this.headers[s].type == 0) {
              let value = _this.tData[index][_this.headers[s].value]
              let f = _this.filters[s][0]
              if (value < f[0] || value > f[1]) {
                pass = false;
                break;
              }
            }
          }

          if (pass) {
            list.push({
              isAgg: false,
              aggStart: new Set(),
              aggEnd: new Set(),
              originData: index,
              path: [...path],
            })
          }
        })
      }
      let end = list.length - 1
      if (end >= start) {
        list[start].aggStart.add(path.length - 1)
        list[end].aggEnd.add(path.length - 1)
      }
      return root
    },
    ...mapMutations([
      'setObj',
    ]),
  },
  watch: {
    filters(){
      console.log('update')
      mathUtils.distributionN([1, 2, 3], [1, 2, 3]).then(res=>{
        console.log(res);
      })
      this.itemsList.length = 0;
      this.handleList(this.itemsList, this.treeRoot, [])
    }
  },
  created() {
  },
  mounted() {
    this.itemsList.length = 0;
    this.handleList(this.itemsList, this.treeRoot, [])
  }
}
</script>

<style scoped>

</style>