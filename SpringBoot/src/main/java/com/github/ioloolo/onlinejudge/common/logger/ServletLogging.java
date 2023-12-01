package com.github.ioloolo.onlinejudge.common.logger;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
public class ServletLogging implements WebMvcConfigurer {

	private final LoggingInterceptor loggingInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {

		registry.addInterceptor(loggingInterceptor).addPathPatterns("/**");
	}

	@Component
	@Slf4j
	protected static class LoggingInterceptor implements HandlerInterceptor {

		private static final String REQ_START_TIME = "RequestStartTime";

		@Override
		public boolean preHandle(
				@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull Object handler
		) {

			request.setAttribute(REQ_START_TIME, System.currentTimeMillis());

			String method = "%6s".formatted(request.getMethod());
			String url = "%-20s".formatted(getSubUrl(request.getRequestURI()));
			String remoteAddress = "%s".formatted(request.getRemoteAddr());

			log.info("[Request ] {} {} {}", method, url, remoteAddress);

			return true;
		}

		@Override
		public void postHandle(
				@Nonnull HttpServletRequest request,
				@Nonnull HttpServletResponse response,
				@Nonnull Object handler,
				ModelAndView modelAndView
		) {

			long startTime = (long) request.getAttribute(REQ_START_TIME);
			long endTime = System.currentTimeMillis();
			long executionTime = endTime - startTime;

			String method = "%6s".formatted(request.getMethod());
			String url = "%-20s".formatted(getSubUrl(request.getRequestURI()));
			String executionTimeStr = "%6s".formatted(executionTime + "ms");
			String remoteAddress = "%s".formatted(request.getRemoteAddr());

			log.info("[Response] {} {} {} {}", method, url, remoteAddress, executionTimeStr);
		}

		private String getSubUrl(String requestUri) {

			try {
				URI uri = new URI(requestUri);
				return uri.getPath();
			} catch (URISyntaxException e) {
				return requestUri;
			}
		}
	}
}
