CREATE TABLE "rol" (
                       "id" int PRIMARY KEY,
                       "nombre" varchar UNIQUE,
                       "descripcion" varchar
);

CREATE TABLE "usuario" (
                           "id" int PRIMARY KEY,
                           "cedula" varchar UNIQUE NOT NULL,
                           "nombre" varchar NOT NULL,
                           "celular" varchar,
                           "nombre_usuario" varchar UNIQUE,
                           "contrasena_hash" varchar NOT NULL,
                           "rol_id" int NOT NULL,
                           "activo" boolean,
                           "fecha_registro" timestamp
);

CREATE TABLE "tipo_vehiculo" (
                                 "id" int PRIMARY KEY,
                                 "nombre" varchar UNIQUE,
                                 "requiere_placa" boolean
);

CREATE TABLE "vehiculo" (
                            "id" int PRIMARY KEY,
                            "tipo_vehiculo_id" int,
                            "placa" varchar UNIQUE,
                            "registro_bici" varchar UNIQUE,
                            "propietario_id" int,
                            "marca" varchar,
                            "modelo" varchar,
                            "color" varchar,
                            "activo" boolean
);

CREATE TABLE "parqueadero" (
                               "id" int PRIMARY KEY,
                               "nombre" varchar,
                               "direccion" varchar,
                               "filas" int,
                               "columnas" int,
                               "asignacion_automatica" boolean,
                               "descuentos_activos" boolean
);

CREATE TABLE "celda" (
                         "id" int PRIMARY KEY,
                         "parqueadero_id" int NOT NULL,
                         "fila" int,
                         "columna" int,
                         "tipo_celda" varchar,
                         "estado" varchar,
                         "tipo_vehiculo_id" int,
                         "reservada_trabajador" boolean
);

CREATE TABLE "tarifa" (
                          "id" int PRIMARY KEY,
                          "parqueadero_id" int NOT NULL,
                          "tipo_vehiculo_id" int,
                          "tipo_tarifa" varchar,
                          "valor" decimal,
                          "fecha_inicio" timestamp,
                          "fecha_fin" timestamp,
                          "activa" boolean
);

CREATE TABLE "configuracion_descuento" (
                                           "id" int PRIMARY KEY,
                                           "parqueadero_id" int,
                                           "activo" boolean,
                                           "monto_minimo_factura_externa" decimal,
                                           "numero_minimo_visitas" int,
                                           "porcentaje_descuento" decimal,
                                           "fecha_inicio" timestamp,
                                           "fecha_fin" timestamp
);

CREATE TABLE "ingreso" (
                           "id" int PRIMARY KEY,
                           "vehiculo_id" int NOT NULL,
                           "celda_id" int NOT NULL,
                           "fecha_hora_ingreso" timestamp,
                           "fecha_hora_salida" timestamp,
                           "tiempo_permanencia_min" int,
                           "estado" varchar
);

CREATE TABLE "factura" (
                           "id" int PRIMARY KEY,
                           "pago_id" int UNIQUE,
                           "numero_factura" varchar UNIQUE,
                           "fecha_emision" timestamp
);

CREATE TABLE "pago" (
                        "id" int PRIMARY KEY,
                        "ingreso_id" int UNIQUE,
                        "subtotal" decimal,
                        "porcentaje_descuento" decimal,
                        "valor_descuento" decimal,
                        "total_pagado" decimal,
                        "metodo_pago" varchar,
                        "fecha_pago" timestamp
);

ALTER TABLE "usuario" ADD FOREIGN KEY ("rol_id") REFERENCES "rol" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "vehiculo" ADD FOREIGN KEY ("tipo_vehiculo_id") REFERENCES "tipo_vehiculo" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "vehiculo" ADD FOREIGN KEY ("propietario_id") REFERENCES "usuario" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "celda" ADD FOREIGN KEY ("parqueadero_id") REFERENCES "parqueadero" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "celda" ADD FOREIGN KEY ("tipo_vehiculo_id") REFERENCES "tipo_vehiculo" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "tarifa" ADD FOREIGN KEY ("parqueadero_id") REFERENCES "parqueadero" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "tarifa" ADD FOREIGN KEY ("tipo_vehiculo_id") REFERENCES "tipo_vehiculo" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "configuracion_descuento" ADD FOREIGN KEY ("parqueadero_id") REFERENCES "parqueadero" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "ingreso" ADD FOREIGN KEY ("vehiculo_id") REFERENCES "vehiculo" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "ingreso" ADD FOREIGN KEY ("celda_id") REFERENCES "celda" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "factura" ADD FOREIGN KEY ("pago_id") REFERENCES "pago" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "pago" ADD FOREIGN KEY ("ingreso_id") REFERENCES "ingreso" ("id") DEFERRABLE INITIALLY IMMEDIATE;