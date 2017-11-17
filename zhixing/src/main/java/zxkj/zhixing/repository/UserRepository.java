package zxkj.zhixing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zxkj.zhixing.domain.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User,Integer> {
    public List<User> findById(Integer id);
}
