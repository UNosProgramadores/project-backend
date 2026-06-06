-- ============================================================
-- ParKing - Datos de demostración
-- ============================================================

-- ============================================================
-- ROL
-- ============================================================
INSERT INTO rol (id, nombre, descripcion) VALUES
                                              (1, 'administrador', 'Control total del sistema'),
                                              (2, 'trabajador',    'Operación diaria del parqueadero'),
                                              (3, 'cliente',       'Usuario del parqueadero');


-- ============================================================
-- TIPO_VEHICULO
-- ============================================================
INSERT INTO tipo_vehiculo (id, nombre, requiere_placa) VALUES
                                                           (1, 'carro',      true),
                                                           (2, 'moto',       true),
                                                           (3, 'bicicleta',  false);


-- ============================================================
-- USUARIO
-- ============================================================
-- Contraseñas (SHA-256):
--   admin123     → a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3
--   trabajo456   → 4b5f29b8c4aa63e5dcf3bfa72d65bfb9c9f8a3d1e2c7b4a1f0e9d8c7b6a5f4e3
--   cliente789   → 9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08

INSERT INTO usuario (id, cedula, nombre, celular, nombre_usuario, contrasena_hash, rol_id, activo, intentos_fallidos, bloqueado, fecha_registro) VALUES
                                                                                                                                                     (1, '10000001', 'Carlos Mendoza',   '3101234567', 'cmendoza',   'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 1, true,  0, false, '2024-01-15 08:00:00'),
                                                                                                                                                     (2, '10000002', 'Laura Gómez',      '3209876543', 'lgomez',     '4b5f29b8c4aa63e5dcf3bfa72d65bfb9c9f8a3d1e2c7b4a1f0e9d8c7b6a5f4e3', 2, true,  0, false, '2024-01-16 09:00:00'),
                                                                                                                                                     (3, '10000003', 'Pedro Ramírez',    '3154445566', 'ppedrram',   '4b5f29b8c4aa63e5dcf3bfa72d65bfb9c9f8a3d1e2c7b4a1f0e9d8c7b6a5f4e3', 2, true,  0, false, '2024-01-17 09:30:00'),
                                                                                                                                                     (4, '10000004', 'Ana Torres',       '3001112233', 'anatorres',  '9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08', 3, true,  0, false, '2024-02-01 10:00:00'),
                                                                                                                                                     (5, '10000005', 'Luis Herrera',     '3177778899', 'lherrera',   '9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08', 3, true,  0, false, '2024-02-05 11:00:00'),
                                                                                                                                                     (6, '10000006', 'Sofía Vargas',     '3123334455', 'svargas',    '9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08', 3, true,  3, false, '2024-02-10 12:00:00'),
                                                                                                                                                     (7, '10000007', 'Mateo Castillo',   '3056667788', 'mcastillo',  '9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08', 3, false, 5, true,  '2024-02-12 08:00:00');
-- usuario 6: 3 intentos fallidos pero aún no bloqueado
-- usuario 7: 5 intentos fallidos, cuenta bloqueada, inactivo


-- ============================================================
-- VEHICULO
-- ============================================================
INSERT INTO vehiculo (id, tipo_vehiculo_id, placa, registro_bici, propietario_id, marca, modelo, color, activo) VALUES
-- Carros
(1, 1, 'ABC123', NULL, 4, 'Chevrolet', 'Spark',   'Rojo',    true),
(2, 1, 'XYZ789', NULL, 5, 'Renault',   'Sandero', 'Blanco',  true),
-- Motos
(3, 2, 'MTO456', NULL, 4, 'Honda',     'CB190',   'Negro',   true),
(4, 2, 'MTO999', NULL, 6, 'Yamaha',    'FZ25',    'Azul',    true),
-- Bicicletas (sin placa, con registro_bici, propietario puede ser null si no está registrado)
(5, 3, NULL, 'BICI-001', 5,    'Trek',      'FX3',     'Verde',   true),
(6, 3, NULL, 'BICI-002', NULL, 'Specialized','Allez',  'Naranja', true);
-- vehiculo 6: bicicleta sin propietario registrado en el sistema


-- ============================================================
-- PARQUEADERO
-- ============================================================
-- Nota: hora_apertura/cierre aquí usan solo la parte de hora (timestamp por definición de la tabla)
INSERT INTO parqueadero (id, nombre, direccion, hora_apertura, hora_cierre, filas, columnas, asignacion_automatica, descuentos_activos) VALUES
    (1, 'ParKing Centro', 'Calle 45 # 12-30, Bogotá', '1970-01-01 06:00:00', '1970-01-01 22:00:00', 4, 5, true, true);
-- 4 filas x 5 columnas = 20 celdas en total


-- ============================================================
-- CELDA (4 filas x 5 columnas = 20 celdas)
-- Distribución:
--   Fila 0: celdas de tránsito (entrada/salida)
--   Filas 1-3, columnas 0-1: espacios para carros (6 espacios)
--   Filas 1-3, columnas 2-3: espacios para motos (6 espacios)
--   Filas 1-3, columna 4: espacios para bicicletas (3 espacios)
--   Fila 3, columna 0: reservada para trabajador (carro)
-- ============================================================
INSERT INTO celda (id, parqueadero_id, fila, columna, nombre, tipo_celda, estado, tipo_vehiculo_id, reservada_trabajador) VALUES
-- Fila 0: tránsito
(1,  1, 0, 0, 'T-00', 'transito', 'disponible', NULL, false),
(2,  1, 0, 1, 'T-01', 'transito', 'disponible', NULL, false),
(3,  1, 0, 2, 'T-02', 'transito', 'disponible', NULL, false),
(4,  1, 0, 3, 'T-03', 'transito', 'disponible', NULL, false),
(5,  1, 0, 4, 'T-04', 'transito', 'disponible', NULL, false),
-- Fila 1: carros col 0-1, motos col 2-3, bici col 4
(6,  1, 1, 0, 'C-10', 'parqueo', 'ocupado',     1, false),
(7,  1, 1, 1, 'C-11', 'parqueo', 'disponible',  1, false),
(8,  1, 1, 2, 'M-12', 'parqueo', 'ocupado',     2, false),
(9,  1, 1, 3, 'M-13', 'parqueo', 'disponible',  2, false),
(10, 1, 1, 4, 'B-14', 'parqueo', 'disponible',  3, false),
-- Fila 2
(11, 1, 2, 0, 'C-20', 'parqueo', 'disponible',  1, false),
(12, 1, 2, 1, 'C-21', 'parqueo', 'ocupado',     1, false),
(13, 1, 2, 2, 'M-22', 'parqueo', 'disponible',  2, false),
(14, 1, 2, 3, 'M-23', 'parqueo', 'ocupado',     2, false),
(15, 1, 2, 4, 'B-24', 'parqueo', 'ocupado',     3, false),
-- Fila 3: col 0 reservada trabajador, resto normal
(16, 1, 3, 0, 'C-30', 'parqueo', 'ocupado',     1, true),   -- reservada trabajador
(17, 1, 3, 1, 'C-31', 'parqueo', 'disponible',  1, false),
(18, 1, 3, 2, 'M-32', 'parqueo', 'disponible',  2, false),
(19, 1, 3, 3, 'M-33', 'parqueo', 'disponible',  2, false),
(20, 1, 3, 4, 'B-34', 'parqueo', 'disponible',  3, false);


-- ============================================================
-- TARIFA
-- ============================================================
-- tipo_tarifa: 'por_minuto' o 'plena'
-- Tarifas vigentes (sin fecha_fin = activas indefinidamente)
INSERT INTO tarifa (id, parqueadero_id, tipo_vehiculo_id, tipo_tarifa, valor, fecha_inicio, fecha_fin, activa) VALUES
-- Tarifas actuales (activas)
(1, 1, 1, 'por_minuto', 150.00, '2024-01-01 00:00:00', NULL,                    true),   -- carro por minuto: $150
(2, 1, 1, 'plena',      8000.00,'2024-01-01 00:00:00', NULL,                    true),   -- carro tarifa plena: $8.000
(3, 1, 2, 'por_minuto', 80.00,  '2024-01-01 00:00:00', NULL,                    true),   -- moto por minuto: $80
(4, 1, 2, 'plena',      4000.00,'2024-01-01 00:00:00', NULL,                    true),   -- moto tarifa plena: $4.000
(5, 1, 3, 'por_minuto', 30.00,  '2024-01-01 00:00:00', NULL,                    true),   -- bici por minuto: $30
(6, 1, 3, 'plena',      2000.00,'2024-01-01 00:00:00', NULL,                    true),   -- bici tarifa plena: $2.000
-- Tarifa histórica (ya no activa, para demostrar RF_08)
(7, 1, 1, 'por_minuto', 120.00, '2023-01-01 00:00:00', '2023-12-31 23:59:59',  false);  -- carro antes era $120/min


-- ============================================================
-- CONFIGURACION_DESCUENTO
-- ============================================================
INSERT INTO configuracion_descuento (id, parqueadero_id, activo, monto_minimo_factura_externa, numero_minimo_visitas, porcentaje_descuento, fecha_inicio, fecha_fin) VALUES
-- Descuento activo: factura externa > $50.000 → 20% de descuento en parqueo
(1, 1, true,  50000.00, NULL, 20.00, '2024-03-01 00:00:00', NULL),
-- Descuento activo: más de 10 visitas → 10% de descuento
(2, 1, true,  NULL, 10,       10.00, '2024-03-01 00:00:00', NULL),
-- Descuento histórico (ya no activo)
(3, 1, false, 30000.00, NULL, 15.00, '2024-01-01 00:00:00', '2024-02-28 23:59:59');


-- ============================================================
-- REGISTRO (antes "ingreso")
-- Estados posibles: 'activo' (vehículo adentro), 'finalizado' (ya salió)
-- pago.ingreso_id referencia esta tabla
-- ============================================================
INSERT INTO registro (id, vehiculo_id, celda_id, fecha_hora_ingreso, fecha_hora_salida, tiempo_permanencia_min, estado) VALUES
-- Registros finalizados (histórico)
(1, 1, 6,  '2024-05-10 08:30:00', '2024-05-10 10:15:00', 105, 'finalizado'),  -- carro ABC123, 1h45m
(2, 3, 8,  '2024-05-10 09:00:00', '2024-05-10 09:45:00', 45,  'finalizado'),  -- moto MTO456, 45min
(3, 5, 15, '2024-05-11 07:00:00', '2024-05-11 08:30:00', 90,  'finalizado'),  -- bici BICI-001, 1h30m
(4, 2, 12, '2024-05-12 14:00:00', '2024-05-12 17:30:00', 210, 'finalizado'),  -- carro XYZ789, 3h30m
-- Registros activos (vehículos actualmente en el parqueadero)
(5, 4, 14, '2024-05-13 08:00:00', NULL, NULL, 'activo'),   -- moto MTO999, aún adentro
(6, 6, 16, '2024-05-13 07:30:00', NULL, NULL, 'activo');   -- bici BICI-002, aún adentro (celda reservada trabajador)


-- ============================================================
-- PAGO
-- pago.ingreso_id referencia registro.id
-- ============================================================
INSERT INTO pago (id, ingreso_id, subtotal, porcentaje_descuento, valor_descuento, total_pagado, metodo_pago, fecha_pago, factura_externa_ref) VALUES
                                                                                                                                                   (1, 1, 15750.00, 20.00, 3150.00, 12600.00, 'efectivo',  '2024-05-10 10:15:00', 'FACT-ALMACEN-0042'),  -- con descuento por factura externa
                                                                                                                                                   (2, 2, 3600.00,  0.00,  0.00,    3600.00,  'efectivo',  '2024-05-10 09:45:00', NULL),                  -- sin descuento
                                                                                                                                                   (3, 3, 2700.00,  10.00, 270.00,  2430.00,  'efectivo',  '2024-05-11 08:30:00', NULL),                  -- con descuento por visitas
                                                                                                                                                   (4, 4, 31500.00, 0.00,  0.00,    31500.00, 'efectivo',  '2024-05-12 17:30:00', NULL);                  -- sin descuento
-- registros 5 y 6 aún no tienen pago (vehículos adentro)


-- ============================================================
-- FACTURA
-- factura.pago_id referencia pago.id
-- ============================================================
INSERT INTO factura (id, pago_id, numero_factura, fecha_emision) VALUES
                                                                     (1, 1, 'FAC-2024-0001', '2024-05-10 10:15:00'),
                                                                     (2, 2, 'FAC-2024-0002', '2024-05-10 09:45:00'),
                                                                     (3, 3, 'FAC-2024-0003', '2024-05-11 08:30:00'),
                                                                     (4, 4, 'FAC-2024-0004', '2024-05-12 17:30:00');