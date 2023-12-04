/*
*SPDX-FileCopyrightText: Copyright 2020 | CSI Piemonte
*SPDX-License-Identifier: EUPL-1.2
*/
package it.csi.siac.siaccommonser.integration.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import it.csi.siac.siaccommon.util.date.DateUtil;

/**
 * Entity di base per tutte le Entity.
 * Rimappa le colonne comuni a tutte le tabelle.
 * 
 * @author Domenico
 *
 */
@MappedSuperclass
public abstract class SiacTBase implements Serializable {
	
	/** Per la serializzazione */
	private static final long serialVersionUID = -2571059206148773563L;

	/** The data inizio validita. */
	@Basic
	@Column(name = "validita_inizio", updatable=false)
	private Date dataInizioValidita;

	/** The data fine validita. */
	@Basic
	@Column(name = "validita_fine")
	private Date dataFineValidita;

	/** The data creazione. */
	@Basic
	@Column(name = "data_creazione", updatable=false)
	private Date dataCreazione;

	/** The data modifica. */
	@Basic
	@Column(name = "data_modifica")
	private Date dataModifica;

	/** The data cancellazione. */
	@Basic
	@Column(name = "data_cancellazione")
	private Date dataCancellazione;

	/** The login operazione. */
	@Basic
	@Column(name = "login_operazione")
	private String loginOperazione;
	
	/**
	 * Instantiates a new siac t base.
	 */
	public SiacTBase() {
		super();
	}
	
	public SiacTBase(SiacTBase siacTBase) {
		this();	
		setUid(siacTBase.getUid());
		this.dataInizioValidita = siacTBase.dataInizioValidita;
		this.dataFineValidita = siacTBase.dataFineValidita;
		this.dataCreazione = siacTBase.dataCreazione;
		this.dataModifica = siacTBase.dataModifica;
		this.dataCancellazione = siacTBase.dataCancellazione;
		this.loginOperazione = siacTBase.loginOperazione;
	}

	/**
	 * Gets the uid.
	 *
	 * @return the uid
	 */
	public abstract Integer getUid();

	/**
	 * Sets the uid.
	 *
	 * @param uid the new uid
	 */
	public abstract void setUid(Integer uid);

	/**
	 * Gets the data cancellazione.
	 *
	 * @return the data cancellazione
	 */
	public Date getDataCancellazione() {
		return dataCancellazione;
	}

	/**
	 * Sets the data cancellazione.
	 *
	 * @param dataCancellazione the new data cancellazione
	 */
	public void setDataCancellazione(Date dataCancellazione) {
		this.dataCancellazione = dataCancellazione;
	}
	
	/**
	 * Sets the data cancellazione if not set.
	 *
	 * @param dataCancellazione the new data cancellazione if not set
	 */
	public void setDataCancellazioneIfNotSet(Date dataCancellazione) {
		if(getDataCancellazione()==null){
			setDataCancellazione(dataCancellazione);
			setDataFineValiditaIfNotSet(dataCancellazione);
		}
	}
	
	/**
	 * Sets the data cancellazione if not set.
	 *
	 * @param dataCancellazioneDaImpostare the new data cancellazione if not set
	 */
	public void setDataFineValiditaEDataCancellazioneSeNelPeriodoDiValidita(Date dataCancellazioneDaImpostare, Date dataNelPeriodoDiValidita, Date dataFineValiditaDaImpostare) {
		
		if(getDataCancellazione()==null) {
			setDataFineValiditaIfNotSet(dataFineValiditaDaImpostare);
			
			//posso impostare la data cancellazione solo se del mio range di validita' di competenza 
			if(isDataValiditaCompresa(dataNelPeriodoDiValidita)) { 
				setDataCancellazione(dataCancellazioneDaImpostare);
			} 
			
			
		}
		
	}
	
	/**
	 * Sets the data cancellazione if not set.
	 *
	 * @param dataCancellazioneDaImpostare the new data cancellazione if not set
	 */
	public void sovrascriviDataFineValiditaESetDataCancellazioneSeNelPeriodoDiValidita(Date dataCancellazioneDaImpostare, Date dataNelPeriodoDiValidita, Date dataFineValiditaDaImpostare) {
		
		if(getDataCancellazione()==null) {
			setDataFineValidita(dataFineValiditaDaImpostare);
			
			//posso impostare la data cancellazione solo se del mio range di validita' di competenza 
			if(isDataValiditaCompresa(dataNelPeriodoDiValidita)) { 
				setDataCancellazione(dataCancellazioneDaImpostare);
			} 
			
			
		}
		
	}

	/**
	 * Gets the data creazione.
	 *
	 * @return the data creazione
	 */
	public Date getDataCreazione() {
		return dataCreazione;
	}

	/**
	 * Sets the data creazione.
	 *
	 * @param dataCreazione the new data creazione
	 */
	public void setDataCreazione(Date dataCreazione) {
		this.dataCreazione = dataCreazione;
	}

	/**
	 * Gets the data modifica.
	 *
	 * @return the data modifica
	 */
	public Date getDataModifica() {
		return dataModifica;
	}

	/**
	 * Sets the data modifica.
	 *
	 * @param dataModifica the new data modifica
	 */
	public void setDataModifica(Date dataModifica) {
		this.dataModifica = dataModifica;		
	}
	
