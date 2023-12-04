/*
*SPDX-FileCopyrightText: Copyright 2020 | CSI Piemonte
*SPDX-License-Identifier: EUPL-1.2
*/
package it.csi.siac.siaccommonser.integration.dao.test;

import java.util.List;

import it.csi.siac.siaccommonser.integration.dao.base.Dao;
import it.csi.siac.siaccommonser.integration.entity.SiacTBase;

public interface TestDao extends Dao<SiacTBase, Integer> {
	
	public List<? extends SiacTBase> testQuery(String query, Object...params);
}
