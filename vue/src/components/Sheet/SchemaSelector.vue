<template>
  <div class="d-flex">
    <div class="frame p-2 w-50">
      <draggable v-model="left" group="schema" animation="200" :emptyInsertThreshold="500" ghostClass="ghost" chosenClass="chosen" @change="onChange">
        <transition-group>
          <v-chip class="item" v-for="(item, index) in left" :key="item.id"
                  :class="{sensitive:item.sensitive}"
                  close
                  @click:close="remove(index)"
                  @click="item.sensitive = ! item.sensitive; onChange();"
          >{{item.name}}</v-chip>
        </transition-group>
      </draggable>
    </div>
    <v-btn class="ma-2" dark @click="moveAll">
      <v-icon dark left>mdi-arrow-left</v-icon>All
    </v-btn>
    <div class="frame p-2 w-50">
      <v-chip class="item" v-for="(item, index) in right" :key="item.id" @click="move(index)">{{item.name}}</v-chip>
    </div>

  </div>
</template>

<script>
import draggable from 'vuedraggable'
export default {
  components: {
    draggable,
  },
  created() {
    let headers = this.$store.state.headers;
    for (let i = 0; i < headers.length; i++) {
      this.right.push({
        id: i,
        name: headers[i].text,
        sensitive: false,
      })
    }
  },
  data() {
    return {
      left:[],
      right:[],
    };
  },
  methods: {
    move(index){
      let obj = this.right[index]
      this.$delete(this.right, index)
      this.left.push(obj)
      this.onChange()
    },
    moveAll(){
      this.$set(this, 'left', this.left.concat(this.right))
      this.$set(this, 'right', [])
      this.onChange()
    },
    onChange(){
      let sensitive = []
      this.left.forEach(d=>d.sensitive && sensitive.push(d.id))
      this.$emit('change', this.left.map(d=>d.id), sensitive)
    },
    remove(index){
      let obj = this.left[index]
      this.$delete(this.left, index)
      this.right.push(obj)
      this.right.sort((a,b) => a.id - b.id)
      this.onChange()
    },
  },
  name: "SchemaSelector"
}
</script>

<style scoped>
.frame{
  /*background-color: rgb(246, 0, 0);*/
  border-style: solid;
  border-color: #848484;
  border-width: 1px;
  border-radius: 10px;
}
.chosen{
}
.ghost{
  background-color: pink !important;
}
.item{
  margin-left: 3px;
}
.sensitive{
  background-color: #868686 !important;
}
</style>