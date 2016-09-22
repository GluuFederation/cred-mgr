package org.gluu.credmgr.config;

import org.gluu.credmgr.domain.OPAuthority;
import org.gluu.credmgr.security.CustomAccessDeniedHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.data.repository.query.SecurityEvaluationContextExtension;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
            .antMatchers(HttpMethod.OPTIONS, "/**")
            .antMatchers("/app/**/*.{js,html}")
            .antMatchers("/bower_components/**")
            .antMatchers("/i18n/**")
            .antMatchers("/content/**")
            .antMatchers("/swagger-ui/index.html")
            .antMatchers("/test/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .exceptionHandling()
            .accessDeniedHandler(new CustomAccessDeniedHandler())
            .and()
            .logout()
            .deleteCookies("JSESSIONID", "CSRF-TOKEN")
            .permitAll()
            .and()
            .headers()
            .frameOptions()
            .disable()
            .and()
            .authorizeRequests()
            .antMatchers("/api/openid/reset_password/init").permitAll()
            .antMatchers("/api/openid/reset_password/finish").permitAll()
            .antMatchers("/api/openid/account").permitAll()
            .antMatchers("/api/openid/logout-redirect").permitAll()
            .antMatchers("/api/openid/login-redirect").permitAll()
            .antMatchers("/api/openid/reset/options").permitAll()
            .antMatchers("/api/openid/login-uri").permitAll()
            .antMatchers("/api/openid/register").permitAll()
            .antMatchers("/api/openid/settings").hasAuthority(OPAuthority.OP_ADMIN.toString())
            .antMatchers("/api/openid/**").authenticated()
            .antMatchers("/api/profile-info").permitAll()
            .antMatchers("/api/**").authenticated()
            .antMatchers("/management/**").hasAuthority(OPAuthority.OP_ADMIN.toString())
            .antMatchers("/v2/api-docs/**").permitAll()
            .antMatchers("/configuration/ui").permitAll()
            .antMatchers("/swagger-ui/index.html").hasAuthority(OPAuthority.OP_ADMIN.toString())
            .and()
            .requiresChannel().anyRequest().requiresSecure().and().portMapper().http(8080).mapsTo(8443).http(80).mapsTo(443);

    }

    @Bean
    public SecurityEvaluationContextExtension securityEvaluationContextExtension() {
        return new SecurityEvaluationContextExtension();
    }
}
