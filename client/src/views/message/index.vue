<template>
    <div style="height: 100vh;">
        <audio muted ref="audio" loop="loop" src="@/assets/video.wav"/>

        <centerControl style="position: relative;left: 0">
            <el-menu
                    style="width: 100%;height: calc(100% - 48px);background-color: rgb(236,236,236)"
                    :default-active="nowActive.id+nowActive.type"
                    class="el-menu-vertical-demo"
                    text-color="#000"
                    active-text-color="#000">
                <el-menu-item @click="open(item)" :index="item.id+item.type" v-for="(item,index) in chatList" :key="index">
                    <img class="headImg" :class="{ gray:onlineUser.indexOf(item.id) === -1  }" :src="item.avatar" alt=""/>
                    <div class="details">
                        <span class="nickname">{{ item.remark || item.nickname || item.username }}</span>
                        <span class="time">{{ item.time | formatDate }}</span>
                        <span class="message">{{ item.msg }}</span>
                        <el-badge v-if="item.badge>0" :value="item.badge" :max="9"/>
                    </div>
                </el-menu-item>
            </el-menu>
        </centerControl>

        <el-main class="main">
            <el-header style="height: 30px">
                {{ nowActive.remark || nowActive.nickname || nowActive.username }}
                <el-divider/>
            </el-header>
            <el-main id="scroll" style="height: calc(100% - 30px);padding: 0">
                <happy-scroll color="rgba(0,0,0,0.5)" :scroll-top="scrollTop" @vertical-start="toTop" hide-horizontal size="5" resize>
                    <div style="width: calc(100% - 8px)" class="clear">
                        <p v-if="loadingMessage">加载中...</p>
                        <p v-if="noMore">没有更多了</p>
                        <div :class="item.from===userId ?'right':'left'" class="clear item left" v-for="(item,index) in msgList" :key="index">
                            <span class="time" v-if="item.showTime">
                                {{ item.time }}
                            </span>
                            <span v-if="item.type==='VOICE'" class="message" style="padding: 3px 0">
                                 <audio-player :src="item.data" :isLeft="item.from!==userId"/>
                            </span>
                            <span v-else class="message" v-html="item.data"></span>
                            <el-image class="headImg" :src="item.from===userId ?avatar:nowActive.avatar"/>
                            <div class="addFriend" v-if="item.to===userId && item.type==='ADD_FRIEND' && (item.status!=='ACCEPT'||item.status!=='REJECT')">
                                <el-button @click="friend={};dialog=true;message=item" size="mini" type="primary">接受</el-button>
                                <el-button @click="message=item;addFriend(false)" size="mini" type="danger">拒绝</el-button>
                            </div>
                            <div class="addFriend" v-else-if="item.type==='ADD_FRIEND'">
                                {{ item.status === 'ACCEPT' ? '已接受' : item.status === 'REJECT' ? '已拒绝' : '' }}
                            </div>
                        </div>
                    </div>
                </happy-scroll>
                <el-button v-if="nowActive.id>9999" @click="drawer = true"
                           icon="el-icon-d-arrow-left" circle
                           type="primary" style="position: absolute;right: 0;top: 50%">
                </el-button>
            </el-main>
        </el-main>

        <el-footer v-if="nowActive.id">
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

        <el-dialog width="450px" title="添加联系人" :visible.sync="dialog">
            <el-input style="width: 400px" v-model="friend.remark" placeholder="备注"></el-input>
            <br>
            <br>
            <el-select v-model="friend.group" style="width: 400px" placeholder="分组">
                <el-option v-for="item in friendData"
                           :key="item.id"
                           :label="item.name"
                           :value="item.id"/>
            </el-select>
            <br>
            <br>
            <el-button @click="addFriend(true)" type="primary">确定</el-button>
        </el-dialog>

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
import {addFriend, getDetails, getGroup, getUsersInfo} from '@/api/user'
import elementResizeDetector from 'element-resize-detector'
import {getCrowdInfo} from "@/api/crowd"
import centerControl from '../../components/CenterControl'
import VideoAndVoice from "@/views/message/VideoAndVoice"
import Voice from "./Voice"
import AudioPlayer from "./AudioPlayer"

