----- AggregateTest.java -----

SELECT AVG(S.age) FROM Sailors S;

SELECT AVG(S.age) FROM Sailors S WHERE S.rating = 10;

SELECT S.sname, S.age FROM Sailors S WHERE S.age = (SELECT MAX(S2.age) FROM Sailors S2);

SELECT COUNT(*) FROM Sailors S;

SELECT COUNT(DISTINCT S.sname) FROM Sailors S;

SELECT S.sname FROM Sailors S WHERE S.age > (SELECT MAX(S2.age) FROM Sailors S2 WHERE S2.rating = 10);

SELECT S.sname FROM Sailors S WHERE S.age > ALL (SELECT S2.age FROM Sailors S2 WHERE S2.rating = 10);

----- GroupByTest.java -----

SELECT S.rating, MIN(S.age) FROM Sailors S GROUP BY S.rating;

SELECT S.rating, MIN(S.age) AS minage FROM Sailors S WHERE S.age >= 18 GROUP BY S.rating HAVING COUNT(*) > 1;

SELECT S.rating, MIN(S.age) AS minage FROM Sailors S WHERE S.age >= 18 AND S.age <= 60 GROUP BY S.rating HAVING COUNT(*) > 1;

SELECT B.bid, COUNT(*) AS reservationcount FROM Boats B, Reserves R WHERE R.bid = B.bid AND B.color = 'red' GROUP BY B.bid;

SELECT S.rating, AVG(S.age) AS avgage FROM Sailors S GROUP BY S.rating HAVING COUNT(*) > 1;

SELECT S.rating, AVG(S.age) AS avgage FROM Sailors S GROUP BY S.rating HAVING 1 < (SELECT COUNT(*) FROM Sailors S2 WHERE S.rating = S2.rating);

SELECT S.rating, AVG(S.age) AS avgage FROM Sailors S WHERE S.age >= 18 GROUP BY S.rating HAVING 1 < (SELECT COUNT(*) FROM Sailors S2 WHERE S.rating = S2.rating);

SELECT S.rating, AVG(S.age) AS avgage FROM Sailors S WHERE S.age > 18 GROUP BY S.rating HAVING 1 < (SELECT COUNT(*) FROM Sailors S2 WHERE S.rating = S2.rating AND S2.age >= 18 );

------- KnownBugsTest.java -----

SELECT sid FROM Sailors S WHERE EXISTS (SELECT 1 FROM Boats WHERE S.sid = 42);

SELECT 1 FROM Sailors S WHERE sid IN (SELECT COUNT(*) FROM Boats);

SELECT 1 FROM Sailors JOIN Reserves WHERE Reserves.bid = 4;

SELECT 1 FROM Sailors JOIN Reserves ON (Reserves.bid = 4);

SELECT 1 FROM Sailors LEFT OUTER JOIN Reserves WHERE Reserves.bid = 4;

SELECT 1 FROM Sailors LEFT OUTER JOIN Reserves ON (Reserves.bid = 4);

SELECT 1 FROM Sailors S JOIN Reserves R WHERE (R.bid = 4) OR (R.bid = 5);

SELECT 1 FROM Sailors S WHERE EXISTS (SELECT 1 FROM Reserves R GROUP BY R.sid HAVING R.sid = S.sid);

SELECT 1 FROM Sailors S JOIN Reserves R ON (S.sid = R.sid) JOIN Boats B ON (R.bid = B.bid);

SELECT 1 FROM Sailors S JOIN Reserves R ON (S.sid = R.sid) JOIN Boats B ON (S.sid = B.bid);

----- NestedTest.java -----

SELECT S.sname FROM Sailors S WHERE S.sid IN (SELECT R.sid FROM Reserves R WHERE R.bid = 103);

SELECT S.sname FROM Sailors S WHERE S.sid IN (SELECT R.sid FROM Reserves R WHERE R.bid IN (SELECT B.bid FROM Boats B WHERE B.color = 'red'));

SELECT S.sname FROM Sailors S WHERE S.sid NOT IN (SELECT R.sid FROM Reserves R WHERE R.bid IN (SELECT B.bid FROM Boats B WHERE B.color = 'red'));

SELECT S.sname FROM Sailors S WHERE EXISTS (SELECT * FROM Reserves R WHERE R.bid = 103 AND R.sid = S.sid);

SELECT S.sid FROM Sailors S WHERE S.rating > ANY (SELECT S2.rating FROM Sailors S2 WHERE S2.sname = 'Horatio');

