<template>
  <div style="position: relative; box-shadow: 0 0 0 2px rgba(116,161,241,0.62);" class="ma-1" :style="{
    height: height + 'px',
    width: width + 'px',
    backgroundColor: innerColor
  }">

    <div style="left:25%; top: 25%; position: absolute" :style="{
      height: height / 2 + 'px',
      width: width / 2 + 'px',
      backgroundColor: outlineColor
    }">
    </div>
  </div>
</template>

<script>
export default {
  name: "UtilityElement",
  props: {
    innerColor: String,
    outlineColor: String,
    width: Number,
    height: Number,
  }
}
</script>

<style scoped>

</style>