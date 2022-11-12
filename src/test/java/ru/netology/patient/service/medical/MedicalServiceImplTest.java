package ru.netology.patient.service.medical;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ru.netology.patient.entity.BloodPressure;
import ru.netology.patient.entity.HealthInfo;
import ru.netology.patient.entity.PatientInfo;
import ru.netology.patient.repository.PatientInfoRepository;
import ru.netology.patient.service.alert.SendAlertService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class MedicalServiceImplTest {
    private static String ID_1 = UUID.randomUUID().toString();
    private static String ANSWER_SEND_ALERT_SERVICE_SEND = String.format("Warning, patient with id: %s, need help", ID_1);
    private static BigDecimal TEMPERATURE_WITH_DEVIATION = new BigDecimal(37);
    private static BigDecimal TEMPERATURE_WITHOUT_DEVIATION = new BigDecimal(36.6);
    private PatientInfoRepository patientInfoRepository;
    private SendAlertService sendAlertService;
    private MedicalService medicalService;
    private BloodPressure currentPressure;

    private ArgumentCaptor<String> argumentCaptor;


    @BeforeEach
    public void init() {
        ID_1 = UUID.randomUUID().toString();
        patientInfoRepository = Mockito.mock(PatientInfoRepository.class);
        Mockito.when(patientInfoRepository.add(
                new PatientInfo("Иван", "Петров", LocalDate.of(1980, 11, 26),
                        new HealthInfo(new BigDecimal("36.6"), new BloodPressure(120, 80)))
        )).thenReturn(ID_1);
    }

    @Test
    public void checkBloodPressureWithDeviationTest() {
        setupWithDeviation();
        currentPressure = new BloodPressure(60, 120);
        medicalService.checkBloodPressure(ID_1, currentPressure);
        checkAssertEqualsWithDeviation();
    }

    @Test
    public void checkBloodPressureWithoutDeviationTest() {
        setupWithoutDeviation();
        currentPressure = new BloodPressure(120, 60);
        medicalService.checkBloodPressure(ID_1, currentPressure);
        checkAssertEqualsWithDeviation();
    }

    @Test
    public void checkTemperatureWithDeviationTest() {
        setupWithDeviation();
        medicalService.checkTemperature(ID_1, TEMPERATURE_WITH_DEVIATION);
        checkAssertEqualsWithDeviation();
    }

    @Test
    public void checkTemperatureWithoutDeviationTest() {
        setupWithoutDeviation();
        medicalService.checkTemperature(ID_1, TEMPERATURE_WITHOUT_DEVIATION);
        checkAssertEqualsWithDeviation();
    }

    private void setupWithDeviation() {
        sendAlertService = Mockito.mock(SendAlertService.class);
        sendAlertService.send(ANSWER_SEND_ALERT_SERVICE_SEND);

        argumentCaptor = ArgumentCaptor.forClass(String.class);

        currentPressure = new BloodPressure(60, 120);
        medicalService = Mockito.spy(new MedicalServiceImpl(patientInfoRepository, sendAlertService));

        Mockito.doNothing().when(medicalService).checkBloodPressure(ID_1, currentPressure);
        Mockito.doNothing().when(medicalService).checkTemperature(ID_1, TEMPERATURE_WITH_DEVIATION);
    }

    private void setupWithoutDeviation() {
        sendAlertService = Mockito.mock(SendAlertService.class);
        sendAlertService.send(ANSWER_SEND_ALERT_SERVICE_SEND);

        argumentCaptor = ArgumentCaptor.forClass(String.class);

        currentPressure = new BloodPressure(120, 60);
        medicalService = Mockito.spy(new MedicalServiceImpl(patientInfoRepository, sendAlertService));

        Mockito.doNothing().when(medicalService).checkBloodPressure(ID_1, currentPressure);
        Mockito.doNothing().when(medicalService).checkTemperature(ID_1, TEMPERATURE_WITHOUT_DEVIATION);
    }

    private void checkAssertEqualsWithDeviation() {
        Mockito.verify(sendAlertService).send(argumentCaptor.capture());
        Assertions.assertEquals(ANSWER_SEND_ALERT_SERVICE_SEND, argumentCaptor.getValue());
    }
}
