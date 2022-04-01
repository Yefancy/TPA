<template>
  <div class="d-flex data-row" @click.ctrl="rowClick" :class="{'data-row-selected': selected}">
    <div style="width: 196px" class="position-relative" v-for="(s, index) in schema" :key="-1-s">
      <svg width="16" height="22" class="position-absolute" stroke="black" @click.stop="aggClick(index)">
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
      <NumberStock v-if="type(s) === 0"
                   :key="s"
                   :height="20"
                   :width="180"
                   :value="parseFloat(value(s))"
                   :max="max(s)"
      ></NumberStock>
      <CategoricalStock v-else-if="type(s) === 1"
                        :key="s"
                        :cateMap="cateMap(s)"
                        :label="path[index]"
                        :height="20"
      ></CategoricalStock>
    </div>
  </div>
</template>

<script>
import NumberStock from "@/components/Sheet/DataStock/Number/NumberStock";
import CategoricalStock from "@/components/Sheet/DataStock/Categorical/CategoricalStock";
import {mapMutations, mapState} from 'vuex'

export default {
  components: {NumberStock, CategoricalStock},
  computed: {
    ...mapState([
      'headers',
      'mapSchema',
      'selectedRow',
    ]),
    selected(){
      return this.selectedRow.has(this.rData)
    },
  },
  created() {

  },
  methods: {
    aggClick(index) {
      this.$emit('aggClick', index)
    },
    cateMap(index) {
      return this.mapSchema[index].y
    },
    max(index) {
      let bins = this.mapSchema[index].y
      return bins[bins.length - 1].max
    },
    rowClick(){
      if(this.selected) {
        this.selectedRemove(this.rData)
      } else {
        this.selectedAdd(this.rData)
      }
    },
    type(index) {
      return this.headers[index].type
    },
    value(index) {
      return this.rData[this.headers[index].value]
    },
    ...mapMutations([
      'selectedAdd',
      'selectedRemove',
    ]),
  },
  name: "SDataRow",
  props: {
    aggEnd: Set,
    aggStart: Set,
    path: Array,
    rData: Object,
    schema: Array,
  },
  updated() {
  }
}
</script>

<style scoped>
@import "row.css";
</style>