package com.imst.event.map.web.datatables.ajax;

import com.imst.event.map.web.datatables.vo.Direction;

import groovy.transform.EqualsAndHashCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Order {

    private String column;
    private Direction dir;

}
