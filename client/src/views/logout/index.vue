<template>
    <div id="app">

    </div>
</template>

<script>
import {mapState} from "vuex"

export default {
    name: "app",
    computed: {
        ...mapState({
            userId: state => state.user.user_id
        })
    },
    mounted() {
        this.$socket.emit('logoutEvent', {from: this.userId, type: "LOGOUT"})
        this.$store.dispatch('user/logout')
    }
};
</script>

<style></style>
