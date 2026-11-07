# TPO POO - Agencia de Turismo "Rumbo Sur"

## Integrantes

* Dolores Cerrudo Amoedo
* Germana Mendoza

---

## Descripcion del Sistema

Sistema integral de gestion desarrollado en Java para la Agencia de Turismo "Rumbo Sur". La aplicacion permite administrar el catalogo de tours (Aventura, Cultural y Naturaleza), programar salidas operativas asignando guias habilitados y vehiculos de la flota, registrar clientes y emitir reservas con calculo dinamico de tarifas segun el perfil de los pasajeros y la anticipacion de la compra. Asimismo, gestiona el cobro en multiples medios de pago con recargos por cuotas, la emision automatica de seguros obligatorios, politicas polimorficas de cancelacion con reintegros, la emision de listas de pasajeros ordenadas, la liquidacion de honorarios a guias y reportes gerenciales de gestion.

La solucion esta construida bajo el patron arquitectonico Modelo-Vista-Controlador (MVC), asegurando bajo acoplamiento y alta cohesion mediante la aplicacion rigurosa de principios SOLID y patrones GRASP.

---

## Requisitos del Entorno

* Java Development Kit (JDK): Version 17 o superior.


* IDE recomendado: IntelliJ IDEA, Eclipse o NetBeans.
* Codificacion: UTF-8.
* Librerias externas: Ninguna (desarrollado con APIs estandar de Java: `java.time`, `java.util`, `java.io`).



---

## Estructura del Proyecto

* `src/excepciones`: Excepciones propias de reglas de negocio (`ReglaNegocioException`, `CupoExcedidoException`).


* `src/modelo/clasesAbstractas`: Clases base del dominio (`Tour`, `Empleado`).


* `src/modelo/enums`: Enumeraciones del sistema (`CategoriaTour`, `EstadoReserva`, `EstadoSalida`, `MedioPago`, `Temporada`, `TipoPasajero`, `TipoServicio`, `TipoVehiculo`).


* `src/modelo/estructura`: Entidades centrales y control temporal (`Agencia`, `Reloj`).


* `src/modelo/interfaces`: Contratos para promociones y cancelaciones (`PoliticaCancelacion`, `Promocion`).


* `src/modelo/personasRoles`: Actores del dominio (`Cliente`, `Pasajero`, `Guia`, `Vendedor`, `Administrador`, `CoordinadorOperaciones`, `Proveedor`).


* `src/modelo/politicasPromos`: Implementaciones polimorficas de cancelacion y promociones vigentes.


* `src/modelo/reservas`: Logica de calculo, pagos, notas de ajuste y reservas (`Reserva`, `CalculadoraTarifa`, `Pago`, `NotaDeAjuste`, `LiquidacionGuia`, `Dinero`).


* `src/modelo/toursEspecificos`: Subclases concretas (`TourAventura`, `TourCultural`, `TourNaturaleza`).


* `src/modelo/viajesServicios`: Logica operativa (`Salida`, `Actividad`, `TarifaTemporada`, `Vehiculo`, `ServicioAdicional`).


* `src/modelo`: Clase utilitaria de validaciones defensivas (`Validaciones`).


* `src/persistencia`: Gestor de serializacion en archivos binarios (`GestorPersistencia`).


* `src/presentacion/controladores`: Controladores del patron MVC (`ControladorReserva`, `ControladorOperaciones`, `ControladorAdministracion`).


* `src/reportes`: Modulo de generacion y calculo de metricas operativas (`Reportes`).


* `src/PruebaSistema.java`: Clase ejecutable principal que simula y valida el ciclo completo de casos de uso y reglas de negocio.



---

## Instrucciones de Instalacion y Ejecucion

1. Clonar el repositorio localmente mediante consola:
`git clone [https://github.com/dolocerrudo16/tpo-poo.git](https://github.com/dolocerrudo16/tpo-poo.git)`
2. Abrir el proyecto en IntelliJ IDEA (u otro IDE compatible con Java).
3. Asegurarse de que el directorio `src` este configurado como "Sources Root" (clic derecho en `src` -> Mark Directory as -> Sources Root).
4. Configurar el Project SDK en Java 17 o superior dentro de File -> Project Structure -> Project.
5. Ejecutar la clase principal haciendo clic derecho en `src/PruebaSistema.java` y seleccionando "Run 'PruebaSistema.main()'".


6. La ejecucion mostrara por consola la simulacion integral, paso a paso, de los casos de uso, las reglas de negocio, los reportes gerenciales y la persistencia de datos.



---

## Datos de Prueba Precargados en la Ejecucion

La clase `PruebaSistema` inicializa y valida los siguientes datos concretos en memoria y en el archivo persistente `sistema_agencia.dat`:

* **Fecha de simulacion (Reloj):** 01/10/2026.
* **Personal y Recursos:**
* Guia: Germana Mendoza (Legajo 1, DNI 22334455, Honorario diario ARS 20.000,00, Habilitacion en categoria Aventura).
* Vendedor: Dolores Ventas (Legajo 2, DNI 33445566, Comision 5%).
* Vehiculo: Combi patente AB123CD (Capacidad 15 pasajeros).


* **Catalogo de Tours:**
* Tour Aventura: "Trekking Glaciar" (Codigo T-AV01, Duracion 5 dias, Cupo min 2 / max 8, Dificultad Alta, Edad minima 18 anos).
* Tarifa Base Temporada Alta: ARS 100.000,00.


* **Salida Programada:**
* Fecha de salida: 15/12/2026 (75 dias de anticipacion respecto a la fecha del sistema).
* Estado inicial: PROGRAMADA (8 cupos disponibles).


* **Reserva Demo:**
* Cliente titular: Carlos (DNI 111, Carlos@mail.com).
* Pasajeros incluidos: Carlos (Adulto, tarifa 100%) y Abuelo (Jubilado, 10% descuento por RN07).
* Descuento aplicado: Promocion Anticipada (15% OFF sobre tarifa base por compra con mas de 60 dias de anticipacion).
* Seguro obligatorio: ARS 5.000,00 por pasajero al ser un tour de aventura (CU06).
* Total final liquidado: ARS 171.500,00.


* **Pagos Registrados:**
* Pago de sena: ARS 60.000,00 en 3 cuotas con Tarjeta (7% de recargo por financiacion). La reserva cambia su estado a CONFIRMADA al superar el 30% del total.


* **Politica de Cancelacion (CU13):**
* Cancelacion flexible: al superar los 30 dias de anticipacion (75 dias restantes), se procesa la devolucion del 90% de lo pagado (ARS 54.000,00) y se emite la respectiva Nota de Ajuste con liberacion automatica de cupos.


* **Control de Restricciones y Excepciones:**
* Validacion de edad minima en tours de aventura (captura controlada de `ReglaNegocioException` al intentar asignar un menor de 10 anos a una actividad con restriccion de 18 anos).


* **Persistencia de Objetos:**
* Guardado binario y recuperacion integra del estado de la agencia mediante `GestorPersistencia` en el archivo `sistema_agencia.dat`.
