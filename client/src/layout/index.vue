<template>
    <div :class="classObj" class="app-wrapper">
        <audio muted ref="audio" src="@/assets/notify.mp3"/>
        <div v-if="device==='mobile'&&sidebar.opened" class="drawer-bg" @click="handleClickOutside"/>
        <sidebar class="sidebar-container"/>
        <div class="main-container">
            <app-main/>
        </div>
    </div>
</template>

<script>
import {AppMain, Navbar, Sidebar} from './components'
import ResizeMixin from './mixin/ResizeHandler'
import {mapState} from 'vuex'
import {getDetails} from "@/api/user"
import {getUnreadMessage} from "@/api/message"

export default {
    name: 'Layout',
    components: {AppMain, Navbar, Sidebar},
    mixins: [ResizeMixin],
    computed: {
        ...mapState({
            chat: state => state.chat.list,
            userId: state => state.user.user_id,
            sidebar: state => state.app.sidebar,
            device: state => state.app.device,
            showSettings: state => state.settings.showSettings,
            needTagsView: state => state.settings.tagsView,
            fixedHeader: state => state.settings.fixedHeader
        }),
        classObj() {
            return {
                hideSidebar: !this.sidebar.opened,
                openSidebar: this.sidebar.opened,
                withoutAnimation: this.sidebar.withoutAnimation,
                mobile: this.device === 'mobile'
            }
        },
        chatList() {
            return [...this.chat].reverse()
        }
    },
    sockets: {
        connect: function () {
            console.log('连接成功')
        },
        receiveEvent(data) {
            if (this.$route.path !== "/message") {
                let chat = this.chatList.filter(i => i.id === data.from)
                if (chat.length === 0) {
                    getDetails({id: data.from}).then(res => {
                        this.$store.dispatch("chat/addList",
                            {
                                avatar: res.data.avatar,
                                id: data.from,
                                nickname: res.data.nickname,
                                time: data.time,
                                msg: data.data,
                                badge: 1
                            })
                    })
                } else {
                    chat = chat[0]
                    this.$store.dispatch("chat/addList",
                        {
                            avatar: chat.avatar,
                            id: data.from,
                            nickname: chat.nickname,
                            time: data.time,
                            msg: data.data,
                            badge: chat.badge + 1
                        })
                }
            }
            console.log("收到消息", data)
            if (data.type === 'VIDEO') {
                return
            }
            this.$refs.audio.play()
        },
        STATISTICS_ONLINE(data) {
            console.log("在线人员", data)
            this.$store.dispatch("chat/updateOnlineUser", data)
        }
    },
    methods: {
        handleClickOutside() {
            this.$store.dispatch('app/closeSideBar', {withoutAnimation: false})
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
        messageToChat(message) {
            let id
            if (message.type === 'ADD_FRIEND') {
                id = -1
            } else if (message.to === this.userId) {
                id = message.from
            } else {
                id = message.to
            }
            return {id: id, type: message.type, time: message.time, msg: this.messageSwitch(message)}
        }
    },
    mounted() {
        this.$socket.emit('loginEvent', {from: this.userId, type: "LOGIN"})
        getUnreadMessage().then((res) => {
            console.log("未读消息", res)
            res.data.forEach(async (item) => {
                let newChat = this.messageToChat(item)
                let chat = this.chat.filter(i => i.id === newChat.id)
                if (chat.length === 0) {
                    if (newChat.type === 'ADD_FRIEND') {
                        newChat.avatar = require('@/assets/bell.png')
                        newChat.badge = 1
                        newChat.nickname = '新朋友'
                    } else {
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
            })
        })
    }
}
</script>

<style lang="scss" scoped>
@import "~@/styles/mixin.scss";
@import "~@/styles/variables.scss";

.app-wrapper {
    @include clearfix;
    position: relative;
    height: 100%;
    width: 100%;

    &.mobile.openSidebar {
        position: fixed;
        top: 0;
    }
}

.drawer-bg {
    background: #000;
    opacity: 0.3;
    width: 100%;
    top: 0;
    height: 100%;
    position: absolute;
    z-index: 999;
}

.fixed-header {
    position: fixed;
    top: 0;
    right: 0;
    z-index: 9;
    width: calc(100% - #{$sideBarWidth});
    transition: width 0.28s;
}

.hideSidebar .fixed-header {
    width: calc(100% - 54px)
}

.mobile .fixed-header {
    width: 100%;
}
</style>
