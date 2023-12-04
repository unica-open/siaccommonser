/*
*SPDX-FileCopyrightText: Copyright 2020 | CSI Piemonte
*SPDX-License-Identifier: EUPL-1.2
*/
package it.csi.siac.siaccommonser.integration.dao.base.postgresql.infoschema;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import it.csi.siac.siaccommonser.integration.dao.base.JpaDao;
import it.csi.siac.siaccommonser.integration.entity.postgresql.infoschema.InformationSchemaColumnConstraint;
import it.csi.siac.siaccommonser.integration.entity.postgresql.infoschema.InformationSchemaTable;

@Component
@Transactional
public class InformationSchemaDaoImpl extends JpaDao<InformationSchemaTable, String> implements
		InformationSchemaDao {
	@Override
	public List<InformationSchemaTable> getTables(InformationSchemaTable filter) {
		Map<String, Object> parameters = new HashMap<String, Object>();

		parameters.put("schema", filter.getSchema());
		parameters.put("name", filter.getName());

		@SuppressWarnings("unchecked")
		List<InformationSchemaTable> listInformationSchemaTables = createQuery(
				"SELECT h FROM InformationSchemaTable h where UPPER(schema) LIKE UPPER(:schema) and UPPER(name) LIKE UPPER(:name)",
				parameters).getResultList();

		return listInformationSchemaTables;
	}

	@Override
	public InformationSchemaTable getTable(InformationSchemaTable filter) {
		return getTables(filter).get(0);
	}

	@Override
	public List<InformationSchemaColumnConstraint> getColumnConstraintsOnTable(
			String tableName, String columnName) {
		Map<String, Object> parameters = new HashMap<String, Object>();

		parameters.put("column", columnName);
		parameters.put("tableName", tableName);

		@SuppressWarnings("unchecked")
		List<InformationSchemaColumnConstraint> listColumnConstraints = createQuery(
				"SELECT h FROM InformationSchemaColumnConstraint h " +
				" where referencingTable=:tableName AND column=:column",
				parameters).getResultList();

		return listColumnConstraints;

	}

}
