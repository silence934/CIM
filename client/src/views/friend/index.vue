<template>
    <div style="height: 100vh;">
        <centerControl>
            <el-menu class="el-menu-vertical-demo">
                <el-submenu :key="index" :index="''+index" v-for="(group,index) in friendData">
                    <template slot="title">
                        <span>
                            {{ group.name }}  [0/{{ group.friends.length }}]
                        </span>
                    </template>
                    <el-menu-item :key="user.id" @click="handleFriend(user,group.id)" :index="''+user.id" v-for="(user) in group.friends">
                        <div style="width: 100%;height: 50px;">
                            <div class="avatar_div">
                                <img class="avatar" :src="user.avatar" alt="">
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
    </div>
</template>

<script>
import centerControl from '../../components/CenterControl'
import {getGroup, updateFriend} from '@/api/user'
import router from '@/router'

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
            groupId: ''
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
        }
    },
    mounted() {
        this.getGroup()
    }
}
</script>

<style scoped>

>>> .el-input__inner {
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
