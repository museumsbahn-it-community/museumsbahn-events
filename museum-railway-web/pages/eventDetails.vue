<style lang="scss">
@use "../assets/variables_impl.scss" as variables;

.content-left-column,
.content-right-column {
    flex: auto 2 1;
    padding: 0;
    overflow: hidden;
    height: variables.$min-content-height;
}

.event-details {
    top: calc(variables.$navbar-height);
}
</style>

<template>
    <div class="event-details w-full grid m-0 p-2 md:p-0">
        <div class="xl:col-4 flex flex-1 content-left-column" v-if="viewport.isGreaterOrEquals('desktop-xl')"></div>
        <div class="col-12 md:col-8 xl:col-6 content-center-column m-0 md:m-2 mb-5 p-0">
            <EventDetails :event="selectedEvent" :no-event-selected-placeholder-text="noEventSelectedPlaceholderText">
                Event Details
            </EventDetails>
            <Sidebar class="mt-2 w-full" v-if="viewport.isLessThan('tablet')"
                    :title="`Weitere Veranstaltungen von ${locationName}`" side="center">
                    <div class="flex flex-column gap-2">
                        <EventCardShort v-for="eventEntry in eventsForSameLocation" :event="eventEntry">
                        </EventCardShort>
                    </div>
                </Sidebar>
        </div>
        <div class="col-2 flex flex-1 content-right-column" v-if="viewport.isGreaterOrEquals('tablet')">
            <div class="w-full flex flex-column justify-content-center align-items-end overflow-hidden">
                <Sidebar class="w-full lg:w-11" style="height: 80%;"
                    :title="`Weitere Veranstaltungen von ${locationName}`" side="right">
                    <div class="flex flex-column gap-2">
                        <EventCardShort v-for="eventEntry in eventsForSameLocation" :event="eventEntry">
                        </EventCardShort>
                    </div>
                </Sidebar>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import EventCardShort from '~/components/EventCardSmall.vue';
import EventDetails from '~/components/EventDetails.vue';
import { EMPTY_EVENT_FILTERS, useEventListData } from '~/composables/eventListData';
import { useGlobalConfigStore } from '~/stores/GlobalConfigStore';


const router = useRouter();
const route = useRoute();
const viewport = useViewport();

const eventKeyParam = route?.params?.eventKey;
const eventListData = useEventListData();
const selectedEvent = computed(() => eventListData.getEventByKey(eventKeyParam));
const globalConfig = useGlobalConfigStore();
const locationName = computed(() => selectedEvent.value?.location?.name);

const eventsForSameLocation = computed(() => {
    const locationId = selectedEvent.value?.locationId
    const events = eventListData.filteredEvents.value.filter((event) => event.locationId === locationId)
    return events;
})

onMounted(loadData);

async function loadData(): Promise<void> {
    await eventListData.loadEvents(EMPTY_EVENT_FILTERS);
}

const noEventSelectedPlaceholderText = "Leider konnte die Veranstaltung nicht gefunden werden."
</script>