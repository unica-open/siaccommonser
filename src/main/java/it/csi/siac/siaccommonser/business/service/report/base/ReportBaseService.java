/*
*SPDX-FileCopyrightText: Copyright 2020 | CSI Piemonte
*SPDX-License-Identifier: EUPL-1.2
*/
package it.csi.siac.siaccommonser.business.service.report.base;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.GenericTypeResolver;

import it.csi.siac.siaccommonser.business.service.base.BaseService;
import it.csi.siac.siaccommonser.business.service.base.exception.ServiceParamError;
import it.csi.siac.siaccorser.frontend.webservice.msg.report.ReportServiceRequest;
import it.csi.siac.siaccorser.frontend.webservice.msg.report.ReportServiceResponse;
import it.csi.siac.siaccorser.model.errore.ErroreCore;



/**
 * Classe base dei servizi che generano dei report.
 * Delega la creazione del report al ReportHandler specificato.
 * 
 *
 * @author Domenico Lisi
 * 
 * @param <REQ> the ReportServiceRequest generic type
 * @param <RES> the ReportServiceResponse generic type
 * @param <RH> the BaseReportHandler generic type
 */
public abstract class ReportBaseService<REQ extends ReportServiceRequest,RES extends ReportServiceResponse,RH extends BaseReportHandler> extends BaseService<REQ, RES> {

	@Autowired
	protected ApplicationContext appCtx;
	
	protected RH reportHandler;
	
		
	@Override
	protected final void execute() {
		preStartElaboration();	
		initReportHandlerBase();
		startElaboration();	
	}	
	

	


	/**
	 * Inizializza la logica di controllo necessaria per inizializzare l'elaborazione.
	 * 
	 */
	protected abstract void preStartElaboration();
		
	/**
	 * Invoca l'elaborazione del reportHandler
	 */	
	protected abstract void startElaboration();
	

	/**
	 * Estende il check del Richiedente aggiungendo il check dell'Ente.
	 */
	@Override
	protected void checkRichiedente() throws ServiceParamError {
		super.checkRichiedente();
		checkNotNull(req.getEnte(), ErroreCore.PARAMETRO_NON_INIZIALIZZATO.getErrore("ente"));
		checkCondition(req.getEnte().getUid()!=0, ErroreCore.PARAMETRO_NON_INIZIALIZZATO.getErrore("uid ente"));
	}
	
	/**
	 * Metodo di base per inizializzare il reportHandler.
	 */
	protected void initReportHandlerBase() {
		final String methodName = "initReportHandlerBase";
		log.debug(methodName, "invoked");
		Class<RH> reportHandlerClass = getReportHandlerClass();
		try{
			reportHandler = appCtx.getBean(reportHandlerClass);
		} catch(BeansException be) {
			log.error(methodName, "Impossibile ottenere il reportHandler! Errore nell'ottenimento del component:  "+reportHandlerClass.getName() + " :"+be.getMessage(),be);
			throw be;
		}
		if(reportHandler==null){
			log.error(methodName, "Impossibile ottenere il reportHandler! Nessun component tovato per "+reportHandlerClass.getName());
		}
		reportHandler.setEnte(req.getEnte());
		reportHandler.setRichiedente(req.getRichiedente());
		initReportHandler();
	}
	
	/**
	 * Inizializza i parametri di input del reportHandler.
	 */
	protected abstract void initReportHandler();




	/**
	 * Specifica la classe finale del reportHandler.
	 * Di default viene istanziata quella espressa nel GenericType, ma sovrascrivendo il metodo &egrave; 
	 * possibile specificare una classe.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected Class<RH> getReportHandlerClass() {	
		String methodName = "getReportHandlerClass";
				
		try {
			@SuppressWarnings("rawtypes")
			Class[] genericTypeArguments = GenericTypeResolver.resolveTypeArguments(this.getClass(), ReportBaseService.class);
			return genericTypeArguments[2];
		} catch (Exception t) {
			String msg = "Errore risoluzione classe del reportHandler. ";
			log.error(methodName, msg, t);
			throw new IllegalArgumentException(msg, t);
		}
	}
	
//	/**
//	 * Estende l'inizializzazione della response aggiungendo l'inizializzazione del reportHandler.
//	 */
//	@Override
//	protected RES instantiateNewRes() {
//		RES result = super.instantiateNewRes();		
//		return result;
//	}

	
//	/**
//	 * Nota: non sovrascrivere questo metodo cambiando il tipo di 
//	 * propagazione della transazione!
//	 * @Transactional(propagation=Propagation.NEVER)
//	 */
//	@Override
//	@Transactional(propagation=Propagation.REQUIRED)
//	public RES executeService(REQ serviceRequest) {
//		return super.executeService(serviceRequest);		
//	}
	
//	/**
//	 * Attende che sia effettivamente terminata l'elaborazione del reportHandler
//	 * @throws InterruptedException
//	 * @throws ExecutionException
//	 */
//	protected final void waitElaborationEnd() {
//		final String methodName = "waitElaborationEnd";
//		try{
//			log.info(methodName, "Attendo il termine dell'elaborazione...");
//			reportHandlerResponse.get();
//			log.info(methodName, "Elaborazione terminata senza eccezioni.");
//			postElaborationSuccess();
//		} catch (CancellationException e) {			
//			elaborationError(e);
//		} catch (InterruptedException e) {			
//			elaborationError(e);
//		} catch (ExecutionException e) {
//			elaborationError(e);			
//		}
//		
//	}
//
//	private void elaborationError(ExecutionException e) {
//		final String methodName = "elaborationError";
//		String msg = "Errore durante l'esecuzione dell'elaborazione: "+ e.getCause().getMessage();
//		log.error(methodName, msg, e);
//		elaborationError(new ReportElaborationException(msg, e.getCause()));		
//	}
//	
//	private void elaborationError(InterruptedException e) {
//		final String methodName = "elaborationError";		
//		String msg = "Esecuzione elaborazione interrotta";
//		log.error(methodName, msg, e);
//		elaborationError(new ReportElaborationException(msg, e));		
//	}
//	
//	private void elaborationError(CancellationException e) {		
//		final String methodName = "elaborationError";	
//		String msg = "Elaborazione cancellata";
//		log.error(methodName, msg, e);
//		elaborationError(new ReportElaborationException(msg, e));
//	}
//	
//	private void elaborationError(ReportElaborationException e) {
//		postElaborationError(e);
//		throw e;
//		
//		//In alternativa:
////		setRollbackOnly();
////		res.setEsito(Esito.FALLIMENTO);
////		res.addErrore(ErroreCore.ERRORE_DI_SISTEMA.getErrore(e.getMessage()));
//		
//	}
//	
//	
//
//
//
//	/**
//	 * 
//	 * Viene invocato se in postStartElaboration viene richiamato waitElaboration() (ovvero al termine dell'elaborazione) e 
//	 * se l'elaborazione &egrave; terminata senza eccezioni.
//	 * 
//	 * .
//	 * 
//	 */
//	protected void postElaborationSuccess() {
//		
//	}
//	
//	/**
//	 * Viene invocato se in postStartElaboration viene richiamato waitElaboration() (ovvero al termine dell'elaborazione) e 
//	 * se l'elaborazione &egrave; terminata con errori.
//	 *
//	 * @param e the ReportElaborationException
//	 */
//	protected void postElaborationError(ReportElaborationException e) {
//		
//	}

}
