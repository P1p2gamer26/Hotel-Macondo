// Referencias al menu comun de la landing. Si la vista no muestra el menu movil,
// estas variables quedan vacias y el resto de la pagina sigue funcionando.
const menuToggle = document.querySelector("[data-menu-toggle]");
const mobileMenu = document.querySelector("[data-mobile-menu]");

// Alterna la visibilidad del menu movil y mantiene sus atributos ARIA sincronizados.
if (menuToggle && mobileMenu) {
  menuToggle.addEventListener("click", () => {
    const willOpen = menuToggle.getAttribute("aria-expanded") !== "true";
    menuToggle.setAttribute("aria-expanded", String(willOpen));
    menuToggle.setAttribute("aria-label", willOpen ? "Cerrar menú" : "Abrir menú");
    mobileMenu.setAttribute("aria-hidden", String(!willOpen));
    mobileMenu.classList.toggle("hidden", !willOpen);
  });
}

// Los filtros y tarjetas se identifican con data attributes para no depender
// de clases de estilo. La categoria proviene de ServicioController y Thymeleaf.
const filterButtons = document.querySelectorAll("[data-service-filter]");
const serviceCards = document.querySelectorAll("[data-service-card]");

// Marca el filtro activo y oculta las tarjetas cuya categoria no coincide.
filterButtons.forEach((button) => {
  button.addEventListener("click", () => {
    const selectedCategory = button.dataset.serviceFilter;

    filterButtons.forEach((item) => {
      item.setAttribute("aria-pressed", String(item === button));
    });

    serviceCards.forEach((card) => {
      const shouldHide =
        selectedCategory !== "Todos" &&
        card.dataset.category !== selectedCategory;
      card.classList.toggle("hidden", shouldHide);
    });
  });
});

// Simula agregar un servicio sin persistir datos: cambia solo el texto y el
// estado accesible del boton, definidos por data-default-label y data-added-label.
document.querySelectorAll("[data-add-service]").forEach((button) => {
  button.addEventListener("click", () => {
    const willBeAdded = button.getAttribute("aria-pressed") !== "true";
    button.setAttribute("aria-pressed", String(willBeAdded));
    button.textContent = willBeAdded
      ? button.dataset.addedLabel
      : button.dataset.defaultLabel;
  });
});

// En el detalle, el enlace de inclusiones restantes desplaza la pantalla hacia
// la lista completa para evitar duplicar los elementos de servicio.incluidos.
const includedMore = document.querySelector("[data-included-more]");
const includedGrid = document.querySelector("[data-included-grid]");

if (includedMore && includedGrid) {
  includedMore.addEventListener("click", () => {
    includedGrid.scrollIntoView({ behavior: "smooth", block: "center" });
  });
}