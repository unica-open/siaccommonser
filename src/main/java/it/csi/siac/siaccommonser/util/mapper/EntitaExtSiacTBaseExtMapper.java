/*
*SPDX-FileCopyrightText: Copyright 2020 | CSI Piemonte
*SPDX-License-Identifier: EUPL-1.2
*/


package it.csi.siac.siaccommonser.util.mapper;


import it.csi.siac.siaccommonser.integration.entity.SiacTBaseExt;
import it.csi.siac.siaccorser.model.EntitaExt;

public abstract class EntitaExtSiacTBaseExtMapper<A extends EntitaExt, B extends SiacTBaseExt> extends EntitaSiacTBaseMapper<A, B> {

	@Override
	public void map(A a, B b) {
		super.map(a, b);
		b.setLoginCreazione(a.getLoginCreazione());
		b.setLoginModifica(a.getLoginModifica());
		b.setLoginCancellazione(a.getLoginCancellazione());
	}

}
