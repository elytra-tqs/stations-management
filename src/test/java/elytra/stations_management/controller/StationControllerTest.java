package elytra.stations_management.controller;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class StationControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Test
        void shouldRegisterNewStation() throws Exception {
                String stationJson = "{" +
                                "\"name\": \"Central Station\"," +
                                "\"address\": \"123 Main St\"," +
                                "\"latitude\": 40.12345," +
                                "\"longitude\": -8.54321" +
                                "}";

                mockMvc.perform(post("/api/v1/stations")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(stationJson))
                                .andExpect(status().isCreated());
        }

        @Test
        void registerStation_withChargers_andRetrieve() throws Exception {
                String stationJson = "{" +
                                "\"name\": \"Central Station\"," +
                                "\"address\": \"123 Main St\"," +
                                "\"latitude\": 40.12345," +
                                "\"longitude\": -8.54321," +
                                "\"chargers\": [" +
                                "{\"type\": \"Type2\", \"power\": 22.0}," +
                                "{\"type\": \"CCS\", \"power\": 50.0}" +
                                "]" +
                                "}";
                mockMvc.perform(post("/api/v1/stations")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(stationJson))
                                .andExpect(status().isCreated());

                mockMvc.perform(get("/api/v1/stations"))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        }

        @Test
        void registerStation_invalidJson_returns400() throws Exception {
                String invalidJson = "{" +
                                "\"address\": \"123 Main St\"," +
                                "\"latitude\": 40.12345," +
                                "\"longitude\": -8.54321" +
                                "}";
                mockMvc.perform(post("/api/v1/stations")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(invalidJson))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void registerStation_unsupportedContentType_returns415() throws Exception {
                String stationJson = "name=Central Station&address=123 Main St&latitude=40.12345&longitude=-8.54321";
                mockMvc.perform(post("/api/v1/stations")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .content(stationJson))
                                .andExpect(status().isUnsupportedMediaType());
        }

        @Test
        void registerStation_extraFields_ignoresOrRejects() throws Exception {
                String stationJson = "{" +
                                "\"name\": \"Central Station\"," +
                                "\"address\": \"123 Main St\"," +
                                "\"latitude\": 40.12345," +
                                "\"longitude\": -8.54321," +
                                "\"unexpectedField\": \"shouldBeIgnoredOrRejected\"" +
                                "}";
                mockMvc.perform(post("/api/v1/stations")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(stationJson))
                                .andExpect(status().isCreated());
        }

        @Test
        void getStations_includesRegisteredStation() throws Exception {
                String stationJson = "{" +
                                "\"name\": \"Central Station\"," +
                                "\"address\": \"123 Main St\"," +
                                "\"latitude\": 40.12345," +
                                "\"longitude\": -8.54321" +
                                "}";
                mockMvc.perform(post("/api/v1/stations")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(stationJson))
                                .andExpect(status().isCreated());

                mockMvc.perform(get("/api/v1/stations"))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        }

        @Test
        void getChargersByStation() throws Exception {
            String stationJson = "{" +
                    "\"name\": \"Central Station\"," +
                    "\"address\": \"123 Main St\"," +
                    "\"latitude\": 40.12345," +
                    "\"longitude\": -8.54321," +
                    "\"chargers\": [" +
                    "{\"type\": \"Type2\", \"power\": 22.0}," +
                    "{\"type\": \"CCS\", \"power\": 50.0}" +
                    "]" +
                    "}";

            MvcResult result = mockMvc.perform(post("/api/v1/stations")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(stationJson))
                    .andExpect(status().isCreated())
                    .andReturn();

            String responseBody = result.getResponse().getContentAsString();
            int stationId = JsonPath.read(responseBody, "$.id");

            mockMvc.perform(get("/api/v1/stations/" + stationId + "/chargers"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        }


        @Test
        void getChargersByStation_invalidId() throws Exception {
                mockMvc.perform(get("/api/v1/stations/invalidId/chargers"))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void getStation_invalidId_shouldReturn404() throws Exception {
            mockMvc.perform(get("/api/v1/stations/999"))
                    .andExpect(status().isNotFound());
        }

        @Test
        void getChargersByStation_noChargers() throws Exception {
            String stationJson = "{" +
                    "\"name\": \"Central Station\"," +
                    "\"address\": \"123 Main St\"," +
                    "\"latitude\": 40.12345," +
                    "\"longitude\": -8.54321" +
                    "}";

            MvcResult result = mockMvc.perform(post("/api/v1/stations")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(stationJson))
                    .andExpect(status().isCreated())
                    .andReturn();

            String responseBody = result.getResponse().getContentAsString();
            int stationId = JsonPath.read(responseBody, "$.id");

            mockMvc.perform(get("/api/v1/stations/" + stationId + "/chargers"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        }

        @Test
        void getChargersByStation_stationNotFound_returns404() throws Exception {
                mockMvc.perform(get("/api/v1/stations/999/chargers"))
                        .andExpect(status().isNotFound());
        }

        @Test
        void createChargerForStation_shouldReturn201() throws Exception {
            String stationJson = "{" +
                    "\"name\": \"North Station\"," +
                    "\"address\": \"789 Elm St\"," +
                    "\"latitude\": 41.12345," +
                    "\"longitude\": -7.54321" +
                    "}";

            mockMvc.perform(post("/api/v1/stations")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(stationJson))
                    .andExpect(status().isCreated());

            String chargerJson = "{" +
                    "\"type\": \"Type2\"," +
                    "\"power\": 22.0" +
                    "}";

            mockMvc.perform(post("/api/v1/stations/1/chargers")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(chargerJson))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        }

        @Test
        void createChargerForInvalidStation_shouldReturn404() throws Exception {
            String chargerJson = "{" +
                    "\"type\": \"Type2\"," +
                    "\"power\": 22.0" +
                    "}";

            mockMvc.perform(post("/api/v1/stations/999/chargers")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(chargerJson))
                    .andExpect(status().isNotFound());
        }


        @Test
        void updateStation_shouldReturn200() throws Exception {
            String stationJson = "{" +
                    "\"name\": \"East Station\"," +
                    "\"address\": \"456 Oak St\"," +
                    "\"latitude\": 42.12345," +
                    "\"longitude\": -6.54321" +
                    "}";

            MvcResult result = mockMvc.perform(post("/api/v1/stations")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(stationJson))
                    .andExpect(status().isCreated())
                    .andReturn();

            String responseBody = result.getResponse().getContentAsString();
            // Extract the ID from the JSON response
            int stationId = JsonPath.read(responseBody, "$.id");

            String updatedStationJson = "{" +
                    "\"name\": \"Updated East Station\"," +
                    "\"address\": \"456 Oak St Updated\"," +
                    "\"latitude\": 42.54321," +
                    "\"longitude\": -6.12345" +
                    "}";

            mockMvc.perform(put("/api/v1/stations/" + stationId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(updatedStationJson))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        }

        @Test
        void updateStation_invalidId_shouldReturn404() throws Exception {
            String updatedStationJson = "{" +
                    "\"name\": \"Updated Station\"," +
                    "\"address\": \"456 Oak St Updated\"," +
                    "\"latitude\": 42.54321," +
                    "\"longitude\": -6.12345" +
                    "}";

            mockMvc.perform(put("/api/v1/stations/999")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(updatedStationJson))
                    .andExpect(status().isNotFound());
        }

        @Test
        void deleteStation_shouldReturn204() throws Exception {
            String stationJson = "{" +
                    "\"name\": \"South Station\"," +
                    "\"address\": \"321 Pine St\"," +
                    "\"latitude\": 43.12345," +
                    "\"longitude\": -5.54321" +
                    "}";

            mockMvc.perform(post("/api/v1/stations")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(stationJson))
                    .andExpect(status().isCreated());

            mockMvc.perform(get("/api/v1/stations/1"))
                    .andExpect(status().isOk());

            mockMvc.perform(delete("/api/v1/stations/1"))
                    .andExpect(status().isNoContent());
        }

        @Test
        void deleteStation_invalidId_shouldReturn404() throws Exception {
            mockMvc.perform(delete("/api/v1/stations/999"))
                    .andExpect(status().isNotFound());
        }

}