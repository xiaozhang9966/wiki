module.exports = {
  root: true,
  env: {
    node: true
  },
  'extends': [
    'plugin:vue/vue3-essential',
    'eslint:recommended',
    '@vue/typescript/recommended'
  ],
  parserOptions: {
    ecmaVersion: 2020
  },
  rules: {
    'no-console': process.env.NODE_ENV === 'production' ? 'warn' : 'off',
    'no-debugger': process.env.NODE_ENV === 'production' ? 'warn' : 'off',
    'vue-unused-components' : 'off',
    '@typescript-eslint-explicit-any' :0,
    'vue-unused-vars':0,
    "@typescript-eslint/explicit-module-boundary-types": "off",
    'vue/no-unused-vars':0

}
}
