/*
*SPDX-FileCopyrightText: Copyright 2020 | CSI Piemonte
*SPDX-License-Identifier: EUPL-1.2
*/
package it.csi.siac.siaccommonser.business.service.base;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.NoTransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import it.csi.siac.siaccommon.util.log.LogUtil;
import it.csi.siac.siaccommonser.business.service.base.exception.BusinessException;
import it.csi.siac.siaccommonser.business.service.base.exception.ExecuteExternalServiceException;
import it.csi.siac.siaccommonser.business.service.base.exception.ServiceParamError;
import it.csi.siac.siaccorser.frontend.webservice.OperazioneAsincronaService;
import it.csi.siac.siaccorser.frontend.webservice.msg.AggiornaOperazioneAsinc;
import it.csi.siac.siaccorser.frontend.webservice.msg.AggiornaOperazioneAsincResponse;
import it.csi.siac.siaccorser.frontend.webservice.msg.AsyncServiceRequest;
import it.csi.siac.siaccorser.frontend.webservice.msg.InserisciDettaglioOperazioneAsinc;
import it.csi.siac.siaccorser.frontend.webservice.msg.InserisciDettaglioOperazioneAsincResponse;
import it.csi.siac.siaccorser.model.Errore;
import it.csi.siac.siaccorser.model.Esito;
import it.csi.siac.siaccorser.model.ServiceRequest;
import it.csi.siac.siaccorser.model.ServiceResponse;
import it.csi.siac.siaccorser.model.StatoOperazioneAsincronaEnum;
import it.csi.siac.siaccorser.model.errore.ErroreCore;

/**
 * Classe base dell'implementazione della business logic di un generico servizio.
 * 
 * Estendendo questa classe bisogna aggiugere le seguenti annotazioni di Spring:
 * 
 * 		\@Service
 * 		\@Scope(BeanDefinition.SCOPE_PROTOTYPE)
 * 
 * 
 * @author Domenico Lisi
 *
 * @param <REQ> Input del servizio che estende ServiceRequest
 * 
 *  @deprecated Usare {@link AsyncBaseService} (come su bilser)
 */
@Deprecated
public abstract class BaseServiceOneWay<REQ extends AsyncServiceRequest> {
	
	protected LogUtil log = new LogUtil(this.getClass());
	
	protected StatoOperazioneAsincronaEnum statoFinaleOperazione = StatoOperazioneAsincronaEnum.STATO_OPASINC_CONCLUSA;	
	
	@Autowired
	protected OperazioneAsincronaService operazioneAsincronaService;
	
	/**
	 * Parametri di input del servizio
	 */
	protected REQ req;
	

	/**
	 * Esecuzione del servizo.
	 * 
	 * @param serviceRequest
	 * @return
	 */
	public void executeService(REQ serviceRequest){
		this.req = serviceRequest;
		executeService();
	}
	
	
	
	/**
	 * Scheletro/Template di base dell'esecuzione di ogni servizio.
	 * In questa gestione viene inserito il comportamento di base di ogni servizio:
	 * 		Log Chiamata del servizio
	 * 		Informazioni di tracciatura
	 * 		Log Risposta del servizio
	 * 		Ecc..
	 */
	public void executeService() {
		final String methodName = "executeService";
				
		log.info(methodName, "Start.");
		logServiceRequest();
		
		try {	
			aggiornaOperazioneAsinc(StatoOperazioneAsincronaEnum.STATO_OPASINC_AVVIATA);
			checkServiceParamBase();
			init();
			//L'execute deve settare lo stato finale dell'operazione.
			execute();
		} catch (ServiceParamError e) {
			log.error(methodName, "Check parametri del servizio terminato con errori." + (e.getErrore()!=null?e.getErrore().getTesto():""));
			setRollbackOnly();
		} catch (BusinessException e) {
			log.error(methodName, "Errore di business nell'esecuzione del Servizio.", e);
			if(e.isRollbackOnly()){
				setRollbackOnly();
			}
		} catch (RuntimeException e) {
			log.error(methodName, "Errore di runtime nell'esecuzione del Servizio.", e);			
			setRollbackOnly();
		} catch (Throwable t) {
			log.error(methodName, "Errore di sistema nell'esecuzione del Servizio.", t);
			setRollbackOnly();
		} finally {
			aggiornaOperazioneAsinc(statoFinaleOperazione);
			log.info(methodName, "End");
		}
				
		
	}
	
