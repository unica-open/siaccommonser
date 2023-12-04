/*
*SPDX-FileCopyrightText: Copyright 2020 | CSI Piemonte
*SPDX-License-Identifier: EUPL-1.2
*/
package it.csi.siac.siaccommonser.integration.entity;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import it.csi.siac.siaccommonser.integration.entity.SiacTBase;

// TODO: Auto-generated Javadoc
/**
 * Estende SiacTBase aggiungendo le colonne di base relative a tutte 
 * quelle tabelle che tracciano il login di creazione, cancellazione e modifica. 
 * 
 * @author Domenico
 * 
 */
@MappedSuperclass
public abstract class SiacTBaseExt extends SiacTBase {
	
	/** Per la serializzazione */
	private static final long serialVersionUID = 9029589581045457151L;

	/** The login cancellazione. */
	@Column(name = "login_cancellazione")
	private String loginCancellazione;

	/** The login creazione. */
	@Column(name = "login_creazione", updatable=false)
	private String loginCreazione;

	/** The login modifica. */
	@Column(name = "login_modifica")
	private String loginModifica;

	/**
	 * Instantiates a new siac t base ext.
	 */
	public SiacTBaseExt() {
		super();
	}
	
	public SiacTBaseExt(SiacTBaseExt siacTBaseExt) {
		super(siacTBaseExt);
		this.loginCancellazione = siacTBaseExt.loginCancellazione;
		this.loginCreazione = siacTBaseExt.loginCreazione;
		this.loginModifica = siacTBaseExt.loginModifica;
	}

	/**
	 * Gets the login cancellazione.
	 *
	 * @return the loginCancellazione
	 */
	public String getLoginCancellazione() {
		return loginCancellazione;
	}

	/**
	 * Sets the login cancellazione.
	 *
	 * @param loginCancellazione            the loginCancellazione to set
	 */
	public void setLoginCancellazione(String loginCancellazione) {
		this.loginCancellazione = loginCancellazione;
	}

	/**
	 * Gets the login creazione.
	 *
	 * @return the loginCreazione
	 */
	public String getLoginCreazione() {
		return loginCreazione;
	}

	/**
	 * Sets the login creazione.
	 *
	 * @param loginCreazione            the loginCreazione to set
	 */
	public void setLoginCreazione(String loginCreazione) {
		this.loginCreazione = loginCreazione;
	}

	/**
	 * Gets the login modifica.
	 *
	 * @return the loginModifica
	 */
	public String getLoginModifica() {
		return loginModifica;
	}

	/**
	 * Sets the login modifica.
	 *
	 * @param loginModifica            the loginModifica to set
	 */
	public void setLoginModifica(String loginModifica) {
		this.loginModifica = loginModifica;
	}
	
	/**
	 * Imposta le login per l'inserimento.
	 * Nel dettaglio imposta loginCreazione, loginModifica e loginOperazione con il valore passato 
	 * come parametro.
	 *
	 * @param dataModifica the new data modifica inserimento
	 */
	public void setLoginPerInserimento(String login) {
		this.loginCreazione = login;
		this.loginModifica = login;
		setLoginOperazione(login);
		setLoginCancellazione(null);
	}
	/**
	 * Imposta le login per l'aggiornamento.
	 * Nel dettaglio imposta loginModifica e loginOperazione con il valore passato 
	 * come parametro.
	 *
	 * @param dataModifica the new data modifica inserimento
	 */
	public void setLoginPerAggiornamento(String login) {
		this.loginModifica = login;
		setLoginOperazione(login);
	}
}