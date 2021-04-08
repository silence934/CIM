<template>
    <div class="app-container">
        <el-form ref="dataForm" :rules="rules" :model="form" label-width="120px">
            <el-form-item label="头像">
                <el-col :span="6">
                    <img @click="cropShow" v-if="form.avatar" :src="form.avatar" class="avatar" alt="">
                </el-col>
            </el-form-item>

            <el-row>
                <el-col :span="12">
                    <el-form-item label="账号">
                        <el-input v-model="form.username" :disabled="true"/>
                    </el-form-item>
                </el-col>
            </el-row>

            <el-row>
                <el-col :span="12">
                    <el-form-item label="昵称" prop="nickname">
                        <el-input v-model="form.nickname" :disabled="isDisabled"/>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="性别" prop="sex">
                        <el-select v-model="form.sex" placeholder="请选择性别" :disabled="isDisabled" value="">
                            <el-option label="男" value="男"/>
                            <el-option label="女" value="女"/>
                        </el-select>
                    </el-form-item>
                </el-col>

            </el-row>

            <el-row>
                <el-col :span="12">
                    <el-form-item label="电话" prop="tel">
                        <el-input v-model="form.phone" :disabled="isDisabled"/>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="邮箱" prop="mail">
                        <el-input v-model="form.mail" :disabled="isDisabled"/>
                    </el-form-item>
                </el-col>
            </el-row>
        </el-form>
        <el-row>
            <el-col :span="24" style="text-align: center">
                <el-button type="primary" @click="onSubmit">保存</el-button>
                <el-button type="danger" @click="updateS()">修改密码</el-button>
            </el-col>
        </el-row>

        <el-dialog title="修改密码" :visible.sync="updateShow" width="450px">
            <el-form label-position="left" ref="active2" :model="form1" :rules="rule">
                <el-form-item label="旧密码" prop="oldPass">
                    <el-input type="password" v-model="form1.oldPass" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="新密码" prop="pass">
                    <el-input type="password" v-model="form1.pass" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="确认新密码" prop="checkPass">
                    <el-input type="password" v-model="form1.checkPass" autocomplete="off"></el-input>
                </el-form-item>
            </el-form>
            <span slot="footer" class="dialog-footer">
                 <el-button @click="updateShow = false">取 消</el-button>
                 <el-button type="primary" @click="updatePwd">确 定</el-button>
            </span>

        </el-dialog>

        <image-cropper
                v-show="imageCropperShow"
                :noCircle="true"
                :params="cropParams"
                url="/api-v1/artifact/upload"
                @close="imageCropperShow = false"
                @crop-upload-success="cropSuccess"
        />

    </div>
</template>

<script>
import {getInfo, update, updatePwdSelf} from '@/api/user'
import {mapGetters} from "vuex"
import ImageCropper from "@/components/ImageCropper/index"

export default {
    data() {
        let validatePass = (rule, value, callback) => {
            if (value === '') {
                callback(new Error('请输入密码'));
            } else {
                if (!/^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,11}$/.test(value)) {
                    callback(new Error('密码6-11位字母和数字'));
                } else if (this.form1.checkPass !== '') {
                    this.$refs.active2.validateField('checkPass');
                }
                callback();
            }
        }
        let validatePass2 = (rule, value, callback) => {
            if (value === '') {
                callback(new Error('请再次输入密码'));
            } else if (value !== this.form1.pass) {
                callback(new Error('两次输入密码不一致!'));
            } else {
                callback();
            }
        }
        return {
            updateShow: false,
            tagType: ['success', '', 'info', 'danger', 'warning'],
            form: {
                id: '',
                username: '',
                phone: '',
                mail: '',
                nickname: '',
                avatar: '',
                sex: ''
            },
            isDisabled: false,
            isSubmit: false,
            rules: {
                nickname: [{required: true, message: '不能为空', trigger: 'change'}],
                phone: [{required: true, message: '不能为空', trigger: 'change'}, {
                    validator: (rule, value, callback) => {
                        if (value === '') {
                            callback(new Error('不能为空'));
                        } else if (!/^[1][3,4,5,7,8][0-9]{9}$/.test(value)) {
                            callback(new Error('请输入正确的手机号码'));
                        } else {
                            callback();
                        }
                    }, trigger: ['blur', 'change']
                }],
                mail: [{required: true, message: '不能为空', trigger: 'change'}, {type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change']}],
                sex: [{required: true, message: '不能为空', trigger: 'change'}]
            },
            form1: {
                oldPass: '',
                pass: '',
                checkPass: ''
            },
            rule: {
                oldPass: [{required: true, message: '请输入密码', trigger: 'blur'}],
                pass: [{required: true, validator: validatePass, trigger: 'blur'}],
                checkPass: [{required: true, validator: validatePass2, trigger: 'blur'}],
            },
            dialogVisible1: false,
            cropParams: {
                type: 'avatar',
            },
            imageCropperShow: false,
        }
    },
    computed: {
        ...mapGetters(['token']),
    },
    components: {ImageCropper},
    methods: {
        updateS() {
            this.form1 = {
                oldPass: '',
                pass: '',
                checkPass: ''
            }
            this.updateShow = true
        },
        updatePwd() {
            this.$refs.active2.validate(valid => {
                if (valid) {
                    let data = new FormData()
                    data.append("pwd", this.form1.oldPass)
                    data.append("npwd", this.form1.pass)
                    updatePwdSelf(data).then(res => {
                        this.$message.success("修改成功")
                        this.updateShow = false
                        this.$socket.emit('logoutEvent', {from: this.userId, type: "LOGOUT"})
                        this.$store.dispatch('user/logout')
                    })
                }
            });
        },
        onSubmit() {
            this.$refs.dataForm.validate(valid => {
                if (valid) {
                    this.isSubmit = false
                    update(this.form).then(res => {
                        this.$message.success("保存成功")
                        this.$store.dispatch('user/getInfo')
                        this.$emit("close")
                    })
                }
            });
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
            this.form.avatarUrl = URL.createObjectURL(file);
            return isJPG && isLt3M;
        },
        success(response) {
            this.form.avatar = response.data.path
            this.$message.success("保存后生效")
        },
        getDetails() {
            getInfo().then(res => {
                this.form = res.data;
            });
        },
        cropSuccess(resData) {
            this.form.avatar = '/proxy/api-v1/artifact' + resData.data.path
        },
        cropShow() {
            this.imageCropperShow = true;
        },
    },
    created() {
        this.getDetails();
    }
}
</script>

<style scoped lang="scss">
.el-tag {
    margin-left: 5px;
}

> > > .avatar-uploader {

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
        width: 100px;
        height: 100px;
        display: block;
    }

}

.avatar {
    width: 100px;
    height: 100px;
    display: block;
}

> > > .el-dialog__body {
    padding: 0 10px !important;
}
</style>

