import {definePreset} from '@primeuix/themes';
import Lara from '@primeuix/themes/lara';
import base from "./theme/base.ts";
import button from "./theme/button.ts";
import message from "./theme/message.ts";
import {colors} from "./theme/colors.ts";
import togglebutton from "assets/theme/togglebutton";
import inputgroup from "assets/theme/inputgroup";
import badge from "assets/theme/badge";
import chip from "assets/theme/chip";

const MuseumRailwayEventsTheme = definePreset(Lara, {
    ...base,
    variables: {
        fontFamily: '-apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif, "Apple Color Emoji", "Segoe UI Emoji", "Segoe UI Symbol"',

        // TODO: remove these as they are meaningless
        // Surfaces
        'text-color': colors.colorUmbragrau,
        'text-color-secondary': colors.colorUmbragrau,
        'primary-color': colors.colorGrauweiss,
        'primary-color-text': colors.colorGrauweiss,

        // Application specific
        'surface-ground': colors.colorGrauweiss,
        'surface-section': colors.colorGrauweiss,
        'surface-card': colors.colorGrauweiss,
        'surface-overlay': colors.colorGrauweiss,
        'surface-border': colors.colorAchatgrau,
        'surface-hover': colors.colorAchatgrau,
    },

    // Component specific styles
    components: {
        badge,
        button,
        chip,
        message,
        togglebutton,
        inputgroup,
        panel: {
            root: {
                background: colors.colorUltramarinblau,
            },
            header: {
                padding: '0.5rem 1rem',
                borderRadius: '4px'
            },
            content: {
                padding: '1rem',
            }
        },
        scrollpanel: {
            content: {
                padding: '1rem 1.5rem 1rem 1rem'
            },
            wrapper: {
                paddingRight: '1px',
                borderRight: '8px solid var(--surface-ground)',
                borderRadius: '5px'
            },
            barY: {
                background: colors.colorUltramarinblau,
                width: '8px'
            },
            barYHover: {
                background: colors.colorUltramarinblau,
                width: '8px'
            }
        },
        card: {
            root: {
                borderRadius: '4px',
            },
            border: `5px ${colors.colorUmbragrau} solid`,
            padding: 'calc(0.75rem - 5px)',
            link: {
                color: colors.colorUmbragrau
            }
        },
        menubar: {
            root: {
                background: colors.colorUmbragrau,
                color: colors.textColorLight,
                gap: "0"
            },
            submenu: {
                background: colors.colorUmbragrau,
                borderRadius: "0",
                borderColor: "transparent"
            },
            item: {
                color: colors.textColorLight,
                focusColor: colors.textColorLight,
                focusBackground: colors.colorUltramarinblau,
                borderRadius: "0",

            },
            menuItem: {
                hoverBackground: colors.colorUltramarinblau,
                hoverColor: colors.textColorLight,
            },
            baseItem: {
                borderRadius: "0",
            },
            mobileButton: {
                color: colors.textColorLight,
                hoverColor: colors.textColorLight,
                hoverBackground: colors.colorUltramarinblau,
            },
            colorScheme: {
                light: {
                    root: {
                        background: colors.colorUmbragrau,
                        color: colors.textColorLight
                    }
                }
            }
        },
        formField: {
            paddingX: '0.75rem',
            paddingY: '0.5rem',
            sm: {
                fontSize: '0.875rem',
                paddingX: '0.5rem',
                paddingY: '0.25rem'
            },
            lg: {
                fontSize: '1.25rem',
                paddingX: '1rem',
                paddingY: '0.75rem'
            },
            borderRadius: '4px',
            focusRing: {
                width: '2px',
                style: 'solid',
                color: colors.colorUmbragrau,
                offset: '2px',
                shadow: 'none'
            },
            transitionDuration: '0.2s'
        },
    }
});

export default {
    preset: MuseumRailwayEventsTheme,
    options: {
        darkModeSelector: '.p-dark'
    }
};
