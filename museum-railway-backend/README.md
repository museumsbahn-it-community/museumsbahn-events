# museum-railway-backend

## Google Cloud Access

### Museum Database

https://developers.google.com/sheets/api/quickstart/java

In Google Cloud Console:
- create a project
- enable GoogleSheets API
- create Service Account

1. Create a Service Account but DO NOT give it any IAM roles in the Google Cloud Console.
2. Get the Service Account's email. Share the specific Google Sheet with that email address (Read-only).
3. From the API Manager, just create select "Create credentials" > "Service Account key" and generate a new key for the Service that is associated to your Google Play account.

This limits the service account to ONLY the document explicitly shared with it.

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

### Images

Images are simply stored in Google Drive and shared publicly. Then the image id is enough to access it.

For added protection the image id's themselves are never shared with the frontend, but the backend controller internally applies
a mapping.