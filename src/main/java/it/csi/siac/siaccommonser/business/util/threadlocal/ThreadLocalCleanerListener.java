/*
*SPDX-FileCopyrightText: Copyright 2020 | CSI Piemonte
*SPDX-License-Identifier: EUPL-1.2
*/
package it.csi.siac.siaccommonser.business.util.threadlocal;

import java.io.Serializable;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;

import it.csi.siac.siaccommon.util.threadlocal.ThreadLocalUtil;

/**
 * Listener for Servlet events: cleans the registered thread local instances at the end of a listener invocation.
 * <p>
 * Cleans the thread local registered with the utility.
 * <p>
 * MUST be added in <code>web.xml</code> or subclassed and annotated by <code>@ServletContextListener</code>.
 * @author Marchino Alessandro
 *
 */
public class ThreadLocalCleanerListener implements ServletRequestListener, Serializable {
	
	/** For serialization */
	private static final long serialVersionUID = 2656833295878826099L;
	
	@Override
	public void requestDestroyed(ServletRequestEvent event) {
		// Cleans the thread local
		ThreadLocalUtil.cleanThreadLocals();
	}

	@Override
	public void requestInitialized(ServletRequestEvent event) {
		// Do nothing
	}
	
}
