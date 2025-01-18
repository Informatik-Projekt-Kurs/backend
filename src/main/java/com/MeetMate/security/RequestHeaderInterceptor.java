package com.MeetMate.security;

import org.jetbrains.annotations.NotNull;
import org.springframework.graphql.server.WebGraphQlInterceptor;
import org.springframework.graphql.server.WebGraphQlRequest;
import org.springframework.graphql.server.WebGraphQlResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Component
public class RequestHeaderInterceptor implements WebGraphQlInterceptor {

  @Override
  public @NotNull Mono<WebGraphQlResponse> intercept(WebGraphQlRequest request, @NotNull Chain chain) {
    String value = request.getHeaders().getFirst("Authorization");
    if (value != null) {
      request.configureExecutionInput((executionInput, builder) ->
          builder.graphQLContext(Collections.singletonMap("token", value)).build());
    }
    return chain.next(request);
  }
}
