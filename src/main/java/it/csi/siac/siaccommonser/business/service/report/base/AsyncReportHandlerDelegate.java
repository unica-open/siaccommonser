/*
*SPDX-FileCopyrightText: Copyright 2020 | CSI Piemonte
*SPDX-License-Identifier: EUPL-1.2
*/
package it.csi.siac.siaccommonser.business.service.report.base;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Delegate per l'esecuzione dei un ReportHandler in modo asincrono.
 * 
 * @author Domenico
 *
 */
@Component
public class AsyncReportHandlerDelegate {
	
	//VERSIONE 1
	/**
	 * Esegue il metodo elaborate del reportHandler passato come parametro in modo asincorono.
	 *
	 * @param <RH> the generic type
	 * @param reportHandler the report handler
	 * @return the future
	 */
	@Async
	public <RH extends BaseReportHandler> void elaborateAsync(RH reportHandler) {
		reportHandler.elaborate();
	}
	
	
	
	//VERSIONE 2
	
//	@Autowired
//	private TaskExecutor taskExecutor;
//	
//	/**
//	 * Esegue il metodo elaborate del reportHandler passato come parametro in modo asincorono.
//	 *
//	 * @param <RH> the generic type
//	 * @param reportHandler the report handler
//	 */
//	@Transactional(propagation=Propagation.NOT_SUPPORTED)
//	public <RH extends BaseReportHandler> void elaborateAsync(final RH reportHandler) {
//		
//		
//		taskExecutor.execute(new Runnable() {
//			
//			@Override
//			public void run() {				
//				reportHandler.elaborate();				
//			}
//			
//		});
//
//	}
	
	
	
	//VERSIONE 3
	
//	@Autowired
//	private ConcurrentTaskExecutor taskExecutor;
//	
//	/**
//	 * Esegue il metodo elaborate del reportHandler passato come parametro in modo asincorono.
//	 *
//	 * @param <RH> the generic type
//	 * @param reportHandler the report handler
//	 * @return the future
//	 */
//	//@Async
//	@Transactional(propagation=Propagation.NOT_SUPPORTED)
//	public <RH extends BaseReportHandler> Future<Void> elaborateAsync(final RH reportHandler) {
//		
//		
//		return taskExecutor.submit(new Callable<Void>() {
//			
//			@Override
//			public Void call() {
//				reportHandler.elaborate();
//				//return new AsyncResult<Void>(null);
//				return null;
//			}
//			
//		}
//		);
//		 
//		
//		//reportHandler.elaborate();
//		//return new AsyncResult<Void>(null);
//	}

}
