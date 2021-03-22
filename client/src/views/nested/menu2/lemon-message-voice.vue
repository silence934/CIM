<script>
export default {
    name: "lemonMessageVoice",
    inheritAttrs: false,
    inject: ["IMUI"],
    render() {
        return (
                " <lemon-message-basic\n" +
                "        class=\"lemon-message-voice\"\n" +
                "        props={{ ...this.$attrs }}\n" +
                "        scopedSlots={{\n" +
                "          content: (props) => {\n" +
                "            return <span>{props.content}&nbsp;🔈</span>;\n" +
                "          },\n" +
                "        }}\n" +
                "      />"
        );
    },
};
</script>
<style lang="stylus">
.lemon-message.lemon-message-voice {
    user-select: none;

    .lemon-message__content {
        border: 2px solid #000;
        font-size: 12px;
        cursor: pointer;

        &::before {
            display: none;
        }
    }
}
</style>
