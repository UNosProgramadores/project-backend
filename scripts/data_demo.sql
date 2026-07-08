-- ============================================================
-- ParKing - Demo Data (v8 — full replacement)
-- ============================================================
-- This script is designed to run against a fresh database
-- after init.sql has created all tables.
-- ============================================================

-- ============================================================
-- ROLE (preserved)
-- ============================================================
INSERT INTO role (id, name, description) VALUES
  (1, 'admin',    'Full system control — owns a parking lot'),
  (2, 'staff',    'Daily operations — works at a parking lot'),
  (3, 'customer', 'Customer — can visit any parking lot');

-- ============================================================
-- VEHICLE_TYPE (preserved)
-- ============================================================
INSERT INTO vehicle_type (id, name, requires_plate) VALUES
  (1, 'car',        true),
  (2, 'motorcycle', true),
  (3, 'bicycle',    false);

-- ============================================================
-- PARKING_LOT (existing lots 1-2 preserved, new lots 3-4)
-- ============================================================
INSERT INTO parking_lot (id, name, address, opening_time, closing_time, rows, columns, auto_assignment, discounts_enabled) VALUES
  -- Existing lots (preserved)
  (1, 'ParKing Downtown', 'Carrera 45 # 12-30, Bogota',    '06:00:00', '22:00:00', 4, 5, true,  true),
  (2, 'ParKing Norte',    'Avenida 68 # 80-15, Bogota',    '07:00:00', '21:00:00', 3, 4, false, false),
  -- New lots
  (3, 'ParKing Sur',      'Calle 25 # 38-50, Bogota',      '06:30:00', '23:00:00', 5, 6, true,  true),
  (4, 'ParKing Oriente',  'Transversal 12 # 95-20, Bogota', '08:00:00', '20:00:00', 4, 5, false, false);

-- ============================================================
-- USER (users 1-8 preserved, 9-14 new)
-- ============================================================
-- Passwords (SHA-256):
--   admin123  → 240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9
--   staff456  → 8f5dada329d6ade1fdba5e207b5a81b312ae838801ca287a00e9428620808dce
--   client789 → 26059b6231aeaca864b1376dc03f48aaf490ad7a5a2c400bb1ca32ddf5f8209b

