require('@rushstack/eslint-patch/modern-module-resolution');

module.exports = {
    root: true,
    env: {
        es6: true,
    },
    plugins: ["@cloudflight/typescript"],
    extends: ['plugin:@cloudflight/vue/recommended'],
    parserOptions: {
        project: ["tsconfig.json"],
        parser: '@typescript-eslint/parser',
        sourceType: "module",
    },
    settings: {
        "import/resolver": {
            typescript: {
                alwaysTryTypes: true,
                project: ["tsconfig.json"],
            },
        },
    },
    overrides: [
        {
            files: ["*.ts", "*.tsx", "*.js", "*.jsx"],
            excludedFiles: ["test-setup.ts", ],
            rules: {
                "no-tabs": ["error", { allowIndentationTabs: true }]
            }
        },
    ],
};