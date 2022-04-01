<template>
  <div class="d-flex">
    <v-chip outlined style="width: 100px; margin-block: auto" >Distance</v-chip>
    <UtilityElement v-for="(s, i) in this.elements" :key="i"
      :width="50"
      :height="50"
      :inner-color="s[0]"
      :outline-color="s[1]"
    ></UtilityElement>
  </div>
</template>

<script>
import UtilityElement from "@/components/Utility/UtilityElement";
import {mapGetters, mapState} from "vuex";
import * as mathUtils from "@/plugins/mathUtils";
import * as d3 from "d3";
export default {
  name: "UtilityLineDistance",
  components: {UtilityElement},
  props: {
    inner: Number,
    out: Number,
  },
  computed: {
    ...mapGetters([
      'headersName',
    ]),
    ...mapState([
      'headers',
      'schema',
      'history'
    ]),
    elements(){
      return this.schema.map(d=>this.computeElement(d));
    }
  },
  methods: {
    computeElement(index){
      let colorInter = d3.interpolateRgb('white', 'lightBlue')
      let type = this.headers[index].type
      let value = this.headers[index].value
      let base = this.history[0].tData.map(d=>{
        if (type === 1 && !Array.isArray(d[value])) {
          return [d[value]];
        }
        return d[value];
      });
      let inner = this.history[this.inner].tData.map(d=>{
        if (type === 1 && !Array.isArray(d[value])) {
          return [d[value]];
        }
        return d[value];
      });
      let out = this.history[this.out].tData.map(d=>{
        if (type === 1 && !Array.isArray(d[value])) {
          return [d[value]];
        }
        return d[value];
      });
      let distInner = 0;
      let distOut = 0;
      if (type === 0) {
        distInner = mathUtils.distanceN(base, inner);
        distOut = mathUtils.distanceN(base, out);
      } else if (type === 1) {
        distInner = mathUtils.distanceC(base, inner);
        distOut = mathUtils.distanceC(base, out);
      }
      return [colorInter(distInner), colorInter(distOut)]
    }
  }
}
</script>

<style scoped>

</style>