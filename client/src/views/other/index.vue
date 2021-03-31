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
                    <el-menu-item @click="dialog3=true" index="1-2">加入群</el-menu-item>
                </el-submenu>
                <!--                <el-menu-item index="2">
                                    <i class="el-icon-menu"></i>
                                    <span slot="title">查看通知</span>
                                </el-menu-item>-->

            </el-menu>
        </centerControl>

        <el-dialog width="580px" :close-on-click-modal="false" title="添加联系人" :visible.sync="dialog1">
            <el-input style="width: 380px" v-model="input" placeholder="账号或昵称"/>
            <el-button @click="findUser" type="primary" icon="el-icon-search">搜索</el-button>
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
            <div v-else-if="user"></div>
            <el-card v-else>
                未搜索到...
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
            <div style="text-align: right">
                <span v-if="user.isExist" style="font: 12px Extra Small;color: #F56C6C">对方已是你的好友，不能重复添加</span>
                <el-button @click="dialog2=false" type="info">取消</el-button>
                <el-button v-if="!user.isExist" @click="addFriend1()" type="primary">确定</el-button>
            </div>
        </el-dialog>

        <el-dialog width="580px" :close-on-click-modal="false" title="加入群" :visible.sync="dialog3">
            <el-input @change="findCrowd" v-model="input1" placeholder="群名称"/>
            <br><br>
            <el-card v-if="crowd&&crowd.id" class="box-card">
                <div style="height: 90px;width: 90px;padding: 5px;display: inline-block">
                    <el-image style="width: 80px;height: 80px" :src="crowd.avatar"/>
                </div>
                <div style="height: 90px;width: 300px;padding: 5px;display: inline-block;vertical-align: 20px">
                    <span class="from">群名称: </span>
                    <span class="from" style="margin-right: 20px">{{ crowd.name }}</span>
                </div>
                <div style="height: 90px;width: 89px;padding: 25px 0;display: inline-block;vertical-align: 40px">
                    <el-button @click="addCrowd" type="primary" icon="el-icon-circle-plus">添加</el-button>
                </div>
            </el-card>
            <div v-else-if="crowd"></div>
            <el-card v-else>
                未搜索到...
            </el-card>
        </el-dialog>

    </div>
</template>

<script>
import centerControl from '../../components/CenterControl'
import {getGroup, getUser} from "@/api/user"
import {mapGetters} from "vuex"
import moment from 'moment'
import {getCrowdByName, joinCrowd} from "@/api/crowd"

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
            dialog3: false,
            dialog4: false,
            input: '',
            input1: '',
            user: {
                avatar: '',
                id: '',
                mail: '',
                nickname: '',
                phone: '',
                sex: '',
                username: '',
                isExist: false
            },
            crowd: {},
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
        findCrowd() {
            getCrowdByName({name: this.input1}).then(res => {
                this.crowd = res.data
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
            if (!this.addFriend.remark) {
                this.$message.warning("请输入备注")
                return
            }
            this.dialog1 = false
            this.dialog2 = false
            let message = {
                from: this.userId,
                to: this.user.id,
                time: moment().format('YYYY-MM-DD HH:mm:ss'),
                data: '请求添加好友  </br>' + (this.addFriend.verify || ''),
                type: 'ADD_FRIEND',
                other: this.addFriend.remark + "-" + this.addFriend.group
            }
            this.$socket.emit('sendEvent', message)
            this.$message.success("发送成功")
        },
        addCrowd() {
            let data = new FormData();
            data.append("userId", this.userId)
            data.append("crowdId", this.crowd.id)
            joinCrowd(data).then(() => {
                this.$message.success("操作成功")
                this.dialog2 = false
                this.dialog3 = false
            })
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
