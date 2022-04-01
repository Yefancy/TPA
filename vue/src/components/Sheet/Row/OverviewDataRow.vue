<template>
  <div class="d-flex overview-data-row">
    <div style="width: 180px; height: 1px; margin-left: 16px;" class="position-relative" v-for="(s, index) in schema" :key="-1-s">
      <template v-if="type(s) ===  0">
        <div class="h-100"
             :style="{backgroundColor: 'lightblue', width: `${180 * parseFloat(value(s)) / max(s)}px`}"
        ></div>
      </template>
      <template v-else>
        <div class="h-100" v-for="item in path[index]"
             :key="item"
             :style="{backgroundColor: color(item), width: `${180 / path[index].length}px`}"
        ></div>
      </template>
    </div>
  </div>
</template>

<script>
import * as d3 from "d3";
import {mapState} from "vuex";

export default {
  name: "OverviewDataRow",

  props: {
    aggEnd: Set,
    aggStart: Set,
    path: Array,
    rData: Object,
    schema: Array,
  },
  methods: {
    color: function (index){
      return d3.schemeSet3[index]
    },
    max(index) {
      let bins = this.mapSchema[index].y
      return bins[bins.length - 1].max
    },
    type(index) {
      return this.headers[index].type
    },
    value(index) {
      return this.rData[this.headers[index].value]
    },
  },
  computed: {
    ...mapState([
      'headers',
      'mapSchema',
      'selectedRow',
    ]),

  }
}
</script>

<style scoped>
@import "row.css";
</style>