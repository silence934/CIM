package xyz.nyist.constant;

/**
 * @Author : fucong
 * @Date: 2021-02-02 13:31
 * @Description :
 */
public enum EventName {

    /**
     * socket 消息事件
     */
    SOCKET_MESSAGE("receiveEvent"),

    GET_CHAT("getChat");

    private final String value;

    EventName(String value) {this.value = value;}

    public String value() {
        return value;
    }
}
