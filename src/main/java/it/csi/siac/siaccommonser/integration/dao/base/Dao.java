/*
*SPDX-FileCopyrightText: Copyright 2020 | CSI Piemonte
*SPDX-License-Identifier: EUPL-1.2
*/
package it.csi.siac.siaccommonser.integration.dao.base;

import java.util.List;

public interface Dao<E, PK> {
	
	void save(E entity);

	E create(E entity);
	
	E update(E entity);

	void delete(E entity);

	List<E> findAll(int... rowStartIdxAndCount);

	E findById(PK id);

	void flushAndClear();
	void flush();
}


