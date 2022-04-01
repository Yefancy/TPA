<template>
  <div>
    <svg ref="svg" viewBox="0 -100 300 100" :width="width"></svg>
  </div>
</template>

<script>
import * as d3 from 'd3';

export default {
  name: "ColumnAbstract",
  data() {
    return {
      aggSize : 8,
    }
  },
  props: {
    data: Array,
    type: Number,
    width: Number,
    cateMap: Array,
  },
  methods: {
    draw() {
      let data;
      if (this.type === 0) {
        let min = d3.min(this.data)
        let dur = (d3.max(this.data) - min) / this.aggSize
        data = new Array(this.aggSize).fill(0)
        this.data.forEach(d=>{
          let index = (d - min) / dur;
          data[Math.floor(index === this.aggSize ? this.aggSize - 1 : index)] += 1
        })
      } else {
        data = new Array(this.cateMap.length).fill(0)
        this.data.forEach(d=>{
          data[this.cateMap.indexOf(d.toString())] += 1
        })
      }
      let linear = d3.scaleLinear().domain([0, d3.max(data)]).range([0, 90])
      d3.select(this.$refs.svg).selectAll('rect')
          .data(data)
          .join('rect')
          .attr('fill', (d, i) => {
            switch (this.type) {
              case 0:
                return 'lightblue'
              case 1:
                return d3.schemeSet3[i];
            }
          })
          .transition()
          .attr('x', (d, i)=>i*300/data.length + 1)
          .attr('y', d=>-linear(d) - 3)
          .attr('width', 300/data.length - 3)
          .attr('height', d=>linear(d))
    }
  },
  mounted() {
    d3.select(this.$refs.svg)
        .append('line')
        .attr('x1', 0)
        .attr('x2', 300)
        .attr('y1', 0)
        .attr('y2', 0)
        .attr('stroke-width', 6)
        .attr('stroke', 'gray')
    this.draw()
  },
  watch: {
    data(){
      this.draw()
    },
    type(){
      this.draw()
    },
    aggSize(){
      this.draw()
    }
  }
}
</script>

<style scoped>

</style>