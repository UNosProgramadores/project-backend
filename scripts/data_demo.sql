

-- =========================
-- TABLA: rol
-- =========================
INSERT INTO rol (id, nombre, descripcion) VALUES
(1, 'Administrador', 'Control total del sistema'),
(2, 'Operador', 'Gestiona ingresos y pagos'),
(3, 'Supervisor', 'Supervisa operaciones'),
(4, 'Cliente', 'Usuario cliente del parqueadero'),
(5, 'Seguridad', 'Control de acceso'),
(6, 'Contador', 'Gestion financiera'),
(7, 'Tecnico', 'Mantenimiento del sistema'),
(8, 'Invitado', 'Acceso temporal'),
(9, 'Gerente', 'Administra sedes'),
(10, 'Auditor', 'Revisa movimientos');

-- =========================
-- TABLA: usuario
-- =========================
INSERT INTO usuario (id, cedula, nombre, celular, nombre_usuario, contrasena_hash, rol_id, activo, fecha_registro) VALUES
(1, '100000001', 'Juan Perez', '3001111111', 'juanp', 'hash1', 1, true, '2026-01-01 08:00:00'),
(2, '100000002', 'Maria Gomez', '3002222222', 'mariag', 'hash2', 2, true, '2026-01-02 09:00:00'),
(3, '100000003', 'Carlos Ruiz', '3003333333', 'carlosr', 'hash3', 3, true, '2026-01-03 10:00:00'),
(4, '100000004', 'Laura Diaz', '3004444444', 'laurad', 'hash4', 4, true, '2026-01-04 11:00:00'),
(5, '100000005', 'Andres Mora', '3005555555', 'andresm', 'hash5', 5, true, '2026-01-05 12:00:00'),
(6, '100000006', 'Paula Rojas', '3006666666', 'paular', 'hash6', 6, true, '2026-01-06 13:00:00'),
(7, '100000007', 'Sofia Torres', '3007777777', 'sofiat', 'hash7', 7, true, '2026-01-07 14:00:00'),
(8, '100000008', 'David Castro', '3008888888', 'davidc', 'hash8', 8, false, '2026-01-08 15:00:00'),
(9, '100000009', 'Camila Vega', '3009999999', 'camilav', 'hash9', 9, true, '2026-01-09 16:00:00'),
(10, '100000010', 'Felipe Luna', '3010000000', 'felipel', 'hash10', 10, true, '2026-01-10 17:00:00');

-- =========================
-- TABLA: tipo_vehiculo
-- =========================
INSERT INTO tipo_vehiculo (id, nombre, requiere_placa) VALUES
(1, 'Carro', true),
(2, 'Moto', true),
(3, 'Bicicleta', false),
(4, 'Patineta', false),
(5, 'Camioneta', true),
(6, 'Bus', true),
(7, 'Camion', true),
(8, 'Scooter', false),
(9, 'Taxi', true),
(10, 'Van', true);

-- =========================
-- TABLA: vehiculo
-- =========================
INSERT INTO vehiculo (id, tipo_vehiculo_id, placa, registro_bici, propietario_id, marca, modelo, color, activo) VALUES
(1, 1, 'ABC123', NULL, 1, 'Toyota', 'Corolla', 'Blanco', true),
(2, 2, 'MOT456', NULL, 2, 'Yamaha', 'FZ', 'Negro', true),
(3, 3, NULL, 'BICI001', 3, 'GW', 'Mountain', 'Rojo', true),
(4, 1, 'DEF789', NULL, 4, 'Mazda', '3', 'Azul', true),
(5, 5, 'GHI321', NULL, 5, 'Ford', 'Escape', 'Gris', true),
(6, 6, 'BUS654', NULL, 6, 'Mercedes', 'Sprinter', 'Blanco', true),
(7, 7, 'CAM987', NULL, 7, 'Chevrolet', 'NHR', 'Amarillo', true),
(8, 8, NULL, 'SCOOT01', 8, 'Xiaomi', 'M365', 'Negro', false),
(9, 9, 'TAX111', NULL, 9, 'Kia', 'Rio', 'Verde', true),
(10, 10, 'VAN222', NULL, 10, 'Renault', 'Kangoo', 'Plateado', true);

