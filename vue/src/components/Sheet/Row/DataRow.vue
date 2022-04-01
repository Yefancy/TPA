<template>
  <div class="d-flex data-row">
    <template v-for="(s) in schema" >
      <NumberStock v-if="type(s) === 0"
                   :key="s"
                   :height="20"
                   :width="180"
                   :value="parseFloat(value(s))"
                   :max="max(s)"
      ></NumberStock>
      <CategoricalStock v-else-if="type(s) === 1"
                        :key="s"
                        :height="20"
                        :width="180"
                        :value="value(s).toString()"
                        :cateMap="cateMap(s)"
      ></CategoricalStock>
    </template>
  </div>
</template>

<script>
import NumberStock from "@/components/Sheet/DataStock/Number/NumberStock";
import CategoricalStock from "@/components/Sheet/DataStock/Categorical/CategoricalStock";
import {mapState} from 'vuex'

export default {
  name: "DataRow",
  components: {NumberStock, CategoricalStock},
  props: {
    rData: Object,
    schema: Array,
    path: Array,
    aggStart: Set,
    aggEnd: Set,
  },
  computed: {
    ...mapState([
        'headers',
        'mapSchema'
    ])
  },
  methods: {
    aggClick(index){
      this.$emit('aggClick', this.path.slice(0, index + 1))
    },
    type(index){
      return this.headers[index].type
    },
    max(index){
      let bins = this.mapSchema[index].y
      return bins[bins.length - 1].max
    },
    cateMap(index){
      return this.mapSchema[index].y
    },
    value(index){
      return this.rData[this.headers[index].value]
    }
  }
}
</script>

<style scoped>
@import "row.css";
</style>