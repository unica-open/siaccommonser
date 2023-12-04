/*
*SPDX-FileCopyrightText: Copyright 2020 | CSI Piemonte
*SPDX-License-Identifier: EUPL-1.2
*/
package it.csi.siac.siaccommonser.integration.entity.postgresql.infoschema;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SecondaryTable;
import javax.persistence.Table;

@Entity
@Table(name = "constraint_column_usage", schema = "information_schema")
@SecondaryTable(name = "table_constraints", schema = "information_schema", pkJoinColumns = { @PrimaryKeyJoinColumn(name = "constraint_name") })
public class InformationSchemaColumnConstraint {
	@Column(name = "table_schema")
	private String schema;

	@Id
	@Column(name = "constraint_name")
	private String name;

	@ManyToOne
	@JoinColumn(name = "table_name")
	private InformationSchemaTable referencedTable;

	@ManyToOne
	@JoinColumn(name = "column_name")
	private InformationSchemaColumn column;

	@Column(table = "table_constraints", name = "constraint_type")
	private String type;

	@Column(table = "table_constraints", name = "table_name")
	private String referencingTable;

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

	public InformationSchemaColumn getColumn() {
		return column;
	}

	public void setColumn(InformationSchemaColumn column) {
		this.column = column;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public InformationSchemaTable getReferencedTable() {
		return referencedTable;
	}

	public void setReferencedTable(InformationSchemaTable referencedTable) {
		this.referencedTable = referencedTable;
	}

	public String getReferencingTable() {
		return referencingTable;
	}

	public void setReferencingTable(String referencingTable) {
		this.referencingTable = referencingTable;
	}

	public boolean isPrimaryKey() {
		return  "PRIMARY KEY".equals(type);
	}

	public boolean isForeignKey() {
		return "FOREIGN KEY".equals(type);
	}
	
}