SELECT S.sid FROM Sailors S WHERE S.rating >= ALL (SELECT S2.rating FROM Sailors S2);

SELECT S.sname FROM Sailors S, Reserves R, Boats B WHERE S.sid = R.sid AND R.bid = B.bid AND B.color = 'red' AND S.sid IN (SELECT S2.sid FROM Sailors S2, Reserves R2, Boats B2 WHERE S2.sid = R2.sid AND R2.bid = B2.bid AND B2.color = 'green');

SELECT 1 FROM Sailors WHERE EXISTS (SELECT * FROM Reserves);

SELECT 1 FROM Sailors WHERE EXISTS (SELECT sid, sname FROM Reserves);

SELECT 1 FROM Sailors S WHERE EXISTS (SELECT * FROM Reserves R WHERE S.sid = R.sid);

SELECT 1 FROM Sailors WHERE EXISTS (SELECT * FROM Reserves WHERE EXISTS (SELECT * FROM Boats));

SELECT 1 FROM Sailors WHERE EXISTS (SELECT * FROM Reserves WHERE bid in (SELECT bid FROM Boats));

SELECT 1 FROM Sailors S WHERE S.sid IN (4);

SELECT 1 FROM Sailors S WHERE S.sid IN (4, 5);

SELECT 1 FROM Sailors S WHERE S.sid IN (4) OR S.sid IN (5);

SELECT 1 FROM Sailors WHERE sid IN (SELECT 4);

SELECT 1 FROM Sailors WHERE sid IN (SELECT sid FROM Reserves WHERE sid = 4);

SELECT 1 FROM Sailors WHERE sid IN (SELECT SUM(sid) FROM Reserves WHERE sid = 4);

SELECT 1 FROM Sailors S WHERE S.sid = 4 AND EXISTS (SELECT 1 FROM Reserves R WHERE R.sid = S.sid);

SELECT 1 FROM Sailors S WHERE S.sid = 4 AND EXISTS (SELECT COUNT(*) FROM Reserves R WHERE R.sid = S.sid);

SELECT 1 FROM Sailors S WHERE EXISTS (SELECT 1 FROM Reserves R WHERE S.sid = 4 AND R.sid = S.sid);

SELECT 1 FROM Sailors S WHERE EXISTS (SELECT COUNT(*) FROM Reserves R WHERE S.sid = 4 AND R.sid = S.sid);

SELECT 1 FROM Sailors S WHERE NOT EXISTS (SELECT 1 FROM Reserves R WHERE S.sid = R.sid);

------- OuterJoinTest.java -----
SELECT 1 FROM Sailors S LEFT OUTER JOIN Reserves R WHERE S.sid = R.sid AND S.sid = 42;

SELECT 1 FROM Sailors S LEFT OUTER JOIN Reserves R WHERE S.sid = R.sid AND R.sid = 42;

SELECT 1 FROM Sailors S LEFT OUTER JOIN Reserves R ON (S.sid = R.sid) WHERE S.sid = 42;

SELECT 1 FROM Sailors S LEFT OUTER JOIN Reserves R ON (S.sid = R.sid AND R.sid = 42);

SELECT 1 FROM Sailors S RIGHT OUTER JOIN Reserves R WHERE S.sid = R.sid AND S.sid = 42;

SELECT 1 FROM Sailors S RIGHT OUTER JOIN Reserves R WHERE S.sid = R.sid AND R.sid = 42;

SELECT 1 FROM Sailors S RIGHT OUTER JOIN Reserves R ON (S.sid = R.sid AND S.sid = 42);

SELECT 1 FROM Sailors S RIGHT OUTER JOIN Reserves R ON (S.sid = R.sid) WHERE R.sid = 42;

SELECT 1 FROM Sailors S FULL OUTER JOIN Reserves R WHERE S.sid = R.sid AND S.sid = 42;

SELECT 1 FROM Sailors S FULL OUTER JOIN Reserves R WHERE S.sid = R.sid AND R.sid = 42;

SELECT 1 FROM Sailors S FULL OUTER JOIN Reserves R ON (S.sid = R.sid AND S.sid = 42);

SELECT 1 FROM Sailors S FULL OUTER JOIN Reserves R ON (S.sid = R.sid AND R.sid = 42);

------- SetOperationTest.java -----

SELECT S.sname FROM Sailors S, Reserves R, Boats B WHERE S.sid = R.sid AND R.bid = B.bid AND (B.color = 'red' OR B.color = 'green');

