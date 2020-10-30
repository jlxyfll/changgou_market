package com.changgou.goods.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.goods.dao.*;
import com.changgou.goods.pojo.*;
import com.changgou.goods.service.SpuService;
import com.changgou.util.IdWorker;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class SpuServiceImpl implements SpuService {

    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private CategoryBrandMapper categoryBrandMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private BrandMapper brandMapper;

    /**
     * 查询全部列表
     *
     * @return
     */
    @Override
    public List<Spu> findAll() {
        return spuMapper.selectAll();
    }

    /**
     * 根据ID查询
     *
     * @param id
     * @return
     */
    @Override
    public Spu findById(String id) {
        return spuMapper.selectByPrimaryKey(id);
    }


    /**
     * 增加
     *
     * @param spu
     */
    @Override
    public void add(Spu spu) {
        spuMapper.insert(spu);
    }


    /**
     * 修改
     *
     * @param spu
     */
    @Override
    public void update(Spu spu) {
        spuMapper.updateByPrimaryKey(spu);
    }

    /**
     * 删除
     *
     * @param id
     */
    @Override
    public void delete(String id) {
        spuMapper.deleteByPrimaryKey(id);
    }


    /**
     * 条件查询
     *
     * @param searchMap
     * @return
     */
    @Override
    public List<Spu> findList(Map<String, Object> searchMap) {
        Example example = createExample(searchMap);
        return spuMapper.selectByExample(example);
    }

    /**
     * 分页查询
     *
     * @param page
     * @param size
     * @return
     */
    @Override
    public Page<Spu> findPage(int page, int size) {
        PageHelper.startPage(page, size);
        return (Page<Spu>) spuMapper.selectAll();
    }

    /**
     * 条件+分页查询
     *
     * @param searchMap 查询条件
     * @param page      页码
     * @param size      页大小
     * @return 分页结果
     */
    @Override
    public Page<Spu> findPage(Map<String, Object> searchMap, int page, int size) {
        PageHelper.startPage(page, size);
        Example example = createExample(searchMap);
        return (Page<Spu>) spuMapper.selectByExample(example);
    }


    /**
     * 构建查询对象
     *
     * @param searchMap
     * @return
     */
    private Example createExample(Map<String, Object> searchMap) {
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        if (searchMap != null) {
            // 主键
            if (searchMap.get("id") != null && !"".equals(searchMap.get("id"))) {
                criteria.andEqualTo("id", searchMap.get("id"));
            }
            // 货号
            if (searchMap.get("sn") != null && !"".equals(searchMap.get("sn"))) {
                criteria.andEqualTo("sn", searchMap.get("sn"));
            }
            // SPU名
            if (searchMap.get("name") != null && !"".equals(searchMap.get("name"))) {
                criteria.andLike("name", "%" + searchMap.get("name") + "%");
            }
            // 副标题
            if (searchMap.get("caption") != null && !"".equals(searchMap.get("caption"))) {
                criteria.andLike("caption", "%" + searchMap.get("caption") + "%");
            }
            // 图片
            if (searchMap.get("image") != null && !"".equals(searchMap.get("image"))) {
                criteria.andLike("image", "%" + searchMap.get("image") + "%");
            }
            // 图片列表
            if (searchMap.get("images") != null && !"".equals(searchMap.get("images"))) {
                criteria.andLike("images", "%" + searchMap.get("images") + "%");
            }
            // 售后服务
            if (searchMap.get("saleService") != null && !"".equals(searchMap.get("saleService"))) {
                criteria.andLike("saleService", "%" + searchMap.get("saleService") + "%");
            }
            // 介绍
            if (searchMap.get("introduction") != null && !"".equals(searchMap.get("introduction"))) {
                criteria.andLike("introduction", "%" + searchMap.get("introduction") + "%");
            }
            // 规格列表
            if (searchMap.get("specItems") != null && !"".equals(searchMap.get("specItems"))) {
                criteria.andLike("specItems", "%" + searchMap.get("specItems") + "%");
            }
            // 参数列表
            if (searchMap.get("paraItems") != null && !"".equals(searchMap.get("paraItems"))) {
                criteria.andLike("paraItems", "%" + searchMap.get("paraItems") + "%");
            }
            // 是否上架
            if (searchMap.get("isMarketable") != null && !"".equals(searchMap.get("isMarketable"))) {
                criteria.andEqualTo("isMarketable", searchMap.get("isMarketable"));
            }
            // 是否启用规格
            if (searchMap.get("isEnableSpec") != null && !"".equals(searchMap.get("isEnableSpec"))) {
                criteria.andEqualTo("isEnableSpec", searchMap.get("isEnableSpec"));
            }
            // 是否删除
            if (searchMap.get("isDelete") != null && !"".equals(searchMap.get("isDelete"))) {
                criteria.andEqualTo("isDelete", searchMap.get("isDelete"));
            }
            // 审核状态
            if (searchMap.get("status") != null && !"".equals(searchMap.get("status"))) {
                criteria.andEqualTo("status", searchMap.get("status"));
            }

            // 品牌ID
            if (searchMap.get("brandId") != null) {
                criteria.andEqualTo("brandId", searchMap.get("brandId"));
            }
            // 一级分类
            if (searchMap.get("category1Id") != null) {
                criteria.andEqualTo("category1Id", searchMap.get("category1Id"));
            }
            // 二级分类
            if (searchMap.get("category2Id") != null) {
                criteria.andEqualTo("category2Id", searchMap.get("category2Id"));
            }
            // 三级分类
            if (searchMap.get("category3Id") != null) {
                criteria.andEqualTo("category3Id", searchMap.get("category3Id"));
            }
            // 模板ID
            if (searchMap.get("templateId") != null) {
                criteria.andEqualTo("templateId", searchMap.get("templateId"));
            }
            // 运费模板id
            if (searchMap.get("freightId") != null) {
                criteria.andEqualTo("freightId", searchMap.get("freightId"));
            }
            // 销量
            if (searchMap.get("saleNum") != null) {
                criteria.andEqualTo("saleNum", searchMap.get("saleNum"));
            }
            // 评论数
            if (searchMap.get("commentNum") != null) {
                criteria.andEqualTo("commentNum", searchMap.get("commentNum"));
            }

        }
        return example;
    }

    /**
     * 新增商品
     *
     * @param goods
     */
    @Transactional
    @Override
    public void addGoods(Goods goods) {
        // 根据ID在数据库中查询商品，如果有，则不新增，如果没有，则新增
        // 判断goods是否为空，如果为空则返回
        if (goods != null) {
            // 1、新增spu信息
            Spu spu = goods.getSpu();
            // 通过雪花算法生成ID
            spu.setId(String.valueOf(idWorker.nextId()));
            spuMapper.insertSelective(spu);
            // 2、新增skuList信息
            addSkuList(goods, spu);
        }
    }

    private void addSkuList(Goods goods, Spu spu) {
        List<Sku> skuList = goods.getSkuList();
        for (Sku sku : skuList) {
            if (skuList != null) {
                // 设置skuId
                sku.setId(String.valueOf(idWorker.nextId()));
                // 设置spuId
                sku.setSpuId(spu.getId());
                // 设置创建时间
                sku.setCreateTime(new Date());
                // 设置更新时间
                sku.setUpdateTime(new Date());
                // 如果spec为空，设置为“{}”
                String spec = sku.getSpec();
                if (StringUtils.isEmpty(spec)) {
                    sku.setSpec("{}");
                }
                // 设置sku的名称
                String spuName = spu.getName();
                // 将json字符串转化为Map
                Map<String, String> map = JSON.parseObject(spec, Map.class);
                if (map != null && map.size() > 0) {
                    for (String key : map.keySet()) {
                        spuName += " " + map.get(key);
                    }
                }
                sku.setName(spuName);
                // 设置分类名称
                Category category = categoryMapper.selectByPrimaryKey(spu.getCategory3Id());
                if (category != null) {
                    sku.setCategoryName(category.getName());
                    // 设置分来Id
                    sku.setCategoryId(spu.getCategory3Id());
                }
                // 设置品牌名称
                Brand brand = brandMapper.selectByPrimaryKey(spu.getBrandId());
                if (brand != null) {
                    sku.setBrandName(brand.getName());
                }
                // 创建品牌与分类关联关系，如果有关联关系则不插入，如果没有关联关系则插入
                CategoryBrand categoryBrand = new CategoryBrand();
                categoryBrand.setBrandId(spu.getBrandId());
                categoryBrand.setCategoryId(spu.getCategory3Id());
                int count = categoryBrandMapper.selectCount(categoryBrand);
                if (count == 0) {
                    categoryBrandMapper.insertSelective(categoryBrand);
                }
            }
            skuMapper.insertSelective(sku);
        }
    }

    /**
     * 根据spuId查询商品
     *
     * @param spuId
     */
    @Override
    public Goods findBySpuId(String spuId) {
        // 1、根据spuId查询spu
        Spu spu = spuMapper.selectByPrimaryKey(spuId);

        // 2、根据spuId查询sku列表
        Example example = new Example(Sku.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("spuId", spuId);
        List<Sku> skuList = skuMapper.selectByExample(example);

        // 3、封装Goods
        Goods goods = new Goods();
        goods.setSpu(spu);
        goods.setSkuList(skuList);
        return goods;
    }

    /**
     * 更新商品
     *
     * @param goods
     * @return
     */
    @Transactional
    @Override
    public void updateGoods(Goods goods) {

        // 1、更新spu
        Spu spu = goods.getSpu();
        String spuId = spu.getId();
        Spu spuInDb = spuMapper.selectByPrimaryKey(spuId);
        if (spuInDb != null) {
            spuMapper.updateByPrimaryKeySelective(spu);

            // 3、删除sku
            Example example = new Example(Sku.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("spuId", spuId);
            skuMapper.deleteByExample(example);

            // 4、更新sku数据
            addSkuList(goods, spu);
        } else {
            throw new RuntimeException("您要更新的商品不存在");
        }
    }

    /**
     * 审核商品
     *
     * @param spuId
     */
    @Override
    public void auditGoods(String spuId) {
        // 1、根据id查询商品
        if (spuId != null) {
            Spu spu = spuMapper.selectByPrimaryKey(spuId);
            // 2、设置status为1
            if (spu == null) {
                throw new RuntimeException("您要审核的商品不存在");
            }
            spu.setStatus("1");
            // 3、执行更新
            spuMapper.updateByPrimaryKey(spu);
        }
    }

    /**
     * 上架商品
     *
     * @param spuId
     */
    @Override
    public void upGoods(String spuId) {
        // 1、判断spuId是否为空
        if (spuId != null) {
            // 根据id查找数据库是否有该商品
            Spu spu = spuMapper.selectByPrimaryKey(spuId);
            if (spu != null) {
                if ("1".equals(spu.getStatus())) {
                    // 2、设置spu为上架状态
                    spu.setIsMarketable("1");
                    // 3、设置sku为上架状态
                    Example example = new Example(Sku.class);
                    Example.Criteria criteria = example.createCriteria();
                    criteria.andEqualTo("spuId", spuId);
                    List<Sku> skuList = skuMapper.selectByExample(example);
                    if (skuList != null && skuList.size() > 0) {
                        for (Sku sku : skuList) {
                            sku.setStatus("1");
                            skuMapper.updateByPrimaryKeySelective(sku);
                        }
                    }
                } else {
                    throw new RuntimeException("该商品未通过审核");
                }
            } else {
                throw new RuntimeException("该商品不存在");
            }
            // 上架商品
            spuMapper.updateByPrimaryKey(spu);
        }
    }

    /**
     * 下架商品
     *
     * @param spuId
     */
    @Override
    public void downGoods(String spuId) {
        // 1、根据spuId查询出spu
        Spu spu = spuMapper.selectByPrimaryKey(spuId);
        // 2、如果spu不为空并且上架状态为未上架，则无需下架
        if (spu != null) {
            if ("1".equals(spu.getIsMarketable())) {
                // 3、设置spu上架状态为已下架
                spu.setIsMarketable("0");
                // 4、设置sku状态为下架
                Example example = new Example(Sku.class);
                Example.Criteria criteria = example.createCriteria();
                criteria.andEqualTo("spuId", spuId);
                List<Sku> skuList = skuMapper.selectByExample(example);
                if (skuList != null && skuList.size() > 0) {
                    for (Sku sku : skuList) {
                        sku.setStatus("2");
                        skuMapper.updateByPrimaryKeySelective(sku);
                    }
                }
            } else if ("0".equals(spu.getIsMarketable())) {
                throw new RuntimeException("该商品未上架，无需下架");
            }
        } else {
            throw new RuntimeException("该商品不存在");
        }
        // 5、更新spu状态
        spuMapper.updateByPrimaryKey(spu);
    }

    /**
     * 逻辑删除商品
     *
     * @param spuId
     */
    @Transactional
    @Override
    public void deleteLogic(String spuId) {
        // 1、根据spuId查询spu
        if (spuId != null) {
            Spu spu = spuMapper.selectByPrimaryKey(spuId);
            if (spu != null) {
                // 2、商品未上架，可以删除，已上架，不能删除
                String marketable = spu.getIsMarketable();
                if ("0".equals(marketable)) {
                    spu.setIsDelete("1");
                    // 3、设置sku状态为已删除
                    Example example = new Example(Sku.class);
                    Example.Criteria criteria = example.createCriteria();
                    criteria.andEqualTo("spuId", spuId);
                    List<Sku> skuList = skuMapper.selectByExample(example);
                    if (skuList != null && skuList.size() > 0) {
                        for (Sku sku : skuList) {
                            sku.setStatus("3");
                            skuMapper.updateByPrimaryKeySelective(sku);
                        }
                    }
                } else if ("1".equals(marketable)) {
                    throw new RuntimeException("该商品已上架，不能删除");
                }
            } else {
                throw new RuntimeException("该商品不存在，无需删除");
            }
            spuMapper.updateByPrimaryKey(spu);
        }
    }

    /**
     * 恢复商品
     *
     * @param spuId
     */
    @Transactional
    @Override
    public void restoreGoods(String spuId) {
        // 1、根据spuId查询spu
        if (spuId != null) {
            Spu spu = spuMapper.selectByPrimaryKey(spuId);
            if (spu != null) {
                String delete = spu.getIsDelete();
                if ("1".equals(delete)) {
                    spu.setIsDelete("0");
                    // 2、设置sku状态为正常
                    Example example = new Example(Sku.class);
                    Example.Criteria criteria = example.createCriteria();
                    criteria.andEqualTo("spuId", spuId);
                    List<Sku> skuList = skuMapper.selectByExample(example);
                    if (skuList != null && skuList.size() > 0) {
                        for (Sku sku : skuList) {
                            sku.setStatus("1");
                            skuMapper.updateByPrimaryKeySelective(sku);
                        }
                    }
                } else if ("0".equals(delete)) {
                    throw new RuntimeException("该商品未删除，无需恢复");
                }
            } else {
                throw new RuntimeException("该商品不存在，无发恢复");
            }
            spuMapper.updateByPrimaryKey(spu);
        }
    }

    /**
     * 物理删除该商品
     *
     * @param spuId
     */
    @Transactional
    @Override
    public void deleteGoods(String spuId) {
        // 1、根据spuId查询spu
        if (spuId != null) {
            Spu spu = spuMapper.selectByPrimaryKey(spuId);
            if (spu != null) {
                // 2、商品未上架，可以删除，商品已上架，不能删除
                String marketable = spu.getIsMarketable();
                if ("0".equals(marketable)) {
                    // 3、删除spu
                    spuMapper.deleteByPrimaryKey(spuId);
                    // 4、删除sku
                    Example example = new Example(Sku.class);
                    Example.Criteria criteria = example.createCriteria();
                    criteria.andEqualTo("spuId", spuId);
                    skuMapper.deleteByExample(example);
                    // 5、删除关联关系
                    Integer categoryId = spu.getCategory3Id();
                    Integer brandId = spu.getBrandId();
                    if (categoryId != null && brandId != null) {
                        Example example1 = new Example(CategoryBrand.class);
                        Example.Criteria criteria1 = example1.createCriteria();
                        criteria1.andEqualTo("categoryId", categoryId);
                        criteria1.andEqualTo("brandId", brandId);
                        categoryBrandMapper.deleteByExample(example1);
                    }
                } else if ("1".equals(marketable)) {
                    throw new RuntimeException("该商品已上架，不能删除");
                }
            } else {
                throw new RuntimeException("该商品不存在，无需删除");
            }
        }
    }
}
