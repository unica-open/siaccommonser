/*
*SPDX-FileCopyrightText: Copyright 2020 | CSI Piemonte
*SPDX-License-Identifier: EUPL-1.2
*/
package it.csi.siac.siaccommonser.integration.dao.base.postgresql.infoschema;

import java.util.List;

import it.csi.siac.siaccommonser.integration.dao.base.Dao;
import it.csi.siac.siaccommonser.integration.entity.postgresql.infoschema.InformationSchemaColumnConstraint;
import it.csi.siac.siaccommonser.integration.entity.postgresql.infoschema.InformationSchemaTable;

public interface InformationSchemaDao extends Dao<InformationSchemaTable, String> {
	List<InformationSchemaTable> getTables(InformationSchemaTable filter);

	InformationSchemaTable getTable(InformationSchemaTable filter);

	List<InformationSchemaColumnConstraint> getColumnConstraintsOnTable(String tableName, String columnName);

}

