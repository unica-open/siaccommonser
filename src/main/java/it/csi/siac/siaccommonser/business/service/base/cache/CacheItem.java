/*
*SPDX-FileCopyrightText: Copyright 2020 | CSI Piemonte
*SPDX-License-Identifier: EUPL-1.2
*/
package it.csi.siac.siaccommonser.business.service.base.cache;

import java.io.Serializable;
import java.util.Date;

/**
 * Base class for a cached item.
 * 
 * @author Domenico
 * @version 1.0.0 - 30/09/2014
 *
 */
public class CacheItem implements Serializable{
	
	private static final long serialVersionUID = 752382703503805613L;
	
	private Date cacheDate;
	private int hitCount;
	
	/**
	 * Default constructor. Sets the hitCount to 1.
	 */
	public CacheItem() {
		this(new Date(), 1);
	}

	/**
	 * Constructs a cacheItem with specified cacheDate and hitCount.
	 * 
	 * @param cacheDate the cache date
	 * @param hitCount  the hit count
	 */
	public CacheItem(Date cacheDate, int hitCount) {
		super();
		this.cacheDate = cacheDate != null ? new Date(cacheDate.getTime()) : null;
		this.hitCount = hitCount;
	}
	
	
	/**
	 * @return the cacheDate
	 */
	public Date getCacheDate() {
		return cacheDate != null ? new Date(cacheDate.getTime()) : null;
	}
	/**
	 * @param cacheDate the cacheDate to set
	 */
	public void setCacheDate(Date cacheDate) {
		this.cacheDate = cacheDate != null ? new Date(cacheDate.getTime()) : null;
	}
	/**
	 * @return the hitCount
	 */
	public int getHitCount() {
		return hitCount;
	}
	
	/**
	 * Hits the cached element, incrementing the hitCount.
	 * <br>
	 * This method is not thread-safe, therefore a lock or a synchronized block should be use if thread-safety is to be ensured.
	 * 
	 * @return the incremented hitCount
	 */
	public int hit() {
		return ++hitCount;
	}

}
