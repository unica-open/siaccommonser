/*
*SPDX-FileCopyrightText: Copyright 2020 | CSI Piemonte
*SPDX-License-Identifier: EUPL-1.2
*/
package it.csi.siac.siaccommonser.integration.dad.base;

import java.util.ArrayList;
import java.util.List;

import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import it.csi.siac.siaccommon.util.log.LogUtil;
import it.csi.siac.siaccommonser.util.dozer.MapId;
import it.csi.siac.siaccorser.model.Entita;
import it.csi.siac.siaccorser.model.paginazione.ListaPaginata;
import it.csi.siac.siaccorser.model.paginazione.ListaPaginataImpl;
import it.csi.siac.siaccorser.model.paginazione.ParametriPaginazione;

/**
 * Data access Delegate di base. Delega l'accesso al database utilizzando in
 * input esclusivamente entit&agrave; di dominio (Domain Model) e non le entit&agrave; di
 * persistenza (Persistence Model - vedi DTO). In questo delegate non vanno
 * inserite operazioni di business ma esclusivamente operazioni di conversione
 * tra Domain Model e Persistence Model e richiamo del relativo dao.
 * 
 * In questa implementazione base viene iniettato dozerBeanMapper come utility
 * di conversione di cui vengono esposti i quattro metodi di mapping.
 * 
 * @author Domenico Lisi
 * 
 */
public class BaseDadImpl {

	protected LogUtil log = new LogUtil(getClass());

	@Autowired
	private Mapper mapper;

	protected <T> T map(Object source, Class<T> clazz) {
		return mapper.map(source, clazz);
	}

	protected void map(Object source, Object dest) {
		mapper.map(source, dest);
	}

	protected <T> T map(Object source, Class<T> clazz, MapId mapId) {
		String mapIdStr = mapId != null ? mapId.name() : null;
		return mapper.map(source, clazz, mapIdStr);
	}

	protected void map(Object source, Object dest, MapId mapId) {
		String mapIdStr = mapId != null ? mapId.name() : null;
		mapper.map(source, dest, mapIdStr);
	}

	protected <T> T mapNotNull(Object source, Class<T> clazz) {
		if (source != null)
			return map(source, clazz);
		return null;
	}

	protected void mapNotNull(Object source, Object dest) {
		if (source != null)
			map(source, dest);
	}

	protected <T> T mapNotNull(Object source, Class<T> clazz, MapId mapId) {
		if (source != null)
			return map(source, clazz, mapId);
		return null;
	}

	protected void mapNotNull(Object source, Object dest, MapId mapId) {
		if (source != null)
			map(source, dest, mapId);
	}

	protected <A, DA> List<A> convertiLista(Iterable<DA> listDa, Class<A> classA) {
		return convertiLista(listDa, classA, null);
	}

	protected <A, DA> List<A> convertiLista(Iterable<DA> listDa, Class<A> classA,
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

	protected Pageable toPageable(ParametriPaginazione pp) {
		Pageable pageable = new PageRequest(pp.getNumeroPagina(),
				pp.getElementiPerPagina());
		return pageable;
	}

	protected Pageable toPageable(ParametriPaginazione pp, Sort sort) {
		Pageable pageable = new PageRequest(pp.getNumeroPagina(),
				pp.getElementiPerPagina(), sort);
		return pageable;
	}

	protected ParametriPaginazione toParametriPaginazione(Pageable pageable) {
		ParametriPaginazione pp = new ParametriPaginazione();
		pp.setNumeroPagina(pageable.getPageNumber());
		pp.setElementiPerPagina(pageable.getPageSize());
		return pp;
	}

	/**
	 * Trasforma una lista di Page<E> in una lista ListaPaginata<T>
	 * 
	 * Dove E &egrave; il tipo della classe entity e T &egrave; il tipo della classe di Model.
	 * 
	 * @author Domenico Lisi
	 * 
	 * @param pagedList
	 * @param classDest
	 * @param mapId
	 * @return
	 */
	protected <T, E> ListaPaginata<T> toListaPaginata(Page<E> pagedList,
			Class<T> classDest, MapId mapId) {
		final String methodName = "toListaPaginata";
		ListaPaginataImpl<T> list = new ListaPaginataImpl<T>();

		if (!pagedList.hasContent())
			return list;

		int elementsPerPage = 1 + (int) (pagedList.getTotalElements() / pagedList
				.getTotalPages());

		list.setPaginaCorrente(pagedList.getNumber());
		list.setTotaleElementi((int) pagedList.getTotalElements());
		list.setTotalePagine(pagedList.getTotalPages());
		list.setHasPaginaPrecedente(pagedList.hasPreviousPage());
		list.setHasPaginaSuccessiva(pagedList.hasNextPage());
		list.setNumeroElementoInizio(1 + pagedList.getNumber()
				* elementsPerPage);
		list.setNumeroElementoFine(pagedList.getNumber() * elementsPerPage
				+ pagedList.getNumberOfElements());

		for (E dto : pagedList.getContent())
			list.add(map(dto, classDest, mapId));

		log.debug(methodName, "PaginaCorrente: "
				+ list.getPaginaCorrente() + " TotaleElementi: "
				+ list.getTotaleElementi() + " TotalePagine: "
				+ list.getTotalePagine());

		return list;

	}

	protected <T, E> ListaPaginata<T> toListaPaginata(Page<E> pagedList,
			Class<T> classDest) {
		return toListaPaginata(pagedList, classDest, null);
	}

	protected <T> ListaPaginata<T> toListaPaginata(Page<T> list) {
		// list.getTotalElements(); //Returns the total amount of elements.
		// list.getTotalPages(); //Returns the number of total pages.
		return toListaPaginata(list.getContent(), list.getTotalElements(),
				list.getTotalPages(), list.getNumber());
	}

	protected <T> ListaPaginata<T> toListaPaginata(List<T> simpleList,
			Long totaleElementi, ParametriPaginazione pp) {
		int totalePagine = (int) (totaleElementi / pp.getElementiPerPagina());
		return toListaPaginata(simpleList, totaleElementi, totalePagine,
				pp.getNumeroPagina());
	}

	protected <T> ListaPaginata<T> toListaPaginata(List<T> simpleList,
			Long totaleElementi, int totalePagine, int paginaCorrente) {
		ListaPaginataImpl<T> result = new ListaPaginataImpl<T>(simpleList);
		/*
		 * for (T dto : simpleList){ result.add(dto); }
		 */

		result.setTotaleElementi(totaleElementi.intValue()); // TODO
																// ListaPaginata
																// DEVE avere
																// Long
																// anziche'
																// int!!
		result.setTotalePagine(totalePagine);
		result.setPaginaCorrente(paginaCorrente);

		return result;
	}

	protected String mapToString(Integer i) {
		return mapToString(i, null);
	}

	protected String mapToString(Integer i, String nullValue) {
		if (i == null)
			return nullValue;

		return i.toString();
	}

	protected String mapToString(boolean flag) {
		return flag ? "S" : "N";
	}

	/**
	 * Ottiene un uid a partire da una Entita solo se l'uid &egrave; diverso da 0.
	 * 
	 * @param entita
	 * @return
	 */
	protected Integer mapToUidIfNotZero(Entita entita) {
		return entita != null && entita.getUid() != 0 ? entita.getUid() : null;
	}

}
