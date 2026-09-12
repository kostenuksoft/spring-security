package com.repetovskyi.websecurity.model;

/*
  @author   Repetovskyi V.
  @project   web-security
  @class  Device
  @version  1.0.0 
  @since 9/12/2026 - 19.03
*/

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Device {

    private String id;
    private String name;
    private String type;
    private String owner;
}
