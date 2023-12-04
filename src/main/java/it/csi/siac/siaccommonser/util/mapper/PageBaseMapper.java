/*
*SPDX-FileCopyrightText: Copyright 2020 | CSI Piemonte
*SPDX-License-Identifier: EUPL-1.2
*/


package it.csi.siac.siaccommonser.util.mapper;


import org.springframework.data.domain.Page;

import it.csi.siac.siaccommon.util.mapper.BaseMapper;
import it.csi.siac.siaccommon.util.mapper.MapperDecorator;
import it.csi.siac.siaccorser.model.paginazione.ListaPaginata;
import it.csi.siac.siaccorser.model.paginazione.ListaPaginataImpl;

public abstract class PageBaseMapper<A, B> extends BaseMapper<A, B> {

	@SafeVarargs
	public final ListaPaginata<B> map(Page<A> pagedList, MapperDecorator<A, B>... decorators) {
		if (pagedList == null) {
			return null;
		}
		
		ListaPaginataImpl<B> listaPaginata = buildListaPaginata(pagedList);

		for (A a : pagedList) {
			listaPaginata.add(map(a, decorators));
		}
		
		return listaPaginata;
	}

	private ListaPaginataImpl<B> buildListaPaginata(Page<A> pagedList) {
		ListaPaginataImpl<B> listaPaginata = new ListaPaginataImpl<B>();
		
		if (!pagedList.hasContent()){
			return listaPaginata;
		}

		int elementsPerPage = 1 + (int) (pagedList.getTotalElements() / pagedList
				.getTotalPages());

		listaPaginata.setPaginaCorrente(pagedList.getNumber());
		listaPaginata.setTotaleElementi((int) pagedList.getTotalElements());
		listaPaginata.setTotalePagine(pagedList.getTotalPages());
		listaPaginata.setHasPaginaPrecedente(pagedList.hasPreviousPage());
		listaPaginata.setHasPaginaSuccessiva(pagedList.hasNextPage());
		listaPaginata.setNumeroElementoInizio(1 + pagedList.getNumber()
				* elementsPerPage);
		listaPaginata.setNumeroElementoFine(pagedList.getNumber() * elementsPerPage
				+ pagedList.getNumberOfElements());
		
		return listaPaginata;
	}

}
