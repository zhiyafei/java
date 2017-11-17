package zxkj.zhixing.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import zxkj.zhixing.domain.Result;
import zxkj.zhixing.domain.User;
import zxkj.zhixing.service.UserService;
import zxkj.zhixing.utils.ResultUtil;

import javax.jws.soap.SOAPBinding;
import java.util.List;

@RestController
public class UserController {

    @Autowired
    private UserService userService;
    /**
     * 获取用户列表
     */
    @RequestMapping(value = "/userlist",method = RequestMethod.GET)
    public Result<User> getUserList(){return ResultUtil.success(userService.getUserList());}

    /**
     * 通过id删除用户
     */
    @RequestMapping(value = "/deleteUserById/{id}",method = RequestMethod.GET)
    public Result deleteOne(@PathVariable("id") Integer id){
        return ResultUtil.success(userService.deleteUserById(id));
    }

    /**
     * 增加用户
     */
    @RequestMapping(value = "/addUser",method = RequestMethod.GET)
    public void addOne(){}

    /**
     * 更新用户
     */
    @RequestMapping(value = "/updateUser/{id}",method = RequestMethod.POST)
    public void updateUser(){}

    /**
     * 获取用户通过id
     */

    @RequestMapping(value = "/getUserById/{id}",method = RequestMethod.GET)
    public Result<User> getUserById(@PathVariable("id") Integer id){
        Object result = userService.getUserById(id);

            return ResultUtil.success(result);

    }
}
