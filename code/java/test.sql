--select f.num_sold
--from flight f
--where f.fnum = 0 and f.actual_departure_date = '2014-05-01';

select p.seats - f.num_sold
from ((flightinfo i
     inner join flight f on i.flight_id = f.fnum)
     inner join plane p on p.id = i.plane_id)
where f.fnum = 0 and f.actual_departure_date = '2014-05-01';
