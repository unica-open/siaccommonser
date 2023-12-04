/*
*SPDX-FileCopyrightText: Copyright 2020 | CSI Piemonte
*SPDX-License-Identifier: EUPL-1.2
*/
package it.csi.siac.siaccommonser.util.dozer.converter;

import java.math.BigDecimal;

import org.dozer.DozerConverter;

import it.csi.siac.siaccommon.util.log.LogUtil;


public class BigDecimalToIntegerConverter extends DozerConverter<BigDecimal, Integer> {
	private final LogUtil log = new LogUtil(this.getClass());

	public BigDecimalToIntegerConverter() {
		super(BigDecimal.class, Integer.class);
	}

	@Override
	public BigDecimal convertFrom(Integer src, BigDecimal dest) {
		String methodName = "convertFrom";
		try {
			return src == null ? null : new BigDecimal(src.intValue());
		} catch (NumberFormatException nfe) {
			log.debug(methodName, nfe.getMessage());
		}
		
		return dest;
	}

	@Override
	public Integer convertTo(BigDecimal src, Integer dest) {
		return src == null ? null : src.intValue();
	}

}
