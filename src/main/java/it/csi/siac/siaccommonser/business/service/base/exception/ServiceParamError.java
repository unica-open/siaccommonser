/*
*SPDX-FileCopyrightText: Copyright 2020 | CSI Piemonte
*SPDX-License-Identifier: EUPL-1.2
*/
package it.csi.siac.siaccommonser.business.service.base.exception;

import it.csi.siac.siaccorser.model.Errore;

/**
 * Errore di business per un parametro di input del servizio errato.
 * 
 * @author Domenico
 * 
 */
public class ServiceParamError extends Exception {

	private static final long serialVersionUID = 2068863996068927517L;
	private final Errore errore;

	public ServiceParamError(Errore errore) {
		super(errore != null ? errore.getCodice() + " " + errore.getTesto() : null);
		this.errore = errore;
	}

	public Errore getErrore() {
		return errore;
	}

}
