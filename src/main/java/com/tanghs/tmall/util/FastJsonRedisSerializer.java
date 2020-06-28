package com.tanghs.tmall.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.nio.charset.Charset;

/**
 * 通用自定义redis序列化方式
 * 默认在 RedisDesktopManager 等软件查看数据是以 HEX 数据，转换成 json查看
 */
public class FastJsonRedisSerializer<T> implements RedisSerializer<T> {

    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    private Class<T> tClass;

    public FastJsonRedisSerializer(Class<T> tClass) {
        super();
        this.tClass = tClass;
    }

    //解决fastJson autoType is not support错误
    static {
        ParserConfig.getGlobalInstance().addAccept("com.smart.xmsmartcms");
    }

    @Override
    public byte[] serialize(T t) throws SerializationException {
        if(null == t){
            return new byte[0];
        }
        return JSON.toJSONString(t,SerializerFeature.WriteClassName).getBytes(DEFAULT_CHARSET);
    }

    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        if(null == bytes || bytes.length <= 0){
            return null;
        }
        String str = new String(bytes,DEFAULT_CHARSET);
        return (T) JSON.parseObject(str,tClass);
    }
}
