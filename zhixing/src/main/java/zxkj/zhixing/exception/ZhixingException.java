package zxkj.zhixing.exception;


import zxkj.zhixing.enums.ResultEnum;

public class ZhixingException extends RuntimeException {

    private Integer code;

    public ZhixingException(ResultEnum resultEnum) {
        super(resultEnum.getMessage());
        this.code = resultEnum.getCode();
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
