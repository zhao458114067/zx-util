package com.zx.util.service.impl;

import com.zx.util.constant.Constants;
import com.zx.util.service.MyRepository;
import com.zx.util.util.ReflectUtil;
import com.zx.util.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.Id;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;

/**
 * JPA通用功能扩展
 * @author : zhaoxu
 */
public class MyRepositoryImpl<T, ID extends Serializable>
        extends SimpleJpaRepository<T, ID> implements MyRepository<T, ID> {

    private ReflectUtil reflectUtil = new ReflectUtil();

    private EntityManager entityManager;

    private Utils utils = new Utils();

    private Class<T> clazz;

    @Autowired(required = false)
    public MyRepositoryImpl(JpaEntityInformation<T, ID> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.clazz = entityInformation.getJavaType();
        this.entityManager = entityManager;
    }

    /**
     * @param tableMap    查询条件
     * @param excludeAttr 是字符串类型，但是不使用模糊查询的字段，可为空
     * @param joinField   外键关联查询，可为空
     * @param sortAttr    排序，可为空
     * @return Page
     */
    @Override
    public Page<T> findByPage(Map<String, String> tableMap, List<String> excludeAttr, Map joinField, String sortAttr) {
        int current = Integer.valueOf(tableMap.get(Constants.CURRENT));
        int pageSize = Integer.valueOf(tableMap.get(Constants.PAGE_SIZE));

        Pageable pageable;
        if (!StringUtils.isEmpty(sortAttr)) {
            pageable = PageRequest.of(current - 1, pageSize, utils.sortAttr(tableMap, sortAttr));
        } else {
            pageable = PageRequest.of(current - 1, pageSize);
        }

        Specification<T> specification = reflectUtil.createSpecification(tableMap, clazz, excludeAttr, joinField);
        return this.findAll(specification, pageable);
    }

    /**
     * 省去不必要的关联map
     *
     * @param tableMap    查询条件
     * @param excludeAttr 是字符串类型，但是不使用模糊查询的字段，可为空
     * @param sortAttr    排序，可为空
     * @return Page
     */
    @Override
    public Page<T> findByPage(Map<String, String> tableMap, List<String> excludeAttr, String sortAttr) {
        int current = Integer.valueOf(tableMap.get(Constants.CURRENT));
        int pageSize = Integer.valueOf(tableMap.get(Constants.PAGE_SIZE));

        Pageable pageable;
        if (!StringUtils.isEmpty(sortAttr)) {
            pageable = PageRequest.of(current - 1, pageSize, utils.sortAttr(tableMap, sortAttr));
        } else {
            pageable = PageRequest.of(current - 1, pageSize);
        }

        Specification<T> specification = reflectUtil.createSpecification(tableMap, clazz, excludeAttr);
        return this.findAll(specification, pageable);
    }

    /**
     * 省去map以及排序
     *
     * @param tableMap    查询条件
     * @param excludeAttr 是字符串类型，但是不使用模糊查询的字段，可为空
     * @return Page
     */
    @Override
    public Page<T> findByPage(Map<String, String> tableMap, List<String> excludeAttr) {
        int current = Integer.valueOf(tableMap.get(Constants.CURRENT));
        int pageSize = Integer.valueOf(tableMap.get(Constants.PAGE_SIZE));

        Pageable pageable;

        pageable = PageRequest.of(current - 1, pageSize);

        //调用省去map参数的方法
        Specification<T> specification = reflectUtil.createSpecification(tableMap, clazz, excludeAttr);
        return this.findAll(specification, pageable);
    }

    /**
     * @param tableMap 查询条件
     * @return Page
     */
    @Override
    public Page<T> findByPage(Map<String, String> tableMap) {
        int current = Integer.valueOf(tableMap.get(Constants.CURRENT));
        int pageSize = Integer.valueOf(tableMap.get(Constants.PAGE_SIZE));

        Pageable pageable;
        pageable = PageRequest.of(current - 1, pageSize);

        //调用省去map参数的方法
        Specification<T> specification = reflectUtil.createSpecification(tableMap, clazz, null);
        return this.findAll(specification, pageable);
    }

    /**
     * @param tableMap    查询条件
     * @param excludeAttr 是字符串类型，但是不使用模糊查询的字段，可为空
     * @param joinField   外键关联查询，可为空
     * @param sortAttr    排序，可为空
     * @return List
     */
    @Override
    public List<T> findByConditions(Map<String, String> tableMap, List<String> excludeAttr, Map joinField, String sortAttr) {
        Specification<T> specification = reflectUtil.createSpecification(tableMap, clazz, excludeAttr, joinField);

        if (!StringUtils.isEmpty(sortAttr)) {
            return this.findAll(specification, utils.sortAttr(tableMap, sortAttr));
        } else {
            return this.findAll(specification);
        }
    }

    /**
     * 省去不必要的关联map参数
     *
     * @param tableMap    查询条件
     * @param excludeAttr 是字符串类型，但是不使用模糊查询的字段，可为空
     * @param sortAttr    排序，可为空
     * @return List
     */
    @Override
    public List<T> findByConditions(Map<String, String> tableMap, List<String> excludeAttr, String sortAttr) {
        Specification<T> specification = reflectUtil.createSpecification(tableMap, clazz, excludeAttr);

        if (!StringUtils.isEmpty(sortAttr)) {
            return this.findAll(specification, utils.sortAttr(tableMap, sortAttr));
        } else {
            return this.findAll(specification);
        }
    }

    /**
     * @param tableMap    查询条件
     * @param excludeAttr 是字符串类型，但是不使用模糊查询的字段，可为空
     * @return List
     */
    @Override
    public List<T> findByConditions(Map<String, String> tableMap, List<String> excludeAttr) {
        //调用省去map参数的方法
        Specification<T> specification = reflectUtil.createSpecification(tableMap, clazz, excludeAttr);
        return this.findAll(specification);
    }

    /**
     * @param tableMap 查询条件
     * @return List
     */
    @Override
    public List<T> findByConditions(Map<String, String> tableMap) {
        //调用省去map参数的方法
        Specification<T> specification = reflectUtil.createSpecification(tableMap, clazz, null);
        return this.findAll(specification);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void deleteValid(String ids) {
        List<String> strings = Arrays.asList(ids.split(","));
        if (!CollectionUtils.isEmpty(strings)) {
            //获取主键
            List<Field> idAnnoation = reflectUtil.getTargetAnnoation(clazz, Id.class);
            if (!CollectionUtils.isEmpty(idAnnoation)) {
                Field field = idAnnoation.get(0);
                strings.stream().forEach(id -> {
                    T object = this.findOneByAttr(field.getName(), id);
                    if (object != null) {
                        reflectUtil.setValue(object, "valid", 0);
                        this.save(object);
                    }
                });
            }
        }
    }

    @Override
    public T findOneByAttr(String attr, String condition) {
        Specification<T> specification = reflectUtil.createOneSpecification(attr, condition);
        Optional<T> result = this.findOne(specification);

        if (result.isPresent()) {
            return result.get();
        } else {
            return null;
        }
    }

    @Override
    public List<T> findByAttr(String attr, String condition) {
        Specification<T> specification = reflectUtil.createOneSpecification(attr, condition);
        List<T> all = this.findAll(specification);
        return all;
    }

    @Override
    public List<T> findByAttrs(String attr, String conditions) {
        List<T> results = new ArrayList<>();
        if (!StringUtils.isEmpty(conditions)) {
            List<String> cons = Arrays.asList(conditions.split(","));
            cons.stream().forEach(condition -> {
                List<T> byAttr = findByAttr(attr, condition);
                if (byAttr != null) {
                    results.addAll(byAttr);
                }
            });
        }
        return results;
    }
}
