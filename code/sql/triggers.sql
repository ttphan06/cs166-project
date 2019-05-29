
CREATE SEQUENCE plane_number_seq START WITH 67; 
CREATE SEQUENCE pilot_number_seq START WITH 250; 
CREATE SEQUENCE flight_number_seq START WITH 2000; 


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

