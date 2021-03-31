<template>
    <div style="height: 25px">
        <el-progress :percentage="percentage"/>
        <el-button style="margin: 0;width: 50px;" size="mini" @click="translationEnd">发送</el-button>
        <el-button style="margin: 0;width: 50px;" size="mini" @click="cancel">取消</el-button>
    </div>
</template>

<script>
import Recorder, {ENCODE_TYPE} from 'recorderx';
import {upload} from "@/api/artifact"
import {mapGetters} from "vuex"

export default {
    name: "Voice",
    data() {
        return {
            rc: '',
            percentage: 0
        }
    },
    computed: {
        ...mapGetters([
            'userId', 'chat', 'token', 'name', 'avatar'
        ])
    },
    props: {
        nowActive: {
            type: Object,
            default: {}
        },
    },
    methods: {
        translationStart() {
            this.rc = new Recorder()
            this.$nextTick(() => {
                this.rc.start()
            })
        },

        translationEnd() {
            this.rc.pause()
            let wav = this.rc.getRecord({
                encodeTo: ENCODE_TYPE.WAV,
            });
            this.upload(wav)
        },

        upload(wav) {
            let formData = new FormData()
            let name = this.nowActive.id + this.userId + '.wav'
            formData.append('file', wav, name)
            formData.append('type', 'record')

            upload(formData).then(res => {
                let path = '/proxy/api-v1/artifact' + res.data.path
                this.$emit("sendVideoInfo", path, 'VOICE')
                this.$emit("close")
            })
        },

        setPercentage() {
            if (this.percentage < 100) {
                this.percentage += 0.2;
                setTimeout(() => {
                    this.setPercentage()
                }, 200)
            } else {
                this.translationEnd()
            }
        },

        cancel() {
            this.$emit("close")
        }
    },
    mounted() {
        this.translationStart()
        this.setPercentage()
    }
}
</script>

<style scoped>
>>> .el-progress {
    display: inline-block;
    width: calc(100% - 110px);
    height: 16px;
    margin-top: 9px;
}

>>> .el-progress__text {
    display: none;
}
</style>
