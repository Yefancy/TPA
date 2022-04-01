<template>
  <div>
    <div ref="chart" style="height: 244px; width: 244px"></div>
  </div>
</template>

<script>
import * as echarts from "echarts";
import {mapState} from "vuex";

export default {
  name: "DotChart",
  data() {
    return {
      myChart: null,
    }
  },
  props: {
    y: Number,
    x: Number,
  },
  computed: {
    ...mapState([
      'schema',
      'headers',
      'mapSchema',
      'selectedRow'
    ]),
  },
  mounted() {
    Object.defineProperty(this.$refs.chart,'clientWidth',{get:function(){return 244;}})
    Object.defineProperty(this.$refs.chart,'clientHeight',{get:function(){return 244;}})
    this.drawBox()
  },
  methods: {
    drawBox() {
      let option;
      let data = []
      let mapX = this.mapSchema[this.x];
      let mapY = this.mapSchema[this.y];
      for (let row of this.selectedRow) {
        data.push([row[mapX.x], row[mapY.x]])
      }
      if (this.myChart == null) {
        this.myChart = echarts.init(this.$refs.chart);
        option = {
          xAxis: {},
          yAxis: {},
          brush: {
            xAxisIndex: 'all',
            outOfBrush: {
              colorAlpha: 0.1
            }
          },
          grid: {
            top: '10%',
            left: '10%',
            right: '10%',
            bottom: '10%',
          },
          series: [
            {
              symbolSize: 2,
              data,
              type: 'scatter'
            }
          ]
        };
      } else {
        option = this.myChart.getOption()
        option.series.data=data
      }
      option && this.myChart.setOption(option);
    }
  }
}
</script>

<style scoped>

</style>