/*
*SPDX-FileCopyrightText: Copyright 2020 | CSI Piemonte
*SPDX-License-Identifier: EUPL-1.2
*/
package it.csi.siac.siaccommonser.business.service.base;

import org.springframework.stereotype.Component;

import it.csi.siac.siaccommonser.business.service.base.exception.ExecuteExternalServiceException;
import it.csi.siac.siaccorser.model.ServiceResponse;

@Component
public class ServiceResponseUtil {

	public <ERES extends ServiceResponse> void checkFallimento(ERES externalServiceResponse) {
		if (externalServiceResponse.isFallimento()) {
			String externalServiceName = getServiceName(externalServiceResponse);

			throw new ExecuteExternalServiceException("\nEsecuzione servizio interno "
					+ externalServiceName 
					+ " terminata con esito Fallimento." + "\nErrori riscontrati da "
					+ externalServiceName + ": {"
					+ externalServiceResponse.getDescrizioneErrori().replaceAll("\n", "\n\t")
					+ "}.", externalServiceResponse.getErrori());
		}
	}

	protected <ERES extends ServiceResponse> String getServiceName(ERES externalServiceResponse) {
		return externalServiceResponse.getClass().getSimpleName().replaceAll("(Response)$", "")
				+ "Service";
	}

}
