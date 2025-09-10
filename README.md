# Gestión Curriculums - Despliegue con Docker

## Requisitos previos

- Tener Docker y Docker Compose instalados en tu máquina
- Tener la contraseña SMTP para la cuenta de email configurada (por ejemplo, contraseña de aplicación para Gmail)

## Variables de entorno

La variable `SPRING_MAIL_PASSWORD` debe estar configurada en tu entorno para que Spring Boot pueda enviar emails.

### En Linux/macOS

```bash
export SPRING_MAIL_PASSWORD="tu_contraseña_smtp"
```

### En Windows PowerShell

```powershell
$env:SPRING_MAIL_PASSWORD="tu_contraseña_smtp"
```

## Levantar la aplicación con Docker Compose

Desde la raíz del proyecto, ejecuta:

```bash
docker-compose up --build
```

Esto reconstruirá la imagen y levantará el contenedor con las variables de entorno configuradas.

## Comprobación

- Verifica que los contenedores estén corriendo con `docker ps`
- Accede a la aplicación en http://localhost:8080
- Prueba a registrar un usuario y verifica que no se produzca error 400 por fallo en el envío de email

---

Así mantienes segura la contraseña de correo, evitas que el registro falle si el correo no se puede enviar, y puedes desplegar fácilmente con Docker.

Si tienes dudas o problemas, avísame y te ayudo.

