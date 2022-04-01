<template>
  <div class="text-center">
    <h4 v-text="this.headers[this.s].text"></h4>
    <div>
      <b-button-group>
        <v-menu
            :close-on-content-click="false"
            offset-y>
          <template v-slot:activator="{ on }">
            <b-button title="Sort" v-on="on">
              <b-icon :icon="sortIcon" aria-hidden="true" color="white"></b-icon>
            </b-button>
          </template>
          <v-color-picker
              hide-canvas
          ></v-color-picker>
        </v-menu>

        <v-menu
            :close-on-content-click="false"
            offset-y>
          <template v-slot:activator="{ on }">
            <b-button title="Filter" v-on="on">
              <b-icon icon="filter-circle" aria-hidden="true" color="white"></b-icon>
            </b-button>
          </template>

          <v-list v-if="type === 0" class="px-4">
            <v-list-item
                v-for="(item, index) in this.filters[s]" :key="index"
            >
              <v-range-slider
                  v-model.lazy="range"
                  :max="mapSchema[s].y[0].max"
                  :min="mapSchema[s].y[0].min"
                  @change="rangeChange(index)"
                  hide-details
                  style="width: 260px"
                  class="align-center"
              >
                <template v-slot:prepend>
                  <v-text-field
                      :value="item[0]"
                      class="mt-0 pt-0"
                      hide-details
                      single-line
                      type="number"
                      style="width: 60px"
                      @change="$set(item, 0, $event)"
                  ></v-text-field>
                </template>
                <template v-slot:append>
                  <v-text-field
                      :value="item[1]"
                      class="mt-0 pt-0"
                      hide-details
                      single-line
                      type="number"
                      style="width: 60px"
                      @change="$set(item, 1, $event)"
                  ></v-text-field>
                </template>
              </v-range-slider>
            </v-list-item>
          </v-list>

          <v-list v-else-if="type === 1">
            <v-list-item
                v-for="(item, index) in this.mapSchema[s].y" :key="index"
            >
              <v-checkbox
                  v-model="filters[s]"
                  :label="item"
                  :value="item"
              ></v-checkbox>
            </v-list-item>
          </v-list>

        </v-menu>

        <v-menu
            :close-on-content-click="false"
            offset-y>
          <template v-slot:activator="{ on }">
            <b-button title="TODO" v-on="on">
              <b-icon icon="type-underline" aria-hidden="true" color="white"></b-icon>
            </b-button>
          </template>
          <v-color-picker
              hide-canvas
          ></v-color-picker>
        </v-menu>

        <v-menu
            :close-on-content-click="false"
            offset-y>
          <template v-slot:activator="{ on }">
            <b-button title="TODO" v-on="on">
              <b-icon icon="type-strikethrough" aria-hidden="true" color="white"></b-icon>
            </b-button>
          </template>
          <v-color-picker
              hide-canvas
          ></v-color-picker>
        </v-menu>
      </b-button-group>
    </div>

    <ColumnAbstract :width="width" :data="columnData" :type="type" :cateMap="this.mapSchema[s].y"></ColumnAbstract>
  </div>

</template>

<script>
import ColumnAbstract from "@/components/Sheet/ColumnAbstract";
import {mapGetters, mapState} from 'vuex'



export default {
  name: "AttributeHeader",
  components: {ColumnAbstract},
  props: {
    s: Number,
  },
  data() {
    return {
      width: 180,
      range:[0,0]
    }
  },
  computed: {
    columnData(){
      return this.tData.map(d=>d[this.headers[this.s].value])
    },
    type(){
      return this.headers[this.s].type
    },
    sortIcon: function (){
      return this.type === 0 ? 'sort-numeric-up': 'sort-up'
    },
    ...mapGetters([
      'tData',
    ]),
    ...mapState([
        'headers',
        'mapSchema',
        'filters',
    ])
  },
  methods: {
    rangeChange(index){
      this.$set(this.filters[this.s][index], 0, this.range[0])
      this.$set(this.filters[this.s][index], 1, this.range[1])
      // this.filters[this.s][index][0] = this.range[0]
      // this.filters[this.s][index][1] = this.range[1]
    }
  },
  mounted() {
    this.range[0] = this.mapSchema[this.s].y[0].min
    this.range[1] = this.mapSchema[this.s].y[0].max
  }
}
</script>

<style scoped>

</style>