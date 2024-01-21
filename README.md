# historic-railway-events




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
