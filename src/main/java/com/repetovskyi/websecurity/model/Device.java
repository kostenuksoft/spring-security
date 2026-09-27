package com.repetovskyi.websecurity.model;

/*
  @author   Repetovskyi V.
  @project   web-security
  @class  Device
  @version  1.1.0
  @since 9/12/2026 - 19.03
*/

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@EqualsAndHashCode(of = "id", callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Device extends Auditable {

    @Id
    private String id;
    private String name;
    private String type;
    private String owner;
}
