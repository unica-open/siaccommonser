/*
*SPDX-FileCopyrightText: Copyright 2020 | CSI Piemonte
*SPDX-License-Identifier: EUPL-1.2
*/
package it.csi.siac.siaccommonser.business.service.report.base;

import org.springframework.beans.factory.annotation.Autowired;

import it.csi.siac.siaccommon.util.log.LogUtil;
import it.csi.siac.siaccorser.frontend.webservice.ReportService;
import it.csi.siac.siaccorser.frontend.webservice.msg.report.GeneraReport;
import it.csi.siac.siaccorser.frontend.webservice.msg.report.GeneraReportResponse;
import it.csi.siac.siaccorser.model.Ente;
import it.csi.siac.siaccorser.model.Richiedente;

/**
 * Handler di base per la creazione di un Report.
 * 
 * @author Domenico Lisi
 *
 */
public abstract class BaseReportHandler {
	
	protected LogUtil log = new LogUtil(this.getClass());
		
	protected Ente ente;
	protected Richiedente richiedente;
		
	
	@Autowired
	private ReportService reportService;
	protected GeneraReportResponse generaReportResponse;



	/**
	 * Elaborazione del report.
	 * 
	 */
	public void elaborate() {
		final String methodName = "elaborate";

		try {
			
			//Elabora i dati del report
			elaborateDataBase();
			
			//Invoca il servizio di generazione del report
			generaReportBase();

			//Gestisce la respose del servizio di generazione del report
			handleResponseBase();			

		} catch (Exception e) {
//			if(e instanceof ReportElaborationException){
//				throw (ReportElaborationException)e;
//			}
			String msg = "Errore durante l'elaborazione del report ";
			log.error(methodName, msg, e);
			throw new ReportElaborationException(msg, e);
		}
	}

	
	/**
	 * Richiama l'implementazione di elaborateData
	 */
	protected void elaborateDataBase() {
		final String methodName = "elaborateDataBase";
		log.info(methodName, "Inizio elaborazione dati del report.");
		try {
			elaborateData();
		} catch (Exception e) {
			String msg = "Errore durante l'elaborazione dei dati del report.";
			log.error(methodName, msg, e);
			throw new ReportElaborationException(msg, e);
		}
		log.info(methodName, "Fine elaborazione dati del report.");
	}
	

	/**
	 * Elabora i dati necessari a costruire il Report
	 */	
	protected abstract void elaborateData();
	
	/**
	 * Ottiene i dati del report in formato xml
	 * 
	 * @return xml contenente i dati il report
	 */
	protected abstract String getReportXml();
	
	/**
	 * Ottiene il codice del template
	 * 
	 * @return codice del template
	 */
	public abstract String getCodiceTemplate();


	protected void generaReportBase() {
		final String methodName = "generaReportBase";
		log.info(methodName, "Inizio generazione report... ");

		String codiceTemplate = getCodiceTemplate();
		String reportXml = getReportXml();

		generaReportResponse = generaReport(codiceTemplate, reportXml);

		log.debug(methodName, "Fine generazione report.");
	}

	


	/**
	 * Invoca il servizio di generazione del report.
	 * 
	 * @return
	 */
	protected GeneraReportResponse generaReport(String codiceTemplate, String objectXml) {
		final String methodName = "generaReport";
			
		
			GeneraReport req = new GeneraReport();
			req.setRichiedente(richiedente);
			req.setEnte(ente);
			
			req.setCodiceTemplate(codiceTemplate);
			req.setObjectXml(objectXml);
			
			GeneraReportResponse res = null;
		try {
			res = reportService.generaReport(req);
			return res;
		} catch (Throwable e) {			
			String msg = "Errore durante la generazione del report effettuata dal servizio GeneraReport.";
			log.error(methodName, msg, e);
			throw new ReportElaborationException(msg, e);
		} finally {
			log.logXmlTypeObject(res, "GeneraReportResponse");
		}
	}
	
	/**
	 * Richiama l'implementazione di handleResponse
	 * 
	 * @param res
	 */
	protected void handleResponseBase() {
		final String methodName = "handleResponseBase";
		log.debug(methodName, "Inizio gestione risposta... ");
		
		if(generaReportResponse==null){
			String msg = "Risposta del servizo generaReportResponse null.";
			log.error(methodName, msg);
			throw new ReportElaborationException(msg);
		}
		
		if(generaReportResponse.isFallimento()){
			String msg = "Esito del servizo generaReportResponse FALLIMENTO.";
			log.error(methodName, msg);
			throw new ReportElaborationException(msg);
		}
		
		try{
			handleResponse(generaReportResponse);
		} catch (Exception e) {
			String msg = "Errore durante la gestione dei dati di ritorno del report.";
			log.error(methodName, msg, e);
			throw new ReportElaborationException(msg, e);
		}
		log.debug(methodName, "Fine gestione risposta.");
	}

	
	
	/**
	 * Gestisce la risposta di creazione del report.
	 * Viene invocato quando il report è stato creato.
	 *  
	 * @param res rsponse del servizio di creazione del report.
	 */
	protected abstract void handleResponse(GeneraReportResponse res);


	/**
	 * @return the ente
	 */
	public Ente getEnte() {
		return ente;
	}


	/**
	 * @param ente the ente to set
	 */
	public void setEnte(Ente ente) {
		this.ente = ente;
	}


	/**
	 * @return the richiedente
	 */
	public Richiedente getRichiedente() {
		return richiedente;
	}


	/**
	 * @param richiedente the richiedente to set
	 */
	public void setRichiedente(Richiedente richiedente) {
		this.richiedente = richiedente;
	}



	/**
	 * @return the generaReportResponse
	 */
	public GeneraReportResponse getGeneraReportResponse() {
		return generaReportResponse;
	}	
	
	
	
}
