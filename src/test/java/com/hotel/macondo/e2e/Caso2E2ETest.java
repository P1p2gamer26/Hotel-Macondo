package com.hotel.macondo.e2e;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.WindowType;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import io.github.bonigarcia.wdm.WebDriverManager;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class Caso2E2ETest {
    
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
    // Funcion extra - iniciar sesion y hacer click visible
    // ========================================================================

    private void iniciarSesion(String correo, String contraseña){

        driver.get(BASE_URL);
        pausa(1000);

        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(@href, '/login')]"))).click();
        pausa(800);

        // Ingresar a la cuenta de un usuario ya registrado (el cliente con id 1)
        WebElement correoInput = wait.until(ExpectedConditions.elementToBeClickable(By.id("username")));
        escribirLento(correoInput, correo, 100);

        WebElement contraseñaInput = wait.until(ExpectedConditions.elementToBeClickable(By.id("password")));
        escribirLento(contraseñaInput, contraseña, 100);

        pausa(2000);

        // Envia el formulario, clic en el boton de submit
        wait.until(ExpectedConditions.elementToBeClickable(By.id("loginBtnSubmit")));

        WebElement loginButton = driver.findElement(By.id("loginBtnSubmit"));
        hacerClickVisible(loginButton);
    }    

    public void hacerClickVisible(WebElement elemento) {

        JavascriptExecutor js = (JavascriptExecutor) driver;

        js.executeScript(
            "arguments[0].scrollIntoView({block: 'center', inline: 'center'});",
            elemento
        );

        wait.until(driver -> {
            return (Boolean) js.executeScript("""
                const elemento = arguments[0];
                const rect = elemento.getBoundingClientRect();

                const x = rect.left + rect.width / 2;
                const y = rect.top + rect.height / 2;

                const encima = document.elementFromPoint(x, y);

                return encima === elemento || elemento.contains(encima);
                """, elemento);
        });

        elemento.click();
    }

    // Convierte un monto formateado en pesos (por ejemplo "$ 1.250.000") en un número
    private long leerMonto(String texto) {
        return Long.parseLong(texto.replaceAll("[^0-9]", ""));
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
    // CASO DE PRUEBA - C2
    // ========================================================================

    @Test
    public void Caso2() {
        // ========================================================================
        // Arrage - Todas las variables que se usan
        // ========================================================================

        // Correo y contraseña del usuario 1
        String correo = "ursula@macondo.com"; 
        String contraseña = "123";

        // Correo y contraseña del operador
        String correoOperador = "operador@macondo.com";
        String contraseñaOperador = "ope";

        // ====================================================================
        // Act y Assert F1 - Landing page, Login y redirección a la página de proximas reservas
        // ====================================================================
        
        iniciarSesion(correo, contraseña);

        // Espera a que angular procese el registro y redirija a la vista del cliente
        wait.until(ExpectedConditions.urlContains("/cliente/"));
        pausa(1200);

        WebElement nombreUsuario = wait.until(ExpectedConditions.elementToBeClickable(By.id("nombreCliente")));

        // Comprueba que el perfil es el correcto
        Assertions.assertThat(nombreUsuario.getText())
                .isEqualTo("Úrsula Iguarán");
        pausa(1800);

        // Redireccionamos a la pestaña de mis reservas
        driver.findElement(By.id("navMisReservas")).click();

        // Obtenemos el codigo de reserva para luego compararlo con el que parecera en operador
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("tarjeta-reserva-item")));
        pausa(3000);

        // Obtiene la lista de reservas y extrae el identificador de la habitacion
        List<WebElement> tarjetasR1 = driver.findElements(By.className("tarjeta-reserva-item"));
        Assertions.assertThat(tarjetasR1).isNotEmpty();
        String codigoReservaCliente = tarjetasR1.get(0).findElement(By.className("numero-reserva")).getText();
        String Texto = tarjetasR1.get(0).findElement(By.className("habitacion-asignada-texto")).getText();
        String numero = Texto.replaceAll("\\D+", " ").trim().split("\\s+")[0];
        
        pausa(1200);

        // ====================================================================
        // Act y Assert F2 - Pestaña operador, login, cambio a reservas, activar reserva
        // ====================================================================

        // Cambiamos de pestaña e iniciamos sesion como operador
        driver.switchTo().newWindow(WindowType.TAB);
        iniciarSesion(correoOperador, contraseñaOperador);

        // Espera a que angular procese el registro y redirija a la vista del cliente
        wait.until(ExpectedConditions.urlContains("/operador"));
        pausa(1200);

        // Vamos a la pestaña de reservas
        driver.findElement(By.id("reservasTabOperador")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("tabla-reservas")));
        List<WebElement> reservas = driver.findElements(By.className("tabla-reservas"));
        WebElement primeraReserva = reservas.get(0);

        // Obtenemos el codigo de reserva del operador para compararlo con el del cliente
        String codigoReservaOperador = primeraReserva.findElement(By.className("codigo-reserva")).getText().trim();
        // Obtenemos el estado de la reserva para confirmar que sea "CONFIRMADA"
        String estado = primeraReserva.findElement(By.className("estado-reserva")).getText().trim();

        // Comprobamos que el codigo de reserva del cliente y del operador son iguales
        Assertions.assertThat(codigoReservaCliente)
                .isEqualTo(codigoReservaOperador);
        // Comprobamos que el estado de la reserva es "CONFIRMADA"
        Assertions.assertThat(estado)
                .isEqualTo("CONFIRMADA");

        // Activamos la reserva del cliente desde el operador
        WebElement botonActivar = primeraReserva.findElement(By.className("activar-reserva"));
        botonActivar.click();

        WebElement estadoActualizado = primeraReserva.findElement(By.className("estado-reserva"));
        // Comprobamos que el estado de la reserva es "ACTIVA"
        pausa(2000);
        Assertions.assertThat(estadoActualizado.getText().trim())
                .isEqualTo("ACTIVA");
        pausa(1200);
        
        // ====================================================================
        // Act y Assert F2 - Cambio de pestaña a cuenta-habitacion, agregar servicios
        // ====================================================================

        driver.findElement(By.id("cuentaTabOperador")).click();

        WebElement numeroInput = wait.until(ExpectedConditions.elementToBeClickable(By.id("habitacionId")));
        numeroInput.click();
        pausa(1000);

        // Ingresar a la cuenta de un usuario ya registrado (el cliente con id 1)
        escribirLento(numeroInput, numero, 100);

        driver.findElement(By.id("buscarCuentaBtn")).click();

        // Esperamos a que se cargue la cuenta y se muestren los servicios disponibles
        WebElement servicioSelect = wait.until(ExpectedConditions.elementToBeClickable(By.id("servicio")));
        Select select = new Select(servicioSelect);

        // Lista que contendra los nombres de los servicios a agregar
        List<String> serviciosAntes = new ArrayList<>();

        // Agregamos el servicio 1 
        select.selectByIndex(1);
        pausa(1000);

        serviciosAntes.add(select.getFirstSelectedOption().getText().split(" — ")[0].trim());

        WebElement cantidadInput = wait.until(ExpectedConditions.elementToBeClickable(By.id("cantidad")));
        cantidadInput.clear();
        cantidadInput.sendKeys("2");
        pausa(1000);

        driver.findElement(By.id("agregar-servicios-btn")).click();
        pausa(1000);

        // Agregamos el servicio 2
        select.selectByIndex(2);
        pausa(1000);

        serviciosAntes.add(select.getFirstSelectedOption().getText().split(" — ")[0].trim());

        cantidadInput = wait.until(ExpectedConditions.elementToBeClickable(By.id("cantidad")));
        cantidadInput.clear();
        cantidadInput.sendKeys("1");
        pausa(1000);

        driver.findElement(By.id("agregar-servicios-btn")).click();
        pausa(1000);

        // Agregamos los servicios a la cuenta

        WebElement serviciosAgregados = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("confirmar-servicios-btn")));
        serviciosAgregados.click();
        pausa(5000);

        // Ahora que ya estan agregados vamos a confirmar que esten la lista de los agregados

        List<WebElement> filas = driver.findElements(By.xpath("//*[@id='tablaServiciosCuenta']/tbody/tr"));
    
        List<String> serviciosDespues = new ArrayList<>();
        for (WebElement fila : filas) {

            List<WebElement> columnas = fila.findElements(By.tagName("td"));

            if (columnas.size() == 6) {
                serviciosDespues.add(columnas.get(0).getText().trim());
            }
        }

        // Comprobamos que los servicios agregados esten en la lista de serviciosDespues

        for(String servicio : serviciosAntes) {
            Assertions.assertThat(serviciosDespues).contains(servicio);
        }
    
        // ====================================================================
        // Act y Assert F2 - checkout de la habitacion, inspeccion y finalizacion de estadia
        // ====================================================================

        // Volvemos a la pagina de inicio del operador para ir a el checkout
    
        driver.findElement(By.id("panelPrincipalTabOperador")).click();
        pausa(1000);

        WebElement checkoutInput = wait.until(ExpectedConditions.elementToBeClickable(By.id("habitacionCheckOutId")));
        checkoutInput.click();
        pausa(1000);

        // Ingresamos a la pestaña ed checkout de la habitacion
        escribirLento(checkoutInput, numero, 100);
        pausa(1000);

        driver.findElement(By.id("checkout-btn")).click();

        // Leemos el saldo que el operador ve antes de pagar (sin valores quemados)
        WebElement saldoPendiente = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("saldo-pendiente")));
        long saldoMostrado = leerMonto(saldoPendiente.getText());
        Assertions.assertThat(saldoMostrado).isPositive();

        WebElement botonPagar = wait.until(ExpectedConditions.elementToBeClickable(By.id("pagar-cuenta-bton")));
        hacerClickVisible(botonPagar);
        pausa(1000);

        // El pago registrado en el historial debe ser exactamente el saldo que se mostró
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("td.pago-monto")));
        List<WebElement> montosPagados = driver.findElements(By.cssSelector("td.pago-monto"));
        long montoCobrado = leerMonto(montosPagados.get(montosPagados.size() - 1).getText());
        Assertions.assertThat(montoCobrado).isEqualTo(saldoMostrado);

        WebElement llavesDevueltas = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("llaves-devueltas")));
        WebElement habitacionInspeccionada = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("habitacion-inspeccionada")));
        
        hacerClickVisible(llavesDevueltas);
        pausa(1000);
        hacerClickVisible(habitacionInspeccionada);
        pausa(1000);

        WebElement finalizarEstadia = wait.until(ExpectedConditions.elementToBeClickable(By.id("finalizar-estadia-btn")));

        hacerClickVisible(finalizarEstadia);
        pausa(1000);

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("estadia-finalizada")));
        WebElement estadiaFinalizada = driver.findElement(By.id("estadia-finalizada"));

        Assertions.assertThat(estadiaFinalizada.getText())
                .contains("Estadía finalizada.");

    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            pausa(2500);
            driver.quit();
        }
    }

}
