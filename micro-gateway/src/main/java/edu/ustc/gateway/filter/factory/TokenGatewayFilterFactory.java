package edu.ustc.gateway.filter.factory;

import edu.ustc.constants.RedisPrefix;
import edu.ustc.exceptions.IllegalTokenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Component
public class TokenGatewayFilterFactory extends AbstractGatewayFilterFactory<TokenGatewayFilterFactory.Config> {

    private static final Logger log = LoggerFactory.getLogger(TokenGatewayFilterFactory.class);

    private RedisTemplate redisTemplate;

    @Autowired
    public TokenGatewayFilterFactory(RedisTemplate redisTemplate) {
        super(Config.class);
        this.redisTemplate = redisTemplate;
    }


    public TokenGatewayFilterFactory(){
        super((Config.class));
    }

    @Override
    public GatewayFilter apply(Config config) {
        log.info("自定义filter");
        return new GatewayFilter() {
            @Override
            public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
                log.info("config required token: {}", config.requiredToken);
                log.info("config name: {}", config.name);
                if (config.requiredToken) {
                    //1.获取token信息
                    if (exchange.getRequest().getQueryParams().get("token") == null) {
                        throw new IllegalTokenException("非法令牌!");
                    }
                    String token = exchange.getRequest().getQueryParams().get("token").get(0);
                    log.info("token:{}", token);
                    //2.根据token信息去redis获取
                    if (!redisTemplate.hasKey(RedisPrefix.TOKEN_KEY+token)) {
                        throw new IllegalTokenException("不合法的令牌!");
                    }
                }
                return chain.filter(exchange);
            }
        };
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return Arrays.asList("requiredToken", "name");
    }



    public static class Config{
        private boolean requiredToken;  //false
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isRequiredToken() {
            return requiredToken;
        }

        public void setRequiredToken(boolean requiredToken) {
            this.requiredToken = requiredToken;
        }
    }

}
