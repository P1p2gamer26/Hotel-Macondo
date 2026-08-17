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
