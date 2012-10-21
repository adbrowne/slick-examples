select x2."NAME", x2."PRICE" from "TrainTrips" x2 where x2."FK_COUNTRY_ID" = 1

select min(x2."NAME"), count(1) from "TrainTrips" x3, "Countries" x2 where x2."COUNTRY_ID" = x3."FK_COUNTRY_ID" group by x2."COUNTRY_ID"
