/*
*SPDX-FileCopyrightText: Copyright 2020 | CSI Piemonte
*SPDX-License-Identifier: EUPL-1.2
*/
package it.csi.siac.siaccommonser.business.service.report.base;

import org.springframework.beans.factory.annotation.Autowired;

import it.csi.siac.siaccorser.frontend.webservice.msg.report.ReportServiceRequest;
import it.csi.siac.siaccorser.frontend.webservice.msg.report.ReportServiceResponse;

/**
 * Classe base dei servizi che generano dei report. Delega la creazione del
 * report al ReportHandler specificato. Specializza la gestione sincrona della
 * generazione del report: la response viene restuita al termine
 * dell'elaborazione.
 * 
 * 
 * @author Domenico Lisi
 * 
 * @param <REQ>
 *            the ReportServiceRequest generic type
 * @param <RES>
 *            the ReportServiceResponse generic type
 * @param <RH>
 *            the BaseReportHandler generic type
 */
public abstract class AsyncReportBaseService<REQ extends ReportServiceRequest, RES extends ReportServiceResponse, RH extends BaseReportHandler>
		extends ReportBaseService<REQ, RES, RH> {

	@Autowired
	protected AsyncReportHandlerDelegate reportHandlerDelegate;

	// protected Future<Void> reportHandlerResponse;

	/**
	 * Invoca l'elaborazione del reportHandler in modo asincrono.
	 */
	@Override
	protected void startElaboration() {
		String methodName = "elaborate";
		log.info(methodName, "inizio elaborazione");
		reportHandlerDelegate.elaborateAsync(reportHandler);
		log.info(methodName, "elaborazione avviata...");
		postStartElaboration();
	}

	/**
	 * Viene invocato appena dopo aver avviato l'elaborazione del reportHandler.
	 * 
	 */
	protected abstract void postStartElaboration();

}
