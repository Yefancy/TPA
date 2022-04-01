<template>
  <div ref="agg"
       :style="{height: `${divHeight}px`}"
  >
    <template v-if="isExpand">
      <template v-if="root.children.length > 0">
        <template v-for="(item, index) in renderList">
          <div class="position-relative" :key="item.key">
            <AggregateNode
                :class="{highlight: item.isOver}"
                :schema="schema"
                :dim="dim + 1"
                :root="item.root"
                :view-off="item.offset"
                ref="children"
                @aggHeight="onAggHeight(item.key, $event)"
            ></AggregateNode>

            <div v-if="dim !== 0" class="position-absolute w" style="top: 0;"
                 :style="{left: `${dim * 196 - 8}px`, }"
                 :class="{agg: draggable}"
                 @dragstart="draggable = true; $event.dataTransfer.setData('index', index)"
                 @dragend="draggable = false"
                 @dragenter="aggDragEnter"
                 @dragleave="aggDragLeave"
                 @dragover="aggDragOver"
                 @drop="aggDrop"
                 draggable="true"
            >
              <svg :height="aggHeight[item.key]" class="agg-line"
                   @mouseenter="item.isOver = true"
                   @mouseleave="item.isOver = false"
                   @click="$refs.children[index].expand()"
              >
                <line x1="8" y1="6" x2="14" y2="6"></line>
                <line x1="8" y1="6" x2="8" :y2="aggHeight[item.key] - 7"></line>
                <line x1="8" :y1="aggHeight[item.key] - 7" x2="14" :y2="aggHeight[item.key] - 7"></line>
              </svg>
            </div>
          </div>
        </template>
      </template>
      <template v-else>
        <DataRow v-for="(row) in root.content.d" :schema="schema" :key="row" :r-data="rD(row)"
                 class="list-item"></DataRow>
      </template>
    </template>
    <template v-else>
      <DataGroupRow :schema="schema" :group="root.content.d.map(d=>tData[d])"></DataGroupRow>
    </template>
  </div>
</template>

<script>
import {mapGetters, mapState} from 'vuex'
import DataRow from "@/components/Sheet/Row/DataRow";
import DataGroupRow from "@/components/Sheet/Row/DataGroupRow";

export default {
  name: "AggregateNode",
  components: {DataGroupRow, DataRow},
  data() {
    return {
      isExpand: true,
      isOver: false,
      divHeight: 0,
      aggHeight: [],
      renderList: [],
      draggable: false,
      mouseState: 0,
    }
  },
  props: {
    schema: Array,
    dim: Number,
    root: Object,
    viewOff: Number,
  },
  computed: {
    ...mapGetters([
      'tData',
    ]),
    ...mapState([
      'headers',
      'mapSchema',
    ])
  },
  methods: {
    aggDrop(e) {
      console.log(e.dataTransfer.getData("index"));

    },
    aggDragOver(e) {
      e.preventDefault();
    },
    aggDragLeave(e) {
      if (this.draggable) {
        e.target.style.borderColor = "crimson"
      }
    },
    aggDragEnter(e) {
      if (this.draggable) {
        e.target.style.borderColor = "black"
      }
    },
    updateRenderList() {
      this.renderList.length = 0
      let offset = this.viewOff
      for (let i = 0; i < this.root.children.length; i++) {
        if (offset > 1200) continue
        this.renderList.push({
          isOver: false,
          key: i,
          root: this.root.children[i],
          offset,
        })
        offset += this.aggHeight[i]
      }
    },
    expandAttribute(result, dim) {
      if (dim === this.dim - 1) {
        this.expand(result)
      } else if (this.root.children.length > 0) {
        this.$refs.children.forEach(child => child.expandAttribute(result, dim))
      }
    },
    updateHeight() {
      if (this.isExpand) {
        let sum = 0
        if (this.root.children.length > 0) {
          for (let i = 0; i < this.root.children.length; i++) {
            if (!this.aggHeight[i]) {
              this.aggHeight[i] = this.root.children[i].count * 22
            }
            sum += this.aggHeight[i]
          }
        } else {
          sum += (22 * this.root.content.d.length)
        }
        this.divHeight = sum
      } else {
        this.divHeight = 22;
      }
      this.updateRenderList();
      this.$emit("aggHeight", this.divHeight)
    },
    onAggHeight(index, height) {
      this.aggHeight[index] = height
      this.updateHeight()
    },
    expand(result) {
      if (this.isExpand === result) return
      this.isExpand = !this.isExpand
      this.updateHeight()
    },
    rD(row) {
      return this.tData[row]
    },
  },
  created() {
    this.updateHeight()
  },
  watch: {
    schema() {
      this.updateHeight()
    },
    viewOff() {
      this.updateRenderList()
    }
  }
}
</script>

<style scoped>
.agg-line {
  width: 16px;
  stroke-width: 2px;
  stroke: gray;
}

.agg-line:hover {
  stroke: black;
  transition: 1s stroke ease;
}

.highlight {
  background-color: rgba(69, 69, 69, 0.41);
  transition: 1s background-color ease;
}

.agg {
  border-style: dashed;
  border-width: 2px;
  border-color: crimson;
  /*box-shadow: 0 0 0 2px rgb(255, 102, 0);*/
}
</style>