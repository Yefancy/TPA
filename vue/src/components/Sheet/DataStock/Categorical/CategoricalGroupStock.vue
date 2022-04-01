<template>
  <div class="stock">
    <div v-for="(data, index) in dataList" :key="index" style="height: 100%;" :style="{width: `${(data[0]+data[1]) / sum * 100}%`}">
      <div :style="{height: `${data[1] * 100 / (data[0] + data[1])}%`, opacity: 0.5, backgroundColor: color(index)}"></div>
      <div :style="{height: `${data[0] * 100 / (data[0] + data[1])}%`, backgroundColor: color(index)}"></div>
    </div>
  </div>
</template>

<script>
import * as d3 from 'd3';

export default {
  name: "CategoricalGroupStock",
  props: {
    group: Array,
    cateSize: Number,
  },
  methods: {
    color: function (index){
      return d3.schemeSet3[index]
    },
    reRender() {
      let countMap = []
      this.group.forEach(d => {
        for (let label of d) {
          if(!countMap[label]) {
            countMap[label] = [0,0]
          }
          if(d.length > 1) {
            countMap[label][1]++
          } else {
            countMap[label][0]++
          }
        }
      })
      for (let i = 1; i < countMap.length; i++) {
        countMap[i] += countMap[i - 1]
      }
      let sum = this.group.length
      d3.select(this.$refs.svg).selectAll('rect')
          .data(countMap)
          .join('rect')
          .attr('height', '100%')
          .attr('x', (d,i) => i > 0 ? `${(countMap[(i-1)] / sum) * 100}%` : 0)
          .attr('fill',(d,i) =>  d3.schemeSet3[i])
          .attr('width', d => `${d / sum * 100}%`)
    }
  },
  computed: {
    dataList() {
      let dataList = new Array(this.cateSize)
      for (let i = 0; i < dataList.length; i++) {
        dataList[i] = [0,0]
      }
      this.group.forEach(d => {
        for (let label of d) {
          if(d.length > 1) {
            dataList[label][1]++
          } else {
            dataList[label][0]++
          }
        }
      })
      return dataList;
    },
    sum() {
      let sum = 0;
      this.dataList.forEach(d=>d.forEach(_d=>sum += _d))
      return sum;
    },
  },
}
</script>

<style scoped>
@import "../stock.css";
</style>