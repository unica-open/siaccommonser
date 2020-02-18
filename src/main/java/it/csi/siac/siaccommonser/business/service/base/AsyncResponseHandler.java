/*
*SPDX-FileCopyrightText: Copyright 2020 | CSI Piemonte
*SPDX-License-Identifier: EUPL-1.2
*/
package it.csi.siac.siaccommonser.business.service.base;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.factory.annotation.Autowired;

import it.csi.siac.siaccommon.util.log.LogUtil;
import it.csi.siac.siaccommonser.business.service.base.exception.ExecuteExternalServiceException;
import it.csi.siac.siaccorser.frontend.webservice.OperazioneAsincronaService;
import it.csi.siac.siaccorser.frontend.webservice.msg.AggiornaOperazioneAsinc;
import it.csi.siac.siaccorser.frontend.webservice.msg.AggiornaOperazioneAsincResponse;
import it.csi.siac.siaccorser.frontend.webservice.msg.InserisciDettaglioOperazioneAsinc;
import it.csi.siac.siaccorser.frontend.webservice.msg.InserisciDettaglioOperazioneAsincResponse;
import it.csi.siac.siaccorser.model.Ente;
import it.csi.siac.siaccorser.model.Esito;
import it.csi.siac.siaccorser.model.Richiedente;
import it.csi.siac.siaccorser.model.ServiceResponse;
import it.csi.siac.siaccorser.model.StatoOperazioneAsincronaEnum;

/**
 * Gestore della response di un servizio asincrono.
 * 
 * @author Domenico
 * 
 * @param <RES>
 */
public abstract class AsyncResponseHandler<RES extends ServiceResponse> extends ResponseHandler<RES> {

	@Autowired
	protected OperazioneAsincronaService operazioneAsincronaService;

	protected LogUtil log = new LogUtil(this.getClass());

	protected Ente ente;
	protected Richiedente richiedente;

	protected Integer idOperazioneAsincrona;
	protected StatoOperazioneAsincronaEnum statoFinaleOperazione = StatoOperazioneAsincronaEnum.STATO_OPASINC_CONCLUSA;

	/**
	 * Gesrione base della response.
	 * 
	 * @param response la response da gestire
	 */
	@Override
	public void handleResponseBase(RES response) {
		final String methodName = "handleResponseBase";
		try {
			if(response==null) {
				throw new IllegalArgumentException(this.getClass().getName() + ": Response del servizio non valorizzata. [null]");
			}
			handleResponse(response);
		} catch (RuntimeException re) {
			statoFinaleOperazione = StatoOperazioneAsincronaEnum.STATO_OPASINC_ERRORE;
			log.error(methodName, "Errore nella gestione della response.", re);
			throw re;
		} finally {
			cleanThread();
			log.info(methodName, "Aggiorno stato finale operazione: " + statoFinaleOperazione);
			aggiornaOperazioneAsinc(statoFinaleOperazione);
		}
	}

	/**
	 * Permette di gestire la risposta del servizio invocato in modalit&agrave; asincrona.
	 * 
	 * @param response
	 */
	@Override
	public abstract void handleResponse(RES response);
	
	/**
	 * Pulizia dei thread al termine dell'invocazione
	 */
	protected void cleanThread() {
		// Permetto eventuali override
	}

	/**
	 * Aggiorna lo stato dell'operazione asincrona
	 * 
	 * @param stato
	 */
	protected void aggiornaOperazioneAsinc(StatoOperazioneAsincronaEnum stato) {
		AggiornaOperazioneAsinc reqAgg = new AggiornaOperazioneAsinc();
		reqAgg.setRichiedente(richiedente);
		reqAgg.setIdOperazioneAsinc(this.idOperazioneAsincrona);
		reqAgg.setIdEnte(ente.getUid());

		reqAgg.setStato(stato);

		AggiornaOperazioneAsincResponse resAgg = operazioneAsincronaService.aggiornaOperazioneAsinc(reqAgg);
		checkServiceResponseFallimento(resAgg);
	}

	/**
	 * Aggiorna lo stato dell'operazione asincrona
	 * 
	 * @param stato
	 */
	protected void inserisciDettaglioOperazioneAsinc(String codice, String descrizione, Esito esito) {
		inserisciDettaglioOperazioneAsinc(codice, descrizione, esito, null);
	}

