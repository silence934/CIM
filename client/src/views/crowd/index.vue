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
            <li @click="quit">退出该群</li>
        </ul>
    </div>
</template>

<script>
import centerControl from '../../components/CenterControl'
import {getCrowd, quitCrowd} from "@/api/crowd"
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
            rightCrowd: {}
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

