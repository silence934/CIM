package xyz.nyist.constant;

/**
 * @Author : fucong
 * @Date: 2021-03-15 15:01
 * @Description :
 */
public enum MessageStatus {

    /**
     * 未读
     */
    UN_READ("未读"),

    HAVE_READ("已读"),

    ACCEPT("接受"),

    REJECT("拒绝");


    private final String value;

    MessageStatus(String value) {this.value = value;}

    public String value() {
        return value;
    }
}