INSERT INTO "user" (id, document, name, phone, username, password_hash, role_id, parking_lot_id, active, failed_attempts, blocked, created_at) VALUES
  -- ======== Existing users (preserved) ========
  -- Admins
  (1,  '10000001', 'Carlos Mendoza',  '3101234567', 'cmendoza',  '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 1, 1, true, 0, false, '2024-01-15 08:00:00'),
  (2,  '10000002', 'Rosa Perez',      '3209111222', 'rperez',    '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 1, 2, true, 0, false, '2024-01-16 09:00:00'),
  -- Staff
  (3,  '10000003', 'Laura Gomez',     '3209876543', 'lgomez',    '8f5dada329d6ade1fdba5e207b5a81b312ae838801ca287a00e9428620808dce', 2, 1, true, 0, false, '2024-01-17 09:00:00'),
  (4,  '10000004', 'Pedro Ramirez',   '3154445566', 'pramirez',  '8f5dada329d6ade1fdba5e207b5a81b312ae838801ca287a00e9428620808dce', 2, 2, true, 0, false, '2024-01-18 09:30:00'),
  -- Customers
  (5,  '10000005', 'Ana Torres',      '3001112233', 'atorres',   '26059b6231aeaca864b1376dc03f48aaf490ad7a5a2c400bb1ca32ddf5f8209b', 3, NULL, true,  0, false, '2024-02-01 10:00:00'),
  (6,  '10000006', 'Luis Herrera',    '3177778899', 'lherrera',  '26059b6231aeaca864b1376dc03f48aaf490ad7a5a2c400bb1ca32ddf5f8209b', 3, NULL, true,  0, false, '2024-02-05 11:00:00'),
  (7,  '10000007', 'Sofia Vargas',    '3123334455', 'svargas',   '26059b6231aeaca864b1376dc03f48aaf490ad7a5a2c400bb1ca32ddf5f8209b', 3, NULL, true,  3, false, '2024-02-10 12:00:00'),
  (8,  '10000008', 'Mateo Castillo',  '3056667788', 'mcastillo', '26059b6231aeaca864b1376dc03f48aaf490ad7a5a2c400bb1ca32ddf5f8209b', 3, NULL, false, 5, true,  '2024-02-12 08:00:00'),

  -- ======== New users for lots 3 and 4 ========
  -- Admin (lot 3)
  (9,  '20000001', 'Diana Moreno',    '3115556677', 'dmoreno',   '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 1, 3, true, 0, false, '2025-01-10 08:00:00'),
  -- Admin (lot 4)
  (10, '20000002', 'Felipe Rojas',    '3188889900', 'frojas',    '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 1, 4, true, 0, false, '2025-01-15 09:00:00'),
  -- Staff (lot 3)
  (11, '20000003', 'Carmen Lopez',    '3201112233', 'clopez',    '8f5dada329d6ade1fdba5e207b5a81b312ae838801ca287a00e9428620808dce', 2, 3, true, 0, false, '2025-02-01 07:30:00'),
  -- Staff (lot 4)
  (12, '20000004', 'Andres Medina',   '3145556677', 'amedina',   '8f5dada329d6ade1fdba5e207b5a81b312ae838801ca287a00e9428620808dce', 2, 4, true, 0, false, '2025-02-05 08:00:00'),
  -- New customers
  (13, '20000005', 'Valentina Rios',  '3009988776', 'vrios',     '26059b6231aeaca864b1376dc03f48aaf490ad7a5a2c400bb1ca32ddf5f8209b', 3, NULL, true, 0, false, '2025-03-01 10:00:00'),
  (14, '20000006', 'Gabriel Silva',   '3194433221', 'gsilva',    '26059b6231aeaca864b1376dc03f48aaf490ad7a5a2c400bb1ca32ddf5f8209b', 3, NULL, true, 0, false, '2025-03-10 11:00:00');
-- user 7: 3 failed attempts, not yet blocked
-- user 8: 5 failed attempts, blocked (RNF_03)

-- ============================================================
-- VEHICLE (all vehicles with valid plate/registration formats)
-- ============================================================
-- Car plates:   ABC123  (3 letters + 3 digits)
-- Moto plates:  MTO45A  (3 letters + 2 digits + 1 letter)
-- Bike reg:     ABCDEFG (7 letters)
INSERT INTO vehicle (id, vehicle_type_id, plate, bike_registration, owner_id, brand, model, color, active) VALUES
  -- Cars
  (1,  1, 'ABC123', NULL, 5,   'Chevrolet',   'Spark',     'Rojo',   true),
  (2,  1, 'XYZ789', NULL, 6,   'Renault',     'Sandero',   'Blanco', true),
  (3,  1, 'DEF456', NULL, 7,   'Mazda',       '2',         'Azul',   true),
  (4,  1, 'GHI789', NULL, 13,  'Toyota',      'Corolla',   'Gris',   true),
  (5,  1, 'JKL012', NULL, 14,  'Hyundai',     'i10',       'Negro',  true),
  -- Motorcycles
  (6,  2, 'MTO45A', NULL, 5,   'Honda',       'CB190',     'Negro',  true),
  (7,  2, 'MTO67B', NULL, 7,   'Yamaha',      'FZ25',      'Azul',   true),
  (8,  2, 'MTO89C', NULL, 13,  'Suzuki',      'GN125',     'Rojo',   true),
  (9,  2, 'MTO12D', NULL, NULL,'Hero',        'Hunk',      'Gris',   true),
  -- Bicycles
  (10, 3, NULL, 'ABCDEFG', 6,   'Trek',        'FX3',       'Verde',  true),
  (11, 3, NULL, 'HIJKLMN', 13,  'Giant',       'Escape',    'Negro',  true),
  (12, 3, NULL, 'OPQRSTU', NULL,'Specialized', 'Allez',     'Naranja',true),
  (13, 3, NULL, 'VWXYZAB', 14,  'Scott',       'SubCross',  'Blanco', true);
-- Vehicle 9, 12: no owner (unregistered, tests RF_10)

-- ============================================================
-- CELL — Parking Lot 1 (preserved: 4 rows x 5 cols = 20 cells)
-- Row 0:          transit
-- Rows 1-3 col 0: car
-- Rows 1-3 col 1: car
-- Rows 1-3 col 2: motorcycle
-- Rows 1-3 col 3: motorcycle
-- Rows 1-3 col 4: bicycle
-- Row 3 col 0:    reserved for staff
-- ============================================================
INSERT INTO cell (id, parking_lot_id, row, col, code, cell_type, status, vehicle_type_id, reserved_for_staff, active) VALUES
  -- Row 0: transit
  (1,  1, 0, 0, '0-0', 'transit', 'available', NULL, false, true),
  (2,  1, 0, 1, '0-1', 'transit', 'available', NULL, false, true),
  (3,  1, 0, 2, '0-2', 'transit', 'available', NULL, false, true),
  (4,  1, 0, 3, '0-3', 'transit', 'available', NULL, false, true),
  (5,  1, 0, 4, '0-4', 'transit', 'available', NULL, false, true),
  -- Row 1
  (6,  1, 1, 0, '1-0', 'parking', 'available', 1, false, true),
  (7,  1, 1, 1, '1-1', 'parking', 'available', 1, false, true),
  (8,  1, 1, 2, '1-2', 'parking', 'available', 2, false, true),
  (9,  1, 1, 3, '1-3', 'parking', 'available', 2, false, true),
  (10, 1, 1, 4, '1-4', 'parking', 'available', 3, false, true),
  -- Row 2
  (11, 1, 2, 0, '2-0', 'parking', 'available', 1, false, true),
  (12, 1, 2, 1, '2-1', 'parking', 'available', 1, false, true),
  (13, 1, 2, 2, '2-2', 'parking', 'available', 2, false, true),
  (14, 1, 2, 3, '2-3', 'parking', 'available', 2, false, true),
  (15, 1, 2, 4, '2-4', 'parking', 'available', 3, false, true),
  -- Row 3
  (16, 1, 3, 0, '3-0', 'parking', 'available', 1, true, true),  -- reserved for staff
  (17, 1, 3, 1, '3-1', 'parking', 'available', 1, false, true),
  (18, 1, 3, 2, '3-2', 'parking', 'available', 2, false, true),
  (19, 1, 3, 3, '3-3', 'parking', 'available', 2, false, true),
  (20, 1, 3, 4, '3-4', 'parking', 'available', 3, false, true);

-- ============================================================
-- CELL — Parking Lot 2 (preserved: 3 rows x 4 cols = 12 cells)
-- Row 0:          transit
-- Rows 1-2 col 0: car
-- Rows 1-2 col 1: car
-- Rows 1-2 col 2: motorcycle
-- Rows 1-2 col 3: bicycle
-- ============================================================
INSERT INTO cell (id, parking_lot_id, row, col, code, cell_type, status, vehicle_type_id, reserved_for_staff, active) VALUES
  -- Row 0: transit
  (21, 2, 0, 0, '0-0', 'transit', 'available', NULL, false, true),
  (22, 2, 0, 1, '0-1', 'transit', 'available', NULL, false, true),
  (23, 2, 0, 2, '0-2', 'transit', 'available', NULL, false, true),
  (24, 2, 0, 3, '0-3', 'transit', 'available', NULL, false, true),
  -- Row 1
  (25, 2, 1, 0, '1-0', 'parking', 'available', 1, false, true),
  (26, 2, 1, 1, '1-1', 'parking', 'available', 1, false, true),
  (27, 2, 1, 2, '1-2', 'parking', 'available', 2, false, true),
  (28, 2, 1, 3, '1-3', 'parking', 'available', 3, false, true),
  -- Row 2
  (29, 2, 2, 0, '2-0', 'parking', 'available', 1, false, true),
  (30, 2, 2, 1, '2-1', 'parking', 'available', 1, false, true),
  (31, 2, 2, 2, '2-2', 'parking', 'available', 2, false, true),
  (32, 2, 2, 3, '2-3', 'parking', 'available', 3, false, true);

-- ============================================================
-- CELL — Parking Lot 3 (new: 5 rows x 6 cols = 30 cells)
-- Row 0:    T T T T T T   (transit entrance)
-- Row 1:    C C T M M T   (car cols 0-1, transit col 2, moto cols 3-4, transit col 5)
-- Row 2:    C C T M M T   (car cols 0-1, transit col 2, moto cols 3-4, transit col 5)
-- Row 3:    C C T M M T   (car cols 0-1, transit col 2, moto cols 3-4, transit col 5)
-- Row 4:    B B T C(S)C T (bike cols 0-1, transit col 2, car staff col 3, car col 4, transit col 5)
-- ============================================================
INSERT INTO cell (id, parking_lot_id, row, col, code, cell_type, status, vehicle_type_id, reserved_for_staff, active) VALUES
  -- Row 0: transit
  (33, 3, 0, 0, '0-0', 'transit', 'available', NULL, false, true),
  (34, 3, 0, 1, '0-1', 'transit', 'available', NULL, false, true),
  (35, 3, 0, 2, '0-2', 'transit', 'available', NULL, false, true),
  (36, 3, 0, 3, '0-3', 'transit', 'available', NULL, false, true),
  (37, 3, 0, 4, '0-4', 'transit', 'available', NULL, false, true),
  (38, 3, 0, 5, '0-5', 'transit', 'available', NULL, false, true),
  -- Row 1
  (39, 3, 1, 0, '1-0', 'parking', 'available', 1, false, true),
  (40, 3, 1, 1, '1-1', 'parking', 'available', 1, false, true),
  (41, 3, 1, 2, '1-2', 'transit', 'available', NULL, false, true),
  (42, 3, 1, 3, '1-3', 'parking', 'available', 2, false, true),
  (43, 3, 1, 4, '1-4', 'parking', 'available', 2, false, true),
  (44, 3, 1, 5, '1-5', 'transit', 'available', NULL, false, true),
  -- Row 2
  (45, 3, 2, 0, '2-0', 'parking', 'available', 1, false, true),
  (46, 3, 2, 1, '2-1', 'parking', 'available', 1, false, true),
  (47, 3, 2, 2, '2-2', 'transit', 'available', NULL, false, true),
  (48, 3, 2, 3, '2-3', 'parking', 'available', 2, false, true),
  (49, 3, 2, 4, '2-4', 'parking', 'available', 2, false, true),
  (50, 3, 2, 5, '2-5', 'transit', 'available', NULL, false, true),
  -- Row 3
  (51, 3, 3, 0, '3-0', 'parking', 'available', 1, false, true),
  (52, 3, 3, 1, '3-1', 'parking', 'available', 1, false, true),
  (53, 3, 3, 2, '3-2', 'transit', 'available', NULL, false, true),
  (54, 3, 3, 3, '3-3', 'parking', 'available', 2, false, true),
  (55, 3, 3, 4, '3-4', 'parking', 'available', 2, false, true),
  (56, 3, 3, 5, '3-5', 'transit', 'available', NULL, false, true),
  -- Row 4
  (57, 3, 4, 0, '4-0', 'parking', 'available', 3, false, true),
  (58, 3, 4, 1, '4-1', 'parking', 'available', 3, false, true),
  (59, 3, 4, 2, '4-2', 'transit', 'available', NULL, false, true),
  (60, 3, 4, 3, '4-3', 'parking', 'available', 1, true, true),  -- reserved for staff
  (61, 3, 4, 4, '4-4', 'parking', 'available', 1, false, true),
  (62, 3, 4, 5, '4-5', 'transit', 'available', NULL, false, true);

-- ============================================================
-- CELL — Parking Lot 4 (new: 4 rows x 5 cols = 20 cells)
-- Row 0:  T T T T T   (transit entrance)
-- Row 1:  C C T M B   (car cols 0-1, transit col 2, moto col 3, bicycle col 4)
-- Row 2:  C C T M B   (car cols 0-1, transit col 2, moto col 3, bicycle col 4)
-- Row 3:  T T T T T   (transit exit only, no parking on this row)
-- Row 4:  doesn't exist (4 rows only: 0-3)
-- Actually wait, 4 rows × 5 cols = 20 cells, rows 0-3
-- Let me redesign:
-- Row 0:  T T T T T   (transit)
-- Row 1:  C C T M B   (car, car, transit, moto, bike)
-- Row 2:  C C T M B   (car, car, transit, moto, bike)
-- Row 3:  B T C(S)T T (bike, transit, car staff, transit, transit)
-- ============================================================
INSERT INTO cell (id, parking_lot_id, row, col, code, cell_type, status, vehicle_type_id, reserved_for_staff, active) VALUES
  -- Row 0: transit
  (63, 4, 0, 0, '0-0', 'transit', 'available', NULL, false, true),
  (64, 4, 0, 1, '0-1', 'transit', 'available', NULL, false, true),
  (65, 4, 0, 2, '0-2', 'transit', 'available', NULL, false, true),
  (66, 4, 0, 3, '0-3', 'transit', 'available', NULL, false, true),
  (67, 4, 0, 4, '0-4', 'transit', 'available', NULL, false, true),
  -- Row 1
  (68, 4, 1, 0, '1-0', 'parking', 'available', 1, false, true),
  (69, 4, 1, 1, '1-1', 'parking', 'available', 1, false, true),
  (70, 4, 1, 2, '1-2', 'transit', 'available', NULL, false, true),
  (71, 4, 1, 3, '1-3', 'parking', 'available', 2, false, true),
  (72, 4, 1, 4, '1-4', 'parking', 'available', 3, false, true),
  -- Row 2
  (73, 4, 2, 0, '2-0', 'parking', 'available', 1, false, true),
  (74, 4, 2, 1, '2-1', 'parking', 'available', 1, false, true),
  (75, 4, 2, 2, '2-2', 'transit', 'available', NULL, false, true),
  (76, 4, 2, 3, '2-3', 'parking', 'available', 2, false, true),
  (77, 4, 2, 4, '2-4', 'parking', 'available', 3, false, true),
  -- Row 3
  (78, 4, 3, 0, '3-0', 'parking', 'available', 3, false, true),
  (79, 4, 3, 1, '3-1', 'transit', 'available', NULL, false, true),
  (80, 4, 3, 2, '3-2', 'parking', 'available', 1, true, true),  -- reserved for staff
  (81, 4, 3, 3, '3-3', 'transit', 'available', NULL, false, true),
  (82, 4, 3, 4, '3-4', 'transit', 'available', NULL, false, true);

-- ============================================================
-- RATE
-- ============================================================
-- Lot 1 rates (existing, preserved with same pricing)
INSERT INTO rate (id, parking_lot_id, vehicle_type_id, rate_type, cost, start_date, end_date, active) VALUES
  (1,  1, 1, 'per_minute',  150.00, '2024-01-01 00:00:00', NULL,                   true),
  (2,  1, 1, 'flat',       8000.00, '2024-01-01 00:00:00', NULL,                   true),
  (3,  1, 2, 'per_minute',   80.00, '2024-01-01 00:00:00', NULL,                   true),
  (4,  1, 2, 'flat',       4000.00, '2024-01-01 00:00:00', NULL,                   true),
  (5,  1, 3, 'per_minute',   30.00, '2024-01-01 00:00:00', NULL,                   true),
  (6,  1, 3, 'flat',       2000.00, '2024-01-01 00:00:00', NULL,                   true),
  -- Historical expired rate (RF_08 demo)
  (7,  1, 1, 'per_minute',  120.00, '2023-01-01 00:00:00', '2024-01-01 00:00:00', false);

-- Lot 2 rates (existing, preserved)
INSERT INTO rate (id, parking_lot_id, vehicle_type_id, rate_type, cost, start_date, end_date, active) VALUES
  (8,  2, 1, 'per_minute',  130.00, '2024-01-01 00:00:00', NULL, true),
  (9,  2, 1, 'flat',       7000.00, '2024-01-01 00:00:00', NULL, true),
  (10, 2, 2, 'per_minute',   70.00, '2024-01-01 00:00:00', NULL, true),
  (11, 2, 2, 'flat',       3500.00, '2024-01-01 00:00:00', NULL, true),
  (12, 2, 3, 'per_minute',   25.00, '2024-01-01 00:00:00', NULL, true),
  (13, 2, 3, 'flat',       1500.00, '2024-01-01 00:00:00', NULL, true);

-- Lot 3 rates (new — slightly cheaper than downtown)
INSERT INTO rate (id, parking_lot_id, vehicle_type_id, rate_type, cost, start_date, end_date, active) VALUES
  (14, 3, 1, 'per_minute',  140.00, '2025-06-01 00:00:00', NULL, true),
  (15, 3, 1, 'flat',       7500.00, '2025-06-01 00:00:00', NULL, true),
  (16, 3, 2, 'per_minute',   75.00, '2025-06-01 00:00:00', NULL, true),
  (17, 3, 2, 'flat',       3800.00, '2025-06-01 00:00:00', NULL, true),
  (18, 3, 3, 'per_minute',   28.00, '2025-06-01 00:00:00', NULL, true),
  (19, 3, 3, 'flat',       1800.00, '2025-06-01 00:00:00', NULL, true),
  -- Historical rate, replaced on 2025-06-01
  (20, 3, 1, 'per_minute',  160.00, '2025-01-01 00:00:00', '2025-06-01 00:00:00', false);

-- Lot 4 rates (new — cheapest option)
INSERT INTO rate (id, parking_lot_id, vehicle_type_id, rate_type, cost, start_date, end_date, active) VALUES
  (21, 4, 1, 'per_minute',  120.00, '2025-06-01 00:00:00', NULL, true),
  (22, 4, 1, 'flat',       6500.00, '2025-06-01 00:00:00', NULL, true),
  (23, 4, 2, 'per_minute',   65.00, '2025-06-01 00:00:00', NULL, true),
  (24, 4, 2, 'flat',       3200.00, '2025-06-01 00:00:00', NULL, true),
  (25, 4, 3, 'per_minute',   22.00, '2025-06-01 00:00:00', NULL, true),
  (26, 4, 3, 'flat',       1300.00, '2025-06-01 00:00:00', NULL, true);

-- ============================================================
-- DISCOUNT_CONFIG
-- ============================================================
INSERT INTO discount_config (id, parking_lot_id, active, min_external_invoice, min_visits, discount_percentage, start_date, end_date) VALUES
  -- Lot 1: 20% off with external invoice >= $50,000 (RF_06)
  (1, 1, true,  50000.00, NULL, 20.00, '2024-03-01 00:00:00', NULL),
  -- Lot 1: 10% off after 5 visits (RF_07) — lowered from 10 to 5 for easier demo
  (2, 1, true,  NULL,     5,    10.00, '2024-03-01 00:00:00', NULL),
  -- Lot 1: Historical expired config (RF_08 demo)
  (3, 1, false, 30000.00, NULL, 15.00, '2024-01-01 00:00:00', '2024-03-01 00:00:00'),

  -- Lot 3: 15% off with external invoice >= $80,000
  (4, 3, true,  80000.00, NULL, 15.00, '2025-06-01 00:00:00', NULL),
  -- Lot 3: 8% off after 3 visits (lower threshold for quicker demo)
  (5, 3, true,  NULL,     3,     8.00, '2025-06-01 00:00:00', NULL);

-- ============================================================
-- ENTRY_RECORD
-- ============================================================
-- All dates in late June / early July 2026 so reports show useful data.
--
-- ---- LOT 1 (ParKing Downtown) ----
-- Entry 1:  Car 1 (ABC123, Ana) — 90 min, per_minute, no discount (Ana has no external invoice and 1 visit)
-- Entry 2:  Car 2 (XYZ789, Luis) — 120 min, per_minute, no discount
-- Entry 3:  Moto 6 (MTO45A, Ana) — 45 min, per_minute, discount by external invoice (INV-STORE-0042 on entry 1)
-- Entry 4:  Car 3 (DEF456, Sofia) — 60 min, per_minute, no discount (1st visit)
-- Entry 5:  Car 3 (DEF456, Sofia) — 75 min, per_minute, no discount (2nd visit)
-- Entry 6:  Car 3 (DEF456, Sofia) — 50 min, per_minute, no discount (3rd visit)
-- Entry 7:  Car 3 (DEF456, Sofia) — 80 min, per_minute, no discount (4th visit)
-- Entry 8:  Car 3 (DEF456, Sofia) — 65 min, per_minute, no discount (5th visit — now qualifies for 10% visit discount!)
-- Entry 9:  Bike 10 (ABCDEFG, Luis) — 30 min, per_minute, no discount (2nd visit)
-- Entry 10: Car 1 (ABC123, Ana) — 400 min (>360), flat rate 8000, discount 20% (external invoice)
-- Entry 11: Moto 9 (MTO12D, no owner) — 20 min, per_minute, no discount
-- Entry 12: Bike 12 (OPQRSTU, no owner) — 55 min, per_minute, no discount
-- Entry 24: Car 2 (XYZ789, Luis) — 40 min, per_minute, no discount (3rd visit)
-- Entry 25: Bike 10 (ABCDEFG, Luis) — 20 min, per_minute, no discount (4th visit)
-- Entry 26: Car 2 (XYZ789, Luis) — 30 min, per_minute, discount 10% (5th visit >= 5!)
--
-- ---- LOT 2 (ParKing Norte, discounts disabled) ----
-- Entry 13: Car 4 (GHI789, Valentina) — 90 min, per_minute, no discount (disabled)
-- Entry 14: Moto 8 (MTO89C, Valentina) — 35 min, per_minute, no discount
-- Entry 15: Bike 11 (HIJKLMN, Valentina) — 40 min, per_minute, no discount
--
-- ---- LOT 3 (ParKing Sur) ----
-- Entry 16: Car 5 (JKL012, Gabriel) — 110 min, per_minute, no discount (1st visit)
-- Entry 17: Car 5 (JKL012, Gabriel) — 85 min, per_minute, no discount (2nd visit)
-- Entry 18: Bike 13 (VWXYZAB, Gabriel) — 25 min, per_minute, no discount (3rd visit — qualifies for 8%!)
-- Entry 19: Car 5 (JKL012, Gabriel) — 260 min, per_minute, discount 8% (4th visit >= 3)
-- Entry 20: Moto 9 (MTO12D, no owner) — 15 min, per_minute, no discount
-- Entry 21: Bike 12 (OPQRSTU, no owner) — 60 min, per_minute, no discount
--
-- ---- LOT 4 (ParKing Oriente, discounts disabled) ----
-- Entry 22: Car 4 (GHI789, Valentina) — 70 min, per_minute, no discount
-- Entry 23: Moto 8 (MTO89C, Valentina) — 45 min, per_minute, no discount
INSERT INTO entry_record (id, vehicle_id, cell_id, recorded_by, entry_time, exit_time, duration, status) VALUES
  -- Lot 1 — recorded by Laura (staff, id=3) unless noted
  (1,  1,  6,  3, '2026-06-23 08:30:00', '2026-06-23 10:00:00', 90,   'completed'),
  (2,  2,  11, 3, '2026-06-23 09:00:00', '2026-06-23 11:00:00', 120,  'completed'),
  (3,  6,  8,  3, '2026-06-24 14:00:00', '2026-06-24 14:45:00', 45,   'completed'),
  (4,  3,  7,  3, '2026-06-25 10:00:00', '2026-06-25 11:00:00', 60,   'completed'),
  (5,  3,  17, 3, '2026-06-26 08:15:00', '2026-06-26 09:30:00', 75,   'completed'),
  (6,  3,  17, 1, '2026-06-27 13:00:00', '2026-06-27 13:50:00', 50,   'completed'),
  (7,  3,  7,  3, '2026-06-28 09:00:00', '2026-06-28 10:20:00', 80,   'completed'),
  (8,  3,  11, 3, '2026-06-30 07:30:00', '2026-06-30 08:35:00', 65,   'completed'),
  (9,  10, 15, 3, '2026-06-30 11:00:00', '2026-06-30 11:30:00', 30,   'completed'),
  (10, 1,  12, 3, '2026-07-01 06:00:00', '2026-07-01 12:40:00', 400,  'completed'),
  (11, 9,  13, 3, '2026-07-02 10:00:00', '2026-07-02 10:20:00', 20,   'completed'),
  (12, 12, 20, 3, '2026-07-02 15:00:00', '2026-07-02 15:55:00', 55,   'completed'),
  -- Extra entries for Luis to reach 5 visits (visit-based discount demo)
  (24, 2,  17, 3, '2026-07-03 08:00:00', '2026-07-03 08:40:00', 40,   'completed'),
  (25, 10, 15, 3, '2026-07-03 10:00:00', '2026-07-03 10:20:00', 20,   'completed'),
  (26, 2,  12, 3, '2026-07-04 07:00:00', '2026-07-04 07:30:00', 30,   'completed'),
  -- Lot 2 — recorded by Pedro (staff, id=4); discounts disabled, no discount applies
  (13, 4,  25, 4, '2026-06-24 08:00:00', '2026-06-24 09:30:00', 90,   'completed'),
  (14, 8,  27, 4, '2026-06-25 16:00:00', '2026-06-25 16:35:00', 35,   'completed'),
  (15, 11, 28, 4, '2026-06-26 09:00:00', '2026-06-26 09:40:00', 40,   'completed'),
  -- Lot 3 — recorded by Carmen (staff, id=11)
  (16, 5,  39, 11, '2026-06-26 10:00:00', '2026-06-26 11:50:00', 110,  'completed'),
  (17, 5,  45, 11, '2026-06-28 14:00:00', '2026-06-28 15:25:00', 85,   'completed'),
  (18, 13, 57, 11, '2026-06-29 07:30:00', '2026-06-29 07:55:00', 25,   'completed'),
  (19, 5,  51, 11, '2026-06-30 18:00:00', '2026-06-30 22:20:00', 260,  'completed'),
  (20, 9,  42, 11, '2026-07-01 08:30:00', '2026-07-01 08:45:00', 15,   'completed'),
  (21, 12, 58, 11, '2026-07-02 12:00:00', '2026-07-02 13:00:00', 60,   'completed'),
  -- Lot 4 — recorded by Andres (staff, id=12); discounts disabled
  (22, 4,  68, 12, '2026-06-27 09:00:00', '2026-06-27 10:10:00', 70,   'completed'),
  (23, 8,  71, 12, '2026-06-29 15:00:00', '2026-06-29 15:45:00', 45,   'completed');

-- ============================================================
-- PAYMENT
-- ============================================================
-- Rate calculation reference (Lot 1):
--   car:  150/min (per_minute), 8000 flat
--   moto:  80/min, 4000 flat
--   bike:  30/min, 2000 flat
-- Rate calculation reference (Lot 2):
--   car:  130/min, 7000 flat
--   moto:  70/min, 3500 flat
--   bike:  25/min, 1500 flat
-- Rate calculation reference (Lot 3):
--   car:  140/min, 7500 flat
--   moto:  75/min, 3800 flat
--   bike:  28/min, 1800 flat
-- Rate calculation reference (Lot 4):
--   car:  120/min, 6500 flat
--   moto:  65/min, 3200 flat
--   bike:  22/min, 1300 flat
INSERT INTO payment (id, entry_record_id, subtotal, discount_percentage, discount_amount, total_paid, payment_method, payment_date, external_invoice_ref, rate_type, rate_value) VALUES
  -- Lot 1 payments
  -- Entry 1: 90 min car, per_minute: 150*90 = 13500. No discount (1st visit, no external inv).
  (1,  1,  13500.00, 0,    0.00,   13500.00, 'CASH',      '2026-06-23 10:00:00', 'INV-EXT-001',      'per_minute', 150.00),
  -- Entry 2: 120 min car, per_minute: 150*120 = 18000. No discount.
  (2,  2,  18000.00, 0,    0.00,   18000.00, 'CARD',      '2026-06-23 11:00:00', NULL,                'per_minute', 150.00),
  -- Entry 3: 45 min moto, per_minute: 80*45 = 3600. Discount 20% via external invoice (Ana has entry 1 with INV-STORE-0042).
  (3,  3,   3600.00, 20.00, 720.00,  2880.00, 'CASH',      '2026-06-24 14:45:00', 'INV-STORE-0042',   'per_minute',  80.00),
  -- Entry 4: 60 min car, per_minute: 150*60 = 9000. Sofia 1st visit, no discount.
  (4,  4,   9000.00, 0,      0.00,   9000.00, 'CARD',      '2026-06-25 11:00:00', NULL,                'per_minute', 150.00),
  -- Entry 5: 75 min car, per_minute: 150*75 = 11250. Sofia 2nd visit, no discount.
  (5,  5,  11250.00, 0,      0.00,  11250.00, 'CASH',      '2026-06-26 09:30:00', NULL,                'per_minute', 150.00),
  -- Entry 6: 50 min car, per_minute: 150*50 = 7500. Sofia 3rd visit, no discount.
  (6,  6,   7500.00, 0,      0.00,   7500.00, 'TRANSFER',  '2026-06-27 13:50:00', NULL,                'per_minute', 150.00),
  -- Entry 7: 80 min car, per_minute: 150*80 = 12000. Sofia 4th visit, no discount.
  (7,  7,  12000.00, 0,      0.00,  12000.00, 'CARD',      '2026-06-28 10:20:00', NULL,                'per_minute', 150.00),
  -- Entry 8: 65 min car, per_minute: 150*65 = 9750. Sofia 5th visit >= 5 → 10% discount!
  (8,  8,   9750.00, 10.00, 975.00,  8775.00, 'CASH',      '2026-06-30 08:35:00', NULL,                'per_minute', 150.00),
  -- Entry 9: 30 min bike, per_minute: 30*30 = 900. Luis 2nd visit, no discount (yet).
  (9,  9,    900.00, 0,       0.00,   900.00, 'CARD',      '2026-06-30 11:30:00', NULL,                'per_minute',  30.00),
  -- Entry 10: 400 min car, >= 360 → flat rate 8000. Ana has external invoice → 20% discount.
  (10, 10,  8000.00, 20.00,1600.00,  6400.00, 'CARD',      '2026-07-01 12:40:00', 'FACT-EXTERNA-001', 'flat',      8000.00),
  -- Entry 11: 20 min moto (no owner), per_minute: 80*20 = 1600. No discount.
  (11, 11,  1600.00, 0,      0.00,   1600.00, 'CASH',      '2026-07-02 10:20:00', NULL,                'per_minute',  80.00),
  -- Entry 12: 55 min bike (no owner), per_minute: 30*55 = 1650. No discount.
  (12, 12,  1650.00, 0,      0.00,   1650.00, 'CASH',      '2026-07-02 15:55:00', NULL,                'per_minute',  30.00),

  -- Lot 2 payments (discounts disabled — no discount ever applies)
  -- Entry 13: 90 min car, per_minute: 130*90 = 11700.
  (13, 13, 11700.00, 0,      0.00,  11700.00, 'CARD',      '2026-06-24 09:30:00', NULL,                'per_minute', 130.00),
  -- Entry 14: 35 min moto, per_minute: 70*35 = 2450.
  (14, 14,  2450.00, 0,      0.00,   2450.00, 'CASH',      '2026-06-25 16:35:00', NULL,                'per_minute',  70.00),
  -- Entry 15: 40 min bike, per_minute: 25*40 = 1000.
  (15, 15,  1000.00, 0,      0.00,   1000.00, 'CASH',      '2026-06-26 09:40:00', NULL,                'per_minute',  25.00),

  -- Lot 3 payments
  -- Entry 16: 110 min car, per_minute: 140*110 = 15400. Gabriel 1st visit, no discount.
  (16, 16, 15400.00, 0,      0.00,  15400.00, 'CARD',      '2026-06-26 11:50:00', NULL,                'per_minute', 140.00),
  -- Entry 17: 85 min car, per_minute: 140*85 = 11900. 2nd visit, no discount.
  (17, 17, 11900.00, 0,      0.00,  11900.00, 'CASH',      '2026-06-28 15:25:00', NULL,                'per_minute', 140.00),
  -- Entry 18: 25 min bike, per_minute: 28*25 = 700. 3rd visit >= 3 → 8% discount!
  (18, 18,   700.00, 8.00,   56.00,   644.00, 'CARD',      '2026-06-29 07:55:00', NULL,                'per_minute',  28.00),
  -- Entry 19: 260 min car, per_minute: 140*260 = 36400. 4th visit >= 3 → 8% discount!
  (19, 19, 36400.00, 8.00, 2912.00, 33488.00, 'TRANSFER',  '2026-06-30 22:20:00', NULL,                'per_minute', 140.00),
  -- Entry 20: 15 min moto (no owner), per_minute: 75*15 = 1125. No discount.
  (20, 20,  1125.00, 0,      0.00,   1125.00, 'CASH',      '2026-07-01 08:45:00', NULL,                'per_minute',  75.00),
  -- Entry 21: 60 min bike (no owner), per_minute: 28*60 = 1680. No discount.
  (21, 21,  1680.00, 0,      0.00,   1680.00, 'CARD',      '2026-07-02 13:00:00', NULL,                'per_minute',  28.00),

  -- Lot 4 payments (discounts disabled)
  -- Entry 22: 70 min car, per_minute: 120*70 = 8400.
  (22, 22,  8400.00, 0,      0.00,   8400.00, 'CARD',      '2026-06-27 10:10:00', NULL,                'per_minute', 120.00),
  -- Entry 23: 45 min moto, per_minute: 65*45 = 2925.
  (23, 23,  2925.00, 0,      0.00,   2925.00, 'CASH',      '2026-06-29 15:45:00', NULL,                'per_minute',  65.00),

  -- Luis extra entries (payments 24-26)
  -- Entry 24: 40 min car, per_minute: 150*40 = 6000. Luis 3rd visit, no discount.
  (24, 24,  6000.00, 0,      0.00,   6000.00, 'CASH',      '2026-07-03 08:40:00', NULL,                'per_minute', 150.00),
  -- Entry 25: 20 min bike, per_minute: 30*20 = 600. Luis 4th visit, no discount.
  (25, 25,   600.00, 0,      0.00,    600.00, 'CARD',      '2026-07-03 10:20:00', NULL,                'per_minute',  30.00),
  -- Entry 26: 30 min car, per_minute: 150*30 = 4500. Luis 5th visit >= 5 → 10% discount!
  (26, 26,  4500.00, 10.00, 450.00,  4050.00, 'CARD',      '2026-07-04 07:30:00', NULL,                'per_minute', 150.00);

-- INVOICE
-- Invoice numbers match the backend pattern: INV-XXXXXXXX
-- ============================================================
INSERT INTO invoice (id, payment_id, invoice_number, issued_at) VALUES
  (1,  1,  'INV-A1B2C3D4', '2026-06-23 10:00:00'),
  (2,  2,  'INV-E5F6G7H8', '2026-06-23 11:00:00'),
  (3,  3,  'INV-I9J0K1L2', '2026-06-24 14:45:00'),
  (4,  4,  'INV-M3N4O5P6', '2026-06-25 11:00:00'),
  (5,  5,  'INV-Q7R8S9T0', '2026-06-26 09:30:00'),
  (6,  6,  'INV-U1V2W3X4', '2026-06-27 13:50:00'),
  (7,  7,  'INV-Y5Z6A7B8', '2026-06-28 10:20:00'),
  (8,  8,  'INV-C9D0E1F2', '2026-06-30 08:35:00'),
  (9,  9,  'INV-G3H4I5J6', '2026-06-30 11:30:00'),
  (10, 10, 'INV-K7L8M9N0', '2026-07-01 12:40:00'),
  (11, 11, 'INV-O1P2Q3R4', '2026-07-02 10:20:00'),
  (12, 12, 'INV-S5T6U7V8', '2026-07-02 15:55:00'),
  (13, 13, 'INV-W9X0Y1Z2', '2026-06-24 09:30:00'),
  (14, 14, 'INV-A3B4C5D6', '2026-06-25 16:35:00'),
  (15, 15, 'INV-E7F8G9H0', '2026-06-26 09:40:00'),
  (16, 16, 'INV-I1J2K3L4', '2026-06-26 11:50:00'),
  (17, 17, 'INV-M5N6O7P8', '2026-06-28 15:25:00'),
  (18, 18, 'INV-Q9R0S1T2', '2026-06-29 07:55:00'),
  (19, 19, 'INV-U3V4W5X6', '2026-06-30 22:20:00'),
  (20, 20, 'INV-Y7Z8A9B0', '2026-07-01 08:45:00'),
  (21, 21, 'INV-C1D2E3F4', '2026-07-02 13:00:00'),
  (22, 22, 'INV-G5H6I7J8', '2026-06-27 10:10:00'),
  (23, 23, 'INV-K9L0M1N2', '2026-06-29 15:45:00'),
  (24, 24, 'INV-O3P4Q5R6', '2026-07-03 08:40:00'),
  (25, 25, 'INV-S7T8U9V0', '2026-07-03 10:20:00'),
  (26, 26, 'INV-W1X2Y3Z4', '2026-07-04 07:30:00');

-- ============================================================
-- RESET IDENTITY SEQUENCES
-- ============================================================
SELECT setval(pg_get_serial_sequence('role', 'id'),
              (SELECT MAX(id) FROM role));
SELECT setval(pg_get_serial_sequence('vehicle_type', 'id'),
              (SELECT MAX(id) FROM vehicle_type));
SELECT setval(pg_get_serial_sequence('parking_lot', 'id'),
              (SELECT MAX(id) FROM parking_lot));
SELECT setval(pg_get_serial_sequence('"user"', 'id'),
              (SELECT MAX(id) FROM "user"));
SELECT setval(pg_get_serial_sequence('vehicle', 'id'),
              (SELECT MAX(id) FROM vehicle));
SELECT setval(pg_get_serial_sequence('cell', 'id'),
              (SELECT MAX(id) FROM cell));
SELECT setval(pg_get_serial_sequence('rate', 'id'),
              (SELECT MAX(id) FROM rate));
SELECT setval(pg_get_serial_sequence('discount_config', 'id'),
              (SELECT MAX(id) FROM discount_config));
SELECT setval(pg_get_serial_sequence('entry_record', 'id'),
              (SELECT MAX(id) FROM entry_record));
SELECT setval(pg_get_serial_sequence('payment', 'id'),
              (SELECT MAX(id) FROM payment));
SELECT setval(pg_get_serial_sequence('invoice', 'id'),
              (SELECT MAX(id) FROM invoice));
