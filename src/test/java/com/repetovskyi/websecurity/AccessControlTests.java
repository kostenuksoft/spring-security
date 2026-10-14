package com.repetovskyi.websecurity;

/*
  @author   Repetovskyi V.
  @project   web-security
  @class  AccessControlTests
  @version  1.0.0 
  @since 10/14/2026 - 20.20
*/

import com.jayway.jsonpath.JsonPath;
import com.repetovskyi.websecurity.model.Device;
import com.repetovskyi.websecurity.repository.DeviceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@SpringBootTest
@ActiveProfiles("test")
class AccessControlTests {

    private static final String DEVICES = "/api/v1/devices";
    private static final String REPORTS = "/api/v1/reports";

    private static final String NEW_DEVICE = """
            {"name": "switch-04", "type": "switch", "owner": "it-department"}
            """;

    private static final String UPDATED_DEVICE = """
            {"name": "printer-02", "type": "printer", "owner": "hr"}
            """;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private DeviceRepository deviceRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        deviceRepository.deleteAll();
        deviceRepository.saveAll(List.of(
                device("1", "router-01", "router", "it-department"),
                device("2", "printer-02", "printer", "accounting"),
                device("3", "laptop-03", "laptop", "r.vladyslav")));
    }

    @Nested
    @WithAnonymousUser(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    class Anonymous {

        @Test
        void canOpenIndexPage() throws Exception {
            MockHttpServletResponse response = perform(get("/index.html"));
            assertEquals(200, response.getStatus());
            assertTrue(response.getContentAsString().contains("device manager"));
        }

        @Test
        void cannotListDevices() throws Exception {
            assertUnauthorized(get(DEVICES));
        }

        @Test
        void cannotReadAuditReport() throws Exception {
            assertUnauthorized(get(REPORTS + "/audit"));
        }
    }

    @Nested
    class Login {

        @Test
        void correctPasswordIsAccepted() throws Exception {
            assertEquals(200, perform(get(DEVICES).with(httpBasic("user", "user"))).getStatus());
        }

        @Test
        void wrongPasswordIsRejected() throws Exception {
            assertUnauthorized(get(DEVICES).with(httpBasic("user", "wrong")));
        }

        @Test
        void unknownUserIsRejected() throws Exception {
            assertUnauthorized(get(DEVICES).with(httpBasic("guest", "guest")));
        }

        @Test
        void userAccountHasNoAdminRights() throws Exception {
            assertForbidden(get(REPORTS + "/owners").with(httpBasic("user", "user")));
        }

        @Test
        void adminAccountHasAdminRights() throws Exception {
            assertEquals(200, perform(get(REPORTS + "/owners").with(httpBasic("admin", "admin"))).getStatus());
        }

        @Test
        void adminAccountHasNoSuperadminRights() throws Exception {
            assertForbidden(get(REPORTS + "/audit").with(httpBasic("admin", "admin")));
        }

        @Test
        void superadminAccountHasSuperadminRights() throws Exception {
            assertEquals(200, perform(get(REPORTS + "/audit").with(httpBasic("superadmin", "superadmin"))).getStatus());
        }
    }

    @Nested
    @WithMockUser(username = "user", roles = "USER", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    class AsUser {

        @Test
        void canListDevices() throws Exception {
            assertDevicesListed();
        }

        @Test
        void canReadOneDevice() throws Exception {
            assertDeviceOneReadable();
        }

        @Test
        void cannotCreateDevice() throws Exception {
            assertForbidden(createDevice());
            assertEquals(3, deviceRepository.count());
        }

        @Test
        void cannotUpdateDevice() throws Exception {
            assertForbidden(updateDevice());
            assertEquals("accounting", stored("2").getOwner());
        }

        @Test
        void cannotDeleteDevice() throws Exception {
            assertForbidden(delete(DEVICES + "/1"));
            assertTrue(deviceRepository.existsById("1"));
        }

        @Test
        void canReadSummaryReport() throws Exception {
            assertSummaryReport();
        }

        @Test
        void cannotReadOwnersReport() throws Exception {
            assertForbidden(get(REPORTS + "/owners"));
        }

        @Test
        void cannotReadAuditReport() throws Exception {
            assertForbidden(get(REPORTS + "/audit"));
        }
    }

    @Nested
    @WithMockUser(username = "admin", roles = "ADMIN", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    class AsAdmin {

        @Test
        void canListDevices() throws Exception {
            assertDevicesListed();
        }

        @Test
        void canReadOneDevice() throws Exception {
            assertDeviceOneReadable();
        }

        @Test
        void canCreateDevice() throws Exception {
            assertCreatedBy("admin");
        }

        @Test
        void canUpdateDevice() throws Exception {
            assertUpdatedBy("admin");
        }

        @Test
        void cannotDeleteDevice() throws Exception {
            assertForbidden(delete(DEVICES + "/1"));
            assertTrue(deviceRepository.existsById("1"));
        }

        @Test
        void canReadSummaryReport() throws Exception {
            assertSummaryReport();
        }

        @Test
        void canReadOwnersReport() throws Exception {
            assertOwnersReport();
        }

        @Test
        void cannotReadAuditReport() throws Exception {
            assertForbidden(get(REPORTS + "/audit"));
        }
    }

    @Nested
    @WithMockUser(username = "superadmin", roles = "SUPERADMIN", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    class AsSuperadmin {

        @Test
        void canListDevices() throws Exception {
            assertDevicesListed();
        }

        @Test
        void canReadOneDevice() throws Exception {
            assertDeviceOneReadable();
        }

        @Test
        void canCreateDevice() throws Exception {
            assertCreatedBy("superadmin");
        }

        @Test
        void canUpdateDevice() throws Exception {
            assertUpdatedBy("superadmin");
        }

        @Test
        void canDeleteDevice() throws Exception {
            assertEquals(204, perform(delete(DEVICES + "/3")).getStatus());
            assertFalse(deviceRepository.existsById("3"));
            assertEquals(2, deviceRepository.count());
        }

        @Test
        void canReadSummaryReport() throws Exception {
            assertSummaryReport();
        }

        @Test
        void canReadOwnersReport() throws Exception {
            assertOwnersReport();
        }

        @Test
        void canReadAuditReport() throws Exception {
            MockHttpServletResponse response = perform(get(REPORTS + "/audit"));
            assertEquals(200, response.getStatus());
            List<String> creators = JsonPath.read(response.getContentAsString(), "$[*].createdBy");
            assertEquals(List.of("system", "system", "system"), creators);
        }
    }

    private Device device(String id, String name, String type, String owner) {
        return Device.builder().id(id).name(name).type(type).owner(owner).build();
    }

    private Device stored(String id) {
        return deviceRepository.findById(id).orElseThrow();
    }

    private MockHttpServletRequestBuilder createDevice() {
        return post(DEVICES).contentType(MediaType.APPLICATION_JSON).content(NEW_DEVICE);
    }

    private MockHttpServletRequestBuilder updateDevice() {
        return put(DEVICES + "/2").contentType(MediaType.APPLICATION_JSON).content(UPDATED_DEVICE);
    }

    private MockHttpServletResponse perform(MockHttpServletRequestBuilder request) throws Exception {
        return mockMvc.perform(request).andReturn().getResponse();
    }

    private void assertDevicesListed() throws Exception {
        MockHttpServletResponse response = perform(get(DEVICES));
        assertEquals(200, response.getStatus());
        List<String> names = JsonPath.read(response.getContentAsString(), "$[*].name");
        assertEquals(Set.of("router-01", "printer-02", "laptop-03"), Set.copyOf(names));
    }

    private void assertDeviceOneReadable() throws Exception {
        MockHttpServletResponse response = perform(get(DEVICES + "/1"));
        assertEquals(200, response.getStatus());
        assertEquals("router-01", JsonPath.read(response.getContentAsString(), "$.name"));
    }

    private void assertCreatedBy(String username) throws Exception {
        MockHttpServletResponse response = perform(createDevice());
        assertEquals(201, response.getStatus());
        String id = JsonPath.read(response.getContentAsString(), "$.id");
        Device created = stored(id);
        assertEquals("switch-04", created.getName());
        assertEquals(username, created.getCreatedBy());
        assertEquals(4, deviceRepository.count());
    }

    private void assertUpdatedBy(String username) throws Exception {
        assertEquals(200, perform(updateDevice()).getStatus());
        Device updated = stored("2");
        assertEquals("hr", updated.getOwner());
        assertEquals("system", updated.getCreatedBy());
        assertEquals(username, updated.getLastModifiedBy());
        assertEquals(1L, updated.getVersion());
    }

    private void assertSummaryReport() throws Exception {
        MockHttpServletResponse response = perform(get(REPORTS + "/summary"));
        assertEquals(200, response.getStatus());
        assertEquals("{\"laptop\":1,\"printer\":1,\"router\":1}", response.getContentAsString());
    }

    private void assertOwnersReport() throws Exception {
        MockHttpServletResponse response = perform(get(REPORTS + "/owners"));
        assertEquals(200, response.getStatus());
        assertEquals(
                "{\"accounting\":[\"printer-02\"],\"it-department\":[\"router-01\"],\"r.vladyslav\":[\"laptop-03\"]}",
                response.getContentAsString());
    }

    private void assertUnauthorized(MockHttpServletRequestBuilder request) throws Exception {
        MockHttpServletResponse response = perform(request);
        assertEquals(401, response.getStatus());
        assertEquals("Unauthorized", response.getErrorMessage());
    }

    private void assertForbidden(MockHttpServletRequestBuilder request) throws Exception {
        MockHttpServletResponse response = perform(request);
        assertEquals(403, response.getStatus());
        assertEquals("Forbidden", response.getErrorMessage());
    }
}
