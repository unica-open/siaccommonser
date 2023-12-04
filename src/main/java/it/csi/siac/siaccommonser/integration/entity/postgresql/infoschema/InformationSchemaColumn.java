/*
*SPDX-FileCopyrightText: Copyright 2020 | CSI Piemonte
*SPDX-License-Identifier: EUPL-1.2
*/
package it.csi.siac.siaccommonser.integration.entity.postgresql.infoschema;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "columns", schema = "information_schema")
public class InformationSchemaColumn {
	@Column(name = "table_schema")
	private String schema;

	@Id
	@Column(name = "column_name")
	private String name;

	@ManyToOne
	@JoinColumn(name = "table_name")
	private InformationSchemaTable table;

	@OneToMany(mappedBy = "column")
	private Set<InformationSchemaColumnConstraint> constraints;

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

	public InformationSchemaTable getTable() {
		return table;
	}

	public void setTable(InformationSchemaTable table) {
		this.table = table;
	}

	public Set<InformationSchemaColumnConstraint> getConstraints() {
		return constraints;
	}

	public void setConstraints(Set<InformationSchemaColumnConstraint> constraints) {
		this.constraints = constraints;
	}
}