<template>
  <div class="nothing">
    <div  class="img-box">
      <img src="./images/nothing.png">
      <div>暂无任何权限~</div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'index',
  data() {
    return {}
  },
  created() {
  },
  mounted() {
  },
  methods: {}
}
</script>

<style scoped lang="scss">
.nothing {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;

  .img-box {
    //width: 50%;
    //height: 50%;
    div{
      text-align: center;
      font-size: 18px;
      color:#575757;
    }
  }
}

</style>
