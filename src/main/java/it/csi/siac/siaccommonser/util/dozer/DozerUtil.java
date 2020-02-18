/*
*SPDX-FileCopyrightText: Copyright 2020 | CSI Piemonte
*SPDX-License-Identifier: EUPL-1.2
*/
package it.csi.siac.siaccommonser.util.dozer;

import java.util.ArrayList;
import java.util.List;

import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DozerUtil {
	@Autowired
	private Mapper mapper;

	public <T> T map(Object source, Class<T> clazz) {
		return mapper.map(source, clazz);
	}

	public void map(Object source, Object dest) {
		mapper.map(source, dest);
	}

	public <T> T map(Object source, Class<T> clazz, MapId mapId) {
		String mapIdStr = mapId != null ? mapId.name() : null;
		return mapper.map(source, clazz, mapIdStr);
	}

	public void map(Object source, Object dest, MapId mapId) {
		String mapIdStr = mapId != null ? mapId.name() : null;
		mapper.map(source, dest, mapIdStr);
	}

	public <T> T mapNotNull(Object source, Class<T> clazz) {
		if (source != null)
			return map(source, clazz);
		return null;
	}

	public void mapNotNull(Object source, Object dest) {
		if (source != null)
			map(source, dest);
	}

	public <T> T mapNotNull(Object source, Class<T> clazz, MapId mapId) {
		if (source != null)
			return map(source, clazz, mapId);
		return null;
	}

	public void mapNotNull(Object source, Object dest, MapId mapId) {
		if (source != null)
			map(source, dest, mapId);
	}

	public <A, DA> List<A> mapList(List<DA> listDa, Class<A> classA) {
		return mapList(listDa, classA, null);
	}

	public <A, DA> List<A> mapList(List<DA> listDa, Class<A> classA,
			MapId mapId) {
		if (listDa == null)
			return null;

		List<A> listA = new ArrayList<A>();

		for (DA tuplaDa : listDa) {
			A mapped = map(tuplaDa, classA, mapId);
			listA.add(mapped);
		}

		return listA;
	}

}