SELECT S.sname FROM Sailors S, Reserves R1, Boats B1, Reserves R2, Boats B2 WHERE S.sid = R1.sid AND R1.bid = B1.bid AND S.sid = R2.sid AND R2.bid = B2.bid AND B1.color = 'red' AND B2.color = 'green';

SELECT S.sname FROM Sailors S, Reserves R, Boats B WHERE S.sid = R.sid AND R.bid = B.bid AND B.color = 'red' UNION SELECT S2.sname FROM Sailors S2, Reserves R2, Boats B2 WHERE S2.sid = R2.sid AND R2.bid = B2.bid AND B2.color = 'green';

SELECT S.sname FROM Sailors S, Reserves R, Boats B WHERE S.sid = R.sid AND R.bid = B.bid AND B.color = 'red' INTERSECT SELECT S2.sname FROM Sailors S2, Reserves R2, Boats B2 WHERE S2.sid = R2.sid AND R2.bid = B2.bid AND B2.color = 'green';

SELECT S.sid FROM Sailors S, Reserves R, Boats B WHERE S.sid = R.sid AND R.bid = B.bid AND B.color = 'red' EXCEPT SELECT S2.sid FROM Sailors S2, Reserves R2, Boats B2 WHERE S2.sid = R2.sid AND R2.bid = B2.bid AND B2.color = 'green';

SELECT R.sid FROM Boats B, Reserves R WHERE R.bid = B.bid AND B.color = 'red' EXCEPT SELECT R2.sid FROM Boats B2, Reserves R2 WHERE R2.bid = B2.bid AND B2.color = 'green';

SELECT S.sid FROM Sailors S WHERE S.rating = 10 UNION SELECT R.sid FROM Reserves R WHERE R.bid = 104;

------- SimpleTest.java -----

SELECT DISTINCT S.sname, S.age FROM Sailors S;

SELECT S.sid, S.sname, S.rating, S.age FROM Sailors AS S WHERE S.rating > 7;

SELECT S.sname FROM Sailors S, Reserves R WHERE S.sid = R.sid AND R.bid = 103;

SELECT R.sid FROM Boats B, Reserves R WHERE B.bid = R.bid AND B.color = 'red';

SELECT S.sname FROM Sailors S, Reserves R, Boats B WHERE S.sid = R.sid AND R.bid = B.bid AND B.color = 'red';

SELECT B.color FROM Sailors S, Reserves R, Boats B WHERE S.sid = R.sid AND R.bid = B.bid AND S.sname = 'Lubber';

SELECT S.sname FROM Sailors S, Reserves R WHERE S.sid = R.sid;

SELECT S.sname, S.rating+1 AS rating FROM Sailors S, Reserves R1, Reserves R2 WHERE S.sid = R1.sid AND S.sid = R2.sid AND R1.day = R2.day AND R1.bid <> R2.bid;

SELECT S1.sname AS name1, S2.sname AS name2 FROM Sailors S1, Sailors S2 WHERE 2*S1.rating = S2.rating-1;

SELECT 1 FROM Boats B1, Boats B2, Boats B3 WHERE B1.bid = B2.bid AND B2.bid = B3.bid AND B3.bid = 1;

SELECT 1 FROM Sailors S LEFT JOIN Reserves R ON (S.sid = R.sid) LEFT JOIN Boats B ON (R.bid = B.bid) WHERE S.sid = 1;

------- TemporaryTableTest.java -----

SELECT sid FROM (SELECT sid FROM Sailors S) AS Temp;

SELECT sid FROM (SELECT * FROM Sailors S) AS Temp;

SELECT Temp.sid FROM (SELECT sid FROM Sailors S) AS Temp;

SELECT m_sid FROM (SELECT sid AS m_sid FROM Sailors S) AS Temp;

SELECT Temp.m_sid FROM (SELECT sid AS m_sid FROM Sailors S) AS Temp;

SELECT Temp.sid FROM (SELECT sid FROM Sailors) AS Temp WHERE Temp.sid < 42;

SELECT Temp.rating, Temp.avgage FROM (SELECT S.rating, AVG(S.age) AS avgage, COUNT(*) AS ratingcount FROM Sailors S WHERE S.age > 18 GROUP BY S.rating) AS Temp WHERE Temp.ratingcount > 1;

SELECT Temp.rating FROM (SELECT S.rating , AVG(S.age) AS avgage FROM Sailors S GROUP BY S.rating) AS Temp WHERE Temp.avgage = (SELECT MIN(Temp2.avgage) FROM (SELECT S2.rating, AVG (S2.age) AS avgage FROM Sailors S2 GROUP BY S2.rating) AS Temp2);

