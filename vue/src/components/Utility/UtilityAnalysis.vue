<template>
  <div class="d-flex">
    <div class="col-2 p-0">
      <v-card elevation="4" shaped tile class="p-lg-2 m-2" style="width: initial">
        <v-card-title>inner</v-card-title>
        <v-list dense style="height: 300px;" class="overflow-auto">
          <v-list-item-group
              v-model="s1"
              mandatory
              color="primary">
            <v-list-item v-for="(item, i) in this.history" :key="i">
              <v-list-item-action>
                <v-chip :color="
                item.action[0]==='Base' ? 'primary':
                item.action[0]==='Merge' ? 'red':
                item.action[0]==='DP' ? 'green':'secondary'"
                        text-color="white">{{item.action[0]}}</v-chip>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title v-text="item.action[1]"></v-list-item-title>
              </v-list-item-content>
            </v-list-item>
          </v-list-item-group>
        </v-list>
      </v-card>
      <v-card elevation="4" shaped tile class="p-lg-2 m-2" style="width: initial">
        <v-card-title>out</v-card-title>
        <v-list dense style="height: 300px;" class="overflow-auto">
          <v-list-item-group
              v-model="s2"
              mandatory
              color="primary">
            <v-list-item v-for="(item, i) in this.history" :key="i">
              <v-list-item-action>
                <v-chip :color="
                item.action[0]==='Base' ? 'primary':
                item.action[0]==='Merge' ? 'red':
                item.action[0]==='DP' ? 'green':'secondary'"
                        text-color="white">{{item.action[0]}}</v-chip>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title v-text="item.action[1]"></v-list-item-title>
              </v-list-item-content>
            </v-list-item>
          </v-list-item-group>
        </v-list>
      </v-card>
    </div>
    <v-card elevation="4" shaped tile class="p-lg-2 m-2 col-3" style="height: fit-content">
      <v-card-title>asefasef</v-card-title>
      <UtilityMatrix :inner="s1" :out="s2"></UtilityMatrix>
    </v-card>
    <div class="col-7 p-0 d-flex overflow-x-auto overflow-y-hidden">
      <DP2DChart
          v-for="(ss) in this.schema"
          :key="ss"
          :x="ss"
          :y="y"
      ></DP2DChart>
    </div>
  </div>
</template>

<script>
import {mapState, mapMutations, mapGetters} from 'vuex'
import UtilityMatrix from "@/components/Utility/UtilityMatrix";
import DP2DChart from "@/components/Sheet/Dialog/DP2DChart";
export default {
  name: "UtilityAnalysis",
  components: {UtilityMatrix, DP2DChart},
  data(){
    return{
      s1:0,
      s2:0,
    }
  },
  computed: {
    ...mapGetters([
        'headersName',
        'tData',
    ]),
    ...mapState([
        'headers',
        'fileName',
        'schema',
        'history'
    ]),
    y() {
      let y = []
      for (let s of this.schema) {
        if (this.headers[s].type === 0) {
          y.push(s)
        }
      }
      return y
    }
  },
  methods: {
    ...mapMutations([
        'setObj',
        'pushTData',
        'setToken'
    ]),
  }
}
</script>

<style scoped>

</style>