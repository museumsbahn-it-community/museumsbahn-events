import type { ToggleButtonDesignTokens, ToggleButtonTokenSections } from '@primeuix/themes/types/togglebutton';

export const root: ToggleButtonTokenSections.Root = {
    padding: '0.625rem 1rem',
    borderRadius: '{content.border.radius}',
    gap: '0.5rem',
    fontWeight: '500',
    disabledBackground: '{form.field.disabled.background}',
    disabledBorderColor: '{form.field.disabled.background}',
    disabledColor: '{form.field.disabled.color}',
    invalidBorderColor: '{form.field.invalid.border.color}',
    focusRing: {
        width: '{focus.ring.width}',
        style: '{focus.ring.style}',
        color: '{focus.ring.color}',
        offset: '{focus.ring.offset}',
        shadow: '{focus.ring.shadow}'
    },
    transitionDuration: '{form.field.transition.duration}',
    sm: {
        fontSize: '{form.field.sm.font.size}',
        padding: '0.5rem 0.75rem'
    },
    lg: {
        fontSize: '{form.field.lg.font.size}',
        padding: '0.75rem 1.25rem'
    }
};

export const icon: ToggleButtonTokenSections.Icon = {
    color: '{text.muted.color}',
    hoverColor: '{text.muted.color}',
    checkedColor: '{highlight.color}',
    disabledColor: '{form.field.disabled.color}'
};

export const content: ToggleButtonTokenSections.Content = {
    checkedBackground: 'transparent',
    checkedShadow: 'none',
    padding: '0',
    borderRadius: '0',
    sm: {
        padding: '0'
    },
    lg: {
        padding: '0'
    }
};

export const colorScheme: ToggleButtonTokenSections.ColorScheme = {
    light: {
        root: {
            background: '{surface.300}',
            checkedBackground: '{surface.700}',
            hoverBackground: '{surface.200}',
            borderColor: '{surface.300}',
            color: '{primary.color}',
            hoverColor: '{surface.700}',
            checkedColor: '{surface.200}',
            checkedBorderColor: '{surface.700}'
        },
        content: {
            checkedBackground: '{surface.700}'
        },
        icon: {
            color: '{surface.500}',
            hoverColor: '{surface.700}',
            checkedColor: '{surface.900}'
        }
    },
    dark: {
        root: {
            background: '{surface.950}',
            checkedBackground: '{surface.950}',
            hoverBackground: '{surface.950}',
            borderColor: '{surface.950}',
            color: '{surface.400}',
            hoverColor: '{surface.300}',
            checkedColor: '{surface.0}',
            checkedBorderColor: '{surface.950}'
        },
        content: {
            checkedBackground: '{surface.800}'
        },
        icon: {
            color: '{surface.400}',
            hoverColor: '{surface.300}',
            checkedColor: '{surface.0}'
        }
    }
};

export default {
    root,
    icon,
    content,
    colorScheme
} satisfies ToggleButtonDesignTokens;