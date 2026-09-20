package com.repetovskyi.websecurity.service;

/*
  @author   Repetovskyi V.
  @project   web-security
  @class  DeviceService
  @version  1.0.0 
  @since 9/12/2026 - 19.07
*/

import com.repetovskyi.websecurity.model.Device;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class DeviceService {

    private final Map<String, Device> devices = new LinkedHashMap<>();

    public DeviceService() {
        seed("1", "router-01", "router", "it-department");
        seed("2", "printer-02", "printer", "accounting");
        seed("3", "laptop-03", "laptop", "r.vladyslav");
    }

    private void seed(String id, String name, String type, String owner) {
        Device device = Device.builder()
                .id(id)
                .name(name)
                .type(type)
                .owner(owner)
                .build();
        devices.put(device.getId(), device);
    }

    public List<Device> findAll() {
        return new ArrayList<>(devices.values());
    }

    public Optional<Device> findById(String id) {
        return Optional.ofNullable(devices.get(id));
    }

    public Device create(Device device) {
        device.setId(UUID.randomUUID().toString());
        devices.put(device.getId(), device);
        return device;
    }

    public Optional<Device> update(String id, Device device) {
        if (!devices.containsKey(id)) {
            return Optional.empty();
        }
        device.setId(id);
        devices.put(id, device);
        return Optional.of(device);
    }

    public boolean delete(String id) {
        return devices.remove(id) != null;
    }
}
