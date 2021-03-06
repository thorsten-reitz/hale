/*
 * Copyright (c) 2012 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.server.webapp;

import org.apache.wicket.authorization.strategies.page.SimplePageAuthorizationStrategy;
import org.apache.wicket.core.request.handler.PageProvider;
import org.apache.wicket.core.request.handler.RenderPageRequestHandler;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.cycle.AbstractRequestCycleListener;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import eu.esdihumboldt.hale.server.security.UserConstants;
import eu.esdihumboldt.hale.server.webapp.pages.ExceptionPage;
import eu.esdihumboldt.hale.server.webapp.pages.LoginPage;
import eu.esdihumboldt.hale.server.webapp.pages.SecuredPage;

/**
 * A basic class for web applications
 * 
 * @author Michel Kraemer
 * @author Simon Templer
 */
public abstract class BaseWebApplication extends WebApplication {

	/**
	 * The default title of a web application
	 */
	public static final String DEFAULT_TITLE = "HALE Web";

	/**
	 * Name of the system property that allows to specify a custom main title.
	 */
	public static final String SYSTEM_PROPERTY_MAIN_TITLE = "hale.webapp.maintitle";

	/**
	 * Name of the system property that allows enabling/disabling the login
	 * page.
	 */
	public static final String SYSTEM_PROPERTY_LOGIN_PAGE = "hale.webapp.loginpage";

	/**
	 * Get the default application title. Is either the value of the system
	 * property {@value #SYSTEM_PROPERTY_MAIN_TITLE} or {@link #DEFAULT_TITLE}.
	 * 
	 * @return the default title
	 */
	public static String getDefaulTitle() {
		return System.getProperty(SYSTEM_PROPERTY_MAIN_TITLE, DEFAULT_TITLE);
	}

	/**
	 * @return the main title of this application
	 */
	public String getMainTitle() {
		return getDefaulTitle();
	}

	/**
	 * States if the login page is enabled for this application. The default
	 * implementation looks at the {@value #SYSTEM_PROPERTY_LOGIN_PAGE} system
	 * property for this, if not specified the default is <code>false</code>.
	 * 
	 * @return if the login page is enabled
	 */
	public boolean isLoginPageEnabled() {
		return Boolean.parseBoolean(System.getProperty(SYSTEM_PROPERTY_LOGIN_PAGE, "false"));
	}

	@Override
	public void init() {
		super.init();

		// enforce mounts so security interceptors based on URLs can't be fooled
		getSecuritySettings().setEnforceMounts(true);

		getSecuritySettings().setAuthorizationStrategy(
				new SimplePageAuthorizationStrategy(SecuredPage.class, LoginPage.class) {

					@Override
					protected boolean isAuthorized() {
						SecurityContext securityContext = SecurityContextHolder.getContext();
						if (securityContext != null) {
							Authentication authentication = securityContext.getAuthentication();
							if (authentication != null && authentication.isAuthenticated()) {
								for (GrantedAuthority authority : authentication.getAuthorities()) {
									if (authority.getAuthority().equals(UserConstants.ROLE_USER)
											|| authority.getAuthority().equals(
													UserConstants.ROLE_ADMIN)) {

										// allow access only for users/admins
										return true;
									}
								}
							}
						}

						return false;
					}

				});

		getComponentInstantiationListeners().add(new SpringComponentInjector(this));

		getRequestCycleListeners().add(new AbstractRequestCycleListener() {

			@Override
			public IRequestHandler onException(RequestCycle cycle, Exception ex) {
				return new RenderPageRequestHandler(new PageProvider(new ExceptionPage(ex)));
			}
		});

		// add login page to every application based on this one (if enabled)
		if (isLoginPageEnabled()) {
			// login page
			mountPage("/login", LoginPage.class);
		}
	}

}
