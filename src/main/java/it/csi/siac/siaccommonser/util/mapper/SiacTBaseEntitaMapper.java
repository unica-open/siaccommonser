/*
*SPDX-FileCopyrightText: Copyright 2020 | CSI Piemonte
*SPDX-License-Identifier: EUPL-1.2
*/


package it.csi.siac.siaccommonser.util.mapper;


import it.csi.siac.siaccommonser.integration.entity.SiacTBase;
import it.csi.siac.siaccorser.model.Entita;

public abstract class SiacTBaseEntitaMapper<A extends SiacTBase, B extends Entita> extends PageBaseMapper<A, B> {

	@Override
	public void map(A a, B b) {
		b.setUid(a.getUid());
		b.setDataCreazione(a.getDataCreazione());
		b.setDataModifica(a.getDataModifica());
		b.setDataInizioValidita(a.getDataInizioValidita());
		b.setDataFineValidita(a.getDataFineValidita());
		b.setDataCancellazione(a.getDataCancellazione());
	}
}
