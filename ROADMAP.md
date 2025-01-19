# Roadmap

### Frontend

- [x] implement frontend
  - [x] homepage
  - [x] map
  - [x] location list
  - [x] events
  - [x] event details
- [x] image caching
- [ ] restyling museum details
  - [ ] introduce proper categories
  - [ ] images
  - [ ] descriptions
  - [ ] opening hours
  - [ ] navigate to.. via google maps
- [ ] restyling and improvement of map
- [ ] display museum railways on the map
- [ ] filters for event list
- [ ] filters for museum list
- [ ] export options for event list

### SEO

- [x] basic analytics setup
- [x] basic SEO attributs
- [ ] SEO attributes per page
- [ ] proper 404 page

### Event Collectors & Data Quality

#### Eventcollectors

already (partially) implemented

- Atterseebahn Collector
- Atterseeschifffahrt Collector
- EBFL Collector
- Eisenbahnmuseum Schwechat Collector
- Eisenbahnmuseum Strasshof Collector
- Erzbergbahn Collector
- Höllentalbahn Collector
- MLV Zwettl Collector
- Mh6 Collector
- Nostalgiebahnen in Kärnten
- ProBahn Vorarlberg Collector
- Reblausexpress Collector
- Regiobahn Collector
- Rheinbähnle Collector
- Steirische Eisenbahnfreunde Collector
- Tramwaymuseum Graz Collector
- Traunseetram Collector
- Wackelsteinexpress Collector
- Waldviertelbahn Collector
- Wälderbähnle/Bregenzerwaldbahn Collector
- Ybbstalbahn Collector
- ÖGEG Normalspur Termine
- ÖGEG Schmalspur Termine

still need to be implemented

- SLB Nostalgie
- Feldbahnmuseum Freiland
- Breitenauerbahn (difficult)
- Kaltenleutgebener Bahn
- NOEVOG (support webshop additionally)

the following websites lack event information (in computer readable format)

- Tiroler Museumsbahnen
- Achenseebahn
- Pinzgaubahn
- Zillertalbahn
- Taurachbahn
- Club 1018

#### Eventcollector Improvements

- [ ] google forms + sheets collector
- [ ] collect regular events (for some collectors not yet working)

### Data Quality (eventcollectors + backend)

- [ ] timetable support (stations, departure location, departure time, arrival location, arrival time, OneWay/RoundTrip)
- [ ] backup images (default if event page does not provide anything)
- [ ] museum descriptions
- [ ] museum opening hours (maybe from OSM?)
- [ ] museum images
- [ ] geojson for museum railways

### Publishing

- [ ] ical link
- [ ] Email Publisher
- [ ] Mastodon Publisher
- [ ] BlueSky Publisher
- [ ] Instagram Publisher (might be difficult)
- [ ] ...
