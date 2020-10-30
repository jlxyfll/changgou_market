package com.changgou.goods.service;

import com.changgou.goods.pojo.Goods;
import com.changgou.goods.pojo.Spu;
import com.github.pagehelper.Page;

import java.util.List;
import java.util.Map;

public interface SpuService {

    /***
     * 查询所有
     * @return
     */
    List<Spu> findAll();

    /**
     * 根据ID查询
     *
     * @param id
     * @return
     */
    Spu findById(String id);

    /***
     * 新增
     * @param spu
     */
    void add(Spu spu);

    /***
     * 修改
     * @param spu
     */
    void update(Spu spu);

    /***
     * 删除
     * @param id
     */
    void delete(String id);

    /***
     * 多条件搜索
     * @param searchMap
     * @return
     */
    List<Spu> findList(Map<String, Object> searchMap);

    /***
     * 分页查询
     * @param page
     * @param size
     * @return
     */
    Page<Spu> findPage(int page, int size);

    /***
     * 多条件分页查询
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    Page<Spu> findPage(Map<String, Object> searchMap, int page, int size);

    /**
     * 新增商品
     *
     * @param goods
     */
    void addGoods(Goods goods);

    /**
     * 根据spuId查询商品
     *
     * @param spuId
     */
    Goods findBySpuId(String spuId);

    /**
     * 更新商品
     *
     * @param goods
     * @return
     */
    void updateGoods(Goods goods);

    /**
     * 审核商品
     * @param spuId
     */
    void auditGoods(String spuId);

    /**
     * 上架商品
     * @param spuId
     */
    void upGoods(String spuId);

    /**
     * 下架商品
     * @param spuId
     */
    void downGoods(String spuId);

    /**
     * 逻辑删除商品
     * @param spuId
     */
    void deleteLogic(String spuId);

    /**
     * 恢复商品
     * @param spuId
     */
    void restoreGoods(String spuId);

    /**
     * 物理删除商品
     * @param spuId
     */
    void deleteGoods(String spuId);
}
