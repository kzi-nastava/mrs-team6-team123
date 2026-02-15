package rs.ac.uns.ftn.asd.Projekatsiit2023.controllers.ride;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.PassengerRepository;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.RideRepository;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.RouteRepository;
import rs.ac.uns.ftn.asd.Projekatsiit2023.services.*;
import rs.ac.uns.ftn.asd.Projekatsiit2023.validations.OrderRideValidation;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RideController.class)
public class RideControllerFinishRideTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private FinishRideService finishRideService;

    @MockitoBean
    private TrackRideService trackRideService;

    @MockitoBean
    private RideCancellationService cancellationService;

    @MockitoBean
    private RateRideService rateRideService;

    @MockitoBean
    private RideStopService rideStopService;

    @MockitoBean
    private DriverMatchingService driverMatchingService;

    @MockitoBean
    private RideService rideService;

    @MockitoBean
    private PassengerRepository passengerRepository;

    @MockitoBean
    private RouteRepository routeRepository;

    @MockitoBean
    private RideRepository rideRepository;

    @MockitoBean
    private OrderRideValidation orderRideValidation;

    @Test
    public void finishRideTest_whenValidRequest_thenReturnOkWithFinishRideResponse() throws Exception {
        Long rideId = 1L;

        doNothing().when(finishRideService).finishRide(rideId);

        mockMvc.perform(post("/api/rides/{rideId}/finish", rideId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(""));
        verify(finishRideService).finishRide(rideId);
    }

    @Test
    public void finishRide_whenServiceThrowsException_thenReturnInternalServerError() throws Exception {
        Long rideId = 1L;

        doThrow(new RuntimeException("Ride not found"))
                .when(finishRideService)
                .finishRide(rideId);

        mockMvc.perform(post("/api/rides/{rideId}/finish", rideId))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Ride not found"));
        verify(finishRideService).finishRide(rideId);
    }
}
