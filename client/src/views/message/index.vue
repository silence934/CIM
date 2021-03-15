<template>
    <div style="height: 100vh;position: relative">
        <centerControl style="position: relative;left: 0">
            <el-menu
                    style="width: 100%;height: calc(100% - 48px);background-color: rgb(236,236,236)"
                    :default-active="nowActive.id.toString()"
                    class="el-menu-vertical-demo"
                    text-color="#000"
                    active-text-color="#000">
                <el-menu-item @click="open(item)" :index="item.id.toString()" v-for="(item,index) in chatList" :key="index">
                    <el-image class="headImg" :src="item.avatar"/>
                    <div class="details">
                        <span class="nickname">{{ item.remark || item.nickname }}</span>
                        <span class="time">{{ item.time | formatDate }}</span>
                        <span class="message">{{ item.msg }}</span>
                        <el-badge v-if="item.badge>0" :value="item.badge" :max="9"/>
                    </div>
                </el-menu-item>
            </el-menu>
        </centerControl>

        <el-main class="main">
            <el-header style="height: 30px">{{ nowActive.remark || nowActive.nickname }}
                <el-divider/>
            </el-header>
            <el-main id="scroll" style="height: calc(100% - 30px);padding: 0">
                <happy-scroll color="rgba(0,0,0,0.5)" :scroll-top="scrollTop" @vertical-start="toTop" hide-horizontal size="5" resize>
                    <div style="width: calc(100% - 8px)" class="clear">
                        <p v-if="loadingMessage">加载中...</p>
                        <p v-if="noMore">没有更多了</p>
                        <div :class="item.from===userId ?'right':'left'" class="clear item left" v-for="(item,index) in msgList" :key="index">
                            <span class="time">{{ item.time }}</span>
                            <span class="message" v-html="item.data"></span>
                            <el-image class="headImg" :src="item.from===userId ?avatar:nowActive.avatar"/>
                            <div class="addFriend" v-if="item.to===userId&&item.type==='ADD_FRIEND'&&item.status==='UN_READ'">
                                <el-button @click="friend={};dialog=true;message=item" size="mini" type="primary">接受</el-button>
                                <el-button @click="message=item;addFriend(false)" size="mini" type="danger">拒绝</el-button>
                            </div>
                            <div class="addFriend" style="font: 12px Extra Small" v-else-if="item.type==='ADD_FRIEND'">
                                {{ item.status === 'ACCEPT' ? '已接受' : '已拒绝' }}
                            </div>
                        </div>
                    </div>
                </happy-scroll>
            </el-main>
        </el-main>

        <el-footer v-if="nowActive.nickname">
            <quill-editor
                    @keyup.enter.native="send"
                    v-model="input"
                    ref="myQuillEditor"
                    :options="editorOption"
            />
            <el-button @click="send" style="float: right" size="mini">发送</el-button>
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
    </div>
</template>

<script>
import {quillRedefine} from 'vue-quill-editor-upload'
import {mapGetters} from 'vuex'
import moment from 'moment'
import {addFriend, getDetails, getGroup, getUsersInfo} from '@/api/user'
import elementResizeDetector from 'element-resize-detector'
import centerControl from '../../components/CenterControl'

export default {
    name: 'index',
    components: {quillRedefine, centerControl},
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
                    this.msgList.unshift(...messageList)
                    this.$nextTick(() => {
                        this.scrollTop = document.getElementsByClassName('happy-scroll-content')[0].scrollHeight
                    })
                    this.nowActive.badge = 0
                    this.nowActive.msg = this.messageSwitch(messageList[messageList.length - 1])
                    this.nowActive.time = messageList[messageList.length - 1].time
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
            let newChat = {id: message.from, time: message.time, msg: this.messageSwitch(message)}
            if (message.from === parseInt(this.nowActive.id)) {
                this.msgList.push(message)
                this.$store.dispatch('chat/updateList', newChat)
            } else {
                let chat = this.chatList.filter(i => i.id === message.from)
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
            dialog: false,
            friend: {
                remark: '',
                group: '',
            },
            friendData: [],
            message: {}
        }
    },
    computed: {
        ...mapGetters([
            'userId', 'chat', 'token', 'name', 'avatar'
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
                this.chat.forEach(item => {
                    item.avatar = map[item.id].avatar
                    item.username = map[item.id].username
                    item.nickname = map[item.id].nickname
                })
                this.$forceUpdate()
            })
            return [...this.chat].reverse()
        }
    },
    methods: {
        open(item) {
            if (this.nowActive.id !== item.id) {
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
        },
        send() {
            if (this.input) {
                this.input = this.input.replace(/<p>/g, '')
                this.input = this.input.replace(/<\/p>/g, '')
                let type = this.input.indexOf('<img src=') === -1 ? 'GENERAL' : 'IMG'
                let time = moment().format('YYYY-MM-DD HH:mm:ss')

                let message = {
                    from: this.userId,
                    to: parseInt(this.nowActive.id),
                    time: time,
                    data: this.input,
                    type: type
                }
                let switchMessage = this.messageSwitch(message)

                this.$store.dispatch('chat/updateList', {id: message.to, time: message.time, msg: switchMessage})
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
            this.nowActive = nowActive
            this.pageQuery.to = parseInt(nowActive.id)
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
                        remark: this.userData.remark
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
    },
    created() {
        this.init()
        this.editorOption = quillRedefine({
            // 图片上传的设置
            uploadConfig: {
                action: '/api-v1/artifact/upload',
                res: (res) => {
                    return '/proxy/api-v1/artifact' + res.data.path
                },
                methods: 'POST',
                name: 'file',
                size: 1024 * 3,  //单位为Kb,
                accept: 'image/png, image/gif, image/jpeg, image/bmp, image/x-icon',
                header: (xhr, formData) => {
                    xhr.setRequestHeader('Authorization', 'Bearer ' + this.token)
                    formData.append('type', 'chat')
                }
            },
            // 以下所有设置都和vue-quill-editor本身所对应
            placeholder: '请输入内容',  // 可选参数 富文本框内的提示语
            toolOptions: ['image', 'bold', 'italic', 'underline', 'strike', 'blockquote', 'code-block',
                {'header': 1}, {'header': 2}, {'list': 'ordered'}, {'list': 'bullet'}, {'color': []}]  // 可选参数  选择工具栏的需要哪些功能  默认是全部
        })
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
    left: 250px;
    bottom: 0;
    right: 0;
    height: 170px !important;
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
        margin-bottom: 25px;

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

    }

    .clear:after {
        content: "";
        clear: both;
        display: block;
    }
}

</style>
