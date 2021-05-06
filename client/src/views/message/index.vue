<template>
    <div style="height: 100vh;">
        <audio muted ref="audio" loop="loop" src="@/assets/video.wav"/>

        <centerControl style="position: relative;left: 0">
            <char-list :defaultActive="defaultActive"  @open="open"></char-list>
        </centerControl>

        <el-main class="main" >
            <el-header style="height: 30px">
                {{ nowActive.remark || nowActive.nickname || nowActive.username }}
                <el-divider/>
            </el-header>
            <el-main  style="height: calc(100% - 30px);padding: 0">
                <message-list ref="messageList"
                              :msgList="msgList"
                              :nowActive="nowActive"/>
                <el-button v-if="nowActive.id>9999" @click="drawer = true"
                           icon="el-icon-d-arrow-left" circle
                           type="primary" style="position: absolute;right: 0;top: 50%">
                </el-button>
            </el-main>
        </el-main>

        <el-footer v-if="nowActive.id&&nowActive.type!=='ADD_FRIEND'">
            <quill-editor
                    @keyup.enter.native="send"
                    v-model="input"
                    ref="myQuillEditor"
                    :options="editorOption"/>
            <div v-if="nowActive.id<10000">
                <button type="button" class="ql-underline video" @click="isVideo=true;video()">
                    <i class="el-icon-video-camera "></i>
                </button>
                <button type="button" class="ql-underline video" @click="voiceShow=true;" style="left: 375px">
                    <i class="el-icon-microphone"></i>
                </button>
                <Voice v-if="voiceShow" class="voice" :nowActive="nowActive"
                       @close="voiceShow=false"
                       @sendVideoInfo="sendVideoInfo"/>
            </div>
            <el-button @click="send()" style="float: right" size="mini">发送</el-button>
        </el-footer>

        <el-drawer :visible.sync="drawer" direction="rtl">
            <div style="height: 100%;width: 100%;padding: 10px">
                <div style="max-height: 30%;overflow: hidden">
                    <h3>群公告</h3>
                    <span>
                        {{ crowdInfo.announcement }}
                    </span>
                </div>
                <div style="max-height: 70%;padding-top: 10px">
                    <h3>群成员({{ crowdInfo.users.length }})</h3>
                    <div v-for="(item,index) in crowdInfo.users" :key="index" class="crowdUser">
                        <el-image :src="item.avatar" style="height: 30px;width: 30px"></el-image>
                        <span style="vertical-align: 10px">{{ item.nickname || item.username }}</span>
                    </div>
                </div>
            </div>
        </el-drawer>

        <el-dialog :modal="false"
                   width="770px"
                   title="视频通话"
                   :visible.sync="dialogVideo"
                   :close-on-click-modal="false"
                   class="video-dialog"
                   :before-close="closeVideo">
            <video-and-voice ref="videoAndVoice"
                             @closeDialog="dialogVideo=false"
                             @sendVideoInfo="sendVideoInfo"
                             :nowActive="nowActive"
                             :isVideo="isVideo"
            />
        </el-dialog>

        <el-dialog :modal="false" width="450px"
                   :visible.sync="dialogRequest"
                   :close-on-click-modal="false"
                   class="video-dialog"
                   right>
            <h2 style="color: #909399;text-align: center">视频消息,是否接听?</h2>
            <span slot="footer" class="dialog-footer">
                <el-button size="medium" @click="rejectVideo">拒 绝</el-button>
                <el-button size="medium" type="primary" @click="acceptVideo">接 受</el-button>
              </span>
        </el-dialog>
    </div>
</template>

<script>
import {mapGetters} from 'vuex'
import moment from 'moment'
import {addFriend, getDetails, getGroup} from '@/api/user'
import {getCrowdInfo} from "@/api/crowd"
import centerControl from '../../components/CenterControl'
import VideoAndVoice from "@/views/message/VideoAndVoice"
import Voice from "./Voice"
import AudioPlayer from "./AudioPlayer"

