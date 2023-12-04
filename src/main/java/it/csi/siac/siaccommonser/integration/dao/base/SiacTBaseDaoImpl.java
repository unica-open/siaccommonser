/*
*SPDX-FileCopyrightText: Copyright 2020 | CSI Piemonte
*SPDX-License-Identifier: EUPL-1.2
*/
package it.csi.siac.siaccommonser.integration.dao.base;

import java.util.Date;

import it.csi.siac.siaccommonser.integration.entity.SiacTBase;

public abstract class SiacTBaseDaoImpl<E extends SiacTBase, PK> extends JpaDao<E , PK> {

	@Override
	public E create(E siacTBase){
		
		Date now = new Date();
		siacTBase.setDataModificaInserimento(now);
		siacTBase.setDataCancellazione(null);
		siacTBase.setUid(null);
		
		super.save(siacTBase);
		return siacTBase;
	}
	
	@Override
	public E update(E siacTBase){
		Date now = new Date();
		siacTBase.setDataModificaAggiornamento(now);
		return super.update(siacTBase);
	}

	
	@Override
	public E logicalDelete(E siacTBase){
		Date now = new Date();
		siacTBase.setDataCancellazioneIfNotSet(now);
		return super.logicalDelete(siacTBase);		
	}
	
}