	/**
	 * Imposta a "rollback only" lo stato della transazione in corso se presente.
	 * Questo metodo presume che la transazione sia gestita tramite le Spring Transaction Api; nel caso venga utilizzata 
	 * un altra tecnologia questo metodo può essere sovrascritto. 
	 */
	protected void setRollbackOnly() {		
		final String methodName = "rollback";
		
		statoFinaleOperazione = StatoOperazioneAsincronaEnum.STATO_OPASINC_ERRORE;
		//aggiornaOperazioneAsinc(StatoOperazioneAsincronaEnum.STATO_OPASINC_ERRORE);		
		
		try{
			TransactionStatus currentTransactionStatus = TransactionAspectSupport.currentTransactionStatus();
			currentTransactionStatus.setRollbackOnly();
			log.info(methodName, "Transaction status is marked as rollback only.");
		} catch (NoTransactionException nte){
			log.info(methodName, "Execution is not in transaction. Nothing to rollback. ");
		}
	}
	
	

	/**
	 * Implementa, se necessario, l'inizializzazione di oggetti necessari al ciclo di vita del servizio.
	 * Fare override per gestire l'implementazione facoltativa.
	 */
	protected void init() {
		// Da implementare se necessario
	}
	
	/**
	 * Controllo parametri di base comuni per tutti i servizi. 
	 * Per casi particolari in cui i controlli comuni non sono validi si può fare override.
	 * 
	 * @throws ServiceParamError se il controllo non è andato a buon fine
	 */
	protected void checkServiceParamBase() throws ServiceParamError {		
		checkNotNull(req, ErroreCore.PARAMETRO_NON_INIZIALIZZATO.getErrore("ServiceRequest"));	
		checkNotNull(req.getIdOperazioneAsincrona(), ErroreCore.PARAMETRO_NON_INIZIALIZZATO.getErrore("idOperazioneAsincrona"));
				
		checkEnte();
		checkRichiedente();
		checkServiceParam();
		
	}



	private void checkEnte() throws ServiceParamError {
		checkNotNull(req.getEnte(), ErroreCore.PARAMETRO_NON_INIZIALIZZATO.getErrore("ente"));
		checkCondition(req.getEnte().getUid()!=0, ErroreCore.PARAMETRO_NON_INIZIALIZZATO.getErrore("uid ente"));
	}


	


	/**
	 * Implementa, se necessario, il controllo dei parametri del servizio specifico.
	 * Fare override per gestire l'implementazione facoltativa.
	 * Utilizzare res.addErrore(errore) per aggiungere eventuali errori di business.
	 * Non solleva eccezioni e/o non aggiunge res.addErrore(errore) se il controllo è andato a buon fine. 
	 * @throws ServiceParamError in caso di errori
	 */
	protected void checkServiceParam() throws ServiceParamError{
		// Da implementare se necessario
	}

	/**
	 * Implementa l'esecuzione della bussiness logic del servizio.
	 * E' demandato all'implementazione di questo metodo 
	 * l'impostazione dell'esito dell'operazione.
	 * Ad esempio in caso di successo bisognarà impostare statoFinaleOperazione = StatoOperazioneAsincronaEnum.STATO_OPASINC_CONCLUSA (che si può omettere perchè è il default).
	 * In caso di errore bisognerà impostare  statoFinaleOperazione = StatoOperazioneAsincronaEnum.STATO_OPASINC_ERRORE.
	 *  
	 */
	protected abstract void execute();




