package com.repetovskyi.websecurity.controller;

/*
  @author   Repetovskyi V.
  @project   web-security
  @class  DeviceReportController
  @version  1.1.0
  @since 10/3/2026 - 09.03
*/

import com.repetovskyi.websecurity.model.Device;
import com.repetovskyi.websecurity.service.DeviceService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class DeviceReportController {

    private final DeviceService deviceService;

    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'SUPERADMIN')")
    @GetMapping("/summary")
    public Map<String, Long> summary() {
        return deviceService.findAll().stream()
                .collect(Collectors.groupingBy(Device::getType, TreeMap::new, Collectors.counting()));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    @GetMapping("/owners")
    public Map<String, List<String>> owners() {
        return deviceService.findAll().stream()
                .collect(Collectors.groupingBy(Device::getOwner, TreeMap::new,
                        Collectors.mapping(Device::getName, Collectors.toList())));
    }

    @PreAuthorize("hasRole('SUPERADMIN')")
    @GetMapping("/audit")
    public List<AuditEntry> audit() {
        return deviceService.findAll().stream()
                .map(AuditEntry::of)
                .toList();
    }

    public record AuditEntry(
            String id,
            String name,
            String createdBy,
            LocalDateTime createdDate,
            String lastModifiedBy,
            LocalDateTime lastModifiedDate,
            Long version) {

        static AuditEntry of(Device device) {
            return new AuditEntry(
                    device.getId(),
                    device.getName(),
                    device.getCreatedBy(),
                    device.getCreatedDate(),
                    device.getLastModifiedBy(),
                    device.getLastModifiedDate(),
                    device.getVersion());
        }
    }
}
