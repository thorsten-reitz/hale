/*
 * Copyright (c) 2013 Simon Templer
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
 *     Simon Templer - initial version
 */

package eu.esdihumboldt.hale.server.api.controller;

import javax.servlet.http.HttpServletRequest

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

import eu.esdihumboldt.hale.server.api.RestAPI
import eu.esdihumboldt.hale.server.api.internal.wadl.doc.DocScope
import eu.esdihumboldt.hale.server.api.internal.wadl.doc.WDoc

/**
 * Basic controller for static information.
 * 
 * @author Simon Templer
 */
@Controller
class Main implements RestAPI {

	/**
	 * Get the REST API version.
	 * 
	 * @param writer the response writer
	 * @throws IOException if writing to the response fails
	 */
	@WDoc(
	title = 'API version',
	content = { 'Plain text representation of the sequential API version number.' },
	scope = DocScope.RESOURCE
	)
	@RequestMapping(value = '/version', method = RequestMethod.GET, produces = 'text/plain')
	public void getVersion(Writer writer) throws IOException {
		writer.write(String.valueOf(VERSION));
	}

	/**
	 * Get the API base URL.
	 * 
	 * @param request the HTTP servlet request
	 * @return the base URL w/o trailing slash
	 */
	static String getBaseUrl(HttpServletRequest request) {
		StringBuilder builder = new StringBuilder()
		builder << request.scheme
		builder << '://'
		builder << request.serverName
		builder << ':'
		builder << request.serverPort
		builder << request.contextPath
		String servPath = request.servletPath
		if (servPath) {
			builder << servPath
		}
		builder.toString();
	}
}