	/**
	 * Se condition è false aggiunge l'errore passato per parametro all'oggetto di response.
	 * Se toThrow è true viene sollevata l'eccezione ServiceParamError che interrompe il check di eventuali successivi parametri.
	 * 
	 * @param condition
	 * @param errore
	 * @throws ServiceParamError 
	 */
	protected void checkCondition(boolean condition, Errore errore/*, boolean toThrow*/) throws ServiceParamError{
		if(!condition){
			//if(toThrow){
				inserisciDettaglioOperazioneAsinc("ServiceParamError", errore.getCodice() + " - "+ errore.getDescrizione(), Esito.FALLIMENTO, "Parametro errato o non valido");
				throw new ServiceParamError(errore);
			//} 
		}
		
	}
	
//	/**
//	 * 
//	 * Se condition è false aggiunge l'errore passato per parametro all'oggetto di response.
//	 * Di default solleva l'eccezione interrompendo quindi il check di eventuali successivi parametri.
//	 * 
//	 * @param condition
//	 * @param errore
//	 * @throws ServiceParamError
//	 */
//	protected void checkCondition(boolean condition, Errore errore) throws ServiceParamError{
//		checkCondition(condition, errore/*, true*/);
//	}
	
	/**
	 * Se il parametro objectNotNull è null aggiunge l'errore passato per parametro all'oggetto di response.
	 * 
	 * @param objectNotNull
	 * @param errore
	 * @throws ServiceParamError 
	 */
	protected void checkNotNull(Object objectNotNull, Errore errore/*, boolean toThrow*/) throws ServiceParamError{
		checkCondition(objectNotNull!=null, errore/*, toThrow*/);		
	}
	
//	/**
//	 * Se il parametro objectNotNull è null aggiunge l'errore passato per parametro all'oggetto di response.
//	 * Di default solleva l'eccezione interrompendo quindi il check di eventuali successivi parametri.
//	 * 
//	 * @param objectNotNull
//	 * @param errore
//	 * @throws ServiceParamError
//	 */
//	protected void checkNotNull(Object objectNotNull, Errore errore) throws ServiceParamError{
//		checkCondition(objectNotNull!=null, errore, true);		
//	}
	
	
	/**
	 * Check del Richiedente, comune per tutti i servizi.
	 * 
	 * @throws ServiceParamError
	 */
	protected void checkRichiedente() throws ServiceParamError {
		final String methodName = "checkRichiedente";
		checkNotNull(req.getRichiedente(), ErroreCore.PARAMETRO_NON_INIZIALIZZATO.getErrore("richiedente"));
		checkNotNull(req.getRichiedente().getOperatore(), ErroreCore.PARAMETRO_NON_INIZIALIZZATO.getErrore("operatore richiedente"));
		checkNotNull(req.getRichiedente().getOperatore().getCodiceFiscale(), ErroreCore.PARAMETRO_NON_INIZIALIZZATO.getErrore("codice fiscale operatore richiedente")/*, false*/);
		log.info(methodName, "Codice fiscale Richiedente: "+req.getRichiedente().getOperatore().getCodiceFiscale());
	}
	
	
	//--------------------------------------------------------------------------
	


	/**
	 * Esegue un servizio esterno all'interno di questo servizio.
	 * Nel caso in cui il servizio richiamato risponda con almeno uno dei codici di errore passato come parametro 
	 * viene sollevata una eccezione runtime di tipo ExecuteExternalServiceException.
	 * Così facendo di default anche questo servizio (ovvero il chiamante) viene interrotto (ovviamente a meno che non intercetti l'eccezione e la gestisca).
	 * 
	 * @param service
	 * @param request
	 * @param codiciErrore
	 * @return
	 */
	protected <EREQ extends ServiceRequest, ERES extends ServiceResponse> ERES executeExternalService(BaseService<EREQ,ERES> service, EREQ request, String... codiciErrore){
		ERES externalServiceResponse = service.executeService(request);
		
		checkServiceResponseErrore(externalServiceResponse, codiciErrore);				
		
		return externalServiceResponse;
	}
	
	/**
	 * Esegue un servizio esterno all'interno di questo servizio.
	 * Si comporta come executeExternalService impostando la lista di errori al default: ERRORE_DI_SISTEMA, PARAMETRO_NON_INIZIALIZZATO e PARAMETRO_ERRATO
	 * 
	 * @author Domenico Lisi
	 * @param service
	 * @param request
	 * @return
	 */
	protected <EREQ extends ServiceRequest, ERES extends ServiceResponse> ERES executeExternalService(BaseService<EREQ,ERES> service, EREQ request){
		return executeExternalService(service, request, ErroreCore.ERRORE_DI_SISTEMA.getCodice(),
				ErroreCore.PARAMETRO_NON_INIZIALIZZATO.getCodice(), ErroreCore.PARAMETRO_ERRATO.getCodice());		
	}
	
	
	
