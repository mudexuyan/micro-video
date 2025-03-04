package edu.ustc.service.impl;

import edu.ustc.dao.VideoDao;
import edu.ustc.entity.Category;
import edu.ustc.entity.User;
import edu.ustc.entity.Video;
import edu.ustc.feighclients.CategoryClients;
import edu.ustc.feighclients.UserClients;
import edu.ustc.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 视频(Video)表服务实现类
 *
 * @author makejava
 * @since 2022-10-01 14:33:51
 */
@Service
@Transactional
public class VideoServiceImpl implements VideoService {
    @Resource
    private VideoDao videoDao;

    @Autowired
    private CategoryClients categoryClients;

    @Autowired
    private UserClients userClients;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public Video queryById(Integer id) {
        return this.videoDao.queryById(id);
    }

    /**
     * 分页查询
     *
     * @param video       筛选条件
     * @param pageRequest 分页对象
     * @return 查询结果
     */
    @Override
    public Page<Video> queryByPage(Video video, PageRequest pageRequest) {
        long total = this.videoDao.count(video);
        return new PageImpl<>(this.videoDao.queryAllByLimit(video, pageRequest), pageRequest, total);
    }

    /**
     * 新增数据
     *
     * @param video 实例对象
     * @return 实例对象
     */
    @Override
    public Video insert(Video video) {
        video.setCreatedAt(new Date());//设置创建日期
        video.setUpdatedAt(new Date());//设置更新日期
        this.videoDao.insert(video);
        return video;
    }

    /**
     * 修改数据
     *
     * @param video 实例对象
     * @return 实例对象
     */
    @Override
    public Video update(Video video) {
        this.videoDao.update(video);
        return this.queryById(video.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Integer id) {
        return this.videoDao.deleteById(id) > 0;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<Video> findAllByKeywords(int offset, int limit, String id, String title, String categoryId, String username) {
        int start = (offset - 1) * limit;
        List<Video> res = videoDao.findAllByKeywords(start, limit, id, title, categoryId, username);
        res.forEach(v -> {
            User user = userClients.user(v.getUid().toString());
            v.setUploader(user);
            Category category = categoryClients.category(v.getCategoryId().toString());
            v.setCategory(category.getName());
        });
        return res;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public Long findTotalCountsByKeywords(String id, String name, String categoryId, String username) {
        return videoDao.findTotalCountsByKeywords(id, name, categoryId, username);
    }
}