import { Quill} from 'vue-quill-editor'
import { ImageExtend, QuillWatch} from 'quill-image-extend-module'
import quillEmoji from 'quill-emoji'
import 'quill-emoji/dist/quill-emoji.css'
import CharList from "@/views/message/CharList";
import MessageList from "@/views/message/MessageList";
Quill.register('modules/quillEmoji', quillEmoji)
Quill.register('modules/ImageExtend', ImageExtend)

export default {
    name: 'index',
    components: {MessageList, CharList, Voice, VideoAndVoice, centerControl,AudioPlayer},
    sockets: {
        async receiveEvent(message) {
            let newChat = this.messageToChat(message);
            if (newChat.id === parseInt(this.nowActive.id) && message.type === 'VIDEO') {
                switch (message.data) {
                    case 'request': {
                        this.$refs.audio.currentTime = 0
                        this.$refs.audio.play()
                        this.dialogRequest = true
                        break
                    }
                    case 'disconnect': {
                        this.$refs.videoAndVoice.disconnectVideo()
                        this.dialogVideo = false
                        break
                    }
                    case 'reject': {
                        clearTimeout(this.videoTimeout)
                        this.hasAnswer = true
                        this.$message({
                            message: '对方拒绝了你的请求！',
                            type: 'warning'
                        });
                        this.dialogVideo = false
                        this.sendVideoInfo('视频通话 已拒绝')
                        break;
                    }
                    case 'accept': {
                        clearTimeout(this.videoTimeout)
                        this.hasAnswer = true
                        break;
                    }
                    case 'cancel': {
                        this.$refs.audio.pause()
                        this.dialogRequest = false
                        break;
                    }
                }
            } else {
                if (newChat.id === parseInt(this.nowActive.id)) {
                    this.msgList.push(message)
                    await this.$store.dispatch('chat/updateList', newChat)
                } else {
                    let chat = this.chat.filter(i => i.id === newChat.id)
                    if (chat.length === 0) {
                        if (newChat.type==='ADD_FRIEND'){
                            newChat.avatar = require('@/assets/bell.png')
                            newChat.badge = 1
                            newChat.nickname = '新朋友'
                        }else {
                            await getDetails({id: newChat.id}).then(res => {
                                newChat.avatar = res.data.avatar
                                newChat.badge = 1
                                newChat.nickname = res.data.nickname
                            })
                        }
                    } else {
                        newChat.avatar = chat[0].avatar
                        newChat.nickname = chat[0].nickname
                        newChat.badge = chat[0].badge + 1
                    }
                    await this.$store.dispatch('chat/addList', newChat)
                }
            }
        }
    },
    data() {
        return {
            defaultActive:'',
            nowActive: {},
            nickname: '',
            input: '',
            msgList: [],
            editorOption: {},
            dialogVideo: false,
            /*群*/
            crowdInfo: {
                announcement: '',
                avatar: "",
                id: 0,
                lordId: 0,
                name: "",
                users: [{
                    avatar: "",
                    id: 1,
                    mail: "",
                    nickname: "",
                    phone: "",
                    sex: "",
                    username: ""
                }]
            },
            drawer: false,
            hasAnswer: false,
            videoTimeout: '',
            dialogRequest: false,
            isVideo: true,
            voiceShow: false
        }
    },
    computed: {
        ...mapGetters([
            'userId', 'chat', 'token', 'name', 'avatar', 'onlineUser'
        ]),
        userData() {
            return this.$route.query
        }
    },
    watch: {
        hasAnswer(value) {
            if (value) {
                this.$refs.audio.pause()
            }
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
        open(item) {
            if (this.nowActive.id !== item.id ) {
                this.active(item)
                this.$nextTick(()=>{
                    this.$refs.messageList.getMsgList()
                })
            }
            this.tagMessage();
        },
        send(type) {
            if (this.input) {
                this.input = this.input.replace(/<p>/g, '')
                this.input = this.input.replace(/<\/p>/g, '')
                if (!type||typeof type!=="string") {
                    type = (this.input.indexOf('<img src=') === -1 ? 'GENERAL' : 'IMG')
                }

                let time = moment().format('YYYY-MM-DD HH:mm:ss')

                let message = {
                    from: this.userId,
                    to: parseInt(this.nowActive.id),
                    time: time,
                    data: this.input,
                    type: type?type:'GENERAL',
                    showTime: this.msgList.length === 0 || !moment(time).diff(moment(this.msgList[this.msgList.length - 1].time), 'minutes') < 1
                }

                this.$store.dispatch('chat/updateList',
                    {id: message.to, type: type, time: message.time, msg: this.messageSwitch(message)})

                this.msgList.push(message)
                this.$socket.emit('sendEvent', message)
                this.$refs.messageList.scrollToBottom()
                this.input = ''
            } else {
                this.$message.info('消息不能为空')
            }
        },
        active(nowActive) {
            console.log("激活了:", nowActive)
            this.nowActive = nowActive
            if (nowActive.id > 9999) {
                this.getCrowdInfo(nowActive.id)
            }
        },
        init() {
            if (this.userData.id) {
                let chatListHasUser = this.chat.filter(e => e.id === parseInt(this.userData.id))[0]
                if (!chatListHasUser) {
                    chatListHasUser = {
                        badge: 0,
                        time: moment().format('YYYY-MM-DD HH:mm:ss'),
                        id: parseInt(this.userData.id),
                        type: 'GENERAL'
                    }
                    console.log("chatListHasUser",chatListHasUser)
                    this.$store.dispatch('chat/addList', chatListHasUser)
                }
                this.defaultActive=''+chatListHasUser.id
                this.$router.push({ query: {} });
            }
        },
        messageSwitch(message) {
            if (message.type === 'IMG') {
                return '图片'
            } else if (message.type === 'VIDEO') {
                return '视频通话'
            } else if (message.type === 'VOICE') {
                return '语音消息'
            } else {
                let a = message.data.replace(/<.*?>/ig, '')
                return a.substring(0, 7) + (a.length > 7 ? '...' : '')
            }
        },

        video() {
            this.sendMessage('VIDEO', 'request')
            this.dialogVideo = true
            this.hasAnswer = false
            this.$refs.audio.currentTime = 0
            this.$refs.audio.play()
            this.videoTimeout = setTimeout(() => {
                if (!this.hasAnswer) {
                    this.cancelVideo()
                    this.$message.warning("对方无应答")
                }
            }, 15000)
        },
        closeVideo(done) {
            if (!this.hasAnswer) {
                this.cancelVideo()
                clearTimeout(this.videoTimeout)
                this.$message.info("已取消")
            } else {
                this.sendMessage('VIDEO', 'disconnect')
                this.$refs.videoAndVoice.disconnectVideo()
            }
            this.hasAnswer = false
            done();
        },
        cancelVideo() {
            this.$refs.audio.pause()
            this.sendMessage('VIDEO', 'cancel')
            this.sendVideoInfo('视频通话 已取消')
            this.dialogVideo = false
        },
        getCrowdInfo(crowdId) {
            getCrowdInfo({id: crowdId}).then(res => {
                this.crowdInfo = res.data
            })
        },
        tagMessage() {
            let data = {
                from: this.userId,
                to: parseInt(this.nowActive.id),
                time: moment().format('YYYY-MM-DD HH:mm:ss'),
            }
            this.$socket.emit('tagMessage', data)
        },
        messageToChat(message) {
            let id
            if (message.type==='ADD_FRIEND'){
                id=-1
            } else if (message.to === this.userId) {
                id=message.from
            } else {
                id=message.to
            }
            return {id: id, type: message.type, time: message.time, msg: this.messageSwitch(message)}
        },
        sendVideoInfo(data, type) {
            this.input = data
            if (type){
              this.send(type)
            }else {
              this.send()
            }
        },
        acceptVideo() {
            this.$refs.audio.pause()
            this.dialogRequest = false
            this.dialogVideo = true
            this.hasAnswer = true
            // 同意之后创建自己的 peer 等待对方的 offer
            this.$nextTick(() => {
                this.$refs.videoAndVoice.createMedia()
            })
            this.sendMessage('VIDEO', 'accept')
        },
        rejectVideo() {
            this.$refs.audio.pause()
            this.dialogRequest = false
            this.sendMessage('VIDEO', 'reject')
        },
        compareChat(chat){
            if (chat.id === parseInt(this.nowActive.id)){
                if (chat.type==='ADD_FRIEND'){
                    return this.nowActive.type==='ADD_FRIEND'
                }else {
                    return this.nowActive.type!=='ADD_FRIEND'
                }
            }
           return  false
        }
    },
    created() {
        this.init()
        this.editorOption={
            placeholder: '请输入内容',
            modules: {
              ImageExtend: {
                loading: true,
                size: 3,
                name: 'file',
                action: '/api-v1/artifact/upload',
                methods: 'POST',
                response: (res) => {
                  return '/proxy/api-v1/artifact' + res.data.path
                },
                change: (xhr, formData) => {
                  xhr.setRequestHeader('Authorization', 'Bearer ' + this.token)
                  formData.append('type', 'chat')
                },
              },
              'emoji-toolbar': true,
              'emoji-shortname': true,
              toolbar: {
                container: ['image', 'bold', 'italic', 'underline', 'strike', 'blockquote', 'code-block',
                  'emoji', {'header': 1}, {'header': 2}, {'list': 'ordered'}, {'list': 'bullet'}, {'color': []}],
                handlers: {
                  'image':  ()=> {
                    QuillWatch.emit(this.quill.id)
                  }
                }
              },
            }
        }
    },
}
</script>

<style lang="scss" scoped>

  .search {
      background-color: rgb(231, 230, 229);
      padding: 10px;
      height: 48px;
      width: 250px;
  }

  > > > .el-drawer {
      width: 400px !important;

      .el-drawer__header {
          display: none;
      }

      .crowdUser {
          height: 50px;
          padding: 10px 5px;
      }

      .crowdUser:hover {
          background-color: rgb(235, 236, 237);
      }
  }

  > > > .el-divider {
      margin-top: 8px;
  }


  > > > .main {
      position: absolute;
      top: 0;
      left: 280px;
      right: 0;
      bottom: 170px;
      padding-bottom: 5px;
      background-color: rgb(245, 245, 245);
  }

  > > > .el-footer {
      position: absolute;
      left: 280px;
      bottom: 0;
      right: 0;
      padding: 0 10px 0 10px;
      height: 170px !important;


      .video {
          border: none;
          background-color: #FFF;
          position: absolute;
          top: 8px;
          left: 345px;
      }
  }

  > > > .quill-editor {
      width: 100%;

      .ql-toolbar {
          border: none;
      }

      .ql-container {
          border: none;
          height: 95px;

          img {
              max-height: 150px;
          }
      }

      .ql-toolbar {
          padding-left: 0 !important;
      }
  }

  > > >#emoji-palette{
    top: -258px!important;
    left: 0!important;
    max-width: 350px;

    #tab-toolbar{
        li{
          width: 40px!important;
        }
    }

    #tab-panel{
      max-height: 180px;
      .bem{
        margin: 3px!important;
      }
    }

  }

  > > > .video-dialog {
      .el-dialog__header {
          padding: 5px;
          text-align: center;

          .el-dialog__headerbtn {
              top: 12px;
          }
      }

      .el-dialog__body {
          padding: 5px 10px;
          position: relative;

          div {
              height: 500px;
          }

          #rtcA {
              width: 750px;
              height: 500px;
          }

          #rtcB {
              width: 350px;
              height: 200px;
              position: absolute;
              top: 5px;
              right: 10px;
          }

          video::-webkit-media-controls-panel {
              display: none !important;
          }

      }
  }

  .voice {
      position: absolute;
      top: -30px;
      left: 20px;
      right: 20px;
  }

</style>
