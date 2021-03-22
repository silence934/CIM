<script>
import ContactsData from "../database/contacts";
import MessagesData from "../database/messages";

export default {
    name: "QqImui",
    components: {},
    data() {
        return {};
    },
    render() {
        return (
                "\n" +
                "      <div class=\"qq-lemon-imui\">\n" +
                "        <lemon-imui\n" +
                "          class=\"lemon-slot\"\n" +
                "          user={UserData}\n" +
                "          width={900}\n" +
                "          avatar-cricle\n" +
                "          hide-message-name\n" +
                "          hide-message-time\n" +
                "          ref=\"IMUI\"\n" +
                "          on={{\n" +
                "            \"pull-messages\": this.handlePullMessages,\n" +
                "            \"change-contact\": this.handleChangeContact,\n" +
                "            send: this.handleSend,\n" +
                "          }}\n" +
                "          scopedSlots={{\n" +
                "            \"message-title\": contact => {\n" +
                "              return (\n" +
                "                <div>\n" +
                "                  <div style=\"display:flex;justify-content:space-between\">\n" +
                "                    <span>{contact.displayName}</span>\n" +
                "                    <span style=\"font-size:12px;\">\n" +
                "                      <span>打开抽屉：</span>\n" +
                "                      <span\n" +
                "                        class=\"cursor:pointer;\"\n" +
                "                        on-click={() => this.openDrawer(\"right\")}\n" +
                "                      >\n" +
                "                        右侧 |{\" \"}\n" +
                "                      </span>\n" +
                "                      <span\n" +
                "                        class=\"cursor:pointer;\"\n" +
                "                        on-click={() => this.openDrawer(\"rightInside\")}\n" +
                "                      >\n" +
                "                        右侧内部 |{\" \"}\n" +
                "                      </span>\n" +
                "                      <span\n" +
                "                        class=\"cursor:pointer;\"\n" +
                "                        on-click={() => this.openDrawer(\"center\")}\n" +
                "                      >\n" +
                "                        居中\n" +
                "                      </span>\n" +
                "                    </span>\n" +
                "                  </div>\n" +
                "                  {contact.isGroup && (\n" +
                "                    <div class=\"slot-group-menu\">\n" +
                "                      <span>聊天</span>\n" +
                "                      <span>公告</span>\n" +
                "                      <span>相册</span>\n" +
                "                      <span>文件</span>\n" +
                "                      <span>活动</span>\n" +
                "                      <span\n" +
                "                        v-lemon-contextmenu_click={[\n" +
                "                          {\n" +
                "                            text: \"操作一\",\n" +
                "                          },\n" +
                "                          {\n" +
                "                            text: \"操作二\",\n" +
                "                          },\n" +
                "                        ]}\n" +
                "                      >\n" +
                "                        设置(左键弹出菜单)\n" +
                "                      </span>\n" +
                "                    </div>\n" +
                "                  )}\n" +
                "                </div>\n" +
                "              );\n" +
                "            },\n" +
                "            \"sidebar-contact-fixedtop\": contact => {\n" +
                "              return (\n" +
                "                <div class=\"slot-contact-fixedtop\">\n" +
                "                  <input class=\"slot-search\" placeholder=\"搜索通讯录\" />\n" +
                "                </div>\n" +
                "              );\n" +
                "            },\n" +
                "            \"message-side\": contact => {\n" +
                "              if (contact.isGroup) {\n" +
                "                return (\n" +
                "                  <div class=\"slot-group\">\n" +
                "                    <div class=\"slot-group-title\">群通知</div>\n" +
                "                    <div class=\"slot-group-notice\">\n" +
                "                      进群请改备注，格式，工作地点-姓名，请大家配合谢谢\n" +
                "                    </div>\n" +
                "                    <div class=\"slot-group-title\">群成员</div>\n" +
                "                    <div class=\"slot-group-panel\">\n" +
                "                      <input class=\"slot-search\" placeholder=\"搜索群成员\" />\n" +
                "                      <div class=\"slot-group-member\">河南-96-十里青山</div>\n" +
                "                      <div class=\"slot-group-member\">河南-96-十里青山</div>\n" +
                "                      <div class=\"slot-group-member\">河南-96-十里青山</div>\n" +
                "                      <div class=\"slot-group-member\">河南-96-十里青山</div>\n" +
                "                      <div class=\"slot-group-member\">河南-96-十里青山</div>\n" +
                "                      <div class=\"slot-group-member\">河南-96-十里青山</div>\n" +
                "                      <div class=\"slot-group-member\">河南-96-十里青山</div>\n" +
                "                      <div class=\"slot-group-member\">河南-96-十里青山</div>\n" +
                "                      <div class=\"slot-group-member\">河南-96-十里青山</div>\n" +
                "                      <div class=\"slot-group-member\">河南-96-十里青山</div>\n" +
                "                    </div>\n" +
                "                  </div>\n" +
                "                );\n" +
                "              }\n" +
                "            },\n" +
                "          }}\n" +
                "        />\n" +
                "      </div>"
        );
    },
    computed: {},
    watch: {},
    created() {
    },
    mounted() {
        const IMUI = this.$refs.IMUI;
        //contactData3.id = "333";
        IMUI.initContacts(ContactsData);
        IMUI.changeContact(13);
        //console.log(IMUI.getContacts());
        // SlotIMUI.initEmoji(emojiData);
    },
    methods: {
        openDrawer(position) {
            const IMUI = this.$refs.IMUI;
            const params = {
                position,
                render: contact => {
                    return (
                            " <div style=\"padding:15px\">\n" +
                            "              <h5>{contact.displayName}</h5>\n" +
                            "              <span on-click={IMUI.closeDrawer}>关闭抽屉</span>\n" +
                            "            </div>"
                    );
                },
            };
            if (position == "center") {
                params.width = "50%";
                params.height = "50%";
            } else if (position == "rightInside") {
                params.height = "90%";
                params.offsetY = "10%";
            }
            IMUI.openDrawer(params);
        },
        handlePullMessages(contact, next) {
            next(MessagesData[contact.id], true);
        },
        handleChangeContact() {
        },
        handleSend(message, next, file) {
            console.log(message, next, file);
            setTimeout(() => {
                next();
            }, 1000);
        },
    },
};
</script>
<style lang="stylus">
.slot-group
    width 170px
    border-left 1px solid #ddd;
    height 100%
    box-sizing border-box
    padding 10px

    .slot-search
        margin 5px 0

.slot-group-notice
    color #999
    padding 6px 0
    font-size 12px

.slot-group-title
    font-size 12px

.slot-group-member
    font-size 12px
    line-height 18px

.slot-group-menu span
    display inline-block
    cursor pointer
    color #888
    margin 4px 10px 0 0
    border-bottom 2px solid transparent;

    &:hover
        color #000
        border-color #333

.slot-contact-fixedtop
    padding 10px
    border-bottom 1px solid #ddd

.slot-search
    width 100%
    box-sizing border-box
    font-size 14px
    border 1px solid #bbb
    padding 5px 10px
</style>