-- =========================
-- TABLA: parqueadero
-- =========================
INSERT INTO parqueadero (id, nombre, direccion, filas, columnas, asignacion_automatica, descuentos_activos) VALUES
(1, 'Parking Norte', 'Calle 100 #10-20', 5, 10, true, true),
(2, 'Parking Centro', 'Carrera 7 #20-30', 4, 8, true, false),
(3, 'Parking Sur', 'Avenida 1 #50-40', 6, 12, false, true),
(4, 'Parking Plaza', 'Calle 80 #15-50', 3, 6, true, true),
(5, 'Parking Mall', 'Centro Comercial Plaza', 7, 14, false, false),
(6, 'Parking Torre', 'Carrera 15 #90-10', 5, 5, true, true),
(7, 'Parking Ejecutivo', 'Zona Empresarial', 8, 16, true, false),
(8, 'Parking Industrial', 'Zona Industrial', 10, 20, false, true),
(9, 'Parking Universidad', 'Campus Central', 6, 10, true, true),
(10, 'Parking Aeropuerto', 'Terminal Nacional', 12, 25, true, true);

-- =========================
-- TABLA: celda
-- =========================
INSERT INTO celda (id, parqueadero_id, fila, columna, tipo_celda, estado, tipo_vehiculo_id, reservada_trabajador) VALUES
(1, 1, 1, 1, 'General', 'Libre', 1, false),
(2, 1, 1, 2, 'Moto', 'Ocupada', 2, false),
(3, 2, 2, 1, 'Bicicleta', 'Libre', 3, false),
(4, 2, 2, 2, 'General', 'Mantenimiento', 1, true),
(5, 3, 3, 1, 'Camioneta', 'Libre', 5, false),
(6, 4, 1, 3, 'Bus', 'Ocupada', 6, false),
(7, 5, 4, 4, 'Camion', 'Libre', 7, false),
(8, 6, 2, 5, 'Scooter', 'Libre', 8, false),
(9, 7, 5, 1, 'Taxi', 'Ocupada', 9, true),
(10, 8, 6, 2, 'Van', 'Libre', 10, false);

-- =========================
-- TABLA: tarifa
-- =========================
INSERT INTO tarifa (id, parqueadero_id, tipo_vehiculo_id, tipo_tarifa, valor, fecha_inicio, fecha_fin, activa) VALUES
(1, 1, 1, 'Hora', 5000, '2026-01-01 00:00:00', '2026-12-31 23:59:59', true),
(2, 1, 2, 'Hora', 3000, '2026-01-01 00:00:00', '2026-12-31 23:59:59', true),
(3, 2, 3, 'Dia', 2000, '2026-01-01 00:00:00', '2026-12-31 23:59:59', true),
(4, 3, 1, 'Hora', 5500, '2026-01-01 00:00:00', '2026-12-31 23:59:59', true),
(5, 4, 5, 'Hora', 7000, '2026-01-01 00:00:00', '2026-12-31 23:59:59', false),
(6, 5, 6, 'Dia', 25000, '2026-01-01 00:00:00', '2026-12-31 23:59:59', true),
(7, 6, 7, 'Dia', 30000, '2026-01-01 00:00:00', '2026-12-31 23:59:59', true),
(8, 7, 8, 'Hora', 1500, '2026-01-01 00:00:00', '2026-12-31 23:59:59', true),
(9, 8, 9, 'Hora', 6000, '2026-01-01 00:00:00', '2026-12-31 23:59:59', true),
(10, 9, 10, 'Dia', 18000, '2026-01-01 00:00:00', '2026-12-31 23:59:59', true);

-- =========================
-- TABLA: configuracion_descuento
-- =========================
INSERT INTO configuracion_descuento (id, parqueadero_id, activo, monto_minimo_factura_externa, numero_minimo_visitas, porcentaje_descuento, fecha_inicio, fecha_fin) VALUES
(1, 1, true, 50000, 5, 10, '2026-01-01 00:00:00', '2026-06-30 23:59:59'),
(2, 2, true, 70000, 7, 15, '2026-01-01 00:00:00', '2026-06-30 23:59:59'),
(3, 3, false, 30000, 3, 5, '2026-01-01 00:00:00', '2026-06-30 23:59:59'),
(4, 4, true, 100000, 10, 20, '2026-01-01 00:00:00', '2026-06-30 23:59:59'),
(5, 5, false, 25000, 2, 3, '2026-01-01 00:00:00', '2026-06-30 23:59:59'),
(6, 6, true, 40000, 4, 8, '2026-01-01 00:00:00', '2026-06-30 23:59:59'),
(7, 7, true, 90000, 9, 18, '2026-01-01 00:00:00', '2026-06-30 23:59:59'),
(8, 8, false, 15000, 1, 2, '2026-01-01 00:00:00', '2026-06-30 23:59:59'),
(9, 9, true, 60000, 6, 12, '2026-01-01 00:00:00', '2026-06-30 23:59:59'),
(10, 10, true, 120000, 12, 25, '2026-01-01 00:00:00', '2026-06-30 23:59:59');

