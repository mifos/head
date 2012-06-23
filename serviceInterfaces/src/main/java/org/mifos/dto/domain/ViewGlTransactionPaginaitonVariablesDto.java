/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

package org.mifos.dto.domain;

public class ViewGlTransactionPaginaitonVariablesDto {

	private int cPage;
	private int cPageNo;
	private int noOfPagesIndex;
	private int iPageNo;
	private int prePageNo;
	private int noOfRecordsPerPage;
	private int iTotalPages;
	private int startRecordCurrentPage;
	private int endRecordCurrentPage;
	private int totalNoOfRowsForPagination;
	private int i;

	public int getI() {
		return i;
	}

	public void setI(int i) {
		this.i = i;
	}

	public int getPrePageNo() {
		return prePageNo;
	}

	public void setPrePageNo(int prePageNo) {
		this.prePageNo = prePageNo;
	}

	public int getcPage() {
		return cPage;
	}

	public void setcPage(int cPage) {
		this.cPage = cPage;
	}

	public int getcPageNo() {
		return cPageNo;
	}

	public void setcPageNo(int cPageNo) {
		this.cPageNo = cPageNo;
	}

	public int getNoOfPagesIndex() {
		return noOfPagesIndex;
	}

	public void setNoOfPagesIndex(int noOfPagesIndex) {
		this.noOfPagesIndex = noOfPagesIndex;
	}

	public int getiPageNo() {
		return iPageNo;
	}

	public void setiPageNo(int iPageNo) {
		this.iPageNo = iPageNo;
	}

	public int getNoOfRecordsPerPage() {
		return noOfRecordsPerPage;
	}

	public void setNoOfRecordsPerPage(int noOfRecordsPerPage) {
		this.noOfRecordsPerPage = noOfRecordsPerPage;
	}

	public int getiTotalPages() {
		return iTotalPages;
	}

	public void setiTotalPages(int iTotalPages) {
		this.iTotalPages = iTotalPages;
	}

	public int getStartRecordCurrentPage() {
		return startRecordCurrentPage;
	}

	public void setStartRecordCurrentPage(int startRecordCurrentPage) {
		this.startRecordCurrentPage = startRecordCurrentPage;
	}

	public int getEndRecordCurrentPage() {
		return endRecordCurrentPage;
	}

	public void setEndRecordCurrentPage(int endRecordCurrentPage) {
		this.endRecordCurrentPage = endRecordCurrentPage;
	}

	public int getTotalNoOfRowsForPagination() {
		return totalNoOfRowsForPagination;
	}

	public void setTotalNoOfRowsForPagination(int totalNoOfRowsForPagination) {
		this.totalNoOfRowsForPagination = totalNoOfRowsForPagination;
	}

}