	/**
	 * Aggiorna lo stato dell'operazione asincrona e specifica un messaggio di errore.
	 * 
	 * @param stato
	 */
	protected void inserisciDettaglioOperazioneAsinc(String codice, String descrizione, Esito esito, String msgErrore) {
		inserisciDettaglioOperazioneAsinc(codice, descrizione, esito, msgErrore, null);
	}

	/**
	 * Aggiorna lo stato dell'operazione asincrona e specifica un messaggio di errore.
	 * Inoltre aggiunge l'intera response del servizio nel dettaglio.
	 * 
	 * @param stato
	 */
	protected void inserisciDettaglioOperazioneAsinc(String codice, String descrizione, Esito esito, String msgErrore, String serviceResponse) {
		InserisciDettaglioOperazioneAsinc reqdett = new InserisciDettaglioOperazioneAsinc();

		reqdett.setIdOperazioneAsincrona(this.idOperazioneAsincrona);
		reqdett.setCodice(codice);
		reqdett.setDescrizione(descrizione);
		reqdett.setRichiedente(richiedente);
		reqdett.setIdEnte(ente.getUid());
		reqdett.setEsito(esito.name());
		reqdett.setMsgErrore(msgErrore);
		reqdett.setServiceResponse(serviceResponse);

		InserisciDettaglioOperazioneAsincResponse resIDOA = operazioneAsincronaService.inserisciDettaglioOperazioneAsinc(reqdett);
		checkServiceResponseFallimento(resIDOA);
	}
	
	/**
	 * Inserisce il dettaglio dell'operazione asincrona relativa al successo dell'elaborazione.
	 */
	protected void inserisciDettaglioSuccesso() {
		inserisciDettaglioOperazioneAsinc("CRU_CON_2001", "L'operazione e' stata completata con successo", Esito.SUCCESSO);
	}

	/**
	 * Esegue il check di una risposta di un servizio.
	 * <br/>
	 * Nel caso esito sia Fallimento e non si sia verificato nessuno degli errori passati nel parametro codiciErroreDaEscludere viene sollevata l'eccezione.
	 * 
	 * @param externalServiceResponse
	 * @param codiciErroreDaEscludere codici di errori per il quale NON sollevare l'eccezione
	 */
	protected <ERES extends ServiceResponse> void checkServiceResponseFallimento(ERES externalServiceResponse, String... codiciErroreDaEscludere) {
		if (externalServiceResponse.isFallimento() && !externalServiceResponse.verificatoErrore(codiciErroreDaEscludere)) {

			String externalServiceName = getServiceName(externalServiceResponse);
			throw new ExecuteExternalServiceException("\nEsecuzione servizio interno " + externalServiceName + " terminata con esito Fallimento."
					+ "\nErrori riscontrati da " + externalServiceName + ": {" + externalServiceResponse.getDescrizioneErrori().replaceAll("\n", "\n\t") + "}."
					+ "\nErrori da escludere: " + ToStringBuilder.reflectionToString(codiciErroreDaEscludere, ToStringStyle.SIMPLE_STYLE) + ". ",
					externalServiceResponse.getErrori());
		}
	}

	/**
	 * Ottiene il nome del servizio.
	 * 
	 * @param externalServiceResponse la response del servizio
	 * 
	 * @return il nomde del servizio
	 */
	protected <ERES extends ServiceResponse> String getServiceName(ERES externalServiceResponse) {
		return externalServiceResponse.getClass().getSimpleName().replaceAll("(Response)$", "") + "Service";
	}

	/**
	 * @param ente the ente to set
	 */
	public void setEnte(Ente ente) {
		this.ente = ente;
	}

	/**
	 * @param richiedente the richiedente to set
	 */
	public void setRichiedente(Richiedente richiedente) {
		this.richiedente = richiedente;
	}

	/**
	 * @param idOperazioneAsincrona the idOperazioneAsincrona to set
	 */
	public void setIdOperazioneAsincrona(Integer idOperazioneAsincrona) {
		this.idOperazioneAsincrona = idOperazioneAsincrona;
	}

}
