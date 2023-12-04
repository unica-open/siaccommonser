/*
*SPDX-FileCopyrightText: Copyright 2020 | CSI Piemonte
*SPDX-License-Identifier: EUPL-1.2
*/
package it.csi.siac.siaccommonser.business.service.base;

import javax.persistence.OptimisticLockException;
import javax.persistence.PessimisticLockException;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.core.GenericTypeResolver;
import org.springframework.transaction.NoTransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import it.csi.siac.siaccommonser.business.service.base.exception.BusinessException;
import it.csi.siac.siaccommonser.business.service.base.exception.ExecuteExternalServiceException;
import it.csi.siac.siaccommonser.business.service.base.exception.ServiceParamError;
import it.csi.siac.siaccommonser.util.log.LogSrvUtil;
import it.csi.siac.siaccorser.model.Errore;
import it.csi.siac.siaccorser.model.Esito;
import it.csi.siac.siaccorser.model.ServiceRequest;
import it.csi.siac.siaccorser.model.ServiceResponse;
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
 * @param <RES> Output del servizio che estende ServiceResponse
 */
public abstract class BaseService<REQ extends ServiceRequest,RES extends ServiceResponse> {
	
	
	//protected Logger  log = Logger.getLogger(this.getClass());
	protected LogSrvUtil log = new LogSrvUtil(this.getClass());
	
	/**
	 * Parametri di input del servizio
	 */
	protected REQ req;
	
	/**
	 * Parametri di output del servizio
	 */
	protected RES res;
	
	
	
	/**
	 * Esecuzione del servizo.
	 * 
	 * @param serviceRequest
	 * @return
	 */
	public RES executeService(REQ serviceRequest) {
		log.initializeUserSessionInfo(serviceRequest.getUserSessionInfo());
		this.req = serviceRequest;
		executeService();
		return res;
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
				
		log.infoStart(methodName);

		logServiceRequest();
		
		res = instantiateNewRes();
		
		try {			
			checkServiceParamBase();
			init();
			//L'execute deve settare l'esito di successo serviceResponse.setEsito(Esito.SUCCESSO);
			execute();
			
		} catch (ServiceParamError e) {
			String logMessage = "Check parametri del servizio terminato con errori.";

			if(e.getErrore()!=null){
				res.addErrore(e.getErrore());
				logMessage += (" - " + e.getErrore().getTesto());
			}

			log.warn(methodName, logMessage);

			res.setEsito(Esito.FALLIMENTO);
			setRollbackOnly();
		} catch (BusinessException e) {
			//SIAC-7945 log.info -> log.error
			log.error(methodName, "Errore di business nell'esecuzione del Servizio.", e);
			if(log.isTraceEnabled()) {
//				log.trace(methodName, "Stack trace: " + ExceptionUtils.getStackTrace(e));
			}
			if(e.getErrore()!=null){
				res.addErrore(e.getErrore());
			}
			res.setEsito(e.getEsito()!=null?e.getEsito():Esito.FALLIMENTO);
			if(e.isRollbackOnly()){
				setRollbackOnly();
			}
		} catch (RuntimeException e) {
			log.error(methodName, "Errore di runtime nell'esecuzione del Servizio.", e);
			//res = instantiateNewRes();
			res.addErroreDiSistema(e);
			handleRuntimeException(e);
			
			res.setEsito(Esito.FALLIMENTO);
			setRollbackOnly();
		} catch (Throwable t) {
			log.error(methodName, "Errore di sistema nell'esecuzione del Servizio.", t);
			//res = instantiateNewRes();			
			res.addErroreDiSistema(t);
			handleThrowable(t);
			
			res.setEsito(Esito.FALLIMENTO);
			setRollbackOnly();
		} finally {
			logServiceResponse();		
			log.debugEnd(methodName, "Esito response: " + res.getEsito());
		}
				
		
	}


	/**
	 * Consente di aggiungere eventuali errore di business piu' parlanti per l'utente a partire 
	 * da una RuntimeException che si e' verificata durante l'esecuzione del servizio.
	 * 
	 * @param e La runtimeException
	 */
	public void handleRuntimeException(RuntimeException e) {
		handleThrowable(e);
		
		if(e instanceof OptimisticLockException || e instanceof PessimisticLockException){
			res.addErrore(ErroreCore.ERRORE_DI_SISTEMA.getErrore("Si e' verificato un errore di accesso concorrente ai dati. Si prega di riprovare."));
		}
	}
	
	/**
	 * Consente di aggiungere eventuali errore di business piu' parlanti per l'utente a partire 
	 * da una Throwable che si e' verificata durante l'esecuzione del servizio.
	 * 
	 * @param e La runtimeException
	 */
	public void handleThrowable(Throwable t) {
		// Da implementare se necessario
	}
	
