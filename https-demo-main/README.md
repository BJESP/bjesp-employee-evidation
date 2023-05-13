# SPRING BOOT APP

Serverska aplikacija razvijena pomoću Spring Boot radnog okvira.

Sva podešavanja vezana za https se nalaze u application.properties datoteci. Da bi se aplikacija uspešno pokrenula koristeći HTTPS protokol, potrebno je instalirati ili server.crt ili njegov CA (myCA.pfx) u Trusted Root Certification Authorities na nivou operativnog sistema.

## Preduslovi
- java sdk 15

## Pokretanje aplikacije

- otvoriti aplikaciju kao mvn projekat u omiljenom razvojnom okruženju
- desni klik na DemoApplication klasu / Run DemoApplication

Aplikacija se pokreće na https://localhost:8443

