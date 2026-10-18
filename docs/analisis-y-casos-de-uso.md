Propuesta elegida: B - Agencia de turismo (Rumbo Sur)
Integrantes: Cerrudo Dolores y Mendoza Germana
1. Actores del Sistema
En el sistema de la agencia operan tres roles internos principales, de los cuales se registra legajo, nombre, documento y teléfono:
Administrador: Se encarga de dar de alta y modificar los tours del catálogo (con su itinerario, duración, cupos, categorías y tarifas según temporada), gestionar los proveedores y el personal, liquidar los sueldos y honorarios de los guías, y sacar los reportes estadísticos de ocupación, ventas por vendedor, ingresos por tour y cancelaciones.
Coordinador de Operaciones: Maneja la programación operativa de las salidas, asignando a cada una el guía habilitado y el vehículo correspondiente. También emite la lista de pasajeros antes de viajar, da inicio a la salida y registra el cierre cuando finaliza.
Vendedor: Es quien atiende al cliente. Registra sus datos, arma las reservas asociándolas a una salida y a uno o más pasajeros, suma los servicios opcionales, anota los pagos de seña y saldo, y maneja las modificaciones (como cambiar un pasajero) o las cancelaciones con sus respectivas devoluciones. (Nota: Clientes, pasajeros, guías y proveedores funcionan como entidades del negocio cuyos datos son administrados en el sistema por los roles anteriores).
2. Requerimientos Funcionales (RF)
RF01 - Gestión de catálogo de tours y proveedores: Permite registrar y modificar tours con su código, nombre, descripción, días de duración, categoría (aventura, cultural o naturaleza), cupos mínimo y máximo, las actividades diarias del itinerario (con horario, lugar y proveedor externo si corresponde) y las tarifas base por temporada con su vigencia.
RF02 - Programación de salidas: Permite generar salidas concretas de un tour indicando fecha de inicio, fecha de fin, cupo máximo y dejándolas con estado inicial «programada».
RF03 - Asignación de recursos: Asigna un guía y un vehículo a cada salida. El sistema valida automáticamente que el guía esté habilitado para la categoría del tour y no tenga superposición de fechas, y que la capacidad del vehículo cubra el cupo de la salida.
RF04 - Registro de clientes y creación de reservas: Da de alta clientes y crea reservas vinculadas a un titular, un vendedor y una salida con lugar disponible, cargando los datos de los pasajeros (documento, fecha de nacimiento, tipo, contacto de emergencia y restricciones médicas).
RF05 - Contratación de servicios adicionales: Permite agregar opcionales (excursiones, traslados, seguro de viaje) por pasajero. El seguro de viaje es obligatorio si el tour es de aventura.
RF06 - Cálculo de precios y promociones: Calcula el total de la reserva según la temporada, la categoría del pasajero (adulto, menor o jubilado) y aplica de forma automática la promoción más conveniente entre «reserva anticipada» y «grupo».
RF07 - Registro de pagos: Controla el pago de la seña (mínimo 30% para confirmar), los pagos de saldo en distintos medios y aplica un recargo del 7% si se paga con tarjeta en cuotas.
RF08 - Modificaciones y cancelaciones: Deja reemplazar pasajeros sin cargo hasta 3 días antes (si cumplen los requisitos) o cancelar la reserva aplicando la escala de devolución correspondiente, generando una nota de ajuste sin borrar la reserva de la base.
RF09 - Operación de la salida: Emite la lista de pasajeros obligatoria para arrancar, controla que tenga guía asignado para pasar a estado EN_CURSO, y evalúa 7 días antes si se cancela por no alcanzar el cupo mínimo.
RF10 - Liquidación a guías: Calcula el pago al guía por cada salida finalizada sumando el monto diario fijo más el 5% de lo recaudado en servicios adicionales de esa salida.
RF11 - Emisión de reportes: Genera los informes gerenciales de ocupación, ventas por vendedor, ingresos y cancelaciones.
3. Reglas de Negocio (RN)
RN01 a RN06 (Tours y Salidas): Los tours se dividen en aventura (exigen edad mínima y dificultad), cultural y naturaleza. Las salidas pasan por 6 estados (programada, confirmada, completa, en curso, finalizada, cancelada). Si 7 días antes no se llega al cupo mínimo, se cancela y se devuelve el 100%. Al llenarse el cupo, pasa a completa. Los guías no pueden superponer fechas ni guiar categorías para las que no están habilitados, y el vehículo debe tener asientos suficientes.
RN07 a RN11 (Reservas y Pagos): Los adultos pagan 100%, menores de 12 el 60%, menores de 3 no pagan pero ocupan lugar, y jubilados tienen 10% de descuento. Las promos de anticipación (>60 días, 15%) y grupo (6 personas, 10%) no se acumulan, se aplica la mejor. El seguro de aventura es obligatorio. La seña es del 30% para confirmar, y el saldo debe pagarse hasta 10 días antes o se anula. Las cuotas con tarjeta tienen 7% de recargo.
RN12 a RN17 (Cancelaciones y Liquidaciones): Las devoluciones al cliente varían según los días de anticipación (90% si es >30 días, 50% entre 29 y 15, 20% entre 14 y 7, y 0% con menos de una semana). Si cancela la agencia se devuelve el 100%. Se puede cambiar un pasajero hasta 3 días antes sin cargo. No se puede iniciar una salida sin lista de pasajeros ni guía. El honorario del guía es el diario más el 5% de adicionales. Las reservas pagadas no se borran, se corrigen con notas de ajuste.
4. Supuestos Adoptados
S01: Los menores de 3 años (que no pagan pasaje) ocupan un asiento físico en el vehículo y un lugar en el cupo por cuestiones de seguridad y seguros de ruta.
S02: Los descuentos por promociones se aplican únicamente sobre la tarifa base del tour de los pasajeros, calculándose los adicionales por separado.
S03: Para reemplazar a un pasajero sin cargo, este debe pertenecer al mismo tipo tarifario (ej. adulto por adulto) o abonar la diferencia si cambia de categoría.
S04: Cada vendedor tiene configurada una comisión estimada del 3% sobre el total neto de las reservas que gestiona y confirma.
S05: El recargo del 7% con tarjeta aplica únicamente cuando se financia en 2 o más cuotas; un pago con tarjeta en un solo pago no lleva recargo.
5. Descripciones de Casos de Uso 
CU01 - Administrar catálogo de tours y tarifas
Actor: Administrador.
Precondición: El administrador inició sesión en el sistema y los proveedores necesarios están registrados.
Flujo principal:
El administrador selecciona la opción de dar de alta o modificar un tour.
Ingresa el código, nombre, descripción, duración en días, categoría (aventura, cultural o naturaleza) y cupo mínimo y máximo.
Si la categoría es aventura, ingresa obligatoriamente la edad mínima y el nivel de dificultad.
Carga las actividades del itinerario indicando día, orden, horario, descripción, lugar y el proveedor asociado si aplica.
Define las tarifas base por temporada (baja, media, alta) con sus fechas de vigencia.
El sistema valida los datos y guarda el tour en el catálogo.
Flujos alternativos:
3a. Falta edad mínima o nivel de dificultad en tour de aventura: El sistema rechaza el registro y muestra un mensaje de error.
CU02 - Programar salida de tour
Actor: Coordinador de Operaciones.
Precondición: El tour seleccionado debe existir en el catálogo.
Flujo principal:
El coordinador selecciona un tour y solicita programar una nueva salida.
Ingresa la fecha de inicio, la fecha de fin y el cupo máximo.
El sistema crea la salida en estado PROGRAMADA con el cupo disponible inicial igual al cupo máximo.
Flujos alternativos:
2a. Fechas inválidas o inconsistentes con la duración del tour: El sistema rechaza la programación y solicita corregir los datos.
CU03 - Crear reserva
Actor: Vendedor.
Precondición: El cliente titular está registrado y la salida se encuentra en estado PROGRAMADA o CONFIRMADA.
Flujo principal:
El vendedor selecciona el cliente, la salida y carga los datos de los pasajeros (nombre, documento, fecha de nacimiento, tipo, contacto de emergencia y restricciones).
El sistema verifica que la salida posea cupo disponible suficiente para la cantidad de pasajeros.
Si el tour es de categoría AVENTURA, el sistema valida la edad mínima de cada pasajero y asigna obligatoriamente el seguro de viaje.
El sistema calcula el precio total aplicando las tarifas de temporada, los descuentos por tipo de pasajero y la promoción más favorable (anticipada o grupo).
El sistema registra la reserva en estado PENDIENTE, descuenta los lugares del cupo y muestra el importe total junto con la seña requerida del 30%.
Flujos alternativos:
2a. Cupo insuficiente: El sistema rechaza la reserva indicando los lugares disponibles.
3a. Incumplimiento de edad mínima: El sistema bloquea la operación y avisa qué pasajero no cumple el requisito.
CU04 - Registrar pago y seña
Actor: Vendedor.
Precondición: Existe una reserva en estado PENDIENTE o CONFIRMADA con saldo pendiente.
Flujo principal:
El vendedor busca la reserva e ingresa el importe, la fecha y el medio de pago (efectivo, transferencia o tarjeta con sus cuotas).
Si el pago es con tarjeta en cuotas, el sistema calcula y aplica automáticamente el 7% de recargo.
El sistema registra el pago asociado a la reserva.
Si la reserva estaba PENDIENTE y el acumulado alcanza el 30% de seña, pasa a estado CONFIRMADA.
El sistema actualiza el estado de la salida si se alcanzan los cupos mínimo o máximo.
Flujos alternativos:
4a. El pago inicial no cubre el 30%: La reserva se mantiene en estado PENDIENTE registrando el pago parcial.
CU05 - Gestionar operación y liquidar honorarios
Actor: Coordinador de Operaciones / Administrador.
Precondición: La salida debe tener guía y vehículo asignados.
Flujo principal:
El coordinador emite la lista de pasajeros antes de partir y da inicio a la salida (EN_CURSO).
Al finalizar, registra el cierre de la salida (FINALIZADA).
El administrador selecciona la salida finalizada para ejecutar la liquidación de honorarios del guía (monto fijo por día más el 5% de los adicionales vendidos).
Flujos alternativos:
1a. Intento de iniciar sin guía o sin lista de pasajeros: El sistema bloquea el cambio de estado a EN_CURSO.


