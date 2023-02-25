import Vue from "vue";
/**
 * 默认零点五秒截流函数
 */
const throttle =  function(func,time) {
  let timer = this.timer;
  if(timer) {
    clearTimeout(timer)
    timer = null
  }
  this.timer = setTimeout(func,time || 500)
}

Vue.prototype.$throttle = throttle;

export default throttle;
