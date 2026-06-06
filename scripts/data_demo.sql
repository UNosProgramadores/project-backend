-- ============================================================
-- ParKing - Demo Data
-- ============================================================

-- ============================================================
-- ROLE
-- ============================================================
INSERT INTO role (id, name, description) VALUES
                                             (1, 'admin',    'Full system control'),
                                             (2, 'staff',    'Daily parking operations'),
                                             (3, 'customer', 'Parking lot customer');


-- ============================================================
-- VEHICLE_TYPE
-- ============================================================
INSERT INTO vehicle_type (id, name, requires_plate) VALUES
                                                        (1, 'car',        true),
                                                        (2, 'motorcycle', true),
                                                        (3, 'bicycle',    false);


-- ============================================================
-- USER
-- ============================================================
-- Passwords (SHA-256):
--   admin123  → a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3
--   staff456  → 4b5f29b8c4aa63e5dcf3bfa72d65bfb9c9f8a3d1e2c7b4a1f0e9d8c7b6a5f4e3
--   client789 → 9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08

INSERT INTO "user" (id, document, name, phone, username, password_hash, role_id, active, failed_attempts, blocked, created_at) VALUES
                                                                                                                                   (1, '10000001', 'Carlos Mendoza', '3101234567', 'cmendoza',  'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 1, true,  0, false, '2024-01-15 08:00:00'),
                                                                                                                                   (2, '10000002', 'Laura Gomez',    '3209876543', 'lgomez',    '4b5f29b8c4aa63e5dcf3bfa72d65bfb9c9f8a3d1e2c7b4a1f0e9d8c7b6a5f4e3', 2, true,  0, false, '2024-01-16 09:00:00'),
                                                                                                                                   (3, '10000003', 'Pedro Ramirez',  '3154445566', 'pramirez',  '4b5f29b8c4aa63e5dcf3bfa72d65bfb9c9f8a3d1e2c7b4a1f0e9d8c7b6a5f4e3', 2, true,  0, false, '2024-01-17 09:30:00'),
                                                                                                                                   (4, '10000004', 'Ana Torres',     '3001112233', 'atorres',   '9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08', 3, true,  0, false, '2024-02-01 10:00:00'),
                                                                                                                                   (5, '10000005', 'Luis Herrera',   '3177778899', 'lherrera',  '9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08', 3, true,  0, false, '2024-02-05 11:00:00'),
                                                                                                                                   (6, '10000006', 'Sofia Vargas',   '3123334455', 'svargas',   '9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08', 3, true,  3, false, '2024-02-10 12:00:00'),
                                                                                                                                   (7, '10000007', 'Mateo Castillo', '3056667788', 'mcastillo', '9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08', 3, false, 5, true,  '2024-02-12 08:00:00');
-- user 6: 3 failed attempts, not yet blocked
-- user 7: 5 failed attempts, account blocked (tests RNF_03)


-- ============================================================
-- VEHICLE
-- ============================================================
INSERT INTO vehicle (id, vehicle_type_id, plate, bike_registration, owner_id, brand, model, color, active) VALUES
                                                                                                               (1, 1, 'ABC123', NULL,       4,    'Chevrolet',   'Spark',   'Red',    true),
                                                                                                               (2, 1, 'XYZ789', NULL,       5,    'Renault',     'Sandero', 'White',  true),
                                                                                                               (3, 2, 'MTO456', NULL,       4,    'Honda',       'CB190',   'Black',  true),
                                                                                                               (4, 2, 'MTO999', NULL,       6,    'Yamaha',      'FZ25',    'Blue',   true),
                                                                                                               (5, 3, NULL,     'BIKE-001', 5,    'Trek',        'FX3',     'Green',  true),
                                                                                                               (6, 3, NULL,     'BIKE-002', NULL, 'Specialized', 'Allez',   'Orange', true);
-- vehicle 6: bicycle with no registered owner (tests RF_10 unregistered bike entry)


-- ============================================================
-- PARKING_LOT
-- ============================================================
INSERT INTO parking_lot (id, name, address, opening_time, closing_time, rows, columns, auto_assignment, discounts_enabled) VALUES
    (1, 'ParKing Downtown', 'Street 45 # 12-30, Bogota', '06:00:00', '22:00:00', 4, 5, true, true);
-- 4 rows x 5 columns = 20 cells total


-- ============================================================
-- CELL (4 rows x 5 columns = 20 cells)
-- Row 0:          transit lanes (no parking)
-- Rows 1-3 col 0-1: car cells       (6 cells)
-- Rows 1-3 col 2-3: motorcycle cells (6 cells)
-- Rows 1-3 col 4:   bicycle cells   (3 cells)
-- Row 3 col 0: reserved for staff
-- ============================================================
INSERT INTO cell (id, parking_lot_id, row, col, code, cell_type, status, vehicle_type_id, reserved_for_staff) VALUES
-- Row 0: transit
(1,  1, 0, 0, 'T-00', 'transit', 'available', NULL, false),
(2,  1, 0, 1, 'T-01', 'transit', 'available', NULL, false),
(3,  1, 0, 2, 'T-02', 'transit', 'available', NULL, false),
(4,  1, 0, 3, 'T-03', 'transit', 'available', NULL, false),
(5,  1, 0, 4, 'T-04', 'transit', 'available', NULL, false),
-- Row 1
(6,  1, 1, 0, 'C-10', 'parking', 'occupied',  1, false),
(7,  1, 1, 1, 'C-11', 'parking', 'available', 1, false),
(8,  1, 1, 2, 'M-12', 'parking', 'occupied',  2, false),
(9,  1, 1, 3, 'M-13', 'parking', 'available', 2, false),
(10, 1, 1, 4, 'B-14', 'parking', 'available', 3, false),
-- Row 2
(11, 1, 2, 0, 'C-20', 'parking', 'available', 1, false),
(12, 1, 2, 1, 'C-21', 'parking', 'occupied',  1, false),
(13, 1, 2, 2, 'M-22', 'parking', 'available', 2, false),
(14, 1, 2, 3, 'M-23', 'parking', 'occupied',  2, false),
(15, 1, 2, 4, 'B-24', 'parking', 'occupied',  3, false),
-- Row 3
(16, 1, 3, 0, 'C-30', 'parking', 'occupied',  1, true),   -- reserved for staff
(17, 1, 3, 1, 'C-31', 'parking', 'available', 1, false),
(18, 1, 3, 2, 'M-32', 'parking', 'available', 2, false),
(19, 1, 3, 3, 'M-33', 'parking', 'available', 2, false),
(20, 1, 3, 4, 'B-34', 'parking', 'available', 3, false);


-- ============================================================
-- RATE
-- rate_type: 'per_minute' | 'flat'
-- end_date NULL = currently active rate (supports RF_08 history)
-- ============================================================
INSERT INTO rate (id, parking_lot_id, vehicle_type_id, rate_type, cost, start_date, end_date, active) VALUES
                                                                                                          (1, 1, 1, 'per_minute',  150.00, '2024-01-01 00:00:00', NULL,                   true),
                                                                                                          (2, 1, 1, 'flat',       8000.00, '2024-01-01 00:00:00', NULL,                   true),
                                                                                                          (3, 1, 2, 'per_minute',   80.00, '2024-01-01 00:00:00', NULL,                   true),
                                                                                                          (4, 1, 2, 'flat',       4000.00, '2024-01-01 00:00:00', NULL,                   true),
                                                                                                          (5, 1, 3, 'per_minute',   30.00, '2024-01-01 00:00:00', NULL,                   true),
                                                                                                          (6, 1, 3, 'flat',       2000.00, '2024-01-01 00:00:00', NULL,                   true),
-- Historical rate — expired, demonstrates RF_08
                                                                                                          (7, 1, 1, 'per_minute',  120.00, '2023-01-01 00:00:00', '2023-12-31 23:59:59', false);


-- ============================================================
-- DISCOUNT_CONFIG
-- end_date NULL = currently active config
-- ============================================================
INSERT INTO discount_config (id, parking_lot_id, active, min_external_invoice, min_visits, discount_percentage, start_date, end_date) VALUES
-- 20% off when customer presents external invoice over $50,000 (RF_06)
(1, 1, true,  50000.00, NULL, 20.00, '2024-03-01 00:00:00', NULL),
-- 10% off after 10 visits (RF_07)
(2, 1, true,  NULL,     10,   10.00, '2024-03-01 00:00:00', NULL),
-- Historical config — expired, demonstrates RF_08
(3, 1, false, 30000.00, NULL, 15.00, '2024-01-01 00:00:00', '2024-02-28 23:59:59');


-- ============================================================
-- ENTRY_RECORD
-- status: 'active' (vehicle inside) | 'completed' (vehicle left)
-- duration: in minutes
-- ============================================================
INSERT INTO entry_record (id, vehicle_id, cell_id, entry_time, exit_time, duration, status) VALUES
                                                                                                (1, 1, 6,  '2024-05-10 08:30:00', '2024-05-10 10:15:00', 105,  'completed'),  -- car 105min
                                                                                                (2, 3, 8,  '2024-05-10 09:00:00', '2024-05-10 09:45:00', 45,   'completed'),  -- motorcycle 45min
                                                                                                (3, 5, 15, '2024-05-11 07:00:00', '2024-05-11 08:30:00', 90,   'completed'),  -- bicycle 90min
                                                                                                (4, 2, 12, '2024-05-12 14:00:00', '2024-05-12 17:30:00', 210,  'completed'),  -- car 210min
                                                                                                (5, 4, 14, '2024-05-13 08:00:00', NULL,                  NULL, 'active'),     -- motorcycle still inside
                                                                                                (6, 6, 16, '2024-05-13 07:30:00', NULL,                  NULL, 'active');     -- bicycle still inside (unregistered owner)


-- ============================================================
-- PAYMENT
-- Records 5 and 6 have no payment yet (vehicles still inside)
-- ============================================================
INSERT INTO payment (id, entry_record_id, subtotal, discount_percentage, discount_amount, total_paid, payment_method, payment_date, external_invoice_ref) VALUES
                                                                                                                                                              (1, 1, 15750.00, 20.00, 3150.00, 12600.00, 'cash', '2024-05-10 10:15:00', 'INV-STORE-0042'),  -- discount by external invoice
                                                                                                                                                              (2, 2,  3600.00,  0.00,    0.00,  3600.00, 'cash', '2024-05-10 09:45:00', NULL),              -- no discount
                                                                                                                                                              (3, 3,  2700.00, 10.00,  270.00,  2430.00, 'cash', '2024-05-11 08:30:00', NULL),              -- discount by visits
                                                                                                                                                              (4, 4, 31500.00,  0.00,    0.00, 31500.00, 'cash', '2024-05-12 17:30:00', NULL);              -- no discount


-- ============================================================
-- INVOICE
-- ============================================================
INSERT INTO invoice (id, payment_id, invoice_number, issued_at) VALUES
                                                                    (1, 1, 'INV-2024-0001', '2024-05-10 10:15:00'),
                                                                    (2, 2, 'INV-2024-0002', '2024-05-10 09:45:00'),
                                                                    (3, 3, 'INV-2024-0003', '2024-05-11 08:30:00'),
                                                                    (4, 4, 'INV-2024-0004', '2024-05-12 17:30:00');