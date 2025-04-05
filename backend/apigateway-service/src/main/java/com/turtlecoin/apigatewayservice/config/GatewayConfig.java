package com.turtlecoin.apigatewayservice.config;

import com.turtlecoin.apigatewayservice.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public GatewayConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auction-service", r -> r.path("/auctions/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://AUCTION-SERVICE"))
                .route("main-service", r -> r.path("/main/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://MAIN-SERVICE"))
                .build();
    }
}

