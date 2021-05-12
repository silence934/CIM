<template>
    <div style="height: 100%">

        <happy-scroll v-if="nowActive.id&&nowActive.type!=='ADD_FRIEND'"
                      color="rgba(0,0,0,0.5)"
                      :scroll-top="scrollTop"
                      @vertical-start="toTop"
                      hide-horizontal size="5"
                      resize>
            <div style="width: calc(100% - 8px)" class="clear">
                <p v-if="loadingMessage">加载中...</p>
                <p v-if="noMore">没有更多了</p>
                <div :class="item.from===userId ?'right':'left'"
                     class="clear item left"
                     v-for="(item,index) in msgList"
                     :key="index">
                    <span class="time" v-if="messageTimeIsShow(item,msgList[index - 1])">
                        {{ item.time }}
                    </span>
                    <span v-if="item.type==='VOICE'" class="message" style="padding: 3px 0">
                        <audio-player :src="item.data" :isLeft="item.from!==userId"/>
                    </span>
                    <span v-else class="message" v-html="item.data"/>
                    <el-image class="headImg" :src="item.from===userId ?avatar:nowActive.avatar"/>
                </div>
            </div>
        </happy-scroll>

        <div v-else>
            <div v-for="(item,index) in msgList"
                 :key="index"
                 class="item">
                <span style="position: absolute;top: -10px;font: 12px Extra Small">{{item.time}}</span>
                <el-image v-if="item.fromUser" class="headImg" :src="item.fromUser.avatar"/>
                <div style="position: absolute;top: 15px;left: 80px;width: 200px">
                    <h3 v-if="item.fromUser" style="margin: 0 0 3px 0">{{item.fromUser.nickname||item.fromUser.username}}</h3>
                    <span style="font: 13px Small">{{item.data}}</span>
                </div>
                <div style="position: absolute;top: 25px;left: 300px;" v-if="(item.status!=='ACCEPT'&&item.status!=='REJECT')">
                    <el-button @click="message=item;friend={};dialog=true" size="mini" type="primary">同意</el-button>
                    <el-button @click="message=item;addFriend(false)" size="mini" type="info">拒绝</el-button>
                </div>
                <div style="position: absolute;top: 25px;left: 300px;" v-else>
                    {{ item.status === 'ACCEPT' ? '已接受' : item.status === 'REJECT' ? '已拒绝' : '' }}
                </div>
            </div>
        </div>

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
    </div>
</template>

<script>
import {mapGetters} from "vuex";
import CharList from "@/views/message/CharList";
import Voice from "@/views/message/Voice";
import VideoAndVoice from "@/views/message/VideoAndVoice";
import centerControl from "@/components/CenterControl/index";
import AudioPlayer from "@/views/message/AudioPlayer";
import moment from "moment";
import elementResizeDetector from "element-resize-detector";
import {addFriend, getGroup} from "@/api/user";

export default {
    name: "MessageList",
    components: {CharList, Voice, VideoAndVoice, centerControl,AudioPlayer},
    computed: {
        ...mapGetters([
            'userId', 'chat', 'token', 'name', 'avatar', 'onlineUser'
        ]),
    },
    props:{
        nowActive:{
            type:Object
        },
        msgList:{
            type:Array,
            default:[{data: '',
                from: 0,
                isRead: false,
                status: '',
                time: '',
                to: 0,
                type: '',
                fromUser:{
                    avatar: '',
                    id: 0,
                    mail: '',
                    nickname: '',
                    phone: '',
                    sex: '',
                    username: ''
                }
            }]
        }
    },
    sockets: {
        getChat(messageList) {
            if (messageList.content.length>0&&messageList.content[0].type!=='ADD_FRIEND'){
                messageList = messageList.content.reverse()
            }else {
                messageList = messageList.content
            }
            //获取聊天记录有四种情况  当前是否已存在消息(判断是第一次获取消息还是上拉获取消息)  获取的是否有消息  组合
            this.loadingMessage = false
            if (this.msgList.length === 0) {
                if (messageList.length === 0) {
                    this.noMore = true
                    this.nowActive.time = moment().format('YYYY-MM-DD HH:mm:ss')
                } else {
                    this.msgList.unshift(...messageList)
                    this.$nextTick(() => {
                        if (this.nowActive.id&&this.nowActive.type!=='ADD_FRIEND'){
                            this.scrollTop = document.getElementsByClassName('happy-scroll-content')[0].scrollHeight
                        }
                    })
                    this.nowActive.badge = 0
                    this.nowActive.msg = this.messageSwitch(messageList[messageList.length - 1])
                    this.nowActive.time = messageList[messageList.length - 1].time
                    this.nowActive.type = messageList[messageList.length - 1].type
                    this.$store.dispatch('chat/updateList', this.nowActive)
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
        }
    },
    data(){
        return{
            noLoad: true,
            loadingMessage: false,
            noMore: false,
            scrollTop: 0,
            b:true,
            happyScrollElement: {},
            happyScrollContainer: {},
            pageQuery:{size: 10, page: 1, t: {from: '', to: ''}},
            /*添加好友*/
            dialog: false,
            friend: {
                remark: '',
                group: '',
            },
            friendData: [],
            message: {},
        }
    },
    methods:{
        toTop() {
            if (!this.noMore && this.noLoad) {
                this.b = false
                this.loadingMessage = true
                this.pageQuery.page++
                this.$socket.emit('selectChat', this.pageQuery)
            }
        },
        messageTimeIsShow(current,last){
            if (last){
                return moment(current.time)
                    .diff(moment(last.time), 'minutes') > 1
            }
            return true
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
        getMsgList(){
            this.noMore = false
            this.noLoad = false
            setTimeout(() => {
                this.noLoad = true
            }, 5)
            this.pageQuery.page = 1
            this.pageQuery.to = parseInt(this.nowActive.id)
            this.pageQuery.type = this.nowActive.type === 'ADD_FRIEND' ? 1 : 2
            this.msgList.length = 0
            this.$socket.emit('selectChat', this.pageQuery)
        },
        scrollToBottom(){
            this.$nextTick(() => {
                if (this.nowActive.id&&this.nowActive.type!=='ADD_FRIEND'){
                    this.scrollTop = document.getElementsByClassName('happy-scroll-content')[0].scrollHeight
                }
            })
        },
        getGroup() {
            getGroup().then(res => {
                this.friendData = res.data
            })
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
                this.msgList.length=0
                this.$socket.emit('selectChat', this.pageQuery)
            })
            this.dialog = false
        },
    },
    mounted() {
        this.pageQuery.from = this.userId
        this.happyScrollContainer = document.getElementsByClassName('happy-scroll-container')[0]
        this.happyScrollElement = document.getElementsByClassName('happy-scroll-content')[0]
        if (this.happyScrollElement){
            elementResizeDetector().listenTo(this.happyScrollElement, (element) => {
                let height = element.offsetHeight
                if (height && this.b) {
                    this.scrollTop = height
                }
            })
        }
        this.getGroup()
    }
}
</script>

<style scoped lang="scss">
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
                top: 28px;
                left: 48px;
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

    .item{
        height: 70px;
        margin: 15px 5px 5px 10px;
        position: relative;
    }
    .headImg {
        width: 50px;
        height: 50px;
        margin-top: 8px;
        margin-left: 5px;
        border-radius: 5px;
    }
</style>
