/**
 * Creación habitación
 */
function abrirNuevaHabitacion() {
  document.getElementById("Titulo").innerText = "Nuevo Tipo de Habitación";
  
  const formId = document.getElementById("formId");
  const formNombre = document.getElementById("formNombre");
  const formDescripcion = document.getElementById("formDescripcion");
  const formCapacidad = document.getElementById("formCapacidad");
  const formPrecio = document.getElementById("formPrecio");

  if (formId) formId.value = "";
  if (formNombre) formNombre.value = "";
  if (formDescripcion) formDescripcion.value = "";
  if (formCapacidad) formCapacidad.value = "";
  if (formPrecio) formPrecio.value = "";

  document.getElementById("Tipo").classList.remove("hidden");
}

/**
 * Edicion
 */
function abrirEditarHabitacion(btn) {
  document.getElementById("Titulo").innerText = "Editar Tipo de Habitación";

  const formId = document.getElementById("formId");
  const formNombre = document.getElementById("formNombre");
  const formDescripcion = document.getElementById("formDescripcion");
  const formCapacidad = document.getElementById("formCapacidad");
  const formPrecio = document.getElementById("formPrecio");

  if (formId) formId.value = btn.getAttribute("data-id") || "";
  if (formNombre) formNombre.value = btn.getAttribute("data-nombre") || "";
  if (formDescripcion) formDescripcion.value = btn.getAttribute("data-descripcion") || "";
  if (formCapacidad) formCapacidad.value = btn.getAttribute("data-capacidad") || "";
  if (formPrecio) formPrecio.value = btn.getAttribute("data-precio") || "";

  document.getElementById("Tipo").classList.remove("hidden");
}

/**
 * Ocultar
 */
function cerrar() {
  document.getElementById("Tipo").classList.add("hidden");
}