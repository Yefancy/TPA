<template>
  <v-row justify="center">
    <v-dialog
        v-model="dialog"
        fullscreen
        hide-overlay
        transition="dialog-bottom-transition"
    >
      <template v-slot:activator="{ on, attrs }">
        <v-btn
            color="primary"
            dark
            v-bind="attrs"
            v-on="on"
        >
          Add DP Noise
        </v-btn>
      </template>
      <v-card>
        <v-toolbar
            dark
            color="primary"
        >
          <v-btn
              icon
              dark
              @click="dialog = false"
          >
            <v-icon>mdi-close</v-icon>
          </v-btn>
          <v-toolbar-title>DP Noise</v-toolbar-title>
          <v-spacer></v-spacer>
          <v-toolbar-items>
            <v-btn
                dark
                text
                @click="saveNoise"
            >
              AddNoise
            </v-btn>
          </v-toolbar-items>
        </v-toolbar>
        <div class="d-flex">
          <div class="col-6 p-0">
            <v-slider
                v-model="slider"
                class="align-center pl-10 pa-2"
                :max="max"
                :min="min"
                hide-details
                label="Aggregate Number"
                style="width: 900px"
            >
              <template v-slot:append>
                <v-text-field
                    v-model="slider"
                    class="mt-0 pt-0"
                    hide-details
                    single-line
                    type="number"
                    style="width: 60px"
                ></v-text-field>
              </template>
            </v-slider>
            <template v-for="item in schema" >
              <div :key="item" v-if="headers[item].type === 0 && dialog" >
                <DPBarChart
                    ref="barCharts"
                    :index="item"
                    :agg-size="parseInt(slider)"
                ></DPBarChart>
              </div>

            </template>
          </div>
          <div class="col-6 p-0 d-flex overflow-x-auto overflow-y-hidden" v-if="dialog">
            <DP2DChart
                v-for="s in schema"
                :key="s"
                :x="s"
                :y="y"
            ></DP2DChart>
          </div>
        </div>
      </v-card>
    </v-dialog>
  </v-row>
</template>

<script>
import {mapState} from "vuex";
import DPBarChart from "@/components/Sheet/Dialog/DPBarChart";
import DP2DChart from "@/components/Sheet/Dialog/DP2DChart";

export default {
  name: "DPNoiseSetting",
  components: {DP2DChart, DPBarChart},
  data() {
    return {
      min: 1,
      max: 60,
      slider: 25,
      dialog: false,
      notifications: false,
      sound: true,
      widgets: false,
    }
  },
  computed: {
    ...mapState([
      'schema',
      'headers',
      'selectedRow'
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
    saveNoise(){
      (this.$refs["barCharts"]).forEach(chart=>chart.saveNoise())
      this.dialog = false
    },
  },
}
</script>

<style scoped>

</style>