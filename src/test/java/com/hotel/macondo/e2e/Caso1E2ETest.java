package com.hotel.macondo.e2e;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import io.github.bonigarcia.wdm.WebDriverManager;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class Caso1E2ETest {

    private final String BASE_URL = "http://localhost:4200";

    private WebDriver driver;

    private WebDriverWait wait;

    // ========================================================================
    // MÉTODOS DE PAUSA PARA VISUALIZAR TODO
    // ========================================================================

    private void pausa(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Simula ingresar texto carácter por carácter en un input.

    private void escribirLento(WebElement elemento, String texto, long msPorLetra) {
        for (char c : texto.toCharArray()) {
            elemento.sendKeys(String.valueOf(c));
            pausa(msPorLetra);
        }
    }

    // ========================================================================
    // CONFIGURACIÓN DEL BEFORE EACH Y EL AFTER EACH
    // ========================================================================

    @BeforeEach
    public void init() {

        WebDriverManager.chromedriver().setup();

        ChromeOptions chromeOptions = new ChromeOptions();

        chromeOptions.addArguments("--disable-notifications");
        chromeOptions.addArguments("--disable-extensions");

        this.driver = new ChromeDriver(chromeOptions);
        // Tiempo máximo de 5 segundos (para esperar a que pase algo)
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    }

    // ========================================================================
    // CASO DE PRUEBA - C1
    // ========================================================================

    @Test
    public void Caso1_RegistroConError_Y_DosReservasEnFechasCruzadasConDistintaHabitacion() {

        // ========================================================================
        // Arrage - Todas las variables que se usan
        // ========================================================================

        String correoInvalido = "prueba@gmail"; // Falta el dominio completo
        String correoValido = "prueba@macondo.com";
        String contrasena = "Clavedeprueba";

        LocalDate hoy = LocalDate.now();

        // EL FOMARTO PARA RESERVAR ES DIA-MES-AÑO
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        System.out.println("Fecha actual: " + hoy.format(formatter));

        // Fechas de la primera reserva: dia actual + 3 al dia actual
        String r1Entrada = hoy.format(formatter);
        String r1Salida = hoy.plusDays(3).format(formatter);

        // Fechas de la segunda reserva: dia actual + 1, y + 4 al dia actual
        String r2Entrada = hoy.plusDays(1).format(formatter);
        String r2Salida = hoy.plusDays(4).format(formatter);

        // ====================================================================
        // Act y Assert F1 - Landing page, registro fallido y error
        // ====================================================================
        // Navega al landing
        driver.get(BASE_URL);
        pausa(1000);

        // Espera hasta que el boton de registro sea clickeable y hace clic en el
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(@href, '/registro')]"))).click();
        pausa(800);

        // Diligencia el formulario con datos validos pero correo invalido
        WebElement inputNombre = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("nombre")));
        escribirLento(inputNombre, "Nuevo", 50);

        WebElement inputApellido = driver.findElement(By.id("apellido"));
        escribirLento(inputApellido, "Usuario", 50);

        WebElement inputCedula = driver.findElement(By.id("cedula"));
        escribirLento(inputCedula, "1234567899", 40);

        WebElement inputTelefono = driver.findElement(By.id("telefono"));
        escribirLento(inputTelefono, "1234567891", 40);

        WebElement inputCorreo = driver.findElement(By.id("correo"));
        escribirLento(inputCorreo, correoInvalido, 60);

        WebElement inputPass = driver.findElement(By.id("contrasena"));
        escribirLento(inputPass, contrasena, 40);

        WebElement inputPassConf = driver.findElement(By.id("confirmarContrasena"));
        escribirLento(inputPassConf, contrasena, 40);

        pausa(1000);

        // Envia el formulario, clic en el boton de submit
        driver.findElement(By.id("btnSubmit")).click();

        // Espera a que aparezca la alerta de error
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("mensajeError")));
        WebElement alertaError = driver.findElement(By.id("mensajeError"));

        // Comprueba que el mensaje es el mismo
        Assertions.assertThat(alertaError.getText())
                .contains("El formato del correo electrónico no es válido.");

        pausa(1800);

        // ====================================================================
        // Act y Assert F2 - Correccion para hacer el registro exitoso
        // ====================================================================
        // Limpia el input del correo invalido
        inputCorreo.clear();
        pausa(400);

        // Escribe el correo valido
        escribirLento(inputCorreo, correoValido, 50);
        pausa(800);

        // Reenvia el formulario con los datos ya corregidos
        driver.findElement(By.id("btnSubmit")).click();

        // Espera a que angular procese el registro y redirija a la vista del cliente
        wait.until(ExpectedConditions.urlContains("/cliente/"));
        pausa(1200);

        // ====================================================================
        // Act y Assert F3 - Crear primera reserva
        // ====================================================================
        // Hace clic en la opcion nueva reserva del navbar (para reciclar mas adelante
        // para la otra reserva)
        wait.until(ExpectedConditions.elementToBeClickable(By.id("navNuevaReserva"))).click();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("selectTipoHabitacion")));
        pausa(800);

        // Selecciona la primera categoria disponible del catalogo
        Select selectTipo1 = new Select(driver.findElement(By.id("selectTipoHabitacion")));
        selectTipo1.selectByIndex(1);
        pausa(400);

        // Ingresa las fechas
        WebElement fechaEntrada1 = driver.findElement(By.id("inputFechaEntrada"));
        fechaEntrada1.sendKeys(r1Entrada);
        pausa(400);

        WebElement fechaSalida1 = driver.findElement(By.id("inputFechaSalida"));
        fechaSalida1.sendKeys(r1Salida);
        pausa(600);

        // Consulta disponibilidad para habilitar el boton de confirmacion
        driver.findElement(By.id("btnConsultarDisponibilidad")).click();
        pausa(4000);

        // Confirma la primera reserva
        wait.until(ExpectedConditions.elementToBeClickable(By.id("btnConfirmarReserva"))).click();

        // Espera la redireccion al listado de reservas activas
        wait.until(ExpectedConditions.urlContains("/reservas"));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("tarjeta-reserva-item")));
        pausa(3000);

        // Obtiene la lista de reservas y extrae el identificador de la habitacion
        List<WebElement> tarjetasR1 = driver.findElements(By.className("tarjeta-reserva-item"));
        Assertions.assertThat(tarjetasR1).isNotEmpty();
        String habitacion1Texto = tarjetasR1.get(0).findElement(By.className("habitacion-asignada-texto")).getText();

        // ====================================================================
        // Act y Asser F4 - Segunda reserva y verifica que se asigne otra habitacion
        // ====================================================================
        // Vuelve a nueva reserva usando otra vez el navbar
        wait.until(ExpectedConditions.elementToBeClickable(By.id("navNuevaReserva"))).click();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("selectTipoHabitacion")));
        pausa(800);

        // Selecciona la misma categoria de habitacion
        Select selectTipo2 = new Select(driver.findElement(By.id("selectTipoHabitacion")));
        selectTipo2.selectByIndex(1);
        pausa(400);

        // Ingresa fechas que se cruzan
        WebElement fechaEntrada2 = driver.findElement(By.id("inputFechaEntrada"));
        fechaEntrada2.sendKeys(r2Entrada);
        pausa(400);

        WebElement fechaSalida2 = driver.findElement(By.id("inputFechaSalida"));
        fechaSalida2.sendKeys(r2Salida);
        pausa(600);

        // Consulta disponibilidad
        driver.findElement(By.id("btnConsultarDisponibilidad")).click();
        pausa(4000);

        // Confirma la segunda reserva
        wait.until(ExpectedConditions.elementToBeClickable(By.id("btnConfirmarReserva"))).click();

        // Espera nuevamente a cargar el listado de reservas activas
        wait.until(ExpectedConditions.urlContains("/reservas"));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("tarjeta-reserva-item")));
        pausa(3000);

        // El cliente debe tener al menos dos reservas activas listadas
        List<WebElement> reservas = driver.findElements(By.className("tarjeta-reserva-item"));
        Assertions.assertThat(reservas.size()).isGreaterThanOrEqualTo(2);

        // Extrae el texto de la habitacion asignada a la segunda reserva
        String habitacion2Texto = reservas.get(1).findElement(By.className("habitacion-asignada-texto"))
                .getText();

        // El numero de habitacion asignado en la segunda reserva DEBE ser
        // diferente al primero
        Assertions.assertThat(habitacion2Texto).isNotEqualTo(habitacion1Texto);

    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            pausa(2500);
            driver.quit();
        }
    }
}