	/**
	 * Esegue un servizio esterno all'interno di questo servizio.
	 * Stesso comportamento di executeService ma in più solleva l'eccezione anche per Esito.Fallimento solo se tra gli errori non è presente nessuno dei 
	 * codiciErroreDaEscludere passato in input
	 * 
	 * @author Domenico Lisi
	 * @param service
	 * @param request
	 * @param codiciErroreDaEscludere
	 * @return
	 */
	protected <EREQ extends ServiceRequest, ERES extends ServiceResponse> ERES executeExternalServiceSuccess(BaseService<EREQ,ERES> service, EREQ request, String... codiciErroreDaEscludere){
		ERES externalServiceResponse = executeExternalService(service,request);
		
		checkServiceResponseFallimento(externalServiceResponse,codiciErroreDaEscludere);
		
		return externalServiceResponse;
	}
	
	
	
	
	
	/**
	 * Esegue un servizio esterno all'interno di questo servizio.
	 * Stesso comportamento di executeExternalServiceSuccess ma solleva l'eccezione per Esito.Fallimento solo se non c'è un errore di tipo ENTITA_NON_TROVATA
	 * 
	 * @author Domenico Lisi
	 * @param service
	 * @param request
	 * @return
	 */
	protected <EREQ extends ServiceRequest, ERES extends ServiceResponse> ERES executeExternalServiceSuccessRicerca(BaseService<EREQ,ERES> service, EREQ request){
		ERES externalServiceResponse = executeExternalService(service,request);
		
		//checkServiceResponseFallimento(externalServiceResponse,ErroreCore.ENTITA_NON_TROVATA.getCodice());
		checkServiceResponseFallimentoRicerca(externalServiceResponse);
		
		return externalServiceResponse;
	}


	
	
	/** 
	 * Esegue il check di una risposta di un servizio.
	 * Nel caso venga invocato un servizio di un altro modulo rispetto al chiamante è possibile passare la serviceResponse ottenuta a
	 * questo metodo di check che in caso si sia verificato un errore con il codice passato in input solleva l'eccezione al chiamante.
	 * 
	 * 
	 * @author Domenico Lisi
	 * @param externalServiceResponse
	 * @param serviceName
	 */
	protected <ERES extends ServiceResponse> void checkServiceResponseErrore(ERES externalServiceResponse, String... codiciErrore) {
		if(externalServiceResponse.verificatoErrore(codiciErrore)) {
			//res.addErrori(externalServiceResponse.getErrori());
			String externalServiceName = getServiceName(externalServiceResponse);			
			throw new ExecuteExternalServiceException("\nEsecuzione servizio interno " + externalServiceName + " richiamato da "+ getServiceName() + " terminata con errori." 
					+ "\nErrori riscontrati da "+externalServiceName+": {"+externalServiceResponse.getDescrizioneErrori().replaceAll("\n", "\n\t")+"}."
					+ "\nErrori da sollevare per "+getServiceName()+": " + ToStringBuilder.reflectionToString(codiciErrore, ToStringStyle.SIMPLE_STYLE)+ ". "
					, externalServiceResponse.getErrori());
		}
	}
	


	/** 
	 * Esegue il check di una risposta di un servizio.
	 * Nel caso esito sia Fallimento  
	 * viene sollevata l'eccezione.
	 * 
	 * @author AR
	 * @param externalServiceResponse
	 */
	protected <ERES extends ServiceResponse> void checkServiceResponseFallimento(ERES externalServiceResponse) {		
		checkServiceResponseFallimento(externalServiceResponse, new String[0]);
	}
	