	/**
	 * Imposta le date per l'inserimento.
	 * Nel dettaglio imposta dataModifica, dataCreazione e dataInizio validit&agrave; con il valore passato 
	 * come parametro.
	 *
	 * @param dataModifica the new data modifica inserimento
	 */
	public void setDataModificaInserimento(Date dataModifica) {
		this.dataModifica = dataModifica;
		this.dataCreazione = dataModifica;
		this.dataInizioValidita = dataModifica;		
	}
	
	/**
	 * Imposta le date per l'inserimento se non settate.
	 * Nel dettaglio imposta dataModifica, dataCreazione e dataInizio validit&agrave; con il valore passato 
	 * come parametro.
	 *
	 * @param dataModifica the new data modifica inserimento
	 */
	public void setDataModificaInserimentoIfNotSet(Date dataModifica) {
		this.dataModifica = dataModifica;
		if(this.dataCreazione == null) {
			this.dataCreazione = dataModifica;
		}
		if(this.dataInizioValidita == null) {
			this.dataInizioValidita = dataModifica;
		}
	}
	
	/**
	 * Imposta le date per l'inserimento.
	 * Nel dettaglio imposta dataModifica, dataCreazione e dataInizio validit&agrave; con il valore passato 
	 * come parametro.
	 *
	 * @param dataModifica the new data modifica inserimento
	 * @param dataInizioValidita the data inizio validita
	 */
	public void setDataModificaInserimento(Date dataModifica, Date dataInizioValidita) {
		this.dataModifica = dataModifica;
		this.dataCreazione = dataModifica;
		this.dataInizioValidita = dataInizioValidita;		
	}
	
	
	
	/**
	 * Imposta le date per l'aggiornamento.
	 * Nel dettaglio imposta dataModifica con il valore passato e se null imposta anche 
	 * dataInizioValidita e dataCreazione.
	 *  
	 *
	 * @param dataModifica the new data modifica aggiornamento
	 */
	public void setDataModificaAggiornamento(Date dataModifica) {
		this.dataModifica = dataModifica;
		
		if(this.dataInizioValidita==null) {
			this.dataInizioValidita = dataModifica;
		}
		
		if(this.dataCreazione==null)  {
			this.dataCreazione = dataModifica;
		}
		
	}
	
	/**
	 * Imposta le date per l'aggiornamento.
	 * Nel dettaglio imposta dataModifica con il valore passato e se null imposta anche 
	 * dataInizioValidita e dataCreazione.
	 *  
	 *
	 * @param dataModifica the new data modifica aggiornamento
	 * @param dataInizioValidita the data inizio validita
	 */
	public void setDataModificaAggiornamento(Date dataModifica, Date dataInizioValidita) {
		this.dataModifica = dataModifica;
		
		if(this.dataInizioValidita==null) {
			this.dataInizioValidita = dataInizioValidita;
		}
		
		if(this.dataCreazione==null)  {
			this.dataCreazione = dataModifica;
		}
		
	}

	/**
	 * Gets the login operazione.
	 *
	 * @return the login operazione
	 */
	public String getLoginOperazione() {
		return loginOperazione;
	}

	/**
	 * Sets the login operazione.
	 *
	 * @param loginOperazione the new login operazione
	 */
	public void setLoginOperazione(String loginOperazione) {
		this.loginOperazione = loginOperazione;
	}

	/**
	 * Gets the data inizio validita.
	 *
	 * @return the data inizio validita
	 */
	public Date getDataInizioValidita() {
		return dataInizioValidita;
	}

	/**
	 * Sets the data inizio validita.
	 *
	 * @param dataInizioValidita the new data inizio validita
	 */
	public void setDataInizioValidita(Date dataInizioValidita) {
		this.dataInizioValidita = dataInizioValidita;
	}

	/**
	 * Gets the data fine validita.
	 *
	 * @return the data fine validita
	 */
	public Date getDataFineValidita() {
		return dataFineValidita;
	}

	/**
	 * Sets the data fine validita.
	 *
	 * @param dataFineValidita the new data fine validita
	 */
	public void setDataFineValidita(Date dataFineValidita) {
		this.dataFineValidita = dataFineValidita;
	}
	
	/**
	 * Sets the data fine validita if not set.
	 *
	 * @param dataFineValidita the new data fine validita if not set
	 */
	public void setDataFineValiditaIfNotSet(Date dataFineValidita) {
		if(getDataFineValidita()==null){
			setDataFineValidita(dataFineValidita);
		}
	}
	
	/**
	 * Checks if is data validita compresa.
	 *
	 * @param data the data
	 * @return true, if is data validita compresa
	 * @exception NullPointerException if <code>data</code> is null.
	 */
	public boolean isDataValiditaCompresa(Date data) {
		
		boolean result = this.getDataInizioValidita().compareTo(data) <= 0
		&& (this.getDataFineValidita()==null || this.getDataFineValidita().compareTo(data)>=0);
		
		return result;
	}

	public boolean isEntitaValida() {
		return !isEntitaCancellata() && isEntitaConDataValida(); 
	}

	public boolean isEntitaCancellata()
	{
		return dataCancellazione != null && DateUtil.beforeNow(dataCancellazione);
	}
	
	public boolean isEntitaConDataValida()
	{
		return !DateUtil.afterNow(dataInizioValidita) && (dataFineValidita == null || DateUtil.afterNow(dataFineValidita));
	}
	

}