/*******************************************************************************
 * Copyright (C) 2021 Johannes Schmidt
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package de.vdi.vdi2770.web.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;

import de.vdi.vdi2770.web.filter.PreAuthentitactedTokenFilter;

/**
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 *
 */
@Configuration
@EnableWebSecurity
@Order(1)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Value("${vdi2770.http.auth.tokenName:Api-Key}")
	private String authHeaderName;

	@Value("${vdi2770.http.auth.tokenValue:XPLSUAWB1CNWQF0FQZXUAFXXCQPSRGHMO6OAMN2L}")
	private String authTokenValue;

	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		PreAuthentitactedTokenFilter filter = new PreAuthentitactedTokenFilter(this.authHeaderName);

		filter.setAuthenticationManager(new AuthenticationManager() {
			@Override
			public Authentication authenticate(Authentication authentication)
					throws AuthenticationException {
				String principal = (String) authentication.getPrincipal();

				if (!SecurityConfiguration.this.authTokenValue.equals(principal)) {
					throw new BadCredentialsException(
							"The API key was not found or not the expected value.");
				}
				authentication.setAuthenticated(true);
				return authentication;
			}
		});

		httpSecurity.cors().and().antMatcher("/rest/**").csrf().disable().sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().addFilter(filter)
				.addFilterBefore(new ExceptionTranslationFilter(new Http403ForbiddenEntryPoint()),
						filter.getClass())
				.authorizeRequests().anyRequest().authenticated();

		System.out.println("REST token: " + this.authTokenValue);
	}

}
