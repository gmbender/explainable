SELECT * FROM Sailors;

SELECT * FROM Sailors WHERE sid = 1;

SELECT sid, sname FROM Sailors;

SELECT * FROM Boats;

SELECT * FROM Boats WHERE bid IN (SELECT bid FROM Reserves WHERE sid = 1);

SELECT bid, color FROM Boats;

SELECT * FROM Reserves;

SELECT * FROM Reserves WHERE sid = 1;

SELECT day FROM Reserves;