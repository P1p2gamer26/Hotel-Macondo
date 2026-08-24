const menuToggle = document.querySelector("[data-menu-toggle]");
const mobileMenu = document.querySelector("[data-mobile-menu]");

if (menuToggle && mobileMenu) {
  menuToggle.addEventListener("click", () => {
    const isOpen = menuToggle.getAttribute("aria-expanded") === "true";
    menuToggle.setAttribute("aria-expanded", String(!isOpen));
    menuToggle.setAttribute("aria-label", isOpen ? "Abrir menú" : "Cerrar menú");
    mobileMenu.hidden = isOpen;
  });
}

const filterButtons = document.querySelectorAll("[data-service-filter]");
const serviceCards = document.querySelectorAll("[data-service-card]");

filterButtons.forEach((button) => {
  button.addEventListener("click", () => {
    const selectedCategory = button.dataset.serviceFilter;

    filterButtons.forEach((item) => {
      const isSelected = item === button;
      item.classList.toggle("is-selected", isSelected);
      item.setAttribute("aria-pressed", String(isSelected));
    });

    serviceCards.forEach((card) => {
      card.hidden =
        selectedCategory !== "Todos" &&
        card.dataset.category !== selectedCategory;
    });
  });
});

document.querySelectorAll("[data-add-service]").forEach((button) => {
  button.addEventListener("click", () => {
    const isAdded = button.classList.toggle("is-added");
    const isDetailButton = button.classList.contains("info-add-button");
    button.textContent = isAdded
      ? isDetailButton ? "Añadido a mi estadía" : "Añadido"
      : isDetailButton ? "Agregar a mi estadía" : "Agregar";
    button.setAttribute("aria-pressed", String(isAdded));
  });
});

const includedMore = document.querySelector(".included-more");
const includedGrid = document.querySelector(".detail-includes-grid");

if (includedMore && includedGrid) {
  includedMore.addEventListener("click", (event) => {
    event.preventDefault();
    includedGrid.scrollIntoView({ behavior: "smooth", block: "center" });
  });
}
