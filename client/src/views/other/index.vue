<template>
    <div style="height: 100vh;">
        <centerControl>
            <el-menu
                    default-active="2"
                    class="el-menu-vertical-demo">
                <el-submenu index="1">
                    <template slot="title">
                        <i class="el-icon-location"></i>
                        <span>查找</span>
                    </template>
                    <el-menu-item @click="dialog1=true;user={}" index="1-1">添加联系人</el-menu-item>
                    <el-menu-item index="1-2">加入群</el-menu-item>
                </el-submenu>
                <el-menu-item index="2">
                    <i class="el-icon-menu"></i>
                    <span slot="title">查看通知</span>
                </el-menu-item>

            </el-menu>
        </centerControl>

        <el-dialog width="580px" :close-on-click-modal="false" title="添加联系人" :visible.sync="dialog1">
            <el-input @change="findUser" v-model="input" placeholder="账号或昵称"/>
            <br><br>
            <el-card v-if="user&&user.id" class="box-card">
                <div style="height: 90px;width: 90px;padding: 5px;display: inline-block">
                    <el-image style="width: 80px;height: 80px" :src="user.avatar"/>
                </div>
                <div style="height: 90px;width: 300px;padding: 5px;display: inline-block;vertical-align: 20px">
                    <span class="from">账号: </span>
                    <span class="from" style="margin-right: 20px">{{ user.username }}</span>
                    <span class="from">昵称: </span>
                    <span class="from">{{ user.nickname }}</span>
                    <br>
                    <br>
                    <span class="from">性别: </span>
                    <span class="from" style="margin-right: 20px;">{{ user.sex }}</span>
                    <span class="from">电话: </span>
                    <span class="from">{{ user.phone }}</span>
                </div>

                <div style="height: 90px;width: 89px;padding: 25px 0;display: inline-block;vertical-align: 40px">
                    <el-button @click="dialog2=true;addFriend={}" type="primary" icon="el-icon-circle-plus">添加</el-button>
                </div>

            </el-card>
        </el-dialog>

        <el-dialog width="450px" title="添加联系人" :visible.sync="dialog2">
            <el-input style="width: 400px" v-model="addFriend.remark" placeholder="备注"></el-input>
            <br>
            <br>
            <el-select v-model="addFriend.group" style="width: 400px" placeholder="分组">
                <el-option v-for="item in friendData"
                           :key="item.id"
                           :label="item.name"
                           :value="item.id"/>
            </el-select>
            <br>
            <br>
            <el-input type="textarea" style="width: 400px" v-model="addFriend.verify" placeholder="验证消息"></el-input>
            <br>
            <br>
            <el-button @click="addFriend1()" type="primary">确定</el-button>
        </el-dialog>
    </div>
</template>

<script>
import centerControl from '../../components/CenterControl'
import {getGroup, getUser} from "@/api/user"
import {mapGetters} from "vuex"
import moment from 'moment'

export default {
    name: 'group',
    computed: {
        ...mapGetters([
            'userId'
        ]),
    },
    components: {centerControl},
    data() {
        return {
            dialog1: false,
            dialog2: false,
            input: '',
            user: {
                avatar: '',
                id: '',
                mail: '',
                nickname: '',
                phone: '',
                sex: '',
                username: ''
            },
            addFriend: {
                remark: '',
                group: '',
                verify: ''
            },
            friendData: []
        }
    },
    methods: {
        findUser() {
            getUser({key: this.input}).then(res => {
                this.user = res.data
            })
        },
        getGroup() {
            getGroup().then(res => {
                this.friendData = res.data
            })
        },
        addFriend1() {
            if (!this.addFriend.group) {
                this.$message.warning("请选择分组")
                return
            }
            this.dialog1 = false
            this.dialog2 = false
            let message = {
                from: this.userId,
                to: this.user.id,
                time: moment().format('YYYY-MM-DD HH:mm:ss'),
                data: '请求添加好友:  </br>' + this.addFriend.verify,
                type: 'ADD_FRIEND',
                other: this.addFriend.remark + "-" + this.addFriend.group
            }
            this.$socket.emit('sendEvent', message)
            this.$message.success("发送成功")
        }
    },
    mounted() {
        this.getGroup()
    }
}
</script>

<style scoped>
.from {
    font: 18px large;
    color: #909399;
}
</style>
