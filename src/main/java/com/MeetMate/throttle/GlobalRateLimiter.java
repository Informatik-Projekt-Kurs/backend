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
import java.util.LinkedList;

@Component
@RequiredArgsConstructor
public class GlobalRateLimiter extends OncePerRequestFilter {

  private final LinkedList<Long> requests = new LinkedList<>();
  private final int maxRequests = 500;
  private final long refreshTime = 1000 * 1; // 1 second

  @Override
  protected void doFilterInternal(
      @NotNull HttpServletRequest request,
      @NotNull HttpServletResponse response,
      @NotNull FilterChain filterChain)
      throws ServletException, IOException {

    requests.addLast(System.currentTimeMillis());

    clearRequests();

    if (requests.size() > maxRequests) {
      response.setStatus(429);
      response.getWriter().write("Too many requests");
      return;
    }

    filterChain.doFilter(request, response);
  }

  private void clearRequests() {
    while (!requests.isEmpty()
        && System.currentTimeMillis() - requests.getFirst() > refreshTime)
      requests.remove();

  }

}
