<style lang="scss">
@use "../assets/colors" as colors;
@use "../assets/variables_impl" as variables;

.image-text {
  border-radius: 0 0 variables.$border-radius-small variables.$border-radius-small;
  background: rgba(0, 0, 0, 0.4);
  color: colors.$color-grauweiß;
}

.navigation-card-image {
  max-height: 300px;
}
</style>
<template>
  <div class="flex flex-column h-full w-full align-items-center">
    <div class="w-10 lg:w-7 flex flex-column mb-6 gap-3">
      <div>
        <h1>Die Informationsseite für Museumsbahnen und Eisenbahnmuseen in Österreich</h1>
        <p>
          Auf museumsbahn-events.at findet ihr eine Übersicht über alles was mit historischen Eisenbahnen in Österreich
          zu
          tun hat. Aktuelle Veranstaltungen, eine Übersicht über Eisenbahnmuseen in eurer Nähe und vieles mehr.
        </p>
      </div>
      <div class="flex align-items-center flex-column xl:flex-row gap-5">
        <div class="flex-grow-1">
          <NavigationImageCard image-alt-text="Ein Rundlokschuppen mit mehreren alten Dieselloks."
            router-link="locations" image-path="img/homepage/museums.jpg" text="Museen und Vereine">
          </NavigationImageCard>
        </div>
        <div class="flex-grow-1">
          <NavigationImageCard image-alt-text="Ein Screenshot einer Karte mit Marker Pins." router-link="locationMap"
            image-path="img/homepage/map.jpg" text="Übersichtskarte">
          </NavigationImageCard>
        </div>
        <div class="flex-grow-1">
          <NavigationImageCard image-alt-text="Eine Dampflokomotive fährt Tender voraus." router-link="events"
            image-path="img/homepage/events.jpg" text="Veranstaltungen">
          </NavigationImageCard>
        </div>
      </div>
      <div class="my-2">
        <InlineMessage severity="info"> Achtung! Die Daten auf dieser Webseite werden automatisch erfasst und nicht
          manuell geprüft.
          Abfahrtszeiten und aktuelle Informationen bitte immer auf den Webseiten der jeweiligen Veranstalter
          kontrollieren!
        </InlineMessage>
      </div>
      <h2>Veranstaltungen in den nächsten 7 Tagen</h2>
      <div class="flex flex-row flex-wrap p-2 bg-mittelgrau border-radius-small">
        <FilteredEventsSmallCardList
        :date-from="yesterday"
        :date-to="in7Days"
        card-class="w-full lg:w-6 p-1"
        >
        </FilteredEventsSmallCardList>
      </div>
      <div class="flex w-full align-items-center justify-content-center">
        <RouterLink class="p-button p-button-outlined" to="/events">Alle Veranstaltungen anzeigen</RouterLink>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { subDays, addDays } from "date-fns";
import { callOnce, useState } from "nuxt/app";
import { computed } from "vue";
import FilteredEventsSmallCardList from "~/components/FilteredEventsSmallCardList.vue";
import NavigationImageCard from "~/components/NavigationImageCard.vue";

const currentDate = useState<Date>('current-date', () => new Date())

const yesterday = computed(() => subDays(currentDate.value, 1))
const in7Days = computed(() => addDays(currentDate.value, 7))
</script>