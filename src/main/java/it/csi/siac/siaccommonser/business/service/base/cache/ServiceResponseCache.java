/*
*SPDX-FileCopyrightText: Copyright 2020 | CSI Piemonte
*SPDX-License-Identifier: EUPL-1.2
*/
package it.csi.siac.siaccommonser.business.service.base.cache;

import java.util.Date;

import it.csi.siac.siaccommon.util.JAXBUtility;
import it.csi.siac.siaccorser.model.ServiceResponse;

/**
 * Cache item for a single ServiceResponse.
 *  
 * @author Domenico
 * @version 1.0.0 - 30/09/2014
 *
 */
public class ServiceResponseCache extends CacheItem {
	
	private static final long serialVersionUID = -7823455224697608769L;
	
	private String serviceResponseXml;
	private Class<? extends ServiceResponse> serviceResponseClass;
	private long elapsedMillis;
	
	/**
	 * Default constructor.
	 */
	public ServiceResponseCache() {
		super();
	}
	
	/**
	 * Constructor given a serviceResponse.
	 * 
	 * @param serviceResponse the response to create the cached item for
	 */
	public ServiceResponseCache(ServiceResponse serviceResponse) {
		super();
		setServiceResponse(serviceResponse);
	}
	
	/**
	 * Constructor given a serviceResponse and elapsedMillis.
	 *
	 * @param serviceResponse the response to create the cached item for
	 * @param elapsedMillis the elapsed millis
	 */
	public ServiceResponseCache(ServiceResponse serviceResponse, long elapsedMillis) {
		super();
		setServiceResponse(serviceResponse);
		setElapsedMillis(elapsedMillis);
	}
	
	/**
	 * Constructor given a serviceResponse, a cacheDate, and a hitCount.
	 * 
	 * @param serviceResponse the response to create the cached item for
	 * @param cacheDate       the initial cache date
	 * @param hitCount        the initial hitCount
	 */
	public ServiceResponseCache(ServiceResponse serviceResponse, Date cacheDate, int hitCount) {
		super(cacheDate,hitCount);
		setServiceResponse(serviceResponse);
	}
	
	/**
	 * Constructor given a serviceResponse, a cacheDate, a hitCount and elapsedMillis.
	 * 
	 * @param serviceResponse the response to create the cached item for
	 * @param cacheDate       the initial cache date
	 * @param hitCount        the initial hitCount
	 * @param elapsedMillis   the elapsed millis
	 * 
	 */
	public ServiceResponseCache(ServiceResponse serviceResponse, Date cacheDate, int hitCount, long elapsedMillis) {
		super(cacheDate,hitCount);
		setServiceResponse(serviceResponse);
		setElapsedMillis(elapsedMillis);
	}
	
	/**
	 * @return the reportXml
	 */
	public String getServiceResponseXml() {
		return serviceResponseXml;
	}
	/**
	 * @param reportXml the reportXml to set
	 */
	public void setServiceResponseXml(String reportXml) {
		this.serviceResponseXml = reportXml;
	}
	/**
	 * @return the serviceResponseClass
	 */
	public Class<? extends ServiceResponse> getServiceResponseClass() {
		return serviceResponseClass;
	}
	/**
	 * @return the serviceResponse
	 */
	@SuppressWarnings("unchecked")
	public <RES extends ServiceResponse> RES getServiceResponse() {
		return (RES) JAXBUtility.unmarshall(serviceResponseXml, serviceResponseClass);
	}
	/**
	 * @param serviceResponse the serviceResponse to set
	 */
	public final void setServiceResponse(ServiceResponse serviceResponse) {
		this.serviceResponseClass = serviceResponse.getClass();
		this.serviceResponseXml = JAXBUtility.marshall(serviceResponse);
	}

	/**
	 * @return the elapsedMillis
	 */
	public long getElapsedMillis() {
		return elapsedMillis;
	}

	/**
	 * @param elapsedMillis the elapsedMillis to set
	 */
	public final void setElapsedMillis(long elapsedMillis) {
		this.elapsedMillis = elapsedMillis;
	}

}
