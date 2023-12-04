/*
*SPDX-FileCopyrightText: Copyright 2020 | CSI Piemonte
*SPDX-License-Identifier: EUPL-1.2
*/
package it.csi.siac.siaccommonser.business.service.base.exception;

import it.csi.siac.siaccorser.model.Errore;
import it.csi.siac.siaccorser.model.Esito;
import it.csi.siac.siaccorser.model.errore.ErroreCore;

/**
 * Errore di business con codifica.
 * 
 * @author Domenico
 *
 */
public class BusinessException extends RuntimeException {
	
	
	private static final long serialVersionUID = -3172047672687110161L;
	
	private final Errore errore;
	private final Esito esito;
	private final boolean rollbackOnly;
	
	public BusinessException(String msg){
		this(msg, Esito.FALLIMENTO, true);
	}

	public BusinessException(Errore errore){
		this(errore, Esito.FALLIMENTO, true);
	}

	public BusinessException(Throwable t){
		this(t.getMessage(), Esito.FALLIMENTO, true);
	}

	public BusinessException(Errore errore, Esito esito) {
		this(errore, esito, true);
	}
	
	public BusinessException(String msg, Esito esito) {
		this(msg, esito, true);
	}
	
	public BusinessException(String msg, Esito esito, boolean rollbackOnly) {
		this(ErroreCore.ERRORE_DI_SISTEMA.getErrore(msg), esito, rollbackOnly);
	}
	
	public BusinessException(Errore errore, Esito esito, boolean rollbackOnly) {
		super(errore != null ? errore.getTesto() : null);
		this.errore = errore;
		this.esito = esito;
		this.rollbackOnly = rollbackOnly;
	}

	public Errore getErrore() {
		return errore;
	}

	public Esito getEsito() {
		return esito;
	}

	public boolean isRollbackOnly() {
		return rollbackOnly;
	}

}
