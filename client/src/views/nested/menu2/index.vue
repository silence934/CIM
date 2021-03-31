<template>
    <div id="app">

    </div>
</template>

<script>
import router from "@/router"

export default {
    name: "app",
    mounted() {
        this.$store.dispatch('user/logout')
        router.push({path: '/login'})
    }
};
</script>

<style></style>
