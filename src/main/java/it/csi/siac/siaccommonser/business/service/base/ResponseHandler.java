/*
*SPDX-FileCopyrightText: Copyright 2020 | CSI Piemonte
*SPDX-License-Identifier: EUPL-1.2
*/
package it.csi.siac.siaccommonser.business.service.base;

import java.util.ArrayList;

import it.csi.siac.siaccorser.model.ServiceResponse;

/**
 * Gestore della response di un servizio.
 * 
 * @author Domenico
 * 
 * @param <RES>
 */
public abstract class ResponseHandler<RES extends ServiceResponse> {


	/**
	 * Handle response base.
	 *
	 * @param response the response
	 */
	public void handleResponseBase(RES response){
		if(response==null) {
			throw new IllegalArgumentException(this.getClass().getName() + ": Response del servizio non valorizzata. [null]");
		}
		handleResponse(response);
	}

	/**
	 * Permette di gestire la risposta del servizio invocato.
	 * 
	 * @param response
	 */
	protected abstract void handleResponse(RES response);


	protected <T> Iterable<T> unNull(Iterable<T> it) {
		return it != null ? it : new ArrayList<T>();
	}
}