	/**
	 * Imposta a "rollback only" lo stato della transazione in corso se presente.
	 * Questo metodo presume che la transazione sia gestita tramite le Spring Transaction Api; nel caso venga utilizzata 
	 * un altra tecnologia questo metodo può essere sovrascritto. 
	 */
	protected void setRollbackOnly() {
		final String methodName = "rollback";
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
		checkRichiedente();
		checkServiceParam();
		if(res.hasErrori()){			
			throw new ServiceParamError(null);
		}		
		
	}


	


	/**
	 * Implementa, se necessario, il controllo dei parametri del servizio specifico.
	 * Fare override per gestire l'implementazione facoltativa.
	 * Utilizzare res.addErrore(errore) per aggiungere eventuali errori di business.
	 * Non solleva eccezioni e/o non aggiunge res.addErrore(errore) se il controllo è andato a buon fine. 
	 * @throws ServiceParamError in caso di errori
	 */
	protected void checkServiceParam() throws ServiceParamError{
		// To be implemented
	}

	/**
	 * Implementa l'esecuzione della bussiness logic del servizio.
	 * E' demandato all'implementazione di questo metodo 
	 * l'impostazione dell'esito del servizio
	 * Ad esempio in caso di successo bisognarà eseguire res.setEsito(Esito.SUCCESSO);
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
	protected void checkCondition(boolean condition, Errore errore, boolean toThrow) throws ServiceParamError{
		if(!condition){
			if(toThrow){
				throw new ServiceParamError(errore);
			}
			res.addErrore(errore);
		}
		
	}
	
	/**
	 * 
	 * Se condition è false aggiunge l'errore passato per parametro all'oggetto di response.
	 * Di default solleva l'eccezione interrompendo quindi il check di eventuali successivi parametri.
	 * 
	 * @param condition
	 * @param errore
	 * @throws ServiceParamError
	 */
	protected void checkCondition(boolean condition, Errore errore) throws ServiceParamError{
		checkCondition(condition, errore, true);
	}
	
	/**
	 * Se il parametro objectNotNull è null aggiunge l'errore passato per parametro all'oggetto di response.
	 * 
	 * @param objectNotNull
	 * @param errore
	 * @throws ServiceParamError 
	 */
	protected void checkNotNull(Object objectNotNull, Errore errore, boolean toThrow) throws ServiceParamError{
		checkCondition(objectNotNull!=null, errore, toThrow);		
	}
	
	/**
	 * Se il parametro objectNotNull è null aggiunge l'errore passato per parametro all'oggetto di response.
	 * Di default solleva l'eccezione interrompendo quindi il check di eventuali successivi parametri.
	 * 
	 * @param objectNotNull
	 * @param errore
	 * @throws ServiceParamError
	 */
	protected void checkNotNull(Object objectNotNull, Errore errore) throws ServiceParamError{
		checkCondition(objectNotNull!=null, errore, true);		
	}
	
	
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
		log.trace(methodName, "Codice fiscale Richiedente: "+req.getRichiedente().getOperatore().getCodiceFiscale());
	}
	
	
	
	/**
	 * Instanzia l'oggetto service response vuoto.
	 * Questo metodo prevede che l'oggetto serviceResponse abbia il costruttore vuoto.
	 * Per esigenze più complesse è necessario fare override.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected RES instantiateNewRes() {		
				
		try {
			@SuppressWarnings("rawtypes")
			Class[] genericTypeArguments = GenericTypeResolver.resolveTypeArguments(this.getClass(), BaseService.class);
			return (RES) genericTypeArguments[1].newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
			throw new IllegalArgumentException("Errore instanziamento automatico serviceResponse. "
					+ "Deve esistere un costruttore vuoto. Per esigenze più complesse "
					+ "sovrascrivere il metodo instantiateNewRes a livello di servizio.", e);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new IllegalArgumentException("Errore instanziamento automatico serviceResponse. Il costruttore vuoto non è accessibile.", e);
		} catch (Throwable t) {
			t.printStackTrace();
			throw new IllegalArgumentException("Errore instanziamento automatico serviceResponse. ", t);
		}
		
	
		/*
		try {
			ParameterizedType t = (ParameterizedType) getClass().getGenericSuperclass();
			Type[] actualTypeArguments = t.getActualTypeArguments();
			Type type  = actualTypeArguments[1];
			String className = (""+type).replaceAll("class ", "");	//TODO verificare se c'è un metodo più pulito		
			Class<?> c = Class.forName(className);
			return (RES) c.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
			throw new IllegalArgumentException("Errore instanziamento automatico serviceResponse. "
					+ "Deve esistere un costruttore vuoto. Per esigenze più complesse "
					+ "sovrascrivere il metodo instantiateNewRes a livello di servizio.", e);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new IllegalArgumentException("Errore instanziamento automatico serviceResponse. ", e);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new IllegalArgumentException("Errore instanziamento automatico serviceResponse. Non è stata trovata la classe che implementa la response.", e);
		}
		*/
	}
	
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

	
	private void logServiceRequest() {
		log.logXmlTypeObject(req, "Service Request param");
	}
	
	private void logServiceResponse() {
		log.logXmlTypeObject(res, "Service Response param");
	}
	
	

}
