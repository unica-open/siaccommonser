/*
*SPDX-FileCopyrightText: Copyright 2020 | CSI Piemonte
*SPDX-License-Identifier: EUPL-1.2
*/
package it.csi.siac.siaccommonser.business.service.base.exception;

import java.util.List;

import it.csi.siac.siaccorser.model.Errore;
/**
 * Eccezione nell'esecuzione di un servizio B richiamato all'interno dell'implementazione di un servizio A.
 * 
 * @author Domenico
 *
 */
public class ExecuteExternalServiceException extends RuntimeException {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6535077169207640317L;
	
	private final List<Errore> errori;

	public ExecuteExternalServiceException() {
		this(null, null, null);
	}

	public ExecuteExternalServiceException(String message, Throwable cause) {
		this(message, null, cause);
	}

	public ExecuteExternalServiceException(String message) {
		this(message, null, null);
	}

	public ExecuteExternalServiceException(Throwable cause) {
		this(null, null, cause);
	}

	public ExecuteExternalServiceException(String message, List<Errore> errori) {
		this(message, errori, null);
	}
	
	public ExecuteExternalServiceException(String message, List<Errore> errori, Throwable cause) {
		super(message, cause);
		this.errori = errori;
	}

	/**
	 * @return the errori
	 */
	public List<Errore> getErrori() {
		return errori;
	}
}