	/** 
	 * Esegue il check di una risposta di un servizio.
	 * Nel caso esito sia Fallimento e non si sia verificato nessuno degli errori passati nel parametro codiciErroreDaEscludere 
	 * viene sollevata l'eccezione.
	 * 
	 * @author Domenico Lisi
	 * @param externalServiceResponse
	 * @param codiciErroreDaEscludere codici di errori per il quale NON sollevare l'eccezione
	 */
	protected <ERES extends ServiceResponse> void checkServiceResponseFallimento(ERES externalServiceResponse, String... codiciErroreDaEscludere) {		
		if (externalServiceResponse.isFallimento() 
				&& !externalServiceResponse.verificatoErrore(codiciErroreDaEscludere)) {
			//res.addErrori(externalServiceResponse.getErrori());
			String externalServiceName = getServiceName(externalServiceResponse);
			throw new ExecuteExternalServiceException("\nEsecuzione servizio interno " + externalServiceName + " richiamato da "+ getServiceName() + " terminata con esito Fallimento." 
					+ "\nErrori riscontrati da "+externalServiceName+": {"+externalServiceResponse.getDescrizioneErrori().replaceAll("\n", "\n\t")+"}." 
					+ "\nErrori da escludere per "+getServiceName()+": " + ToStringBuilder.reflectionToString(codiciErroreDaEscludere, ToStringStyle.SIMPLE_STYLE)+ ". "
					, externalServiceResponse.getErrori());
		}
	}
	
	
	
	protected <ERES extends ServiceResponse> void checkServiceResponseFallimentoRicerca(ERES externalServiceResponse) {		
		checkServiceResponseFallimento(externalServiceResponse, ErroreCore.ENTITA_NON_TROVATA.getCodice());
		
	}	


	protected <ERES extends ServiceResponse> String getServiceName(ERES externalServiceResponse) {
		return externalServiceResponse.getClass().getSimpleName().replaceAll("(Response)$","")+"Service";
	}
	
	protected <ERES extends ServiceResponse> String getServiceName() {
		return this.getClass().getSimpleName();
	}
	
	
	/**
	 * Aggiorna lo stato dell'operazione asincrona
	 * @param stato
	 */
	protected void aggiornaOperazioneAsinc(StatoOperazioneAsincronaEnum stato) {
		AggiornaOperazioneAsinc reqAgg = new AggiornaOperazioneAsinc();
		reqAgg.setRichiedente(req.getRichiedente());
		reqAgg.setIdOperazioneAsinc(req.getIdOperazioneAsincrona());		
		reqAgg.setIdEnte(req.getEnte().getUid());
		
		reqAgg.setStato(stato);

		AggiornaOperazioneAsincResponse resAgg = operazioneAsincronaService.aggiornaOperazioneAsinc(reqAgg);
		checkServiceResponseFallimento(resAgg);
	}
	
	
	/**
	 * Aggiorna lo stato dell'operazione asincrona
	 * @param stato
	 */
	protected void inserisciDettaglioOperazioneAsinc(String codice, String descrizione, Esito esito) {
		inserisciDettaglioOperazioneAsinc(codice, descrizione, esito, null);
	}
	
	/**
	 * Aggiorna lo stato dell'operazione asincrona e specifica un messaggio di errore.
	 * @param stato
	 */
	protected void inserisciDettaglioOperazioneAsinc(String codice, String descrizione, Esito esito, String msgErrore) {
		InserisciDettaglioOperazioneAsinc reqdett = new InserisciDettaglioOperazioneAsinc();		
		
		reqdett.setIdOperazioneAsincrona(req.getIdOperazioneAsincrona());
		reqdett.setCodice(codice);
		reqdett.setDescrizione(descrizione);
		reqdett.setRichiedente(req.getRichiedente());
		reqdett.setIdEnte(req.getEnte().getUid());
		reqdett.setEsito(esito.name());
		reqdett.setMsgErrore(msgErrore);
		
		InserisciDettaglioOperazioneAsincResponse resIDOA = operazioneAsincronaService.inserisciDettaglioOperazioneAsinc(reqdett);
		checkServiceResponseFallimento(resIDOA);
	}

	
	private void logServiceRequest() {
		log.logXmlTypeObject(req, "Service Request param");
	}
	
	
	
	
	
	
	
	
	
	

}
