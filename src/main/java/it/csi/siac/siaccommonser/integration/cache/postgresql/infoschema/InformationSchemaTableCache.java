/*
*SPDX-FileCopyrightText: Copyright 2020 | CSI Piemonte
*SPDX-License-Identifier: EUPL-1.2
*/
package it.csi.siac.siaccommonser.integration.cache.postgresql.infoschema;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import it.csi.siac.siaccommonser.integration.dao.base.postgresql.infoschema.InformationSchemaDao;
import it.csi.siac.siaccommonser.integration.entity.postgresql.infoschema.InformationSchemaColumn;
import it.csi.siac.siaccommonser.integration.entity.postgresql.infoschema.InformationSchemaColumnConstraint;
import it.csi.siac.siaccommonser.integration.entity.postgresql.infoschema.InformationSchemaTable;

@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
@Transactional(propagation = Propagation.SUPPORTS)
public class InformationSchemaTableCache {
	@Autowired
	private InformationSchemaDao informationSchemaDao;

	private final Map<String, InformationSchemaTable> tableCache = new HashMap<String, InformationSchemaTable>();
	private final Map<String, List<InformationSchemaTable>> tableListCache = new HashMap<String, List<InformationSchemaTable>>();
	private final Map<String, List<InformationSchemaColumnConstraint>> columnConstraintsOnTableCache = new HashMap<String, List<InformationSchemaColumnConstraint>>();

	public InformationSchemaTable getTable(InformationSchemaTable filter) {
		String key = String.format("%s/%s", filter.getSchema(), filter.getName());

		if (!tableCache.containsKey(key))
			tableCache.put(key, informationSchemaDao.getTable(filter));

		return tableCache.get(key);
	}

	public List<InformationSchemaTable> getTables(InformationSchemaTable filter) {
		String key = String.format("%s/%s", filter.getSchema(), filter.getName());

		if (!tableListCache.containsKey(key))
			tableListCache.put(key, informationSchemaDao.getTables(filter));

		return tableListCache.get(key);
	}

	public List<InformationSchemaColumnConstraint> getColumnConstraintsOnTable(String tableName,
			String columnName) {
		String key = String.format("%s/%s", tableName, columnName);

		if (!columnConstraintsOnTableCache.containsKey(key))
			columnConstraintsOnTableCache.put(key,
					informationSchemaDao.getColumnConstraintsOnTable(tableName, columnName));

		return columnConstraintsOnTableCache.get(key);
	}

	public List<InformationSchemaColumnConstraint> getColumnConstraintsOnTable(
			InformationSchemaColumn column) {
		return getColumnConstraintsOnTable(column.getTable().getName(), column.getName());
	}
}
