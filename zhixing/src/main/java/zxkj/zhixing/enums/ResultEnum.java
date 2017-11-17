package zxkj.zhixing.enums;

public enum ResultEnum {

    UNKNOW_ERROR(-1,"未知错误"),
    SUCCESS(0,"成功");

    private Integer code;
    private String message;

    public Integer getCode() {
        return code;
    }



    public String getMessage() {
        return message;
    }

    ResultEnum(Integer code,String msg){
        this.code = code;
        this.message = msg;
    }
}
