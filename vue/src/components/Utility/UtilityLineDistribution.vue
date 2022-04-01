<template>
  <div class="d-flex">
    <v-chip outlined style="width: 100px; margin-block: auto">Distribution</v-chip>
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
  name: "UtilityLineDistribution",
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
    async computeElement(index){
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
        distInner = (await mathUtils.distributionN(base, inner)).data.pvalue;
        distOut = (await mathUtils.distributionN(base, out)).data.pvalue;
        console.log(distInner)
        console.log(distOut)
        return [colorInter(1 - distOut), colorInter(1 - distOut)]
      } else if (type === 1) {
        distInner = mathUtils.distributionC(base, inner);
        distOut = mathUtils.distributionC(base, out);
        return [colorInter(distInner / 50), colorInter(distOut / 50)]
      }
    }
  }
}
</script>

<style scoped>

</style>