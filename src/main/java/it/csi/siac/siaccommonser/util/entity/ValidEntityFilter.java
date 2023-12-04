/*
*SPDX-FileCopyrightText: Copyright 2020 | CSI Piemonte
*SPDX-License-Identifier: EUPL-1.2
*/
package it.csi.siac.siaccommonser.util.entity;

import it.csi.siac.siaccommon.util.collections.Filter;
import it.csi.siac.siaccommonser.integration.entity.SiacTBase;

public class ValidEntityFilter<E extends SiacTBase> implements Filter<E> {

	@Override
	public boolean isAcceptable(E entity) {
		return EntityUtil.isValid(entity);
	}

}
