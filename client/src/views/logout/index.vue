<template>
    <div id="app">

    </div>
</template>

<script>
import router from "@/router"
import {mapState} from "vuex"

export default {
    name: "app",
    computed: {
        ...mapState({
            userId: state => state.user.user_id
        })
    },
    mounted() {
        this.$store.dispatch('user/logout')
        this.$socket.emit('logoutEvent', {from: this.userId, type: "LOGOUT"})
        router.push({path: '/login'})
    }
};
</script>

<style></style>
