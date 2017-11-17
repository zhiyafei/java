package zxkj.zhixing.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import zxkj.zhixing.domain.User;
import zxkj.zhixing.repository.UserRepository;

import java.util.List;

@Service
public class UserService  {

    @Autowired
    private UserRepository userRepository;

    public List<User> getUserById(Integer id){return userRepository.findById(id);}

    public  List<User> getUserList(){return userRepository.findAll();}

    public Object deleteUserById(Integer id){
        try {
            userRepository.delete(id);
        }catch (Exception e){

        }

        return null;
    }
}
