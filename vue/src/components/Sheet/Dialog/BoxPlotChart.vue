<template>
  <div>
    <div ref="chart" style="height: 244px; width: 244px"></div>
  </div>
</template>

<script>
import * as echarts from "echarts";
import {mapState} from "vuex";

export default {
  name: "BoxPlotChart",
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
  watch: {
    selectedRow() {
      this.drawBox()
    }
  },
  methods: {
    drawBox() {
      let option;
      let data = []
      let mapX = this.mapSchema[this.x];
      let mapY = this.mapSchema[this.y];
      for (let i = 0; i < mapX.y.length; i++) {
        data.push([])
      }
      for (let row of this.selectedRow) {
        let datum = row[mapX.x]
        if (Array.isArray(datum)) {
          for (let d of datum) {
            data[mapX.y.indexOf(d.toString())].push(row[mapY.x])
          }
        } else {
          data[mapX.y.indexOf(datum.toString())].push(row[mapY.x])
        }
      }
      if (this.myChart == null) {
        this.myChart = echarts.init(this.$refs.chart);
        option = {
          dataset: [
            {
              source: data
            },
            {
              transform: {
                type: 'boxplot',
                config: { itemNameFormatter: function (params) {
                    return mapX.y[params.value].toString();
                  } }
              }
            }
          ],
          grid: {
            top: '10%',
            left: '10%',
            right: '10%',
            bottom: '10%',
          },
          xAxis: {
            type: 'category',
            data: mapX.y
          },
          yAxis: {
            type: 'value'
          },
          series: [
            {
              name: 'boxplot',
              type: 'boxplot',
              datasetIndex: 1
            }
          ]
        };
      } else {
        option = this.myChart.getOption()
        option.dataset.source=data
        option.xAxis.data=mapX.y
      }
      option && this.myChart.setOption(option);
    }
  }
}
</script>

<style scoped>

</style>