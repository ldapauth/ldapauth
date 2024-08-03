/*
 * Copyright (c) 2022, ldapauth and/or its affiliates. All rights reserved.
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * Please contact ldapauth, visit www.ldapauth.com if you need additional
 * information or have any questions.
 */

package com.ldapauth.entity;

import java.util.ArrayList;
import java.util.HashMap;

public class DbTableMetaData {
	String tableName;

	ArrayList<DbTableColumn> columns = new ArrayList<DbTableColumn>();

	HashMap<String,DbTableColumn> columnsMap = new HashMap<String,DbTableColumn>();

	public DbTableMetaData(String tableName) {
		super();
		this.tableName = tableName;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public ArrayList<DbTableColumn> getColumns() {
		return columns;
	}

	public void setColumns(ArrayList<DbTableColumn> columns) {
		this.columns = columns;
	}

	public HashMap<String, DbTableColumn> getColumnsMap() {
		return columnsMap;
	}

	public void setColumnsMap(HashMap<String, DbTableColumn> columnsMap) {
		this.columnsMap = columnsMap;
	}


}
