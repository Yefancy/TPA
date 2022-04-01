<template>
  <div class="stock">
    <svg ref="svg" :width="width" :height="height">
      <rect :width="width * (value / max)" :height="height" fill="lightblue"></rect>
    </svg>
  </div>
</template>

<script>
export default {
  name: "NumberStock",
  props: {
    value: Number,
    max: Number,
    width: Number,
    height: Number,
  }
}
</script>

<style scoped>
@import "../stock.css";
</style>