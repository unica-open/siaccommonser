/*
*SPDX-FileCopyrightText: Copyright 2020 | CSI Piemonte
*SPDX-License-Identifier: EUPL-1.2
*/
package it.csi.siac.siaccommonser.util.proxy;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.remoting.jaxws.JaxWsPortProxyFactoryBean;

import it.csi.siac.siaccommonser.util.log.LogSrvUtil;
import it.csi.siac.siaccorser.model.ServiceRequest;


public class SiacJaxWsPortProxyFactoryBean extends JaxWsPortProxyFactoryBean{

	@Override
	protected Object doInvoke(MethodInvocation invocation) throws Throwable {

		Object[] arguments = invocation.getArguments();

		for (Object argument : arguments) {
			if (argument instanceof ServiceRequest) {
				ServiceRequest req = ((ServiceRequest) argument);
				req.setUserSessionInfo(LogSrvUtil.TL_USER_SESSION_INFO.get());
			}
		}
		
		return super.doInvoke(invocation);
	}
}
