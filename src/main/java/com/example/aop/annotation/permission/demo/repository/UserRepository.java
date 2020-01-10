package com.example.aop.annotation.permission.demo.repository;

import com.example.aop.annotation.permission.demo.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface UserRepository extends JpaRepository<User,Long> {
    /**
     * @Description 查询指定用户名用户数据
     * @param userName
     * @Date 10:22 2020/1/3
     * @return
     */
    User findByUserName(String userName);

    /**
     * 查询指定ID用户信息
     * @param id
     * @return
     */
    User findById(Integer id);

    /**
     * @Description 自定义Sql查询.(这个本来是HQL的写法,我的运行不了,改成了本地的SQL)
     * @Date 10:18 2020/1/3
     * @Param pageable
     * @return org.springframework.data.domain.Page<com.jpa.springdatajpa.model.User>
     **/
    @Query(value = "select * from user",nativeQuery = true)
    Page<User> findALL(Pageable pageable);

    /**
     * @Description 原生SQL的写法,?1表示方法参数中的顺序
     * @Date 10:20 2020/1/3
     * @Param nickName pageable
     * @return org.springframework.data.domain.Page<com.jpa.springdatajpa.model.User>
     **/
    @Query(value = "select * from user where nick_name = ?1",nativeQuery = true)
    Page<User> findByNickName(String nickName, Pageable pageable);

    /**
     * @Description 修改,添加事务的支持
     * @Date 10:21 2020/1/3
     * @Param userName id
     * @return int
     **/
    @Transactional(timeout = 10)
    @Modifying
    @Query("update User set userName = ?1 where id = ?2")
    int modifyById(String  userName, Long id);

    /**
     * @Description 删除
     * @Date 10:22 2020/1/3
     * @Param id
     * @return void
     **/
    @Transactional
    @Modifying
    @Query("delete from User where id = ?1")
    @Override
    void deleteById(Long id);
}
