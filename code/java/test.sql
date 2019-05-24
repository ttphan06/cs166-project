SELECT COUNT(r.status)
FROM Reservation r
WHERE r.fid = 1388
GROUP BY r.status
HAVING r.status = 'W';
