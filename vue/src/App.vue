<template>
  <v-app id="app" onselectstart="return false">
    <link href="https://fonts.googleapis.com/css?family=Roboto:100,300,400,500,700,900" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/@mdi/font@4.x/css/materialdesignicons.min.css" rel="stylesheet">
    <v-tabs fixed-tabs dark style="flex: none" color="#FFC0CBFF">
      <v-tab @click="route('/upload')">Import</v-tab>
      <v-tab @click="route('/sheet')">Privacy Analysis</v-tab>
<!--      <v-tab @click="route('/utility')">Utility</v-tab>-->
      <v-tab>Export</v-tab>
    </v-tabs>
    <router-view></router-view>
  </v-app>
</template>

<script>

export default {
  name: 'App',
  components: {},
  methods: {
    route(path){
      if (this.$route.path === path) return
      this.$router.push(path)
    }
  }
}
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}
.solid{
  border-style: solid;
  border-color: red;
}
</style>
