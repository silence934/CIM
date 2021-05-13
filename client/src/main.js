import Vue from 'vue'

import 'normalize.css/normalize.css' // A modern alternative to CSS resets
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import locale from 'element-ui/lib/locale/lang/zh-CN' // lang i18n
import '@/styles/index.scss' // global css
import App from './App'
import store from './store'
import router from './router'

import '@/icons' // icon
import '@/permission' // permission control
import VueQuillEditor from 'vue-quill-editor'
// require styles
import 'quill/dist/quill.core.css'
import 'quill/dist/quill.snow.css'
import 'quill/dist/quill.bubble.css'
import {mockXHR} from '../mock'

import VueSocketIO from 'vue-socket.io'
import {HappyScroll} from 'vue-happy-scroll'
// 引入css
import 'vue-happy-scroll/docs/happy-scroll.css'

import Moment from 'moment'
import AudioRecorder from 'vue-audio-recorder'

Vue.filter('formatDate', function (value) {
    Moment.locale('zh-cn')
    let differDay = Moment(Moment().format('YYYY-MM-DD')).diff(Moment(value).format('YYYY-MM-DD'), 'days')
    if (differDay === 1) {
        return '昨天'
    } else if (differDay === 0) {
        return Moment(value).format('HH:mm:ss')
    } else if (differDay <= 7) {
        return Moment(value).format('dddd')
    } else {
        return Moment(value).format('YYYY-MM-DD')
    }
})

Vue.use(new VueSocketIO({
    debug: true,
    connection: '',
    vuex: {
        store,
        actionPrefix: 'SOCKET_',
        mutationPrefix: 'SOCKET_'
    },
    options: {path: '', autoConnect: true, transports: ['websocket']},
    query: {userName: 'admin'}
}))
Vue.use(VueQuillEditor)
Vue.use(AudioRecorder)

//自定义组件名
Vue.component('happy-scroll', HappyScroll)

if (process.env.NODE_ENV === 'production') {
    mockXHR()
}
Vue.use(ElementUI, {locale})

Vue.config.productionTip = false

new Vue({
    el: '#app',
    router,
    store,
    render: h => h(App)
})
