/*
*SPDX-FileCopyrightText: Copyright 2020 | CSI Piemonte
*SPDX-License-Identifier: EUPL-1.2
*/
package it.csi.siac.siaccommonser.integration.entitymapping;

import org.dozer.CustomConverter;

/**
 * Interfaccia di base per gli enum ModelDetailEnum.
 * Rappresenta una dettaglio di una classe di Model e fornisce la classe dell converter che ottiene tale dettaglio. 
 *
 * @author Domenico Lisi
 */
public interface Converter {
	

	/**
	 * Il converter associato al dettaglio di Model.
	 *
	 * @return the converterClass
	 */
	public Class<? extends CustomConverter> getCustomConverterClass();	
	

}
