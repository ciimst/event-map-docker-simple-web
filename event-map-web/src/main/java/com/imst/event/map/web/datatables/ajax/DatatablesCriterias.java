/*
 * [The "BSD licence"]
 * Copyright (c) 2012 Dandelion
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

import java.beans.FeatureDescriptor;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.imst.event.map.web.datatables.EntitySortKey;
import com.imst.event.map.web.datatables.util.StringUtils;
import com.imst.event.map.web.datatables.vo.Direction;
import com.imst.event.map.web.datatables.vo.Search;
import com.imst.event.map.web.utils.ApplicationContextUtils;


/**
 * <p>
 * POJO that wraps all the parameters sent by Datatables to the server when
 * server-side processing is enabled. This bean can then be used to build SQL
 * queries.
 * </p>
 * 
 * @author Thibault Duchateau
 * @since 0.8.2
 */
public class DatatablesCriterias implements Serializable {

   private static final long serialVersionUID = 8661357461501153387L;

//   private static final Logger LOG = LoggerFactory.getLogger(DatatablesCriterias.class);

   private static Pattern pattern = Pattern.compile("columns\\[([0-9]*)?\\]");
   private final Search search;
   private final Integer start;
   private final Integer length;
   private final List<ColumnDef> columns;
   private List<Order> order;
   private final Integer draw;
   private final String refresh;
   private final Timestamp pageRefreshDate;

   private DatatablesCriterias(Search search, Integer displayStart, Integer displaySize, List<ColumnDef> columns,
							   List<Order> order, Integer draw, String refresh, Timestamp pageRefreshDate) {
      this.search = search;
      this.start = displayStart;
      this.length = displaySize;
      this.columns = columns;
      this.order = order;
      this.draw = draw;
      this.refresh = refresh;
      this.pageRefreshDate = pageRefreshDate;
   }

   public Integer getStart() {
      return start;
   }

   public Integer getLength() {
      return length;
   }

   public Search getSearch() {
      return search;
   }

   public Integer getDraw() {
      return draw;
   }
   
   public String getRefresh() {
	   return refresh;
   }
   
   public Timestamp getPageRefreshDate() {
	   return pageRefreshDate;
   }

   public List<ColumnDef> getColumns() {
      return columns;
   }

	public List<Order> getOrder() {
		return order;
	}
	
	public void setOrder(List<Order> order) {
		this.order = order;
	}

   /**
    * @return {@code true} if one the columns is searchable, {@code false}
    *         otherwise.
    * @deprecated Use {@link #hasOneSearchableColumn()} instead.
    */
   public Boolean hasOneFilterableColumn() {
      return hasOneSearchableColumn();
   }

   /**
    * @return {@code true} if one the columns is searchable, {@code false}
    *         otherwise.
    */
   public Boolean hasOneSearchableColumn() {
      for (ColumnDef columnDef : this.columns) {
         if (columnDef.isSearchable()) {
            return true;
         }
      }
      return false;
   }

   /**
    * @return true if a column is being filtered, false otherwise.
    */
   public Boolean hasOneFilteredColumn() {
      for (ColumnDef columnDef : this.columns) {
         if ((columnDef.getSearch() != null && StringUtils.isNotBlank(columnDef.getSearch().getValue()))
        		 || StringUtils.isNotBlank(columnDef.getSearchFrom())
        		 || StringUtils.isNotBlank(columnDef.getSearchTo())) {
            return true;
         }
      }
      return false;
   }



