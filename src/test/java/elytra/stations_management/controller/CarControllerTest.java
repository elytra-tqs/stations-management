package elytra.stations_management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import elytra.stations_management.models.Car;
import elytra.stations_management.models.EVDriver;
import elytra.stations_management.services.CarService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;
import org.springframework.security.test.context.support.WithMockUser;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "testuser", roles = {"EV_DRIVER"})
class CarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CarService carService;

    private Car testCar;
    private EVDriver testDriver;

    @BeforeEach
    void setUp() {
        testDriver = EVDriver.builder()
                .id(1L)
                .build();

        testCar = Car.builder()
                .id(1L)
                .model("Tesla Model 3")
                .licensePlate("ABC-1234")
                .batteryCapacity(75.0)
                .chargerType("Type 2")
                .evDriver(testDriver)
                .build();
    }

    @Test
    void registerCar_ShouldReturnCreatedCar() throws Exception {
        Car newCar = Car.builder()
                .model("Tesla Model S")
                .licensePlate("XYZ-5678")
                .batteryCapacity(100.0)
                .chargerType("Type 2")
                .build();

        when(carService.registerCar(any(Car.class), eq(1L))).thenReturn(testCar);

        mockMvc.perform(post("/api/v1/cars/driver/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newCar)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.model").value("Tesla Model 3"))
                .andExpect(jsonPath("$.licensePlate").value("ABC-1234"));

        verify(carService).registerCar(any(Car.class), eq(1L));
    }

    @Test
    void registerCar_ShouldReturnBadRequest_WhenServiceThrowsException() throws Exception {
        Car newCar = Car.builder()
                .model("Tesla Model S")
                .licensePlate("EXISTING-123")
                .batteryCapacity(100.0)
                .chargerType("Type 2")
                .build();

        when(carService.registerCar(any(Car.class), eq(1L)))
                .thenThrow(new RuntimeException("License plate already registered"));

        mockMvc.perform(post("/api/v1/cars/driver/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newCar)))
                .andExpect(status().isBadRequest());

        verify(carService).registerCar(any(Car.class), eq(1L));
    }

    @Test
    void getCarsByDriver_ShouldReturnCarsList() throws Exception {
        List<Car> cars = Arrays.asList(testCar,
                Car.builder()
                    .id(2L)
                    .model("Nissan Leaf")
                    .licensePlate("DEF-5678")
                    .batteryCapacity(40.0)
                    .chargerType("CHAdeMO")
                    .evDriver(testDriver)
                    .build());

        when(carService.getCarsByDriver(1L)).thenReturn(cars);

        mockMvc.perform(get("/api/v1/cars/driver/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].model").value("Tesla Model 3"))
                .andExpect(jsonPath("$[1].model").value("Nissan Leaf"));

        verify(carService).getCarsByDriver(1L);
    }

    @Test
    void getCarsByDriver_ShouldReturnEmptyList_WhenNoCars() throws Exception {
        when(carService.getCarsByDriver(999L)).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/v1/cars/driver/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(carService).getCarsByDriver(999L);
    }

    @Test
    void getCarsByChargerType_ShouldReturnCarsList() throws Exception {
        List<Car> cars = Arrays.asList(testCar,
                Car.builder()
                    .id(2L)
                    .model("BMW i3")
                    .licensePlate("GHI-9012")
                    .batteryCapacity(42.0)
                    .chargerType("Type 2")
                    .evDriver(testDriver)
                    .build());

        when(carService.getCarsByChargerType("Type 2")).thenReturn(cars);

        mockMvc.perform(get("/api/v1/cars/charger-type/Type 2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].chargerType").value("Type 2"))
                .andExpect(jsonPath("$[1].chargerType").value("Type 2"));

        verify(carService).getCarsByChargerType("Type 2");
    }

    @Test
    void updateCar_ShouldReturnUpdatedCar() throws Exception {
        Car updatedCarData = Car.builder()
                .model("Tesla Model 3 Updated")
                .licensePlate("ABC-1234")
                .batteryCapacity(80.0)
                .chargerType("CCS")
                .build();

        Car updatedCar = Car.builder()
                .id(1L)
                .model("Tesla Model 3 Updated")
                .licensePlate("ABC-1234")
                .batteryCapacity(80.0)
                .chargerType("CCS")
                .evDriver(testDriver)
                .build();

        when(carService.updateCar(eq(1L), any(Car.class))).thenReturn(updatedCar);

        mockMvc.perform(put("/api/v1/cars/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedCarData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.model").value("Tesla Model 3 Updated"))
                .andExpect(jsonPath("$.batteryCapacity").value(80.0))
                .andExpect(jsonPath("$.chargerType").value("CCS"));

        verify(carService).updateCar(eq(1L), any(Car.class));
    }

    @Test
    void updateCar_ShouldReturnBadRequest_WhenServiceThrowsException() throws Exception {
        Car updatedCarData = Car.builder()
                .model("Tesla Model 3 Updated")
                .licensePlate("EXISTING-LICENSE")
                .batteryCapacity(80.0)
                .chargerType("CCS")
                .build();

        when(carService.updateCar(eq(1L), any(Car.class)))
                .thenThrow(new RuntimeException("License plate already registered"));

        mockMvc.perform(put("/api/v1/cars/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedCarData)))
                .andExpect(status().isBadRequest());

        verify(carService).updateCar(eq(1L), any(Car.class));
    }

    @Test
    void deleteCar_ShouldReturnNoContent() throws Exception {
        doNothing().when(carService).deleteCar(1L);

        mockMvc.perform(delete("/api/v1/cars/1"))
                .andExpect(status().isNoContent());

        verify(carService).deleteCar(1L);
    }

    @Test
    void deleteCar_ShouldReturnNotFound_WhenCarNotFound() throws Exception {
        doThrow(new RuntimeException("Car not found")).when(carService).deleteCar(999L);

        mockMvc.perform(delete("/api/v1/cars/999"))
                .andExpect(status().isNotFound());

        verify(carService).deleteCar(999L);
    }
}