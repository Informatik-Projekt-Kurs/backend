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
  private final int maxRequests = 5;
  private final long refreshTime = 1000 * 10; // 10 seconds

  @Override
  protected void doFilterInternal(
      @NotNull HttpServletRequest request,
      @NotNull HttpServletResponse response,
      @NotNull FilterChain filterChain)
      throws ServletException, IOException {

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
    for (int i = 0; i < requests.get(ip).size(); i++) {
      if (System.currentTimeMillis() - requests.get(ip).get(i) > refreshTime)
        requests.get(ip).remove(i);
    }
  }

}