import { Quill} from 'vue-quill-editor'
import { ImageExtend, QuillWatch} from 'quill-image-extend-module'
import quillEmoji from 'quill-emoji'
import 'quill-emoji/dist/quill-emoji.css'
Quill.register('modules/quillEmoji', quillEmoji)
Quill.register('modules/ImageExtend', ImageExtend)

export default {
    name: 'index',
    components: {Voice, VideoAndVoice, centerControl,AudioPlayer},
    sockets: {
        getChat(messageList) {
            messageList = messageList.content.reverse()
            //获取聊天记录有四种情况  当前是否已存在消息(判断是第一次获取消息还是上拉获取消息)  获取的是否有消息  组合
            this.loadingMessage = false
            if (this.msgList.length === 0) {
                if (messageList.length === 0) {
                    this.noMore = true
                    this.nowActive.time = moment().format('YYYY-MM-DD HH:mm:ss')
                } else {
                    messageList.forEach((item, index) => {
                        item.showTime = !(index > 0 &&
                                moment(item.time).diff(moment(messageList[index - 1].time), 'minutes') < 1)
                    })
                    this.msgList.unshift(...messageList)
                    this.$nextTick(() => {
                        this.scrollTop = document.getElementsByClassName('happy-scroll-content')[0].scrollHeight
                    })
                    this.nowActive.badge = 0
                    this.nowActive.msg = this.messageSwitch(messageList[messageList.length - 1])
                    this.nowActive.time = messageList[messageList.length - 1].time
                    this.nowActive.type = messageList[messageList.length - 1].type
                }
            } else {
                setTimeout(() => {
                    this.b = true
                }, 1000)
                if (messageList.length === 0) {
                    this.noMore = true
                } else {
                    this.msgList.unshift(...messageList)
                    this.$nextTick(() => {
                        this.scrollTop = 20
                    })
                }
            }
            this.$store.dispatch('chat/updateList', this.nowActive)
            this.loading = false
        },
        receiveEvent(message) {
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
                if (newChat.id === parseInt(this.nowActive.id) &&
                        (newChat.type === this.nowActive.type ||
                                (newChat.type !== 'ADD_FRIEND') && this.nowActive.type !== 'ADD_FRIEND')) {
                    message.showTime = this.msgList.length === 0 || !moment(message.time)
                            .diff(moment(this.msgList[this.msgList.length - 1].time), 'minutes') < 1
                    this.msgList.push(message)
                    //this.nowActive.type = message.type
                    this.$store.dispatch('chat/updateList', newChat)
                } else {
                    let chat = this.chatList.filter(i => i.id === newChat.id)
                    if (chat.length === 0) {
                        getDetails({id: message.from}).then(res => {
                            newChat.avatar = res.data.avatar
                            newChat.badge = 1
                            newChat.nickname = res.data.nickname
                            this.$store.dispatch('chat/addList', newChat)
                        })
                    } else {
                        newChat.avatar = chat[0].avatar
                        newChat.nickname = chat[0].nickname
                        newChat.badge = chat[0].badge + 1
                        this.$store.dispatch('chat/addList', newChat)
                    }
                }
            }
        }
    },
    data() {
        return {
            noLoad: true,
            loadingMessage: false,
            noMore: false,
            scrollTop: 0,
            nowActive: {avatar: '', id: '', nickname: '', msg: '', time: '', badge: 0, remark: ''},
            nickname: '',
            loading: true,
            pageQuery: {size: 10, page: 1, t: {from: '', to: ''}},
            input: '',
            keywords: '',
            msgList: [],
            editorOption: {},
            happyScrollElement: {},
            happyScrollContainer: {},
            b: true,
            dialogVideo: false,
            /*添加好友*/
            dialog: false,
            friend: {
                remark: '',
                group: '',
            },
            friendData: [],
            message: {},
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
        },
        chatList() {
            let ids = this.chat.map(item => item.id)
            getUsersInfo(ids).then(res => {
                let map = {}
                res.data.forEach(row => {
                    map[row.id] = row
                })
                this.chat.forEach((item, index) => {
                    item.avatar = item.type === 'ADD_FRIEND' ? require('@/assets/bell.png') : map[item.id].avatar
                    item.username = map[item.id].username
                    item.nickname = map[item.id].nickname
                })
                this.$forceUpdate()
            })
            return this.chat
            //return [...this.chat].reverse()
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
            if (!(this.nowActive.id === item.id && this.nowActive.type === item.type)) {
                this.loading = true
                this.noMore = false
                this.noLoad = false
                setTimeout(() => {
                    this.noLoad = true
                }, 5)
                this.active(item)
                this.scrollTop = document.getElementsByClassName('happy-scroll-content')[0].scrollHeight - 1
                this.pageQuery.page = 1
                this.msgList = []
                this.$socket.emit('selectChat', this.pageQuery)
            }
            item.badge = 0
            this.$store.dispatch('chat/updateList', item)
            this.tagMessage();
        },
        send(type) {
            if (this.input) {
                this.input = this.input.replace(/<p>/g, '')
                this.input = this.input.replace(/<\/p>/g, '')
                if (!type) {
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
                let switchMessage = this.messageSwitch(message)

                this.$store.dispatch('chat/updateList', {id: message.to, type: type, time: message.time, msg: switchMessage})
                this.msgList.push(message)
                this.$socket.emit('sendEvent', message)
                this.$nextTick(() => {
                    this.scrollTop = document.getElementsByClassName('happy-scroll-content')[0].scrollHeight
                })
                this.input = ''
            } else {
                this.$message.info('消息不能为空')
            }
        },
        toTop() {
            if (!this.noMore && this.noLoad) {
                this.b = false
                this.loadingMessage = true
                this.pageQuery.page++
                this.$socket.emit('selectChat', this.pageQuery)
            }
        },
        active(nowActive) {
            console.log("激活了:", nowActive)
            this.nowActive = nowActive
            this.pageQuery.to = parseInt(nowActive.id)
            this.pageQuery.type = nowActive.type === 'ADD_FRIEND' ? 1 : 2
            if (nowActive.id > 9999) {
                this.getCrowdInfo(nowActive.id)
            }
        },
        init() {
            this.pageQuery.from = this.userId
            if (this.userData.id) {
                let chatListHasUser = this.chatList.filter(e => e.id === parseInt(this.userData.id))[0]
                if (!chatListHasUser) {
                    chatListHasUser = {
                        time: moment().format('YYYY-MM-DD HH:mm:ss'),
                        badge: 0,
                        avatar: this.userData.avatar,
                        id: parseInt(this.userData.id),
                        nickname: this.userData.nickname,
                        remark: this.userData.remark,
                        type: 'GENERAL'
                    }
                    this.$store.dispatch('chat/addList', chatListHasUser)
                }
                this.active(chatListHasUser)
            } else if (this.chatList.length > 0) {
                this.active(this.chatList[0])
            } else {
                return
            }
            this.$socket.emit('selectChat', this.pageQuery)
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
        addFriend(accept) {
            let data;
            if (accept) {
                if (!this.friend.group) {
                    this.$message.warning("请选择分组")
                    return
                }
                data = {id: this.message.id, accept: accept, remark: this.friend.remark, group: this.friend.group}
            } else {
                data = {id: this.message.id, accept: accept}
            }
            addFriend(data).then(() => {
                this.$message.success("操作成功")
                this.msgList = []
                this.$socket.emit('selectChat', this.pageQuery)
            })
            this.dialog = false
        },
        getGroup() {
            getGroup().then(res => {
                this.friendData = res.data
            })
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
            if (message.to === this.userId) {
                return {id: message.from, type: message.type, time: message.time, msg: this.messageSwitch(message)}
            } else {
                return {id: message.to, type: message.type, time: message.time, msg: this.messageSwitch(message)}
            }
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
    mounted() {
        this.happyScrollContainer = document.getElementsByClassName('happy-scroll-container')[0]
        this.happyScrollElement = document.getElementsByClassName('happy-scroll-content')[0]
        elementResizeDetector().listenTo(this.happyScrollElement, (element) => {
            let height = element.offsetHeight
            let r = this.happyScrollContainer.scrollHeight - this.happyScrollContainer.clientHeight - this.happyScrollContainer.scrollTop
            if (height && this.b) {
                this.scrollTop = height
            }
        })
        this.getGroup()
    },
}
</script>

<style lang="scss" scoped>

  .gray {
      -webkit-filter: grayscale(100%);
      -o-filter: grayscale(100%);
      -moz-filter: grayscale(100%);
      -ms-filter: grayscale(100%);
      filter: grayscale(100%);
      filter: gray;
  }

  .search {
      background-color: rgb(231, 230, 229);
      padding: 10px;
      height: 48px;
      width: 250px;
  }

  .headImg {
      vertical-align: 5px;
      width: 44px;
      height: 44px;
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

  > > > .el-menu-item {
      padding: 8px !important;
      height: 60px;

      .details {
          position: absolute;
          padding-left: 8px;
          left: 52px;
          top: 8px;
          height: 44px;
          width: 189px;

          .el-badge__content {
              position: absolute;
              left: 150px;
              top: -5px;
          }

          .nickname {
              position: absolute;
              top: 0;
              left: 8px;
              height: 25px;
              line-height: 25px;
              font: 14px Base;
              font-weight: 600;
              color: #303133;
          }

          .message {
              position: absolute;
              top: 25px;
              left: 8px;
              height: 19px;
              line-height: 19px;
              font: 12px Extra Small;
              color: #909399;
          }

          .time {
              position: absolute;
              right: 0;
              top: 0;
              height: 19px;
              line-height: 19px;
              font: 12px Extra Small;
              color: #909399;
          }
      }
  }

  > > > .el-menu-item:hover {
      background-color: rgb(198, 197, 196)
  }

  > > > .is-active {
      background-color: rgb(198, 197, 196)
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

  > > > .happy-scroll-container {
      width: 100% !important;
      height: 100% !important;

      .happy-scroll-content {
          width: 100%;

          p {
              font: 12px Extra Small;
              color: #909399;
              text-align: center
          }
      }

      .item {
          position: relative;
          margin-bottom: 30px;

          .headImg {
              height: 38px;
              width: 38px;
              position: absolute;
              top: 20px;
              left: 0;
          }

          .time {
              position: absolute;
              top: 0;
              left: 50%;
              transform: translate(-50%, 0);
              color: #C0C4CC;
              font: 13px Extra Small;
          }

          .message:after {
              border: 3px solid transparent;
              border-right: 3px solid #FFF;
              position: absolute;
              content: "";
              top: 20px;
              margin-top: -10px;
              right: 100%;
          }

          .message {
              display: inline-block;
              border-radius: 4px;
              box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
              padding: 10px;
              position: relative;
              top: 20px;
              left: 45px;
              background-color: #FFF;
              max-width: 50%;

              img {
                  max-width: 150px;
              }
          }

          .addFriend {
              margin-top: 30px;
              margin-left: 60px;
              font: 12px Extra Small;
          }

          .ar-player {
              width: 280px;
              height: 30px;
              position: relative;


              .ar-volume {
                  display: none;
              }

              .ar-player-bar {
                  margin-left: 0;
                  position: absolute;
                  left: 0;
              }

              .ar-player-actions {
                  width: 30px;
                  height: 30px;
                  position: absolute;
                  right: 0;

                  .ar-icon {
                      width: 30px;
                      height: 30px;
                  }
              }

              svg {
                  vertical-align: top;
              }
          }

      }

      .right {
          text-align: right;

          .headImg {
              left: auto;
              right: 0 !important;
          }

          .message:after {
              border-left: 3px solid rgb(158, 234, 106);
              left: 100%;
          }

          .message {
              text-align: left;
              left: -43px;
              background-color: rgb(158, 234, 106);
          }

          .addFriend {
              margin-top: 30px;
              margin-right: 60px;
          }


          .ar-player {
              .ar-player-bar {
                  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);

                  .ar-player__time {
                      color: black;
                  }
              }

          }
      }

      .clear:after {
          content: "";
          clear: both;
          display: block;
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
