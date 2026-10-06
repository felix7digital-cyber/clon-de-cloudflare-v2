# Clon de Cloudflare (Android)

Cliente Android nativo (Kotlin + Jetpack Compose) para la API de Cloudflare.

## Estado

- Login real con validacion contra Cloudflare
- Token guardado cifrado (EncryptedSharedPreferences)
- Pages: lista, crea, elimina proyectos
- Bindings: agrega D1, R2, KV a un proyecto Pages
- Tema oscuro/claro Material 3
- Bottom bar, FAB, recientes/fijadas

## Permisos del token

Crea un token en dash.cloudflare.com/profile/api-tokens con:

- User / User Details / Read
- Account / Account Settings / Read
- Account / Cloudflare Pages / Edit
- Account / D1 / Edit
- Account / Workers R2 Storage / Edit
- Account / Workers KV Storage / Edit

## Build

Se compila automaticamente con GitHub Actions en cada push a main.
