package com.hmdp.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.hmdp.entity.ShopType;
import com.hmdp.mapper.ShopTypeMapper;
import com.hmdp.service.IShopTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.hmdp.utils.RedisConstants.CACHE_SHOP_TTL;
import static com.hmdp.utils.RedisConstants.CACHE_SHOP_TYPE_KEY;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements IShopTypeService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public List<ShopType> listBySort() {
        String key = CACHE_SHOP_TYPE_KEY + "sort";
        // 1.从redis查询缓存
        String shopTypeJson = stringRedisTemplate.opsForValue().get(key);
        // 2.判断是否存在
        if (StrUtil.isNotBlank(shopTypeJson)){
            // 3.存在，直接返回
            List<ShopType> typeList = JSONUtil.toList(shopTypeJson, ShopType.class);
            return typeList;
        }
        // 4.不存在，根据id查询数据库
        List<ShopType> typeList = this
                .query().orderByAsc("sort").list();
        // 5. 整个 List 序列化成一个 JSON 数组写入缓存
        stringRedisTemplate.opsForValue().set(
                key,
                JSONUtil.toJsonStr(typeList),
                CACHE_SHOP_TTL, TimeUnit.MINUTES
        );
        // 6. 返回
        return typeList;
    }
}
