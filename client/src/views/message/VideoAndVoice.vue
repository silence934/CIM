<template>
    <div>
        <div v-if="localstream">
            <video src="" id="rtcA" controls autoplay></video>
            <video src="" id="rtcB" controls autoplay></video>
        </div>
        <div v-else>
            <img style="height: 100%;" src="@/assets/wait.jpg" alt=""/>
        </div>
    </div>
</template>

<script>
import moment from "moment"
import {mapGetters} from "vuex"

export default {
    name: "VideoAndVoice",
    computed: {
        ...mapGetters([
            'userId', 'chat', 'token', 'name', 'avatar'
        ]),
    },
    sockets: {
        async receiveEvent(message) {
            if (message.type === 'VIDEO') {
                switch (message.data) {
                    case 'accept':
                        this.startTime = new Date()
                        // 对方同意之后创建自己的 peer
                        await this.createMedia();
                        // 并给对方发送 offer
                        await this.createOffer();
                        break;
                    case 'busy': // 正在通话中
                        this.$message({
                            message: '对方正在通话中！',
                            type: 'warning'
                        });
                        break;
                    case 'offer': //offer
                        await this.onOffer(message);
                        break;
                    case 'answer':
                        await this.onAnswer(message);
                        break;
                    case 'ice' :
                        await this.onIce(message);
                        break;
                }
            }
        }
    },
    props: {
        nowActive: {
            type: Object,
            default: {}
        },
        isVideo: {
            type: Boolean,
            default: false
        }
    },
    data() {
        return {
            offerOption: {
                offerToReceiveAudio: 1,
                offerToReceiveVideo: 1
            },
            peer: null,
            PeerConnection: window.RTCPeerConnection || window.mozRTCPeerConnection || window.webkitRTCPeerConnection,
            dialogVideo: false,
            startTime: '',
            localstream: '',
        }
    },
    methods: {
        sendMessage(type, data, other) {
            let msg = {
                type: type,
                from: this.userId,
                data: data,
                other: JSON.stringify(other),
                to: parseInt(this.nowActive.id),
                time: moment().format('YYYY-MM-DD HH:mm:ss')
            }
            this.$socket.emit('sendEvent', msg)
        },
        async createMedia() {
            this.localstream = true
            // 保存本地流到全局
            try {
                await navigator.mediaDevices.getUserMedia({audio: true, video: this.isVideo}).then(success => {
                    this.localstream = success;
                    document.querySelector('#rtcB').srcObject = this.localstream;
                    console.log("视频加载完毕");
                }).catch((error) => {
                    alert("访问用户媒体设备失败：" + error.name + error.message);
                });
            } catch (e) {
                console.log('getUserMedia: ', e)
            }
            // 获取到媒体流后，调用函数初始化 RTCPeerConnection
            this.initPeer();
        },
        initPeer() {
            // 创建输出端 PeerConnection
            let PeerConnection = window.RTCPeerConnection || window.mozRTCPeerConnection || window.webkitRTCPeerConnection
            this.peer = new PeerConnection()
            this.peer.addStream(this.localstream)
            // 监听ICE候选信息 如果收集到，就发送给对方
            this.peer.onicecandidate = (event) => {
                if (event.candidate) {
                    this.sendMessage('VIDEO', 'ice', event.candidate)
                }
            }
            this.peer.onaddstream = (event) => {
                // 监听是否有媒体流接入，如果有就赋值给 rtcB 的 src
                console.log('有数据流');
                let video = document.querySelector('#rtcA');
                video.srcObject = event.stream;
            }
        },
        async createOffer() {
            try {
                // 创建offer
                let offer = await this.peer.createOffer(this.offerOption);
                // 呼叫端设置本地 offer 描述
                await this.peer.setLocalDescription(offer);
                // 给对方发送 offer
                this.sendMessage('VIDEO', 'offer', offer)
            } catch (e) {
                console.log('createOffer: ', e);
            }
        },
        async onOffer(data) {
            // 接收offer并发送 answer
            try {
                // 接收端设置远程 offer 描述
                await this.peer.setRemoteDescription(JSON.parse(data.other))
                // 接收端创建 answer
                let answer = await this.peer.createAnswer();
                // 接收端设置本地 answer 描述
                await this.peer.setLocalDescription(answer);
                // 给对方发送 answer
                this.sendMessage('VIDEO', 'answer', answer);
            } catch (e) {
                console.log('onOffer: ', e);
            }
        },
        async onAnswer(data) {
            // 接收answer
            try {
                // 呼叫端设置远程 answer 描述
                await this.peer.setRemoteDescription(JSON.parse(data.other))
            } catch (e) {
                console.log('onAnswer: ', e);
            }
        },
        async onIce(data) { // 接收 ICE 候选
            try {
                // 设置远程 ICE
                await this.peer.addIceCandidate(JSON.parse(data.other))
            } catch (e) {
                console.log('onAnswer: ', e);
            }
        },
        closeVideo(done) {
            done();
            this.sendMessage('VIDEO', 'disconnect')
            this.disconnectVideo()
        },
        disconnectVideo() {
            if (this.localstream) {
                this.localstream.getTracks().forEach(track => track.stop())
            }
            if (this.peer) {
                this.peer.close()
            }
            if (this.startTime) {
                let input = '视频通话 ' + (new Date() - this.startTime) / 1000 + 's'
                this.$emit("sendVideoInfo", input);
            }
            this.localstream = ''
            this.startTime = ''
        },
    }
}
</script>

<style scoped>

</style>
