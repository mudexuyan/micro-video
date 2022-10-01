package edu.ustc.service.impl;

import edu.ustc.dao.UserDao;
import edu.ustc.entity.User;
import edu.ustc.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * 用户(User)表服务实现类
 *
 * @author makejava
 * @since 2022-10-01 12:50:31
 */
@Transactional
@Service
public class UserServiceImpl implements UserService {
    @Resource
    private UserDao userDao;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public User queryById(Integer id) {
        return this.userDao.queryById(id);
    }

    /**
     * 分页查询
     *
     * @param user 筛选条件
     * @param pageRequest      分页对象
     * @return 查询结果
     */
    @Override
    public Page<User> queryByPage(User user, PageRequest pageRequest) {
        long total = this.userDao.count(user);
        return new PageImpl<>(this.userDao.queryAllByLimit(user, pageRequest), pageRequest, total);
    }

    /**
     * 新增数据
     *
     * @param user 实例对象
     * @return 实例对象
     */
    @Override
    public User insert(User user) {
        this.userDao.insert(user);
        return user;
    }

    /**
     * 修改数据
     *
     * @param user 实例对象
     * @return 实例对象
     */
    @Override
    public User update(User user) {
        this.userDao.update(user);
        return this.queryById(user.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Integer id) {
        return this.userDao.deleteById(id) > 0;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS) //SUPPORTS 支持事务： 外层存在事务融入当前事务  外层不存在事务 不会开启新的事务
    public List<User> findAllByKeywords(int offset, int limit, String id, String name, String phone) {
        int start = (offset - 1) * limit;
        return userDao.findAllByKeywords(start, limit, id, name, phone);
    }



    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public Long findTotalCountsByKeywords(String id, String name, String phone) {
        return this.userDao.findTotalCountsByKeywords(id, name, phone);
    }
}
