<template>
  <div class="stock" style="height: 20px;">
    <div class="h-100" v-for="item in label" :key="item" :style="{backgroundColor: color(item), width: `${width}px`}"></div>
    <div class="overflow" style="width: 60%; padding-left: 10px;" :style="{lineHeight: `${height}px`}" @dblclick="doubleClick">
      {{value}}
    </div>
  </div>
</template>

<script>
import * as d3 from 'd3'
export default {
  name: "CategoricalStock",
  props: {
    label: Array,
    cateMap: Array,
    height: Number,
  },
  methods: {
    doubleClick(){
      console.log(123)
    },
    color: function (index){
      return d3.schemeSet3[index]
    }
  },
  computed: {
    width(){
      return 1 / this.label.length * this.height
    },
    value(){
      let str = ''
      for (let i = 0; i < this.label.length; i++) {
        str += this.cateMap[this.label[i]]
        if(i < this.label.length - 1) {
          str += ' or '
        }
      }
      return str;
    }
  }
}
</script>

<style scoped>
@import "../stock.css";
.overflow{
  white-space:nowrap;
  overflow:hidden;
  text-overflow: ellipsis;
}
</style>