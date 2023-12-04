/*
*SPDX-FileCopyrightText: Copyright 2020 | CSI Piemonte
*SPDX-License-Identifier: EUPL-1.2
*/
package it.csi.siac.siaccommonser.integration.entity.postgresql.infoschema;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "tables", schema = "information_schema")
public class InformationSchemaTable {
	@Column(name = "table_schema")
	private String schema;

	@Id
	@Column(name = "table_name")
	private String name;

	@OneToMany(mappedBy = "table")
	private Set<InformationSchemaColumn> columns;


	public InformationSchemaTable(String schema, String name) {
		this();
		this.schema = schema;
		this.name = name;
	}

	public InformationSchemaTable() {
		super();
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<InformationSchemaColumn> getColumns() {
		return columns;
	}

	public void setColumns(Set<InformationSchemaColumn> columns) {
		this.columns = columns;
	}

	

}