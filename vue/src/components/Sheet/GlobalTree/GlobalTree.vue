<template>
  <div>
    <v-item-group mandatory v-model="mode">
      <v-item
          v-for="n in [0,1,2]"
          :key="n"
          v-slot="{ active, toggle }"
      >
        <v-chip
            active-class="blue--text"
            small
            :input-value="active"
            @click="toggle"
        >
          {{ ['k','l','t'][n] }}
        </v-chip>
      </v-item>
    </v-item-group>
    <svg ref="svg" style="height: 300px; width: 100%;"
         :viewBox="`${-width / 2}, ${-height / 2}, ${width}, ${height}`">
      <g id="treeGroup"></g>
      <foreignObject id="tips" hidden="true" x="0" y="0" width="500" height="300">
        <div style="background-color: #ffffff; opacity: 70%; font-size: 30px; border: 2px solid; border-radius: 20px;">
          asdfafasefasfeas
        </div>
      </foreignObject>
    </svg>
    <v-slider
        v-model="threshold"
        :max="max"
        :color="colorMin"
        :track-color="colorMax"
        min="1"
        hide-details
    >
      <template v-slot:prepend>
        <v-menu
            :close-on-content-click="false"
            offset-y>
          <template v-slot:activator="{ on, attrs }">
            <v-btn
                icon
                color="primary"
                dark
                v-bind="attrs"
                v-on="on"
            >
              <v-icon>mdi-eyedropper-variant</v-icon>
            </v-btn>
          </template>
          <v-color-picker
              v-model="colorMin"
              hide-canvas
          ></v-color-picker>
          <v-color-picker
              v-model="colorMax"
              hide-canvas
          ></v-color-picker>
        </v-menu>
      </template>
      <template v-slot:append>
        <v-text-field
            v-model="threshold"
            class="mt-0 pt-0"
            hide-details
            single-line
            type="number"
            style="width: 60px"
        ></v-text-field>
      </template>
    </v-slider>
  </div>

</template>

<script>
import * as d3 from 'd3'
import {mapState} from "vuex";
export default {
  name: "GlobalTree",
  data(){
    return {
      threshold: 0,
      width: 1080,
      height: 800,
      mode: 0,
      colorMin: "#E82424FF",
      colorMax: "#1C9DF3FF",
    }
  },
  props:{
    treeRoot: Object
  },
  computed:{
    ...mapState([
      'headers',
      'schema',
      'mapSchema',
      'filters',
    ]),
    max(){
      return this.parseData ? d3.max(this.parseData, d=>this.temp(d))[0] : 0
    },
    parseData(){
      return this.parseTree(this.treeRoot, [], 0, 2 * Math.PI, 50, "A", 0)
    },
  },
  methods: {
    getType(type, index){
      return this.mapSchema[type].y[index]
    },
    parseTree(root, data, sA, eA, sR, parentID, dim){
      let _this = this;
      if(!root) return null
      let children = root.children
      let b = 0;
      let sum = 0
      children.forEach(child => {
        b += child.branch
        sum += child.count
      })
      let st = sA
      b = (eA - sA) / b
      sum = 20 / sum
      children.forEach(child => {
        let x0 = st
        let x1 = st + b * child.branch
        let y0 = sR
        let y1 = sR + 20 + sum * child.count
        let id = `${parentID}-${child.labels}`

        let pass = false
        let s = _this.schema[dim]
        let f = _this.filters[s]

        if (_this.headers[s].type === 0) {
          pass = true
        } else {
          for (let label of child.labels) {
            if (f.indexOf(_this.mapSchema[s].y[label]) >= 0) {
              pass = true
              break
            }
          }
        }

        if (pass) {
          data.push({x0,x1,y0,y1,id,k: child.content.k, l: child.content.l, t: child.content.t})
          this.parseTree(child, data, x0, x1, y1, id, dim + 1)
        }

        st = x1
      })
      return data
    },
    temp(d) {
      if(this.mode === 0) return d.k;
      if(this.mode === 1) return d.l;
      if(this.mode === 2) return d.t;
    },
    reRender() {
      if(!this.treeRoot) return

      let linear = d3.scaleLinear().domain([0, this.threshold]).range([0, 1]).clamp(true);
      let compute = d3.interpolateRgb(this.colorMin, this.colorMax)

      let _this = this;
      let arc = d3
          .arc()
          .startAngle(d => d.x0)
          .endAngle(d => d.x1)
          .padAngle(1 / 200)
          .padRadius(200)
          .innerRadius(d => d.y0 + 1)
          .outerRadius(d => d.y1 - 1)

      d3.select(this.$refs.svg).select('#treeGroup')
          .selectAll(".tree")
          .data(this.parseData)
          .join("path")
          .attr("id", d => d.id)
          .attr("class", "tree")
          .attr("d", arc)
          .attr("fill", d=>{
            if (d.id.split(',').length > 1) return 'gray'
            return compute(linear(this.temp(d)))
          })
          .on("mouseover", function(e, d){
            let pos = d3.pointer(e)
            d3.select(this).attr("fill", 'lightGreen')
            let a = d.id.split("-");
            a = a.slice(1, a.length)
            console.log(a);
            a = a.map((d,i)=>{
              let result
              if (_this.headers[_this.schema[i]].type === 0) {
                result = _this.getType(_this.schema[i], parseInt(d))
                result = `[${result.min}-${result.max}]`
              } else {
                let fuzzy = d.split(',')
                result = ''
                for (let j = 0; j < fuzzy.length; j++) {
                  result += _this.getType(_this.schema[i], parseInt(fuzzy[j]))
                  if (j < fuzzy.length - 1) {
                    result += '|'
                  }
                }
              }
              return `${_this.headers[_this.schema[i]].value}(${result})`;
            })
            let str = ''
            for (let i = 0; i < a.length; i++) {
              str += a[i]
              if (i !== a.length - 1){
                str += '-'
              }
            }
            d3.select(_this.$refs.svg).select('#tips')
                .attr('hidden', null)
                .attr('x', pos[0] - 250)
                .attr('y', pos[1] - 80)
                .select('div')
                .text(str)
          })
          .on("mouseleave", function(e, d){
            d3.select(this).attr("fill", ()=>{
              if (d.id.split(',').length > 1) return 'gray'
              return compute(linear(_this.temp(d)))
            })
            d3.select(_this.$refs.svg).select('#tips').attr('hidden', true)
          })
    },
  },
  mounted() {
    this.reRender();
  },
  watch: {
    treeRoot(){
      this.reRender()
    }
  },
  updated() {
    this.reRender()
  }
}
</script>

<style scoped>

</style>