package de.carstenlex;// Copyright 2018 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

// [START calendar_quickstart]

import com.google.api.services.calendar.model.CalendarListEntry;
import lombok.extern.java.Log;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

@Log
public class BasketballCalendarSync {


    public static void main(String... args) throws IOException, GeneralSecurityException {
        log.info("Starte Basketballkalender Transfer von Basketplan nach Google-Calendar");

        log.info("initialisiere Kalender");
        GoogleCalendar calendar = new GoogleCalendar();
        // Build a new authorized API client service.

        log.info("Finde Basketballspiele - Kalender");
        CalendarListEntry calendarBasketball = calendar.findCalendarBasketball();

        log.info("Basketballspiele - Kalender clearen...");
        calendar.clear(calendarBasketball);

        log.info("Spiele laden und Termine eintragen...");
        Spielplan spielplan = new Spielplan();
        for (Mannschaft mannschaft : Mannschaft.values()) {
            System.out.println("===========================");
            System.out.println("Mannschaft: "+mannschaft);
            System.out.println("===========================");
            List<Spiel> spiele = spielplan.loadFromBasketplan(mannschaft);
            spiele.forEach(spiel -> log.info(spiel.toString()));
            calendar.erstelleTermineInKalender( calendarBasketball, spiele);
        }

        log.info("Terminübertragung fertig!");
    }


}

