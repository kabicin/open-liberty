CREATE TABLE ${schemaname}.criteria_car_origin (CAR_ID VARCHAR(254), CAR_VER INTEGER, component VARCHAR(254) NOT NULL, origin VARCHAR(254));
CREATE TABLE ${schemaname}.SimpleEntityOLGH10515 (CAR_ID VARCHAR(254) NOT NULL, CAR_VER INTEGER NOT NULL, PRIMARY KEY (CAR_ID, CAR_VER));
CREATE INDEX I_CRTRRGN_CAR_ID ON ${schemaname}.criteria_car_origin (CAR_ID, CAR_VER);