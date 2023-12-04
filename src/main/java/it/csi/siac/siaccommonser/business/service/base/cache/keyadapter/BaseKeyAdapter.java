/*
*SPDX-FileCopyrightText: Copyright 2020 | CSI Piemonte
*SPDX-License-Identifier: EUPL-1.2
*/
package it.csi.siac.siaccommonser.business.service.base.cache.keyadapter;

import it.csi.siac.siaccommonser.business.service.base.cache.KeyAdapter;
import it.csi.siac.siaccorser.model.Entita;
import it.csi.siac.siaccorser.model.paginazione.ParametriPaginazione;

/**
 * Adapter base per le chiavi.
 * <br/>
 * <strong>Attenzione</strong>: non thread-safe
 * @author Marchino Alessandro
 * @param <T> il tipo della richiesta per cui ottenere una chiave
 */
public abstract class BaseKeyAdapter<T> implements KeyAdapter<T> {

	protected String separator = "_";
	protected StringBuilder sb;
	
	@Override
	public String computeKey(T o) {
		this.sb = new StringBuilder();
		computeKeyBase(o);
		return sb.toString();
	}
	
	/**
	 * Composizione della chiave
	 * @param o l'oggetto per cui chiamare la chiave
	 */
	protected abstract void computeKeyBase(T o);

	/**
	 * Appende l'entit&agrave; fornita
	 * @param entita l'entit&agrave; fornita
	 */
	protected void append(Entita entita) {
		if(entita == null) {
			append("null");
			return;
		}
		append(entita.getUid());
	}
	
	/**
	 * Appende l'oggetto fornito
	 * @param str la stringa da appendere
	 */
	protected void append(Object obj) {
		if(obj == null) {
			append("null");
			return;
		}
		append(obj.toString());
	}
	
	/**
	 * Appende la stringa fornita
	 * @param str la stringa da appendere
	 */
	protected void append(String str) {
		sb.append(str)
			.append(separator);
	}

	/**
	 * Appende i dati del parametro di paginazione
	 * @param sb lo stringBuilder
	 * @param pp i parametri di paginazione
	 */
	protected void appendParametriPaginazione(ParametriPaginazione pp) {
		if(pp == null) {
			sb.append("null");
			return;
		}
		sb.append(pp.getElementiPerPagina())
			.append("-")
			.append(pp.getNumeroPagina());
	}
	
	// Utilita' per i primitivi
	
	/**
	 * Appende l'int
	 * @param value il valore da appendere
	 */
	protected void append(int value) {
		append(Integer.toString(value));
	}
	
	/**
	 * Appende il boolean
	 * @param value il valore da appendere
	 */
	protected void append(boolean value) {
		append(Boolean.toString(value));
	}
}
