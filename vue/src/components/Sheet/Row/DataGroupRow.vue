<template>
  <div class="d-flex data-row">
    <template v-for="s in schema" >
      <NumberGroupStock  v-if="type(s) === 0"
                        :key="s"
                        :height="20"
                        :width="180"
                        :group="groupMap(s)"
                        :min="min(s)"
                        :max="max(s)"
      ></NumberGroupStock>
      <CategoricalGroupStock v-else-if="type(s) === 1"
                             :key="s"
                             :height="20"
                             :width="180"
                             :group="groupMap(s)"
                             :cate-map="cateMap(s)"
      ></CategoricalGroupStock>
    </template>
  </div>
</template>

<script>
import {mapState} from "vuex";
import NumberGroupStock from "@/components/Sheet/DataStock/Number/NumberGroupStock";
import CategoricalGroupStock from "@/components/Sheet/DataStock/Categorical/CategoricalGroupStock";

export default {
  name: "DataGroupRow",
  components: {CategoricalGroupStock, NumberGroupStock},
  props: {
    group: Array,
    schema: Array,
    selected: Boolean,
  },
  computed: {
    ...mapState([
      'headers',
      'mapSchema'
    ])
  },
  methods: {
    groupMap(index){
      let header = this.headers;
      return this.group.map(rD => rD[header[index].value])
    },
    type(index){
      return this.headers[index].type
    },
    min(index){
      let bins = this.mapSchema[index].y
      return bins[0].min
    },
    max(index){
      let bins = this.mapSchema[index].y
      return bins[bins.length - 1].max
    },
    cateMap(index){
      return this.mapSchema[index].y
    }
  }
}
</script>

<style scoped>
@import "row.css";
</style>