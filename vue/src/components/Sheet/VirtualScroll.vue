<template>
  <div class="virtual-scroll" :style="{height: `${height}px`}" @scroll="onScroll">
    <div class="virtual-scroll__container" :style="{height: `${items.length * __itemHeight}px`}">
      <slot name="global"></slot>
      <div class="virtual-scroll__item"
           v-for="(item, index) in renderList"
           :key="index + firstToRender"
           :style="{top: `${(index + firstToRender) * __itemHeight}px`}"
      >
        <slot :item="item"></slot>
      </div>
    </div>
  </div>
</template>

<script>

export default {
  name: "VirtualScroll",
  data(){
    return {
      first: 0,
      last: 0,
      scrollTop: 0,
    }
  },
  props: {
    height: String,
    bench: {
      type: [Number, String],
      default: 0,
    },
    itemHeight: {
      type: [Number, String],
      required: true,
    },
    items: {
      type: Array,
      default: () => [],
    }
  },
  computed: {
    renderList(){
      return this.items.slice(
          this.firstToRender,
          this.lastToRender,
      )
    },
    __bench (){
      return 10
    },
    __itemHeight () {
      return parseInt(this.itemHeight, 10)
    },
    firstToRender () {
      return Math.max(0, this.first - this.__bench)
    },
    lastToRender () {
      return Math.min(this.items.length, this.last + this.__bench)
    },
  },
  mounted () {
    this.last = this.getLast(0)
  },
  methods: {
    getFirst () {
      return Math.floor(this.scrollTop / this.__itemHeight)
    },
    getLast (first) {
      const height = parseInt(this.height || 0, 10) || this.$el.clientHeight

      return first + Math.ceil(height / this.__itemHeight)
    },
    onScroll () {
      this.scrollTop = this.$el.scrollTop
      this.first = this.getFirst()
      this.last = this.getLast(this.first)
    },
  },
}
</script>

<style scoped>
.virtual-scroll {
  display: block;
  flex: 1 1 auto;
  height: 100%;
  max-width: 100%;
  overflow: auto;
  position: relative;
}
.virtual-scroll__item {
  left: 0;
  position: absolute;
  right: 0;
}
.virtual-scroll__container {
  display: block;
}
</style>