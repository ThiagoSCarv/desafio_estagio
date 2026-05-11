package com.thiago.desafio_estagio.shared.wicket;

import org.apache.wicket.protocol.http.WicketFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WicketConfig {

    @Bean
    public FilterRegistrationBean<WicketFilter> wicketFilter() {
        FilterRegistrationBean<WicketFilter> registration = new FilterRegistrationBean<>();
        WicketFilter filter = new WicketFilter();
        registration.setFilter(filter);
        // Usa o factory do wicket-spring para obter o WicketApplication do contexto Spring
        registration.addInitParameter(
            WicketFilter.APP_FACT_PARAM,
            "org.apache.wicket.spring.SpringWebApplicationFactory"
        );
        registration.addInitParameter(WicketFilter.FILTER_MAPPING_PARAM, "/*");
        registration.addUrlPatterns("/*");
        // Ordem 1: Wicket intercepta primeiro; URLs não reconhecidas passam para o DispatcherServlet (REST)
        registration.setOrder(1);
        return registration;
    }
}
