<template>
    <div class="main">
        <div style="width: 100%;height: 80px;padding: 20px 18px 0 18px">
            <div @click="dialogVisible = true" style="position: relative;float:right;font-size:20px;text-align: right;">
                <i class="el-icon-s-operation"></i>
            </div>
            <div style="position: relative;float:left">
                <img style="height: 40px;width: 40px;-webkit-border-radius: 50%;margin-right: 10px" :src="avatar">
            </div>
            <div style="line-height: 1.6;overflow: hidden;">
                <h3 style="margin: 0">{{ name }}</h3>
            </div>
        </div>
        <div class="search">
            <el-input

                    size="mini"
                    placeholder="搜索"
                    prefix-icon="el-icon-search"
            />
        </div>
        <slot></slot>


        <el-dialog :modal=false :visible.sync="dialogVisible" width="130px">
            <div class="select" @click="createGroup">新建分组</div>
            <div class="select" @click="createCrowd">新建群</div>
        </el-dialog>
    </div>

</template>

<script>
import {mapGetters} from "vuex"
import {addGroup} from "@/api/user"
import {addCrowd} from "@/api/crowd"

export default {
    name: 'CenterControl',
    computed: {
        ...mapGetters([
            'name', 'avatar'
        ]),
    },
    data() {
        return {
            dialogVisible: false
        }
    },
    methods: {
        createGroup() {
            this.$prompt('新增分组', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
            }).then(({value}) => {
                if (value) {
                    let data = new FormData();
                    data.append("groupName", value)
                    addGroup(data).then(() => {
                        this.$message.success("操作成功");
                    })
                }
            })
        },
        createCrowd() {
            this.$prompt('新建群', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
            }).then(({value}) => {
                if (value) {
                    let data = new FormData();
                    data.append("name", value)
                    addCrowd(data).then(() => {
                        this.$message.success("操作成功");
                    })
                }
            })
        }
    }
}
</script>

<style scoped lang="scss">
.search {
    padding: 0 10px 10px 10px;
    height: 58px;
    width: 100%;
}

.main {
    width: 280px;
    height: 100%;
    background-color: rgb(236, 236, 236);
    display: inline-block;
    vertical-align: top;
}

> > > .el-dialog {

    width: 130px;
    top: 35px;
    left: 325px;
    margin: 0 !important;

    .el-dialog__header {
        display: none;
    }

    .el-dialog__body {

        padding: 5px 0;

        .select {
            font-size: 16px;
            color: #303133;
            font-family: "Helvetica Neue", Helvetica, "PingFang SC", "Hiragino Sans GB", "Microsoft YaHei", "微软雅黑", Arial, sans-serif;
            cursor: pointer;
            padding: 5px 8px;
        }

        .select:hover {
            background-color: rgb(205, 205, 207);
        }
    }
}


</style>