   /**
    * <p>
    * Map all request parameters into a wrapper POJO that eases SQL querying.
    * </p>
    * 
    * @param request
    *           The request sent by Datatables containing all parameters.
    * @return a wrapper POJO.
    */
//   public static DatatablesCriterias getFromRequest(HttpServletRequest request) {
//
//      Validate.notNull(request, "The HTTP request cannot be null");
//
//      int columnNumber = getColumnNumber(request);
//      LOG.trace("Number of columns: {}", columnNumber);
//
//      String paramSearch = request.getParameter(DTConstants.DT_S_SEARCH);
//      Search paramSearchItem = new Search();
//      paramSearchItem.setValue(paramSearch);;
//      
//      
//      String paramDraw = request.getParameter(DTConstants.DT_I_DRAW);
//      String paramStart = request.getParameter(DTConstants.DT_I_START);
//      String paramLength = request.getParameter(DTConstants.DT_I_LENGTH);
//
//      Integer draw = StringUtils.isNotBlank(paramDraw) ? Integer.parseInt(paramDraw) : -1;
//      Integer start = StringUtils.isNotBlank(paramStart) ? Integer.parseInt(paramStart) : -1;
//      Integer length = StringUtils.isNotBlank(paramLength) ? Integer.parseInt(paramLength) : -1;
//
//      // Column definitions
//      List<ColumnDef> columnDefs = new ArrayList<ColumnDef>();
//
//      for (int i = 0; i < columnNumber; i++) {
//
//         ColumnDef columnDef = new ColumnDef();
//
//         columnDef.setName(request.getParameter("columns[" + i + "][data]"));
//         columnDef.setSearchable(Boolean.parseBoolean(request.getParameter("columns[" + i + "][searchable]")));
//         columnDef.setSortable(Boolean.parseBoolean(request.getParameter("columns[" + i + "][orderable]")));
//         columnDef.setRegex(request.getParameter("columns[" + i + "][search][regex]"));
//
//         String searchTerm = request.getParameter("columns[" + i + "][search][value]");
//
//         if (StringUtils.isNotBlank(searchTerm)) {
//            columnDef.setFiltered(true);
//            String[] splittedSearch = searchTerm.split("~");
//            if ("~".equals(searchTerm)) {
//            	Search searchItem = new Search();
//            	searchItem.setValue("");
//               columnDef.setSearch(searchItem);
//            }
//            else if (searchTerm.startsWith("~")) {
//               columnDef.setSearchTo(splittedSearch[1]);
//            }
//            else if (searchTerm.endsWith("~")) {
//               columnDef.setSearchFrom(splittedSearch[0]);
//            }
//            else if (searchTerm.contains("~")) {
//               columnDef.setSearchFrom(splittedSearch[0]);
//               columnDef.setSearchTo(splittedSearch[1]);
//            }
//            else {
//            	Search searchItem = new Search();
//            	searchItem.setValue(searchTerm);
//               columnDef.setSearch(searchItem);
//            }
//         }
//
//         for (int j = 0; j < columnNumber; j++) {
//            String ordered = request.getParameter("order[" + j + "][column]");
//            if (ordered != null && ordered.equals(String.valueOf(i))) {
//               columnDef.setSorted(true);
//               break;
//            }
//         }
//
//         columnDefs.add(columnDef);
//      }
//
//      // Sorted column definitions
//      List<Order> sortedColumnDefs = new LinkedList<Order>();
//
//      for (int i = 0; i < columnNumber; i++) {
//         String paramSortedCol = request.getParameter("order[" + i + "][column]");
//
//         // The column is being sorted
//         if (StringUtils.isNotBlank(paramSortedCol)) {
//            Integer sortedCol = Integer.parseInt(paramSortedCol);
//            ColumnDef sortedColumnDef = columnDefs.get(sortedCol);
//            String sortedColDirection = request.getParameter("order[" + i + "][dir]");
//            if (StringUtils.isNotBlank(sortedColDirection)) {
//               sortedColumnDef.setSortDirection(ColumnDef.SortDirection.valueOf(sortedColDirection.toUpperCase()));
//            }
//
//            sortedColumnDefs.add(sortedColumnDef);
//         }
//      }
//
//      return new DatatablesCriterias(paramSearchItem, start, length, columnDefs, sortedColumnDefs, draw);
//   }

   private static int getColumnNumber(HttpServletRequest request) {

      int columnNumber = 0;
      for (Enumeration<String> e = request.getParameterNames(); e.hasMoreElements();) {
         String param = e.nextElement();
         Matcher matcher = pattern.matcher(param);
         while (matcher.find()) {
            Integer col = Integer.parseInt(matcher.group(1));
            if (col > columnNumber) {
               columnNumber = col;
            }
         }
      }

      if (columnNumber != 0) {
         columnNumber++;
      }
      return columnNumber;
   }

