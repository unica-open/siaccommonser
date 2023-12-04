/*
*SPDX-FileCopyrightText: Copyright 2020 | CSI Piemonte
*SPDX-License-Identifier: EUPL-1.2
*/
package it.csi.siac.siaccommonser.util.log;

import it.csi.siac.siaccommon.model.UserSessionInfo;
import it.csi.siac.siaccommon.util.log.LogUtil;

public class LogSrvUtil extends LogUtil {

	public static final ThreadLocal<UserSessionInfo> TL_USER_SESSION_INFO = new ThreadLocal<UserSessionInfo>() {
		@Override
		protected UserSessionInfo initialValue() {
			return null;
		}
	};
	
	public LogSrvUtil(Class<?> cls) {
		super(cls);
	}

	@Override
	protected UserSessionInfo getInternalUserSessionInfo() {
		UserSessionInfo userSessionInfo = getUserSessionInfo();
		
		return userSessionInfo == null ? UserSessionInfo.EMPTY : userSessionInfo;
	}

	public UserSessionInfo getUserSessionInfo() {
		return TL_USER_SESSION_INFO.get();
	}

	
	public void initializeUserSessionInfo(UserSessionInfo userSessionInfo) {

		if (userSessionInfo == null) {
			return;
		}

		TL_USER_SESSION_INFO.set(userSessionInfo);
	}
	
}
