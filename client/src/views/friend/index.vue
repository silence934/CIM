<template>
    <div style="height: 100vh;">
        <centerControl>
            <el-menu class="el-menu-vertical-demo">
                <el-submenu :key="index" :index="''+index" v-for="(group,index) in friendData">
                    <template slot="title">
                        <span>
                            {{ group.name }}  [{{ group.friends.filter(item => onlineUser.indexOf(item.id) > -1).length }}/{{ group.friends.length }}]
                        </span>
                    </template>
                    <el-menu-item
                            @contextmenu.prevent.native="openMenu($event,user)"
                            :key="user.id" @click="handleFriend(user,group.id)"
                            :index="''+user.id" v-for="(user) in group.friends">
                        <div style="width: 100%;height: 50px;">
                            <div class="avatar_div">
                                <img :class="{ gray:onlineUser.indexOf(user.id) === -1  }" class="avatar" :src="user.avatar" alt="">
                            </div>
                            <div class="name">
                                {{ user.remark ? user.remark : user.nickname ? user.nickname : user.username }}
                            </div>
                        </div>
                    </el-menu-item>
                </el-submenu>
            </el-menu>
        </centerControl>
        <div class="right">
            <div class="right_main" v-if="activate.id">
                <img :src="activate.avatar" class="avatar1" alt="">
                <h3 style="font: 20px Extra large">{{ activate.nickname }}</h3>

                <el-row type="flex" justify="center">
                    <el-col :span="2">
                        <span style="margin-right: 5px">备注: </span>
                    </el-col>
                    <el-col :span="8">
                        <el-input @change="updateRemark" style="width: 100px;" v-model="activate.remark"></el-input>
                    </el-col>
                </el-row>
                <br>
                <el-row type="flex" justify="center">
                    <el-col :span="2">
                        <span style="margin-right: 5px">账号: </span>
                    </el-col>
                    <el-col :span="8">
                        <span>{{ activate.username }}</span>
                    </el-col>
                </el-row>
                <br>
                <el-row type="flex" justify="center">
                    <el-col :span="2">
                        <span style="margin-right: 5px">性别: </span>
                    </el-col>
                    <el-col :span="8">
                        <span>{{ activate.sex }}</span>
                    </el-col>
                </el-row>
                <br>
                <el-row type="flex" justify="center">
                    <el-col :span="2">
                        <span style="margin-right: 5px">电话: </span>
                    </el-col>
                    <el-col :span="8">
                        <span>{{ activate.phone }}</span>
                    </el-col>
                </el-row>
                <br>
                <el-row type="flex" justify="center">
                    <el-col :span="2">
                        <span style="margin-right: 5px">邮箱: </span>
                    </el-col>
                    <el-col :span="8">
                        <span>{{ activate.mail }}</span>
                    </el-col>
                </el-row>
                <br>
                <el-button type="primary" @click="sendMessage">发送消息</el-button>
            </div>
            <div class="right_main" v-else>
                未选择
            </div>
        </div>

        <ul v-show="menu.visible" :style="{left:menu.left+'px',top:menu.top+'px'}" class="contextmenu">
            <li @click="deleteFriend">删除该好友</li>
        </ul>

    </div>
</template>

<script>
import centerControl from '../../components/CenterControl'
import {deleteFriend, getGroup, updateFriend} from '@/api/user'
import router from '@/router'
import {mapGetters} from "vuex"

export default {
    name: 'friend',
    components: {centerControl},
    data() {
        return {
            friendData: [{
                id: 2,
                name: '我的家人',
                friends: [{
                    avatar: '',
                    id: '',
                    mail: null,
                    nickname: null,
                    phone: null,
                    username: 'test3',
                    remark: '',
                    sex: '',
                    friendId: ''
                }]
            }],
            activate: {
                avatar: '',
                id: '',
                mail: null,
                nickname: null,
                phone: null,
                username: 'test3',
                remark: '',
                sex: '',
                friendId: ''
            },
            groupId: '',
            menu: {
                visible: false,
                left: 0,
                top: 0
            },
            rightUser: {}
        }
    },
    computed: {
        visible() {
            return this.menu.visible;
        },
        ...mapGetters([
            'userId', 'onlineUser'
        ]),
    },
    watch: {
        visible(value) {
            if (value) {
                document.body.addEventListener('click', this.closeMenu)
            } else {
                document.body.removeEventListener('click', this.closeMenu)
            }
        }
    },
    methods: {
        handleFriend(key, groupId) {
            this.activate = key
            this.groupId = groupId
        },
        getGroup() {
            getGroup().then(res => {
                this.friendData = res.data
            })
        },
        updateRemark() {
            if (!this.activate.remark) {
                this.$message.warning('备注不能为空')
                return
            }
            let data = new FormData()
            data.append('id', this.activate.friendId)
            data.append('userId', this.activate.id)
            data.append('remark', this.activate.remark)
            data.append('groupId', this.groupId)
            updateFriend(data).then(() => {
                this.$message.success('修改成功')
                this.getGroup()
            })
        },
        sendMessage() {
            router.push({path: '/message', query: this.activate})
        },
        openMenu(e, user) {
            this.rightUser = user
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
            this.menu.top = e.clientY - 60
            this.menu.visible = true
        },
        closeMenu() {
            this.menu.visible = false
        },
        deleteFriend() {
            this.$confirm('确定删除该好友吗', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                let data = new FormData();
                data.append("id", this.rightUser.friendId)
                deleteFriend(data).then(() => {
                    this.$message({
                        type: 'success',
                        message: '操作成功!'
                    })
                    this.menu.visible = false
                    this.getGroup()
                })
            })
        }
    },
    mounted() {
        this.getGroup()
    }
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

> > > .el-input__inner {
    line-height: 30px;
    height: 30px;
}

.avatar_div {
    text-align: center;
    vertical-align: middle;
    position: relative;
    float: left;
    margin: 7px 0;
    height: 36px;
    width: 36px
}

.avatar {
    vertical-align: -5px;
    height: 36px;
    width: 36px;
    -webkit-border-radius: 50%;
}

.avatar1 {
    vertical-align: -5px;
    height: 80px;
    width: 80px;
    -webkit-border-radius: 50%;
}

.name {
    line-height: 1.6;
    overflow: hidden;
    padding-top: 8px;
    padding-left: 10px;
}

.right {
    position: absolute;
    top: 0;
    left: 280px;
    right: 0;
    bottom: 170px;
    padding-bottom: 5px;
    background-color: #FFF;
    text-align: center;
}

.right_main {
    width: 800px;
    position: relative;
    top: 20%;
    left: 50%;
    transform: translateX(-50%);
    text-align: center;
}
</style>
