package com.changgou.service;

import com.alibaba.fastjson.JSON;
import com.changgou.pojo.SkuInfo;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
@Service
public class EsSearchServiceImpl implements EsSearchService {
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Override
    public Map search(Map<String, String> searchMap) {
        // 定义返回结果
        Map result = new HashMap();
        // 判读searchMap是否为空
        if (searchMap == null) {
            return result;
        }
        // 构建综合搜索条件类，可以进行模糊搜索、精确搜索、范围搜索
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        // 根据搜索关键词在商品搜索页面的搜索框进行搜索，相当于mysql的 select * from tb_sku where name like "%关键词的分词%"
        // must相当于and，should相当于or,not相当于not
        if (StringUtils.isNotEmpty(searchMap.get("keywords"))) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("name", searchMap.get("keywords")).operator(Operator.AND));
        }
        // 构建顶级查询对象
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        nativeSearchQueryBuilder.withQuery(boolQueryBuilder);
        // 执行搜索
        /*List<SkuInfo> skuInfos = elasticsearchTemplate.queryForList(nativeSearchQueryBuilder.build(), SkuInfo.class);*/
        AggregatedPage<SkuInfo> searchResult = elasticsearchTemplate.queryForPage(nativeSearchQueryBuilder.build(), SkuInfo.class, new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> clazz, Pageable pageable) {
                // 搜索命中的结果集
                SearchHit[] hits = response.getHits().getHits();
                List<T> skuList = new ArrayList<>();
                // 搜索命中的结果集的条数
                long totalHits = response.getHits().getTotalHits();
                if (hits != null && hits.length > 0) {
                    for (SearchHit hit : hits) {
                        // 获取搜索命中的每一条商品的JSON数据
                        String skuInfoJson = hit.getSourceAsString();
                        // 将搜索商品的json字符串转为指定的类型
                        SkuInfo skuInfo = JSON.parseObject(skuInfoJson, SkuInfo.class);
                        skuList.add((T) skuInfo);
                    }
                }
                return new AggregatedPageImpl<>(skuList, pageable, totalHits, response.getAggregations());
            }
        });
/*        if (skuInfos != null) {
            result.put("rows", skuInfos);
            result.put("total", skuInfos.size());
        }*/
        if (searchResult != null) {
            // 搜索结果列表
            result.put("rows", searchResult.getContent());
            // 搜索结果的总条数
            result.put("total", searchResult.getTotalElements());
            // 总页数，默认的是1页，除非手动操作分页
            result.put("totalpage", searchResult.getTotalPages());
        }
        return result;
    }
}
