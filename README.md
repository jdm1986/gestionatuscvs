# Gestión Curriculums - Despliegue con Docker

## Requisitos previos

- Docker y Docker Compose instalados
- Cuenta de SendGrid (recomendado autenticar el dominio)

## Variables de entorno (email)

La app puede enviar emails de dos formas:
- API HTTP de SendGrid: define `SENDGRID_API_KEY` y se usará la API.
- SMTP de SendGrid (fallback): usuario `apikey` y contraseña tu `SENDGRID_API_KEY`.

Variables recomendadas:

```
SENDGRID_API_KEY=SG.xxxxxx      # no la subas al repo
APP_MAIL_FROM=info@gestionatuscv.es
APP_MAIL_ADMIN=info@gestionatuscv.es
```

No es necesario usar Gmail ni contraseñas de aplicación.

## Levantar la aplicación con Docker Compose

Desde la raíz del proyecto, ejecuta:

```
docker-compose up --build
```

## Comprobación

- Verifica contenedores con `docker ps`
- Accede a http://localhost:8080
- Registra un usuario y revisa logs; al iniciar debe aparecer `[EmailService] mode=SendGrid` si la API key está presente

Si tienes dudas o problemas, avísame y te ayudo.
