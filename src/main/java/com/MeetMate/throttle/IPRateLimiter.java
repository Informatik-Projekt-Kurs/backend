package com.MeetMate.throttle;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

@Component
@RequiredArgsConstructor
public class IPRateLimiter extends OncePerRequestFilter {

  private final HashMap<String, LinkedList<Long>> requests = new HashMap<>();
  private final int maxRequests = 2;
  private final long refreshTime = 1000 * 10; // 10 seconds

  @Override
  protected void doFilterInternal(
      @NotNull HttpServletRequest request,
      @NotNull HttpServletResponse response,
      @NotNull FilterChain filterChain)
      throws ServletException, IOException {

    String url = request.getRequestURI();
    if (url.equals("/api/user/get")) {
      filterChain.doFilter(request, response);
      return;
    }

    String ip = request.getRemoteAddr();

    if (requests.containsKey(ip))
      requests.get(ip).addLast(System.currentTimeMillis());
    else
      requests.put(ip, new LinkedList<Long>(Collections.singleton(System.currentTimeMillis())));

    clearRequests(ip);

    if (requests.get(ip).size() > maxRequests) {
      response.setStatus(429);
      response.getWriter().write("Too many requests");
      return;
    }

    filterChain.doFilter(request, response);
  }

  private void clearRequests(String ip) {
    while (!requests.isEmpty()
        && System.currentTimeMillis() - requests.get(ip).getFirst() > refreshTime)
      requests.get(ip).remove();

  }

}
