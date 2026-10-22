# Trabajo Práctico Obligatorio (TPO) - Programación Orientada a Objetos

## Propuesta B: Agencia de Turismo "Rumbo Sur"

---

##  Integrantes del Grupo
* **Dolores Cerrudo Amoedo**
* **Germana Mendoza**

---

## Descripción del Sistema
Este proyecto implementa la solución de software para la agencia de turismo **"Rumbo Sur"**. El sistema gestiona de forma integral el catálogo de tours (aventura, cultural y naturaleza), las salidas programadas, la asignación de recursos (guías y vehículos con validaciones de habilitación y cupos), la inscripción de clientes y pasajeros, la contratación de servicios adicionales y seguros obligatorios, el cálculo de tarifas por temporada y promociones, el registro de pagos con seña y recargos por tarjeta, las modificaciones y cancelaciones con generación de notas de ajuste, la operación de salidas y la liquidación de honorarios a guías, además de la emisión de reportes gerenciales.

---

## Estructura del Repositorio
* `/docs`: Contiene la documentación del proyecto, el informe final en PDF de la Etapa 1 y los supuestos adoptados.
* `/docs/uml`: Contiene los diagramas de casos de uso, clases (inicial y final) y secuencia en formato de imagen.
* `/src`: Código fuente de la aplicación en Java.
* `/test`: Pruebas unitarias sobre la lógica de negocio.

---

### Diagrama de Casos de Uso
- **Descripción:** Modela las funcionalidades principales y los límites del sistema de la agencia de turismo desde la perspectiva de los actores internos (Administrador, Coordinador de Operaciones y Vendedor).
- **Elementos destacados:** Identifica claramente las interacciones directas con el sistema y detalla las relaciones de dependencia, utilizando estereotipos de inclusión (`<<include>>`) para acciones obligatorias y de extensión (`<<extend>>`) para flujos opcionales, como la contratación de servicios adicionales.

---

### Diagrama de Clases Inicial
- **Descripción:** Representa la estructura estática del dominio del negocio de "Rumbo Sur" antes de la etapa de refactorización y aplicación de patrones de diseño, definiendo las entidades clave, sus atributos y métodos.
- **Elementos destacados:** Ilustra las relaciones estructurales de asociación, composición (ej. entre `Reserva` y `Pasajero` o `Pago`) y establece jerarquías de herencia fundamentales con sobreescritura de métodos (polimorfismo), aplicadas en las superclases abstractas `Tour` y `Empleado`.
---

## Diagramas de Secuencia

### Diagrama 1: Creación de Reserva (CU06)
* **Descripción:** Modela la interacción dinámica entre el vendedor, la interfaz, el controlador, la entidad salida, la entidad reserva y la base de datos durante el flujo exitoso de registro.
* **Elementos destacados:** Incorpora un bloque de control condicional (`opt`) para evaluar de forma opcional la selección de servicios adicionales por parte del cliente.

### Diagrama 2: Cancelación de Reserva (CU13 - Flujo Alternativo)
* **Descripción:** Representa el comportamiento del sistema ante la solicitud de cancelación de una reserva por parte de un usuario.
* **Elementos destacados:** Utiliza un bloque de control condicional (`alt`) para bifurcar el flujo según los días de anticipación restantes, aplicando un reintegro total o una penalización con cargo según corresponda.
