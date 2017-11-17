package zxkj.zhixing.handle;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import zxkj.zhixing.domain.Result;
import zxkj.zhixing.exception.ZhixingException;
import zxkj.zhixing.utils.ResultUtil;

@ControllerAdvice
public class ExceptionHandle {

    private final  static Logger logger = LoggerFactory.getLogger(ExceptionHandle.class);

    public Result handle(Exception e) {
        if (e instanceof ZhixingException) {
            ZhixingException zhixingE = (ZhixingException)e;
            return ResultUtil.error(zhixingE.getCode(),zhixingE.getMessage());
        }else {
            logger.error("【-----------系统异常----------】{}",e);
            return ResultUtil.error(-1,"未知错误");
        }
    }

}
