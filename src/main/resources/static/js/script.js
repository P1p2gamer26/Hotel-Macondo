// Genera los indicadores del carrusel de servicios segun cuantos slides existan.
// Se hace con JS y no en el HTML porque los servicios vienen del modelo: si
// manana se agrega uno mas, los puntitos se ajustan solos.
const contenedorIndicadores = document.getElementById("indicadoresServicios");

if (contenedorIndicadores) {
  const slides = document.querySelectorAll("#carruselServicios .carousel-item");

  for (let i = 0; i < slides.length; i++) {
    const boton = document.createElement("button");

    boton.type = "button";
    boton.setAttribute("data-bs-target", "#carruselServicios");
    boton.setAttribute("data-bs-slide-to", i);
    boton.setAttribute("aria-label", "Servicio " + (i + 1));

    if (i === 0) {
      boton.classList.add("active");
      boton.setAttribute("aria-current", "true");
    }

    contenedorIndicadores.appendChild(boton);
  }
}

// La fecha de salida nunca puede ser anterior a la de entrada.
// Se resuelve con el atributo min del input date (nativo del navegador) en vez
// de escribir una validacion propia.
const entrada = document.getElementById("fechaEntrada");
const salida = document.getElementById("fechaSalida");

if (entrada && salida) {
  entrada.addEventListener("change", () => {
    salida.min = entrada.value;

    if (salida.value && salida.value <= entrada.value) {
      salida.value = "";
    }
  });
}

document.addEventListener("DOMContentLoaded", () => {
  const slides = document.querySelectorAll(".slide-servicio");
  const thumbs = document.querySelectorAll(".thumb-btn");
  
  if (slides.length === 0) return; // Validación de seguridad

  let actual = 0;
  let intervaloAutoPlay;

  // Función principal para cambiar de diapositiva
  function cambiarSlide(nuevoIndex) {
    // 1. Ocultar el actual
    slides[actual].classList.replace("opacity-100", "opacity-0");
    slides[actual].classList.replace("z-10", "z-0");
    
    // Quitar estilos dorados de la miniatura actual
    thumbs[actual].classList.remove("border-[var(--dorado)]", "opacity-100", "scale-105");
    thumbs[actual].classList.add("border-transparent", "opacity-50");

    // 2. Actualizar el índice
    actual = nuevoIndex;

    // 3. Mostrar el nuevo
    slides[actual].classList.replace("opacity-0", "opacity-100");
    slides[actual].classList.replace("z-0", "z-10");
    
    // Resaltar la nueva miniatura
    thumbs[actual].classList.remove("border-transparent", "opacity-50");
    thumbs[actual].classList.add("border-[var(--dorado)]", "opacity-100", "scale-105");
  }

  // Función para avanzar automáticamente
  function avanzar() {
    let siguiente = (actual + 1) % slides.length;
    cambiarSlide(siguiente);
  }

  // Iniciar el temporizador (cada 5 segundos)
  intervaloAutoPlay = setInterval(avanzar, 5000);

  // Hacer que las miniaturas sean clickeables
  thumbs.forEach((boton) => {
    boton.addEventListener("click", function() {
      // Reiniciamos el temporizador para que no cambie justo después del clic
      clearInterval(intervaloAutoPlay); 
      
      let indexSeleccionado = parseInt(this.getAttribute("data-slide"));
      cambiarSlide(indexSeleccionado);
      
      intervaloAutoPlay = setInterval(avanzar, 5000);
    });
  });
});
