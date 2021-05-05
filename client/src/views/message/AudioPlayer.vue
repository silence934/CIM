<template>
  <div @click="click" style="width: 60px;height: 30px;padding-left: 15px">

      <div :class="isLeft?'wifi-symbol-left':'wifi-symbol-right'">
        <div class="wifi-circle first"></div>
        <div class="wifi-circle second" :class="status?'play-second':''"></div>
        <div class="wifi-circle third" :class="status?'play-third':''"></div>
      </div>

    <span :class="isLeft?'time-left':'time-right'">    {{length}}s </span>
  </div>
</template>

<script>
export default {
  name: "AudioPlayer",
  props: {
    src: {
      type: String,
      default: ''
    },
    isLeft:{
      type:Boolean,
      default: false
    }
  },
  data(){
    return{
      audio:new Audio(),
      status:false,
      length:0
    }
  },
  methods:{
    init(){
      this.audio.src = this.src
      this.audio.addEventListener("playing",()=>{
        this.status=true
      })
      this.audio.addEventListener("pause",()=>{
        this.status=false
      })
      this.audio.oncanplay = ()=>{
        this.length=Math.round(this.audio.duration)
      }
      this.audio.load()

    },
    click(){
      if (this.audio.paused){
        this.audio.currentTime = 0
        this.audio.play()
      }else {
        this.audio.pause()
      }
    }
  },
  created() {
    this.init()
  }
}
</script>

<style scoped>
  .wifi-symbol-right {
    width: 30px;
    height: 30px;
    box-sizing: border-box;
    overflow: hidden;
    transform: rotate(315deg);
  }

  .wifi-symbol-left {
    width: 30px;
    height: 30px;
    box-sizing: border-box;
    overflow: hidden;
    transform: rotate(135deg);
  }

  .wifi-circle {
    border: 3px solid #999999;
    border-radius: 50%;
    position: absolute;
  }

  .first {
    width: 3px;
    height: 3px;
    background: #cccccc;
    top: 27px;
    left: 27px;
  }

  .second {
    width: 18px;
    height: 18px;
    top: 21px;
    left: 21px;
  }

  .third {
    width: 28px;
    height: 28px;
    top: 16px;
    left: 16px;
  }

  .play-second{
    animation: fadeInOut 1s infinite 0.2s;
  }
  .play-third{
    animation: fadeInOut 1s infinite 0.4s;
  }

  @keyframes fadeInOut {
    0% {
      opacity: 0; /*初始状态 透明度为0*/
    }
    100% {
      opacity: 1; /*结尾状态 透明度为1*/
    }
  }

  .time-right{
    position: absolute;
    top:10px;
    left: 10px
  }

  .time-left{
    position: absolute;
    top:10px;
    left: 28px
  }

</style>