-- =========================
-- TABLA: ingreso
-- =========================
INSERT INTO ingreso (id, vehiculo_id, celda_id, fecha_hora_ingreso, fecha_hora_salida, tiempo_permanencia_min, estado) VALUES
(1, 1, 1, '2026-02-01 08:00:00', '2026-02-01 10:00:00', 120, 'Finalizado'),
(2, 2, 2, '2026-02-01 09:00:00', '2026-02-01 11:30:00', 150, 'Finalizado'),
(3, 3, 3, '2026-02-01 10:00:00', NULL, NULL, 'Activo'),
(4, 4, 4, '2026-02-01 07:30:00', '2026-02-01 09:00:00', 90, 'Finalizado'),
(5, 5, 5, '2026-02-01 12:00:00', NULL, NULL, 'Activo'),
(6, 6, 6, '2026-02-01 13:00:00', '2026-02-01 18:00:00', 300, 'Finalizado'),
(7, 7, 7, '2026-02-01 06:00:00', '2026-02-01 08:00:00', 120, 'Finalizado'),
(8, 8, 8, '2026-02-01 15:00:00', NULL, NULL, 'Activo'),
(9, 9, 9, '2026-02-01 16:00:00', '2026-02-01 17:00:00', 60, 'Finalizado'),
(10, 10, 10, '2026-02-01 18:00:00', NULL, NULL, 'Activo');

-- =========================
-- TABLA: pago
-- =========================
INSERT INTO pago (id, ingreso_id, subtotal, porcentaje_descuento, valor_descuento, total_pagado, metodo_pago, fecha_pago) VALUES
(1, 1, 10000, 10, 1000, 9000, 'Efectivo', '2026-02-01 10:05:00'),
(2, 2, 15000, 0, 0, 15000, 'Tarjeta', '2026-02-01 11:35:00'),
(3, 3, 5000, 5, 250, 4750, 'Transferencia', '2026-02-01 12:00:00'),
(4, 4, 8000, 0, 0, 8000, 'Efectivo', '2026-02-01 09:05:00'),
(5, 5, 12000, 15, 1800, 10200, 'Tarjeta', '2026-02-01 14:00:00'),
(6, 6, 30000, 20, 6000, 24000, 'Transferencia', '2026-02-01 18:10:00'),
(7, 7, 9000, 0, 0, 9000, 'Efectivo', '2026-02-01 08:05:00'),
(8, 8, 4000, 5, 200, 3800, 'Tarjeta', '2026-02-01 16:00:00'),
(9, 9, 6000, 10, 600, 5400, 'Efectivo', '2026-02-01 17:05:00'),
(10, 10, 18000, 25, 4500, 13500, 'Transferencia', '2026-02-01 19:00:00');

-- =========================
-- TABLA: factura
-- =========================
INSERT INTO factura (id, pago_id, numero_factura, fecha_emision) VALUES
(1, 1, 'FAC-1001', '2026-02-01 10:10:00'),
(2, 2, 'FAC-1002', '2026-02-01 11:40:00'),
(3, 3, 'FAC-1003', '2026-02-01 12:05:00'),
(4, 4, 'FAC-1004', '2026-02-01 09:10:00'),
(5, 5, 'FAC-1005', '2026-02-01 14:05:00'),
(6, 6, 'FAC-1006', '2026-02-01 18:15:00'),
(7, 7, 'FAC-1007', '2026-02-01 08:10:00'),
(8, 8, 'FAC-1008', '2026-02-01 16:05:00'),
(9, 9, 'FAC-1009', '2026-02-01 17:10:00'),
(10, 10, 'FAC-1010', '2026-02-01 19:05:00');
