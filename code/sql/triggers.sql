
CREATE SEQUENCE plane_number_seq START WITH 67; 
CREATE SEQUENCE pilot_number_seq START WITH 250; 
CREATE SEQUENCE flight_number_seq START WITH 2000;
CREATE SEQUENCE customer_number_seq START WITH 250;
CREATE SEQUENCE technician_number_seq START WITH 250;
CREATE SEQUENCE reservation_number_seq START WITH 9999;


CREATE OR REPLACE FUNCTION func_pilot() 
    RETURNS trigger AS 
    $BODY$ 
    BEGIN  
    new.id := nextval('pilot_number_seq'); 
    RETURN new; 
    END;  
    $BODY$ 
    LANGUAGE plpgsql VOLATILE; 
CREATE TRIGGER pilot_trigger BEFORE INSERT ON Pilot FOR EACH ROW EXECUTE PROCEDURE func_pilot(); 


CREATE OR REPLACE FUNCTION func_customer() 
    RETURNS trigger AS 
    $BODY$ 
    BEGIN  
    new.id := nextval('customer_number_seq'); 
    RETURN new; 
    END;  
    $BODY$ 
    LANGUAGE plpgsql VOLATILE; 
CREATE TRIGGER customer_trigger BEFORE INSERT ON Customer FOR EACH ROW EXECUTE PROCEDURE func_customer(); 


CREATE OR REPLACE FUNCTION func_plane() 
    RETURNS trigger AS 
    $BODY$ 
    BEGIN 
    new.id := nextval('plane_number_seq'); 
    RETURN new; 
    END; 
    $BODY$
    LANGUAGE plpgsql VOLATILE;
CREATE TRIGGER plane_trigger BEFORE INSERT ON Plane FOR EACH ROW EXECUTE PROCEDURE func_plane();


CREATE OR REPLACE FUNCTION func_flight() 
    RETURNS trigger AS 
    $BODY$ 
    BEGIN  
    new.fnum := nextval('flight_number_seq'); 
    RETURN new; 
    END;  
    $BODY$
    LANGUAGE plpgsql VOLATILE;
CREATE TRIGGER flight_trigger BEFORE INSERT ON Flight FOR EACH ROW EXECUTE PROCEDURE func_flight();


CREATE OR REPLACE FUNCTION func_technician() 
    RETURNS trigger AS 
    $BODY$ 
    BEGIN  
    new.id := nextval('technician_number_seq'); 
    RETURN new; 
    END;  
    $BODY$
    LANGUAGE plpgsql VOLATILE;
CREATE TRIGGER technician_trigger BEFORE INSERT ON Technician FOR EACH ROW EXECUTE PROCEDURE func_technician();


CREATE OR REPLACE FUNCTION func_reservation() 
    RETURNS trigger AS 
    $BODY$ 
    BEGIN  
    new.rnum := nextval('reservation_number_seq'); 
    RETURN new; 
    END;  
    $BODY$ 
    LANGUAGE plpgsql VOLATILE; 
CREATE TRIGGER reservation_trigger BEFORE INSERT ON Reservation FOR EACH ROW EXECUTE PROCEDURE func_reservation();
