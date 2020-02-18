/*
*SPDX-FileCopyrightText: Copyright 2020 | CSI Piemonte
*SPDX-License-Identifier: EUPL-1.2
*/
package it.csi.siac.siaccommonser.business.dtomapping.converter;

import java.util.Date;
import java.util.GregorianCalendar;

import org.dozer.ConfigurableCustomConverter;

/**
 * Converte qualunque Object (non String) in uno String a partire dal formato passato come parametro.
 * Per il formato vedere java.util.Formatter
 * 
 * http://docs.oracle.com/javase/6/docs/api/java/util/Formatter.html#syntax
 * 
 * @author Domenico
 *
 */
public class StringFormatConverter implements ConfigurableCustomConverter {

	// Se non specificata il default e' di tipo stringa
	private String formatMask = "%s";

	@Override
	public Object convert(Object a, Object b, Class<?> ac, Class<?> bc) {
		
		if("java.lang.String".equals(bc.getName())){
			return String.format(formatMask, a);
		} else if ("java.lang.String".equals(ac.getName())){
			return String.format(formatMask, b);
		}		
		
		return null;
	}

	@Override
	public void setParameter(String formatMask) {
		//example: "%05d"
		this.formatMask = formatMask;
	}
	
	
	
	public static void main(String[] args) {
		StringFormatConverter c = new StringFormatConverter();
		c.setParameter("%05d");
		System.out.println("converted: "+c.convert(Integer.valueOf(5), null, Integer.class, String.class ));
		//solo l'anno
		c.setParameter("%1$tY");
		System.out.println("converted: "+c.convert(new GregorianCalendar(), null, GregorianCalendar.class, String.class ));
		c.setParameter("%1$tY");
		System.out.println("converted: "+c.convert(new Date(), null, Date.class, String.class ));
		//solo il giorno
		c.setParameter("%1$te");
		System.out.println("converted: "+c.convert(null, new Date(), String.class,   Date.class));
		c.setParameter("%1$te/%1$tm/%1$tY");
		System.out.println("converted: "+c.convert(new Date(), null, Date.class, String.class ));
	}

}
