import http.server
import ssl
import os

# Cambiar el directorio a 'frontend'
os.chdir('frontend')

# Configuración del servidor
server_address = ('', 8000)
httpd = http.server.HTTPServer(server_address, http.server.SimpleHTTPRequestHandler)

# Crear un contexto SSL
context = ssl.SSLContext(ssl.PROTOCOL_TLS_SERVER)
context.load_cert_chain(certfile="C:/cursos/java/IdeaProyect/untitled/gestionatuscvs/certificados/fullchain.pem",
                        keyfile="C:/cursos/java/IdeaProyect/untitled/gestionatuscvs/certificados/privkey.pem")

# Envolver el socket del servidor en SSL
httpd.socket = context.wrap_socket(httpd.socket, server_side=True)

print("Serving on https://localhost:8000")
httpd.serve_forever()
