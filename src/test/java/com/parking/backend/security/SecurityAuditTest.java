package com.parking.backend.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityAuditTest {

    @Autowired
    private MockMvc mvc;

    private void assertRejectsUnauthenticated(ResultActions result) throws Exception {
        result.andExpect(mvcResult -> {
            int status = mvcResult.getResponse().getStatus();
            if (status != 401 && status != 403) {
                throw new AssertionError("Expected 401 or 403 but got " + status);
            }
        });
    }

    @Test
    void customersSearch_shouldRejectWithoutAuth() throws Exception {
        assertRejectsUnauthenticated(
                mvc.perform(get("/api/customers/search").param("document", "12345")));
    }

    @Test
    void customersMeVehicles_shouldRejectWithoutAuth() throws Exception {
        assertRejectsUnauthenticated(
                mvc.perform(get("/api/customers/me/vehicles")));
    }

    @Test
    void staffEndpoint_shouldRejectWithoutAuth() throws Exception {
        assertRejectsUnauthenticated(
                mvc.perform(get("/api/staff").param("parkingLotId", "1")));
    }

    @Test
    void invoicesEndpoint_shouldRejectWithoutAuth() throws Exception {
        assertRejectsUnauthenticated(
                mvc.perform(get("/api/invoices/1")));
    }

    @Test
    void ratesEndpoint_shouldRejectWithoutAuth() throws Exception {
        assertRejectsUnauthenticated(
                mvc.perform(get("/api/parking-lots/1/rates")));
    }

    @Test
    void discountsConfigEndpoint_shouldRejectWithoutAuth() throws Exception {
        assertRejectsUnauthenticated(
                mvc.perform(get("/api/parking-lots/1/discounts/config")));
    }

    @Test
    void activeEntriesEndpoint_shouldRejectWithoutAuth() throws Exception {
        assertRejectsUnauthenticated(
                mvc.perform(get("/api/parking-lots/1/active-entries")));
    }

    @Test
    void reportsEndpoint_shouldRejectWithoutAuth() throws Exception {
        assertRejectsUnauthenticated(
                mvc.perform(get("/api/parking-lots/1/reports").param("period", "day")));
    }

    @Test
    void vehiclesClaimEndpoint_shouldRejectWithoutAuth() throws Exception {
        assertRejectsUnauthenticated(
                mvc.perform(post("/api/vehicles/claim")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")));
    }

    @Test
    void publicParkingLots_shouldBeAccessible() throws Exception {
        mvc.perform(get("/api/parking-lots"))
                .andExpect(status().isOk());
    }

    @Test
    void publicVehicleTypes_shouldBeAccessible() throws Exception {
        mvc.perform(get("/api/vehicle-types"))
                .andExpect(status().isOk());
    }
}
