# museum-railway-backend


https://developers.google.com/sheets/api/quickstart/java


- create project
- enable sheets api
- create client


From the API Manager, just create select "Create credentials" > "Service Account key" and generate a new key for the Service that is associated to your Google Play account.

credentials file should look like
```json
{
  "type": "service_account",
  "project_id": "",
  "private_key_id": "",
  "private_key": "",
  "client_email": "",
  "client_id": "",
  "auth_uri": "https://accounts.google.com/o/oauth2/auth",
  "token_uri": "https://accounts.google.com/o/oauth2/token",
  "auth_provider_x509_cert_url": "https://www.googleapis.com/oauth2/v1/certs",
  "client_x509_cert_url": ""
}

```