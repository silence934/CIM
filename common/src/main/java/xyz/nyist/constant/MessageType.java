package xyz.nyist.constant;

/**
 * @Author : fucong
 * @Date: 2021-03-13 22:20
 * @Description :
 */
public enum MessageType {
    /**
     * socket 登陆消息
     */
    LOGIN("login"),
    /**
     * 退出登录
     */
    LOGOUT("logout"),

    /**
     * 普通消息
     */
    GENERAL("general"),

    /**
     * 图片消息
     */
    IMG("img"),

    /**
     * 添加好友
     */
    ADD_FRIEND("add_friend");

    private final String value;

    MessageType(String value) {this.value = value;}

    public String value() {
        return value;
    }
}