   @Override
   public String toString() {
      return "DatatablesCriterias [search=" + search + ", start=" + start + ", length=" + length + ", columnDefs="
            + columns + ", order=" + order + ", draw=" + draw + "]";
   }
   
//   public PageRequest getPageRequest(Class<?> tClass) {
//   
//      Map<String, String> sortMap = getSortMap(tClass);
//      
//      Sort.Direction direction = Sort.Direction.ASC;
//      List<ColumnDef> sortedColumnDefs = this.getOrder();
//      Sort sort = Sort.by(direction, "id");
//      if (sortedColumnDefs != null) {
//   
//         List<Sort.Order> orderList = new ArrayList<>();
//         for (ColumnDef columnDef : sortedColumnDefs) {
//   
//            String sortKey = sortMap.get(columnDef.getName());
//            if (sortKey == null) {
//               continue;
//            }
//            ColumnDef.SortDirection sortDirection = columnDef.getSortDirection();
//            direction = sortDirection.equals(ColumnDef.SortDirection.ASC) ? Sort.Direction.ASC : Sort.Direction.DESC;
//   
//            Sort.Order order = new Sort.Order(direction, sortKey);
//            orderList.add(order);
//         }
//       
//         sort = Sort.by(orderList);
//      }
//   
//      int page = this.getStart() / ApplicationContextUtils.getTableLength(this);
//      int size = ApplicationContextUtils.getTableLength(this);
//            
//      return PageRequest.of(page, size, sort);
//   }
   
   public Map<String, String> getSortMap(Class<?> tClass) {
      
      Map<String, String> collect;
   
      PropertyDescriptor[] propertyDescriptors = BeanUtils.getPropertyDescriptors(tClass);
      collect = Arrays.stream(propertyDescriptors)
              .filter(descriptor -> {
   
                 boolean test = !descriptor.getName().equals("class");
                 EntitySortKey annotation = null;
                 try {
                    Field declaredField = tClass.getDeclaredField(descriptor.getName());
                    annotation = declaredField.getAnnotation(EntitySortKey.class);
      
                 } catch (NoSuchFieldException ignore) {}
   
                 if (annotation == null) {
                    Method readMethod = descriptor.getReadMethod();
                    annotation = readMethod.getAnnotation(EntitySortKey.class);
                 }
   
                 if (Objects.nonNull(annotation)) {
                    
                    test &= annotation.sortable();
                 }
                 
                 return test;
              })
              .collect(Collectors.toMap(FeatureDescriptor::getName, descriptor -> {
                 String name = descriptor.getName();
                 EntitySortKey annotation = null;
                 try {
                    Field declaredField = tClass.getDeclaredField(name);
                    annotation = declaredField.getAnnotation(EntitySortKey.class);
                  
                 } catch (NoSuchFieldException ignore) {}
              
                 if (annotation == null) {
                    Method readMethod = descriptor.getReadMethod();
                    annotation = readMethod.getAnnotation(EntitySortKey.class);
                 }
   
                 if (Objects.nonNull(annotation) && annotation.sortable()) {
                    name = annotation.value();
                 }
                 
                 return name;
              }));
      
      return collect;
   
   }
   
   
   public PageRequest getPageRequest(Class<?> tClass) {
	   
	      Map<String, String> sortMap = getSortMap(tClass);
	      
	      Sort.Direction direction = Sort.Direction.ASC;
	      List<Order> sortedColumnDefs = this.getOrder();
	      Sort sort = Sort.by(direction, "id");
	      if (sortedColumnDefs != null) {
	   
	         List<Sort.Order> orderList = new ArrayList<>();
	         for (Order order : sortedColumnDefs) {
	   
	            String sortKey = sortMap.get(order.getColumn());
	            if (sortKey == null) {
	               continue;
	            }
	            Direction sortDirection = order.getDir();
	            direction = sortDirection.name().equalsIgnoreCase(ColumnDef.SortDirection.asc.name()) ? Sort.Direction.ASC : Sort.Direction.DESC;	   
	            
	            Sort.Order order2 = new Sort.Order(direction, sortKey);
	            orderList.add(order2);
	         }
	       
	         sort = Sort.by(orderList);
	      }
	   
	      int page = this.getStart() / ApplicationContextUtils.getTableLength(this);
	      int size = ApplicationContextUtils.getTableLength(this);
	            
	      return PageRequest.of(page, size, sort);
	   }
   
}