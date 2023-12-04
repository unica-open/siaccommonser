/*
*SPDX-FileCopyrightText: Copyright 2020 | CSI Piemonte
*SPDX-License-Identifier: EUPL-1.2
*/
package it.csi.siac.siaccommonser.business.util.service;

import it.csi.siac.siaccorser.model.Esito;
import it.csi.siac.siaccorser.model.ServiceResponse;
import it.csi.siac.siaccorser.model.errore.ErroreCore;

public class ServiceImplUtil {
	
	private ServiceImplUtil() {
		// Prevent instantiation
	}

	public static <R extends ServiceResponse> R unimplementedServiceResponse(R res) {
		res.setEsito(Esito.FALLIMENTO);
		res.addErrore(ErroreCore.ERRORE_DI_SISTEMA.getErrore("Servizio non implementato"));
		return res;
	}
}
