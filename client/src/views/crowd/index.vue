<template>
    <div style="height: 100vh;">
        <centerControl style="position: relative;left: 0">
            <el-menu class="el-menu-vertical-demo">
                <el-menu-item :key="crowd.id"
                              @contextmenu.prevent.native="openMenu($event,crowd)"
                              @click="handleFriend(crowd)"
                              :index="''+index" v-for="(crowd,index) in crowdList">
                    <div style="width: 100%;height: 50px;">
                        <div class="avatar_div">
                            <img class="avatar" :src="crowd.avatar" alt="">
                        </div>
                        <div class="name">
                            {{ crowd.name }}
                        </div>
                    </div>
                </el-menu-item>
            </el-menu>
        </centerControl>
        <div class="right">
            <div class="right_main" v-if="activate.id">
                <img :src="activate.avatar" class="avatar1" alt="">
                <h3 style="font: 20px Extra large">{{ activate.name }}</h3>
                <br>
                <el-button type="primary" @click="sendMessage">发送消息</el-button>
            </div>
            <div class="right_main" v-else>
                未选择
            </div>
        </div>


        <ul v-show="menu.visible" :style="{left:menu.left+'px',top:menu.top+'px'}" class="contextmenu">
            <li v-if="rightCrowd.lordId===userId" @click="dialog=true">编辑群信息</li>
            <li @click="quit">退出该群</li>
        </ul>


        <el-dialog width="530px" :close-on-click-modal="false" title="编辑群信息" :visible.sync="dialog">
            <span class="crowd_label" style="vertical-align: top">群头像</span>
            <el-upload
                    :data="{type:'avatar'}"
                    class="avatar-uploader"
                    action="/api-v1/artifact/upload"
                    :headers="{Authorization: 'Bearer ' + this.token}"
                    :show-file-list="false"
                    :on-success="success"
                    :before-upload="beforeAvatarUpload">
                <img v-if="rightCrowd.avatar" :src="rightCrowd.avatar" class="avatar" alt="">
                <i v-else class="el-icon-plus avatar-uploader-icon"></i>
            </el-upload>
            <br><br>
            <span class="crowd_label">群名称</span>
            <el-input style="width: 380px" v-model="rightCrowd.name" placeholder="请输入群名称"/>
            <br><br>
            <span class="crowd_label" style="vertical-align: top">群公告</span>
            <el-input type="textarea" :rows="2" style="width: 380px" v-model="rightCrowd.announcement" placeholder="请输入群公告"/>
            <br><br>
            <div style="text-align: right">
                <el-button @click="dialog=false;getCrowd()" type="info">取消</el-button>
                <el-button @click="updateCrowdInfo()" type="primary">确定</el-button>
            </div>
        </el-dialog>
    </div>
</template>

<script>
import centerControl from '../../components/CenterControl'
import {getCrowd, quitCrowd, updateCrowd} from "@/api/crowd"
import {quillRedefine} from "vue-quill-editor-upload"
import {mapGetters} from "vuex"
import router from "@/router"

export default {
    name: 'group',
    components: {quillRedefine, centerControl},
    data() {
        return {
            crowdList: [],
            activate: {},
            groupId: '',
            menu: {
                visible: false,
                left: 0,
                top: 0
            },
            rightCrowd: {},
            dialog: false
        }
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
    computed: {
        ...mapGetters([
            'userId', 'chat', 'token', 'name', 'avatar'
        ]),
        userData() {
            return this.$route.query
        },
        visible() {
            return this.menu.visible;
        }
    },
    methods: {
        getCrowd() {
            getCrowd().then(res => {
                this.crowdList = res.data
            })
        },
        handleFriend(key) {
            this.activate = key
        },
        sendMessage() {
            router.push({path: '/message', query: this.activate})
        },
        openMenu(e, crowd) {
            this.rightCrowd = crowd
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
        quit() {
            this.$confirm('确定退出吗', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                let data = new FormData();
                data.append("crowdId", this.rightCrowd.id)
                quitCrowd(data).then(() => {
                    this.$message({
                        type: 'success',
                        message: '操作成功!'
                    })
                    this.menu.visible = false
                    this.getCrowd()
                })
            })
        },
        beforeAvatarUpload(file) {
            const isJPG = /^image/.test(file.type);
            const isLt3M = file.size / 1024 / 1024 < 3;
            if (!isJPG) {
                this.$message.error('上传头像只能是图片!');
            }
            if (!isLt3M) {
                this.$message.error('上传头像图片大小不能超过 3MB!');
            }
            this.rightCrowd.avatar = URL.createObjectURL(file);
            return isJPG && isLt3M;
        },
        success(response) {
            this.rightCrowd.avatar = response.data.path
            this.$message.success("保存后生效")
        },
        updateCrowdInfo() {
            updateCrowd(this.rightCrowd).then(() => {
                this.$message.success("操作成功")
                this.dialog = false
                this.getCrowd()
            }).catch(() => {
                this.dialog = false
            })
        }
    },
    mounted() {
        this.getCrowd()
    },
}
</script>

<style scoped lang="scss">

> > > .el-input__inner {
    line-height: 30px;
    height: 30px;
}

.crowd_label {
    margin-right: 8px;
    color: #303133;
    font: 16px Medium;
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

> > > .avatar-uploader {

    width: 83px;
    display: inline-block;

    .avatar-uploader .el-upload {
        border: 1px dashed #d9d9d9;
        border-radius: 6px;
        cursor: pointer;
        position: relative;
        overflow: hidden;
    }

    .avatar-uploader .el-upload:hover {
        border-color: #409EFF;
    }

    .avatar-uploader-icon {
        font-size: 28px;
        color: #8c939d;
        width: 150px;
        height: 150px;
        line-height: 178px;
        text-align: center;
    }

    .avatar {
        width: 80px;
        height: 80px;
        display: block;
        border-radius: 0;
    }

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
</style>

