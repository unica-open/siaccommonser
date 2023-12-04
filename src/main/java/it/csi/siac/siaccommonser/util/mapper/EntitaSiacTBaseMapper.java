/*
*SPDX-FileCopyrightText: Copyright 2020 | CSI Piemonte
*SPDX-License-Identifier: EUPL-1.2
*/


package it.csi.siac.siaccommonser.util.mapper;


import it.csi.siac.siaccommon.util.mapper.BaseMapper;
import it.csi.siac.siaccommonser.integration.entity.SiacTBase;
import it.csi.siac.siaccorser.model.Entita;

public abstract class EntitaSiacTBaseMapper<A extends Entita, B extends SiacTBase> extends BaseMapper<A, B> {

	@Override
	public void map(A a, B b) {
		b.setUid(a.getUid());
		b.setLoginOperazione(a.getLoginOperazione());
	}

}
