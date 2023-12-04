/*
*SPDX-FileCopyrightText: Copyright 2020 | CSI Piemonte
*SPDX-License-Identifier: EUPL-1.2
*/
package it.csi.siac.siaccommonser.util.entity;

import java.util.Collection;
import java.util.List;

import it.csi.siac.siaccommon.util.collections.CollectionUtil;
import it.csi.siac.siaccommon.util.collections.Function;
import it.csi.siac.siaccommonser.integration.entity.SiacTBase;

public final class EntityUtil {

	private EntityUtil() {
	}

	public static <E extends SiacTBase> List<E> getAllValid(Collection<E> entityList) {
		return CollectionUtil.filter(entityList, new ValidEntityFilter<E>());
	}

	public static <E1 extends SiacTBase, E2 extends SiacTBase> List<E2> getAllValidMappedBy(Collection<E1> entityList, Function<E1, E2> mappingFunction) {
		return getAllValid(CollectionUtil.map(getAllValid(entityList), mappingFunction));
	}

	public static <E extends SiacTBase> E findFirstValid(Collection<E> entityList) {
		return CollectionUtil.findFirst(entityList, new ValidEntityFilter<E>());
	}

	public static boolean isValid(SiacTBase entity) {
		return entity != null && entity.isEntitaValida();
	}

	public static <E extends SiacTBase> E getValid(E entity) {
		return isValid(entity) ? entity : null;
	}
}
