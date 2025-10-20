import {definePreset} from '@primeuix/themes';
import Lara from '@primeuix/themes/lara';
import base from "./theme/base.ts";
import button from "./theme/button.ts";
import message from "./theme/message.ts";
import {colors} from "./theme/colors.ts";
import togglebutton from "assets/theme/togglebutton";
import inputgroup from "assets/theme/inputgroup";
import badge from "assets/theme/badge";

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
    },
    colorScheme: {
        light: {
            surface: {
                0: '#ffffff',
                50: '{slate.50}',
                100: '{slate.100}',
                200: '{slate.200}',
                300: '{slate.300}',
                400: '{slate.400}',
                500: '{slate.500}',
                600: '{slate.600}',
                700: '{slate.700}',
                800: '{slate.800}',
                900: '{slate.900}',
                950: '{slate.950}'
            },
            primary: {
                color: '{primary.500}',
                contrastColor: '#ffffff',
                hoverColor: '{primary.600}',
                activeColor: '{primary.700}'
            },
            highlight: {
                background: '{primary.50}',
                focusBackground: '{primary.100}',
                color: '{primary.700}',
                focusColor: '{primary.800}'
            },
            focusRing: {
                shadow: '0 0 0 0.2rem {primary.200}'
            },
            mask: {
                background: 'rgba(0,0,0,0.4)',
                color: '{surface.200}'
            },
            formField: {
                background: '{surface.0}',
                disabledBackground: '{surface.200}',
                filledBackground: '{surface.50}',
                filledHoverBackground: '{surface.50}',
                filledFocusBackground: '{surface.0}',
                borderColor: '{surface.300}',
                hoverBorderColor: '{primary.color}',
                focusBorderColor: '{primary.color}',
                invalidBorderColor: '{red.400}',
                color: '{surface.700}',
                disabledColor: '{surface.500}',
                placeholderColor: '{surface.500}',
                invalidPlaceholderColor: '{red.600}',
                floatLabelColor: '{surface.500}',
                floatLabelFocusColor: '{primary.600}',
                floatLabelActiveColor: '{surface.500}',
                floatLabelInvalidColor: '{form.field.invalid.placeholder.color}',
                iconColor: '{surface.500}',
                shadow: 'none'
            },
            text: {
                color: '{surface.700}',
                hoverColor: '{surface.800}',
                mutedColor: '{surface.500}',
                hoverMutedColor: '{surface.600}'
            },
            content: {
                background: '{surface.0}',
                hoverBackground: '{surface.100}',
                borderColor: '{surface.200}',
                color: '{text.color}',
                hoverColor: '{text.hover.color}'
            },
            overlay: {
                select: {
                    background: '{surface.0}',
                    borderColor: '{surface.200}',
                    color: '{text.color}'
                },
                popover: {
                    background: '{surface.0}',
                    borderColor: '{surface.200}',
                    color: '{text.color}'
                },
                modal: {
                    background: '{surface.0}',
                    borderColor: '{surface.200}',
                    color: '{text.color}'
                }
            },
            list: {
                option: {
                    focusBackground: '{surface.100}',
                    selectedBackground: '{highlight.background}',
                    selectedFocusBackground: '{highlight.focus.background}',
                    color: '{text.color}',
                    focusColor: '{text.hover.color}',
                    selectedColor: '{highlight.color}',
                    selectedFocusColor: '{highlight.focus.color}',
                    icon: {
                        color: '{surface.400}',
                        focusColor: '{surface.500}'
                    }
                },
                optionGroup: {
                    background: 'transparent',
                    color: '{text.color}'
                }
            },
            navigation: {
                item: {
                    focusBackground: '{surface.100}',
                    activeBackground: '{surface.100}',
                    color: '{text.color}',
                    focusColor: '{text.hover.color}',
                    activeColor: '{text.hover.color}',
                    icon: {
                        color: '{surface.400}',
                        focusColor: '{surface.500}',
                        activeColor: '{surface.500}'
                    }
                },
                submenuLabel: {
                    background: 'transparent',
                    color: '{text.color}'
                },
                submenuIcon: {
                    color: '{surface.400}',
                    focusColor: '{surface.500}',
                    activeColor: '{surface.500}'
                }
            }
        }
    }
});

export default {
    preset: MuseumRailwayEventsTheme,
    options: {
        darkModeSelector: '.p-dark'
    }
};
