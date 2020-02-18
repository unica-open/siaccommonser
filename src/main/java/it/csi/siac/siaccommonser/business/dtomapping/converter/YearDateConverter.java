/*
*SPDX-FileCopyrightText: Copyright 2020 | CSI Piemonte
*SPDX-License-Identifier: EUPL-1.2
*/
package it.csi.siac.siaccommonser.business.dtomapping.converter;

import java.util.Calendar;
import java.util.Date;

import org.dozer.CustomConverter;

import it.csi.siac.siaccorser.frontend.webservice.exception.ServiceException;
import it.csi.siac.siaccorser.model.errore.ErroreCore;

public class YearDateConverter implements CustomConverter {

	@Override
	public Object convert(Object dest, Object src, Class<?> destCls,
			Class<?> srcCls) {

		if (src == null)
			return null;

		if (src instanceof Integer) {
			Calendar cal = Calendar.getInstance();

			cal.clear();
			cal.set(Calendar.YEAR, (Integer) src);

			return cal.getTime();
		}

		if (src instanceof Date) {
			Calendar cal = Calendar.getInstance();
			cal.setTime((Date) src);
			return cal.get(Calendar.YEAR);
		}

		throw new ServiceException(
				ErroreCore.ERRORE_DI_SISTEMA
						.getErrore("Oggetto di origine non valido"));

	}

	public Integer convert(Date date) {
		return (Integer) convert(null, date, Integer.class, Date.class);
	}

	public Date convert(Integer year) {
		return (Date) convert(null, year, Date.class, Integer.class);
	}

}
