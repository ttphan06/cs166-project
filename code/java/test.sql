select extract(year from r.repair_date), count(extract(year from r.repair_date))
from Repairs r
group by extract(year from r.repair_date)
order by count(extract(year from r.repair_date));





