/*
*SPDX-FileCopyrightText: Copyright 2020 | CSI Piemonte
*SPDX-License-Identifier: EUPL-1.2
*/
package it.csi.siac.siaccommonser.integration.dao.test;

import java.util.List;

import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import it.csi.siac.siaccommonser.integration.dao.base.JpaDao;
import it.csi.siac.siaccommonser.integration.entity.SiacTBase;

@Component
@Transactional
public class TestDaoImpl extends JpaDao<SiacTBase, Integer> implements TestDao {

	@Override
	public List<? extends SiacTBase> testQuery(String sql, Object... params) {
		
		if (StringUtils.isBlank(sql)) {
			return null;
		}
		
		Query query = entityManager.createQuery(sql);

		if (params != null) {
			for (int i = 0; i < params.length;) {
				query.setParameter(params[i++].toString(), params[i++]);
			}
		}

		@SuppressWarnings("unchecked")
		List<? extends SiacTBase> x = query.getResultList();

		System.out.println("TESTDAO: result size = " + (x == null ? null : x.size()));
		
		return x;
	}

}
