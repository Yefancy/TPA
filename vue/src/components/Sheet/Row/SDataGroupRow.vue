<template>
  <div class="d-flex data-row" @click.ctrl="rowClick" :class="{'could-drop': couldDrop, 'data-row-selected': selected}">
    <div v-for="(s, index) in schema" style="width: 196px" class="position-relative" :key="s"
         @dragstart="onDragStart($event, index)"
         @dragenter="onDragEnter($event, index)"
         @dragleave="onDragLeave($event, index)"
         @dragend="onDragEnd($event, index)"
         @dragover="onDragOver($event, index)"
         @drop="onDrop($event, index)"
         draggable="true">
      <svg v-if="index < path.length" width="16" height="22" class="position-absolute" stroke="black"
           @click="aggClick(index)">
        <g v-if="aggStart.has(index) && aggEnd.has(index)">
          <line x1="8" x2="8" y1="4" y2="18"></line>
          <line x1="8" x2="14" y1="4" y2="4"></line>
          <line x1="8" x2="14" y1="18" y2="18"></line>
        </g>
        <g v-else-if="aggStart.has(index)">
          <line x1="8" x2="14" y1="10" y2="10"></line>
          <line x1="8" x2="8" y1="10" y2="22"></line>
        </g>
        <g v-else-if="aggEnd.has(index)">
          <line x1="8" x2="14" y1="12" y2="12"></line>
          <line x1="8" x2="8" y1="0" y2="12"></line>
        </g>
        <line v-else x1="8" x2="8" y1="0" y2="22"></line>
      </svg>
      <NumberGroupStock v-if="type(s) === 0"
                        :height="20"
                        :width="180"
                        :group="groupMap(s)"
                        :min="min(s)"
                        :max="max(s)"
      ></NumberGroupStock>
      <CategoricalGroupStock v-else-if="type(s) === 1"
                             style="width: 180px; height: 20px"
                             :group="cateGroupMap(s)"
                             :cate-size="cateMap(s).length"
      ></CategoricalGroupStock>
    </div>
  </div>
</template>

<script>
import {mapMutations, mapState} from "vuex";
import NumberGroupStock from "@/components/Sheet/DataStock/Number/NumberGroupStock";
import CategoricalGroupStock from "@/components/Sheet/DataStock/Categorical/CategoricalGroupStock";

export default {
  name: "SDataGroupRow",
  components: {CategoricalGroupStock, NumberGroupStock},
  data(){
    return{
      couldDrop: false,
    }
  },
  props: {
    rDataGroup: Array,
    schema: Array,
    path: Array,
    aggStart: Set,
    aggEnd: Set,
  },
  computed: {
    selected(){
      for (let rData of this.rDataGroup) {
        if(!this.selectedRow.has(rData)) {
          return false
        }
      }
      return true
    },
    ...mapState([
      'headers',
      'mapSchema',
      'selectedRow',
    ])
  },
  methods: {
    rowClick(){
      if(this.selected) {
        this.selectedRemove(this.rDataGroup)
      } else {
        this.selectedAdd(this.rDataGroup)
      }
    },
    checkDrag(index){
      if(index === this.path.length - 1) {
        if (this.$global.dragged && this.$global.dragged !== this.path && this.$global.dragged.length === this.path.length) {
          for (let i = 0; i < this.path.length - 1; i++) {
            if(this.$global.dragged[i] !== this.path[i]) return false
          }
          return true
        }
      }
      return false
    },
    onDrop(event){
      event.preventDefault();
      this.$emit("mergeGroup", this.$global.dragged, this.path)
      this.couldDrop = false
    },
    onDragOver(event, index){
      if(this.checkDrag(index)) {
        event.preventDefault()
      }
    },
    onDragLeave(event, index){
      if(this.checkDrag(index)) {
        this.couldDrop = false
      }
    },
    onDragEnter(event, index){
      if(this.checkDrag(index)) {
        this.couldDrop = true
      }
    },
    onDragStart(event, index){
      if(index !== this.path.length - 1) {
        event.preventDefault()
      } else {
        this.$global.dragged = this.path
      }
    },
    onDragEnd(){
      this.$global.dragged = null
    },
    aggClick(index) {
      this.$emit('aggClick', index)
    },
    groupMap(index) {
      let header = this.headers;
      return this.rDataGroup.map(rD => rD[header[index].value])
    },
    cateGroupMap(s) {
      let header = this.headers;
      let cateMap = this.mapSchema[s].y
      return this.rDataGroup.map(rD => {
        let value = rD[header[s].value];
        if (Array.isArray(value)) {
          return value.map(d=>cateMap.indexOf(d.toString()))
        }
        return [cateMap.indexOf(value.toString())]
      })
    },
    type(index) {
      return this.headers[index].type
    },
    min(index) {
      let bins = this.mapSchema[index].y
      return bins[0].min
    },
    max(index) {
      let bins = this.mapSchema[index].y
      return bins[bins.length - 1].max
    },
    cateMap(index) {
      return this.mapSchema[index].y
    },
    ...mapMutations([
      'selectedAdd',
      'selectedRemove',
    ]),
  },
}
</script>

<style scoped>
@import "row.css";
.could-drop{
  background-color: pink;
}
</style>