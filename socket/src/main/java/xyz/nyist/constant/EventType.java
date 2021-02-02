package xyz.nyist.constant;

/**
 * @Author : fucong
 * @Date: 2021-02-02 13:31
 * @Description :
 */
public enum EventType {

    /**
     * socket 消息事件
     */
    SOCKET_MESSAGE("messageevent");

    private final String value;

    EventType(String value) {this.value = value;}

    public String value() {
        return value;
    }
}
