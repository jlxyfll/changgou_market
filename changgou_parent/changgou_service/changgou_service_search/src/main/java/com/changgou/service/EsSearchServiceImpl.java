package com.changgou.service;

import com.alibaba.fastjson.JSON;
import com.changgou.pojo.SkuInfo;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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

        // 需求1：根据搜索关键词在商品搜索页面的搜索框进行搜索，类似于mysql的
        // select * from tb_sku where name like "%关键词的分词%"
        // must相当于and，should相当于or,not相当于not
        if (StringUtils.isNotEmpty(searchMap.get("keywords"))) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("name", searchMap.get("keywords")).operator(Operator.AND));
        }

        // 需求5：根据品牌名称进行精确搜索，类似于mysql的
        // select * from tb_sku where brand_name = '品牌名称'
        if (StringUtils.isNotEmpty(searchMap.get("brandName"))) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("brandName", searchMap.get("brandName")));
        }

        // 需求6：根据分类名称进行精确搜索，类似于mysql的
        // select * from tb_sku where category_name = '分类名称'
        if (StringUtils.isNotEmpty(searchMap.get("categoryName"))) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("categoryName", searchMap.get("categoryName")));
        }

        // 需求7：根据规格名称进行精确搜索，类似于mysql的
        // select * from tb_sku where spec_color = '颜色名'
        for (String key : searchMap.keySet()) {
            if (key.startsWith("spec_")) {
                String specMapKey = key.substring(5);
                String specMapValue = searchMap.get(key);
                boolQueryBuilder.filter(QueryBuilders.termQuery("specMap." + specMapKey + ".keyword", specMapValue));
            }
        }

        // 需求8：根据价格进行范围搜索，类似于mysql的
        // select * from tb_sku where price between 100 and 200
        if (StringUtils.isNotEmpty(searchMap.get("price"))) {
            String price = searchMap.get("price");
            String[] split = price.split("-");
            if (split.length == 2) {
                // 获取最小价格
                String lowPrice = split[0];
                // 获取最大价格
                String highPrice = split[1];
                boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(lowPrice).lte(highPrice));
            }
        }

        // 构建顶级查询对象
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        nativeSearchQueryBuilder.withQuery(boolQueryBuilder);

        // 需求2.1：根据品牌进行分组，设置分组，类似于mysql的 select brand_name from tb_sku group by brand_name
        String brandGroup = "brandGroup";
        TermsAggregationBuilder brandGroupBuilder = AggregationBuilders.terms(brandGroup).field("brandName");
        nativeSearchQueryBuilder.addAggregation(brandGroupBuilder);


        // 需求3.1：根据分类名称进行分组，设置分组，类似于mysql的 select category_name from tb_sku group by category_name
        String categoryGroup = "categoryGroup";
        TermsAggregationBuilder categoryGroupBuilder = AggregationBuilders.terms(categoryGroup).field("categoryName");
        nativeSearchQueryBuilder.addAggregation(categoryGroupBuilder);

        // 需求4.1：根据规格进行分组，设置分组，类似于mysql的 select spec from tb_sku group by spec
        String specGroup = "specGroup";
        TermsAggregationBuilder specGroupBuilder = AggregationBuilders.terms(specGroup).field("spec.keyword");
        nativeSearchQueryBuilder.addAggregation(specGroupBuilder);

        // 需求9：搜索结果进行分页，类似于mysql的
        // select * from tb_sku limit 10,20
        int pageNum = 1;
        int pageSize = 20;
        if (StringUtils.isNotEmpty(searchMap.get("pageNum"))) {
            pageNum = Integer.valueOf(searchMap.get("pageNum"));
        }
        if (StringUtils.isNotEmpty(searchMap.get("pageSize"))) {
            pageSize = Integer.valueOf(searchMap.get("pageSize"));
        }
        nativeSearchQueryBuilder.withPageable(PageRequest.of(pageNum - 1, pageSize));

        // 需求10：对搜索结果进行排序，类似于mysql的
        // select * from tb_sku order by price desc / asc
        if (StringUtils.isNotEmpty(searchMap.get("sortField")) && StringUtils.isNotEmpty(searchMap.get("sortRule"))) {
            String sortField = searchMap.get("sortField");
            String sortRule = searchMap.get("sortRule");
            if ("DESC".equalsIgnoreCase(sortRule)) {
                nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort(sortField).order(SortOrder.DESC));
            } else {
                nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort(sortField).order(SortOrder.ASC));
            }
        }

        // 需求11.1：对搜索结果的商品名称进行高亮显示，设置高亮标签
        HighlightBuilder.Field highlightField = new HighlightBuilder.Field("name").preTags("<span style='color:red'>").postTags("</span>");
        nativeSearchQueryBuilder.withHighlightFields(highlightField);

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

                        // 需求11.2：对搜索结果的商品名称进行高亮显示，获取高亮的名称，覆盖到原来没有高亮名称的skuinfo对象里
                        Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                        if (highlightFields != null && highlightFields.size() > 0) {
                            HighlightField field = highlightFields.get("name");
                            String highlightName = field.getFragments()[0].toString();
                            skuInfo.setName(highlightName);
                        }

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

        // 需求2.2：根据品牌进行分组，取品牌分组的结果
        StringTerms brandTerms = (StringTerms) searchResult.getAggregation(brandGroup);
        List<String> brandList = brandTerms.getBuckets().stream().map(bucket -> bucket.getKeyAsString()).collect(Collectors.toList());
/*        List<StringTerms.Bucket> buckets = brandTerms.getBuckets();
        List<String> brandList = new ArrayList<>();
        if (buckets != null && buckets.size() > 0) {
            for (StringTerms.Bucket bucket : buckets) {
                String brandName = bucket.getKeyAsString();
                brandList.add(brandName);
            }
        }*/

        // 需求3.2：根据分类进行分组，取出分类的聚合结果
        StringTerms categoryTerms = (StringTerms) searchResult.getAggregation(categoryGroup);
        List<String> categoryList = categoryTerms.getBuckets().stream().map(bucket -> bucket.getKeyAsString()).collect(Collectors.toList());

        // 需求4.2：根据规格进行分组，去除规格的分组结果
        StringTerms specTerms = (StringTerms) searchResult.getAggregation(specGroup);
        List<StringTerms.Bucket> buckets = specTerms.getBuckets();
        Map<String, Set<String>> specMap = new HashMap<>();
        if (buckets != null && buckets.size() > 0) {
            Set<String> set = null;
            for (StringTerms.Bucket bucket : buckets) {
                String specName = bucket.getKeyAsString();
                Map<String, String> map = JSON.parseObject(specName, Map.class);
                if (map != null && map.size() > 0) {
                    for (String key : map.keySet()) {
                        if (!specMap.containsKey(key)) {
                            set = new HashSet<>();
                        } else {
                            set = specMap.get(key);
                        }
                        set.add(map.get(key));
                        specMap.put(key, set);
                    }
                }
            }
        }

        if (searchResult != null) {
            // 搜索结果列表
            result.put("rows", searchResult.getContent());
            // 搜索结果的总条数
            result.put("total", searchResult.getTotalElements());
            // 总页数，默认的是1页，除非手动操作分页
            result.put("totalpage", searchResult.getTotalPages());
            // 品牌名称，分组结果集
            result.put("brandList", brandList);
            // 分类名称，分组结果集
            result.put("categoryList", categoryList);
            // 规格名称，分组结果集
            result.put("specMap", specMap);
            // 当前页
            result.put("pageNum", pageNum);
            // 每页显示的条数
            result.put("pageSize", pageSize);
        }
        return result;
    }
}
