<template>
    <div>
        <el-menu v-if="chatList.length>0"
                 style="width: 100%;height: calc(100% - 48px);background-color: rgb(236,236,236)"
                 :default-active="defaultActive||''+chatList[0].id"
                 class="el-menu-vertical-demo"
                 text-color="#000"
                 active-text-color="#000">
            <el-menu-item
                @contextmenu.prevent.native="openMenu($event,item)"
                @click="open(item)"
                v-for="(item,index) in chatList"
                :index="item.id+''"
                :key="index">
                <img class="headImg" :class="{gray:item.id<10000 && item.id>0 && onlineUser.indexOf(item.id) === -1 }"
                     :src="item.avatar" alt=""/>
                <div class="details">
                    <span class="nickname">{{ item.remark || item.nickname || item.username }}</span>
                    <span class="time">{{ item.time | formatDate }}</span>
                    <span class="message">{{ item.msg }}</span>
                    <el-badge v-if="item.badge>0" :value="item.badge" :max="9"/>
                </div>
            </el-menu-item>
        </el-menu>
        <div v-else>
            <h4 style="text-align: center">暂无消息</h4>
        </div>

        <ul v-show="menu.visible" :style="{left:menu.left+'px',top:menu.top+'px'}" class="contextmenu">
            <li @click="deleteList">移除会话</li>
        </ul>
    </div>
</template>

<script>
import {mapGetters} from "vuex";
import {getChatsInfo} from "@/api/user";

export default {
    name: "CharList",
    computed: {
        ...mapGetters(['chat', 'onlineUser']),
        chatList() {
            let ids = this.chat.map(item => item.id)
            getChatsInfo(ids).then(res => {
                let map = {}
                res.data.forEach(row => {
                    map[row.id] = row
                })
                this.chat.forEach((item) => {
                    if (item.type === 'ADD_FRIEND') {
                        item.avatar = require('@/assets/bell.png')
                        item.remark = '新朋友'
                    } else {
                        item.avatar = map[item.id].avatar
                        item.remark = map[item.id].name
                    }
                })
                this.$forceUpdate()
            })
            return this.chat
        }
    },
    props: {
        defaultActive: {
            type: String,
            default: undefined
        }
    },
    watch: {
        "menu.visible"(value) {
            if (value) {
                document.body.addEventListener('click', this.closeMenu)
            } else {
                document.body.removeEventListener('click', this.closeMenu)
            }
        }
    },
    data() {
        return {
            menu: {
                visible: false,
                left: 0,
                top: 0
            },
            item: ''
        }
    },
    methods: {
        open(item) {
            this.$emit("open", item)
        },
        openMenu(e, item) {
            this.item = item
            const menuMinWidth = 105
            const offsetLeft = this.$el.getBoundingClientRect().left
            const offsetWidth = this.$el.offsetWidth
            const maxLeft = offsetWidth - menuMinWidth
            const left = e.clientX - offsetLeft
            if (left > maxLeft) {
                this.menu.left = maxLeft
            } else {
                this.menu.left = left
            }
            this.menu.top = e.clientY
            this.menu.visible = true
        },
        closeMenu() {
            this.menu.visible = false
        },
        deleteList() {
            this.$store.dispatch('chat/deleteList', this.item)
        }
    },
    mounted() {
        if (this.chatList.length > 0) {
            let item
            if (this.defaultActive) {
                item = this.chatList.filter(e => e.id === parseInt(this.defaultActive))[0]
            } else {
                item = this.chatList[0]
            }
            this.open(item)
        }
    },
}
</script>

<style scoped lang="scss">
.gray {
    -webkit-filter: grayscale(100%);
    filter: grayscale(100%);
}

.contextmenu {
    margin: 0;
    background: #fff;
    z-index: 3000;
    position: absolute;
    list-style-type: none;
    padding: 5px 0;
    border-radius: 4px;
    font-size: 12px;
    font-weight: 400;
    color: #333;
    box-shadow: 2px 2px 3px 0 rgba(0, 0, 0, 0.3);

    li {
        margin: 0;
        padding: 7px 16px;
        cursor: pointer;

        & :hover {
            background: #eee;
        }

    }
}

.headImg {
    vertical-align: 5px;
    width: 44px;
    height: 44px;
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

</style>
