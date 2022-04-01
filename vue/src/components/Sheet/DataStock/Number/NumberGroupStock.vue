<template>
  <div class="stock">
    <svg ref="svg" :width="width" :height="height">
      <line
          :x1="width * ((gMin - min) / (max - min))"
          :x2="width * ((gMax - min) / (max - min))"
          :y1="height / 2"
          :y2="height / 2"
          stroke="black"
      ></line>
      <rect
          :x="width * ((gQuarterMin - min) / (max - min))"
          :y="2"
          :width="width * ((gQuarterMax - gQuarterMin) / (max - min))"
          :height="height - 4"
          class="box-plot">
      </rect>
      <line
          :x1="width * ((gAvg - min) / (max - min))"
          :x2="width * ((gAvg - min) / (max - min))"
          :y1="2"
          :y2="height - 2"
          stroke="black"
      ></line>
      <line
          :x1="width * ((gMin - min) / (max - min))"
          :x2="width * ((gMin - min) / (max - min))"
          :y1="3"
          :y2="height - 3"
          stroke="black"
      ></line>
      <line
          :x1="width * ((gMax - min) / (max - min))"
          :x2="width * ((gMax - min) / (max - min))"
          :y1="3"
          :y2="height - 3"
          stroke="black"
      ></line>
    </svg>
  </div>
</template>

<script>
import * as d3 from 'd3';
export default {
  name: "NumberGroupStock",
  props: {
    group: Array,
    max: Number,
    min: Number,
    width: Number,
    height: Number,
  },
  computed: {
    sorted(){
      return d3.sort(this.group)
    },
    gQuarterMax(){
      return this.sorted[Math.floor(this.sorted.length * 0.75)]
    },
    gQuarterMin(){
      return this.sorted[Math.floor(this.sorted.length * 0.25)]
    },
    gAvg(){
      return d3.sum(this.group) / this.group.length
    },
    gMax(){
      return d3.max(this.group)
    },
    gMin(){
      return d3.min(this.group)
    }
  }
}

</script>

<style scoped>
@import "../stock.css";
.box-plot{
  stroke-width: 1px;
  stroke: black;
  fill: lightblue;
}
</style>