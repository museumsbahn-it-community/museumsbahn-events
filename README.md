# museumsbahn-events

This is the code behind the page museumsbahn-events.at which will be launched in the first half of 2024.

## Goal

The goal of the museumsbahn-events plattform is to crawl events from all associations across Austria
which are involved in preserving and running heritage trains.

The main idea is that this project does not cause any additional work for the associations, but
gives a single point of entry for people who are planning their next trips and want to know
which events are happening.

## Architecture

As a central hub we use boudicca.events and the eventdb provided by boudicca.
We implemented some custom eventcollectors, which are fully compatible with boudicca.

Additionally we use a separate backend to provide additional information like museum 
locations, opening hours, contact details and so on.

## Current Limitations

The api package needs to be published to a private repo or linked using `npm link`.

## Development

### Publish artifacts to private repos

#### npm

add to your ~/.npmrc
```
@boudicca:registry=https://<your registry address>/<your registry path>
//<your registry address>/<your registry path>/:_authToken=<your auth token>
```

in the generated typescript client follow the README.md, but generally you can run
```
npm install
npm run build
npm publish
```

for dry run you can use
```
npm publish --dry-run
```

## GDPR conforming matomo configuration

https://matomo.org/faq/how-to/how-do-i-configure-matomo-without-tracking-consent-for-french-visitors-cnil-exemption/

https://matomo.org/wp-content/uploads/2021/10/Matomo-Analytics-Exemption-from-Tracking-Consent-in-France-CNIL.pdf

https://matomo.org/faq/general/configure-privacy-settings-in-matomo/

https://matomo.org/faq/how-to/how-do-i-enforce-tracking-without-cookies/

How do I go cookieless (disable all cookies) for a visitor?

https://matomo.org/faq/general/faq_157/

### Checklist

- have user opt out
- Automatically Anonymise Visitor IPs and replace User IDs
- use anonymized IP for geolocation
- have no user id / no login
- regularly delete old raw visitor data - 90 days
- delete old aggregated report data - 6 months
