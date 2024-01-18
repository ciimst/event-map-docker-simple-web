/*
 * [The "BSD licence"]
 * Copyright (c) 2013-2015 Dandelion
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. Neither the name of Dandelion nor the names of its contributors 
 * may be used to endorse or promote products derived from this software 
 * without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.imst.event.map.web.datatables.ajax;

import java.io.Serializable;

import com.imst.event.map.web.datatables.vo.Search;


/**
 * <p>
 * A column definition, containing the different information used when
 * server-side processing is enabled.
 * 
 * @author Thibault Duchateau
 * @since 0.8.2
 */
public class ColumnDef implements Serializable {

   private static final long serialVersionUID = 6349611254914115218L;
   private boolean sortable;
   private boolean sorted = false;
   private Boolean searchable;
   private boolean filtered;
   private String regex;
//   private String search;
   private String searchFrom;
   private String searchTo;
   private SortDirection sortDirection;
   
   private String data;
   private String name;
//   private Boolean orderable;
   private Search search;

   public enum SortDirection {//ASC, DESC
      asc,desc,
      ASC,DESC
   }

   public SortDirection getSortDirection() {
      return sortDirection;
   }

   public void setSortDirection(SortDirection sortDirection) {
      this.sortDirection = sortDirection;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }
   
   public String getData() {
	   return data;
   }
   
   public void setData(String data) {
	   this.data = data;
   }

   public boolean isSortable() {
      return sortable;
   }

   public void setSortable(boolean sortable) {
      this.sortable = sortable;
   }

   /**
    * @return {@code true} if the column is searchable, {@code false} otherwise.
    * @deprecated Use {@link #isSearchable()} instead.
    */
   public boolean isFilterable() {
      return searchable;
   }

   public void setSearchable(boolean searchable) {
      this.searchable = searchable;
   }

   /**
    * @return {@code true} if the column is searchable, {@code false} otherwise.
    */
   public boolean isSearchable() {
      return searchable;
   }

   public String getRegex() {
      return regex;
   }

   public void setRegex(String regex) {
      this.regex = regex;
   }

   public Search getSearch() {
      return search;
   }

   public void setSearch(Search search) {
      this.search = search;
   }

   public String getSearchFrom() {
      return searchFrom;
   }

   public void setSearchFrom(String searchFrom) {
      this.searchFrom = searchFrom;
   }

   public String getSearchTo() {
      return searchTo;
   }

   public void setSearchTo(String searchTo) {
      this.searchTo = searchTo;
   }

   public boolean isSorted() {
      return sorted;
   }

   public void setSorted(boolean sorted) {
      this.sorted = sorted;
   }

   public boolean isFiltered() {
      return filtered;
   }

   public void setFiltered(boolean filtered) {
      this.filtered = filtered;
   }

   @Override
   public String toString() {
      return "ColumnDef [name=" + name + ", sortable=" + sortable + ", sorted=" + sorted + ", searchable=" + searchable
            + ", filtered=" + filtered + ", regex=" + regex + ", search=" + search + ", searchFrom=" + searchFrom
            + ", searchTo=" + searchTo + ", sortDirection=" + sortDirection + "]";
   }
}