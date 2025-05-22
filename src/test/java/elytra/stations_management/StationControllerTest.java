package elytra.stations_management;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
                mockMvc.perform(post("/api/v1/stations")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(stationJson))
                                .andExpect(status().isCreated());

                mockMvc.perform(get("/api/v1/stations/1/chargers"))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        }


        @Test
        void getChargersByStation_invalidId() throws Exception {
                mockMvc.perform(get("/api/v1/stations/invalidId/chargers"))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void getChargersByStation_noChargers() throws Exception {
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

                mockMvc.perform(get("/api/v1/stations/1/chargers"))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        }
}