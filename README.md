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

La clase `PruebaSistema` inicializa y valida los siguientes datos concretos en memoria y en el archivo binario persistente `sistema_agencia.dat`:

* **Fecha de simulacion (Reloj):** 01/10/2026.
* **Personal y Recursos:**
* Guia: Germana Mendoza (Legajo 1, DNI 22334455, Honorario diario ARS 20.000,00, Habilitada en categorias Aventura y Cultural).
* Vendedor: Dolores Ventas (Legajo 2, DNI 33445566, Comision 5%).
* Vehiculo: Combi patente AB123CD (Capacidad 15 pasajeros).


* **Catalogo de Tours:**
* Tour Aventura: "Trekking Glaciar" (Codigo T-AV01, Duracion 5 dias, Cupo min 2 / max 8, Dificultad Alta, Edad minima 18 anos). Tarifa Base Temporada Alta: ARS 100.000,00.
* Tour Cultural: "Circuito Historico y Museos" (Codigo T-CUL01, Duracion 3 dias, Cupo min 1 / max 10). Tarifa Base Temporada Media: ARS 40.000,00.


* **Salida #1 (Tour Aventura):**
* Fechas: del 15/12/2026 al 19/12/2026 (75 dias de anticipacion respecto a la fecha del sistema).
* Recursos asignados: Guia Germana Mendoza y Combi AB123CD.
* Reserva #1: Titular Carlos Gomez (DNI 111). Pasajeros: Carlos Gomez (Adulto, tarifa base ARS 100.000,00) y Abuelo Gomez (Jubilado, 10% de descuento por RN07 = ARS 90.000,00). Subtotal base: ARS 190.000,00.
* Promocion y adicionales: 15% OFF por compra anticipada (>60 dias) = descuento de ARS 28.500,00. Mas 2 seguros de viaje obligatorios por tour de aventura (ARS 5.000,00 c/u) = ARS 10.000,00. Total calculado: ARS 171.500,00.
* Pago registrado: ARS 60.000,00 en 3 cuotas con Tarjeta (7% de recargo por financiacion = ARS 4.200,00). Estado pasa a CONFIRMADA por superar el 30% de sena.
* Cancelacion (CU13): Solicitud por motivos personales. Politica flexible a 75 dias devuelve el 90% de lo pagado (ARS 54.000,00) y retiene ARS 6.000,00 con emision de Nota de Ajuste y liberacion total del cupo (0/8 ocupados).


* **Control de Restricciones (LSP y Excepciones):**
* Captura controlada de `ReglaNegocioException` al intentar inscribir a un menor de 10 anos en el Tour Aventura que exige un minimo de 18 anos.


* **Salida #2 (Tour Cultural - Flujo Operativo Completo):**
* Fechas: del 20/11/2026 al 22/11/2026.
* Reserva #2: Titular Marta Fernandez (DNI 555) con 2 pasajeros adultos (Marta y Esteban Fernandez). Total: ARS 80.000,00 abonado 100% en efectivo.
* Modificacion de Reserva (CU12): Reemplazo de pasajero Esteban Fernandez por Julieta Gomez respetando la anticipacion minima de 3 dias.
* Operacion de Salida (CU09): Emision de lista de pasajeros ordenada alfabeticamente mediante `Comparable` (Julieta Gomez, luego Marta Fernandez). Transicion de estados de salida: PROGRAMADA -> EN_CURSO -> FINALIZADA.
* Liquidacion a Guias (CU10): Calculo de honorarios para Germana Mendoza sobre la Salida #2 finalizada (3 dias a ARS 20.000,00 diarios = ARS 60.000,00 liquidados).


* **Reportes Gerenciales (CU11):**
* Ocupacion por Salida (Salida #1: 0/8 por cancelacion; Salida #2: 2/10).
* Cancelaciones del periodo (1 reserva cancelada).
* Ventas por Vendedor (Dolores Ventas: 1 reserva confirmada por ARS 80.000,00 con comision del 5% = ARS 4.000,00).
* Ingresos por Tour (ARS 80.000,00 totales percibidos).


* **Persistencia Binaria:**
* Serializacion y deserializacion integral del objeto raiz `Agencia` mediante `GestorPersistencia` en el archivo `sistema_agencia.dat`, recuperando las 2 salidas registradas sin inconsistencias de estado.
